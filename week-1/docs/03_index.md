# 🔍 PHẦN 3 — INDEX (Lý thuyết đầy đủ + Benchmark EruSentia)

> Đối chiếu thực hành: [Index](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/02_indexes.sql), [Bài benchmark](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/sql/04_week01_benchmark.sql). Các lệnh shell trong bài chạy từ thư mục gốc repo `EruSentia-Server` sau khi setup theo [README backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).

> Các đoạn chỉ có WHERE/ORDER BY, `?` hay tên cột mẫu là mảnh cú pháp minh họa; không phải script chạy nguyên khối. Index bổ sung dưới đây chỉ thử trong database thực hành sau khi xem workload/index đã có.

---

## 3.1 Index là gì?

**Index** (chỉ mục) là một **cấu trúc dữ liệu phụ** được tạo ra bên cạnh bảng,
lưu trữ giá trị của một hoặc nhiều cột kèm con trỏ (pointer) đến vị trí của row tương ứng.

**Mục đích:** Tăng tốc các thao tác tìm kiếm, sắp xếp, và lọc — đặc biệt với bảng lớn.

### Tại sao cần Index?

Không có index, database phải thực hiện **Sequential Scan** (Full Table Scan):
- Đọc từng row từ đầu đến cuối bảng
- Độ phức tạp: **O(n)** — n là số rows
- 1,000,000 rows: phải đọc qua 1,000,000 rows để tìm 1 kết quả

Với Index (B-Tree):
- Tra cứu theo cấu trúc cây cân bằng
- Độ phức tạp: **O(log n)**
- B-tree có nhiều nhánh trên mỗi page; số tầng phụ thuộc fan-out/kích thước khóa, không phải cây nhị phân với `log₂(n)` tầng. Chi phí còn gồm đọc heap và số kết quả.

### Analogy — Mục lục sách

```
Không có index = sách không có mục lục
  → Muốn tìm "PostgreSQL" phải đọc từ trang 1 đến trang 500

Có index = sách có mục lục
  → Mục lục: "PostgreSQL → trang 287"
  → Lật thẳng đến trang 287
```

---

## 3.2 Cấu trúc B-Tree Index

**B-tree (cây tìm kiếm nhiều nhánh cân bằng)** là cấu trúc index mặc định trong PostgreSQL (và hầu hết DBMS).

### Đặc điểm B-Tree
- **Balanced:** mọi node lá ở cùng độ sâu → thời gian tìm kiếm nhất quán
- **Ordered:** dữ liệu được sắp xếp → hỗ trợ range query (>, <, BETWEEN)
- **Self-balancing:** tự cân bằng khi insert/delete

### Minh họa B-Tree

```
Sơ đồ hai nhánh dưới chỉ minh họa đường tìm kiếm trên `views_count`, không mô tả cấu trúc page B-tree PostgreSQL:

                    [50000]
                   /       \
            [20000]         [80000]
           /       \       /       \
       [10000]  [35000] [65000]  [90000]
         ↓         ↓       ↓        ↓
      row ptr   row ptr  row ptr  row ptr
```

- Tìm views_count = 35000: root → rẽ trái → [20000] → rẽ phải → tìm thấy, 3 bước
- Tìm views_count > 50000: dùng node lá theo thứ tự → range scan hiệu quả

### Những loại query B-Tree hỗ trợ

```sql
WHERE col = value           -- equality
WHERE col > value           -- range (greater than)
WHERE col < value           -- range (less than)
WHERE col BETWEEN a AND b   -- range
WHERE col LIKE 'abc%'       -- prefix; tùy collation, có thể cần text_pattern_ops
ORDER BY col                -- có thể tránh Sort nếu thứ tự/index/query phù hợp
```

---

## 3.3 Các loại Index trong PostgreSQL

| Loại | Cấu trúc | Dùng cho |
|------|---------|---------|
| **B-Tree** | Balanced Tree | Mặc định, equality + range, sort |
| **Hash** | Hash table | Chỉ equality (`=`); hiệu năng cần đo, không mặc định nhanh hơn B-tree |
| **GiST** | Generalized Search Tree | Geometric data, full-text search |
| **GIN** | Generalized Inverted Index | Array, JSONB, full-text (contains queries) |
| **BRIN** | Block Range Index | Bảng cực lớn, dữ liệu có thứ tự tự nhiên (time-series) |

