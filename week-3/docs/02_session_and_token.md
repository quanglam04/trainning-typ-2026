# 02 — Session-based và token-based authentication

## Flow session-based thường gặp

1. Client gửi thông tin đăng nhập qua kết nối được bảo vệ.
2. Server xác thực, tạo session với ID ngẫu nhiên và lưu trạng thái phía server.
3. Trình duyệt thường nhận session ID qua cookie.
4. Mỗi request tiếp theo gửi ID; server tra session và tài khoản để xử lý.
5. Logout xóa/thu hồi session; ID cũ không được tiếp tục dùng.

Nếu có nhiều instance, session cần một cơ chế chia sẻ hoặc định tuyến phù hợp.
Cookie là cơ chế lưu/gửi dữ liệu HTTP, không phải bản thân session. Cookie cũng
có thể chứa token; so sánh session/token không đồng nghĩa so sánh cookie/header.

## Flow access token thường gặp

Sau login, server phát token có thời hạn. Client gửi token cho resource API, ví dụ
Authorization: Bearer. Với JWT ký, server có thể kiểm tra chữ ký và claims mà không
tra một bản ghi cho riêng token đó. Token opaque thường cần lookup/introspection.

Client giữ token không làm nó trở thành dữ liệu đáng tin. Bearer có nghĩa bên giữ
token có thể sử dụng quyền của token; lộ token có thể dẫn đến giả mạo request.
JWT cũng là một loại token, không phải mọi token đều là JWT.

## So sánh

| Tiêu chí | Session ID với lookup | JWT tự chứa, chỉ verify |
|---|---|---|
| Trạng thái riêng cho phiên | Thường lưu server | Có thể giảm lookup token |
| Thu hồi tức thời | Cập nhật/xóa session | Cần thêm trạng thái hoặc chờ expiry |
| Kích thước gửi | ID thường nhỏ | Claims/chữ ký làm token lớn hơn |
| Cập nhật quyền | Có thể đọc trạng thái hiện tại | Claims có thể cũ tới expiry |
| Phân phối nhiều API | Cần truy cập session/introspection | Có thể chia sẻ cơ chế verify phù hợp |

Đây là hai mô hình điển hình; có thiết kế lai. Lựa chọn phụ thuộc client, nhu cầu
thu hồi, cách triển khai và rủi ro XSS/CSRF. Session không mặc nhiên kém mở rộng,
JWT không mặc nhiên an toàn hơn hoặc nhanh hơn trong mọi hệ thống.

## Thiết kế đang dùng

API tuần 3 dùng access JWT 15 phút và refresh token opaque 7 ngày. Guard vẫn tra
user hiện tại và family của phiên mỗi request để thu hồi quyền sau logout/replay
hoặc khóa tài khoản. Vì vậy đây **không phải hệ thống hoàn toàn stateless**.

Roles lấy từ store hiện tại, không tin role do client gửi. Lịch sử refresh được
lưu trong PostgreSQL; memory chỉ phù hợp một process và mất dữ liệu khi restart.
Đổi JWT_SECRET làm access JWT cũ không verify được; giữ key ổn định khi cần restart.

## Client ở phạm vi bài

Swagger giữ Bearer authorization trong bộ nhớ của trang; persistAuthorization=false.
API trả token qua JSON, chưa đặt refresh cookie hoặc xây dựng CSRF flow. App vẫn
chưa quản lý token hoặc gửi API. Quyết định lưu token trong client thật cần được
thiết kế khi tích hợp, thay vì mặc định chép token dài hạn vào localStorage.

Nguồn: [OWASP Session Management](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html),
[Nest authentication](https://docs.nestjs.com/security/authentication).

[Trước](01_authentication_authorization.md) · [Tiếp: JWT](03_jwt.md)
