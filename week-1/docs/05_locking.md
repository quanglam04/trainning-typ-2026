# 🔒 PHẦN 5 — LOCKING (Lý thuyết đầy đủ + Demo EruSentia)

> Đối chiếu thực hành: [báo cáo và ảnh thực hành](../WEEK1_REPORT.md). Các lệnh shell trong bài chạy từ thư mục gốc repo `EruSentia-Server` sau khi setup theo [README backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).

---

## 5.1 Tại sao cần Locking?

### Vấn đề: Concurrent Access

Database thường phục vụ **nhiều kết nối đồng thời** (concurrent connections).
Khi nhiều transaction cùng đọc/ghi một dữ liệu, có thể xảy ra **data anomalies** (dị thường dữ liệu).

### Các dạng Data Anomaly

#### 1. Lost Update (Mất cập nhật)
```
T1 đọc: reputation = 100
T2 đọc: reputation = 100
T1 tính: 100 + 10 = 110, ghi lại
T2 tính: 100 + 20 = 120, ghi lại  ← ghi đè kết quả của T1!
Kết quả: 120 (đúng ra phải là 130)
```

#### 2. Dirty Read (Đọc dữ liệu bẩn)
```
T1 bắt đầu: cập nhật balance = 0 (đang trong transaction, chưa COMMIT)
T2 đọc: thấy balance = 0 (đọc uncommitted data!)
T1 ROLLBACK: balance về lại giá trị cũ
T2 đã hành động dựa trên dữ liệu không bao giờ tồn tại!
```

#### 3. Non-Repeatable Read
```
T1 đọc: price = 99000
T2 UPDATE: price = 199000, COMMIT
T1 đọc lại trong cùng transaction: price = 199000 (đã thay đổi!)
```

#### 4. Phantom Read
```
T1 đếm: SELECT COUNT(*) WHERE status='published' → 100
T2 INSERT thêm 5 published templates, COMMIT
T1 đếm lại: SELECT COUNT(*) WHERE status='published' → 105
(records "ma" xuất hiện trong transaction của T1)
```

MVCC, isolation level, row lock và điều kiện UPDATE phối hợp kiểm soát truy cập đồng thời. Dirty read là khái niệm tổng quát; PostgreSQL không cho dirty read ở bất kỳ isolation level nào.

---

## 5.2 Transaction Isolation Levels

Chuẩn SQL có 4 tên mức cô lập; PostgreSQL chỉ thực thi 3 hành vi khác nhau:
`READ UNCOMMITTED` được ánh xạ thành `READ COMMITTED`.

| Mức trong PostgreSQL | Dirty read | Non-repeatable read | Phantom read | Serialization anomaly |
|---------------------|------------|---------------------|--------------|-----------------------|
| READ UNCOMMITTED / READ COMMITTED (mặc định) | Không | Có thể | Có thể | Có thể |
| REPEATABLE READ | Không | Không | Không | Có thể, ví dụ write skew |
| SERIALIZABLE | Không | Không | Không | Không với các transaction commit thành công |

READ COMMITTED lấy snapshot mới cho mỗi statement và vẫn thấy thay đổi của chính
transaction mình. REPEATABLE READ/SERIALIZABLE có thể yêu cầu retry transaction
(`40001`). Các lệnh sau là hai lựa chọn độc lập, không chạy hai BEGIN lồng nhau:

```sql
BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT COUNT(*) FROM templates;
ROLLBACK;

BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SELECT COUNT(*) FROM templates;
ROLLBACK;
```

