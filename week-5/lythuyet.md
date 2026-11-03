# Tuần 5: Caching & Redis - Ghi chép

> Đây là phần mình tự tổng hợp để học. Đề bài gốc vẫn ở [README.md](README.md).

## 1. Caching là gì?

Cache là chỗ lưu tạm dữ liệu hay được đọc để đỡ phải truy vấn database nhiều lần. Ví dụ 100 người cùng xem một khóa học thì không nhất thiết cả 100 request đều phải đọc PostgreSQL.

- **Application cache:** lưu trong RAM của một app. Dễ dùng nhưng restart là mất, chạy nhiều instance thì mỗi instance có cache riêng.
- **Distributed cache:** dùng Redis riêng, nhiều instance có thể cùng đọc một cache.
- **Nên cache:** dữ liệu hay đọc, ít thay đổi, ví dụ thông tin khóa học đã xuất bản.
- **Không nên cache tùy tiện:** mật khẩu, access token, dữ liệu cá nhân nhạy cảm, số dư hoặc dữ liệu cần đúng ngay lập tức.

## 2. Hai chiến lược

**Cache-Aside (bài này dùng):**

1. Client gọi API, app tìm dữ liệu trong Redis.
2. Nếu có (**hit**) thì trả luôn.
3. Nếu chưa có (**miss**) thì đọc PostgreSQL, sau đó lưu vào Redis với thời gian hết hạn.

**Write-Through:** Khi ghi dữ liệu, app ghi DB và cập nhật cache theo cùng luồng xử lý. Điểm khó là hai nơi có thể ghi lỗi ở những thời điểm khác nhau nên phải thiết kế đồng bộ cẩn thận.

Với Course Management, mình chọn Cache-Aside vì phần xem chi tiết được gọi nhiều hơn phần sửa.

## 3. Redis cơ bản

Redis lưu dữ liệu theo key-value, thường lưu trên RAM nên thao tác nhanh.

| Kiểu | Ví dụ dùng |
| --- | --- |
| String | JSON chi tiết khóa học |
| Hash | Các trường của một user |
| List | Hàng đợi việc đơn giản |
| Set | Danh sách user đã thích bài |
| Sorted Set | Bảng xếp hạng theo điểm |

Một số lệnh cần nhớ:

```redis
SET name "java"
GET name
EXPIRE name 120
TTL name
DEL name
```

`TTL` trả về số giây còn lại; `-1` là key không có hạn, `-2` là key không tồn tại.

## 4. Đã làm trong code

Mình cache `GET /api/courses/{course_id}`, nhưng **chỉ cache khóa học PUBLISHED**. Khóa học DRAFT vẫn đọc DB và kiểm tra quyền như Week 3.

- [CourseController.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/controller/CourseController.java): gọi cache trước, nếu miss mới gọi service. Trả thêm header `X-Cache` và `X-Elapsed-Ms` để dễ demo.
- [CourseDetailCache.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/cache/CourseDetailCache.java): dùng `StringRedisTemplate` để GET / SET / DEL JSON, TTL 120-150 giây.
- [CourseCacheInvalidator.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/cache/CourseCacheInvalidator.java): xóa key sau khi transaction cập nhật DB thành công.
- [CourseServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/CourseServiceImpl.java): phát sự kiện khi sửa/xóa khóa học.
- [LessonServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/LessonServiceImpl.java) và [ReviewServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/ReviewServiceImpl.java): khi nội dung bài học hoặc đánh giá thay đổi thì cache chi tiết cũng bị xóa.
- [docker-compose.yml](CourseManagement_FinalProjectModule03/docker-compose.yml): chạy Redis cùng PostgreSQL, RabbitMQ và ứng dụng.

Ví dụ: lần đầu gọi `GET /api/courses/1` → `X-Cache: MISS`; lần kế tiếp → `X-Cache: HIT`; gọi `?useCache=false` → `X-Cache: BYPASS` để so sánh với đọc DB trực tiếp.

## 5. Vấn đề dễ gặp

**TTL:** đặt quá ngắn thì hay miss, quá dài có thể đọc dữ liệu cũ. Bài này chọn 120-150 giây để dễ thử.

**Cache invalidation:** khi sửa khóa học, bài học hoặc review thì phải xóa cache cũ. Bài này chỉ xóa sau khi transaction commit. Nếu sửa DB trực tiếp bằng SQL thì event không chạy, phải chờ TTL hoặc tự `DEL`.

**Cache avalanche:** nhiều key hết hạn cùng lúc làm DB tăng tải đột ngột. Bài này cộng ngẫu nhiên 0-30 giây vào TTL để giảm khả năng trùng thời điểm hết hạn.

**Cache penetration:** liên tục hỏi ID không tồn tại làm Redis luôn miss và DB luôn bị truy vấn. Có thể xử lý bằng negative cache ngắn, giới hạn request hoặc Bloom Filter. Bài demo **chưa làm negative cache**, vì cần phân biệt ID không tồn tại với khóa học nháp mà người dùng không có quyền xem.

**Redis bị tắt:** app thử đọc Redis không được thì quay về DB. Có nghĩa hệ thống vẫn lấy được khóa học nếu PostgreSQL còn hoạt động, nhưng chậm hơn.

## 6. Kết luận ngắn

Redis giúp giảm số lần query DB với dữ liệu đọc lặp lại. Nhưng cache không phải nguồn dữ liệu chính: PostgreSQL mới là nơi lưu thật. Khi dùng cache phải nhớ 3 thứ: key, TTL và xóa cache lúc dữ liệu thay đổi.

Xem [hướng dẫn demo](demo/README.md) để thực hành và so thời gian.
