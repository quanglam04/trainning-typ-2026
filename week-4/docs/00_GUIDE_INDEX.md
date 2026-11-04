# Tuần 4 — Message Queue trong EruSentia

Bổ sung welcome email vào đăng ký của server tuần 3 bằng RabbitMQ và worker riêng.
App chưa tích hợp API. [Đề gốc](../README.md) giữ nguyên; TYP không chứa backend thứ hai.

| Bài | Nội dung |
|---|---|
| [01 — MQ và sync/async](01_message_queue.md) | Lý do tách tác vụ, trade-off, khi nào gọi trực tiếp |
| [02 — Các mô hình](02_queue_and_pubsub.md) | Competing consumers, pub/sub và ordering |
| [03 — RabbitMQ](03_rabbitmq_architecture.md) | Exchange, routing, queues, quorum, Docker |
| [04 — Transactional outbox](04_outbox_and_registration.md) | User + event atomic, dispatcher, dual-write |
| [05 — Delivery reliability](05_ack_retry_dlq.md) | Confirm, ack, retry, redelivery và DLQ |
| [06 — Idempotency](06_idempotency_and_ordering.md) | Ledger, khóa, SMTP duplicate window và thứ tự |
| [07 — Thực hành](07_demo_and_evidence.md) | Swagger → queue → Mailpit, ảnh và kiểm thử |
| [08 — Ôn tập](08_interview_qa.md) | Câu hỏi, quyết định thiết kế và giới hạn |

[Báo cáo](../WEEK4_REPORT.md) · [Nguồn đối chiếu](../SOURCE_SNAPSHOT.md) ·
[Tiến độ](../../docs/LEARNING_TRACKER.md).

## Cách học

Đọc bài 01–03 để hiểu mô hình, 04–06 để hiểu điểm lỗi, rồi thực hành bài 07.
Mã nguồn nằm trong erusentia-server, nhánh feat/week-4-message-queue. Chạy API và
worker ở hai terminal; RabbitMQ/Mailpit bằng Docker, PostgreSQL giữ stack hiện tại.
Dùng DB riêng khi tái hiện có ghi dữ liệu. README server dẫn trong snapshot có đầy
đủ biến môi trường, migration, lệnh start/test và giới hạn.

Không cần Postman. Swagger vẫn dùng JWT tuần 3 cho protected API; register là public.
Snapshot ghim commit server tuần 4 và SHA-256; báo cáo lưu tại tag typ-2026-week-4.
