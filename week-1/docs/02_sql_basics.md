# 📊 PHẦN 2 — SQL CƠ BẢN (Lý thuyết + Thực hành EruSentia)

> Đối chiếu thực hành: [Schema nghiệp vụ](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/01_schema.sql), [Seed](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/03_seed.sql). Các lệnh shell trong bài chạy từ thư mục gốc repo `EruSentia-Server` sau khi setup theo [README backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).

> Phạm vi: ví dụ nghiệp vụ dùng schema 9 bảng của backend. `ratings` lưu đánh giá,
> `templates.tags` là `TEXT[]`; không có `reviews`, `template_tags`, `templates.status` hoặc `price`.
> Các đoạn có `...`, `table_name`, `<...>` và mảnh `WHERE` là cú pháp mẫu, cần thay giá trị.

---

## 2.1 CSDL Quan hệ là gì?

**Relational Database (CSDL quan hệ)** là hệ thống lưu trữ dữ liệu theo mô hình bảng (table),
trong đó các bảng có quan hệ với nhau thông qua **khóa (key)**.

Được đề xuất bởi **Edgar F. Codd** (IBM, 1970) dựa trên lý thuyết tập hợp trong toán học.

### Các khái niệm nền tảng

| Khái niệm | Định nghĩa | Ví dụ EruSentia |
|-----------|-----------|----------------|
| **Table (Bảng)** | Tập hợp dữ liệu có cùng cấu trúc, gồm rows và columns | `users`, `templates`, `profiles` |
| **Row / Record (Hàng)** | Một bản ghi cụ thể trong bảng | 1 user cụ thể |
| **Column / Field (Cột)** | Thuộc tính của bảng, có kiểu dữ liệu xác định | `email`, `created_at` |
| **Primary Key (PK)** | Cột (hoặc tổ hợp cột) định danh DUY NHẤT mỗi row | `users.id` (UUID) |
| **Foreign Key (FK)** | Cột tham chiếu đến PK của bảng khác, đảm bảo tính toàn vẹn | `templates.author_id → users.id` |
| **Schema** | Cấu trúc tổng thể của DB: các bảng, cột, quan hệ, constraints | Toàn bộ file `sql/01_schema.sql` |

### Tại sao dùng CSDL quan hệ thay vì lưu file JSON?

| File JSON | CSDL Quan hệ |
|-----------|-------------|
| File JSON thuần không tự áp schema | Schema định nghĩa rõ ràng |
| Tìm kiếm chậm (O(n)) | Index giúp tìm kiếm O(log n) |
| Phải tự xây kiểm tra/đồng bộ khi ghi | Constraints + ACID |
| Khó query phức tạp (join) | SQL cực mạnh, linh hoạt |
| File thuần không cung cấp transaction DB | COMMIT / ROLLBACK |

---

## 2.2 SQL là gì?

**SQL (Structured Query Language)** — ngôn ngữ truy vấn có cấu trúc, dùng để:
- Tạo và quản lý cấu trúc DB (**DDL**)
- Đọc và ghi dữ liệu (**DML**)
- Kiểm soát quyền truy cập (**DCL**)
- Điều khiển transaction (**TCL**)

SQL là **declarative language** — bạn chỉ nói "tôi muốn gì", không cần nói "làm thế nào".
Database engine (PostgreSQL) tự tìm cách tối ưu để lấy dữ liệu.

```sql
-- Declarative: nói "tôi muốn templates published, sắp theo mới nhất"
SELECT * FROM templates WHERE is_public = TRUE AND deleted_at IS NULL ORDER BY created_at DESC, id DESC;
-- PostgreSQL tự quyết: dùng index nào, join order nào, sort algorithm nào
```

---

## 2.3 DDL — Data Definition Language

DDL là nhóm lệnh **định nghĩa cấu trúc** của database. DDL chủ yếu quản lý cấu trúc; một số lệnh như `DROP` và `TRUNCATE` cũng xóa dữ liệu.

### 2.3.1 Các lệnh DDL chính

