# Snapshot nguồn — Tuần 2

**Ngày chốt nguồn:** 04/11/2026. Nhánh server: `feat/week-2-template-api`; nhánh báo cáo TYP: `week-2`.

- **Commit server đầy đủ:** `b9017f77f6b97a4935fd72d22f2e149f32c4b649`.
- [Toàn bộ cây mã nguồn](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b9017f77f6b97a4935fd72d22f2e149f32c4b649).
- [Thay đổi của commit](https://github.com/Tunhoclaptrinh/EruSentia-Server/commit/b9017f77f6b97a4935fd72d22f2e149f32c4b649).
- [ZIP toàn bộ source tại mốc này](https://github.com/Tunhoclaptrinh/EruSentia-Server/archive/b9017f77f6b97a4935fd72d22f2e149f32c4b649.zip).

HTTP/DB/Postman được kiểm chứng ngày 03/11/2026. Swagger được bổ sung và kiểm tra
ngày 04/11/2026 bằng typecheck/build, frozen lockfile, HTTP memory/OpenAPI và trình
duyệt. Kết quả DB/ảnh Postman thuộc lượt trước khi bổ sung Swagger; không phải lượt
chạy lại DB trên commit này. Chi tiết nằm trong [báo cáo](WEEK2_REPORT.md).

## File nguồn và SHA-256

Các liên kết dưới ghim theo commit, mở độc lập với vị trí hai repo trong workspace.
SHA-256 được tính từ nội dung file trong commit, không phải mã Git commit.

| File thực thi / tài liệu server | SHA-256 nội dung đã đối chiếu |
|---|---|
| [README.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/README.md) | `b5675dc508af8842cf3334b41554221024c24ff14c9a464009e250b051ce2d02` |
| [package.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/package.json) | `95ab858f133c7678eaa19d103fd0980c590006984ea068eea70b12f198a23d2b` |
| [pnpm-lock.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/pnpm-lock.yaml) | `49327301e178108c5bc38c79a0d256d183e36d6a3ca6f842db9c09b6da30b3bb` |
| [pnpm-workspace.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/pnpm-workspace.yaml) | `8bc33e4b902aa8182b2fd4f22c498128b7b8475b77427b1cad2f250318231ef2` |
| [.env.example](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/.env.example) | `6244161c3d1e0e65d0886d11d6d65ae1a85a599f78b202278a37825a10f96150` |
| [src/main.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/main.ts) | `d413cdbce2b5d4d77de6a0c356e9a04379270e4a0d145a10379fa313a751642b` |
| [src/app.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/app.ts) | `a50903954d62414a6db3c2dd54cde8f9e375279381672cba6f2406a7b8ce63e3` |
| [src/config.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/config.ts) | `754294aaac8dcf3a1deb3a919d854ffce4e4a6c126e2feebe1b469b319890976` |
| [src/openapi.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/openapi.ts) | `ea8ea86a037d363eb26ba509bfcfa81f2605ca9179c12a801b6e216226db5338` |
| [src/common/uuid.pipe.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/common/uuid.pipe.ts) | `67f31cb8534811aa3f81673892de2ac4928466fbf79dbb853cc7b114c12342b2` |
| [src/common/api-exception.filter.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/common/api-exception.filter.ts) | `c671842cf2089a761df98a76ea75d26edf29611daa79263dd56ab087560c76d8` |
| [src/templates/templates.module.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/templates.module.ts) | `c15a0ce54116b4a35cd446060bf299942d83f257d22342f68097b978ffe364e2` |
| [src/templates/templates.controller.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/templates.controller.ts) | `c98ed17f82d675358599f5876b868bf04fc4b0708f5ac498b6d33474cf3587f7` |
| [src/templates/templates.service.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/templates.service.ts) | `2011a578ec68895c4e2f375b5f7395f971524b5905fe9551736822f2ae24df6a` |
| [src/templates/template-store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/template-store.ts) | `aa10439a30464015aa3160fb33f07b5fcf78923cca5ec280737eb70975f4a1e1` |
| [src/templates/dto/template.dto.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/dto/template.dto.ts) | `187da181217a72f59b5009bdfa4d6d7c8ed87c7a312b4ee247e16f5d349d1a1a` |
| [src/templates/dto/template-content.dto.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/dto/template-content.dto.ts) | `a0025e5a1e0b762149592cf0f60e88b675e2a0d57ab85db9e73a357d76ec29e4` |
| [src/templates/dto/template-response.dto.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/dto/template-response.dto.ts) | `2e81a730f2ad935fd5c22f5d3fe1b086d331f51c4ad545592942eaa33fc0f2fe` |
| [src/templates/persistence/memory-template.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/persistence/memory-template.store.ts) | `208942f8d6b40e84f291e8378709e4df34ff97acfd88ec1a4379bfed07db98b0` |
| [src/templates/persistence/postgres-template.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/persistence/postgres-template.store.ts) | `9440600f21a5b7787d7347183b53aec01f48bd01bd3fe6fa8e631cfdb2f81526` |
| [src/templates/persistence/entities.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/templates/persistence/entities.ts) | `1ffe517cc8422b2f1c9a2267ab9e8b463d0278fd8f98d88c1c38fdd348b5945d` |
| [scripts/check-db.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/scripts/check-db.ts) | `6f63ed8705c62c1e2c90ad771513289e917600c3cba40d1604d6c49833f6a88d` |
| [test/cli.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/test/cli.test.cjs) | `de7a6ab0a61ceecc22c983e2c99621c53dcabfa505bd8d11e8339cacfd3de3f1` |
| [test/openapi.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/test/openapi.test.cjs) | `330479be34c0e85feae76509422afd6961d0c6f707b6f1ce51c48eaa0d06a71e` |
| [test/templates.api.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/test/templates.api.test.cjs) | `f525f7e0434fbdc5203e1c5e98d8e0757f1bf3fe71e50620392b6a7e21823b93` |
| [test/templates.contract.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/test/templates.contract.cjs) | `473b6058413260d2a74357d0f765bd7b434c2b9104d33aa5e62a5a1c74b5c9a7` |
| [test/templates.postgres.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/test/templates.postgres.test.cjs) | `0c326d3c348d1ce43aa6fb6388e7747bd3851056a80d9f923d45ff5512d80ca2` |
| [tools/examples/create-template.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/tools/examples/create-template.json) | `9fc4962cb055d3338cbf937a72a0b418de6be9fe2347a40d53fe117d7e8d8772` |
| [tools/postman/templates.postman_collection.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/tools/postman/templates.postman_collection.json) | `3dce44898dd0def1023b393b55ab8a12131a09a6b87a872524213d09bb25e06c` |
| [tools/postman/erusentia-local.postman_environment.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/tools/postman/erusentia-local.postman_environment.json) | `79a1eb5ae622e83995a352978bc8ad6bcad8e0ef50031221819b81f0bbdcba30` |
| [tools/postman/prepare-local.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/tools/postman/prepare-local.cjs) | `894a1b48b89256041fa5843d6c86f9d039943b7d9da17f2cf405b3012dc2c187` |
| [docs/TEMPLATE_API.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/docs/TEMPLATE_API.md) | `cf222d2c004e38d8dd8a3c18c768bb8984e0516da7fc0ee0dc1523e3ea9ef932` |
| [docs/APP_TEMPLATE_CONTRACT.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/docs/APP_TEMPLATE_CONTRACT.md) | `079d4bcb2a9c3fac745dcef87a23175f90486fd530106e550af013fd5f7e195a` |
| [sql/01_schema.sql](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/sql/01_schema.sql) | `64d854c7c643a25f17cc2b1189406b5cae1ce729c4b001b4429b0ab05c34594e` |
| [sql/02_indexes.sql](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/sql/02_indexes.sql) | `07bc689807bf0a9050f4af17006e65849708a146bca7e3515ba600c1d9bd6c5b` |
| [sql/03_seed.sql](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/sql/03_seed.sql) | `fbf3d6f2b5005066eaaabcab2d5b101a08e4ce47f73ff9eb9af5ff554c75475f` |
| [src/week01-practice/oop/demo.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b9017f77f6b97a4935fd72d22f2e149f32c4b649/src/week01-practice/oop/demo.ts) | `c208ae6db00715c1306fe608f8a4cfe30589f934482257b8cbf62d31db5755f7` |

## Phần nguồn đã có mốc Git bất biến

HEAD nền server trước tuần 2: `b851e1576ae48fe62efcec10dd2d912e011a6220`. Có thể mở SQL tuần 1 ở commit này:

- [sql/01_schema.sql tại SHA nền](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/01_schema.sql).
- [sql/02_indexes.sql tại SHA nền](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/02_indexes.sql).
- [sql/03_seed.sql tại SHA nền](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/03_seed.sql).

Schema/seed/index không bị đổi trong lần tuần 2 này. Tag tuần 1 giữ nguyên.

## Giữ và cập nhật snapshot

Mốc mới dùng commit mới; giữ nguyên các tag tuần 1. Các permalink phía trên tiếp tục
mở file ở thời điểm chốt dù nhánh phát triển sửa hoặc xóa file đó. Khả năng truy cập
phụ thuộc việc remote còn giữ commit và quyền đọc repo. ZIP/bundle có thể được lưu
làm bản sao độc lập. Website bên ngoài chỉ được dẫn nguồn, không được đóng băng theo commit này.

## Đối chiếu với App riêng tư

Đã đọc model Template, DefaultTemplate và hook thư viện hiện tại; không sửa App,
không copy HTML/template sản phẩm hoặc tài liệu riêng sang repo nộp bài. Hợp đồng
và bảng ánh xạ ở `docs/APP_TEMPLATE_CONTRACT.md` server, gồm các path local để chủ dự
án tự đối chiếu. Không tạo link công khai tới source App riêng tư.

[Báo cáo](WEEK2_REPORT.md) · [Mục lục](docs/00_GUIDE_INDEX.md).
