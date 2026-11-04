# 05 — Acknowledgement, retry và Dead Letter Queue

## Hai loại xác nhận

Publisher confirm nói broker đã nhận trách nhiệm với publication. mandatory return
báo message không route được đến queue; một publish vẫn có thể được confirm khi
unroutable, nên phải kiểm tra cả hai. Dispatcher chỉ mark published sau kết quả hợp lệ.

Consumer manual ack nói delivery đã xử lý xong, broker có thể bỏ bản đó. Ack chỉ sau
SMTP/ledger transaction thành công. Auto-ack ngay khi nhận có thể mất công việc nếu
consumer chết trước xử lý; manual ack không tự tạo exactly-once business effect.

## Worker chết

Connection/channel đóng khi còn unacked khiến message được requeue/redeliver, không
phải lần chết đầu tiên tự động vào DLQ. Nếu đã gửi email mà chưa ledger/ack, lần
sau có thể gửi trùng; xem giới hạn SMTP ở bài 06.

Quorum work queue đặt delivery-limit=3 để hạn chế crash loop; vượt giới hạn broker
đưa sang DLX. Đây là counter khác x-attempt đếm retry ứng dụng.

## Retry ứng dụng

SMTP/DB lỗi tạm thời: worker publish persistent message vào retry queue, chờ confirm
và kiểm tra routing, rồi mới ack bản cũ. Retry queue không có consumer; TTL 5000 ms
hết thì dead-letter về events exchange và quay lại work queue.

| Lần xử lý | x-attempt lúc nhận | Nếu lỗi |
|---|---|---|
| Đầu | Chưa có / 0 | Retry với 1 |
| Hai | 1 | Retry với 2 |
| Ba | 2 | Vào dead queue với 3 |

Payload sai/type lạ/event không thuộc outbox là lỗi vĩnh viễn, đi dead ngay, không
đợi 3 lần. Retry dùng mã lỗi chung, không chuyển raw credential/SMTP error vào headers.
Forward lỗi thì không ack bản gốc; đóng connection để requeue thay vì bỏ message.

## DLQ và an toàn

DLQ là nơi giữ message xử lý thất bại để điều tra, không phải một cơ chế tự sửa lỗi
hoặc bảo đảm mọi delivery thành công. Replay sau khi sửa nguyên nhân phải giữ event
ID/body, reset attempt có kiểm soát, tránh tạo ID mới để né idempotency.

Work/retry dùng quorum DLX at-least-once và overflow=reject-publish. Chưa triển khai
broker HA; TTL/retry có thể trễ hơn cấu hình, message/forward/ack vẫn có thể trùng.
Không dùng nack requeue=true liên tục khi SMTP lỗi vì dễ tạo vòng lặp nóng.

## Kiểm chứng

Integration test có SMTP fail hai lần rồi thành công; fail cả ba lần vào DLQ;
invalid JSON/event lạ vào DLQ; publish unroutable giữ outbox pending; đóng connection
trước ack giao lại message; operator replay sau sửa mailer tạo một ledger sent.
Các ca fail có kiểm soát được phân biệt với SMTP thật tới Mailpit.

Nguồn: [RabbitMQ confirms](https://www.rabbitmq.com/docs/confirms),
[DLX](https://www.rabbitmq.com/docs/dlx), [TTL](https://www.rabbitmq.com/docs/ttl).

[Trước](04_outbox_and_registration.md) · [Tiếp: idempotency](06_idempotency_and_ordering.md)
