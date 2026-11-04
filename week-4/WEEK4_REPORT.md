# Báo cáo tuần 4 — Message Queue

**Học viên:** Nguyễn Tiến Tuấn. **Ngày thực hành:** 04/11/2026, Asia/Bangkok.
**Dự án:** EruSentia — Sentia Hub Backend.
**Trạng thái:** đã chốt nguồn tuần 4. [Snapshot](SOURCE_SNAPSHOT.md) ghi commit
backend đầy đủ, permalink và SHA-256 các file đã đối chiếu.
Tag lưu trữ báo cáo: `typ-2026-week-4`. [Đề gốc](README.md) giữ nguyên.

## 1. Mục tiêu và lựa chọn nghiệp vụ

Phát triển tiếp server NestJS/TypeORM/PostgreSQL tuần 3. User đăng ký thành công phát
sự kiện user.registered.v1; consumer gửi welcome email qua Mailpit local. RabbitMQ
được chọn cho task queue, routing và manual ack; không triển khai Kafka song song.
App chưa tích hợp API. Nguồn/SQL/tests ở server, TYP chỉ giữ báo cáo/lý thuyết/minh chứng.

| Yêu cầu | Thực hiện |
|---|---|
| MQ, sync/async, khi nào dùng | Bài 01, phân biệt kết quả HTTP với email đến sau |
| Queue và pub/sub | Bài 02; triển khai một subscriber welcome, nhiều worker có thể chia việc |
| Producer/consumer/exchange/queue | Topic exchange + durable quorum work/retry/dead queues |
| Docker | RabbitMQ 4.4.0-management, Mailpit 1.32.0, localhost ports |
| API → MQ → consumer → kết quả | Register 201 → outbox → publish → SMTP → ledger/ack |
| Duplicate/idempotency | Event ID, ledger PK, advisory lock, test fresh-event race |
| Ack/retry/DLQ | Confirm + mandatory, manual ack, TTL retry tối đa ba lần, DLQ |
| Ordering | Giải thích giới hạn; welcome giữa users không cần global order |
| Demo và output | 6 ảnh giao diện thật + integration PostgreSQL/RabbitMQ/SMTP |

## 2. Flow và phạm vi thay đổi

QUEUE_ENABLED=false mặc định giữ flow tuần 3. Khi bật, chỉ PostgreSQL được hỗ trợ.
Register vẫn trả 201 UserDto; user và event ghi chung transaction. API không chờ
RabbitMQ hoặc SMTP. Worker riêng poll batch 10 mỗi giây, publish persistent + mandatory,
chờ confirm rồi mark published. Consumer prefetch=1, đọc email hiện tại, gửi SMTP,
ghi ledger sent/skipped và ack.

Không thêm endpoint không cần thiết hoặc đổi hợp đồng CRUD/JWT. CLI tạo admin không
phát welcome event. Email chỉ được nhận tại Mailpit local; không phải verification,
không kích hoạt account App, không gửi ra Internet.

Migration 06 thêm message_outbox và welcome_email_deliveries sau auth; đủ migrations
có 12 bảng. SQL schema/index/seed gốc giữ nguyên, không bật ORM synchronize hoặc
auto migrate khi khởi động. Không áp migration/seed vào DB nghiệp vụ trong lượt demo.

## 3. Độ tin cậy và các ranh giới

- Outbox giải quyết user/event dual-write bằng một transaction PostgreSQL. Broker lỗi
  không làm API đợi MQ; outbox lỗi thì rollback cả đăng ký.
- Confirm chỉ xác nhận broker, không chứng minh consumer/email hoàn tất. Unroutable
  publish phải kiểm tra mandatory return, không mark published nhầm.
- Manual ack sau ledger commit. Mất connection trước ack requeue/redeliver; không
  giải thích sai rằng consumer chết là đi DLQ ngay.
- SMTP/DB lỗi tạm thời → republish vào retry, confirm rồi ack bản cũ; TTL 5 giây về
  work. Fail ba lần đi DLQ; malformed/unknown event vào DLQ ngay. Delivery-limit của
  broker là counter riêng cho crash loop.
