const amqp = require('amqplib');

let channel = null;

async function getChannel() {
  if (channel) return channel;
  try {
    const CLOUD_AMQP_URL = 'amqps://tznrqksx:5Bn8rj3EjDoCaD0NiZt4awesrFjPUmcc@fuji.lmq.cloudamqp.com/tznrqksx';
    const connection = await amqp.connect(process.env.RABBITMQ_URL || CLOUD_AMQP_URL);
    channel = await connection.createChannel();
    console.log('✓ Kết nối RabbitMQ Cloud thành công!');
    return channel;
  } catch (error) {
    console.error('RabbitMQ connection error:', error);
    throw error;
  }
}

module.exports = { getChannel };