# Snapshot nguồn — Tuần 5

**Ngày chốt nguồn:** 04/11/2026. Nhánh server: `feat/week-5-redis-cache`; nhánh TYP: `week-5`.
Tag lưu trữ báo cáo: `typ-2026-week-5`.

- **Commit server đầy đủ:** `70e02a0ea0504b1eb20d26fe935a90ec54b400a9`.
- [Toàn bộ cây mã nguồn](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/70e02a0ea0504b1eb20d26fe935a90ec54b400a9).
- [Thay đổi của commit](https://github.com/Tunhoclaptrinh/EruSentia-Server/commit/70e02a0ea0504b1eb20d26fe935a90ec54b400a9).
- [ZIP toàn bộ source](https://github.com/Tunhoclaptrinh/EruSentia-Server/archive/70e02a0ea0504b1eb20d26fe935a90ec54b400a9.zip).
- Nền tuần 4: server `4c46b30d4bc87c70d1341e6a45737ad97b988283`, TYP `97131397e86df91aa97552cccf70fe8b639722fb`.
- [Báo cáo tuần 4 lưu trữ](https://github.com/Tunhoclaptrinh/trainning-typ-2026/tree/typ-2026-week-4/week-4).

Các liên kết ghim theo commit đầy đủ; SHA-256 tính từ nội dung file trong commit,
không phải mã Git commit. Commit nguồn và tag báo cáo cần được push để các đường
dẫn GitHub truy cập được. Trong repo local có thể xem bằng `git show SHA:đường/dẫn`.
Nguồn tại mốc này vẫn xem được khi nhánh phát triển thay đổi/xóa file; giữ nguyên
tag và snapshot tuần 1–4, cập nhật về sau dùng mốc mới.

## File nguồn và SHA-256

| File | SHA-256 nội dung đã đối chiếu |
|---|---|
| [README.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/README.md) | `2049029355502955ae7ff4de9adb9adeda562ed7a2504e0929bf69cec672f34a` |
| [package.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/package.json) | `eb5c47e59cb548210c271eb325e3796bf462852542bec07e9bb46abfc991030f` |
| [pnpm-lock.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/pnpm-lock.yaml) | `4dc76f01e1b170fa97933dbf34f3047b3ce002699eaa7f7ebee6b1d038b29f4f` |
| [pnpm-workspace.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/pnpm-workspace.yaml) | `8bc33e4b902aa8182b2fd4f22c498128b7b8475b77427b1cad2f250318231ef2` |
| [.env.example](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/.env.example) | `1a46ecd4e0c110983be363a6b2e9318ff2589a74e826ef6f56f81428d1c10ef9` |
| [docker/docker-compose.cache.yml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/docker/docker-compose.cache.yml) | `e2bf9a1fd11d945a4ca7fa22d0f6a84fa8265604157ea9ac56922db83aaa5658` |
| [sql/07_template_cache_revision.sql](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/sql/07_template_cache_revision.sql) | `021197760d8a776715732e8daba2fb167089283252ca326299cd074f329902e7` |
| [src/cache/template-cache.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/src/cache/template-cache.ts) | `bc0976e7b6c0b6366589d430ac85f2e911ac73b464a3b91333e8eaf8f7bb6daf` |
| [src/config.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/src/config.ts) | `043263015d0ec5ecdcac23bae24087c54f814b550d6c614741f2400a9e5ef201` |
| [src/app.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/src/app.ts) | `688e1afa4d3e4399bd2f6c3b063b1c490c9d5cca7d5b2b8b12c28fd141b922a6` |
| [src/templates/templates.module.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/src/templates/templates.module.ts) | `4882939a6a8a67f59669636b572fe1d160653e99f22c5d8197e682919a00293d` |
| [src/templates/templates.service.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/src/templates/templates.service.ts) | `05e4be0c828ef3f19a0af6dcd49d8f47871dab1428a67a3578847bb53cf166a0` |
| [src/templates/templates.controller.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/src/templates/templates.controller.ts) | `e6ccf807e9e00bf9a99fe22f6fe32da7ab1ff9f39ab763dcef25f55f45ceb9ca` |
| [scripts/redis-types.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/scripts/redis-types.ts) | `97db48a7dd6345db17a05d07052e1c895a8ae4cf347de9fd7b5d929f2ac5a75b` |
| [scripts/benchmark-cache.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/scripts/benchmark-cache.ts) | `35ecaa447d609eabf65dfd7e23e91e8a94ef0320844aec1557a7c0b5ed8085fb` |
| [test/cache.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/test/cache.test.cjs) | `16453f718831a6fc2968ddea893ce0ded6cb9be8e8e9a240f43a4ebe04c39aec` |
| [test/cache.integration.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/test/cache.integration.test.cjs) | `95e156db156e3e53a5e0962d19d9c9bb3014a4e3a93098cd160d498d65edf904` |
| [test/openapi.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/test/openapi.test.cjs) | `390c3cb3a07c3856501bc5450e4baa17a204651f78d999f39a6591252bbd547b` |
| [docs/CACHE.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/docs/CACHE.md) | `eb817edea9ab2dc189ba0e139bb56024536687ca92d8c46fe8a98abeb03e7bf4` |
| [docs/TEMPLATE_API.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/docs/TEMPLATE_API.md) | `2932503d35b8c9926d9f7c05238788b2459eb0a0f647f79fa5c1d942a07491e9` |
| [docs/DATABASE_DESIGN.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/docs/DATABASE_DESIGN.md) | `60efab7cf35f54d6d6b456b579d10ab318327380967b9f4489c380e3236f7b94` |
| [docs/COMMANDS_AND_CHEATSHEET.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/docs/COMMANDS_AND_CHEATSHEET.md) | `d8b98ed087427c8a2fa77e42360f5dd6b5b745b05005241c2a0a0d5bb5adb7e5` |
| [docs/TRAINING_REFERENCES.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/70e02a0ea0504b1eb20d26fe935a90ec54b400a9/docs/TRAINING_REFERENCES.md) | `eb11f322aee7e2f773c19c361bf5e118d72b2798823e7b76cab17d42abc004db` |

## Hợp đồng kế thừa và tái hiện

- [Nguồn tuần 4](../week-4/SOURCE_SNAPSHOT.md) giữ auth/MQ/CRUD trước cache.
- [Báo cáo](WEEK5_REPORT.md) ghi kiểm thử, số đo và giới hạn.
- [Hướng dẫn Swagger](docs/07_swagger_demo.md) có 5 ảnh; [benchmark](docs/06_performance.md) nêu phương pháp và kết quả lượt cuối.
- Cache integration cần DB rỗng week5_test_*; benchmark/type demo dùng DB test đã chuẩn bị.
- SQL schema/index/seed và migrations tuần trước, App, đề gốc không được sửa để làm demo cache.
- Không lưu .env, JWT/password thật, raw execution dump hoặc script chụp tạm vào TYP.
