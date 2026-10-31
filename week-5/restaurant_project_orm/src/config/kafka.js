const { Kafka, Partitioners } = require('kafkajs');

const kafka = new Kafka({
    clientId: 'restaurant-service',
    brokers: [process.env.KAFKA_BROKERS || 'localhost:9092']
});

const producer = kafka.producer({
    createPartitioner: Partitioners.LegacyPartitioner
});

const consumer = kafka.consumer({ groupId: 'reservation-email-group' });

module.exports = { kafka, producer, consumer };