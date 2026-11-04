# 🗺️ TÀI LIỆU KỸ THUẬT & HƯỚNG DẪN THỰC HÀNH TUẦN 1

> Tổng hợp toàn bộ 8 chuyên đề lý thuyết chuyên sâu, phân tích kỹ thuật và liên kết mã nguồn thực hành của Tuần 1. Mã nguồn thực thi được quản lý tại repo backend.

---

## 📁 Cấu trúc tài liệu

| Tài liệu | Nội dung trọng tâm |
|----------|-------------------|
| [01_docker_and_environment_handbook.md](./01_docker_and_environment_handbook.md) | Kiến trúc môi trường Docker, WSL 2, mạng container và PostgreSQL 16 |
| [02_sql_basics.md](./02_sql_basics.md) | DDL/DML, Foreign Keys, UUID PK, Soft Delete, INNER/LEFT JOIN, GROUP BY & Aggregate |
| [03_index.md](./03_index.md) | Cấu trúc dữ liệu B-Tree Index, chi phí tra cứu và phương pháp đo kiểm EXPLAIN ANALYZE |
| [04_pagination.md](./04_pagination.md) | So sánh chi tiết kỹ thuật Offset-based vs Cursor-based Pagination |
| [05_locking.md](./05_locking.md) | Cơ chế Pessimistic Lock (`FOR UPDATE`), Optimistic Lock (Version) và xử lý Deadlock |
| [06_transaction.md](./06_transaction.md) | Nguyên lý ACID, các cấp độ Isolation và kỹ thuật kiểm soát điểm lưu (Savepoint) |
| [07_oop.md](./07_oop.md) | 4 tính chất OOP (Encapsulation, Inheritance, Polymorphism, Abstraction) trên TypeScript |
| [08_di_ioc.md](./08_di_ioc.md) | Kiến trúc Dependency Injection, Inversion of Control Container và ứng dụng trong NestJS |

---

## 🏗️ Mã nguồn thực hành & Báo cáo kết quả

| Đường dẫn | Nội dung |
|-----------|----------|
| [Hiệu đính sau review](../REVIEW_UPDATES.md) | Hai góp ý đã xử lý, các lỗi khác và giới hạn bằng chứng |
| [Mốc nguồn cố định](../SOURCE_SNAPSHOT.md) | Commit backend và bản lưu trữ báo cáo, hướng dẫn mở hoặc tải toàn bộ repo tại mốc tuần 1 |
| [Demo OOP](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/src/week01-practice/oop/demo.ts) | Thực nghiệm 4 tính chất OOP (Encapsulation, Inheritance, Polymorphism, Abstraction) |
| [Demo DI](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/src/week01-practice/di/demo.ts) | Thực nghiệm Dependency Injection & DI Container |
| [Hướng dẫn cài đặt và lệnh chạy](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/README.md) | Cài dependencies, cấu hình PostgreSQL và chạy demo từ repo backend |
| [Schema thực tế](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/01_schema.sql) | 9 bảng nghiệp vụ hiện có |
| [SQL của backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220/sql) | Toàn bộ script DDL Schema, Index, Seed nghiệp vụ cốt lõi & Benchmark đo kiểm |
| [Kiểm tra sau dọn repo](../CLEANUP_CHECKS.md) | Kết quả kiểm tra ngày 20/10/2026, môi trường và commit backend |
| [`WEEK1_REPORT.md`](../WEEK1_REPORT.md) | Báo cáo, 12 ảnh lịch sử và giới hạn bằng chứng benchmark/locking |

---

## 📋 Kết quả hoàn thành

| # | Hạng mục yêu cầu | Trạng thái |
|---|-----------------|------------|
| 1 | Trình bày lý thuyết Database + OOP | ✅ Hoàn thành |
| 2 | Database có dữ liệu mẫu, chạy được JOIN / GROUP BY | ✅ Hoàn thành |
| 3 | Demo index (EXPLAIN ANALYZE) | Đã đo cùng bảng/query trên Docker PostgreSQL 16.15, kết quả trong báo cáo |
| 4 | Demo cơ chế Locking | Đã kiểm chứng hai connection: blocking, version conflict và deadlock trên PostgreSQL 16.15 |

Các đường dẫn `src/` và `sql/` trong ví dụ lý thuyết được hiểu theo thư mục gốc của backend; các file mẫu trong bài học không phải tất cả đều là file đã triển khai. Xem [hướng dẫn tái hiện](#báo-cáo-và-hướng-dẫn-tái-hiện).

## Báo cáo và hướng dẫn tái hiện

- [Nhật ký xử lý review và rà soát](../REVIEW_UPDATES.md).
- [Chạy trên Ubuntu](01_docker_and_environment_handbook.md#7-chạy-thực-tế-trên-ubuntu-native).
- [Báo cáo kỹ thuật tuần 1](../WEEK1_REPORT.md).
- [Bản nguồn cố định: commit, tag và tải toàn bộ repo](../SOURCE_SNAPSHOT.md).
- [Kết quả kiểm tra sau dọn repo ngày 20/10/2026](../CLEANUP_CHECKS.md).
- [8 chuyên đề lý thuyết](00_GUIDE_INDEX.md).
- [Ảnh minh chứng](../screenshots).
- [Mã nguồn backend và hướng dẫn cài đặt](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).
- [Schema, index, seed và benchmark SQL](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220/sql).
- [Demo OOP](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/src/week01-practice/oop/demo.ts) và [demo DI](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/src/week01-practice/di/demo.ts).

Các lệnh sau chạy từ thư mục `EruSentia-Server` sau khi đã cài dependencies và cấu hình PostgreSQL theo README của backend:

```bash
pnpm db:check
pnpm demo:oop
pnpm demo:di
pnpm db:benchmark
```

Schema nghiệp vụ có 9 bảng. `users_no_index` và `template_slots` trong báo cáo là bảng phụ của bài thực hành. Báo cáo giữ kết quả và ảnh của lần đo được ghi ngày trong tài liệu.

Demo DI dùng repository Postgres mô phỏng; phần kết nối database thật được thực hiện bởi các script `db:*` của backend.

Bản hiện tại đã hiệu đính review và bổ sung kiểm chứng trên Docker PostgreSQL 16.15 của
máy Ubuntu; kết quả và ảnh Adminer thật nằm trong báo cáo. Tag `typ-2026-week-1` giữ bản
lưu trữ cũ. Phần OOP/DI dùng TypeScript/NestJS theo stack EruSentia.