```sql
-- B-Tree (mặc định)
-- users_email_key đã có do UNIQUE(email); không cần tạo index B-tree trùng.

-- Hash (chỉ equality)
CREATE INDEX idx_users_email_hash ON users USING hash(email);

-- GIN cho JSONB (EruSentia có thể lưu template metadata dạng JSON)
CREATE INDEX idx_templates_meta ON templates USING gin(content_json);

-- GIN cho full-text search
CREATE INDEX idx_templates_search ON templates USING gin(to_tsvector('english', title));
```

---

## 3.4 Khi nào NÊN đánh Index?

### ✅ NÊN đánh

**1. Cột thường xuyên xuất hiện trong WHERE:**
```sql
-- Query phổ biến: tìm user theo email
SELECT * FROM users WHERE email = ?;
-- → Cần index trên email

-- Query phổ biến: template theo tác giả, chưa bị xóa
SELECT * FROM templates WHERE deleted_at IS NULL AND author_id = ?;
-- → Đối chiếu idx_templates_author_list đã có; không tạo index trùng.
```

**2. Cột dùng trong JOIN ON:**
```sql
SELECT * FROM templates t JOIN users u ON t.author_id = u.id;
-- → Cân nhắc index theo workload; JOIN không bắt buộc phải dùng index.
-- (PostgreSQL không tự tạo index ở phía cột FK; PK/UNIQUE phía được tham chiếu có index.)
```

**3. Cột dùng trong ORDER BY (với bảng lớn):**
```sql
SELECT * FROM templates ORDER BY created_at DESC LIMIT 20;
-- → Index trên created_at giúp tránh full sort
```

**4. Cột có cardinality cao:**
- **Cardinality** = số lượng giá trị phân biệt trong cột
- `email`: cardinality = số users (rất cao) → index rất hiệu quả
- `role`: ít giá trị, nhưng index vẫn có thể hữu ích khi lọc một nhóm hiếm
- `is_active`: hiệu quả phụ thuộc phân bố, điều kiện query và khả năng index-only scan

### ❌ KHÔNG NÊN đánh

**1. Bảng nhỏ:**
Sequential Scan trên bảng nhỏ thường nhanh hơn Index Scan vì overhead của index lookup.

**2. Cột cardinality thấp:**
```sql
-- Nếu 90% users có is_active = TRUE
-- Lấy phần lớn bảng → planner có thể chọn Seq Scan; còn tùy projection/cost/cache
CREATE INDEX idx_users_active ON users(is_active);  -- ít tác dụng
```

**3. Cột bị UPDATE liên tục:**
UPDATE có thể cần tạo index entries mới, tăng chi phí ghi; không rebuild toàn bộ index. HOT update có thể tránh cập nhật index khi đủ điều kiện.

**4. Quá nhiều index trên 1 bảng:**
- Index tăng chi phí ghi và dọn các phiên bản cũ (VACUUM). Partial index chỉ chứa rows phù hợp predicate.
- UPDATE phụ thuộc cột thay đổi/HOT; DELETE để lại tuple cần dọn, không xóa tức thì khỏi mọi index.
- Storage tăng đáng kể

---

## 3.5 Index đặc biệt

### Composite Index (Index nhiều cột)

```sql
-- Composite index: (col_a, col_b)
CREATE INDEX idx_templates_is_public_author ON templates(is_public, author_id);
```

**Leftmost prefix nói về thứ tự cột trong index, không phải thứ tự viết điều kiện `WHERE`.**
Các dòng dưới là mảnh điều kiện minh họa; `?` là chỗ điền giá trị, không phải placeholder của PostgreSQL.

```sql
-- Với B-tree (is_public, author_id):
WHERE is_public = ?                         -- giới hạn theo cột đầu
WHERE is_public = ? AND author_id = ?       -- giới hạn theo cả hai cột
WHERE author_id = ? AND is_public = ?       -- tương đương dòng trên
WHERE author_id = ?                      -- vẫn có thể dùng index,
                                        -- nhưng thiếu cột đầu có thể phải quét toàn index
```

