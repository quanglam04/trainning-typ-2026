const nodemailer = require('nodemailer');

const sendReservationConfirmEmail = async ({ customerEmail, customerName, tableNumber, reservationTime, numberOfPeople }) => {
    const testAccount = await nodemailer.createTestAccount();
    const transporter = nodemailer.createTransport({
        host: 'smtp.ethereal.email',
        port: 587,
        auth: { user: testAccount.user, pass: testAccount.pass }
    });

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
    console.log(`[MailService] Đã gửi mail đặt bàn tới ${customerEmail}`);
    console.log(`[MailService] Xem preview email tại: ${nodemailer.getTestMessageUrl(info)}`);
    return info;
};

/**
 * Gửi email hóa đơn thanh toán kèm danh sách món đã dùng
 */
const sendOrderInvoiceEmail = async ({ customerEmail, customerName, orderId, totalAmount, paymentMethod, items, tableNumber }) => {
    const testAccount = await nodemailer.createTestAccount();
    const transporter = nodemailer.createTransport({
        host: 'smtp.ethereal.email',
        port: 587,
        auth: { user: testAccount.user, pass: testAccount.pass }
    });

    const itemsHtml = items.map(item => `
        <tr>
            <td style="padding: 8px; border: 1px solid #ddd;">${item.product_name}</td>
            <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">${item.quantity}</td>
            <td style="padding: 8px; border: 1px solid #ddd; text-align: right;">${item.unit_price.toLocaleString('vi-VN')} đ</td>
            <td style="padding: 8px; border: 1px solid #ddd; text-align: right;">${item.subtotal.toLocaleString('vi-VN')} đ</td>
        </tr>
    `).join('');

    const mailOptions = {
        from: "MTD Restaurant <billing@mtd-restaurant.com>",
        to: customerEmail,
        subject: `Hóa đơn thanh toán đơn hàng #${orderId}`,
        html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 8px;">
            <h2 style="color: #27ae60; text-align: center;">HÓA ĐƠN THANH TOÁN</h2>
            <p>Kính gửi quý khách <b>${customerName || 'Quý khách'}</b>,</p>
            <p>Nhà hàng MTD xin chân thành cảm ơn quý khách đã dùng bữa. Dưới đây là thông tin chi tiết hóa đơn:</p>
            
            <table style="width: 100%; margin-bottom: 20px;">
                <tr><td><b>Mã đơn hàng:</b></td><td>${orderId}</td></tr>
                <tr><td><b>Bàn số:</b></td><td>${tableNumber || 'Mang về'}</td></tr>
                <tr><td><b>Hình thức thanh toán:</b></td><td>${paymentMethod.toUpperCase()}</td></tr>
                <tr><td><b>Thời gian thanh toán:</b></td><td>${new Date().toLocaleString('vi-VN')}</td></tr>
            </table>

            <h3 style="border-bottom: 2px solid #27ae60; padding-bottom: 5px;">Chi tiết món đã gọi:</h3>
            <table style="width: 100%; border-collapse: collapse; margin-bottom: 20px;">
                <thead>
                    <tr style="background-color: #f8f9fa;">
                        <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">Tên món</th>
                        <th style="padding: 8px; border: 1px solid #ddd; text-align: center;">SL</th>
                        <th style="padding: 8px; border: 1px solid #ddd; text-align: right;">Đơn giá</th>
                        <th style="padding: 8px; border: 1px solid #ddd; text-align: right;">Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    ${itemsHtml}
                </tbody>
            </table>

            <div style="text-align: right; font-size: 18px; color: #e74c3c;">
                <b>TỔNG CỘNG: ${totalAmount.toLocaleString('vi-VN')} VNĐ</b>
            </div>

            <p style="margin-top: 30px; text-align: center; color: #7f8c8d; font-size: 13px;">
                Rất hân hạnh được phục vụ quý khách trong những lần tới!<br>
                <i>MTD Restaurant - Hotline: 1900 1234</i>
            </p>
        </div>
        `
    };

    const info = await transporter.sendMail(mailOptions);
    console.log(`[MailService] 📧 Đã gửi hóa đơn đơn hàng #${orderId} tới ${customerEmail}`);
    console.log(`[MailService] 🔗 Xem preview email tại: ${nodemailer.getTestMessageUrl(info)}`);
    return info;
};

module.exports = {
    sendReservationConfirmEmail,
    sendOrderInvoiceEmail
};
