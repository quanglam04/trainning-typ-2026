# Hướng dẫn chạy - Locking

## 1. Điều kiện trước khi chạy

Chạy:

```text
01-sql-basic/ddl.sql
01-sql-basic/seed.sql
```

Kiểm tra:

```sql
select id, title, available_copies, version
from books
where id = 1;
```

## 2. Demo Pessimistic Locking

Cần mở **2 session database độc lập**:

```text
Session A
Session B
```

### Bước 1 - Reset

```sql
update books
set available_copies = 1
where id = 1;
```

### Bước 2 - Session A giữ lock

Session A:

```sql
docker exec -it week1-postgres psql -U postgres -d week1_library

begin;

select id, title, available_copies
from books
where id = 1
for update;
```

Không `commit`.

Row `id = 1` đang bị Session A giữ lock.

### Bước 3 - Session B thử khóa cùng row

Session B:

```sql

docker exec -it week1-postgres psql -U postgres -d week1_library

begin;

select id, title, available_copies
from books
where id = 1
for update;
```

Query của Session B chưa trả kết quả ngay.

Nó phải chờ Session A giải phóng lock.

### Bước 4 - Session A cập nhật và commit

Session A:

```sql
update books
set available_copies = available_copies - 1
where id = 1
  and available_copies > 0;

commit;
```

Sau `commit`, Session B tiếp tục.

Kết thúc Session B:

```sql
rollback;
```

### Phân tích

Việc Session B phải chờ cho thấy Pessimistic Locking đang hoạt động.

## 3. Demo Optimistic Locking

### Bước 1 - Reset

```sql
update books
set available_copies = 1,
    version = 0
where id = 1;
```

### Bước 2 - Hai session cùng đọc

A và B cùng chạy:

```sql
select id, title, available_copies, version
from books
where id = 1;
```

Cả hai cùng thấy:

```text
version = 0
```

### Bước 3 - Session A update

```sql
update books
set available_copies = available_copies - 1,
    version = version + 1
where id = 1
  and version = 0
  and available_copies > 0;
```

Kết quả mong đợi:

```text
UPDATE 1
```

Row chuyển sang:

```text
version = 1
```

### Bước 4 - Session B dùng version cũ

B chạy lại cùng update với:

```text
version = 0
```

Kết quả:

```text
UPDATE 0
```

### Phân tích

Session B không ghi đè dữ liệu của Session A.

Version giúp phát hiện dữ liệu đã thay đổi kể từ lúc B đọc.

Ứng dụng có thể:

```text
retry
hoặc
trả lỗi conflict
```
### Luồng
Ban đầu Session A và Session B cùng đọc một bản ghi và nhận được cùng một `version`:

```text
A đọc: version = 0 ──┐
                     ├── cùng đọc version = 0
B đọc: version = 0 ──┘
```

Session A thực hiện cập nhật trước:

```text
A update với version = 0
→ thành công
→ available_copies = 0
→ version = 1
```

Sau đó Session B thực hiện cập nhật dựa trên `version = 0` đã đọc trước đó:

```text
B update với version = 0
→ thất bại: 0 rows affected
→ vì version hiện tại trong database đã là 1
```

### Luồng tổng quát

```text
        ┌── Session A đọc version = 0
        │
Database: version = 0
        │
        └── Session B đọc version = 0

        │
        ▼

Session A update
WHERE version = 0
        │
        ▼
    UPDATE 1
available_copies = 0
version = 1

        │
        ▼

Session B update
WHERE version = 0
        │
        ▼
    UPDATE 0
version hiện tại đã là 1
```

### Kết luận

Optimistic Locking không khóa bản ghi ngay khi đọc.

Hai Session vẫn có thể đọc dữ liệu cùng lúc. Khi cập nhật, `version` được sử dụng để kiểm tra dữ liệu có bị thay đổi kể từ lần đọc hay không.

- `UPDATE 1`: cập nhật thành công.
- `UPDATE 0`: dữ liệu đã bị Session khác thay đổi, xảy ra conflict.

Nhờ đó, Optimistic Locking ngăn việc một Session ghi đè lên dữ liệu mới hơn của Session khác mà không cần giữ lock trong suốt quá trình đọc.

## 4. Output

Cần chứng minh:

```text
- 2 transaction chạy đồng thời
- pessimistic: session thứ hai phải chờ
- optimistic: session thứ hai update 0
- phân biệt được khi nào dùng mỗi loại
```
