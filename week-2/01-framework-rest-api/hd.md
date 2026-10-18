# Hướng dẫn thực hành — REST CRUD bằng Course Management + Bruno

## Mục tiêu

Dùng chính Course Management trong ZIP gốc để chạy API có JWT, CRUD khóa học, phân trang, validation và response lỗi thống nhất. Không cần chạy một mini project CRUD khác.

## 1. Chuẩn bị backend

Mở PowerShell trong thư mục:

`CourseManagement_FinalProjectModule03`

Kiểm tra Java và sao chép cấu hình mẫu:

```powershell
java -version
Copy-Item .env.example .env
```

`.env.example` có đủ giá trị demo local để chạy stack cơ bản. Không điền key/email/cloud credential thật nếu chưa cần chức năng gửi email hoặc upload file.

Khởi động backend và PostgreSQL:

```powershell
docker compose up --build -d
docker compose ps
```

Nếu cần xem log:

```powershell
docker compose logs -f app
```

Khi app đã khởi động, mở:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Nếu cổng `5432` hoặc `8080` đang được sử dụng, dừng dịch vụ chiếm cổng hoặc chỉnh cổng trong `docker-compose.yml` trước khi chạy.

## 2. Mở collection Bruno

1. Mở Bruno → **Open Collection** → chọn `CourseManagement_FinalProjectModule03/bruno/`, nơi có `opencollection.yml`.
2. Sao chép `bruno/environments/Local.example.yml` thành `bruno/environments/Local.yml`, rồi đổi dòng đầu thành `name: Local`.
3. Chọn environment **Local** trong Bruno. Mặc định `baseUrl=http://localhost:8080`, `username=admin`, `password=123456`.
4. Chạy request `auth/Login(1)`.
5. Trong JSON response, copy `data.accessToken` và dán vào biến `adminToken` trong environment Local. Nếu token hết hạn, đăng nhập lại. Không chia sẻ hoặc commit token thật.

`admin` / `123456` là tài khoản seed local của source project; chỉ dùng để demo trên môi trường phát triển.

## 3. Chạy luồng CRUD Week 2

Mở folder **Week 2 - Course CRUD**. Chạy lần lượt:

| Thứ tự | Request | Kết quả mong đợi |
|---:|---|---|
| 1 | `01 - List Courses (Pagination)` | `200`, response có danh sách và metadata phân trang |
| 2 | `02 - Create Course` | `201`, response chứa `data.courseId` |
| 3 | Chép `data.courseId` thành biến `courseId` | Các request tiếp theo dùng đúng ID mới |
| 4 | `03 - Get Course by ID` | `200`, đọc được khóa học vừa tạo |
| 5 | `04 - Update Course (PUT)` | `200`, thông tin cập nhật |
| 6 | `05 - Patch Course (partial update)` | `200`, chỉ trường được gửi được cập nhật |
| 7 | `06 - Validation Error (400)` | `400`, `error_code=INVALID_INPUT_DATA` |
| 8 | `07 - Missing Course (404)` | `404`, `error_code=RESOURCE_NOT_FOUND` |
| 9 | `08 - Delete Course` | `200`, khóa học được xóa mềm |

Sau request POST, copy trường `data.courseId` trong response vào biến `courseId` của environment Local. Nếu `teacherId=3` không trỏ tới tài khoản role `TEACHER` trong dữ liệu hiện tại, cập nhật biến `teacherId` theo ID giảng viên hợp lệ.

Chạy DELETE ở cuối, với ID của khóa học mới tạo. API từ chối xóa khóa học đã có enrollment; đây là quy tắc nghiệp vụ của source project.

## 4. Query, path và body cần chụp minh chứng

- Pagination: `GET /api/courses?page=0&size=5`
- Tìm kiếm: `GET /api/courses?page=0&size=5&search=java`
- Lọc theo teacher: `GET /api/courses?page=0&size=5&teacher_id=3`
- Đọc theo ID: `GET /api/courses/{course_id}`
- Tạo/cập nhật: request body JSON có `title`, `description`, `teacherId`, `price`, `durationHours` tùy method.
- PATCH: chỉ gửi trường cần đổi, ví dụ `{ "description": "Updated by PATCH" }`.

Lưu ý quy ước page bắt đầu từ `0`; `page=0` là trang đầu.

## 5. Kiểm tra lỗi và phân quyền

- Gửi POST body trong request `06 - Validation Error (400)` để thấy lỗi validation.
- Chạy GET `/api/courses/999999` trong request `07 - Missing Course (404)` để thấy lỗi không tìm thấy tài nguyên.
- Đăng nhập role STUDENT rồi thử POST/PUT/PATCH/DELETE Course; mong đợi `403 ACCESS_DENIED`.
- Xóa header/token hoặc dùng token hết hạn để kiểm tra `401`.

## 6. Quan sát HTTP code và ORM

Mở `CourseController.java`, xem `ResponseEntity.status(201)` cho POST và `ResponseEntity.ok(...)` cho GET/PUT/PATCH/DELETE. Mở `GlobalExceptionHandler.java` để xem lỗi được đổi thành status code và `ApiResponse`.

## 7. Chạy test tự động

Trong PowerShell tại root project:

```powershell
.\gradlew.bat test
```

Test suite dùng H2 trong `src/test/resources/application.properties`; không cần bật PostgreSQL riêng cho test suite. Tuy nhiên, luồng Bruno demo API thật dùng PostgreSQL qua Docker Compose.

## 8. Dừng môi trường

```powershell
docker compose down
```

Lệnh trên giữ lại volume database. Chỉ chạy `docker compose down -v` nếu muốn xóa dữ liệu PostgreSQL phát triển và tạo lại từ đầu.
