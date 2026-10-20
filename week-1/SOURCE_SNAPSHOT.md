# Mốc nguồn cố định — Tuần 1

Mốc này được chốt sau đợt dọn repo và rà soát tài liệu ngày 20/10/2026. Báo cáo ngày 03/10/2026 vẫn giữ các ảnh và số đo lịch sử; kết quả kiểm tra sau dọn repo nằm tại [CLEANUP_CHECKS.md](CLEANUP_CHECKS.md). Mốc nguồn này không đổi ngày hoặc nguồn gốc của các kết quả lịch sử đó.

Bản tài liệu hiện tại trên nhánh có [hiệu đính review](REVIEW_UPDATES.md). Tag `typ-2026-week-1` lưu bản **trước hiệu đính** và không được di chuyển; các dẫn chứng mã nguồn backend vẫn dùng commit cố định dưới đây.

## Backend tại thời điểm chốt

| Thành phần | Mốc cố định |
|---|---|
| Repo | `Tunhoclaptrinh/EruSentia-Server` |
| Commit đầy đủ | `b851e1576ae48fe62efcec10dd2d912e011a6220` |
| Tag dễ nhớ | `typ-2026-week-1` |
| Toàn bộ cây file | [Xem toàn bộ backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220) |
| Nội dung commit | [Xem thay đổi của commit](https://github.com/Tunhoclaptrinh/EruSentia-Server/commit/b851e1576ae48fe62efcec10dd2d912e011a6220) |
| Bản tải xuống | [ZIP toàn bộ backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/archive/b851e1576ae48fe62efcec10dd2d912e011a6220.zip) |
| SQL | [Schema, indexes, seed và bài benchmark](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220/sql) |
| Seed thực thi | [scripts/seed.ts](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/scripts/seed.ts) |
| Demo | [OOP](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/src/week01-practice/oop/demo.ts), [DI](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/src/week01-practice/di/demo.ts) |

Link có mã commit đầy đủ trỏ tới đúng phiên bản file ở thời điểm đó. Việc sửa hoặc xóa SQL/seed bằng commit mới trên nhánh phát triển không thay đổi cây file của commit đã chốt. Có thể mở thư mục cha hoặc toàn bộ repo bằng link `tree/<SHA>` phía trên.

## Báo cáo và ảnh trong TYP

- [Toàn bộ repo TYP tại tag tuần 1](https://github.com/Tunhoclaptrinh/trainning-typ-2026/tree/typ-2026-week-1).
- [Báo cáo tuần 1 đã chốt](https://github.com/Tunhoclaptrinh/trainning-typ-2026/blob/typ-2026-week-1/week-1/WEEK1_REPORT.md).
- [Tài liệu và ảnh minh chứng đã chốt](https://github.com/Tunhoclaptrinh/trainning-typ-2026/tree/typ-2026-week-1/week-1).
- [ZIP toàn bộ TYP tại tag tuần 1](https://github.com/Tunhoclaptrinh/trainning-typ-2026/archive/refs/tags/typ-2026-week-1.zip).

Các liên kết tương đối trong bản báo cáo mở từ tag tiếp tục dẫn tới tài liệu và ảnh ở cùng mốc đó. Mã commit cụ thể của tag được xem qua `git rev-parse typ-2026-week-1^{commit}` trong repo tương ứng.

## Mở mã nguồn độc lập với nhánh hiện tại

```bash
git clone https://github.com/Tunhoclaptrinh/EruSentia-Server.git EruSentia-Server-week-1
cd EruSentia-Server-week-1
git switch --detach b851e1576ae48fe62efcec10dd2d912e011a6220
pnpm install --frozen-lockfile
```

Đây là một bản clone riêng, chứa toàn bộ file đã được commit ở mốc backend phía trên, kể cả khi nhánh production sau này đã xóa các file thực hành.

## Giữ mốc đã chốt

- Giữ tag `typ-2026-week-1` ở cả hai repo, không di chuyển hoặc ghi đè tag đó. Mốc mới dùng tên tag mới.
- Đẩy commit và tag lên remote để người đọc mở được các URL GitHub. Tag cần được push riêng, ví dụ `git push origin refs/tags/typ-2026-week-1` trong từng repo.
- Link SHA là định danh nội dung chính; tag giúp tìm lại mốc và giữ commit có tham chiếu trong Git. Repo, quyền đọc và lịch sử được lưu vẫn là điều kiện để truy cập về sau.
- Mốc Git giữ các file đã commit vào hai repo. Nội dung website bên ngoài chỉ được dẫn bằng URL và không được đóng băng theo mốc Git.

Tham khảo cách tạo [permalink theo commit trong tài liệu GitHub](https://docs.github.com/en/repositories/working-with-files/using-files/getting-permanent-links-to-files).
