# 📄 PHẦN 4 — PHÂN TRANG (PAGINATION)

> Đối chiếu thực hành: [báo cáo và ảnh thực hành](../WEEK1_REPORT.md). Các lệnh shell trong bài chạy từ thư mục gốc repo `EruSentia-Server` sau khi setup theo [README backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).

---

## 4.1 Pagination là gì và tại sao cần?

**Pagination (phân trang)** là kỹ thuật chia tập kết quả lớn thành các **trang nhỏ hơn**,
trả về từng phần thay vì toàn bộ.

### Tại sao cần phân trang?

**Vấn đề không có phân trang:**
```sql
SELECT * FROM templates;  -- Giả sử hệ thống tăng tới 500,000 templates
```
- Server phải đọc 500,000 rows từ disk → tốn I/O
- Server phải serialize 500,000 objects thành JSON → tốn CPU + RAM
- Network phải truyền vài trăm MB → user chờ lâu, tốn bandwidth
- Browser phải render 500,000 items → crash tab

**Với phân trang (20 items/trang):**
- Client nhận tối đa 20 rows; DB vẫn có thể đọc/sort nhiều hơn tùy plan
- Response nhỏ, nhanh
- User thấy kết quả trong milliseconds

### Use cases thực tế

| Use case | Dữ liệu | Kỳ vọng |
|----------|---------|---------|
| Marketplace feed | Templates mới nhất | Load nhanh, scroll vô tận |
| Admin dashboard | Quản lý user | Nhảy đến trang N, biết tổng số |
| Search results | Kết quả tìm kiếm | Top N relevant nhất |
| API pagination | Client API | Predictable, cacheable |

---

## 4.2 Offset-based Pagination

### Định nghĩa

**Offset-based pagination** sử dụng `LIMIT` và `OFFSET` để "bỏ qua" N rows đầu và lấy M rows tiếp theo.

```sql
SELECT columns FROM table
ORDER BY sort_column
LIMIT  page_size           -- số rows mỗi trang
OFFSET (page - 1) * page_size;  -- bỏ qua bao nhiêu rows
```

### Cách hoạt động

```
Page 1: LIMIT 20 OFFSET 0   → đọc rows 1-20   → trả rows 1-20
Page 2: LIMIT 20 OFFSET 20  → đọc rows 1-40   → bỏ 20, trả rows 21-40
Page 3: LIMIT 20 OFFSET 40  → đọc rows 1-60   → bỏ 40, trả rows 41-60
...
Page 100: LIMIT 20 OFFSET 1980 → đọc rows 1-2000 → bỏ 1980, trả rows 1981-2000
```

**Quan sát quan trọng:** Ở page 100, PostgreSQL vẫn phải đọc qua 1980 rows rồi mới lấy 20 rows cuối. Đây là nguồn gốc của vấn đề hiệu năng.

### Thực hành

```sql
-- Page 1: 20 templates published mới nhất
SELECT id, title, rating_avg, created_at
FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
ORDER BY created_at DESC, id DESC
LIMIT 20 OFFSET 0;

-- Page 2
SELECT id, title, rating_avg, created_at
FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
ORDER BY created_at DESC, id DESC
LIMIT 20 OFFSET 20;

-- API thường cần cả total count để tính tổng số trang
SELECT COUNT(*) FROM templates WHERE is_public = TRUE AND deleted_at IS NULL;
-- totalPages = CEILING(total / pageSize)

-- Xem execution plan với OFFSET nhỏ vs lớn
\timing on
EXPLAIN ANALYZE
SELECT id, title FROM templates WHERE is_public = TRUE AND deleted_at IS NULL
ORDER BY created_at DESC, id DESC LIMIT 20 OFFSET 0;

EXPLAIN ANALYZE
SELECT id, title FROM templates WHERE is_public = TRUE AND deleted_at IS NULL
ORDER BY created_at DESC, id DESC LIMIT 20 OFFSET 10000;
-- Xem rows của node con bên dưới Limit: OFFSET không phải Rows Removed by Filter.
```

### Ưu điểm Offset-based

- ✅ **Đơn giản:** dễ implement, dễ hiểu
- ✅ **Nhảy đến trang N:** client chỉ cần tính `OFFSET = (N-1) * pageSize`
- ✅ **Biết tổng số trang:** `COUNT(*)` + pageSize → totalPages
- ✅ **Phù hợp pagination bar:** UI hiển thị "1 2 3 ... 50"

