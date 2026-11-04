# 03 — RabbitMQ: kiến trúc và Docker

## Thành phần

Producer tạo message; exchange chọn queues theo bindings/routing key. Queue giữ
message chờ consumer. Consumer nhận delivery, xử lý và ack. Connection là kết nối
AMQP, channel là kênh logic trong connection; delivery tag thuộc channel đã giao nó.
Ack trên channel khác hoặc ack hai lần có thể làm channel lỗi.

Publisher confirm và consumer ack là hai phía khác nhau. Confirm không chờ consumer
hoàn thành; HTTP response của register cũng không chờ email.

## Topology đã triển khai

| Thành phần | Chức năng |
|---|---|
| erusentia.events | Durable topic exchange |
| erusentia.welcome | Durable quorum queue, binding user.registered.v1 |
| erusentia.welcome.retry | Quorum queue TTL, DLX về events |
| erusentia.dead | Direct DLX cho delivery limit của work queue |
| erusentia.welcome.dead | Quorum DLQ để điều tra/replay có kiểm soát |

Tên bắt đầu bằng QUEUE_NAMESPACE, giúp test/demo không chạm queue của người dùng.
Worker dùng amqplib trực tiếp để điều khiển mandatory, confirm, retry và ack rõ ràng;
API vẫn NestJS/TypeORM, không thay framework hoặc thêm backend trong TYP.

## Vì sao RabbitMQ?

RabbitMQ phù hợp task queue, routing và ack/retry của use case welcome. Kafka mạnh
ở event log, replay và xử lý luồng/partition nhưng thêm bài toán retention/offset/
consumer group. “Kafka production, RabbitMQ chỉ demo” là kết luận sai; lựa chọn phụ
thuộc nghiệp vụ và vận hành. Bài chọn một broker, không triển khai cả hai.

## Docker local

Compose queue riêng dùng RabbitMQ 4.4.0-management và Mailpit v1.32.0. AMQP 5672,
management 15672, SMTP 1025 và mailbox 8025 chỉ bind 127.0.0.1. RabbitMQ có volume;
Mailpit hiện không cấu hình volume, mailbox có thể mất khi recreate container.

Quorum queue trên một node local vẫn chỉ có một bản sao: chưa có HA. Persistent
message + durable queue + volume không chống mọi lỗi mất ổ đĩa hoặc xóa volume.
Không dùng docker compose --remove-orphans vì stack PostgreSQL hiện tại được giữ.

## Quan sát

Ready là message chờ giao; Unacked là đã giao nhưng chưa ack; Consumers là số
consumer đăng ký. Queue Ready=0 không chứng minh email thành công: còn Unacked,
retry hoặc DLQ. Phải đối chiếu ledger/SMTP và đúng event ID.

Nguồn: [RabbitMQ queues](https://www.rabbitmq.com/docs/queues),
[quorum queues](https://www.rabbitmq.com/docs/quorum-queues),
[Mailpit Docker](https://mailpit.axllent.org/docs/install/docker/).

[Trước](02_queue_and_pubsub.md) · [Tiếp: outbox](04_outbox_and_registration.md)
