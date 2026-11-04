# Tuần 3 — Authentication, authorization và quản lý phiên

Phát triển trên API Template tuần 2 bằng NestJS/TypeORM/PostgreSQL. Đề bài trong
[README](../README.md) giữ nguyên; các bài dưới là phần giải thích và thực hành.
App vẫn dùng dữ liệu local, chưa tích hợp API hoặc luồng đăng nhập.

| Bài | Nội dung |
|---|---|
| [01 — Authentication và authorization](01_authentication_authorization.md) | Danh tính, RBAC, ownership, 401/403/404 |
| [02 — Session và token](02_session_and_token.md) | Flow, cookie/Bearer, trạng thái server và lựa chọn triển khai |
| [03 — JWT](03_jwt.md) | JWS, claims, chữ ký, expiry, issuer/audience, key |
| [04 — Password hashing](04_password_hashing.md) | Salt, cost, Argon2id, bcrypt và dữ liệu seed |
| [05 — Guard và quyền Template](05_guards_and_ownership.md) | DI, global guards, role hiện tại, tránh IDOR |
| [06 — Refresh rotation](06_refresh_rotation.md) | Token opaque, transaction, replay, logout và race |
| [07 — Thực hành Swagger](07_swagger_practice.md) | Register/login, Authorize, protected API, admin, refresh/logout và ảnh |
| [08 — Câu hỏi ôn tập](08_interview_qa.md) | Giải thích quyết định thiết kế và giới hạn |

- [Báo cáo tuần 3](../WEEK3_REPORT.md).
- [Mã nguồn và trạng thái snapshot](../SOURCE_SNAPSHOT.md).
- [Tiến độ chung](../../docs/LEARNING_TRACKER.md).

## Cách bắt đầu

Chạy từ repo erusentia-server, không tạo backend/package khác trong TYP:

```bash
pnpm install --frozen-lockfile
TEMPLATE_STORE=memory HOST=127.0.0.1 PORT=3001 pnpm dev
```

Mở `/docs`, đăng ký user, login, dán accessToken vào Authorize. Memory không có tài
khoản admin sẵn; dùng PostgreSQL và CLI `pnpm auth:admin` cho luồng admin đầy đủ.
Hướng dẫn database/migration/key nằm trong README server, dẫn ở snapshot.

Tuần 2 giữ bản CRUD trước auth tại [mốc nguồn cố định](../../week-2/SOURCE_SNAPSHOT.md).
Ở tuần 3, mọi route Template cần JWT và body tạo template không còn nhận authorId.