- Ledger/lock ngăn duplicate đã commit và race hai workers. SMTP accepted rồi DB
  chưa commit vẫn có thể gửi trùng: không cam kết exactly-once. Message-ID ổn định
  hỗ trợ đối chiếu, không buộc SMTP deduplicate.
- Retry/multiple consumers có thể đổi thứ tự hoàn tất. Chưa có ordering theo aggregate.

Event chỉ chứa ID/type/version/time, không chứa password/hash/email/JWT/refresh token.
LocalWelcomeMailer chỉ cho SMTP localhost. RabbitMQ/UI/Mailpit mới chỉ bind loopback.
Single-node quorum chưa có HA và không thay backup/retention/monitoring production.

## 4. Môi trường và kiểm chứng

Node.js 24.21.0, pnpm 12.11.2, NestJS 12.3.2, TypeORM 0.3.31, PostgreSQL 16.15.
RabbitMQ 4.4.0, Mailpit 1.32.0, amqplib 2.2.1, Nodemailer 10.3.1.

| Kiểm tra | Kết quả |
|---|---|
| Frozen lockfile, typecheck/build | Đạt |
| pnpm test | 35 pass, 0 fail, 2 integration suites skip mặc định |
| PostgreSQL CRUD/auth tuần 3 | 19/19 pass với queue tắt |
| Queue integration đầy đủ | 13/13 pass trên DB mới/rỗng + broker + Mailpit |
| Swagger register, worker chưa consume | 201, event pending rồi message Ready=1 |
| SMTP thật | Mailpit nhận welcome email, ledger sent, queue ack/rỗng |
| SMTP local bị từ chối | Retry queue rồi DLQ; event lỗi không có sent ledger |

Test runner tính cả parent/subtest, không phải số endpoint. Bộ queue test gồm
registration/outbox atomicity, duplicate email, publish unroutable, duplicate event,
fresh-event concurrency, retry thành công, DLQ và replay sau sửa, malformed/unknown
event, close-before-ack redelivery, inactive user và worker CLI/SIGTERM. Fail mailer
trong một số test là dependency có kiểm soát; email thành công đi qua SMTP thật.
Ảnh retry/DLQ dùng SMTP_PORT=1 để tạo lỗi kết nối thật, không dựng response.

DB riêng dùng các tên week4_test_20261104_queue_1, queue_2, queue_final, demo và
week3_test_20261104_week4_regression. Namespace test ngẫu nhiên, namespace ảnh week4demo.
Sau kiểm chứng đã dọn đúng DB/namespace/email/file tạm; API/browser/worker demo đã đóng.
DB nghiệp vụ sentia_hub_db giữ 10 users và 1.012 templates, chưa áp migration 05/06;
container/volume dev được giữ.

## 5. Minh chứng và nguồn

[Hướng dẫn demo](docs/07_demo_and_evidence.md) có 6 ảnh Swagger/RabbitMQ/Mailpit gắn
với từng bước và giải thích trạng thái Ready/Unacked/Consumers. Ảnh không có khoanh
vùng, không sửa dữ liệu hoặc công bố token. RabbitMQ/Mailpit dùng giao diện dark thật.

[Snapshot nguồn](SOURCE_SNAPSHOT.md) dẫn auth store, event, dispatcher/consumer,
mailer, SQL, Compose, tests và hướng dẫn server bằng permalink commit đầy đủ.
Báo cáo lưu tại tag `typ-2026-week-4`, giữ nguyên các mốc tuần 1–3.

## 6. Kết luận trong phạm vi bài và hướng tiếp theo

Use case welcome thể hiện yêu cầu MQ ngay trên đăng ký đang có, không cần project
đơn hàng giả hoặc thay stack. Thiết kế thêm thành phần vận hành để tách tác vụ nền;
chưa benchmark để kết luận nhanh/rẻ hơn một SMTP request trực tiếp trong mọi trường hợp.

Chưa có SMTP provider/TLS/auth ngoài local, broker HA/backup, retention outbox/ledger,
monitoring/alerting, UI replay DLQ, load test hoặc tích hợp App. Cache/Redis thuộc
Tuần 5, chưa thêm vào bài này. Không kết luận sẵn sàng production từ demo local.