| Lệnh | Ý nghĩa |
|------|---------|
| `CREATE TABLE` | Tạo bảng mới |
| `ALTER TABLE` | Sửa cấu trúc bảng (thêm/xóa/sửa cột) |
| `DROP TABLE` | Xóa bảng (cả data lẫn cấu trúc) |
| `TRUNCATE TABLE` | Xóa toàn bộ data trong bảng, giữ cấu trúc |
| `CREATE INDEX` | Tạo index để tối ưu query |
| `CREATE EXTENSION` | Bật extension (vd: `uuid-ossp`) |

### 2.3.2 Kiểu dữ liệu trong PostgreSQL

| Kiểu | Ý nghĩa | Ví dụ |
|------|---------|-------|
| `UUID` | Giá trị định danh 128-bit; uniqueness trong bảng do PK/UNIQUE bảo đảm | `550e8400-e29b-41d4-a716-446655440000` |
| `VARCHAR(n)` | Chuỗi ký tự, tối đa n ký tự | `email VARCHAR(255)` |
| `TEXT` | Chuỗi không giới hạn độ dài | `bio TEXT` |
| `INT / INTEGER` | Số nguyên 32-bit (-2 tỷ đến 2 tỷ) | `reputation_score INT` |
| `BIGINT` | Số nguyên 64-bit | `view_count BIGINT` |
| `NUMERIC(p,s)` | Số thập phân chính xác (precision, scale) | `rating_avg NUMERIC(3,2)` |
| `BOOLEAN` | TRUE / FALSE | `is_active BOOLEAN` |
| `TIMESTAMP` | Ngày giờ (không có timezone) | - |
| `TIMESTAMPTZ` | Thời điểm tuyệt đối; hiển thị theo timezone của session | `created_at TIMESTAMPTZ` |
| `DATE` | Chỉ ngày, không có giờ | `birth_date DATE` |
| `SMALLINT` | Số nguyên 16-bit (-32768 đến 32767) | `score SMALLINT` |

> **Tại sao EruSentia dùng `TIMESTAMPTZ` thay `TIMESTAMP`?**
> `TIMESTAMPTZ` chuẩn hóa thời điểm theo UTC, không giữ tên múi giờ/offset gốc. Khi đọc, PostgreSQL hiển thị theo `TimeZone` của session; nếu cần múi giờ gốc phải lưu riêng.

> **Tại sao dùng `UUID` thay `SERIAL` (auto-increment)?**
> UUID có thể generate ở client (không cần hỏi DB), phân tán tốt hơn, không lộ thông tin
> (user id = 1, 2, 3 lộ có bao nhiêu user trong hệ thống).

### 2.3.3 Constraints và giá trị mặc định

**Constraint** là quy tắc ràng buộc dữ liệu được DB tự động kiểm tra.

| Constraint | Định nghĩa | Ví dụ trong EruSentia |
|-----------|-----------|----------------------|
| `PRIMARY KEY` | Cột có giá trị duy nhất + NOT NULL, định danh row | `id UUID PRIMARY KEY` |
| `NOT NULL` | Cột bắt buộc phải có giá trị | `email VARCHAR(255) NOT NULL` |
| `UNIQUE` | Giá trị trong cột phải duy nhất (cho phép NULL) | `UNIQUE(email)` |
| `CHECK` | Điều kiện tùy chỉnh | `CHECK (role IN ('user','creator','admin'))` |
| `DEFAULT` (không phải constraint kiểm tra) | Giá trị mặc định khi không truyền vào | `DEFAULT CURRENT_TIMESTAMP` |
| `FOREIGN KEY` | Đảm bảo giá trị phải tồn tại ở bảng tham chiếu | `author_id REFERENCES users(id)` |

**Tại sao constraints quan trọng?**
Nếu không có constraints, code application có thể vô tình insert dữ liệu sai (email trùng, role không hợp lệ...). Constraints là "lớp bảo vệ cuối cùng" ngay tại DB.