### Nhược điểm Offset-based

**Nhược điểm 1: Hiệu năng giảm với OFFSET lớn**
```
OFFSET 0:      scan 20 rows       → nhanh
OFFSET 10000:  scan 10020 rows, skip 10000  → chậm hơn
OFFSET 100000: scan 100020 rows, skip 100000 → rất chậm
```
PostgreSQL KHÔNG thể "nhảy" trực tiếp đến OFFSET mà phải đọc và bỏ qua tuần tự.

**Nhược điểm 2: Dữ liệu thay đổi gây inconsistency**
```
User đọc page 1 (items 1-20)
Creator A thêm template mới → template mới trở thành item #1

User chuyển page 2 (OFFSET 20) → item #20 cũ xuất hiện lại ở vị trí #21 mới
→ Trùng item cuối page 1. Nếu một item trước cursor bị xóa, OFFSET có thể bỏ sót item.
```

---

## 4.3 Cursor-based Pagination (Keyset Pagination)

### Định nghĩa

**Cursor-based pagination** (hay keyset pagination) dùng **giá trị của row cuối cùng** làm "con trỏ" (cursor) để xác định điểm bắt đầu của trang tiếp theo.

Thay vì "bỏ qua N rows", ta nói "lấy rows **sau** row này".

### Tại sao hiệu quả?

```
Offset-based: "Bỏ qua 10,000 rows đầu" → phải đọc 10,000 rows
Cursor-based: "Lấy rows có created_at < '2024-03-15'" → Index ngay lập tức
```

Với index trên cột cursor, DB có thể **nhảy thẳng** đến điểm đúng trong B-Tree.

### Cách hoạt động

```sql
-- Trang đầu tiên (không có cursor)
SELECT id, title, rating_avg, created_at
FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
ORDER BY created_at DESC, id DESC  -- id làm tiebreaker
LIMIT 20;

-- → Row cuối: created_at = '2024-03-15 10:30:00+07', id = 'a0000000-0000-0000-0000-000000000001'
-- → Đây là cursor cho trang tiếp

-- Trang tiếp (dùng cursor)
SELECT id, title, rating_avg, created_at
FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
  AND (created_at, id) < ('2024-03-15 10:30:00+07', 'a0000000-0000-0000-0000-000000000001')
  --                ↑ Row comparison: lấy rows "nhỏ hơn" cursor
ORDER BY created_at DESC, id DESC
LIMIT 20;
```

### Tại sao cần tiebreaker (id)?

`created_at` có thể trùng nhau (nhiều templates tạo cùng giây).
Nếu chỉ dùng `created_at` làm cursor:
```
Templates: [T1: 10:30, T2: 10:30, T3: 10:30, T4: 10:29]
Page 1 cursor: created_at = 10:30 (lấy được T1)
Page 2 query: WHERE created_at < 10:30
→ Bỏ qua T2, T3 (cũng có created_at = 10:30)!
```

Với composite cursor `(created_at, id)`:
- Cặp `(created_at, id)` xác định thứ tự duy nhất, tránh bỏ sót do trùng timestamp trên tập dữ liệu ổn định.
- Các request khác snapshot vẫn có thể thấy thiếu/trùng nếu sort key hay điều kiện lọc bị sửa; keyset không tự đóng băng dữ liệu.

### Tiebreaker phải là cột gì?

- Tổ hợp sort key + tiebreaker phải unique, non-NULL cho ví dụ row comparison này
- Nên có **composite index** cùng thứ tự sort/filter, không chỉ index riêng từng cột
- `id` (UUID/SERIAL) là lựa chọn phổ biến nhất

### Encode cursor cho API

```typescript
// Base64 chỉ mã hóa biểu diễn, không bảo mật hoặc chống sửa cursor.
// Ví dụ encode/decode; endpoint thực phải validate schema/giới hạn độ dài,
// giữ nguyên độ chính xác timestamp và dùng query parameters $1, $2.

// Encode
const cursor = Buffer.from(JSON.stringify({
  created_at: lastRow.cursor_created_at,  // lấy nguyên chuỗi timestamp từ DB
  id: lastRow.id
})).toString('base64');
// Giá trị output phụ thuộc row cuối thực tế.

// Decode
const decoded = JSON.parse(Buffer.from(cursor, 'base64').toString());
// Validate timestamp/UUID và dùng decoded trong parameterized query.
```

### Thực hành

