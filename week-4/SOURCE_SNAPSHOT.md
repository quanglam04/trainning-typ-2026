# Snapshot nguồn — Tuần 4

**Ngày chốt nguồn:** 04/11/2026. Nhánh server: `feat/week-4-message-queue`; nhánh TYP: `week-4`.
Tag lưu trữ báo cáo: `typ-2026-week-4`.

- **Commit server đầy đủ:** `4c46b30d4bc87c70d1341e6a45737ad97b988283`.
- [Toàn bộ cây mã nguồn](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/4c46b30d4bc87c70d1341e6a45737ad97b988283).
- [Thay đổi của commit](https://github.com/Tunhoclaptrinh/EruSentia-Server/commit/4c46b30d4bc87c70d1341e6a45737ad97b988283).
- [ZIP toàn bộ source](https://github.com/Tunhoclaptrinh/EruSentia-Server/archive/4c46b30d4bc87c70d1341e6a45737ad97b988283.zip).
- Nền tuần 3: server `d5c845891a28094b7fc935573509e904d4c8b09b`, TYP `6ed84fc37b7ee41964bdcccc22ba9783d6f13b65`.
- [Báo cáo tuần 3 lưu trữ](https://github.com/Tunhoclaptrinh/trainning-typ-2026/tree/typ-2026-week-3/week-3).

Các liên kết dưới ghim theo commit, mở độc lập với vị trí hai repo trong workspace.
SHA-256 tính từ nội dung file trong commit, không phải mã Git commit. Nguồn tại mốc
này vẫn xem được khi nhánh phát triển thay đổi hoặc xóa file. Giữ nguyên tag lưu trữ
và các snapshot tuần 1–3; cập nhật về sau dùng mốc mới.

## File nguồn và SHA-256

| File | SHA-256 nội dung đã đối chiếu |
|---|---|
| [README.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/README.md) | `ac97e4067ec7b51316475c86a27effb974a08d303fd1e2a1cbae323ac9a83668` |
| [package.json](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/package.json) | `b27d89b30d1b138d9ac3ce10c21cf67d02cfb593da9cc684198ce1df3a5f1eb9` |
| [pnpm-lock.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/pnpm-lock.yaml) | `bb40ac385bda6858bf20f55b4621d95008ac94ac2d6fa4a5aa981a58b3c2ecc5` |
| [pnpm-workspace.yaml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/pnpm-workspace.yaml) | `8bc33e4b902aa8182b2fd4f22c498128b7b8475b77427b1cad2f250318231ef2` |
| [.env.example](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/.env.example) | `21c78c9b77cd1a153790d84170463a221de2f32439d78b0788be87c47530e0e3` |
| [docker/docker-compose.queue.yml](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/docker/docker-compose.queue.yml) | `ca57c5734c10ef13e74a3b865d4ffe399933eb43439a6ddd6b810923872b1e78` |
| [sql/06_message_queue.sql](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/sql/06_message_queue.sql) | `4a6c63975e4c7b96ad99271c452d63b7c35a4d5469a1f3d60020234ec30d222d` |
| [src/config.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/config.ts) | `47d492d5942bc3266001ef2676a294a8c6692fb7f4e434e5927b956941df9f79` |
| [src/auth/auth.module.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/auth/auth.module.ts) | `833667619cf26bfce4ba7c67a193ad07cf927870a859023c0ed0d76466da63ed` |
| [src/auth/persistence/postgres-auth.store.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/auth/persistence/postgres-auth.store.ts) | `fb36a3ddd0f4dfb6ebb0037ef27f5ac96ca57d019751b1d5bf3ce27550677983` |
| [docs/MESSAGE_QUEUE.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/docs/MESSAGE_QUEUE.md) | `146dfbab1dbba943104bf844da56fae7f59667b5fdf2c5f0e06b59d2e95ebd47` |
| [docs/AUTHENTICATION.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/docs/AUTHENTICATION.md) | `db97d353218286b36f21e68a20ce5c18d73707e2a89953ee65643ee3edd83728` |
| [docs/DATABASE_DESIGN.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/docs/DATABASE_DESIGN.md) | `2ea0e09053005515fc0e14a4ae239f97c7237ef4151e2a8246ca2af0f5c3b1e5` |
| [docs/COMMANDS_AND_CHEATSHEET.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/docs/COMMANDS_AND_CHEATSHEET.md) | `a3a2eb5bb4a26925e0fda75a41951b9e05ee45c2355fd66de08966d11d1c77e2` |
| [docs/TRAINING_REFERENCES.md](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/docs/TRAINING_REFERENCES.md) | `b59ffecf20da7351a006776b4ba3bd7dc84c7f6ac5954e65c40d31e9d732e974` |
| [test/messaging.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/test/messaging.test.cjs) | `d1dbf47e1ba45d7f9ab75f4c0ba7ab009555703f0fc7d9e3616b896524faf189` |
| [test/messaging.integration.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/test/messaging.integration.test.cjs) | `3c676707c8bef08cac3a1399035590438bbd5ba48e5c9fdf9d2abd6677902a24` |
| [test/cli.test.cjs](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/test/cli.test.cjs) | `935c71368f67b907313e9837dfa15f40d9b3e6dc24938c22814f3b02c1208a50` |
| [src/messaging/rabbit.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/messaging/rabbit.ts) | `f02c08f89269b490ee944c5edfeeeaea139f21d91700059ba722c45c279b0bcd` |
| [src/messaging/registration-event.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/messaging/registration-event.ts) | `8b2cdfcfb0fb00333f2fbed4039a881b0dd55e508efde201ff65e456787581d5` |
| [src/messaging/welcome-mailer.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/messaging/welcome-mailer.ts) | `797b705950677bd9b552235bba9e6db9d4cfa9962f1dc3ac8ac13050f0964536` |
| [src/messaging/welcome-repository.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/messaging/welcome-repository.ts) | `a33ecbf7d59270f086f348d47bbdd9b016e09bd5ec2e4de4b7249529419fe837` |
| [src/messaging/welcome-worker.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/messaging/welcome-worker.ts) | `2d192272e7cc91ba5768cbc0d26b1720bdd9e06e63c0692caf7dae627b0df73c` |
| [src/messaging/worker.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/4c46b30d4bc87c70d1341e6a45737ad97b988283/src/messaging/worker.ts) | `4ff254ca0218e0ac78567cb90c08e5dcddd63d1d373f97744357ad532debeec9` |

## Hợp đồng kế thừa

CRUD/JWT/refresh được kế thừa từ [snapshot tuần 3](../week-3/SOURCE_SNAPSHOT.md).
Chỉ các file đã thay đổi cho MQ được đối chiếu thêm ở bảng trên; SQL 01/02/03/05,
API Template, App và đề gốc không được viết lại để làm demo MQ.

## Tái hiện

- [Báo cáo](WEEK4_REPORT.md) nêu kết quả kiểm thử và giới hạn SMTP/idempotency.
- [Thực hành](docs/07_demo_and_evidence.md) có 6 ảnh và cách quan sát từng bước.
- README/MESSAGE_QUEUE phía server có lệnh migration, start worker và DB test riêng.
- Không đưa `.env`, token, mật khẩu thật, raw execution dump hoặc script chụp tạm vào snapshot.
