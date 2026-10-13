# Lý thuyết — Spring Boot và RESTful API (dùng Course Management)

## 1. Framework được chọn

Tuần 2 thống nhất sử dụng **Java + Spring Boot** và lấy dự án Course Management sẵn có làm ví dụ chính. Spring Boot giúp xây dựng ứng dụng web, map HTTP request vào controller, tích hợp validation, security, JSON và tầng truy cập database.

## 2. Theo dấu một request trong project

```text
Bruno / Swagger UI
        │ HTTP Request + Bearer token
        ▼
SecurityConfig + JwtAuthenticationFilter
        ▼
CourseController        nhận URL, query/path/body, kiểm tra role
        ▼
CourseServiceImpl       xử lý quy tắc nghiệp vụ
        ▼
ICourseRepo             Spring Data JPA repository
        ▼
Course entity           Hibernate ánh xạ entity ↔ bảng courses
        ▼
PostgreSQL
```

| Thành phần | File/vị trí trong dự án | Trách nhiệm |
|---|---|---|
| Controller | `src/main/java/.../controller/CourseController.java` | Map URL, HTTP method, input và response |
| Service | `src/main/java/.../model/service/impl/CourseServiceImpl.java` | Xử lý nghiệp vụ khóa học |
| Repository | `src/main/java/.../model/repo/ICourseRepo.java` | Đọc/ghi dữ liệu bằng Spring Data JPA |
| Entity | `src/main/java/.../model/entity/Course.java` | Ánh xạ lớp Java vào bảng `courses` |
| Request DTO | `src/main/java/.../model/dto/req/CourseCreateRequest.java` và `CourseUpdateRequest.java` | Định nghĩa body client gửi lên |
| Response wrapper | `src/main/java/.../model/dto/wrapper/ApiResponse.java` | Chuẩn hóa JSON response |
| Error handler | `src/main/java/.../exception/GlobalExceptionHandler.java` | Quy đổi exception thành HTTP status và JSON lỗi |

## 3. HTTP methods áp dụng vào Course API

| Method | Endpoint trong bản Week 2 | Ý nghĩa |
|---|---|---|
| `GET` | `/api/courses` | Đọc danh sách; hỗ trợ phân trang và bộ lọc |
| `GET` | `/api/courses/{course_id}` | Đọc chi tiết một khóa học |
| `POST` | `/api/courses` | Tạo khóa học; controller trả `201 Created` |
| `PUT` | `/api/courses/{course_id}` | Cập nhật thông tin khóa học |
| `PATCH` | `/api/courses/{course_id}` | Cập nhật một phần; route này được bổ sung trong bản Week 2 |
| `DELETE` | `/api/courses/{course_id}` | Xóa mềm khóa học; controller hiện trả `200 OK` |

**Phân biệt về thiết kế:** theo ngữ nghĩa REST, PUT thường được dùng để thay thế representation của tài nguyên, còn PATCH cập nhật một phần. Trong source gốc, `CourseMapper` dùng `NullValuePropertyMappingStrategy.IGNORE`, nên các trường null bị bỏ qua khi map `CourseUpdateRequest`. Vì vậy PUT hiện tại cũng có hành vi gần với cập nhật một phần. Bản Week 2 giữ cách xử lý PUT hiện hữu và bổ sung PATCH dùng cùng DTO/service để thực hành; nếu đưa vào sản phẩm thực, nên thống nhất rõ hợp đồng PUT/PATCH.

## 4. Path variable, query parameter và JSON body

- **Path variable:** `/api/courses/12` — xác định tài nguyên bằng ID. Controller dùng `@PathVariable("course_id")`.
- **Query parameter:** `/api/courses?page=0&size=5&search=java` — dùng để phân trang và tìm kiếm. Controller nhận `Pageable`, `search`, `teacher_id` và `status`.
- **JSON body:** POST/PUT/PATCH gửi dữ liệu có cấu trúc qua `@RequestBody`; `@Valid` kết hợp Bean Validation kiểm tra các trường có annotation validation.

Ví dụ body tạo khóa học:

```json
{
  "title": "Spring Boot căn bản",
  "description": "REST API và ORM",
  "teacherId": 3,
  "price": 0,
  "durationHours": 2
}
```

`teacherId` phải trỏ đến một user có role `TEACHER`. Người gửi POST/PUT/PATCH/DELETE phải đăng nhập và có role `ADMIN`.

## 5. HTTP status và cấu trúc response

| Status | Trường hợp trong project |
|---|---|
| `200 OK` | GET, PUT, PATCH và DELETE thành công theo controller hiện tại |
| `201 Created` | Tạo khóa học thành công |
| `400 Bad Request` | Validation hoặc dữ liệu đầu vào không hợp lệ |
| `401 Unauthorized` | Thiếu/sai/hết hạn access token hoặc xác thực thất bại |
| `403 Forbidden` | Đã xác thực nhưng không đủ quyền |
| `404 Not Found` | Không tìm thấy khóa học hoặc tài nguyên |
| `409 Conflict` | Ví dụ khóa học đã có enrollment nên không thể xóa |
| `500 Internal Server Error` | Lỗi server ngoài dự kiến |

Response thành công dùng `ApiResponse<T>`, ví dụ các trường có thể gồm `success`, `status_code`, `message`, `data`, `timestamp`. Response lỗi được `GlobalExceptionHandler` chuẩn hóa, có thể gồm `success`, `status_code`, `error_code`, `message`, `errors`, `timestamp`. Trường null có thể bị ẩn khỏi JSON.

Ví dụ response lỗi validation dự kiến:

```json
{
  "success": false,
  "status_code": 400,
  "error_code": "INVALID_INPUT_DATA",
  "message": "Dữ liệu đầu vào không hợp lệ",
  "errors": [
    {
      "field": "title",
      "message": "Tiêu đề khóa học không được để trống"
    }
  ],
  "timestamp": "2026-10-13T08:00:00"
}
```

Chi tiết thông báo và timestamp cụ thể có thể khác tùy request.

## 6. Pagination

Course API nhận `Pageable`, vì vậy Spring Data mặc định sử dụng chỉ số trang bắt đầu từ **0**:

```text
GET /api/courses?page=0&size=5
GET /api/courses?page=1&size=5
GET /api/courses?page=0&size=5&search=java
GET /api/courses?page=0&size=5&teacher_id=3&status=PUBLISHED
```

`page` là số trang, `size` là số phần tử tối đa mỗi trang. API trả một `Page<CourseListResponse>` bên trong trường `data`; cấu trúc trang có metadata như nội dung, tổng phần tử, tổng trang và số trang hiện tại tùy cách serialize.

Lưu ý: `search`, `teacher_id` và `status` được khai báo trong `CourseController`. Không tự đổi tên query parameter thành `teacherId` nếu controller hiện tại đang dùng `teacher_id`.

## 7. Bảo mật endpoint

- Endpoint Course yêu cầu người dùng đã xác thực.
- Tạo, cập nhật, cập nhật một phần và xóa khóa học có `@PreAuthorize("hasRole('ADMIN')")`.
- JWT được gửi bằng header `Authorization: Bearer <accessToken>`.
- `SecurityConfig` cho phép HTTP method PATCH trong cấu hình CORS; bản Week 2 thêm route PATCH vào controller.

## 8. Thực hành cần hoàn thành

Dùng Bruno để chứng minh GET danh sách + pagination, GET theo ID, POST, PUT, PATCH, DELETE, lỗi 400 và lỗi 404. Xem bảng luồng trong `hd.md` và chạy folder **Week 2 - Course CRUD** trong collection Bruno.
