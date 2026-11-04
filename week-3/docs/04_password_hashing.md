# 04 — Password hashing, salt và cost

## Vì sao không lưu plain text hoặc hash nhanh trực tiếp?

Nếu database bị lộ, plain text cho phép đọc ngay mật khẩu. Một phép MD5/SHA trực
tiếp quá nhanh để chống thử hàng loạt mật khẩu yếu. Hash mật khẩu cần thuật toán
chuyên dụng có cost phù hợp. Hashing một chiều khác encryption có thể giải mã.

Nhận định này không cấm mọi cách dùng SHA: PBKDF2 dùng HMAC trong một password KDF;
SHA-256 của token ngẫu nhiên entropy cao cũng có ngữ cảnh khác. Vấn đề là băm trực
tiếp password bằng một thuật toán nhanh, không có cơ chế tăng chi phí phù hợp.

## Salt và cost

Salt ngẫu nhiên riêng cho mỗi password làm cùng password có hash khác nhau và hạn
chế bảng tính sẵn. Salt không cần giữ bí mật; password KDF thường lưu salt cùng tham
số và hash. Salt không tự làm password yếu trở thành mạnh hoặc ngăn mọi brute force.

Cost làm mỗi phép thử tốn tài nguyên hơn. Cần cân bằng độ khó tấn công với latency,
bộ nhớ và số request đồng thời trên máy triển khai; không suy từ một request local
ra năng lực phục vụ tải lớn.

## Argon2id trong server

Passwords service dùng @node-rs/argon2, Argon2id v19 với:

| Tham số | Giá trị |
|---|---|
| memoryCost | 19456 KiB = 19 MiB |
| timeCost | 2 |
| parallelism | 1 |
| outputLen | 32 byte |
| Salt | Thư viện tự sinh ngẫu nhiên cho mỗi hash |

users.password_hash lưu PHC string gồm thuật toán, tham số, salt và digest. Dạng
minh họa: `$argon2id$v=19$m=19456,t=2,p=1$<salt>$<digest>`; đây không phải hash dùng
để login. Verify dùng thư viện đọc PHC string, không so sánh plain text với hash.

Register nhận password 12–128 ký tự và không trim; khoảng trắng nếu có là một phần
của password. API không nhận passwordHash từ client. Login password sai, email
không có hoặc account không hoạt động dùng thông báo 401 chung.

## Bcrypt và legacy data

Bcrypt cũng là password hashing có salt và work factor; cần chú ý giới hạn 72 byte
của phần lớn implementation, đặc biệt Unicode. Bài chọn Argon2id; không có luồng
migrate hash bcrypt cũ. Hash placeholder trong seed tuần 1 không phải bcrypt/Argon2
credential hợp lệ và không tạo mật khẩu admin mặc định.

Account lạ/legacy chạy dummy verification để tránh bỏ qua hoàn toàn công việc hash;
điều này không chứng minh toàn bộ HTTP có thời gian cố định. Chưa có rate limiting,
kiểm tra password đã bị lộ, reset password, email verification hay MFA.

Nguồn: [OWASP Password Storage](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html),
[node-rs Argon2](https://github.com/napi-rs/node-rs/tree/main/packages/argon2).

[Trước](03_jwt.md) · [Tiếp: Guards và ownership](05_guards_and_ownership.md)
