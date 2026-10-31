const nodemailer = require('nodemailer');

const sendReservationConfirmEmail = async ({ customerEmail, customerName, tableNumber, reservationTime, numberOfPeople }) => {
    const testAccount = await nodemailer.createTestAccount();
    const transporter = nodemailer.createTransport({
        host: 'smtp.ethereal.email',
        port: 587,
        auth: { user: testAccount.user, pass: testAccount.pass }
    })

    const mailOptions = {
        from: "MTD restaurant <mtd@gmail.com>",
        to: customerEmail,
        subject: 'Xác nhận đặt bàn thành công',
        html: `
        <h3>Kính gửi ${customerName},</h3>
        <p>Bạn đã đặt bàn thành công tại nhà hàng:</p>
        <ul>
            <li><b>Bàn số:</b> ${tableNumber}</li>
            <li><b>Số lượng khách:</b> ${numberOfPeople} người</li>
            <li><b>Thời gian:</b> ${new Date(reservationTime).toLocaleString('vi-VN')}</li>
        </ul>
        <p>Hân hạnh được phục vụ quý khách!</p>
        `
    };

    const info = await transporter.sendMail(mailOptions);

    console.log(`[MailService] Đã gửi mail tới ${customerEmail}`);
    console.log(`[MailService] Xem preview email tại: ${nodemailer.getTestMessageUrl(info)}`);

    return info;
}

module.exports = { sendReservationConfirmEmail };