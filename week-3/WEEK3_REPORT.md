# Báo cáo tuần 3 — Authentication và Authorization

**Học viên:** Nguyễn Tiến Tuấn. **Ngày thực hành:** 04/11/2026, Asia/Bangkok.
**Dự án:** EruSentia — Sentia Hub Backend.
**Trạng thái:** đã chốt nguồn tuần 3; [snapshot](SOURCE_SNAPSHOT.md)
ghi commit backend đầy đủ, permalink và SHA-256 của nguồn đã đối chiếu.
Tag lưu trữ báo cáo: `typ-2026-week-3`. [Đề gốc](README.md) được giữ nguyên.

## 1. Mục tiêu và phạm vi

Phát triển project tuần 2 bằng NestJS/TypeORM/PostgreSQL để có register, login, JWT,
protected API, RBAC admin/user và refresh token. Mã nguồn/SQL/tests ở server; TYP giữ
lý thuyết, báo cáo và minh chứng. App vẫn dùng dữ liệu local, chưa tích hợp API.

| Yêu cầu | Thực hiện |
|---|---|
| Authentication / authorization | AccessTokenGuard, RolesGuard và ownership ở TemplatesService |
| Session vs token | Bài lý thuyết; triển khai JWT có lookup trạng thái phiên |
| JWT | HS256, issuer/audience/exp/tokenUse/sub/sid, jti riêng cho lần phát hành |
| Password hashing | Argon2id, salt riêng, m=19456 KiB/t=2/p=1 |
| Register/login | POST /auth/register, /auth/login; DTO chỉ nhận trường cho phép |
| Protected API | GET /auth/me và toàn bộ CRUD Template cần Bearer |
| Admin vs user | GET /admin/users, admin cấp qua CLI local |
| Refresh/logout | Token opaque, rotation, replay revoke, POST /auth/logout |
| Demo và ảnh | Swagger UI thực thi trên PostgreSQL, 11 ảnh overview/dialog/response |

Cơ chế access JWT kèm kiểm tra user và family mỗi request hỗ trợ thu hồi phiên.
Thiết kế có trạng thái server; đây không phải triển khai OAuth/OIDC hoàn chỉnh.

## 2. Nghiệp vụ và thay đổi từ tuần 2

- Register chuẩn hóa email; role luôn user, không cho client tự cấp admin/creator.
- Password không trim; giữ 12–128 ký tự, lưu PHC Argon2id, không trả hash qua HTTP.
- Login dùng lỗi 401 chung cho email/password/account không hợp lệ. Seed tuần 1
  không phải credential thật; CLI tạo admin mới và không ghi đè tài khoản đã có.
- POST Template lấy authorId từ JWT. Client gửi authorId vào POST/PATCH nhận 400.
- User/creator đọc public và private của mình, chỉ sửa/xóa của mình. Admin có quyền
  quản lý; private của người khác trả 404, sửa public trái quyền trả 403.
- Visibility áp trước list/count/pagination. HTML envelope, metrics và soft-delete
  giữ quy tắc còn lại của tuần 2; App không được sửa để tích hợp API.

## 3. Token và database

Access JWT sống 900 giây; refresh là 32 byte ngẫu nhiên base64url. Database chỉ lưu
SHA-256 của refresh token, cùng family, expiry, used_at và revoked_at. Mỗi refresh
thay token và giữ hạn tuyệt đối 7 ngày của family ban đầu.

PostgreSQL dùng transaction advisory lock theo family và row lock để bảo vệ rotation,
replay và logout, kể cả race giữa các thế hệ token. Revoke phải commit trước khi
AuthService trả 401. Logout/replay làm access của family bị từ chối ở request sau;
login khác vẫn hoạt động. Request đã vượt guard có thể vẫn hoàn tất.

Migration `sql/05_auth_sessions.sql` thêm bảng auth_refresh_sessions và unique index
LOWER(email). Schema sau migration có 10 bảng; các file schema/index/seed tuần 1
được giữ nguyên. Không bật synchronize/migrationsRun hoặc tự migrate khi bootstrap.
Bộ test xác nhận migration chạy lại được, token hash được lưu và refresh còn dùng
được qua application restart.

## 4. Môi trường và kiểm chứng

