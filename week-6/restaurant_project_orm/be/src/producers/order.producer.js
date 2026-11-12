const { producer } = require('../config/kafka');

let isConnected = false;

const sendOrderPaidEvent = async (orderPaidData) => {
    if (!isConnected) {
        await producer.connect();
        isConnected = true;
    }

    await producer.send({
        topic: 'order-paid',
        messages: [
            {
                key: orderPaidData.orderId,
                value: JSON.stringify({
                    ...orderPaidData,
                    producedAt: Date.now()
                })
            }
        ]
    });
    console.log(`[OrderProducer] 🚀 Đã bắn event 'order-paid' cho đơn hàng #${orderPaidData.orderId} vào Kafka`);
};

module.exports = { sendOrderPaidEvent };

