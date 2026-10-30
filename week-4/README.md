# Tuần 4: Message Queue (RabbitMQ)

## 1. Tìm hiểu Message Queue
- **Message Queue là gì:** Thành phần trung gian lưu trữ thông điệp (messages) giữa Producer và Consumer, cho phép các tiến trình trao đổi dữ liệu bất đồng bộ.
- **Tại sao cần MQ:** Tránh rủi ro nghẽn I/O khi thực hiện các tác vụ nặng (gửi email, xử lý hình ảnh, xuất báo cáo). Người dùng nhận phản hồi ngay lập tức (<50ms) thay vì phải đợi 2-3 giây. Đảm bảo tính độc lập và chịu lỗi (fault tolerance).
- **Mô hình sử dụng:** Point-to-Point (Queue) với RabbitMQ, mỗi tin nhắn được xử lý bởi một worker duy nhất.

## 2. Kiến trúc RabbitMQ trong Project
- **Producer:** API Server (`/api/auth/register`) tiếp nhận request và đẩy event vào RabbitMQ.
- **Queue:** `user_welcome_email` (durable: true, persistent: true).
- **Consumer:** Worker độc lập (`email.worker.js`) lấy tin nhắn từ queue, giả lập gửi email và gửi `ack` xác nhận.

## 3. Các vấn đề cần lưu ý
- **Idempotency:** Kiểm tra ID của message để tránh xử lý trùng lặp khi mạng bị gián đoạn.
- **Acknowledge Mechanism:** Sử dụng `noAck: false`, chỉ xác nhận xóa tin nhắn khỏi hàng đợi sau khi xử lý thành công.
- **Dead Letter Queue (DLQ):** Lưu trữ các tin nhắn bị lỗi sau nhiều lần retry để kiểm tra thủ công.

## 4. Hướng dẫn chạy thử
1. Khởi động RabbitMQ:
   ```bash
   docker compose up -d