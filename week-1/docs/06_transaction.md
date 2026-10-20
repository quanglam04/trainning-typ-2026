# 💳 PHẦN 6 — TRANSACTION (ACID + Thực hành EruSentia)

> Mã thực thi: [transaction trong seed](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/scripts/seed.ts).
> Setup theo [README backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).
> SQL dưới dùng schema hiện có; các hàm TypeScript là thiết kế minh họa, chưa phải endpoint đã triển khai.

## 6.1 Transaction là gì?

Transaction gom các thao tác cần thành công cùng nhau thành một đơn vị công việc.
`COMMIT` xác nhận thay đổi; `ROLLBACK` hủy thay đổi chưa commit. Ứng dụng phải phát hiện
cả lỗi SQL và trường hợp nghiệp vụ như `UPDATE 0` — PostgreSQL không coi 0 rows là lỗi.

Ví dụ phù hợp EruSentia: công khai một template, cộng điểm tác giả và ghi `audit_logs`.
Đây là quy tắc nghiệp vụ minh họa, chưa phải luồng publish đã triển khai. Schema dùng
`is_public`, không có `status`, `price` hoặc `activity_log`. `templates.version` là chuỗi
phiên bản nội dung (`1.0.0`), không phải counter optimistic lock của bảng lab `template_slots`.

## 6.2 ACID Properties

| Tính chất | Ý nghĩa và giới hạn |
|-----------|---------------------|
| Atomicity | Các thay đổi thuộc phạm vi commit cùng thành công hoặc bị hủy. Sequence/SERIAL không hoàn lại giá trị đã cấp khi rollback. |
| Consistency | DB kiểm tra constraints đã khai báo; application chịu trách nhiệm quy tắc nghiệp vụ còn lại. Transaction không tự làm mọi logic đúng. |
| Isolation | Cách các transaction thấy/tương tác với nhau phụ thuộc isolation level. READ COMMITTED ngăn dirty read nhưng không tương đương chạy tuần tự. |
| Durability | Với cấu hình bền vững thông thường (`fsync`/`synchronous_commit` bật), WAL được flush trước khi xác nhận commit. Không thay thế backup hay bảo đảm trước mất toàn bộ ổ lưu trữ. |

PostgreSQL dùng MVCC để quản lý phiên bản row và WAL cho crash recovery. WAL replay
khôi phục thay đổi; các phiên bản của transaction chưa commit không trở thành dữ liệu
đã commit. Không nên mô tả recovery như việc phát lại một SQL `ROLLBACK` cho từng lệnh.

