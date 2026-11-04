# Báo cáo tuần 2 — Framework, REST API và ORM

**Học viên:** Nguyễn Tiến Tuấn. **Kiểm chứng HTTP/DB/Postman:** 03/11/2026.
**Bổ sung và kiểm tra Swagger:** 04/11/2026.
**Dự án áp dụng:** EruSentia — Sentia Hub Backend.
**Trạng thái:** đã triển khai và kiểm thử; nguồn server được ghim theo commit trong
[snapshot](SOURCE_SNAPSHOT.md), kèm SHA-256 của từng file đối chiếu.
[Mục lục lý thuyết](docs/00_GUIDE_INDEX.md) gồm 8 chuyên đề và hướng dẫn Postman.

## 1. Mục tiêu và phạm vi áp dụng

Xây dựng CRUD cho Template của EruSentia để vận dụng kiến thức framework, HTTP và ORM.
Backend TypeScript + NestJS + TypeORM trên PostgreSQL 16; cùng 5 route chuyển adapter
memory → DB thật. Mã nguồn/tests/Postman chỉ ở server; TYP giữ lý thuyết, ví dụ, báo
cáo và ảnh.

App hiện dùng template HTML/metadata và localStorage, **chưa tích hợp API**. Tuần 2
không sửa App. Server lưu HTML cùng metadata trong `content_json` JSONB, sử dụng
schema SQL tuần 1. Mẫu HTML minh họa được tạo riêng cho bài thực hành. Bảng ánh xạ
App ↔ server nằm trong [hợp đồng dữ liệu](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/docs/APP_TEMPLATE_CONTRACT.md).

| Yêu cầu | Thực hiện / nguồn đối chiếu trong server |
|---|---|
| Framework, module/controller/service, IoC/DI | src/app.ts, src/templates/templates.*.ts, TEMPLATE_STORE token |
| Path/query/body, status | UuidPipe, DTO, controller; 200/201/204/400/404/409/413/500 |
| Tối thiểu 5 CRUD API | GET list/detail, POST, PATCH, DELETE /templates |
| Bước chưa có DB | MemoryTemplateStore; cùng service/DTO/contract, không kết nối DB |
| ORM và PostgreSQL | PostgresTemplateStore, TypeORM Repository/QueryBuilder |
| Relationship | Template ManyToOne Author/Category, OneToMany chiều ngược, JOIN category |
| Pagination | page/size, totalPages/totalElements, filter/sort + ID tiebreaker |
| Error response | ApiExceptionFilter, nested validation và DB SQLSTATE mapping |
| Postman | Collection 12 request/30 assertion trong tools/postman; ảnh thao tác và Runner ở mục 4 |
| Swagger/OpenAPI bổ sung | UI `/docs`, JSON `/docs-json`, schema và Try it out cho 5 route |

Năm route sử dụng đường dẫn `/templates`, chưa có tiền tố phiên bản. Xác thực và
phân quyền nằm ngoài phạm vi tuần 2.

## 2. Kiến thức vận dụng và hợp đồng dữ liệu

- API map author_id/category_id/content_json/views_count/forks_count/rating_avg/deleted_at
  theo schema SQL; FK dùng RESTRICT để bảo vệ bản ghi đang được tham chiếu.
- POST cần authorId/categoryId hợp lệ. title tối đa 255, slug 280; không nhận metrics.
- HTML envelope có format/schemaVersion/html và metadata App; ValidateNested từ
  chối field lạ, enum sai, null không cho phép và chuỗi boolean. Giữ Unicode/false.
- categoryId taxonomy SQL khác subjectCategory của App; official/custom là nguồn
  local UI, không phải quyền xuất bản hoặc một category UUID.
- PATCH chỉ sửa field được gửi; contentJson thay cả envelope. authorId bất biến.
- GET/PATCH/DELETE không tăng view ngầm. DELETE soft-delete, slug còn UNIQUE.
- List trả `{data,page,size,totalPages,totalElements}`; detail/POST/PATCH trả object Template.
- synchronize/migrationsRun/installExtensions đều false, không auto thay schema.

Domain entities tuần 1 vẫn độc lập ORM. `TemplateStore` là interface lưu trữ cho API,
tách biệt với `ITemplateRepository` của domain tuần 1. NestJS chọn adapter qua Symbol
token và `useClass`; service sử dụng cùng interface ở cả memory và PostgreSQL.
DTO kiểm tra dữ liệu HTTP, còn UNIQUE/FK của database bảo vệ ràng buộc khi ghi.