```sql
-- Ví dụ: không có UNIQUE(email) → có thể tạo 2 user cùng email
INSERT INTO users (email, ...) VALUES ('a@b.com', ...);
INSERT INTO users (email, ...) VALUES ('a@b.com', ...);  -- cho phép! Bug!

-- Với UNIQUE: PostgreSQL tự reject
INSERT INTO users (email, ...) VALUES ('a@b.com', ...);  -- ERROR: duplicate key
```

### 2.3.4 ON DELETE behavior của Foreign Key

| Option | Hành vi khi xóa row ở bảng cha |
|--------|-------------------------------|
| `RESTRICT` | Từ chối xóa nếu còn tham chiếu; không trì hoãn kiểm tra |
| `CASCADE` | Tự động xóa cả row con |
| `SET NULL` | Set FK của row con thành NULL |
| `SET DEFAULT` | Set FK về giá trị DEFAULT |
| `NO ACTION` (default) | Kiểm tra ngay theo mặc định; chỉ trì hoãn nếu FK được khai báo `DEFERRABLE` và đang deferred |

```sql
-- Trong EruSentia:
-- profiles.user_id REFERENCES users(id) ON DELETE CASCADE
-- → Xóa user → profiles tương ứng tự xóa theo

-- templates.author_id REFERENCES users(id) ON DELETE RESTRICT
-- → Không thể xóa user nếu còn template (bảo vệ dữ liệu)
```

### 2.3.5 Thực hành DDL — bảng đánh giá

Schema thực tế đã có `ratings` với FK tới `templates`, `users`, `CHECK (score BETWEEN 1 AND 5)`
và `UNIQUE (template_id, user_id)`. Không tạo thêm `reviews` trùng nghiệp vụ.
Dưới đây là bài DDL độc lập, chạy trong session thực hành rồi hoàn tác:

```sql
BEGIN;
CREATE TABLE rating_ddl_lab (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_id UUID NOT NULL REFERENCES templates(id),
    score SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 5)
);
ALTER TABLE rating_ddl_lab ADD COLUMN comment TEXT;
-- Trong psql: \d rating_ddl_lab
ROLLBACK;
```

---

## 2.4 DML — Data Manipulation Language

DML là nhóm lệnh thao tác với **dữ liệu** (không phải cấu trúc).

### 2.4.1 INSERT — Thêm dữ liệu

```sql
-- Cú pháp cơ bản
INSERT INTO table_name (col1, col2, col3)
VALUES (val1, val2, val3);

-- Insert nhiều rows cùng lúc
INSERT INTO ratings (template_id, user_id, score, comment)
VALUES
  ('<template_id_1>', '<user_id_2>', 5, 'Tuyệt vời!'),
  ('<template_id_1>', '<user_id_3>', 4, 'Khá ổn');

-- INSERT ... RETURNING: lấy lại giá trị vừa insert (thường dùng để lấy auto-generated id)
INSERT INTO ratings (template_id, user_id, score)
VALUES ('<t_id>', '<u_id>', 5)
RETURNING id, created_at;
```

### 2.4.2 SELECT — Truy vấn dữ liệu

**Thứ tự logic để hiểu SQL query** (optimizer có thể dùng kế hoạch vật lý khác) (quan trọng để hiểu WHERE vs HAVING):

```
1. FROM       — xác định bảng nào
2. JOIN       — kết hợp bảng
3. WHERE      — lọc rows TRƯỚC khi GROUP
4. GROUP BY   — nhóm các rows
5. HAVING     — lọc groups SAU khi GROUP
6. SELECT     — chọn cột để trả về
7. DISTINCT   — loại bỏ trùng
8. ORDER BY   — sắp xếp
9. OFFSET/LIMIT — bỏ qua OFFSET rows rồi lấy tối đa LIMIT rows
```

> **Tại sao không dùng WHERE với aggregate function?**
> WHERE chạy ở bước 3, trước khi GROUP BY (bước 4). Lúc đó chưa có aggregate (COUNT, SUM...).
> HAVING chạy ở bước 5, sau GROUP BY — lúc đó mới có aggregate để lọc.

