# Pagination

## 1. Tại sao cần phân trang?

Khi bảng có rất nhiều dữ liệu, lấy toàn bộ bản ghi trong một lần gây:

- tốn RAM và CPU;
- tăng lượng dữ liệu truyền qua mạng;
- response chậm;
- giao diện phải xử lý quá nhiều dữ liệu.

Pagination chia tập dữ liệu thành các phần nhỏ.

## 2. Offset-based Pagination

Cú pháp:

```sql
select id, title
from books
order by id
limit 3 offset 3;
```

Trong đó:

- `limit`: số bản ghi cần lấy.
- `offset`: số bản ghi cần bỏ qua.

Công thức:

```text
offset = (page - 1) * page_size
```

Ví dụ `page_size = 3`:

```text
page 1 -> offset 0
page 2 -> offset 3
page 3 -> offset 6
```

### Ưu điểm

- dễ hiểu;
- dễ triển khai;
- có thể nhảy đến một trang cụ thể.

### Nhược điểm

- offset lớn có thể chậm;
- dữ liệu thêm/xóa liên tục có thể làm dữ liệu giữa các trang bị dịch chuyển.

## 3. Cursor-based Pagination

Cursor dùng giá trị cuối của trang trước để lấy dữ liệu tiếp theo.

Ví dụ:

```sql
select id, title
from books
where id > 3
order by id
limit 3;
```

`3` là `last_id` của trang trước.

### Ưu điểm

- hiệu quả hơn khi dữ liệu lớn;
- phù hợp `load more`;
- phù hợp infinite scroll;
- ổn định hơn khi dữ liệu liên tục thay đổi.

### Nhược điểm

- khó nhảy trực tiếp đến trang số N;
- client phải lưu cursor;
- cần một thứ tự ổn định.

## 4. Vai trò của order by

Pagination nên đi cùng `order by`.

Nếu không có thứ tự ổn định, database không đảm bảo các bản ghi giữa những lần query xuất hiện theo cùng thứ tự.