Kiến thức phân trang tuần 1 được đưa vào query API: lọc trước khi phân trang, giới
hạn page/size và thêm ID để xác định thứ tự khi các giá trị sắp xếp trùng nhau.

## 3. Môi trường và cách kiểm thử

Node.js **24.21.0**, pnpm **12.11.2**, NestJS **12.3.2**, TypeORM **0.3.31**,
@nestjs/typeorm **12.0.2**, @nestjs/swagger **12.1.0**, PostgreSQL Docker **16.15**.
Postman GUI Linux **12.35.1**, Native Git **Local mode**; CLI chạy collection v2.1
bằng Newman **6.2.3**.

| Kiểm tra | Kết quả |
|---|---|
| pnpm typecheck | Đạt |
| pnpm build (trong các lượt test) | Đạt |
| pnpm install --frozen-lockfile --ignore-scripts | Đạt |
| pnpm test, sau khi bổ sung Swagger | **18 pass, 0 fail, 1 DB test skip mặc định** |
| Swagger UI bằng trình duyệt | Render đủ 5 API; Try it out POST trả **201**, giữ HTML/metadata |
| RUN_DB_TESTS=1 ... pnpm test:api:db | **8 pass, 0 fail** trên DB rỗng riêng |
| Postman collection bằng Newman, memory | **12 request, 30 assertion, 0 lỗi** |
| Postman collection bằng Newman, PostgreSQL | **12 request, 30 assertion, 0 lỗi** |
| Postman GUI thao tác thủ công, Local mode | POST 201; GET 200; PATCH 200; DELETE 204; GET 404; query sai 400 |
| Postman GUI Collection Runner, Functional / Local | **12 request, 30 assertion PASS, 0 FAIL, 0 ERROR** |

Các số test gồm parent/subtest của Node runner, không cộng thành số endpoint.
Memory và postgres dùng cùng HTTP contract: validation, HTML/metadata round-trip,
HTML lớn, payload 413, filters/pagination, giữ field khi PATCH, thay toàn bộ JSONB,
concurrent slug conflict, soft-delete và lỗi. DB suite thêm kiểm tra physical row,
column metadata trước/sau và tạo qua HTTP → đóng writer → mở reader mới vẫn đọc
đúng nội dung. Đây là kiểm thử HTTP/DB, chưa bao gồm tích hợp giao diện App.

Lượt kiểm tra Swagger ngày 04/11/2026 chạy lại typecheck/build, frozen lockfile và
CLI/HTTP memory/OpenAPI. Kết quả DB/Newman/Postman là của lượt thực hành trước đó;
không chạy lại PostgreSQL sau bổ sung Swagger vì Docker daemon chưa sẵn sàng.

Các lượt kiểm chứng dùng DB mới `week2_test_20261103_app_contract`,
`week2_test_20261103_final_contract` và `week2_test_20261103_postman_clean`.
Không reset/seed lại DB nghiệp vụ. DB nghiệp vụ trước/sau có **10 users,
1012 templates**; các DB test đã dọn và API tạm đã dừng sau khi lấy minh chứng.
Các container PostgreSQL/Adminer không bị xóa trong lượt kiểm chứng.

## 4. Minh chứng Postman và đối chiếu DB

[Hướng dẫn Postman chi tiết](docs/09_postman.md) có **15 ảnh chụp thao tác**: collection variables, chọn environment,
chỉnh baseUrl, hover kiểm tra URL, gửi request, Test Results và Runner. Chuỗi thao
tác thủ công dùng UUID `6de7fe79-f8f9-4801-844b-fe66b49881ab`:

| Thao tác | Ảnh |
|---|---|
| Tạo template, nhận ID và HTML envelope | [POST 201](screenshots/01-postman-create.png) |
| Lọc và phân trang | [GET 200](screenshots/02-postman-pagination.png) |
| Cập nhật title, giữ các field khác | [PATCH 200](screenshots/03-postman-update.png) |
| Đọc lại nội dung đã lưu | [GET sau PATCH](screenshots/03b-postman-read-updated.png) |
| Soft delete, body rỗng | [DELETE 204](screenshots/04-postman-delete.png) |
| Đọc lại ID đã xóa | [GET 404](screenshots/05-postman-not-found.png) |
| Query page/size không hợp lệ | [GET 400](screenshots/06-postman-validation.png) |

Sau chuỗi thao tác GUI, đã truy vấn DB test trước khi dọn:

