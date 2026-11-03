# Lý thuyết — ORM, JPA, Hibernate và PostgreSQL trong Course Management

## 1. Khái niệm

- **ORM (Object-Relational Mapping):** ánh xạ object Java sang bảng quan hệ.
- **JPA:** đặc tả Java về persistence; entity và repository sử dụng các khái niệm JPA.
- **Hibernate:** implementation ORM mà Spring Boot dùng để thực hiện mapping và sinh câu SQL.
- **Spring Data JPA:** cung cấp các repository như `JpaRepository`, giúp CRUD và phân trang mà không cần tự viết toàn bộ SQL.
- **PostgreSQL:** database quan hệ lưu dữ liệu qua các lần restart, không giống collection/list ở bộ nhớ.

Trong project, `build.gradle` khai báo `spring-boot-starter-data-jpa` và PostgreSQL Driver. File cấu hình thật là `src/main/resources/application.properties`.

## 2. Entity trong project

Một entity đại diện cho dữ liệu được quản lý trong database. Ví dụ `model/entity/Course.java`:

```java
@Entity
@Table(name = "courses")
public class Course { ... }
```

Một số annotation quan trọng trong entity Course:

| Annotation | Tác dụng |
|---|---|
| `@Entity` | Đánh dấu class được quản lý bởi JPA |
| `@Table(name = "courses")` | Chỉ định bảng database |
| `@Id` | Đánh dấu khóa chính |
| `@GeneratedValue` | Sinh khóa chính theo chiến lược đã cấu hình |
| `@Column` | Cấu hình cột |
| `@ManyToOne` + `@JoinColumn` | Khai báo quan hệ nhiều-một |
| `@Enumerated(EnumType.STRING)` | Lưu enum bằng tên dạng chuỗi |
| `@CreationTimestamp`, `@UpdateTimestamp` | Quản lý thời điểm tạo/cập nhật với Hibernate |

Course có `@ManyToOne` tới `User` qua cột `teacher_id`. Entity dùng `@SQLRestriction("is_deleted = false")`, nên truy vấn ORM thông thường loại các khóa học được đánh dấu đã xóa.

## 3. Quan hệ giữa các entity

| Entity | Quan hệ được khai báo | Diễn giải |
|---|---|---|
| `Course` | `ManyToOne` → `User` | Mỗi khóa học có một giảng viên phụ trách |
| `Lesson` | `ManyToOne` → `Course` | Bài học thuộc một khóa học |
| `Enrollment` | `ManyToOne` → `User` và `Course` | Một lượt đăng ký gắn với người học và khóa học |
| `Review` | `ManyToOne` → `User` và `Course` | Đánh giá gắn với người dùng và khóa học |
| `LessonProgress` | liên kết lesson/enrollment theo entity | Theo dõi trạng thái hoàn thành bài học |

Đây là quan hệ được khai báo trong các entity hiện có; không nên tự suy ra rằng luôn có mapping hai chiều `OneToMany` nếu trong class không thấy annotation đó.

## 4. Repository và CRUD

`model/repo/ICourseRepo.java`:

```java
public interface ICourseRepo
        extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByTitle(String title);
}
```

- `JpaRepository<Course, Integer>` cung cấp thao tác đọc/lưu/xóa và phân trang cơ bản với kiểu ID là `Integer`.
- `JpaSpecificationExecutor<Course>` cho phép chạy query động bằng `Specification`.
- `CourseSpecification.filterCourses(...)` tạo bộ lọc từ keyword, teacher ID, status và role người dùng.

Luồng tạo khóa học:

1. `CourseController#createCourse` nhận JSON và chạy validation với `@Valid`.
2. `CourseServiceImpl#createCourse` tìm user theo `teacherId`, kiểm tra role có phải `TEACHER`, tạo entity `Course` và gọi `courseRepo.save(...)`.
3. Hibernate sinh câu SQL tương ứng.
4. Controller trả `201 Created` với dữ liệu response trong `ApiResponse`.

## 5. Pagination từ API đến database

Trong `CourseController#getAllCourses`, tham số `Pageable` được Spring tạo từ query string. `CourseServiceImpl#getAllCourses` gọi:

```java
Page<Course> courses = courseRepo.findAll(spec, pageable);
return courses.map(courseMapper::toListResponse);
```

Như vậy việc phân trang không cần tự lấy toàn bộ bản ghi vào `ArrayList` rồi cắt danh sách; điều kiện lọc và thông tin phân trang được đưa xuống Spring Data JPA/Hibernate để truy vấn database.

Ví dụ: `GET /api/courses?page=0&size=5&search=java`. Trang bắt đầu từ `0`.

## 6. Mapper và DTO

- Request DTO: `CourseCreateRequest`, `CourseUpdateRequest`, `CourseUpdateStatusRequest`.
- Response DTO: `CourseListResponse`, `CourseDetailResponse`, `CourseResponse`.
- Mapper: `CourseMapper` dùng MapStruct để chuyển entity thành DTO.

Không trả entity JPA thẳng ra mọi API giúp kiểm soát hợp đồng JSON và tránh vô tình để lộ toàn bộ cấu trúc persistence.

**Điểm cần chú ý:** `CourseMapper` dùng `NullValuePropertyMappingStrategy.IGNORE`. Điều này khiến field null của DTO không ghi đè field hiện tại. Đây là hành vi đặc biệt cần hiểu khi so sánh PUT và PATCH; phần Week 2 đã bổ sung endpoint PATCH vào bản sao thực hành, không sửa ZIP gốc.

## 7. Transaction, validation và lỗi

`CourseServiceImpl` dùng `@Transactional` cho thao tác ghi dữ liệu. DTO có Bean Validation như `@NotBlank`, `@NotNull`, `@Min`; `GlobalExceptionHandler` tập trung xử lý `MethodArgumentNotValidException`, `BadRequestException`, `ResourceNotFoundException`, `ConflictException` và các lỗi khác.

Response lỗi thống nhất có thể gồm `success=false`, `status_code`, `error_code`, `message`, `errors`, `timestamp`. Ví dụ error code dùng trong project: `INVALID_INPUT_DATA`, `RESOURCE_NOT_FOUND`, `DUPLICATE_RESOURCE`, `ACCESS_DENIED`.

## 8. Cấu hình schema và migration

Trong `application.properties`, project hiện đặt `spring.jpa.hibernate.ddl-auto=update`. Hibernate có thể cập nhật schema theo entity khi chạy ứng dụng, phù hợp bài thực hành nhưng không thay thế quy trình migration được version hóa. Source hiện tại không khai báo Flyway/Liquibase; không nên khẳng định rằng project đang có migration.

## 9. Các file cần mở để tự trình bày

- `model/entity/Course.java` — entity và relationship.
- `model/repo/ICourseRepo.java` — repository và Specification executor.
- `model/repo/spec/CourseSpecification.java` — filter động.
- `model/service/impl/CourseServiceImpl.java` — nghiệp vụ và gọi repository.
- `controller/CourseController.java` — route CRUD/pagination.
- `model/mapper/CourseMapper.java` — mapping entity/DTO.
- `resources/application.properties` — kết nối PostgreSQL và cấu hình JPA.
