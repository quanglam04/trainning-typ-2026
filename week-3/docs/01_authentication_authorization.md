# 01 — Authentication và authorization

## Hai câu hỏi khác nhau

**Authentication (xác thực)** xác định người đang gửi request. Ở bài này, login
kiểm tra email/password; request sau dùng Bearer access JWT đã được server ký.

**Authorization (phân quyền)** xác định tài khoản đó được làm gì với tài nguyên
cụ thể. Có JWT hợp lệ không có nghĩa được sửa mọi template hoặc gọi API admin.

Ví dụ: user A có thể xác thực thành công nhưng vẫn bị từ chối sửa template của B.
Server cần cả role và quan hệ giữa người gọi với tài nguyên, không chỉ kiểm tra
client đã đăng nhập hay chưa.

## Áp dụng vào EruSentia

Bảng users tuần 1 đã có role user/creator/admin, is_active và deleted_at. Register
chỉ tạo user; client không được gửi role hoặc cờ tài khoản. Admin được cấp qua CLI
local, không qua endpoint đăng ký công khai. Creator hiện có cùng quyền sở hữu
Template như user, không phải một cơ chế mua bán hoặc xuất bản mới.

| Thao tác | User/creator | Admin |
|---|---|---|
| Đọc template public | Có JWT | Có JWT |
| Đọc private | Của mình | Tất cả |
| Tạo template | Author là người gọi | Author là người gọi |
| Sửa/xóa template | Của mình | Tất cả |
| GET /admin/users | Không | Có |

Role admin không làm POST nhận authorId tùy ý. Tác giả luôn được lấy từ danh tính
server xác thực; muốn tạo tài nguyên thay người khác cần hợp đồng riêng về sau.
Các tab official/custom của App không tự chuyển thành role hoặc quyền API.

## Status có ý nghĩa gì?

- **400:** dữ liệu không hợp lệ, chẳng hạn gửi role khi register hoặc authorId khi POST.
- **401:** thiếu/sai/hết hạn JWT, tài khoản không hoạt động hoặc phiên đã bị thu hồi.
- **403:** đã xác thực nhưng không đủ quyền, ví dụ user gọi API admin.
- **404:** không có resource; bài này cũng dùng cho private template của người khác.

403 và 404 là lựa chọn của hợp đồng: public template đọc được nhưng sửa trái quyền
trả 403; private của người khác được che bằng 404. List/count áp cùng điều kiện
visibility trước pagination, tránh lộ private qua totalElements.

## Kiểm tra ở server

AccessTokenGuard verify JWT, kiểm tra user và phiên còn hiệu lực. RolesGuard áp
role của tài khoản hiện tại. TemplatesService kiểm tra quyền với từng template.
Chặn một nút ở frontend không thay các kiểm tra này; App hiện chưa tích hợp API.

Các tình huống 401, 403, 404 và giả mạo authorId đã được kiểm tra qua HTTP ở memory
và PostgreSQL. Chi tiết và đường dẫn file nằm trong [snapshot](../SOURCE_SNAPSHOT.md).

Nguồn: [Nest authentication](https://docs.nestjs.com/security/authentication),
[Nest authorization](https://docs.nestjs.com/security/authorization),
[RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html).

[Tiếp: Session và token](02_session_and_token.md)
