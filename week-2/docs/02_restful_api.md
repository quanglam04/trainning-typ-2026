# 02 — REST và hợp đồng HTTP

## REST và HTTP

REST là phong cách kiến trúc, không phải tên gọi cho mọi API trả JSON. Các ràng buộc
là client-server, stateless, cache, uniform interface, layered system và code-on-demand
(tùy chọn). Uniform interface còn gồm nhận diện resource, thao tác qua representation,
message tự mô tả và hypermedia; không chỉ chọn GET/POST đúng tên.
Bài thực hành triển khai HTTP resource API; hypermedia chưa được triển khai.

Stateless yêu cầu request có đủ ngữ cảnh xử lý, không phụ thuộc một chuỗi request
được server nhớ riêng cho client. Server vẫn lưu resource trong database; stateless
không có nghĩa server không được lưu dữ liệu.

## Method và idempotency

| Method | Ý nghĩa | Idempotent theo ngữ nghĩa HTTP |
|---|---|---|
| GET | Đọc representation | Có |
| POST | Xử lý dữ liệu gửi lên; thường tạo resource trong collection | Không được đảm bảo |
| PUT | Tạo/thay thế representation tại URI đích | Có |
| PATCH | Áp dụng thay đổi theo hợp đồng patch | Không được đảm bảo; có thể thiết kế có |
| DELETE | Loại bỏ liên kết resource tại URI | Có |

Idempotency xét tác động dự định của việc lặp lại cùng request, không yêu cầu body,
status hoặc timestamp response giống hệt. DELETE lần đầu 204, lần sau 404 vẫn có
thể idempotent. PATCH đặt `title="A"` có thể idempotent; PATCH tăng counter thì không.
PUT không bắt buộc gửi mọi cột nội bộ DB hoặc tự đặt mọi cột thiếu thành null: phải
định nghĩa representation và cách xử lý trường thiếu. Trong bài chỉ có PATCH,
không dùng PUT cho thao tác merge một phần.

## Năm route hiện có

| Route | Input | Thành công |
|---|---|---|
| GET /templates | query page/size và filters | 200 + page envelope |
| GET /templates/:id | UUID path | 200 + Template |
| POST /templates | CreateTemplateDto | 201 + Template mới |
| PATCH /templates/:id | UUID + UpdateTemplateDto | 200 + Template mới nhất |
| DELETE /templates/:id | UUID | 204, body rỗng |

Đường dẫn gốc của resource là `/templates`. Template là đối tượng thực hành áp
dụng vào EruSentia. `authorId` ở POST chưa
phải danh tính người gọi được xác thực; auth/authorization thuộc tuần 3.

## Path, query, body

- Path xác định resource cụ thể; ID sai syntax trả 400, không có row trả 404.
- Query lọc/sort/phân trang, ví dụ `/templates?page=1&size=5&isPublic=false`.
- Body JSON tạo/sửa resource. Không truyền object qua path hoặc dùng GET để ghi
  nội dung template. GET/PATCH/DELETE không tăng lượt xem ngầm.

HTML của App nằm trong contentJson có format/schemaVersion, không phải cột
html_content mới. Mẫu đầy đủ và giới hạn nằm ở README server/API contract.

## Response thống nhất đang dùng

List trả trực tiếp `{data, page, size, totalPages, totalElements}`. Detail/POST/PATCH
trả một object Template, không bọc thêm `success` hay `data`. `data` chỉ xuất hiện
một lần ở list; không có meta ở ngoài hoặc `data.data`.

Error trả `{statusCode, code, message, details, path, timestamp}`. Đây là hợp đồng
riêng của bài; RFC 9457 định nghĩa một cấu trúc Problem Details khác.
400: request không hợp lệ; 404: không có resource; 409: slug trùng; 413: body quá lớn;
500: lỗi bất ngờ. 401/403 là kiến thức cho auth tuần sau, chưa phải tính năng tuần 2.

## Lưu ý về xóa và dữ liệu riêng tư

DELETE thực hiện soft delete, GET/list loại row đã xóa. UNIQUE(slug) vẫn áp dụng nên
slug của row đã xóa còn được giữ. HTTP không bắt buộc DELETE phải xóa vật lý row.
API hiện là local management CRUD, có thể đọc row isPublic=false; chưa phải public
feed đã có quyền truy cập. App chưa gọi endpoint này.

[Mốc nguồn](../SOURCE_SNAPSHOT.md) · [Postman](09_postman.md).
Nguồn: [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html),
[RFC 5789 — PATCH](https://www.rfc-editor.org/rfc/rfc5789.html).

[Trước](01_nestjs_architecture.md) · [Tiếp: DTO](03_dto_validation.md)
