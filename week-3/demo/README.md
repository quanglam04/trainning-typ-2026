# Demo Week 3 bằng Bruno

Mục tiêu: có đủ màn hình **register → activate → login → protected → RBAC → refresh → logout**. Chụp hoặc quay lại lúc chạy thật, không dùng ảnh giả.

## Chuẩn bị

1. Mở Bruno, chọn [collection](../CourseManagement_FinalProjectModule03/bruno/opencollection.yml) trong thư mục `bruno` và folder **Week 3 - Auth & RBAC**.
2. Copy [Local.example.yml](../CourseManagement_FinalProjectModule03/bruno/environments/Local.example.yml) thành `Local.yml` rồi đổi `name: Local`. Chọn environment Local trong Bruno. Không commit `Local.yml` có token thật.
3. Mở backend ở `http://localhost:8080` (hoặc chỉnh `baseUrl`). Database cần có tài khoản demo `admin` và `student_pro`, mật khẩu mẫu `123456`. Nếu dùng DB mới trống, bật `SEED_DEMO_DATA=true` đúng lần đầu.
4. Để demo tài khoản tự đăng ký, phải cấu hình SMTP thực để nhận OTP; nếu chưa có Gmail SMTP thì có thể demo phần protected/RBAC/refresh bằng tài khoản được seed, nhưng phần register→activate chưa hoàn tất.

## Thứ tự chạy và kết quả cần chụp

| Request trong Bruno | Kết quả cần kiểm tra |
| --- | --- |
| 01 Register | 201, yêu cầu nhận OTP qua email |
| 02 Activate | Nhập OTP thật trong biến `otp`, nhận 200 |
| 03 Login ADMIN | 200, nhận `accessToken`, `refreshToken` |
| 04 Login STUDENT | 200, nhận token STUDENT |
| 05 Protected without token | 401 |
| 06 Protected with access | 200, trả thông tin ADMIN |
| 07 STUDENT on /users | 403 |
| 08 ADMIN on /users | 200 |
| 09 Refresh token | 200, trả Access Token mới |
| 10 ACCESS cannot refresh | 401, từ chối Access Token |
| 11 REFRESH cannot access API | 401 |
| 12 Logout both tokens | 200, thu hồi Access và Refresh Token |
| 13 Access after logout | 401 |
| 14 Refresh after logout | 401 |

**Quan trọng với biến Bruno:** Sau `03 Login ADMIN`, copy `data.accessToken` vào `adminToken`, `data.refreshToken` vào `refreshToken` (đều của ADMIN). Sau `04 Login STUDENT`, copy Access Token của STUDENT vào `studentToken`, **đừng ghi đè refreshToken của ADMIN**. Khi hết hạn 5 phút, login lại để tạo Access Token mới. Dùng `09` lấy Access Token mới cũng được, nhưng nếu thay `adminToken` thì phải logout bằng token mới đó ở bước 12.

Các request có sẵn trong [week3-auth](../CourseManagement_FinalProjectModule03/bruno/week3-auth/) dùng các biến này. Nhớ demo API login thành công trước khi chạy các request cần token.

## Code dùng trong lúc thuyết trình

- [AuthController.java](../CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/controller/AuthController.java): các đường dẫn API.
- [JwtProvider.java](../CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/jwt/JwtProvider.java): phân biệt Access và Refresh.
- [JwtAuthenticationFilter.java](../CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/jwt/JwtAuthenticationFilter.java): chỗ chặn token sai.
- [SecurityConfig.java](../CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/SecurityConfig.java): BCrypt và stateless.
- [AuthServiceImpl.java](../CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/AuthServiceImpl.java): login/refresh/logout.

## Chạy test

Vào thư mục project, chạy `./gradlew test` hoặc `.\gradlew.bat test` trên Windows. Test cho Week 3 nằm ở [Week3AuthenticationTest.java](../CourseManagement_FinalProjectModule03/src/test/java/com/example/coursemanagement_finalprojectmodule03/Week3AuthenticationTest.java).

Test tự động dùng H2 và bật seed riêng. Không cần PostgreSQL hay SMTP thật cho các test refresh/logout. **Chưa ghi kết quả pass hay thêm ảnh** khi chưa chạy thực tế.
