# Tuần 4: Message Queue

## 1. Message Queue là gì?

**Message Queue (MQ)** là nơi nhận và giữ message để bên khác lấy ra xử lý. Bên gửi gọi là **producer**, bên nhận gọi là **consumer**.

Ví dụ: sinh viên đăng ký khóa học. Nếu API vừa lưu đăng ký, vừa gửi email, vừa tạo thông báo thì phải chờ nhiều việc. Một việc lỗi có thể làm request chậm hoặc thất bại. Dùng MQ thì API xử lý phần chính trước, việc phụ làm sau.

**Đồng bộ (sync):** gọi xong phải đợi kết quả. Hợp với việc cần biết kết quả ngay, ví dụ kiểm tra khóa học có tồn tại không.

**Bất đồng bộ (async):** gửi yêu cầu rồi làm việc tiếp, không phải đợi bên nhận xử lý xong. Hợp với email, thông báo, ghi log.

MQ không có nghĩa là xử lý nhanh hơn trong mọi trường hợp. Nó giúp tách việc phụ khỏi thời gian phản hồi của API.

## 2. Queue và Publish/Subscribe

- **Point-to-Point (Queue):** nhiều consumer có thể cùng đọc một queue, nhưng mỗi message chỉ được giao cho một consumer xử lý tại một thời điểm. Dùng khi muốn chia tải công việc.
- **Publish/Subscribe:** một sự kiện được gửi đến nhiều nhóm consumer qua các queue khác nhau. Ví dụ đăng ký khóa học thì một nhóm gửi email, một nhóm cập nhật thống kê.

Trong bài mình dùng **1 queue để tạo thông báo**. Nếu sau này muốn gửi email riêng thì bind thêm queue vào exchange, không phải sửa luồng đăng ký.

## 3. RabbitMQ hoạt động thế nào?

Các khái niệm chính:

- **Producer:** bên gửi message. Trong bài là [OutboxPublisher.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/OutboxPublisher.java).
- **Exchange:** nơi nhận message, quyết định chuyển đến queue nào.
- **Routing key:** khóa để exchange chọn đường đi cho message.
- **Queue:** giữ message đợi consumer xử lý.
- **Consumer:** bên lấy message để xử lý, ở [EnrollmentNotificationConsumer.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/EnrollmentNotificationConsumer.java).
- **Binding:** nối exchange với queue theo routing key.

Cấu hình ở [RabbitMqConfig.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/RabbitMqConfig.java). Trong bài có:

```text
exchange: course.events
routing key: enrollment.created
queue: course.enrollment.notifications
consumer: tạo bản ghi Notification
```

RabbitMQ chạy bằng Docker, không cần cài trực tiếp vào máy. Xem [docker-compose.yml](CourseManagement_FinalProjectModule03/docker-compose.yml).

## 4. Phần mình làm trong project

Khi STUDENT gọi `POST /api/enrollments`:

1. [EnrollmentServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/EnrollmentServiceImpl.java) kiểm tra và lưu đăng ký vào PostgreSQL.
2. Cùng transaction đó, lưu một dòng vào bảng `mq_outbox_events`.
3. API trả `201`, không đợi consumer.
4. [OutboxPublisher.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/OutboxPublisher.java) định kỳ lấy các event chưa gửi, đưa vào RabbitMQ.
5. [EnrollmentNotificationHandler.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/EnrollmentNotificationHandler.java) tạo notification cho sinh viên.
6. Sinh viên gọi `GET /api/notifications` để xem kết quả.

**Vì sao thêm Outbox?** Nếu lưu đăng ký thành công mà RabbitMQ đang tắt, gọi publish trực tiếp có thể mất message. Outbox lưu việc cần gửi vào DB trước, RabbitMQ lên lại thì gửi tiếp.

Tham khảo thêm [OutboxEvent.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/OutboxEvent.java) và [EnrollmentCreatedEvent.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/EnrollmentCreatedEvent.java).

## 5. Những lỗi cần chú ý

**Message bị trùng:** broker có thể gửi lại nếu consumer đã xử lý nhưng chưa ACK kịp. Mỗi event có một `eventId`. Bảng `mq_processed_events` ghi các ID đã xử lý; xử lý lại cùng ID thì không tạo thêm thông báo. Đây là **idempotency**.

**ACK:** khi xử lý thành công, consumer xác nhận để RabbitMQ bỏ message khỏi queue. Nếu xử lý lỗi thì không ACK thành công.

**Consumer lỗi:** bài cấu hình thử lại 3 lần, vẫn lỗi thì chuyển message sang **Dead Letter Queue (DLQ)** để xem và xử lý lại bằng tay.

**Message bị mất:** queue durable, message persistent và publisher confirm giúp giảm nguy cơ mất ở RabbitMQ. Outbox giúp tránh mất sự kiện khi broker tạm ngắt. Vẫn cần backup và giám sát trong hệ thống thực tế.

**Thứ tự:** với một queue và một consumer thì thường giữ được thứ tự nhận, nhưng khi retry, chạy nhiều consumer hoặc nhiều queue thì không nên mặc định tin vào thứ tự tuyệt đối.

**Điểm cần nhớ:** MQ thường theo kiểu *at-least-once* (có thể nhận lại), nên consumer phải xử lý trùng an toàn. Không nên hiểu là RabbitMQ luôn đảm bảo đúng một lần trong mọi tình huống.

## Tóm tắt

MQ giúp làm việc phụ bất đồng bộ, API phản hồi sớm hơn. RabbitMQ có exchange, routing key, queue và consumer. Khi triển khai cần hiểu ACK, retry, DLQ, idempotency và outbox.
