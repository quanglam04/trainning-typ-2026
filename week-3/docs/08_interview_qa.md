# 08 — Câu hỏi ôn tập

## Authentication khác authorization thế nào?

Authentication xác định người gọi; authorization quyết định hành động với tài
nguyên. User có JWT hợp lệ vẫn bị 403 khi gọi API admin hoặc sửa template public
của người khác. Private của người khác được che bằng 404 theo hợp đồng bài này.

## JWT có làm server hoàn toàn stateless không?

Không tự động. Verify chữ ký có thể không cần lookup token, nhưng API hiện kiểm tra
user và family mỗi request để thu hồi phiên/đổi quyền kịp thời. Đây là thiết kế lai
với JWT và trạng thái phiên phía server. Cookie và token cũng không phải hai khái
niệm đối lập: cookie có thể vận chuyển một token.

## Vì sao không dùng decode để xác thực?

Decode chỉ đọc payload. Guard phải verify chữ ký, allowlist thuật toán, issuer,
audience, expiry và loại token. Bài dùng HS256, không nhận thuật toán tùy ý từ alg.
JWT ký không được mã hóa; payload không chứa password/email hay nội dung template.

## Salt có giữ bí mật không? Cost giải quyết gì?

Salt được lưu trong PHC string, tạo sự khác biệt giữa các hash cùng password và
hạn chế tính sẵn. Cost tăng tài nguyên mỗi phép thử; không bảo đảm password yếu
không bị đoán. Argon2id trong bài có m=19456 KiB, t=2, p=1; chưa benchmark tải lớn.

## Vì sao refresh token dùng SHA-256, password lại dùng Argon2id?

Refresh là token ngẫu nhiên 256 bit, không phải password yếu của người dùng. Hash
nhanh phục vụ lookup mà không lưu token gốc. Password cần KDF làm mỗi lần đoán tốn
kém hơn. Không suy từ token hashing ra việc dùng SHA-256 trực tiếp cho password.

## Logout xong access JWT còn hạn có dùng được không?

Ở API này không dùng được ở request kế tiếp vì guard kiểm tra family đã bị revoke.
Các login khác không bị ảnh hưởng. Request đã vượt guard vẫn có thể hoàn tất.
Nếu chỉ verify JWT mà không có lookup thu hồi thì hành vi có thể khác.

## Hai tab cùng refresh thì sao?

Một request có thể trả 200, request còn lại dùng token cũ trả 401 và revoke family.
Client phải phối hợp refresh. Transaction và lock theo family bảo vệ cả race giữa
các thế hệ token; không chỉ khóa row của đúng token cũ.

## Vì sao không cho client gửi role hoặc authorId?

Register luôn tạo user; admin được cấp qua CLI local. Author lấy từ CurrentUser
đã xác thực. Query authorId chỉ lọc, không cấp quyền; count cũng phải áp visibility.
Không tin cờ isAdmin hoặc owner do frontend tự gửi.

## Đã có account admin hoặc password seed để thử sẵn chưa?

Không có credential admin mặc định. Seed tuần 1 chứa placeholder; tạo admin mới
qua pnpm auth:admin bằng email/password tự chọn. Trùng email thì CLI không sửa
account. Chưa có luồng nâng role qua API.

## Còn thiếu gì ngoài phạm vi bài?

Rate limiting, email verification, reset password, MFA, cookie/CSRF flow, quản lý và
dọn phiên, TLS deployment, kiểm thử tải và tích hợp App. Có demo local không đồng
nghĩa đã hoàn thành một hệ thống xác thực production.

[Mục lục](00_GUIDE_INDEX.md) · [Báo cáo](../WEEK3_REPORT.md) · [Nguồn](../SOURCE_SNAPSHOT.md).
