# Tuần 1: Kiến thức về Cơ sở dữ liệu và Lập trình hướng đối tượng

---

## Phần 1: Database (CSDL quan hệ)

### 1. Cài đặt DBMS
- MySQL hoặc PostgreSQL
- Cài đặt qua Docker Container, XAMPP, hoặc cloud (Supabase, PlanetScale,...)

### 2. SQL cơ bản
- Tạo 1 CSDL cho bài toán cụ thể (quản lý giải đấu, quản lý thư viện,...)
- DDL: CREATE TABLE, ALTER TABLE, DROP TABLE
- DML: SELECT, INSERT, UPDATE, DELETE
- Query: WHERE, JOIN (INNER, LEFT, RIGHT), GROUP BY, HAVING, ORDER BY
- Aggregate functions: COUNT, SUM, AVG, MIN, MAX

### 3. Index
- Index là gì? Tại sao cần?
- Khi nào nên đánh index, khi nào không nên?
- Thực hành: tạo index, so sánh tốc độ query trước và sau khi đánh index

### 4. Phân trang (Pagination)
- Tại sao cần phân trang?
- Offset-based pagination: `LIMIT` + `OFFSET`
- Cursor-based pagination: `WHERE id > last_id LIMIT N`
- Ưu nhược điểm và trường hợp sử dụng của từng cách

### 5. Locking trong Database
- Tại sao cần lock?
- Optimistic Locking
- Pessimistic Locking
- So sánh: khi nào dùng cái nào?
- Demo: chạy 2 transaction đồng thời để thấy lock hoạt động

### 6. Transaction
- Transaction là gì?
- Khi nào cần gom nhiều thao tác vào 1 transaction?
- COMMIT, ROLLBACK
- Ví dụ: chuyển tiền — trừ tài khoản A + cộng tài khoản B phải thành công cùng nhau, fail thì rollback cả hai

---

## Phần 2: OOP

### 1. OOP trong Java
- Các tính chất: Encapsulation, Inheritance, Polymorphism, Abstraction
- Class, Abstract Class, Interface — khi nào dùng cái nào?

### 2. Dependency Injection (DI) & Inversion of Control (IoC)
- Khái niệm, ví dụ bằng Java thuần (không dùng framework)
- Tại sao DI giúp code dễ test, dễ thay đổi?
- Tìm hiểu thêm: DI/IoC được ứng dụng thế nào trong Spring

---

## Output

- Trình bày lý thuyết
- Database có dữ liệu mẫu, chạy được các query (JOIN, GROUP BY)
- Demo được sự khác nhau giữa có index và không có index
- Demo được locking: chạy 2 transaction đồng thời, cho thấy lock hoạt động


## Báo cáo và hướng dẫn tái hiện

- [Nhật ký xử lý review và rà soát](REVIEW_UPDATES.md).
- [Chạy trên Ubuntu](docs/01_docker_and_environment_handbook.md#7-chạy-thực-tế-trên-ubuntu-native).
- [Báo cáo kỹ thuật tuần 1](WEEK1_REPORT.md).
- [Bản nguồn cố định: commit, tag và tải toàn bộ repo](SOURCE_SNAPSHOT.md).
- [Kết quả kiểm tra sau dọn repo ngày 20/10/2026](CLEANUP_CHECKS.md).
- [8 chuyên đề lý thuyết](docs/00_GUIDE_INDEX.md).
- [12 ảnh minh chứng](screenshots/).
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
