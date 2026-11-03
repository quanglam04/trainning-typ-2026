# Tuần 4: Message Queue

Tuần này mình dùng **RabbitMQ** để gửi thông báo sau khi sinh viên đăng ký khóa học.

Ý tưởng: đăng ký khóa học vẫn lưu vào PostgreSQL như cũ. Việc tạo thông báo để consumer xử lý sau, nên API không phải chờ thông báo xong mới trả kết quả. Không dùng email vì muốn demo dễ hơn, không cần SMTP.

## Trong thư mục có gì?

- [Lý thuyết](lythuyet.md): các phần cần học của Week 4.
- [Hướng dẫn chạy và demo](demo/README.md): thao tác từng bước với Docker, Bruno, RabbitMQ.
- [Đề bài gốc](de-bai.md).
- [Source Java](CourseManagement_FinalProjectModule03): code phát triển từ Week 3.

## Chạy nhanh

Máy cần Docker Desktop. Mở terminal tại thư mục `CourseManagement_FinalProjectModule03`:

```powershell
Copy-Item .env.example .env
# Mở .env, sửa các giá trị cần thiết, nhất là DB_PASSWORD, JWT_SECRET.
# Nếu DB mới hoàn toàn: SEED_DEMO_DATA=true để có tài khoản và khóa học mẫu.
docker compose up --build -d
```

- Swagger: http://localhost:8080/swagger-ui/index.html
- RabbitMQ: http://localhost:15672 (`mq_demo` / mật khẩu trong `.env`)
- Bruno: mở folder `bruno`, chạy request trong `week4-mq`.
- Xem log: `docker compose logs -f app`

`SEED_DEMO_DATA=true` chỉ dùng để seed trên database **trống**. Database đã có dữ liệu thì giữ `false`.

## Flow đã làm

```text
POST /api/enrollments (STUDENT)
  -> lưu Enrollment + Outbox vào cùng transaction
  -> API trả 201
  -> OutboxPublisher gửi event lên RabbitMQ
  -> Consumer nhận event và tạo Notification
  -> GET /api/notifications thấy thông báo mới
```

Code chính:

- [EnrollmentServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/EnrollmentServiceImpl.java) – ghi enrollment và outbox.
- [OutboxPublisher.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/OutboxPublisher.java) – gửi event lên RabbitMQ.
- [RabbitMqConfig.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/RabbitMqConfig.java) – khai báo exchange, queue, DLQ.
- [EnrollmentNotificationConsumer.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/EnrollmentNotificationConsumer.java) – nhận message.
- [EnrollmentNotificationHandler.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/mq/EnrollmentNotificationHandler.java) – tạo thông báo, chống xử lý trùng.
- [docker-compose.yml](CourseManagement_FinalProjectModule03/docker-compose.yml) – PostgreSQL + RabbitMQ + App.

## Lưu ý

- Không có thêm API mới; mình dùng lại API đăng ký khóa học và thông báo.
- Nếu RabbitMQ đang tắt, đăng ký khóa học vẫn thành công; outbox giữ sự kiện để thử gửi lại.
- Consumer lỗi sẽ thử tối đa 3 lượt, sau đó message sang DLQ (không tự gửi lại từ DLQ).
- Demo mặc định chạy một instance app. Chưa xử lý khóa outbox cho nhiều instance cùng publish, chưa có dashboard quản lý DLQ.
- Phần bảo đảm ở đây là **at-least-once**. Message có thể tới nhiều lần, nhưng notification chỉ được tạo một lần theo `eventId`.

Mình giữ phần học đủ ý, còn project chạy thực tế cần Docker, PostgreSQL và RabbitMQ.