```sql
-- Lỗi: WHERE với aggregate
SELECT author_id, COUNT(*) FROM templates
WHERE COUNT(*) > 2  -- ← LỖI! WHERE không biết COUNT là gì
GROUP BY author_id;

-- Đúng: HAVING với aggregate
SELECT author_id, COUNT(*) FROM templates
GROUP BY author_id
HAVING COUNT(*) > 2;  -- ← lọc sau khi đã GROUP xong
```

### 2.4.3 Các mệnh đề SELECT quan trọng

**WHERE — Lọc điều kiện:**
```sql
-- Operators cơ bản
WHERE views_count = 99000           -- bằng
WHERE views_count != 0              -- khác
WHERE views_count > 50000           -- lớn hơn
WHERE views_count BETWEEN 10000 AND 100000  -- trong khoảng (inclusive)
WHERE title LIKE '%React%'    -- chứa chuỗi (% = bất kỳ ký tự)
WHERE title ILIKE '%react%'   -- case-insensitive LIKE (PostgreSQL)
WHERE role IN ('creator', 'admin')  -- trong danh sách
WHERE role NOT IN ('admin')
WHERE deleted_at IS NULL      -- NULL check (không dùng = NULL!)
WHERE deleted_at IS NOT NULL

-- Kết hợp điều kiện
WHERE is_public = TRUE AND views_count > 0
WHERE role = 'creator' OR role = 'admin'
WHERE NOT is_public
```

**ORDER BY — Sắp xếp:**
```sql
ORDER BY created_at DESC           -- mới nhất trước
ORDER BY views_count ASC                 -- ít lượt xem nhất trước
ORDER BY views_count DESC, created_at DESC  -- sort nhiều cột
ORDER BY views_count DESC NULLS LAST    -- NULL xuống cuối
```

**Aggregate Functions:**

| Function | Ý nghĩa | Chú ý |
|---------|---------|-------|
| `COUNT(*)` | Đếm số rows (kể cả NULL) | Luôn trả về số |
| `COUNT(col)` | Đếm số rows có giá trị non-NULL ở cột đó | Khác COUNT(*) |
| `SUM(col)` | Tổng giá trị | Bỏ qua NULL |
| `AVG(col)` | Trung bình | Bỏ qua NULL |
| `MIN(col)` | Giá trị nhỏ nhất | Hoạt động với cả string, date |
| `MAX(col)` | Giá trị lớn nhất | Hoạt động với cả string, date |

SUM/AVG/MIN/MAX trả NULL nếu tập đầu vào không có giá trị; dùng COALESCE khi cần giá trị mặc định. NOT IN cũng có ngữ nghĩa ba giá trị khi danh sách chứa NULL; không thay cho IS NULL.

### 2.4.4 UPDATE — Cập nhật dữ liệu

```sql
-- Cú pháp: LUÔN có WHERE! (trừ khi thực sự muốn update toàn bộ)
UPDATE table_name
SET col1 = val1, col2 = val2
WHERE condition;

-- Ví dụ EruSentia:
UPDATE templates
SET is_public = TRUE, updated_at = NOW()
WHERE id = '<template_id>' AND author_id = '<user_id>';

-- UPDATE ... RETURNING: lấy lại row sau khi update
UPDATE profiles
SET reputation_score = reputation_score + 10
WHERE user_id = '<user_id>'
RETURNING id, reputation_score;

-- Cập nhật dựa trên giá trị cột khác (bảng khác)
UPDATE templates t
SET rating_avg = COALESCE((
    SELECT ROUND(AVG(r.score)::NUMERIC, 2)
    FROM ratings r
    WHERE r.template_id = t.id
), 0),
ratings_count = (SELECT COUNT(*) FROM ratings r WHERE r.template_id = t.id)
WHERE id = '<template_id>';
-- Khi thêm/xóa rating thực tế, cập nhật cached aggregate trong cùng transaction.
-- Cần khóa template/serialization phù hợp khi có nhiều người đánh giá đồng thời.
```

### 2.4.5 DELETE — Xóa dữ liệu

