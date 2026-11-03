# Locking trong Database

## 1. Tại sao cần Lock?

Database có thể được nhiều transaction truy cập cùng lúc.

Nếu hai transaction cùng đọc và sửa một bản ghi, có thể xảy ra xung đột dữ liệu.

Ví dụ:

```text
available_copies = 1
```

Hai transaction cùng đọc giá trị `1` và cùng thực hiện mượn sách có thể làm dữ liệu nghiệp vụ sai.

## 2. Optimistic Locking

Optimistic Locking giả định conflict ít xảy ra.

Không giữ database lock trong toàn bộ thời gian xử lý.

Một cách phổ biến là dùng cột:

```text
version
```

Khi đọc bản ghi, application ghi nhớ version.

Khi update:

```sql
update books
set version = version + 1
where id = ?
  and version = ?;
```

Nếu version đã bị transaction khác thay đổi thì điều kiện không còn khớp.

Kết quả:

```text
update 0
```

cho biết có conflict.

### Khi nên dùng

- đọc nhiều;
- ghi ít;
- conflict hiếm;
- không muốn giữ lock lâu.

## 3. Pessimistic Locking

Pessimistic Locking giả định conflict có khả năng xảy ra.

Transaction khóa row trước khi sửa.

Trong PostgreSQL:

```sql
select ...
for update;
```

Nếu transaction khác cũng muốn khóa cùng row, nó phải chờ transaction đang giữ lock kết thúc.

### Khi nên dùng

- conflict dễ xảy ra;
- tồn kho;
- đặt chỗ;
- tài chính;
- nghiệp vụ cần tính nhất quán cao.

## 4. So sánh

| Tiêu chí | Optimistic | Pessimistic |
|---|---|---|
| Giả định | conflict hiếm | conflict dễ xảy ra |
| Khóa row ngay khi đọc | không | có |
| Khi conflict | phát hiện lúc update | transaction khác phải chờ |
| Xử lý | retry / báo conflict | chờ lock |
| Phù hợp | CRUD thông thường | tồn kho, booking, tài chính |

## 5. Lưu ý

Pessimistic Locking không nên giữ transaction quá lâu vì:

- transaction khác phải chờ;
- giảm throughput;
- có thể dẫn tới deadlock nếu khóa nhiều tài nguyên không đúng thứ tự.