```sql
-- Page 1
SELECT id, title, rating_avg, created_at
FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
ORDER BY created_at DESC, id DESC
LIMIT 5;  -- dùng 5 để dễ test

-- Ghi lại row cuối: created_at = '???', id = '???'

-- Page 2 (thay giá trị thực vào)
SELECT id, title, rating_avg, created_at
FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
  AND (created_at, id) < ('<created_at_của_row_cuối>', '<id_của_row_cuối>')
ORDER BY created_at DESC, id DESC
LIMIT 5;

-- Tạo index để cursor query hiệu quả
CREATE INDEX IF NOT EXISTS idx_templates_cursor
ON templates(created_at DESC, id DESC)
WHERE is_public = TRUE AND deleted_at IS NULL;

-- Xem execution plan
EXPLAIN ANALYZE
SELECT id, title FROM templates
WHERE is_public = TRUE AND deleted_at IS NULL
  AND (created_at, id) < (NOW() - INTERVAL '7 days', 'ffffffff-ffff-ffff-ffff-ffffffffffff')
ORDER BY created_at DESC, id DESC
LIMIT 20;
-- Planner chọn theo cost; bảng nhỏ vẫn có thể Seq Scan. Kiểm tra plan, không ép tên scan.
```

### Ưu điểm Cursor-based

- ✅ **Tránh chi phí deep OFFSET:** với index phù hợp, gần O(log n + k) cho k rows; còn phụ thuộc lọc/cache/I/O.
- ✅ **Ổn định hơn trước insert/delete phía trước cursor:** vẫn cần sort key ổn định và chính sách snapshot phù hợp.
- ✅ **Phù hợp infinite scroll:** TikTok, Instagram, Twitter feed

### Nhược điểm Cursor-based

- ❌ **Không nhảy đến trang N:** phải đi tuần tự qua các cursor
- ⚠️ **Không tự có tổng:** vẫn có thể chạy `COUNT(*)` riêng nhưng tốn thêm chi phí và có thể khác snapshot.
- ⚠️ **Tổ hợp cursor phải unique:** index phù hợp giúp hiệu năng, không phải điều kiện cú pháp.
- ❌ **Phức tạp hơn** để implement và debug

---

## 4.4 Bảng so sánh toàn diện

| Tiêu chí | Offset-based | Cursor-based |
|---------|-------------|-------------|
| **Hiệu năng trang đầu** | ✅ Nhanh | ✅ Nhanh |
| **Hiệu năng trang cuối (OFFSET lớn)** | ❌ Chậm dần | ✅ Tránh quét phần OFFSET, phụ thuộc plan |
| **Nhảy đến trang N** | ✅ Có | ❌ Không |
| **Biết tổng số trang** | ✅ Có (cần COUNT) | Có thể COUNT riêng |
| **Consistent khi data thay đổi** | ❌ Có thể miss/dup | Ổn định hơn, không bảo đảm snapshot |
| **Complexity** | ✅ Đơn giản | ⚠️ Phức tạp hơn |
| **Use case EruSentia** | Admin dashboard | Marketplace feed |
| **UI phù hợp** | Pagination bar | Infinite scroll / "Load more" |

---

## 4.5 Khi nào dùng cái nào?

**Dùng Offset-based khi:**
- Admin interface cần nhảy đến trang N
- Cần hiển thị tổng số trang
- OFFSET thực tế nhỏ và độ trễ đo được đáp ứng yêu cầu; không có ngưỡng số rows cố định
- Ví dụ EruSentia: Admin quản lý users, admin xem danh sách reports

**Dùng Cursor-based khi:**
- Public API với dataset lớn
- Infinite scroll (mobile apps)
- Real-time data (data thay đổi liên tục)
- Ví dụ EruSentia: Marketplace feed, API cho mobile app

> Node-postgres mặc định chuyển timestamp thành JavaScript `Date` (mili giây), trong khi PostgreSQL có độ chính xác microsecond. Cursor phải giữ nguyên giá trị sort ở DB (ví dụ lấy thêm `created_at::text AS cursor_created_at`), tránh làm tròn rồi mất row ở ranh giới trang. API phân trang là thiết kế minh họa; backend tuần 1 chưa có HTTP endpoint.

Nguồn: [LIMIT/OFFSET](https://www.postgresql.org/docs/16/queries-limit.html), [row comparisons](https://www.postgresql.org/docs/16/functions-comparisons.html#ROW-WISE-COMPARISON), [node-postgres types](https://node-postgres.com/features/types).
