# Hướng dẫn chạy - Pagination

## 1. Điều kiện trước khi chạy

Chạy module `01-sql-basic` trước:

```text
ddl.sql
seed.sql
```

Bảng `books` phải có dữ liệu.

## 2. Thứ tự chạy pagination.sql

### Query 1 - Offset page 1

```sql
select id, title
from books
order by id
limit 3 offset 0;
```

Kết quả:

```text
lấy 3 bản ghi đầu tiên
```

### Query 2 - Offset page 2

```sql
select id, title
from books
order by id
limit 3 offset 3;
```

Kết quả:

```text
bỏ qua 3 bản ghi đầu
lấy 3 bản ghi tiếp theo
```

Với `page_size = 3`:

```text
page 1 -> offset 0
page 2 -> offset 3
```

### Query 3 - Cursor-based

```sql
select id, title
from books
where id > 3
order by id
limit 3;
```

Kết quả trả về các bản ghi đứng sau `id = 3`.

Trong thực tế, số `3` được thay bằng `id` cuối cùng của trang trước.

## 3. Phân tích

Ở dữ liệu nhỏ, hai cách đều chạy nhanh.

Khác biệt rõ hơn khi bảng rất lớn.

### Offset

Database vẫn phải xác định rồi bỏ qua các bản ghi trước vị trí offset.

Ví dụ:

```text
limit 20 offset 100000
```

có thể chậm khi offset tăng cao.

### Cursor

Query đi tiếp từ giá trị đã biết:

```text
where id > last_id
```

Nếu `id` được Index thì database có thể tìm vị trí bắt đầu hiệu quả hơn.

## 4. Output

Cần giải thích được:

```text
- tại sao cần pagination
- limit và offset hoạt động thế nào
- công thức offset
- cursor-based hoạt động thế nào
- ưu nhược điểm của hai cách
```
