const prisma = require('../config/prisma');
const { redisClient, clearCachePattern } = require('../config/redis');
const { assertOrderTransitionAllowed } = require('./order-status.service');

const DEFAULT_TTL = 60;

const createOrder = async (orderData, userID) => {
    const { customer_id, customer_email, customer_name, customer_phone, table_id, items, note } = orderData;
    const createdOrder = await prisma.$transaction(async (tx) => {
        let table = null;
        if (table_id) {
            table = await tx.restaurantTable.findUnique({
                where: { id: table_id }
            });
            if (!table) {
                const error = new Error(`Table with ID ${table_id} does not exist`);
                error.statusCode = 404;
                throw error;
            }
            if (table.status !== 'available') {
                const error = new Error('Table is not available');
                error.statusCode = 409;
                error.details = `Current table status: ${table.status}`;
                throw error;
            }

            // Chặn tạo 2 đơn trên cùng 1 bàn nếu có đơn đang phục vụ
            const activeOrderOnTable = await tx.order.findFirst({
                where: {
                    table_id: table_id,
                    status: { in: ['pending', 'confirmed', 'cooking', 'ready', 'served'] }
                }
            });
            if (activeOrderOnTable) {
                const error = new Error('Table already has an active order');
                error.statusCode = 409;
                error.details = `Table ${table.table_number} is currently serving active order #${activeOrderOnTable.id} (${activeOrderOnTable.status})`;
                throw error;
            }
        }

        // Tự động tìm hoặc tạo Customer nếu có email / phone khách
        let finalCustomerId = customer_id || null;
        const validEmail = customer_email && typeof customer_email === 'string' && customer_email.trim();
        const validPhone = customer_phone && typeof customer_phone === 'string' && customer_phone.trim();
        if (!finalCustomerId && (validEmail || validPhone)) {
            const normalizedEmail = validEmail ? validEmail.toLowerCase() : null;
            let existingCustomer = await tx.customer.findFirst({
                where: {
                    OR: [
                        ...(normalizedEmail ? [{ email: normalizedEmail }] : []),
                        ...(validPhone ? [{ phone: validPhone }] : [])
                    ]
                }
            });
            if (!existingCustomer && normalizedEmail) {
                existingCustomer = await tx.customer.create({
                    data: {
                        full_name: (customer_name && customer_name.trim()) || 'Khách hàng',
                        email: normalizedEmail,
                        phone: validPhone || null
                    }
                });
            }
            if (existingCustomer) {
                finalCustomerId = existingCustomer.id;
            }
        }

        let totalAmount = 0;
        const orderItemsToCreate = [];
        const outOfStockError = [];
        for (const item of items) {
            const product = await tx.product.findUnique({
                where: { id: item.product_id }
            })
            if (!product) {
                outOfStockError.push({
                    product_id: item.product_id,
                    message: 'Product do not exists'
                });
                continue;
            }
            if (product.status !== 'available') {
                outOfStockError.push({
                    product_id: product.id,
                    product_name: product.name,
                    message: "Product not available"
                });
                continue;
            }
            if (product.quantity < item.quantity) {
                outOfStockError.push({
                    product_id: product.id,
                    product_name: product.name,
                    request_quantity: item.quantity,
                    available_quantity: product.quantity,
                    message: "Do not enough request quantity"
                });
                continue;
            }
            orderItemsToCreate.push({
                product,
                requestedQuantity: item.quantity,
                note: item.note
            })
        }
        if (outOfStockError.length > 0) {
            const error = new Error('Product not available to served');
            error.statusCode = 409;
            error.details = outOfStockError;
            throw error;
        }
        const finalOrderItem = [];
        for (const item of orderItemsToCreate) {
            const stockUpdate = await tx.product.updateMany({
                where: {
                    id: item.product.id,
                    status: 'available',
                    quantity: { gte: item.requestedQuantity }
                },
                data: {
                    quantity: { decrement: item.requestedQuantity }
                }
            });
            if (stockUpdate.count !== 1) {
                const error = new Error('Product stock changed while creating the order');
                error.statusCode = 409;
                error.details = `Please reload product ${item.product.name} and try again`;
                throw error;
            }
            await tx.product.updateMany({
                where: { id: item.product.id, quantity: 0 },
                data: { status: 'unavailable' }
            });
            const subtotal = item.product.price * item.requestedQuantity;
            totalAmount += subtotal;

            finalOrderItem.push({
                product_id: item.product.id,
                product_name: item.product.name,
                unit_price: item.product.price,
                quantity: item.requestedQuantity,
                subtotal: subtotal,
                note: item.note || null
            })
        }

        const createOrder = await tx.order.create({
            data: {
                customer_id: finalCustomerId,
                table_id: table_id,
                created_by: userID,
                total_amount: totalAmount,
                status: 'pending',
                note: note || null,
                items: {
                    create: finalOrderItem
                }
            },
            include: {
                items: true,
                table: true,
                customer: true
            }
        })

        if (table_id) {
            await tx.restaurantTable.update({
                where: { id: table_id },
                data: { status: 'occupied' }
            });
        }
        await tx.orderStatusHistory.create({
            data: {
                order_id: createOrder.id,
                changed_by: userID,
                old_status: 'none',
                new_status: 'pending',
                note: 'Create new order'
            }
        });
        return createOrder;
    }, { isolationLevel: 'Serializable' });

    // Invalidate Cache sau khi tạo đơn thành công (trừ kho & đổi trạng thái bàn)
    try {
        for (const item of items) {
            if (redisClient.isOpen) await redisClient.del(`product:${item.product_id}`);
        }
        await clearCachePattern('products:*');
        await clearCachePattern('tables:*');
        await clearCachePattern('orders:stats');
        console.log('[Redis Invalidate] Đã xóa cache products, tables, orders:stats sau khi tạo đơn');
    } catch (e) {
        console.error('[Redis Invalidate Error] createOrder:', e.message);
    }

    return createdOrder;
};

