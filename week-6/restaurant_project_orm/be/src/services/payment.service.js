const prisma = require('../config/prisma');
const { sendOrderPaidEvent } = require('../producers/order.producer');
const { clearCachePattern } = require('../config/redis');
const { assertOrderPayable } = require('./payment-policy.service');

/**
 * Xử lý xác nhận thanh toán đơn hàng
 * - Kiểm tra order có hợp lệ và chưa thanh toán
 * - Chạy Transaction: tạo Payment, đổi Order status -> 'completed', đổi Table status -> 'available', lưu History
 * - Bắn event qua Kafka bất đồng bộ để gửi email hóa đơn
 */
const createPayment = async (paymentData, cashierId) => {
  const { order_id, payment_method } = paymentData;

  const VALID_METHODS = ['cash', 'banking', 'card', 'e_wallet'];
  const method = (payment_method || 'cash').toLowerCase();
  if (!VALID_METHODS.includes(method)) {
    const error = new Error('Invalid payment method');
    error.statusCode = 400;
    error.details = `Supported methods: ${VALID_METHODS.join(', ')}`;
    throw error;
  }

  // 1. Thực hiện Transaction trong Database
  const result = await prisma.$transaction(async (tx) => {
    const order = await tx.order.findUnique({
      where: { id: order_id },
      include: {
        payment: true,
        customer: true,
        table: true,
        items: true
      }
    });

    if (!order) {
      const error = new Error('Order not found');
      error.statusCode = 404;
      throw error;
    }

    assertOrderPayable(order);

    // Tạo bản ghi thanh toán
    const payment = await tx.payment.create({
      data: {
        order_id: order.id,
        cashier_id: cashierId,
        amount: order.total_amount,
        payment_method: method,
        status: 'paid',
        paid_at: new Date()
      }
    });

    // Đổi trạng thái Order sang completed
    await tx.order.update({
      where: { id: order.id },
      data: { status: 'completed' }
    });

    // Giải phóng bàn ăn (chỉ khi không còn đơn active nào khác trên bàn)
    if (order.table_id) {
      const remainingActive = await tx.order.findFirst({
        where: {
          table_id: order.table_id,
          id: { not: order.id },
          status: { in: ['pending', 'confirmed', 'cooking', 'ready', 'served'] }
        }
      });
      if (!remainingActive) {
        await tx.restaurantTable.update({
          where: { id: order.table_id },
          data: { status: 'available' }
        });
      }
    }

    // Ghi nhận lịch sử chuyển trạng thái
    await tx.orderStatusHistory.create({
      data: {
        order_id: order.id,
        changed_by: cashierId,
        old_status: order.status,
        new_status: 'completed',
        note: `Paid via ${method}. Table released.`
      }
    });

    return { payment, order };
  });

  // 2. Bắn sự kiện sang Kafka để gửi Email hóa đơn bất đồng bộ (sau khi DB đã commit thành công)
  try {
    const { order, payment } = result;
    let customerEmail = order.customer?.email;
    let customerName = order.customer?.full_name;

    // Fallback nếu order không có quan hệ customer nhưng trong note có ghi "Khách: email@..."
    if (!customerEmail && order.note) {
      const emailMatch = order.note.match(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/);
      if (emailMatch) {
        customerEmail = emailMatch[0];
      }
    }

    await sendOrderPaidEvent({
      orderId: order.id,
      customerEmail: customerEmail || null,
      customerName: customerName || 'Quý khách',
      totalAmount: order.total_amount,
      paymentMethod: payment.payment_method,
      tableNumber: order.table?.table_number || null,
      items: order.items.map(i => ({
        product_name: i.product_name,
        quantity: i.quantity,
        unit_price: i.unit_price,
        subtotal: i.subtotal
      }))
    });
  } catch (kafkaErr) {
    console.error('⚠️ [Payment] Lỗi bắn Kafka event gửi mail (DB đã thanh toán thành công):', kafkaErr.message);
  }

  // 3. Invalidate cache: giải phóng bàn & cập nhật doanh thu
  try {
    await clearCachePattern('tables:*');
    await clearCachePattern('orders:stats');
    console.log('[Redis Invalidate] Đã xóa cache tables:* và orders:stats sau khi thanh toán');
  } catch (e) {
    console.error('[Redis Invalidate Error] createPayment:', e.message);
  }

  return result.payment;
};

const getPaymentByOrderId = async (orderId) => {
  return await prisma.payment.findUnique({
    where: { order_id: orderId },
    include: {
      cashier: {
        select: { id: true, full_name: true, role: true }
      },
      order: true
    }
  });
};

module.exports = {
  createPayment,
  getPaymentByOrderId
};