```sql
-- Hard delete (xóa thật) — NGUY HIỂM nếu thiếu WHERE
DELETE FROM ratings WHERE id = '<review_id>';

-- Soft delete (xóa mềm) — pattern EruSentia dùng
-- Không xóa thật, chỉ đánh dấu deleted_at
UPDATE users
SET deleted_at = NOW(), is_active = FALSE
WHERE id = '<user_id>';

-- Query loại trừ soft-deleted records
SELECT * FROM users WHERE deleted_at IS NULL;
```

> **Tại sao EruSentia dùng soft delete?**
> 1. Giữ thời điểm đánh dấu xóa; muốn biết ai xóa/lý do cần ghi thêm `audit_logs`
> 2. Khôi phục được nếu xóa nhầm
> 3. FK constraint: nếu user có templates, hard delete sẽ fail nếu có RESTRICT

---

## 2.5 JOIN — Kết hợp bảng

**JOIN** là cơ chế kết hợp dữ liệu từ nhiều bảng dựa trên điều kiện quan hệ.

### Các loại JOIN

#### INNER JOIN
Chỉ lấy rows **có match ở cả hai bảng**.

```
Bảng A: [1, 2, 3]
Bảng B: [2, 3, 4]
INNER JOIN: [2, 3]  ← chỉ phần giao
```

```sql
-- Templates với tên tác giả (chỉ lấy template có user tồn tại)
SELECT
    t.title,
    t.rating_avg,
    p.username     AS author,
    c.name         AS category
FROM templates t
INNER JOIN users u      ON t.author_id = u.id
INNER JOIN profiles p   ON p.user_id = u.id
INNER JOIN categories c ON t.category_id = c.id
WHERE t.is_public = TRUE AND t.deleted_at IS NULL
  AND u.deleted_at IS NULL
ORDER BY t.created_at DESC;
```

#### LEFT JOIN (LEFT OUTER JOIN)
Lấy **tất cả rows bên trái**, rows bên phải NULL nếu không match.

```
Bảng A: [1, 2, 3]
Bảng B: [2, 3, 4]
LEFT JOIN: [1(null), 2, 3]  ← A đầy đủ, B NULL nếu không match
```

```sql
-- Tìm creator chưa có template nào (LEFT JOIN + IS NULL)
SELECT
    u.email,
    p.username,
    COUNT(t.id) AS template_count
FROM users u
LEFT JOIN profiles p  ON p.user_id = u.id
LEFT JOIN templates t ON t.author_id = u.id AND t.deleted_at IS NULL
WHERE u.role = 'creator'
GROUP BY u.id, u.email, p.username
HAVING COUNT(t.id) = 0;
```

#### RIGHT JOIN
Ngược với LEFT JOIN — ít dùng (có thể đổi thứ tự bảng + dùng LEFT JOIN thay thế).

#### FULL OUTER JOIN
Lấy tất cả rows cả hai phía, NULL ở phía không match.

```sql
-- Tất cả user + tất cả template, kể cả không match
SELECT u.email, t.title
FROM users u
FULL OUTER JOIN templates t ON t.author_id = u.id;
```

#### SELF JOIN
Join bảng với chính nó — dùng khi bảng có quan hệ đệ quy.

```sql
-- Ví dụ: categories có parent category
-- SELECT c.name, parent.name AS parent_category
-- FROM categories c
-- LEFT JOIN categories parent ON c.parent_id = parent.id
```

### Quy tắc đặt alias
```sql
-- Dùng alias để query dễ đọc hơn
FROM templates t           -- t thay cho templates
INNER JOIN users u ON ...  -- u thay cho users
INNER JOIN profiles p ON ...
-- Sau đó: t.title, u.email, p.username — rõ ràng thuộc bảng nào
```

---

## 2.6 GROUP BY và Aggregate

**GROUP BY** gom các rows có cùng giá trị của cột được chỉ định thành một nhóm,
sau đó áp dụng aggregate function lên mỗi nhóm.