Ở READ COMMITTED, mỗi statement có snapshot mới và nhìn thấy thay đổi trước đó của
chính transaction mình. REPEATABLE READ giữ snapshot, nhưng vẫn có serialization anomaly
như write skew. SERIALIZABLE bảo đảm các transaction commit có hiệu ứng tương đương
một thứ tự tuần tự và có thể trả lỗi yêu cầu retry. Xem [bảng isolation](05_locking.md#52-transaction-isolation-levels).

## 6.3 Cú pháp Transaction

```sql
BEGIN;                         -- hoặc START TRANSACTION
SELECT COUNT(*) FROM templates;
COMMIT;                        -- hoặc END

BEGIN;
SELECT COUNT(*) FROM templates;
ROLLBACK;
```

Khi không có transaction block, mỗi statement thường được client thực hiện trong
transaction riêng (autocommit). Trong transaction block, lỗi SQL thông thường làm
transaction aborted: cần `ROLLBACK`, hoặc `ROLLBACK TO SAVEPOINT` hợp lệ, trước khi tiếp tục.
Vi phạm constraint không tự gửi `ROLLBACK` thay cho ứng dụng.

## 6.4 Demo: Transaction với ROLLBACK

Chạy trong database thực hành đã seed. Query chọn một profile cố định theo `id`;
`ROLLBACK` giữ nguyên điểm sau demo:

```sql
BEGIN;
WITH chosen AS (SELECT id FROM profiles ORDER BY id LIMIT 1)
UPDATE profiles p
SET reputation_score = reputation_score + 9999
FROM chosen WHERE p.id = chosen.id
RETURNING p.id, p.reputation_score;
ROLLBACK;
SELECT id, reputation_score FROM profiles ORDER BY id LIMIT 1;
```

Nhiều DDL PostgreSQL cũng rollback được, nhưng không phải mọi lệnh đều chạy trong
transaction block (ví dụ `CREATE INDEX CONCURRENTLY`, `CREATE DATABASE`). Không xem
`ROLLBACK` là cơ chế hoàn tác email, file, API hoặc các tác động bên ngoài DB.

## 6.5 Khi nào cần Transaction?

Khi các thao tác DB phải thành công cùng nhau, dùng transaction và kiểm tra kết quả từng bước.
Một statement SQL riêng lẻ vốn đã atomic; nhiều statement độc lập không nhất thiết cần gom chung.

### Tạo User + Profile

Dùng CTE chuyển ID vừa tạo sang profile, không cần chép UUID bằng tay. Chạy thử rồi rollback:

```sql
BEGIN;
WITH new_user AS (
  INSERT INTO users (email, password_hash, role)
  VALUES ('transaction-lab@example.test', 'demo-only-not-a-real-hash', 'user')
  RETURNING id
)
INSERT INTO profiles (user_id, username, display_name)
SELECT id, 'transaction_lab', 'Transaction Lab' FROM new_user
RETURNING id, user_id;
ROLLBACK;
```

Nếu email/username lab đã tồn tại, statement trả lỗi UNIQUE; chạy `ROLLBACK` và dùng
định danh lab khác. Chuỗi hash ở đây chỉ là dữ liệu giả cho bài SQL.

### Công khai hoặc xóa mềm Template

Cần kiểm tra quyền tác giả và trạng thái hiện tại trong `WHERE`, kiểm tra `rowCount`;
chỉ cộng/trừ điểm sau khi thay đổi template thành công. Retry không được cộng điểm hai lần.
Soft delete cần `deleted_at IS NULL` để lần gọi lặp không trừ điểm lần nữa. Đây là thiết kế
minh họa; chưa có service/API thực thi trong backend tuần 1.

## 6.6 Savepoint — Checkpoint trong Transaction

Ví dụ độc lập, không phụ thuộc ID dữ liệu seed:

```sql
BEGIN;
CREATE TEMP TABLE transaction_lab (id INT PRIMARY KEY, points INT NOT NULL) ON COMMIT DROP;
INSERT INTO transaction_lab VALUES (1, 10);
SAVEPOINT after_initial;
UPDATE transaction_lab SET points = points + 1000 WHERE id = 1;
ROLLBACK TO SAVEPOINT after_initial;
SELECT * FROM transaction_lab;  -- points = 10
RELEASE SAVEPOINT after_initial;
COMMIT;                        -- bảng TEMP được dọn bởi ON COMMIT DROP
```

Savepoint cho phép bỏ một phần công việc khi nghiệp vụ cho phép. Nó không làm thay đổi
đã rollback xuất hiện trong kết quả commit. `GET DIAGNOSTICS ... ROW_COUNT` thuộc PL/pgSQL,
không phải câu lệnh SQL độc lập để gõ trực tiếp trong psql; ở Node dùng `result.rowCount`.

## 6.7 Transaction trong Application Code (TypeScript)

Helper minh họa dùng một `PoolClient` cho toàn bộ transaction, giữ lỗi gốc nếu rollback
cũng lỗi, và loại connection lỗi khỏi pool. Import type tách riêng để dùng được với
TypeScript strict. Callback chỉ dùng `client` được cấp, không dùng `pool.query()`:

```typescript
import { Pool } from 'pg';
import type { PoolClient } from 'pg';

async function withTransaction<T>(
  pool: Pool,
  operation: (client: PoolClient) => Promise<T>,
): Promise<T> {
  const client = await pool.connect();
  let discardClient = false;
  try {
    await client.query('BEGIN');
    const value = await operation(client);
    await client.query('COMMIT');
    return value;
  } catch (error) {
    try {
      await client.query('ROLLBACK');
    } catch {
      discardClient = true;
    }
    throw error;
  } finally {
    client.release(discardClient);
  }
}

// Business rule minh họa: chỉ cộng điểm khi chuyển private → public thành công.
async function publishTemplate(pool: Pool, templateId: string, authorId: string) {
  return withTransaction(pool, async (client) => {
    const changed = await client.query<{ id: string; title: string }>(
      `UPDATE templates SET is_public = TRUE, updated_at = NOW()
       WHERE id = $1 AND author_id = $2 AND is_public = FALSE AND deleted_at IS NULL
       RETURNING id, title`,
      [templateId, authorId],
    );
    if (changed.rowCount !== 1) throw new Error('Template absent, forbidden, or already public');

    const profile = await client.query(
      `UPDATE profiles SET reputation_score = reputation_score + 10, updated_at = NOW()
       WHERE user_id = $1`,
      [authorId],
    );
    if (profile.rowCount !== 1) throw new Error('Author profile missing');

    await client.query(
      `INSERT INTO audit_logs (user_id, action, entity_type, entity_id)
       VALUES ($1, 'template.published', 'template', $2)`,
      [authorId, templateId],
    );
    return changed.rows[0];
  });
}
```

Caller sở hữu và đóng `pool` khi kết thúc CLI/app. Trong API thực tế, `authorId` lấy từ
ngữ cảnh xác thực đã kiểm tra. Các lỗi deadlock `40P01`/serialization `40001` có thể được
retry toàn bộ transaction theo giới hạn và backoff phù hợp. Mất kết nối lúc COMMIT có thể
khiến client không biết giao dịch đã commit hay chưa; cần cơ chế đối soát/idempotency,
không retry mù. Không gửi email trực tiếp trong callback có thể retry; cần thiết kế outbox
nếu đòi hỏi việc gửi gắn với commit.

## 6.8 Transaction và Connection Pool

`pool.connect()` → `client.query('BEGIN')` → tất cả query dùng cùng `client` →
`COMMIT`/`ROLLBACK` → `client.release()`. Dùng `pool.query()` cho từng bước có thể chọn
các connection khác nhau, làm mất ranh giới transaction mong muốn.

Nguồn: [Transactions — node-postgres](https://node-postgres.com/features/transactions),
[Transaction Isolation](https://www.postgresql.org/docs/16/transaction-iso.html),
[WAL](https://www.postgresql.org/docs/16/wal-intro.html),
[Asynchronous Commit](https://www.postgresql.org/docs/16/wal-async-commit.html).
