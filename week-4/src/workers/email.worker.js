const { getChannel } = require('../config/rabbitmq');

const QUEUE_NAME = 'user_welcome_email';

async function startWorker() {
  const channel = await getChannel();
  await channel.assertQueue(QUEUE_NAME, { durable: true });
  channel.prefetch(1);

  console.log(`[*] Worker đang chờ message từ queue: ${QUEUE_NAME}`);

  channel.consume(
    QUEUE_NAME,
    async (msg) => {
      if (!msg) return;

      try {
        const payload = JSON.parse(msg.content.toString());
        console.log(`[Processing] Đang gửi mail cho: ${payload.email}`);
        
        // Giả lập gửi mail tốn 2 giây
        await new Promise((resolve) => setTimeout(resolve, 2000));
        
        console.log(`[Success] Đã gửi mail thành công: ${payload.email}`);
        channel.ack(msg);
      } catch (err) {
        console.error('[Error] Lỗi xử lý message:', err.message);
        channel.nack(msg, false, false);
      }
    },
    { noAck: false }
  );
}

startWorker();