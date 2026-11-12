const { Kafka, Partitioners } = require('kafkajs');

const kafka = new Kafka({
    clientId: 'restaurant-service',
    brokers: [process.env.KAFKA_BROKERS || 'localhost:9092']
});

const producer = kafka.producer({
    createPartitioner: Partitioners.LegacyPartitioner
});

// Consumer cho tác vụ gửi mail đặt bàn
const reservationConsumer = kafka.consumer({ groupId: 'reservation-email-group' });

// Consumer riêng cho tác vụ gửi mail hóa đơn thanh toán
const orderConsumer = kafka.consumer({ groupId: 'order-email-group' });

module.exports = { kafka, producer, consumer: reservationConsumer, orderConsumer };
