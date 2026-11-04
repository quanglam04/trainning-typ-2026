# 05 — Guards, RBAC và quyền sở hữu Template

## Luồng request trong NestJS

Sau bước parse HTTP/body và xác định route, global AccessTokenGuard chạy trước
RolesGuard. Nếu qua guard, Nest mới validate DTO/path/query và gọi controller/service.
JSON hỏng có thể bị parser từ chối trước guard; guard không thay parser hoặc DTO.

```text
HTTP/parser → route → AccessTokenGuard → RolesGuard → pipes/DTO
             → controller → service ownership → store/query → response
```

AuthModule đăng ký APP_GUARD; @Public chỉ áp vào register/login/refresh/logout.
Swagger UI/JSON được phục vụ bởi SwaggerModule khi bật, không phải API tài khoản.
Những endpoint cần login được khai báo Bearer security trong OpenAPI.

## Authentication context

AccessTokenGuard kiểm tra header `Authorization: Bearer <JWT>`, gọi AuthService để
verify token và lookup user/session, rồi gắn UserIdentity vào request. CurrentUser
lấy identity đó cho controller, không lấy từ body hoặc query.

Identity gồm id/email/role/createdAt, không có password hash. Role được lấy từ store
hiện tại nên thay đổi role có hiệu lực ở request sau, dù JWT cũ chưa hết hạn.

## Role không đủ để quyết định quyền với một row

RolesGuard bảo vệ GET /admin/users bằng @Roles("admin"). User hoặc creator nhận 403.
Template còn cần ownership; không thể chỉ dùng decorator role để biết row của ai.

```typescript
// Trích logic từ TemplatesService, viewer do guard xác thực.
const current = await this.find(id, viewer);
if (current.authorId !== viewer.id && viewer.role !== "admin") {
  throw new ForbiddenException("Only the author or an admin can modify this template");
}
```

find trước đó che private không thuộc người gọi bằng 404. Nếu template public,
người khác được đọc nhưng sửa/xóa trả 403. Admin có thể quản lý mọi template.
Body create không nhận authorId; service luôn ghép `{...input, authorId: viewer.id}`.
Body patch cũng không cho đổi tác giả, metrics hoặc timestamps.

## Visibility phải áp cả list và count

PostgreSQL thêm điều kiện public hoặc authorId của người gọi trước filter khác,
skip/take và getManyAndCount. Admin không bị giới hạn visibility này. Memory áp
cùng điều kiện trước khi slice/count. Query authorId chỉ lọc, không được bỏ điều
kiện visibility khi người gọi chọn một author khác.

Tests tạo private/public của A, dùng JWT B đọc/sửa/xóa và xem count, rồi dùng admin
để quản lý. Gửi authorId giả trả 400. Đây là kiểm chứng chống truy cập trái quyền
trong các route hiện có, chưa phải audit mọi loại IDOR của một ứng dụng hoàn chỉnh.

## Dữ liệu và exception

API vẫn dùng ApiExceptionFilter chung, bổ sung UNAUTHORIZED/FORBIDDEN. 401 có
WWW-Authenticate: Bearer. Auth và request protected đặt Cache-Control:no-store.
Logs/response không trả password hash hoặc token trong thông báo lỗi.

Migration, nguồn guard/service/store và tests nằm tại [snapshot](../SOURCE_SNAPSHOT.md).
Nguồn: [Nest guards](https://docs.nestjs.com/guards),
[authorization](https://docs.nestjs.com/security/authorization).

[Trước](04_password_hashing.md) · [Tiếp: Refresh rotation](06_refresh_rotation.md)
