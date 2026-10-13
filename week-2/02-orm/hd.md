# Hướng dẫn thực hành — ORM thật bằng Course Management

## Mục tiêu

Chạy Spring Boot với PostgreSQL và quan sát cách API CRUD sử dụng Spring Data JPA/Hibernate. Đây là cùng project Course Management dùng ở phần REST API, không phải một project CRUD khác.

## 1. Khởi động project

Mở PowerShell tại `CourseManagement_FinalProjectModule03`:

```powershell
Copy-Item .env.example .env
docker compose up --build -d
docker compose ps
```

Nếu muốn xem log:

```powershell
docker compose logs -f app
```

Mở Swagger UI: `http://localhost:8080/swagger-ui/index.html`.

`.env.example` sử dụng giá trị demo local. JWT secret mẫu không được dùng cho production; thay bằng secret riêng nếu deploy. Mail/Cloudinary chỉ cần cấu hình khi muốn chạy chức năng phụ thuộc các dịch vụ đó.

## 2. Mở Bruno và lấy token

1. Mở Bruno → **Open Collection** → `CourseManagement_FinalProjectModule03/bruno/`.
2. Copy `environments/Local.example.yml` thành `environments/Local.yml` và đổi dòng đầu thành `name: Local`.
3. Chọn environment **Local**.
4. Chạy `auth/Login(1)` với username `admin`, password `123456`.
5. Copy `data.accessToken` từ response vào biến `adminToken` trong `Local.yml`.

Nếu các request trả `401`, đăng nhập lấy token mới. Nếu trả `403`, kiểm tra bạn đang dùng token có role phù hợp: CRUD khóa học yêu cầu `ADMIN`.

## 3. CRUD + pagination

Trong Bruno mở **Week 2 - Course CRUD**. Chạy GET list, POST, GET detail, PUT, PATCH, kiểm tra lỗi 400/404 rồi DELETE. Sau POST phải chép `data.courseId` sang biến `courseId`; biến `teacherId` cần trỏ đến user role `TEACHER`.

Các URL cần thử:

```text
GET    /api/courses?page=0&size=5
GET    /api/courses?page=0&size=5&search=java
GET    /api/courses?page=0&size=5&teacher_id=3
GET    /api/courses/{course_id}
POST   /api/courses
PUT    /api/courses/{course_id}
PATCH  /api/courses/{course_id}
DELETE /api/courses/{course_id}
```

`page=0` là trang đầu. DELETE chỉ thành công với khóa học chưa có enrollment; API dùng xóa mềm (đặt `is_deleted`) và trả `200 OK`.

## 4. Theo dõi ORM qua source code

Mở các file và theo dấu luồng xử lý:

1. `controller/CourseController.java` — nhận request và gọi service.
2. `model/service/impl/CourseServiceImpl.java` — kiểm tra `teacherId`, tạo/cập nhật `Course`, gọi repository.
3. `model/repo/ICourseRepo.java` — kế thừa `JpaRepository` và `JpaSpecificationExecutor`.
4. `model/repo/spec/CourseSpecification.java` — tạo điều kiện tìm kiếm/lọc.
5. `model/entity/Course.java` — `@Entity`, `@Table`, khóa chính, quan hệ với `User`.
6. `model/mapper/CourseMapper.java` — map entity sang response DTO.
7. `src/main/resources/application.properties` — URL PostgreSQL và cấu hình JPA/Hibernate.

Trong log của app có thể thấy SQL do Hibernate phát ra vì `spring.jpa.show-sql=true`.

## 5. Kiểm tra error response

- Request body không hợp lệ: HTTP `400`, `error_code=INVALID_INPUT_DATA`.
- ID không tồn tại: HTTP `404`, `error_code=RESOURCE_NOT_FOUND`.
- Token không hợp lệ/hết hạn: HTTP `401`.
- Token role STUDENT gọi POST/PUT/PATCH/DELETE: HTTP `403`, thường là `ACCESS_DENIED`.
- Xóa khóa học đã có sinh viên đăng ký: HTTP `409`, `DUPLICATE_RESOURCE` theo logic nghiệp vụ hiện có.

## 6. Kiểm tra test tự động

Trong thư mục project:

```powershell
.\gradlew.bat test
```

Bộ test dùng H2 trong-memory với cấu hình riêng ở `src/test/resources/application.properties`, nên không cần PostgreSQL đang chạy cho test suite. Trong phiên làm việc này, chỉ xác nhận chạy được sau khi lệnh hoàn tất trên máy/môi trường của bạn.

## 7. Dọn môi trường

```powershell
docker compose down
```

Lệnh trên không xóa dữ liệu volume. Chỉ sử dụng `docker compose down -v` khi bạn chủ động muốn reset database dev từ đầu.

## 8. Phần được bổ sung cho Week 2

ZIP gốc có PUT cập nhật course nhưng chưa có PATCH. Bản Week 2 thêm `@PatchMapping("/{course_id}")`, dùng lại `CourseUpdateRequest` và service hiện có; request PATCH rỗng bị trả `400`. Đây là thay đổi chỉ nằm trong file ZIP bài Week 2 đã tạo, không ghi đè ZIP gốc mà bạn gửi.
