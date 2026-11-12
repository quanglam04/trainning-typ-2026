const { kafka, consumer } = require('../config/kafka');
const mailService = require('../services/mail.service');

const ensureTopicExists = async () => {
    const admin = kafka.admin();
    try {
        await admin.connect();
        const existingTopics = await admin.listTopics();
        if (!existingTopics.includes('reservation-created')) {
            await admin.createTopics({
                topics: [
                    {
                        topic: 'reservation-created',
                        numPartitions: 3,
                        replicationFactor: 1
                    }
                ]
            });
            console.log('[Kafka Admin] Đã tạo topic reservation-created (3 partitions)');
        }
    } catch (err) {
        console.warn('[Kafka Admin] Bỏ qua khởi tạo topic:', err.message);
    } finally {
        await admin.disconnect();
    }
};

const startReservationConsumer = async () => {
    await ensureTopicExists();
    await consumer.connect();
    await consumer.subscribe({ topic: 'reservation-created', fromBeginning: false });

    console.log('[Consumer] Đang lắng nghe topic reservation-created...');

    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            const rawData = message.value.toString();
            const reservation = JSON.parse(rawData);
            console.log(`[Consumer] Nhận được message từ partition ${partition}:`, reservation.id);

            try {
                const startTime = Date.now();

                await mailService.sendReservationConfirmEmail({
                    customerEmail: reservation.customerEmail,
                    customerName: reservation.customerName,
                    tableNumber: reservation.tableNumber,
                    reservationTime: reservation.reservationTime,
                    numberOfPeople: reservation.numberOfPeople
                });

                const endTime = Date.now();
                const mailDuration = endTime - startTime;
                console.log(`Thời gian gửi mail qua SMTP: ${mailDuration}ms (${(mailDuration / 1000).toFixed(2)}s)`);

                if (reservation.producedAt) {
                    const totalQueueTime = endTime - reservation.producedAt;
                    console.log(`Tổng thời gian từ lúc đặt bàn -> gửi mail xong: ${totalQueueTime}ms (${(totalQueueTime / 1000).toFixed(2)}s)`);
                }
            } catch (error) {
                console.error(`[Consumer] Lỗi khi gửi email:`, error);
            }
        }
    });
};

module.exports = { startReservationConsumer };
