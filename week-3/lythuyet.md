# Tuần 3: Authentication & Authorization

## 1. Authentication và Authorization

- **Authentication (xác thực):** kiểm tra người đang đăng nhập là ai. Ví dụ nhập username, password đúng mới được login.
- **Authorization (phân quyền):** kiểm tra người đã đăng nhập được làm gì. Ví dụ STUDENT không được quản lý tài khoản, ADMIN thì được.
- Điểm khác nhau: xác thực là **bạn là ai**, phân quyền là **bạn được làm gì**.

Trong code: [login()](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/AuthServiceImpl.java) xử lý đăng nhập; [UserController.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/controller/UserController.java) có `@PreAuthorize` để kiểm tra role.

## 2. Session-based và Token-based

**Session-based:**
1. Login thành công, server tạo session và lưu thông tin session.
2. Trình duyệt giữ cookie có session ID.
3. Những lần gọi sau trình duyệt gửi cookie, server tra session để biết người dùng.

**Token-based:**
1. Login thành công, server trả về token.
2. Client tự giữ token, khi gọi API gửi `Authorization: Bearer <access_token>`.
3. Server kiểm tra chữ ký và hạn dùng của token. JWT không bắt buộc lưu session trên server.

**So sánh:** Session dễ thu hồi ngay tại server nhưng phải quản lý session; JWT tiện cho REST API/mobile, kiểm tra được mà không cần session, nhưng khó thu hồi ngay nếu không có danh sách token bị thu hồi.

Project đang dùng JWT và `SessionCreationPolicy.STATELESS` trong [SecurityConfig.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/SecurityConfig.java). Phần logout vẫn lưu token bị thu hồi vào DB để chặn dùng lại.

## 3. JWT (JSON Web Token)

JWT là chuỗi token có 3 phần nối nhau bằng dấu chấm:

`Header.Payload.Signature`

- **Header:** loại token, thuật toán ký (ví dụ HS256).
- **Payload:** các thông tin như `sub`, `exp`, `iat`, `jti`, `type`, `role`. Payload chỉ mã hóa Base64URL, **không phải dữ liệu bí mật**.
- **Signature:** chữ ký tạo bằng key, giúp phát hiện token bị sửa.

Ưu điểm: gọn, dễ gửi qua API, không cần session cho mỗi lần truy cập. Nhược điểm: lộ token thì người khác có thể dùng trước khi hết hạn; payload không được để mật khẩu; secret phải giữ kín, token nên có hạn ngắn và dùng HTTPS.

Ở [JwtProvider.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/jwt/JwtProvider.java): `generateToken()` tạo Access Token, `generateRefreshToken()` tạo Refresh Token. Hai loại có claim `type` khác nhau. [JwtAuthenticationFilter.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/jwt/JwtAuthenticationFilter.java) chỉ cho Access Token vào API cần đăng nhập.

## 4. Password Hashing

Không lưu password dạng chữ thường vì lộ database là lộ toàn bộ mật khẩu. MD5 hoặc SHA-256 dùng trực tiếp quá nhanh, dễ bị thử hàng loạt; cùng một mật khẩu thường cho cùng hash nếu không có salt.

- **Salt:** dữ liệu ngẫu nhiên trộn với mật khẩu trước khi hash, làm các mật khẩu giống nhau vẫn có hash khác nhau.
- **Cost factor:** độ tốn công tính toán. Tăng cost thì việc đoán mật khẩu khó hơn nhưng đăng nhập cũng chậm hơn.
- **BCrypt/Argon2:** thuật toán hash mật khẩu có salt và tham số chi phí. Dự án này dùng BCrypt.

Trong [SecurityConfig.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/SecurityConfig.java) có `BCryptPasswordEncoder`. Khi đăng ký, [AuthServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/AuthServiceImpl.java) gọi `passwordEncoder.encode(password)` và lưu **hash**; lúc login Spring Security tự so sánh mật khẩu với hash.

## 5. Thực hành trên project Week 2

- **Register:** `POST /api/auth/register` → kiểm tra username/email, hash mật khẩu, gửi OTP. Kích hoạt tài khoản qua `POST /api/auth/active`.
- **Login:** `POST /api/auth/login` → kiểm tra mật khẩu bằng BCrypt → trả Access + Refresh Token.
- **Protected API:** `GET /api/auth/me` → phải gửi Access Token trong header; thiếu token trả 401.
- **JWT Filter:** đọc Bearer Token, kiểm tra chữ ký, hạn, loại ACCESS, trạng thái tài khoản và token đã thu hồi chưa; rồi mới cho phép truy cập.
- **RBAC:** `GET /api/users` → ADMIN được xem (200), STUDENT không có quyền (403).
- **Refresh Token:** `POST /api/auth/refresh` nhận Refresh Token còn hạn và chưa thu hồi → trả Access Token mới; gửi nhầm Access Token sẽ bị từ chối.
- **Logout:** `POST /api/auth/logout` → gửi Access Token trong header và Refresh Token trong JSON body → server thu hồi cả hai token.

Code nên đọc theo thứ tự: [AuthController.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/controller/AuthController.java) → [AuthServiceImpl.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/model/service/impl/AuthServiceImpl.java) → [JwtProvider.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/jwt/JwtProvider.java) → [JwtAuthenticationFilter.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/jwt/JwtAuthenticationFilter.java) → [SecurityConfig.java](CourseManagement_FinalProjectModule03/src/main/java/com/example/coursemanagement_finalprojectmodule03/security/SecurityConfig.java).

**Nhớ phân biệt 401 và 403:** 401 là chưa xác thực được hoặc token sai; 403 là đã đăng nhập nhưng không đủ quyền.

Để quay demo thì làm theo [demo/README.md](demo/README.md).
