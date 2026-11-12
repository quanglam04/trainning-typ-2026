const orderService = require('../services/order.service');
const prisma = require('../config/prisma');
const { redisClient, clearCachePattern } = require('../config/redis');
const { STATUS_UPDATE_TARGETS } = require('../services/order-status.service');

const createOrder = async (req, res, next) => {
    try {
        const { items, table_id } = req.body;
        if (!items || !Array.isArray(items) || items.length === 0) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: 'The order must exist at least 1 item'
            });
        }

        for (const item of items) {
            const qty = Number(item.quantity);
            if (!item.product_id || !Number.isInteger(qty) || qty <= 0) {
                return res.status(400).json({
                    error: true,
                    code: 400,
                    message: 'Validation Error',
                    details: 'Each order item must have a valid product_id and an integer quantity > 0'
                });
            }
            item.quantity = qty;
        }

        const userID = req.user.id;
        const order = await orderService.createOrder(req.body, userID);
        return res.status(201).json({
            error: false,
            code: 201,
            message: 'Create order success',
            data: order
        });
    } catch (err) {
        next(err);
    }
};

const updateStatusOrder = async (req, res, next) => {
    try {
        const targetOrderId = req.body.orderID || req.body.order_id || req.body.id;
        const targetStatus = req.body.new_status || req.body.status;
        const { note } = req.body;

        if (!targetOrderId) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: 'orderID (or order_id) is required'
            });
        }
        if (!STATUS_UPDATE_TARGETS.includes(targetStatus)) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: `Invalid order status. Allowed: ${STATUS_UPDATE_TARGETS.join(', ')}`
            });
        }

        const userID = req.user.id;
        const updatedOrder = await orderService.updateOrder({
            orderID: targetOrderId,
            new_status: targetStatus,
            note
        }, userID);

        return res.status(200).json({
            error: false,
            code: 200,
            message: 'Update order status successfully',
            data: updatedOrder
        });
    } catch (err) {
        next(err);
    }
};

const getOrders = async (req, res, next) => {
    try {
        const page = Math.max(1, parseInt(req.query.page, 10) || 1);
        const limit = Math.min(100, Math.max(1, parseInt(req.query.limit, 10) || 10));
        const { status, search, table_number } = req.query;

        const skip = (page - 1) * limit;
        const where = {};

        if (status && status !== 'all') {
            where.status = status;
        }

        if (table_number) {
            where.table = {
                table_number: table_number
            };
        }

        if (search && search.trim()) {
            const keyword = search.trim();
            where.OR = [
                { id: { contains: keyword, mode: 'insensitive' } },
                { table: { table_number: { contains: keyword, mode: 'insensitive' } } },
                { creator: { full_name: { contains: keyword, mode: 'insensitive' } } },
                { note: { contains: keyword, mode: 'insensitive' } }
            ];
        }

        // 1. Kiểm tra cache thống kê đơn hàng (orders:stats)
        let stats = null;
        if (redisClient.isOpen) {
            try {
                const cachedStats = await redisClient.get('orders:stats');
                if (cachedStats) {
                    stats = JSON.parse(cachedStats);
                }
            } catch (e) {
                console.error('[Redis Error] Đọc cache stats:', e.message);
            }
        }

        // 2. Query dữ liệu phân trang & thống kê nếu miss cache
        const queries = [
            prisma.order.findMany({
                where,
                skip,
                take: limit,
                orderBy: { created_at: 'desc' },
                include: {
                    table: true,
                    customer: true,
                    items: true,
                    payment: true,
                    creator: { select: { id: true, full_name: true, role: true } }
                }
            }),
            prisma.order.count({ where })
        ];

        if (!stats) {
            queries.push(
                prisma.order.findMany({
                    select: {
                        status: true,
                        total_amount: true,
                        payment: { select: { status: true, amount: true } }
                    }
                })
            );
        }

        const queryResults = await Promise.all(queries);
        const orders = queryResults[0];
        const total = queryResults[1];

        if (!stats && queryResults[2]) {
            const allOrdersSummary = queryResults[2];
            stats = {
                totalOrders: allOrdersSummary.length,
                activeCount: allOrdersSummary.filter(o => ['pending', 'confirmed', 'cooking', 'ready', 'served'].includes(o.status)).length,
                completedCount: allOrdersSummary.filter(o => o.status === 'completed').length,
                totalRevenue: allOrdersSummary.reduce(
                    (sum, o) => sum + (o.payment?.status === 'paid' ? o.payment.amount : 0),
                    0
                )
            };

            // Lưu cache stats trong 20s
            if (redisClient.isOpen) {
                try {
                    await redisClient.setEx('orders:stats', 20, JSON.stringify(stats));
                } catch (e) {
                    console.error('[Redis Error] Ghi cache stats:', e.message);
                }
            }
        }

        return res.status(200).json({
            error: false,
            code: 200,
            message: 'Orders retrieved successfully',
            data: orders,
            meta: {
                total,
                page,
                limit,
                totalPages: Math.ceil(total / limit) || 1
            },
            stats
        });
    } catch (err) {
        next(err);
    }
};

