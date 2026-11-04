# 01 — Framework và kiến trúc NestJS

## Framework giải quyết gì?

Framework cung cấp cấu trúc và vòng đời ứng dụng: nhận HTTP, route, resolve dependency,
validate và xử lý lỗi. Ứng dụng thường chủ động gọi thư viện; framework điều phối và
gọi mã ứng dụng tại các điểm mở rộng (IoC). Quy tắc nghiệp vụ do ứng dụng định nghĩa.
NestJS chạy trên Node.js, dùng TypeScript và metadata decorator; dự án dùng Express
adapter. Với Express, lập trình viên có thể tổ chức các lớp tương tự hoặc bổ sung
DI container; NestJS cung cấp sẵn cấu trúc module và cơ chế đăng ký provider.

## Cấu trúc đang chạy

```text
main.ts → readConfig → createApplication
                         └─ AppModule.register
                              ├─ TypeORM root (chỉ postgres)
                              └─ TemplatesModule.register
                                  ├─ TemplatesController
                                  ├─ TemplatesService
                                  └─ TEMPLATE_STORE
                                      ├─ MemoryTemplateStore
                                      └─ PostgresTemplateStore
```

- Module khai báo controller/provider và quan hệ import/export của DI container.
- Controller ánh xạ method/URL và chuyển DTO sang service.
- Service giữ quy tắc chung: không PATCH rỗng, 404 nếu không có tài nguyên.
- Store thực hiện lưu trữ, lọc, phân trang, kiểm tra tham chiếu và slug trùng.
- DTO mô tả dữ liệu được nhận và validation runtime; TypeScript interface riêng
  không tồn tại ở runtime để ValidationPipe đọc decorator.

Hai adapter phục vụ **cùng đối tượng Template và cùng 5 route**. Chọn bằng
`TEMPLATE_STORE=memory|postgres`, không chạy hai backend và không đổi client contract.
Memory mất dữ liệu khi restart, khởi đầu không có template; DB giữ dữ liệu. Memory
có author/category mẫu, còn DB kiểm tra bảng thật.

## Dependency Injection và IoC

`TEMPLATE_STORE` là Symbol token runtime. `TemplateStore` là interface TypeScript,
bị xóa khi biên dịch, nên không thể dùng interface đó một mình làm token runtime.
Module bind token với useClass theo cấu hình; service chỉ biết các phương thức của
port. `@InjectRepository` inject TypeORM repository cho adapter PostgreSQL.

Ví dụ trích từ phần đăng ký provider, với `store` là cấu hình đã được kiểm tra:

```typescript
{
  provide: TEMPLATE_STORE,
  useClass: store === "postgres" ? PostgresTemplateStore : MemoryTemplateStore,
}
```

Service nhận dependency bằng `@Inject(TEMPLATE_STORE)` trong constructor. Đổi cấu
hình adapter không yêu cầu đổi controller hoặc các phương thức gọi từ service.

Provider mặc định dùng singleton theo đăng ký module. Request scope tạo instance
theo request và có thể lan qua chuỗi phụ thuộc; transient tạo instance riêng cho
consumer. Không có tỷ lệ chậm cố định áp dụng mọi ứng dụng; cần đo khi chọn scope.
Hai store ở bài này dùng mặc định, không giữ thông tin người dùng theo request.

## Một request đi qua đâu?

1. Express đọc JSON, giới hạn body 2 MiB.
2. Nest tìm controller/route; global ValidationPipe biến đổi và validate DTO.
3. UUID pipe kiểm tra path ID; controller gọi service.
4. Service gọi store; postgres store sinh query có bind parameters.
5. Kết quả thành công được serialize thành JSON; DELETE 204 không body.
6. Exception được global ApiExceptionFilter đổi sang error envelope.

Luồng trên mô tả các thành phần đang sử dụng. Nest còn hỗ trợ middleware, guard và
interceptor; bài thực hành chưa đăng ký auth guard hay success wrapper interceptor.

## Bootstrap và cấu hình

`main.ts` nạp dotenv, đọc HOST, kiểm tra PORT/TEMPLATE_STORE rồi listen. Mặc định trong code
là 127.0.0.1:3001; giá trị có sẵn trong environment/.env có thể thay đổi cổng.
`pnpm dev` dùng ts-node để phát decorator metadata; `pnpm build` dùng tsc và
`pnpm start` chạy JavaScript đã build. DB check độc lập ở `pnpm db:check`.
Postgres mode tắt synchronize/migrationsRun/installExtensions: schema được chuẩn bị
bằng SQL, bootstrap không tự sửa DB.

## Ranh giới với kiến trúc tuần 1 và App

Domain entities tuần 1 vẫn độc lập ORM. Catalog TemplateStore mới phục vụ projection
HTTP và quan hệ, tách biệt với ITemplateRepository domain tuần 1. App vẫn dùng
template HTML/localStorage; tuần 2
không sửa App và chưa kiểm thử tích hợp UI với API.

Đọc [mốc nguồn và từng file](../SOURCE_SNAPSHOT.md), [hướng dẫn chạy](00_GUIDE_INDEX.md#chạy-thực-hành-erusentia).
Tham khảo: [Nest modules](https://docs.nestjs.com/modules),
[custom providers](https://docs.nestjs.com/fundamentals/custom-providers),
[injection scopes](https://docs.nestjs.com/fundamentals/injection-scopes).

[Tiếp: RESTful API](02_restful_api.md)