```sql
-- Thống kê template theo category (số lượng, điểm trung bình, lượt xem)
SELECT
    c.name                       AS category_name,
    COUNT(t.id)                  AS total_templates,
    COUNT(CASE WHEN t.is_public = TRUE THEN 1 END) AS public_count,
    ROUND(AVG(t.rating_avg)::NUMERIC, 0) AS mean_template_rating,
    MAX(t.views_count)                 AS max_views,
    MIN(t.views_count)                 AS min_views,
    SUM(t.views_count)                 AS total_views
FROM categories c
LEFT JOIN templates t ON t.category_id = c.id AND t.deleted_at IS NULL
GROUP BY c.id, c.name
ORDER BY total_templates DESC;
```

> **Quy tắc GROUP BY:** Cột SELECT không được aggregate phải có trong GROUP BY,
> hoặc phụ thuộc hàm vào khóa chính đã được nhóm của chính bảng đó. PostgreSQL cho phép
> `SELECT c.id, c.name ... GROUP BY c.id` khi `c.id` là PK; đây không phải ví dụ lỗi.
> Để viết rõ ràng và dễ chuyển DBMS, có thể nhóm cả `c.id, c.name`.

**HAVING — Lọc sau GROUP:**
```sql
-- Top creators có >= 2 templates published
SELECT
    p.username,
    p.reputation_score,
    COUNT(t.id)  AS template_count
FROM profiles p
JOIN users u     ON p.user_id = u.id
JOIN templates t ON t.author_id = u.id
WHERE t.is_public = TRUE AND t.deleted_at IS NULL   -- lọc TRƯỚC group (rows)
GROUP BY p.id, p.username, p.reputation_score
HAVING COUNT(t.id) >= 2        -- lọc SAU group (groups)
ORDER BY template_count DESC;
```

---

## 2.7 Subquery (Truy vấn con)

**Subquery** là query lồng bên trong query khác.

```sql
-- Tìm user có email trong danh sách creators
SELECT email FROM users
WHERE id IN (
    SELECT DISTINCT author_id FROM templates WHERE is_public = TRUE AND deleted_at IS NULL
);

-- Template có lượt xem cao hơn trung bình của các template công khai còn hoạt động
SELECT title, views_count FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
  AND views_count > (
    SELECT AVG(views_count) FROM templates
    WHERE is_public = TRUE AND deleted_at IS NULL
  );

-- Correlated subquery: subquery tham chiếu đến outer query
SELECT
    t.title,
    (SELECT COUNT(*) FROM ratings r WHERE r.template_id = t.id) AS ratings_count
FROM templates t
WHERE t.is_public = TRUE AND t.deleted_at IS NULL;
```

> **Subquery vs JOIN:** Chọn theo ngữ nghĩa (đặc biệt số row và NULL), rồi đo plan.
> PostgreSQL có thể chuyển đổi một số subquery; không bảo đảm mọi subquery chậm hơn JOIN.

---

## 2.8 Bài tập thực hành

Tự tạo file `sql/05_week01_queries.sql` trong bản clone backend để lưu bài tập; repo chưa có file lời giải này:

```sql
-- Bài 1: Tất cả template published kèm tên tác giả, category, số tags
-- Hint: cần JOIN templates, users, profiles, categories; cardinality(t.tags) đếm tags

-- Bài 2: User đăng ký > 30 ngày chưa verify email
-- Hint: NOW() - created_at > INTERVAL '30 days' AND email_verified_at IS NULL

-- Bài 3: Top 5 category nhiều template published nhất (có số lượng)
-- Hint: GROUP BY + ORDER BY COUNT DESC LIMIT 5

-- Bài 4: Template không có tag nào
-- Hint: cardinality(tags) = 0

-- Bài 5: Tính tổng lượt xem của các template công khai chưa bị xóa
-- Hint: SUM(views_count) + WHERE is_public = TRUE AND deleted_at IS NULL

-- Bài 6 (nâng cao): Với mỗi creator, show: tên, số template, template nhiều lượt xem nhất, tổng lượt xem
```

Nguồn đối chiếu: [Constraints](https://www.postgresql.org/docs/16/ddl-constraints.html), [Date/Time](https://www.postgresql.org/docs/16/datatype-datetime.html), [SELECT](https://www.postgresql.org/docs/16/sql-select.html) — PostgreSQL 16.
