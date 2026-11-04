# 08 — Câu hỏi ôn tập Message Queue

## Vì sao không gửi email ngay trong register?

SMTP làm request phải chờ và thêm dependency lỗi vào flow. Outbox + worker cho phép
commit tài khoản trước, retry email sau. Vẫn phải đợi transaction DB; không cam kết
zero latency hoặc email đã đến khi HTTP 201.

## Tại sao cần outbox nếu đã có publisher confirm?

Confirm chỉ bao phủ publisher/broker, không làm insert user và publish atomic.
Outbox ghi cùng transaction user rồi publish sau, chống mất event ở dual-write.
Vẫn có thể publish trùng khi confirm/DB commit không rõ kết quả.

## Confirm khác ack thế nào?

Confirm từ broker cho publisher; ack từ consumer sau xử lý delivery. Broker confirm
không chứng minh email gửi xong. Message unroutable có thể confirm nên dùng mandatory
và xử lý return trước mark published.

## Hai worker có nhận cùng message không?

Cùng queue thường chia việc; một delivery giao một consumer, nhưng redelivery và
publish trùng có thể khiến cùng event bị xử lý nhiều lần. Pub/sub nhiều subscriber
cần queues riêng, không chỉ thêm consumers cùng queue.

## Consumer chết có đi DLQ ngay không?

Không. Unacked được requeue khi connection đóng; DLQ theo reject/dead-letter policy,
TTL hoặc delivery limit. Retry ứng dụng trong bài tối đa ba lần; broker crash-loop
counter riêng, không đồng nhất với retry SMTP.

## Có exactly-once email chưa?

Không. Ledger và khóa chống duplicate đã commit/race bình thường, nhưng SMTP accepted
rồi DB chưa commit vẫn có cửa sổ gửi trùng. Message-ID ổn định không bảo đảm SMTP
deduplicate. Tuyên bố exactly-once từ một unique key trong DB là thiếu điều kiện.

## Queue rỗng có nghĩa email đã gửi chưa?

Chưa chắc: kiểm tra Unacked, retry, DLQ, ledger và Mailpit. published_at chỉ chứng
minh đã publish. SMTP accepted cũng không chứng minh thư Internet đã đến inbox;
bài chỉ dùng Mailpit local, không gửi ra ngoài.

## Nếu broker tắt thì đăng ký ra sao?

Khi queue bật, API chỉ dùng PostgreSQL: user + outbox vẫn có thể commit/201. Worker
nối lại rồi dispatch pending. DB/outbox lỗi thì rollback user và trả lỗi, không lờ
sự kiện đi. Nếu queue tắt, đăng ký theo flow tuần 3 và không tạo welcome event.

## Có giữ thứ tự không?

Welcome giữa users độc lập không cần strict ordering. Retry/multiple consumers có
thể đảo thứ tự hoàn tất. Prefetch 1 không tạo global ordering; chưa triển khai
sequence/version theo aggregate.

## Còn thiếu gì cho production?

SMTP provider/TLS/auth, broker HA/backup, retention, monitoring/alerting, quyền và
công cụ replay DLQ, load test. App chưa tích hợp API. RabbitMQ một node local và
kiểm thử chức năng không chứng minh đủ năng lực production.

[Mục lục](00_GUIDE_INDEX.md) · [Báo cáo](../WEEK4_REPORT.md) · [Nguồn](../SOURCE_SNAPSHOT.md).
