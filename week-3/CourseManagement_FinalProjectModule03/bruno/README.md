## Week 3 - Authentication & Authorization

Folder `week3-auth/` có 14 request cho luồng demo Week 3. Xem [hướng dẫn demo](../../demo/README.md) để biết thứ tự, token và kết quả cần chụp. Logout ở Week 3 phải gửi thêm `refreshToken` trong JSON body để thu hồi cả hai token.

---

# Bruno — Course Management API

Đây là collection Bruno lấy từ thư mục `test_api/` trong ZIP Course Management bạn gửi. Các file request được giữ theo cấu trúc API gốc; URL, token và thông tin cá nhân nhúng cứng đã được chuyển sang biến/mẫu để tránh dùng token hết hạn hoặc lộ thông tin. Collection dùng OpenCollection YAML, mở trực tiếp bằng Bruno.

## Mở collection

1. Mở Bruno → **Open Collection** và chọn thư mục `bruno/` (nơi chứa `opencollection.yml`).
2. Sao chép `environments/Local.example.yml` thành `environments/Local.yml`, rồi đổi dòng đầu thành `name: Local`.
3. Chọn environment **Local** trong Bruno.
4. Chạy `auth/Login(1)` với `username=admin`, `password=123456` (tài khoản seed chỉ dành cho local demo). Sao chép trường `data.accessToken` trong response vào biến `adminToken` của environment. Không commit `Local.yml` hoặc token thật.
5. Với các request yêu cầu role khác, đăng nhập role tương ứng rồi cập nhật `studentToken` hoặc `teacherToken`.

## Luồng trình diễn Week 2

Mở folder **Week 2 - Course CRUD** và chạy theo thứ tự:

1. `01 - List Courses (Pagination)` — GET danh sách, `page` mặc định 0 và `size` mặc định 5.
2. `02 - Create Course` — POST tạo khóa học mới.
3. Copy `data.courseId` từ response POST vào biến `courseId` của environment.
4. `03 - Get Course by ID` — GET khóa học vừa tạo.
5. `04 - Update Course (PUT)` — cập nhật thông tin khóa học.
6. `05 - Patch Course (partial update)` — chỉ gửi trường cần đổi.
7. `06 - Validation Error (400)` và `07 - Missing Course (404)` — chứng minh cấu trúc lỗi.
8. `08 - Delete Course` — chạy cuối cùng; dùng ID của khóa học vừa tạo, không phải khóa học đang có enrollment.

## Biến môi trường cần kiểm tra

`baseUrl`, `username`, `password`, `adminToken`, `studentToken`, `teacherToken`, `teacherId`, `courseId`, `page`, `size`, `keyword`.

- `baseUrl` mặc định là `http://localhost:8080`.
- `teacherId=3` và `courseId` placeholder chỉ là giá trị bắt đầu tham khảo; cập nhật theo dữ liệu trả về trong môi trường của bạn.
- Token là ngắn hạn. Nếu request trả 401, đăng nhập lại và thay token.
- Một số request nguyên bản về OTP/email, enrollment, review và lesson phụ thuộc dữ liệu hoặc trạng thái trước đó, không phải tất cả đều chạy tuần tự từ database trống.

## Ghi chú về PATCH

Source gốc có GET/POST/PUT/DELETE cho Course nhưng chưa có `PATCH /api/courses/{course_id}`. Bản Week 2 này bổ sung route PATCH và kiểm tra request rỗng bị từ chối; source ZIP gốc bạn gửi không bị thay đổi. PATCH dùng `CourseUpdateRequest` và MapStruct đang bỏ qua trường null. Trong source gốc, PUT cũng dùng cấu hình bỏ qua null, vì vậy hiện tại PUT có thể hoạt động như cập nhật từng phần; phần lý thuyết đã ghi rõ để bạn không nhầm giữa ý nghĩa REST và hành vi code đang có.
