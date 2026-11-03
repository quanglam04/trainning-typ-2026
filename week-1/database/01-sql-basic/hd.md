# Hướng dẫn chạy - SQL cơ bản

## 1. Thứ tự chạy

Chạy lần lượt:

```text
1. ddl.sql
2. seed.sql
3. dml-query.sql
4. aggregate.sql
```

`ddl.sql` phải chạy trước vì các file sau phụ thuộc vào cấu trúc bảng.

`seed.sql` tạo dữ liệu mẫu để các query phía sau có dữ liệu kiểm thử.

## 2. ddl.sql

Sau khi chạy thành công, database phải có các bảng:

```text
authors
books
members
loans
loan_items
```

Trong file cũng có ví dụ `alter table` và `drop table`.

Bảng `ddl_drop_demo` chỉ được tạo để minh họa `drop table`, sau đó bị xóa ngay.

## 3. seed.sql

Sau khi chạy, kiểm tra:

```sql
select *
from books
order by id;
```

Kết quả mong đợi có 8 cuốn sách mẫu.

Kiểm tra bạn đọc:

```sql
select *
from members
order by id;
```

Kết quả mong đợi có 4 bạn đọc mẫu.

## 4. dml-query.sql

File thực hiện:

```text
insert
update
delete
where
inner join
left join
right join
order by
```

### DML

Một thành viên demo được thêm bằng `insert`.

Sau đó tên được thay đổi bằng `update`.

Cuối cùng bản ghi demo bị xóa bằng `delete`.

Nếu chạy đến cuối file, email:

```text
demo@example.com
```

không còn tồn tại.

### inner join

Kết quả chỉ chứa các sách đã có tác giả.

Các sách có `author_id = null` không xuất hiện.

### left join

Kết quả chứa toàn bộ sách.

Các sách chưa có tác giả vẫn xuất hiện nhưng `author_name` là `null`.

### right join

Kết quả giữ toàn bộ tác giả.

Tác giả `Tác giả chưa có sách` vẫn xuất hiện và `title` là `null`.

## 5. aggregate.sql

Query đầu gom sách theo `category`.

Điều kiện:

```sql
having count(*) >= 2
```

chỉ giữ các category có ít nhất 2 cuốn sách.

Query tổng hợp trả về:

- tổng số sách;
- tổng số bản sách đang có;
- số bản trung bình;
- năm xuất bản nhỏ nhất;
- năm xuất bản lớn nhất.

## 6. Output

Sau khi hoàn thành module phải chứng minh được:

```text
- database có dữ liệu mẫu
- create table / alter table / drop table
- select / insert / update / delete
- where
- inner join / left join / right join
- group by / having / order by
- count / sum / avg / min / max
```
