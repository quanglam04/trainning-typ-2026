# Snapshot nguồn — Tuần 3

**Ngày chốt nguồn:** 04/11/2026. Nhánh server: `feat/week-3-auth`; nhánh TYP: `week-3`.
Tag lưu trữ báo cáo: `typ-2026-week-3`.

- **Commit server đầy đủ:** `d5c845891a28094b7fc935573509e904d4c8b09b`.
- [Toàn bộ cây mã nguồn](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/d5c845891a28094b7fc935573509e904d4c8b09b).
- [Thay đổi của commit](https://github.com/Tunhoclaptrinh/EruSentia-Server/commit/d5c845891a28094b7fc935573509e904d4c8b09b).
- [ZIP toàn bộ source](https://github.com/Tunhoclaptrinh/EruSentia-Server/archive/d5c845891a28094b7fc935573509e904d4c8b09b.zip).
- Nền tuần 2: server `b9017f77f6b97a4935fd72d22f2e149f32c4b649`, TYP `bafe279cb9c397210bb965306983a5ff607eff55`.

Các liên kết dưới ghim theo commit, mở độc lập với vị trí hai repo trong workspace.
SHA-256 được tính từ nội dung file trong commit, không phải mã Git commit. Các file
vẫn có thể xem tại mốc này khi nhánh phát triển thay đổi hoặc xóa chúng. Giữ nguyên
tag lưu trữ và các snapshot tuần 1–2; cập nhật về sau dùng mốc mới.

## File nguồn và SHA-256

| File | SHA-256 nội dung đã đối chiếu |
|---|---|
| [README.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/README.md) | `58ce950e470ef09f5aceada94a297fe96010249d4194512bfef16902b0819b72` |
| [package.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/package.json) | `8481efc0ad34f8bb08c5f9fcababfb640a900f9585840860ed5dde3fe568271d` |
| [pnpm-lock.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/pnpm-lock.yaml) | `f008c1ab1c0ca5b2cd623fa6a8d7776a4f906b73e3819b867d008c89b121f334` |
| [pnpm-workspace.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/pnpm-workspace.yaml) | `8bc33e4b902aa8182b2fd4f22c498128b7b8475b77427b1cad2f250318231ef2` |
| [.env.example](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/.env.example) | `dc08f3a6944979a45cf47ab00e4038a49c34f09923fe145af721751601826f07` |
| [src/app.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/app.ts) | `554b696307775a100342640bf30d5a519c980b6287b50f4f0ee08dc01fbe46f2` |
| [src/main.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/main.ts) | `86baca408250c6b150d9a1656c020b88078e7a8153db8e4b945df64a5d406934` |
| [src/config.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/config.ts) | `9f95f42ec6cc7d955e117872424efa929dcbf05665300f49558e2d6a58161cd9` |
| [src/openapi.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/openapi.ts) | `6ca4e0b2b23961a748de9c3e2ef1db912acc1a183704625b8ee28352e57254ad` |
| [src/common/api-exception.filter.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/common/api-exception.filter.ts) | `3d330b603d7fed13c5465ad092a80cf6fdfcb5c289a4442e623dfc8de1668eb3` |
| [scripts/create-admin.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/scripts/create-admin.ts) | `de77d789b9ff41a57182c99b0f43743eeb54e1fb9e0a743e662a3b37522e726d` |
| [sql/05_auth_sessions.sql](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/sql/05_auth_sessions.sql) | `785337f68031599e52973aa61ae8a989022f371a3929c5de3d25ecf5817a839a` |
| [docs/AUTHENTICATION.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/docs/AUTHENTICATION.md) | `7af184b5fc33b7a321f18a0ed601dc62881ad80eb47d5a66b9dc49acdfcec5b7` |
| [docs/TEMPLATE_API.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/docs/TEMPLATE_API.md) | `bc4a4d1c2ac8269ec5e46e9e8f5e23a2be68e829ee0e4ea89dc9f043d73e8c12` |
| [docs/APP_TEMPLATE_CONTRACT.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/docs/APP_TEMPLATE_CONTRACT.md) | `4f5be640fd51cde013f145d1e70492ed179d2d8f2058536215547d8ab80ebb38` |
| [src/auth/auth-store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/auth-store.ts) | `c8f2c46700d0de0d5cc2bbd72d10d9b6b848478c9c0def78a4dafbb6ad34eced` |
| [src/auth/auth.controller.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/auth.controller.ts) | `e8f6603a7cf4a06b571f4bce914d827ed9d5edad181f7c09ca411e375844a86c` |
| [src/auth/auth.dto.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/auth.dto.ts) | `25c0c2b51ae590f3cce6d23fd3464a7d2ea0f61ecf62711e520ae2158b106a7e` |
| [src/auth/auth.guards.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/auth.guards.ts) | `3756027c0d27dcca476843add41cab9f5174ab57aa5c669c40b9f956c5971d30` |
| [src/auth/auth.module.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/auth.module.ts) | `27249efe299fce1051904fdb2b1c347cd5401057bcbd6083685266ca52a8a7ca` |
| [src/auth/auth.service.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/auth.service.ts) | `520113f5d1542a8de4e45839706281e575d91e906ba82fe0ad6a8813201b2531` |
| [src/auth/passwords.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/passwords.ts) | `a6e9834775988eff8320adeefa12ed4289b6e5b15277ceeb450e695a9a663300` |
| [src/auth/persistence/memory-auth.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/persistence/memory-auth.store.ts) | `1b11f59ddc463af496d448e76774f12e9a38795b8e60896e010dc0aedcb6de8b` |
| [src/auth/persistence/postgres-auth.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/persistence/postgres-auth.store.ts) | `2f3ebdaed32020b1a7ed6a50e6feba48a02d3077d0e6007c7d1504086053425b` |
| [src/auth/persistence/refresh-session.entity.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/auth/persistence/refresh-session.entity.ts) | `9d413678a3fd336632382b7970c2f11b9a2c3ca731e02a96fddd98f9464a0581` |
| [src/templates/templates.controller.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/templates.controller.ts) | `2a1ac40750f0925d74b9a88fe43ba100563c6e575a7920160b975d68c3d612a2` |
| [src/templates/templates.service.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/templates.service.ts) | `aaba4e350553c93fd46bc406dcce2253deb833b106fcba8d442cf126bf2b1e29` |
| [src/templates/template-store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/template-store.ts) | `8c701cb66c6c43db68f1a65a18f12f7c0d88fffe1f909cb3599e457786395da8` |
| [src/templates/dto/template.dto.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/dto/template.dto.ts) | `c848dd546dc3c3ef790de2940c9e9406a1ac7b443087829e2dd0848c1434c1b3` |
| [src/templates/persistence/entities.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/persistence/entities.ts) | `5855c6cab312c48eba186e6140aacbe17b891a28ba564289229f0ffae01fe73b` |
| [src/templates/persistence/memory-template.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/persistence/memory-template.store.ts) | `a5aed0ddcdf361458eff6e192d2c568ac58482c55795e754a68f8c67459a5b0e` |
| [src/templates/persistence/postgres-template.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/src/templates/persistence/postgres-template.store.ts) | `8c41d4b7383108da708fce087b40b8d7f75628830383dab6bea1d7b0283c278f` |
| [test/auth-config.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/auth-config.test.cjs) | `e877fee8187333e5437863b999959be7005f30bb266ad5455c74684f298f5354` |
| [test/auth.api.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/auth.api.test.cjs) | `09bf9c578ebbd68e1c058defe7ac4af6ae4474c179de88e3429e04fa710277b4` |
| [test/auth.contract.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/auth.contract.cjs) | `6c7d86c6da6bd81c031b856bc8bbba9b0ba9f3cbadc5b2e1cb9ca7455388f6f8` |
| [test/templates.contract.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/templates.contract.cjs) | `ba0d644a0151e612d66393e66f1f441a288ccbf669f6d474737cc25343757bdc` |
| [test/templates.postgres.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/templates.postgres.test.cjs) | `c512d8b60c9d5e62462e77a9b62925f8788adf6415d32df1ad884a0ebeb2fc5c` |
| [test/openapi.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/openapi.test.cjs) | `c3df84a979121dec42dc7cc716ea5a491b200feb53452cc0a2e2dcf190702f31` |
| [test/cli.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/test/cli.test.cjs) | `3fa9fc6e6e3c8dd81d431498d1c57eda1470f1ecd8998aa0d4095e1bb62d0b35` |
| [tools/examples/create-template.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/tools/examples/create-template.json) | `053f6dbd7a8072a7882f4f0e09b7694123b7416b8d95f3d4bf36962b96ab2e6d` |
| [docs/TRAINING_REFERENCES.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/d5c845891a28094b7fc935573509e904d4c8b09b/docs/TRAINING_REFERENCES.md) | `d5781f631856f81aae37d41b796394bb8507add01a4ef2368774c3bb101c9d2a` |

## Đối chiếu và tái hiện

- [Báo cáo](WEEK3_REPORT.md) ghi kết quả và giới hạn kiểm chứng.
- [Hướng dẫn Swagger](docs/07_swagger_practice.md) có thao tác thực thi và ảnh.
- README server hướng dẫn `pnpm test`, `pnpm test:api:db`, migration và CLI admin.
- PostgreSQL tests chỉ nhận database riêng rỗng có tiền tố `week3_test_`; không reset DB nghiệp vụ.
- SHA trên đối chiếu mã nguồn, không bao gồm `.env`, credential hoặc nội dung token.