Nguồn: [Transaction Isolation — PostgreSQL 16](https://www.postgresql.org/docs/16/transaction-iso.html).

---

## 5.3 Pessimistic Locking

### Định nghĩa

**Pessimistic Locking** (Khóa bi quan) = giả định rằng **conflict sẽ xảy ra**,
do đó **khóa row lại ngay khi đọc** để không ai khác có thể sửa cho đến khi transaction hoàn tất.

Tên gọi "bi quan" vì: "tôi bi quan, tôi nghĩ sẽ có người khác muốn sửa dữ liệu này — nên tôi khóa trước".

### Cơ chế hoạt động

```sql
BEGIN;

SELECT * FROM template_slots WHERE id = 1
FOR UPDATE;   -- ← Ghi lock vào row này

-- Các session khác muốn SELECT ... FOR UPDATE cùng row:
-- → BỊ BLOCK, phải đợi transaction này COMMIT hoặc ROLLBACK

UPDATE template_slots SET slots = slots - 1 WHERE id = 1;

COMMIT;  -- → Release lock, session đang chờ sẽ được thực thi
```

### Các variants của SELECT FOR UPDATE

Các lệnh bên dưới là lựa chọn độc lập. Muốn giữ row lock qua bước xử lý tiếp theo,
chạy trong BEGIN/COMMIT; ở autocommit lock kết thúc ngay sau statement.

```sql
-- Đợi vô thời hạn nếu bị lock (mặc định)
SELECT * FROM template_slots WHERE id = 1 FOR UPDATE;

-- Không đợi, lỗi ngay nếu bị lock
SELECT * FROM template_slots WHERE id = 1 FOR UPDATE NOWAIT;
-- → ERROR: could not obtain lock on row in relation "template_slots"

-- Bỏ qua rows đang bị lock (lấy rows khác không bị lock)
SELECT * FROM template_slots WHERE id IN (1,2,3) FOR UPDATE SKIP LOCKED;
-- → Hữu ích cho job queue: worker A lấy item đầu, worker B tự động lấy item tiếp theo

-- FOR SHARE xung đột với FOR NO KEY UPDATE và FOR UPDATE, không xung đột FOR SHARE
SELECT * FROM template_slots WHERE id = 1 FOR SHARE;
```

### Lock Types trong PostgreSQL

Các row lock xung đột giữa **hai transaction khác nhau** trên cùng row:

| Lock yêu cầu | Xung đột với lock đang giữ |
|--------------|---------------------------|
| `FOR UPDATE` | UPDATE, NO KEY UPDATE, SHARE, KEY SHARE |
| `FOR NO KEY UPDATE` | UPDATE, NO KEY UPDATE, SHARE |
| `FOR SHARE` | UPDATE, NO KEY UPDATE |
| `FOR KEY SHARE` | UPDATE |

`UPDATE` tự lấy row lock (thường NO KEY UPDATE; UPDATE nếu sửa khóa thuộc trường hợp
PostgreSQL quy định). Row lock không chặn SELECT thường dùng MVCC. `NOWAIT`/`SKIP LOCKED`
chỉ đổi cách đợi row lock, không loại bỏ mọi khả năng chờ table lock.

### Demo Pessimistic Lock — 2 Terminal

**Setup (database thực hành, đóng các transaction demo cũ trước khi reset):**
```sql
CREATE TABLE IF NOT EXISTS template_slots (
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100),
    slots   INT NOT NULL DEFAULT 0 CHECK (slots >= 0),
    version INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
INSERT INTO template_slots (id, name, slots, version)
VALUES (1, 'Limited Edition Pack', 1, 0), (2, 'Second Pack', 10, 0)
ON CONFLICT (id) DO UPDATE
SET slots = EXCLUDED.slots, version = 0, updated_at = NOW();
```

**Terminal 1:**
```sql
BEGIN;
SELECT id, slots FROM template_slots WHERE id = 1 FOR UPDATE;
-- → slots = 1, session này đang giữ lock
-- DỪNG LẠI đây, không COMMIT ngay
```

**Terminal 2 (mở ngay sau):**
```sql
BEGIN;
SELECT id, slots FROM template_slots WHERE id = 1 FOR UPDATE;
-- → BỊ BLOCK (đứng chờ, không trả về kết quả)
```

**Quay Terminal 1:**
```sql
UPDATE template_slots SET slots = slots - 1 WHERE id = 1;
COMMIT;  -- → Terminal 2 tự động UNBLOCK
```

**Terminal 2 (sau khi unblock):**
```sql
-- Giờ mới nhận được kết quả
SELECT id, slots FROM template_slots WHERE id = 1 FOR UPDATE;
-- → slots = 0 (đã hết!)
-- Xử lý: kiểm tra slots <= 0 → ROLLBACK
ROLLBACK;
```

### Ưu điểm Pessimistic

- ✅ **Bảo vệ đoạn read-modify-write trên row đã khóa** nếu mọi luồng ghi tuân thủ quy tắc nghiệp vụ.
- ⚠️ **Vẫn cần xử lý lỗi:** deadlock/serialization failure có thể yêu cầu retry transaction.
- ✅ **Phù hợp conflict rate cao:** nhiều user cùng tranh 1 resource

### Nhược điểm Pessimistic

- ❌ **Throughput thấp:** các session xếp hàng chờ nhau
- ❌ **Nguy cơ Deadlock:** A chờ B, B chờ A
- ❌ **Giữ lock khi xử lý lâu làm tăng thời gian chờ:** SELECT thường không cần thêm FOR UPDATE cho đọc thuần túy.

---

## 5.4 Optimistic Locking

### Định nghĩa

**Optimistic Locking** (Khóa lạc quan) = giả định rằng **conflict hiếm khi xảy ra**,
do đó **không khóa khi đọc** — chỉ kiểm tra conflict **tại thời điểm ghi**.

Nếu phát hiện conflict → đọc lại và quyết định retry có giới hạn hoặc báo người dùng; không ghi đè mù.

Tên gọi "lạc quan" vì: "tôi lạc quan, tôi nghĩ không ai tranh với tôi — nên tôi đọc mà không lock".

### Cơ chế: Version Column

Thêm cột `version` vào bảng. Mỗi lần UPDATE thành công → version tăng lên 1.

```sql
ALTER TABLE template_slots ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0;
```

**Quy trình:**
```
1. READ: SELECT id, slots, version FROM template_slots WHERE id = 1
   → version = 3

2. Business logic: tính toán, xử lý...

3. WRITE: UPDATE template_slots
          SET slots = new_value, version = version + 1
          WHERE id = 1 AND version = 3   ← điều kiện then chốt!

4. Kiểm tra rowsAffected:
   - rowsAffected = 1 → THÀNH CÔNG (version vẫn là 3 khi ta ghi)
   - rowsAffected = 0 → không còn row khớp điều kiện (version đổi, row bị xóa, hoặc ID không tồn tại)
   → Xử lý nghiệp vụ, có thể đọc lại và retry có giới hạn
```

### Demo Optimistic Lock — 2 Terminal

Đóng các transaction cũ, reset trước khi mở hai terminal:

```sql
UPDATE template_slots SET slots = 10, version = 0 WHERE id IN (1, 2);
```

**Terminal 1:**
```sql
BEGIN;
SELECT slots, version FROM template_slots WHERE id = 1;
-- → slots=10, version=0
-- Giả vờ đang xử lý business logic (không làm gì cả, chờ T2)
```

**Terminal 2:**
```sql
BEGIN;
SELECT slots, version FROM template_slots WHERE id = 1;
-- → slots=10, version=0

UPDATE template_slots
SET slots = 9, version = version + 1, updated_at = NOW()
WHERE id = 1 AND version = 0;
-- → 1 row affected ✅ SUCCESS

COMMIT;
-- version bây giờ = 1
```

**Terminal 1 (thử ghi với version cũ):**
```sql
UPDATE template_slots
SET slots = 9, version = version + 1
WHERE id = 1 AND version = 0;  -- version = 0 không còn đúng nữa!
-- → 0 rows affected ← CONFLICT DETECTED

-- Phải retry: đọc lại version mới
SELECT slots, version FROM template_slots WHERE id = 1;
-- → slots=9, version=1
-- Quyết định: có muốn tiếp tục không?

ROLLBACK;
```

### Timestamp thay vì Version?

Một số hệ thống dùng `updated_at` thay vì `version`:
```sql
WHERE id = 1 AND updated_at = '2024-03-15 10:30:00.123456'
```

**Rủi ro:** timestamp có thể lặp và mất độ chính xác khi chuyển qua client. `NOW()` là thời điểm bắt đầu transaction, nên nhiều UPDATE trong cùng transaction nhận cùng giá trị. Counter tăng đơn điệu trên mỗi row dễ kiểm soát hơn; mọi luồng ghi phải cùng cập nhật counter. `templates.version` trong schema thật là VARCHAR phiên bản nội dung, không dùng như counter INT của lab.

### Ưu điểm Optimistic

- ✅ **Không giữ row lock từ bước đọc:** đọc thường không dùng `SELECT FOR UPDATE`.
- ⚠️ **Vẫn có blocking và deadlock khi ghi:** `UPDATE ... WHERE version = ...` vẫn lấy row lock.
  Hai transaction cập nhật nhiều row theo thứ tự ngược nhau có thể deadlock, dù đều kiểm tra version.
- ✅ **Phù hợp read-heavy:** phần lớn thời gian chỉ đọc, hiếm khi ghi đồng thời

### Nhược điểm Optimistic

- ❌ **Cần retry logic:** phức tạp hơn để implement đúng
- ❌ **Lãng phí khi conflict rate cao:** nhiều retry = nhiều work vô ích
- ❌ **Không phù hợp conflict rate cao:** booking vé concert → mọi người đều cùng tranh

---

## 5.5 Deadlock

### Định nghĩa

**Deadlock** = tình trạng **hai (hoặc nhiều) transaction đang chờ nhau**,
không transaction nào có thể tiến hành được.

```
Transaction A: Đang giữ lock trên row 1, muốn lock row 2
Transaction B: Đang giữ lock trên row 2, muốn lock row 1
→ A chờ B release row 2
→ B chờ A release row 1
→ Cả hai chờ nhau mãi mãi = DEADLOCK
```

### PostgreSQL phát hiện và xử lý Deadlock

PostgreSQL chủ động **phát hiện deadlock** định kỳ.
Khi phát hiện, PostgreSQL hủy một transaction với `40P01`; không đóng connection. Session đó cần `ROLLBACK`; transaction còn lại có thể tiếp tục.

```
ERROR: deadlock detected
DETAIL: Process 12345 waits for ShareLock on transaction 67890;
        blocked by process 67890.
        Process 67890 waits for ShareLock on transaction 12345;
        blocked by process 12345.
HINT: See server log for query details.
```

### Tái tạo Deadlock

Dùng setup hai row ở mục 5.3; kết thúc mỗi session bằng COMMIT hoặc ROLLBACK sau demo.

**Terminal 1:**
```sql
BEGIN;
SELECT * FROM template_slots WHERE id = 1 FOR UPDATE;
-- Đang giữ lock id=1, muốn lock id=2
```

**Terminal 2:**
```sql
BEGIN;
SELECT * FROM template_slots WHERE id = 2 FOR UPDATE;
-- Đang giữ lock id=2, muốn lock id=1
SELECT * FROM template_slots WHERE id = 1 FOR UPDATE;
-- → BLOCK
```

**Terminal 1:**
```sql
SELECT * FROM template_slots WHERE id = 2 FOR UPDATE;
-- → Một trong hai terminal sẽ nhận ERROR: deadlock detected
```

### Optimistic lock cũng có thể deadlock

Đóng các transaction cũ, chạy `UPDATE template_slots SET version = 0 WHERE id IN (1, 2);` ở autocommit. Ví dụ thứ tự lệnh trên **hai row đã tồn tại**, mỗi row bắt đầu ở `version = 0`:

| Bước | Session A | Session B |
|------|-----------|-----------|
| 1 | `BEGIN;` | `BEGIN;` |
| 2 | `UPDATE template_slots SET version = version + 1 WHERE id = 1 AND version = 0;` | |
| 3 | | `UPDATE template_slots SET version = version + 1 WHERE id = 2 AND version = 0;` |
| 4 | `UPDATE template_slots SET version = version + 1 WHERE id = 2 AND version = 0;` → chờ B | |
| 5 | | `UPDATE template_slots SET version = version + 1 WHERE id = 1 AND version = 0;` → tạo vòng chờ |

PostgreSQL hủy một transaction với SQLSTATE `40P01`. Session bị lỗi cần `ROLLBACK`;
ứng dụng có thể retry **toàn bộ transaction**, đọc lại version và kiểm tra nghiệp vụ,
với giới hạn số lần thử. Không chỉ chạy lại câu `UPDATE` lỗi trong transaction đã aborted.
Xem [Explicit Locking — PostgreSQL 16](https://www.postgresql.org/docs/16/explicit-locking.html#LOCKING-DEADLOCKS).

### Cách phòng tránh Deadlock

**Nguyên tắc vàng: Lock theo thứ tự nhất quán**
```sql
-- BAD: T1 lock (1, 2), T2 lock (2, 1) → deadlock
-- T1: lock id=1, rồi lock id=2
-- T2: lock id=2, rồi lock id=1

-- GOOD: Cả hai đều lock theo thứ tự id ASC
-- T1: lock id=1, rồi lock id=2
-- T2: lock id=1 (block, chờ T1), rồi lock id=2 (sau khi T1 xong)
-- → Không deadlock
```

```sql
-- Trong PostgreSQL: sort ids trước khi lock
SELECT * FROM template_slots WHERE id = ANY(ARRAY[1, 2])
ORDER BY id  -- ← đảm bảo lock theo thứ tự tăng dần
FOR UPDATE;
```

---

## 5.6 So sánh Pessimistic vs Optimistic

| Tiêu chí | Pessimistic | Optimistic |
|---------|------------|-----------|
| **Triết lý** | "Conflict sẽ xảy ra — lock trước" | "Conflict hiếm — check khi ghi" |
| **Cơ chế** | `SELECT FOR UPDATE` | version column + rowsAffected check |
| **Blocking** | Có khi lấy lock xung đột | Có thể xảy ra tại `UPDATE` |
| **Throughput** | Phụ thuộc thời gian giữ lock và contention | Phụ thuộc contention và số lần retry |
| **Deadlock risk** | Có | Có, do lock của `UPDATE` |
| **Retry logic** | Cần khi deadlock/serialization failure | Xử lý version conflict và lỗi transaction |
| **Conflict rate cao** | ✅ Phù hợp | ❌ Nhiều retry |
| **Read-heavy** | Chỉ lock đoạn cần read-modify-write | Thường phù hợp khi write conflict thấp |
| **Use case EruSentia** | Mua template giới hạn | Edit profile, update template |

Đã kiểm chứng hai connection trên Docker PostgreSQL 16.15 ngày 20/10/2026: blocking/unblock, UPDATE 0 với version cũ và deadlock 40P01. Kết quả ghi trong [báo cáo](../WEEK1_REPORT.md).
