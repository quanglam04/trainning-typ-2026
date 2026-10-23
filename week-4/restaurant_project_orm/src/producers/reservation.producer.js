const { producer } = require('../config/kafka');

let isConnected = false;

const sendReservationCreatedEvent = async (reservationData) => {
    if (!isConnected) {
        await producer.connect();
        isConnected = true;
    }

    await producer.send({
        topic: 'reservation-created',
        messages: [
            {
                key: reservationData.id,
                value: JSON.stringify({
                    ...reservationData,
                    producedAt: Date.now()
                })
            }
        ]
    })
    console.log(`[Producer] Đã bắn event đặt bàn ${reservationData.id} vào Kafka`);
};

module.exports = { sendReservationCreatedEvent };
