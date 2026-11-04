# Tuần 2 — Mục lục lý thuyết, thực hành và minh chứng

Đối tượng thực hành: Template của EruSentia, TypeScript + NestJS + TypeORM + PostgreSQL.
Bộ tài liệu gồm 8 chuyên đề và hướng dẫn Postman, đối chiếu với mã server tuần 2.
Mã chạy/tests/collection ở server; TYP giữ báo cáo, bài đọc, ví dụ và ảnh.

| Bài | Nội dung |
|---|---|
| [01 — Kiến trúc NestJS](01_nestjs_architecture.md) | Framework, module/controller/service, DI token, lifecycle, bootstrap |
| [02 — RESTful API](02_restful_api.md) | HTTP methods/status, idempotency, 5 routes, hợp đồng response |
| [03 — DTO & validation](03_dto_validation.md) | whitelist, null/false, nested HTML metadata, PATCH |
| [04 — ORM entities](04_typeorm_entities.md) | Mapping schema tuần 1, relations, soft delete, migrations |
| [05 — Repository](05_repository_pattern.md) | Port/adapters, QueryBuilder, N+1, constraint/race, transaction |
| [06 — Pagination](06_pagination_api.md) | Offset, total/filter, tiebreaker, cursor lý thuyết và giới hạn |
| [07 — Error handling](07_error_handling.md) | Global filter, SQLSTATE mapping, validation, 204/413/500 |
| [08 — Câu hỏi ôn tập](08_interview_qa.md) | 5 câu hỏi gốc và kiến thức mở rộng gắn thực hành |
| [09 — Postman](09_postman.md) | Native Git Local mode, baseUrl/environment, collection/Runner, assertions và ảnh minh chứng |

- [Đề bài gốc](../README.md) · [Hướng dẫn chạy thực hành](#chạy-thực-hành-erusentia).
- [Báo cáo kết quả và giới hạn](../WEEK2_REPORT.md).
- [Nguồn tham chiếu, trạng thái SHA và manifest](../SOURCE_SNAPSHOT.md).
- [Ảnh Postman](../screenshots/01-postman-create.png).

App chưa tích hợp API và nằm ngoài phạm vi triển khai tuần 2. Các ví dụ code rút gọn
trong bài là minh họa; file thực thi được dẫn tại SOURCE_SNAPSHOT. Kết quả kiểm thử
và giới hạn thực nghiệm được ghi trong báo cáo.

[Tuần 1](../../week-1/docs/00_GUIDE_INDEX.md) · [Tổng quan](../../README.md)

## Chạy thực hành EruSentia

Mã chạy, package/lockfile, DTO, ORM, tests và collection nằm ở **erusentia-server**.
TYP chỉ giữ bài đọc, ví dụ, báo cáo và ảnh; không cần cài dependencies trong week-2.

Trong thư mục server:

```bash
pnpm install --frozen-lockfile
# Bước 1: cùng CRUD chưa cần database
TEMPLATE_STORE=memory HOST=127.0.0.1 PORT=3001 pnpm dev
# Ctrl+C trước khi dùng cùng port; bước 2: DB/schema/seed theo README server
TEMPLATE_STORE=postgres HOST=127.0.0.1 PORT=3001 pnpm dev
```

Chỉ định database test riêng khi thực hành ghi dữ liệu. README server có lệnh tạo,
chạy integration test và dọn DB; không reset database nghiệp vụ. API dùng `/templates`
trực tiếp, không `/api/v1`. Payload đầy đủ trong server `tools/examples/create-template.json`.
List có `{data,page,size,totalPages,totalElements}`; DELETE soft-delete trả 204.

App hiện có template HTML + metadata, thư viện cá nhân trong localStorage; **chưa tích
hợp API**. Server nhận envelope HTML tương ứng trong content_json, chưa thay đổi App.
Nhóm chủ đề App khác categoryId taxonomy SQL; không tự đồng nhất hai khái niệm.
Auth/authorization thuộc tuần 3, API tuần 2 chỉ là local management CRUD.

Nguồn tuần 2 hiện được đối chiếu bằng đường dẫn local và SHA-256 trong
SOURCE_SNAPSHOT; permalink GitHub sẽ được bổ sung khi công bố commit nguồn.
Swagger UI cơ bản ở `/docs`, đặc tả OpenAPI ở `/docs-json`, cùng host/port với API.
Dùng Try it out để gửi request; xem cấu hình bật/tắt trong README server.