Node.js **24.21.0**, pnpm **12.11.2**, NestJS **12.3.2**, TypeORM **0.3.31**,
@nestjs/jwt **12.0.3**, @node-rs/argon2 **2.2.2**, @nestjs/swagger **12.1.0**.
PostgreSQL Docker **16.15** trên Ubuntu; browser headless mở Swagger UI cùng server.

| Kiểm tra | Kết quả |
|---|---|
| pnpm install --frozen-lockfile | Đạt |
| pnpm typecheck / build | Đạt |
| pnpm test | 32 pass, 0 fail, 1 DB test skip mặc định |
| RUN_DB_TESTS=1 ... pnpm test:api:db | 19 pass, 0 fail trên DB riêng rỗng |
| Swagger register/login/protected | 201 / 200 / JWT hợp lệ 200, thiếu token 401 |
| Swagger role | User gọi admin 403; admin gọi cùng route 200 |
| Swagger refresh/replay | Refresh 200, token mới; reuse token cũ 401, family bị thu hồi |
| Swagger logout | 204, access JWT của phiên bị từ chối 401 ở request sau |

Node runner tính cả parent/subtest; số test không phải số endpoint. Các ca gồm
email race, chống gửi role/authorId, hash không lộ, JWT sai key/alg/issuer/audience,
thiếu/hết exp, ownership, private count, refresh đồng thời, parent/child replay,
logout race, khóa/xóa account, expiry fixture và persistence qua app mới.
Hết hạn refresh được kiểm chứng bằng fixture thời gian, không chờ thực tế 7 ngày.

Các DB kiểm chứng `week3_test_20261104_auth_1`, `week3_test_20261104_auth_2` và
`week3_test_20261104_swagger` tách biệt DB nghiệp vụ. Lượt rà soát trước commit
dùng thêm `week3_test_20261104_review`, đạt lại 19/19 test. Không áp migration hay seed
vào sentia_hub_db; dữ liệu nghiệp vụ trước/sau giữ 10 users và 1.012 templates.
API/browser và các database test được dọn khi kết thúc, container/volume dev được giữ.

## 5. Minh chứng và cách tái hiện

[Hướng dẫn Swagger](docs/07_swagger_practice.md) trình bày đủ flow register → login →
Authorize → protected → admin/user → refresh/replay → logout. Ảnh nguyên bản; phần
response được chụp riêng để không công bố token hoặc cURL chứa Authorization.
Login/refresh 200 được thực thi và kiểm tra nhưng không chèn response chứa token gốc.

README server hướng dẫn key, migration, cấp admin, chạy memory/PostgreSQL và test.
[SOURCE_SNAPSHOT](SOURCE_SNAPSHOT.md) dẫn từng file thực thi/test. TYP không chứa một
project backend thứ hai, dữ liệu tài khoản thật, token hay file execution dump.

## 6. Vấn đề kỹ thuật và cách xử lý

| Vấn đề | Cách xử lý |
|---|---|
| Role trong token có thể cũ | Đọc role/user hiện tại sau verify JWT |
| ID tài nguyên không chứng minh ownership | Lấy author từ CurrentUser, kiểm tra từng row và visibility trong query |
| Refresh bị dùng đồng thời hoặc replay parent | Lock theo family, atomic rotation và revoke family |
| Ném exception trong transaction làm mất revoke | Commit kết quả thất bại có revoke, rồi mới trả HTTP 401 |
| Seed password chưa dùng được | Không tạo password mặc định; CLI cấp admin mới bằng credential do người vận hành nhập |
| JWT qua restart | Production bắt buộc key ổn định; development thiếu key cảnh báo key tạm |

## 7. Giới hạn và hướng tiếp theo

Chưa có rate limiting, email verification/reset password, MFA, cookie/CSRF flow,
purge lịch sử phiên, quản lý phiên qua UI, TLS deployment, kiểm thử tải hoặc tích hợp
App. Không kết luận sẵn sàng production từ demo local. Message Queue thuộc tuần 4,
chưa thêm vào tuần 3.

Nguồn tuần 3 được ghim bằng permalink commit đầy đủ trong snapshot; báo cáo lưu
tại tag `typ-2026-week-3`. Mốc nguồn và báo cáo tuần 1–2 giữ nguyên.
