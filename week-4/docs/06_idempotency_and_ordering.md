# 06 — Duplicate, idempotency và ordering

## Duplicate là tình huống bình thường cần thiết kế

Publisher có thể đã được broker nhận nhưng mất confirm, hoặc DB commit published_at
lỗi nên gửi lại. Consumer có thể hoàn tất business effect nhưng mất ack. Vì vậy
thiết kế at-least-once phải chịu message trùng; UUID tự nó không loại duplicate.

Idempotency là xử lý nhiều lần vẫn cho kết quả nghiệp vụ tương đương một lần theo
một key/hợp đồng cụ thể. Key trong bài là event.id, giữ ổn định qua retry/replay.
Không tạo UUID mới khi retry và không chỉ dùng một Set trong RAM.

## Ledger và khóa

welcome_email_deliveries dùng event_id làm PK, lưu sent/skipped. Consumer mở
transaction, lấy advisory lock theo event ID, kiểm tra ledger rồi mới gửi SMTP/ghi
kết quả. Hai worker xử lý cùng event mới phải tuần tự hóa; worker sau thấy sent và
không gửi nữa. Account inactive/deleted ghi skipped, không gửi email.

Test concurrent dùng cùng event chưa từng xử lý, không chỉ replay event đã sent;
đối chiếu một email Mailpit và một ledger. Duplicate message sau đó không tăng email.

## Cửa sổ SMTP không thể che giấu

SMTP nhận email trước khi DB commit ledger. Nếu worker chết hoặc DB rollback ở
khoảng đó, retry chưa thấy sent nên có thể gửi thêm. Ngược lại, ghi sent trước SMTP
sẽ có nguy cơ không gửi dù ledger báo sent. Một DB transaction không giải được
cả hai với SMTP thông thường.

Bài không cam kết exactly-once email. Message-ID ổn định giúp truy vết nhưng không
buộc SMTP server/hộp thư deduplicate. Muốn bảo đảm mạnh hơn cần provider có hỗ trợ
idempotency hoặc thiết kế delivery phù hợp; test duplicate không chứng minh loại
bỏ hoàn toàn failure window này.

## Thứ tự

Welcome của các user độc lập, không cần strict global order. Nhiều consumers,
redelivery, TTL retry có thể làm B hoàn tất trước A. Prefetch=1 chỉ giới hạn mỗi
consumer có một unacked delivery, không khóa thứ tự toàn hệ thống.

Nếu nghiệp vụ sau này có nhiều event cùng user/template phụ thuộc nhau, cần thiết
kế version/sequence, partition hoặc cơ chế xử lý theo aggregate và stale event.
Không suy từ queue FIFO ra kết luận mọi side effect hoàn tất đúng thứ tự.

## Giới hạn vận hành

Outbox/ledger/DLQ cần retention, cảnh báo backlog và quyền replay. Bài hiện chưa có
purge job, monitoring dashboard riêng hoặc SMTP ngoài local. App chưa tích hợp auth
hay email; không coi welcome mail là xác minh tài khoản hoặc chức năng App đã có.

Nguồn: [RabbitMQ reliability](https://www.rabbitmq.com/docs/reliability),
[PostgreSQL advisory locks](https://www.postgresql.org/docs/16/explicit-locking.html#ADVISORY-LOCKS),
[Nodemailer SMTP](https://nodemailer.com/smtp).

[Trước](05_ack_retry_dlq.md) · [Tiếp: thực hành](07_demo_and_evidence.md)
