# Hiệu đính review và rà soát tuần 1

Ngày rà soát: 20/10/2026. Phạm vi: báo cáo, 8 chuyên đề, hướng dẫn tái hiện,
điều hướng và tài liệu theo dõi tuần 1. Mã nguồn thực thi vẫn tập trung ở backend;
bản nháp tuần 2 và EruSentia App không nằm trong đợt chỉnh sửa này.

## Commit 1 — xử lý hai góp ý của reviewer

Review tại [PR #12](https://github.com/quanglam04/trainning-typ-2026/pull/12),
reviewer `ttvKieran` ngày 11/10/2026:

| Góp ý | Hiệu đính |
|-------|-----------|
| [Composite index và thứ tự WHERE](https://github.com/quanglam04/trainning-typ-2026/pull/12#discussion_r4240718934) | Leftmost prefix phụ thuộc thứ tự cột index, không phụ thuộc thứ tự viết AND. Điều kiện chỉ có cột sau vẫn có thể dùng index nhưng có thể quét toàn index ở PostgreSQL 16. |
| [Optimistic lock vẫn deadlock](https://github.com/quanglam04/trainning-typ-2026/pull/12#discussion_r4240705833) | UPDATE lấy row lock dù có kiểm tra version. Bổ sung trình tự hai session cập nhật ngược thứ tự, xử lý `40P01` và sửa bảng so sánh/retry. |

Commit xử lý: `584c801`. Đây là sửa tài liệu theo review; không đồng nghĩa reviewer đã
approve hoặc đánh dấu các thread resolved.

## Commit 2 — rà lại và cập nhật tài liệu

| Phần | Những điểm sửa chính |
|------|----------------------|
| Docker/môi trường | Số bảng đúng là 9; phân biệt Compose ghi cứng và `.env`, WSL 2 backend với các backend khác, điều kiện init scripts chạy. |
| SQL | Dùng `is_public`, `rating_avg`, `views_count`, `ratings.score`, `tags TEXT[]` đúng schema. Bỏ bảng reviews trùng nghiệp vụ; sửa TIMESTAMPTZ, NO ACTION/RESTRICT, GROUP BY khóa chính và cập nhật aggregate khi chưa có rating. |
| Index | B-tree nhiều nhánh; không coi UPDATE là rebuild toàn index; HOT, partial predicate, visibility map của index-only scan, Planning Time tách Execution Time, giới hạn CONCURRENTLY và planner chọn theo cost. |
| Pagination | Thứ tự duy nhất `(created_at, id)`, UUID hợp lệ, partial composite index; bỏ khẳng định O(1)/snapshot ổn định tuyệt đối; sửa ví dụ trùng/mất row của OFFSET, COUNT riêng và độ chính xác cursor timestamp. |
| Locking | READ UNCOMMITTED ánh xạ READ COMMITTED trong PG; sửa bảng xung đột 4 row-lock modes, setup đủ 2 rows/updated_at và reset version giữa demo, NOW() theo transaction, xử lý transaction aborted. |
| Transaction | Dùng schema thật, phát hiện UPDATE 0, kiểm tra quyền và tránh cộng điểm lặp; cùng PoolClient, rollback/release đúng, savepoint độc lập và giới hạn ACID/side effects. |
| OOP | Phân biệt ví dụ giả định với domain thật; private/readonly ở type-check, getter Date trả bản sao, Java access modifiers, bắt lỗi trong ví dụ, giới hạn overload/interface. |
| DI/IoC | Repository Postgres là stub; phân biệt DI/IoC/DIP, token runtime cho interface NestJS, singleton wiring thủ công, skeleton chưa đủ contract và JSON không khôi phục entity. |
| Báo cáo/điều hướng | Giữ 12 ảnh và số đo lịch sử; sửa diễn giải benchmark, số row filter, 4 bảng/3 JOIN; nêu đúng giới hạn bằng chứng. Mục lục mở bản hiện tại, tag cũ được ghi rõ là lưu trữ trước hiệu đính. |

## Kiểm chứng bổ sung cho bản hiệu đính

Các nguồn kỹ thuật chính thức được dẫn ngay trong từng chuyên đề, đối chiếu tài liệu
PostgreSQL **16**, TypeScript, node-postgres, NestJS và Docker.

| Kiểm tra ngày 20/10/2026 | Kết quả |
|-------------------------|---------|
| Ví dụ SQL độc lập trích từ bài SQL, pagination, transaction | 15 block chạy đạt trên schema/seed backend; 10 block chứa placeholder/mảnh cú pháp/lỗi cố ý không chạy nguyên khối. |
| Index mới và thứ tự hai điều kiện AND đảo nhau | DDL chạy được, hai query trả cùng tập row; không ép planner dùng loại scan cụ thể. |
| Helper transaction trích từ tài liệu | TypeScript strict đạt; publish thành công, gọi lặp không cộng điểm lần hai, lỗi audit log làm rollback cả trạng thái template và điểm. |
| Locking hai connection | Quan sát blocking bằng `pg_blocking_pids`, nhả lock sau COMMIT; version cũ UPDATE 0; optimistic UPDATE ngược thứ tự tái hiện `40P01`. |
| Liên kết và Markdown | 16 tài liệu: 63 đích tương đối, 44 đường dẫn GitHub được đối chiếu object Git, 4 anchor hợp lệ; code fence cân bằng và diff không có lỗi whitespace. |
| Các ví dụ TypeScript đủ ngữ cảnh | 4 block OOP và chuỗi constructor DI/container compile strict, chạy exit 0. |

Database kiểm chứng là PostgreSQL **18.6** từ gói Ubuntu chính thức, tạo riêng dưới `/tmp`,
chỉ mở Unix socket, không sử dụng DB/volume dự án. Đây là kiểm chứng bổ sung cho tài liệu,
**chưa phải** lần chạy lại image PostgreSQL 16, không thay thế 12 ảnh thực hành ngày
03/10/2026. Các fragment NestJS/Jest/Redis vẫn là hướng dẫn mở rộng, chưa chạy như ứng dụng
thật. Không có số đo benchmark định lượng mới để thay hai ảnh cũ.

## Bổ sung kiểm chứng trên Ubuntu

Ngày 20/10/2026 đã chạy đúng Compose của backend trên Ubuntu 26.04.1 LTS với
PostgreSQL 16.15. Benchmark cùng bảng/query và các tình huống locking hai connection
đều đạt; kết quả và hai ảnh Adminer thật được đưa vào [báo cáo tuần 1](WEEK1_REPORT.md).
Phần OOP/DI giữ TypeScript/NestJS theo stack dự án. Hai ảnh benchmark cũ vẫn là số đo
lịch sử, không dùng làm kết luận tăng tốc 65 lần. Tag `typ-2026-week-1` giữ nguyên.