```sql
SELECT id, title, views_count, deleted_at IS NOT NULL AS soft_deleted,
       content_json->>'format' AS format,
       content_json->>'subjectCategory' AS subject
FROM templates
WHERE id = '6de7fe79-f8f9-4801-844b-fe66b49881ab';
```

Kết quả ghi nhận: title=`Updated week 2 planner`, views_count=0,
soft_deleted=true, format=`erusentia-template-html`, subject=`study`. Row còn tồn
tại vật lý dù GET đã 404. Kết quả SQL được ghi nhận trong phiên kiểm chứng, không có
ảnh SQL riêng. ID và timestamp trên ảnh thuộc lần chạy này; khi tái hiện lấy ID từ POST.

Runner GUI có [ảnh cấu hình Local](screenshots/08a-postman-run-configuration.png)
và [bảng kết quả 30 assertion đạt](screenshots/08b-postman-runner-results.png).
Runner tạo bản ghi riêng `a67237e4-00d8-4ad5-96e2-b2d9a6ab6414`, không dùng UUID
của lần thao tác thủ công. Đối chiếu SQL xác nhận cả hai row đã soft-delete, title
đã cập nhật, views_count=0 và HTML/subject metadata được giữ đúng.

Collection/environment mở từ thư mục local bằng Native Git, không import lên
workspace Cloud, không chọn Push/Share. Bản v3 local được sinh từ collection v2.1
bằng `tools/postman/prepare-local.cjs`; script giữ nguyên request, body và assertions,
không gọi mạng. Kết quả Runner GUI và Newman CLI là hai lượt kiểm chứng riêng.

## 5. Vấn đề kỹ thuật và cách xử lý

| Vấn đề | Cách xử lý trong bài thực hành |
|---|---|
| DI cần metadata runtime của constructor | Dùng ts-node khi phát triển và tsc khi build; Symbol làm token cho interface lưu trữ |
| Chuỗi query `false` có thể bị ép sai thành boolean true | Chuyển đổi tường minh query; body chỉ nhận boolean JSON |
| Hai POST có thể cùng sử dụng một slug | Giữ UNIQUE ở DB và ánh xạ SQLSTATE 23505 thành HTTP 409 |
| PATCH cần giữ các field không được gửi | Dùng DTO cập nhật từng phần; riêng contentJson thay toàn bộ envelope |
| Mapping ORM chỉ bao gồm một phần bảng users | Tắt synchronize/migrationsRun/installExtensions, dùng SQL tuần 1 để khởi tạo |

JOIN category giúp tránh truy vấn bổ sung cho từng template. Chưa đo số query hoặc
benchmark N+1 dưới tải; thời gian phản hồi trong ảnh chỉ là số liệu của request đó.
ILIKE phục vụ tìm chuỗi con, chưa phải cơ chế full-text search.

## 6. Cách tái hiện và phần còn ngoài phạm vi

README server hướng dẫn cài/build/run, tạo DB test rỗng và chạy/dọn integration DB.
[Postman](docs/09_postman.md) hướng dẫn Desktop, mở thư mục local, cấu hình variables/
environment, thứ tự request, Runner và CLI. Bộ tests/collection ở server có thể chạy lại.

Chưa có auth/authorization/ownership theo danh tính, public feed bảo vệ dữ liệu,
App adapter gọi API, render HTML qua App, migration rollout, optimistic lock hay
load testing. Seed tuần 1 có thể còn content_json dạng legacy blocks; client cần
kiểm tra format/schemaVersion trước khi sử dụng. API hiện phục vụ quản lý và thực hành local.

Nguồn thực thi được dẫn bằng permalink theo commit tại [snapshot](SOURCE_SNAPSHOT.md),
giữ khả năng xem lại mã nguồn khi nhánh phát triển thay đổi.

## 7. Swagger/OpenAPI và hướng phát triển tiếp theo

Đã bổ sung Swagger UI cơ bản tại `/docs` và đặc tả JSON tại `/docs-json`, cùng
host/port với API. UI hiển thị query, DTO và response của 5 route, hỗ trợ Try it out;
`SWAGGER_ENABLED` điều khiển bật/tắt, mặc định tắt ở production. Cách chạy nằm trong
README server; phần này phục vụ tra cứu và phát triển API về sau.

Bộ ảnh Postman được giữ làm minh chứng của lượt thực hành này. Tuần 3 tiếp tục với
authentication/authorization và xác định tác giả từ danh tính đã xác thực. Tích hợp
App với API là bước riêng sau khi thống nhất quy tắc truy cập và chuyển đổi dữ liệu.
