# 03 — JWT: cấu trúc, kiểm chứng và giới hạn

## JWT/JWS

JWT biểu diễn một tập claims. Dạng JWS compact ký phổ biến có ba đoạn base64url:
`header.payload.signature`. Header mô tả thuật toán/loại, payload chứa claims,
signature bảo vệ tính toàn vẹn bằng thuật toán đã chọn. JWE là dạng mã hóa khác,
không phải mọi JWT đều có đúng ba đoạn.

Base64url là encoding; ai giữ token có thể đọc payload của JWT ký. Không đặt
password, refresh token hoặc nội dung template riêng vào payload. Chữ ký hợp lệ
không mã hóa payload, và JWT có thể bị dùng lại khi bị lộ.

## Claims phát hành trong server

| Claim | Ý nghĩa |
|---|---|
| sub | UUID tài khoản |
| sid | UUID family của phiên login |
| tokenUse | access, phân biệt mục đích token |
| jti | UUID riêng cho lần phát hành JWT |
| iat / exp | Thời điểm phát/hết hạn dạng Unix seconds |
| iss | erusentia-server |
| aud | erusentia-api |

Thời hạn access JWT là 900 giây. Role không được dùng làm quyết định cố định từ
payload; guard đọc role hiện tại của user trong store. Hai JWT phát cùng giây vẫn
khác jti. Đây là hợp đồng riêng của API, không phải các claim đều bắt buộc cho mọi JWT.

## Verify khác decode

Decode chỉ đọc dữ liệu, chưa chứng minh nguồn phát hay thời hạn. Guard dùng
JwtService.verifyAsync với allowlist HS256, issuer/audience cố định, kiểm tra exp,
tokenUse và dạng sub/sid, rồi kiểm tra tài khoản và session.

Tests gửi token bị sửa, sai key, HS384, sai issuer/audience, thiếu exp và đã hết hạn.
Tất cả phải trả 401. Server không chọn thuật toán tin cậy dựa riêng vào header do
người gửi cung cấp. Trường hợp alg=none không nằm trong allowlist.

## Key và triển khai

HS256 dùng shared secret để ký/verify, phù hợp phạm vi một backend đang thực hành.
JWT_SECRET cần ngẫu nhiên, ít nhất 32 byte; production thiếu key phải dừng khởi động.
Development thiếu key dùng key tạm và cảnh báo. Key không được đưa vào Git, ảnh hay
response. Cách sinh key nằm ở README server.

Khi hệ thống có nhiều resource server hoặc cần key rotation có kiểm soát, có thể
thiết kế cơ chế quản lý khóa khác. Bài hiện chưa triển khai JWKS hoặc OAuth/OIDC.
Không thay thuật toán chỉ bằng tin giá trị alg từ token.

## Token còn hạn có thể bị từ chối không?

Có: logout/replay revoke family; user inactive hoặc soft-delete cũng bị từ chối.
Điều này cần lookup trạng thái server mỗi request. Request đã vượt guard trước
lúc thu hồi có thể vẫn hoàn tất. JWT bị đánh cắp vẫn có thể được dùng trong thời
gian nó còn hợp lệ và phiên chưa bị thu hồi; rotation không tự phát hiện mọi vụ lộ access token.

Nguồn: [RFC 7519](https://www.rfc-editor.org/rfc/rfc7519.html),
[RFC 8725](https://www.rfc-editor.org/rfc/rfc8725.html),
[Nest JWT authentication](https://docs.nestjs.com/security/authentication).

[Trước](02_session_and_token.md) · [Tiếp: Password hashing](04_password_hashing.md)
