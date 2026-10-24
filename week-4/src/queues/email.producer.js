const { getChannel } = require('../config/rabbitmq');

const QUEUE_NAME = 'user_welcome_email';

async function sendWelcomeEmailEvent(data) {
  const channel = await getChannel();
  await channel.assertQueue(QUEUE_NAME, { durable: true });
  
  return channel.sendToQueue(QUEUE_NAME, Buffer.from(JSON.stringify(data)), {
    persistent: true,
  });
}

module.exports = { sendWelcomeEmailEvent };