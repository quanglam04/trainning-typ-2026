# Demo Week 4 – RabbitMQ

## 1. Chuẩn bị

Cần Docker Desktop đang chạy, JDK 21 nếu muốn chạy Java trực tiếp, và Bruno để gọi API.

Mở terminal tại thư mục `CourseManagement_FinalProjectModule03`:

```powershell
Copy-Item .env.example .env
```

Mở `.env` và chỉnh `DB_PASSWORD`, `JWT_SECRET`. Đặt `SEED_DEMO_DATA=true` **chỉ nếu dùng database trống** để có `student_new` và khóa học mẫu. Không bật seed để làm lại dữ liệu trên DB thật.

```powershell
docker compose up --build -d
docker compose ps
```

Chạy 3 service: `postgres`, `rabbitmq`, `app`. Mở http://localhost:15672, đăng nhập bằng `RABBITMQ_USER` và `RABBITMQ_PASSWORD` trong `.env` (mặc định `mq_demo` / `mq_demo_password`).

## 2. Gọi API bằng Bruno

Mở collection trong thư mục `bruno`. Tạo environment `Local.yml` từ `Local.example.yml` (không commit token thật). Trong Bruno chọn folder `week4-mq`:

1. `01-login-student`: đăng nhập `student_new / 123456`. Copy `data.accessToken` vào biến `studentToken`. Token hết hạn thì đăng nhập lại.
2. `02-list-courses`: xem danh sách khóa học, chọn khóa `PUBLISHED` mà sinh viên này **chưa đăng ký**. Copy `courseId` vào biến `mqCourseId`.
3. `03-enroll-course`: POST đăng ký khóa học. Kết quả mong đợi là `201` với `data.enrollmentId`.
4. `04-my-notifications`: GET danh sách thông báo. Có thể phải gọi lại sau vài giây vì consumer xử lý bất đồng bộ. Tìm thông báo `type = ENROLLMENT`.
5. `05-my-enrollments`: kiểm tra đăng ký đã lưu.
6. `06-duplicate-enrollment`: gửi lại cùng khóa học, mong đợi `409`, không có thêm thông báo mới.

**Lưu ý:** nếu trước đó `student_new` đã đăng ký mọi khóa `PUBLISHED` thì hãy tạo khóa mới bằng tài khoản ADMIN hoặc dùng một STUDENT mới. Không xóa database chỉ để demo.

## 3. Xem RabbitMQ và log

Mở RabbitMQ Management → **Queues and Streams**. Có hai queue:

- `course.enrollment.notifications` – queue nhận message.
- `course.enrollment.notifications.dlq` – queue chứa message lỗi.

Queue chính có thể hiện `Ready = 0` vì consumer đã lấy và ACK rất nhanh. Đây là bình thường, không phải message chưa được gửi.

Mở terminal:

```powershell
docker compose logs -f app
```

Tìm `MQ published eventId=...` và `MQ processed eventId=...`.

Có thể xem outbox trong PostgreSQL:

```powershell
docker compose exec postgres psql -U postgres -d final_project_03 -c "select event_id, sent_at from mq_outbox_events order by created_at desc limit 5;"
```

Nếu sửa `POSTGRES_USER` hoặc `POSTGRES_DB` trong `.env`, thay `postgres`/`final_project_03` trong câu lệnh.

## 4. Thử RabbitMQ bị tắt

Cách này để hiểu vì sao cần outbox:

1. Giữ `app` chạy. Chạy `docker compose stop rabbitmq`.
2. Đăng ký **một khóa học khác** bằng Bruno; API vẫn trả `201` vì chỉ ghi vào PostgreSQL.
3. Mở bảng `mq_outbox_events`: event mới sẽ có `sent_at = null`.
4. Chạy `docker compose start rabbitmq`; chờ RabbitMQ lên và ứng dụng gửi lại event.
5. Gọi `GET /api/notifications`, kiểm tra thông báo mới; `sent_at` được cập nhật.

Trường hợp RabbitMQ tắt không ảnh hưởng đăng ký chính, nhưng thông báo sẽ đến muộn. Đó là tính chất bất đồng bộ.

## 5. Kiểm tra DLQ (tùy chọn)

Trong RabbitMQ Management, vào **Exchanges → course.events → Publish message**, nhập:

- Routing key: `enrollment.created`
- Content type: `application/json`
- Payload:

```json
{"eventId":"test-invalid-student-001","studentId":99999999,"courseId":1,"courseTitle":"Demo loi"}
```

Consumer không tìm thấy student → thử lại tối đa 3 lượt → message được đẩy sang `course.enrollment.notifications.dlq`. Đây chỉ là dữ liệu test, không phải lỗi của luồng đăng ký thật.

## 6. Chuẩn bị ảnh/quay nộp

Chụp: API `201` → log producer/consumer → trang RabbitMQ hai queue → API `GET /api/notifications` có thông báo. Nếu muốn thêm, chụp bảng outbox và thử tình huống RabbitMQ bị tắt.

Xong thì dừng bằng `docker compose down` (không thêm `-v` nếu muốn giữ dữ liệu).

Các bài test tự động ở `src/test`. Có thể chạy `./gradlew test` hoặc `gradlew.bat test` trên Windows. Test không cần RabbitMQ thật; demo thực tế thì cần.
