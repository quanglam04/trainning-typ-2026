const { kafka, orderConsumer } = require('../config/kafka');
const mailService = require('../services/mail.service');

const ensureOrderTopicExists = async () => {
    const admin = kafka.admin();
    try {
        await admin.connect();
        const existingTopics = await admin.listTopics();
        if (!existingTopics.includes('order-paid')) {
            await admin.createTopics({
                topics: [
                    {
                        topic: 'order-paid',
                        numPartitions: 3,
                        replicationFactor: 1
                    }
                ]
            });
            console.log('[Kafka Admin] Đã tạo topic order-paid (3 partitions)');
        }
    } catch (err) {
        console.warn('[Kafka Admin] Bỏ qua khởi tạo topic order-paid:', err.message);
    } finally {
        await admin.disconnect();
    }
};

const startOrderConsumer = async () => {
    await ensureOrderTopicExists();
    await orderConsumer.connect();
    await orderConsumer.subscribe({ topic: 'order-paid', fromBeginning: false });

    console.log('[OrderConsumer] 🎧 Đang lắng nghe topic order-paid...');

    await orderConsumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            const rawData = message.value.toString();
            const orderPaidData = JSON.parse(rawData);
            console.log(`[OrderConsumer] 📩 Nhận event order-paid cho đơn #${orderPaidData.orderId}`);

            try {
                if (orderPaidData.customerEmail) {
                    await mailService.sendOrderInvoiceEmail({
                        customerEmail: orderPaidData.customerEmail,
                        customerName: orderPaidData.customerName,
                        orderId: orderPaidData.orderId,
                        totalAmount: orderPaidData.totalAmount,
                        paymentMethod: orderPaidData.paymentMethod,
                        items: orderPaidData.items || [],
                        tableNumber: orderPaidData.tableNumber
                    });
                } else {
                    console.log(`[OrderConsumer] Đơn #${orderPaidData.orderId} không có email khách, bỏ qua gửi mail.`);
                }
            } catch (error) {
                console.error(`[OrderConsumer] ❌ Lỗi khi gửi hóa đơn qua email:`, error.message);
            }
        }
    });
};

module.exports = { startOrderConsumer };