const updateOrder = async (orderData, userID) => {
    const { orderID, new_status, note } = orderData;
    let orderCancelledItems = [];

    const updatedResult = await prisma.$transaction(async (tx) => {
        const user = await tx.user.findUnique({
            where: { id: userID }
        });
        if (!user || user.status !== 'active') {
            const err = new Error('Authorization Error');
            err.statusCode = 403;
            err.details = 'User is missing or inactive';
            throw err;
        }
        const order = await tx.order.findUnique({
            where: { id: orderID },
            include: { items: true }
        });
        if (!order) {
            const error = new Error('Order not found');
            error.statusCode = 404;
            throw error;
        }
        assertOrderTransitionAllowed(order.status, new_status, user.role);
        await tx.orderStatusHistory.create({
            data: {
                order_id: orderID,
                changed_by: userID,
                old_status: order.status,
                new_status: new_status,
                note: note || null
            }
        });
        const result = await tx.order.update({
            where: { id: orderID },
            data: {
                status: new_status
            }
        });
        if (new_status === 'cancelled') {
            orderCancelledItems = order.items;
            for (const item of order.items) {
                await tx.product.update({
                    where: {
                        id: item.product_id
                    },
                    data: {
                        quantity: { increment: item.quantity },
                        status: 'available'
                    }
                });
            }
            if (order.table_id) {
                const otherActiveOrders = await tx.order.findFirst({
                    where: {
                        table_id: order.table_id,
                        id: { not: order.id },
                        status: { in: ['pending', 'confirmed', 'cooking', 'ready', 'served'] }
                    }
                });
                if (!otherActiveOrders) {
                    await tx.restaurantTable.update({
                        where: { id: order.table_id },
                        data: { status: 'available' }
                    });
                }
            }
        }
        return result;
    });

    // Invalidate Cache sau khi đổi trạng thái đơn
    try {
        if (new_status === 'cancelled') {
            for (const item of orderCancelledItems) {
                if (redisClient.isOpen) await redisClient.del(`product:${item.product_id}`);
            }
            await clearCachePattern('products:*');
            await clearCachePattern('tables:*');
            console.log('[Redis Invalidate] Đã hoàn kho & xóa cache products, tables khi hủy đơn');
        }
        await clearCachePattern('orders:stats');
    } catch (e) {
        console.error('[Redis Invalidate Error] updateOrder:', e.message);
    }

    return updatedResult;
};

module.exports = { createOrder, updateOrder }
