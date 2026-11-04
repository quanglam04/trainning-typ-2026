# 01 — Message Queue và giao tiếp sync/async

## MQ giải quyết điều gì?

Message Queue là hạ tầng giữ và chuyển message giữa producer với consumer. Producer
không cần đợi consumer xử lý nghiệp vụ xong mới tiếp tục. Broker giúp đệm backlog,
phân phối việc và hỗ trợ delivery; không tự thay transaction/idempotency ở ứng dụng.

Ví dụ EruSentia: tạo tài khoản là logic chính; welcome email là tác vụ phụ. Gọi SMTP
ngay trong request làm response phải đợi kết nối/gửi thư. Nếu account đã commit nhưng
SMTP lỗi, trả 500 có thể khiến người dùng đăng ký lại và gặp email trùng. Không thể
rollback việc gửi thư chỉ bằng rollback DB.

## Sync và async

| Tiêu chí | Gọi SMTP trong request | Outbox + queue + worker |
|---|---|---|
| Response | Chờ thao tác SMTP | Chờ user + outbox commit |
| Broker/SMTP lỗi | Có thể ảnh hưởng request | Backlog chờ; DB vẫn phải hoạt động |
| Kết quả email | Có thể biết SMTP đã nhận trong request | Đến sau, phải quan sát trạng thái |
| Vận hành | Ít thành phần | Thêm broker/worker/retry/monitoring |
| Tính nhất quán | Vẫn có dual-write DB/SMTP | Eventual consistency, cần idempotency |

Async không đồng nghĩa mọi bước đều không chờ: producer vẫn chờ broker confirm;
consumer vẫn chờ SMTP/DB trước ack. Khác biệt là client HTTP không đợi email.
Request 201 chỉ chứng minh tài khoản/sự kiện đã commit, không chứng minh email đến inbox.

## Khi nên và không nên dùng

MQ phù hợp tác vụ nền, burst cần đệm, nhiều consumer độc lập hoặc retry không nên
chặn request. Gọi trực tiếp hợp lý khi client cần ngay kết quả để ra quyết định,
logic đơn giản/tải thấp hoặc chi phí vận hành MQ chưa xứng đáng.

Welcome email ở bài là use case thực hành phù hợp với đăng ký hiện có. Với một
email nhẹ, MQ chưa chắc nhanh/rẻ hơn tổng thể; lợi ích được minh họa là tách lỗi và
retry. Không có số đo tải để kết luận cải thiện hiệu năng production.

## Cách áp dụng

API PostgreSQL ghi user + outbox trong cùng transaction; worker chuyển event qua
RabbitMQ và gửi vào Mailpit local. QUEUE_ENABLED=false mặc định giữ flow tuần 3.
App vẫn chưa gọi API và không cần thay stack để thực hành.

Nguồn: [RabbitMQ tutorials](https://www.rabbitmq.com/tutorials),
[consumer/publisher confirms](https://www.rabbitmq.com/docs/confirms).

[Tiếp: các mô hình](02_queue_and_pubsub.md)