PostgreSQL 16 giới hạn vùng quét hiệu quả bằng các điều kiện equality trên các cột
đầu và điều kiện range trên cột đầu tiên không có equality. Planner chọn index hay
sequential scan theo chi phí; không thể kết luận chỉ từ thứ tự viết các biểu thức `AND`.
Xem [Multicolumn Indexes — PostgreSQL 16](https://www.postgresql.org/docs/16/indexes-multicolumn.html).

**Thứ tự cột trong composite index:**
- Đặt cột **equality** (`=`) trước, cột **range** (`>`, `<`) sau
- Chọn thứ tự theo các query cần phục vụ (lọc, range, ORDER BY, prefix). Không có luật luôn đặt cardinality cao nhất trước.

```sql
-- Tốt: is_public (equality) trước, created_at (range) sau
CREATE INDEX idx_good ON templates(is_public, created_at);
WHERE is_public = TRUE AND created_at > '2024-01-01'  -- ✅

-- Kém: created_at (range) trước, is_public (equality) sau
CREATE INDEX idx_bad ON templates(created_at, is_public);
-- Equality trước thường giới hạn vùng quét tốt hơn cho dạng query này; vẫn cần EXPLAIN.
```

### Partial Index (Index có điều kiện)

```sql
-- Ví dụ chỉ index các template public (không có cột trạng thái draft/archived).
CREATE INDEX idx_published_templates ON templates(created_at DESC, author_id)
WHERE is_public = TRUE;

-- Kích thước phụ thuộc tỷ lệ rows thỏa điều kiện; không có tỷ lệ cố định.
-- Planner phải chứng minh predicate của query suy ra predicate của index.

-- Để sử dụng partial index, query phải có điều kiện tương ứng:
SELECT * FROM templates WHERE is_public = TRUE ORDER BY created_at DESC;
```

### Covering Index

```sql
-- Index chứa các cột query cần → có thể dùng Index Only Scan
CREATE INDEX idx_templates_listing ON templates(is_public, created_at DESC)
INCLUDE (id, title, rating_avg, author_id);

-- Chỉ tránh heap fetch khi visibility map đánh dấu page all-visible.
-- Xem Heap Fetches trong EXPLAIN; INCLUDE không bảo đảm luôn không đọc heap.
SELECT id, title, rating_avg, author_id
FROM templates
WHERE is_public = TRUE
ORDER BY created_at DESC
LIMIT 20;
```

---

## 3.6 EXPLAIN ANALYZE — Đọc Execution Plan

**EXPLAIN** cho biết PostgreSQL **dự định** làm gì (không thực sự chạy query).
**EXPLAIN ANALYZE** thực sự chạy query và cho biết **đã làm gì** (timing thực tế).

```sql
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'test@erusentia.com';
```

**Đọc output:**
```
Index Scan using users_email_key on users
  (cost=0.42..8.44 rows=1 width=89)
  (actual time=0.043..0.044 rows=1 loops=1)
  Index Cond: ((email)::text = 'test@erusentia.com')
Planning Time: 0.15 ms
Execution Time: 0.065 ms
```

| Trường | Ý nghĩa |
|--------|---------|
| `Index Scan` | Loại scan: Index Scan, Seq Scan, Index Only Scan, Bitmap Heap Scan |
| `cost=0.42..8.44` | Chi phí ước tính: startup_cost..total_cost (đơn vị tương đối, không phải ms) |
| `rows=1` | Số rows ước tính sẽ trả về |
| `width=89` | Kích thước trung bình mỗi row (bytes) |
| `actual time=0.043..0.044` | Thời gian startup..total của node (ms), trung bình mỗi loop |
| `rows=1` | Số rows thực tế trung bình mỗi loop |
| `loops=1` | Node này chạy bao nhiêu lần |
| `Planning Time` | Thời gian optimizer tính execution plan |
| `Execution Time` | Thời gian thực thi, tách riêng Planning Time; không gồm truyền kết quả tới client |

**Các loại Scan:** `Seq Scan` có thể tốt cho bảng nhỏ hoặc khi lấy phần lớn bảng;
`Index Scan` thường phù hợp khi chọn ít rows; `Bitmap Heap Scan` gom vị trí heap từ index;
`Index Only Scan` có thể giảm heap fetch khi visibility map cho phép. Không có thứ hạng
nhanh/chậm cố định: đọc plan và `BUFFERS` theo query/dataset thực tế.

---

## 3.7 Thực hành Benchmark trên EruSentia

### Bước 1: Tạo dữ liệu benchmark

```bash
# Chạy file benchmark tạo 100,000 bản ghi
docker exec -i sentia_postgres_dev psql -v ON_ERROR_STOP=1 -U postgres -d sentia_hub_db < sql/04_week01_benchmark.sql
```

### Bước 2: Query KHÔNG có index

```sql
\timing on

-- Bảng không có index
EXPLAIN ANALYZE
SELECT * FROM users_no_index WHERE email = 'bench_50000@erusentia.test';
-- Ghi số đo mới; ảnh lịch sử dùng query/dataset chưa được kiểm soát đồng nhất.
```

### Bước 3: Query CÓ index (bảng gốc)

```sql
EXPLAIN ANALYZE
SELECT * FROM users WHERE email = 'bench_50000@erusentia.test';
-- Chạy cùng email, cùng tập dữ liệu; không dùng số 0.112 ms của ảnh cũ cho query này.
```

Chạy `ANALYZE users; ANALYZE users_no_index;` trước khi đo. Dùng
`EXPLAIN (ANALYZE, BUFFERS)` nhiều lần, ghi số dòng, môi trường và tình trạng cache.
Ảnh cũ trong báo cáo dùng hai email khác nhau và không xác nhận cùng kích thước bảng;
tỷ số ~65 chỉ mô tả hai số đo đó, chưa chứng minh mức tăng tốc nhân quả của index.

### Bước 4: Test Foreign Key index

```sql
-- templates.author_id có index chưa?
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'templates' AND indexdef LIKE '%author_id%';

-- Backend có partial index (author_id, created_at DESC) WHERE deleted_at IS NULL.
-- Nó phục vụ query loại soft-delete; query toàn bộ rows cần được đánh giá riêng.
-- Chỉ thêm full index khi workload cần; IF NOT EXISTS chỉ kiểm tra tên index.

-- So sánh
EXPLAIN ANALYZE SELECT * FROM templates
WHERE author_id = 'a0000000-0000-0000-0000-000000000002' AND deleted_at IS NULL;
```

---

## 3.8 Quản lý Index

```sql
-- Xem tất cả index + kích thước
SELECT
    indexname,
    tablename,
    pg_size_pretty(pg_relation_size(indexname::regclass)) AS index_size,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexname::regclass) DESC;

-- Xóa index
DROP INDEX IF EXISTS idx_users_is_active;

-- Rebuild index (nếu index bị bloat sau nhiều UPDATE)
REINDEX INDEX idx_templates_author_list;

-- Ví dụ thêm index khi đã xác nhận cần cho workload:
CREATE INDEX CONCURRENTLY idx_templates_views ON templates(views_count);
-- Không chặn INSERT/UPDATE/DELETE như CREATE INDEX thường, nhưng vẫn lấy lock,
-- có thể chờ transaction, tốn thêm công và có thể để lại index INVALID nếu lỗi.
-- Không chạy CONCURRENTLY trong transaction block.
```

Nguồn: [Index-only scan](https://www.postgresql.org/docs/16/indexes-index-only-scans.html), [EXPLAIN](https://www.postgresql.org/docs/16/using-explain.html), [CREATE INDEX](https://www.postgresql.org/docs/16/sql-createindex.html), [HOT](https://www.postgresql.org/docs/16/storage-hot.html).

Đã đo lại trên Docker PostgreSQL 16.15 ngày 20/10/2026, cùng bảng/query và 10 mẫu mỗi pha; kết quả, điều kiện cache và giới hạn ghi trong [báo cáo](../WEEK1_REPORT.md).
