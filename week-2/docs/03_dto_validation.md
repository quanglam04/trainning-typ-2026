# 03 — DTO, validation và hợp đồng nội dung App

## DTO khác entity thế nào?

DTO xác định dữ liệu qua ranh giới HTTP; entity map dữ liệu lưu trữ. Request DTO
chỉ nhận field client được phép gửi, không dùng model ORM toàn bộ làm body rồi
save thẳng. Response cũng cần projection rõ ràng: API này trả author chỉ gồm id,
category gồm id/name/slug, không query user credentials.

ValidationPipe dùng DTO class và decorator runtime. Type annotation/interface
TypeScript không tự kiểm tra JSON từ mạng. DTO không thay authentication, authorization,
DB constraint hoặc validation nghiệp vụ.

## Cấu hình thực tế

```typescript
new ValidationPipe({
  transform: true,
  whitelist: true,
  forbidNonWhitelisted: true,
  transformOptions: { enableImplicitConversion: false },
})
```

whitelist dựa trên validation decorators. forbidNonWhitelisted làm field lạ bị từ
chối thay vì âm thầm bỏ. transform tạo instance DTO và áp explicit Transform/Type;
không bật chuyển kiểu ngầm. Đặc biệt Boolean("false") là true trong JavaScript nên
không dùng ép Boolean chung cho query. Query isPublic chỉ nhận true/false theo
chuỗi tương ứng; body phải gửi boolean JSON thật.

## CreateTemplateDto

| Field | Quy tắc |
|---|---|
| authorId, categoryId | UUID syntax PostgreSQL, lowercase; tham chiếu tồn tại |
| title | trim, 3..255 ký tự |
| slug | trim, 1..280, lowercase chữ/số với gạch nối |
| description | trim, 1..10000 |
| contentJson | object + ValidateNested + Type(TemplateContentDto) |
| version | tùy chọn, 1..32 |
| thumbnailUrl | tùy chọn chuỗi ≤512 hoặc null |
| isPublic | tùy chọn boolean, mặc định true |
| tags | tùy chọn array ≤20, unique, mỗi chuỗi 1..64 |

UuidPipe nhận UUID dạng `8-4-4-4-12` chữ số hex có dấu gạch nối; chấp nhận chữ hoa
và chuẩn hóa thành chữ thường. Seed tuần 1 được PostgreSQL chấp nhận nhưng không
mang version/variant UUIDv4. API không nhận mọi cách viết UUID mà PostgreSQL hỗ trợ,
chẳng hạn UUID bỏ dấu gạch nối hoặc đặt trong dấu ngoặc nhọn.

## Envelope HTML khớp dữ liệu App hiện tại

`contentJson` bắt buộc format=`erusentia-template-html`, schemaVersion=1, html là
chuỗi có nội dung, tối đa 1.000.000 ký tự. Metadata tùy chọn gồm originId, guide,
iconName, subjectCategory, level, defaultWidth/defaultHeight, fitMode, aspectRatio,
allowHeightResize. Nested field lạ, enum sai hoặc boolean dạng chuỗi trả 400.

subjectCategory nhận study/productivity/math/interactive/visual/finance; level nhận
full/semi/basic; fitMode nhận fit-template/fit-page/fixed. Chúng giữ nghĩa của App,
được lưu riêng với categoryId UUID. Body JSON tối đa 2 MiB.
API chỉ lưu HTML; không thực thi HTML hay kiểm chứng luồng render/sandbox App.

## Absent, null và PATCH

`undefined`/field vắng nghĩa là không gửi. null chỉ hợp lệ với thumbnailUrl. Các
field tùy chọn khác dùng ValidateIf bỏ qua **undefined**, vẫn reject null.
UpdateTemplateDto dùng OmitType loại authorId và PartialType với skipNullProperties=false.
Các mapped types được import từ `@nestjs/swagger` để giữ metadata OpenAPI cùng validation.
Body PATCH rỗng cũng trả 400.

PATCH title giữ các field khác. Nếu gửi contentJson phải gửi **đủ envelope mới**;
server thay cả document JSONB, không merge metadata sâu. Để đổi riêng title không
cần gửi lại contentJson. ID, authorId, timestamps, metrics và featured không được
client cập nhật; cấm field không thay kiểm tra quyền sở hữu khi thêm auth.

## Query và lỗi

page: số nguyên 1..10000; size: 1..100. Transform chỉ chuyển chuỗi chữ số sang number,
không dùng parseInt để chấp nhận ngầm `1abc`/`1.5`. sort/order theo whitelist.
ValidationPipe có thể trả mảng message; filter giữ ở details, message tổng quát.

Bộ HTTP tests dùng cả memory và PostgreSQL kiểm tra null, false, field lạ, HTML lớn,
metadata round-trip, body quá lớn và JSON hỏng. Đây là kiểm chứng validation HTTP,
không phải test App tích hợp.

[Mã nguồn DTO, example và tests](../SOURCE_SNAPSHOT.md).
Nguồn: [Nest validation](https://docs.nestjs.com/techniques/validation),
[class-transformer](https://github.com/typestack/class-transformer),
[class-validator](https://github.com/typestack/class-validator),
[PostgreSQL 16 UUID](https://www.postgresql.org/docs/16/datatype-uuid.html).

[Trước](02_restful_api.md) · [Tiếp: ORM](04_typeorm_entities.md)