const getTables = async (req, res, next) => {
    try {
        const cacheKey = 'tables:list';

        // 1. Kiểm tra cache Redis
        if (redisClient.isOpen) {
            try {
                const cached = await redisClient.get(cacheKey);
                if (cached) {
                    return res.status(200).json({
                        error: false,
                        code: 200,
                        message: 'Tables retrieved successfully (from cache)',
                        data: JSON.parse(cached),
                        _source: 'cache'
                    });
                }
            } catch (e) {
                console.error('[Redis Error] Đọc cache tables:', e.message);
            }
        }

        // 2. Cache Miss: Truy vấn Database
        const tables = await prisma.restaurantTable.findMany({
            orderBy: { table_number: 'asc' }
        });

        // 3. Lưu vào Redis (TTL: 30s)
        if (redisClient.isOpen) {
            try {
                await redisClient.setEx(cacheKey, 30, JSON.stringify(tables));
            } catch (e) {
                console.error('[Redis Error] Ghi cache tables:', e.message);
            }
        }

        return res.status(200).json({
            error: false,
            code: 200,
            message: 'Tables retrieved successfully',
            data: tables,
            _source: 'database'
        });
    } catch (err) {
        next(err);
    }
};

const updateTableStatus = async (req, res, next) => {
    try {
        const { id } = req.params;
        const { status } = req.body;
        const VALID_STATUSES = ['available', 'occupied', 'reserved'];
        if (!status || !VALID_STATUSES.includes(status)) {
            return res.status(400).json({
                error: true,
                code: 400,
                message: 'Validation Error',
                details: 'Invalid table status. Allowed: available, occupied, reserved'
            });
        }

        // Chặn chuyển bàn về available nếu bàn đang có đơn hàng hoạt động
        if (status === 'available') {
            const activeOrder = await prisma.order.findFirst({
                where: {
                    table_id: id,
                    status: { in: ['pending', 'confirmed', 'cooking', 'ready', 'served'] }
                }
            });
            if (activeOrder) {
                return res.status(409).json({
                    error: true,
                    code: 409,
                    message: 'Conflict Error',
                    details: `Cannot release table: active order #${activeOrder.id} is still in progress (${activeOrder.status})`
                });
            }
        }

        const updatedTable = await prisma.restaurantTable.update({
            where: { id },
            data: { status }
        });

        // Invalidate cache sơ đồ bàn
        await clearCachePattern('tables:*');
        console.log(`[Redis Invalidate] Đã xóa cache tables:* khi bàn đổi trạng thái sang: ${status}`);

        return res.status(200).json({
            error: false,
            code: 200,
            message: `Table status updated to ${status}`,
            data: updatedTable
        });
    } catch (err) {
        next(err);
    }
};

module.exports = {
    createOrder,
    updateStatusOrder,
    getOrders,
    getTables,
    updateTableStatus
};
