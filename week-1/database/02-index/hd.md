# Hướng dẫn chạy - Index

## 1. Mục tiêu

So sánh cùng một query:

```text
trước khi tạo Index
sau khi tạo Index
```

## 2. Thứ tự chạy

Mở `index-demo.sql` và chạy từ trên xuống.

File thực hiện:

```text
1. xóa bảng demo cũ nếu có
2. tạo bảng index_demo
3. sinh 200000 bản ghi
4. analyze bảng
5. explain trước Index
6. tạo Index
7. analyze lại
8. explain sau Index
```

## 3. Kết quả trước Index

Query:

```sql
select id, search_code, payload
from index_demo
where search_code = 'BOOK-199999';
```

được kiểm tra bằng:

```sql
explain (analyze, buffers)
```

Trước khi có Index, execution plan thường xuất hiện:

```text
Seq Scan
```

Điều đó nghĩa là PostgreSQL phải kiểm tra nhiều bản ghi để tìm giá trị cần thiết.

## 4. Kết quả sau Index

Sau khi chạy:

```sql
create index idx_index_demo_search_code
on index_demo(search_code);
```

chạy lại đúng query cũ.

Execution plan thường chuyển sang:

```text
Index Scan
```

hoặc:

```text
Bitmap Index Scan
```

## 5. Phân tích

So sánh:

```text
Execution Time
Buffers
loại Scan
```

Thời gian cụ thể không cố định vì phụ thuộc máy, cache và trạng thái PostgreSQL.

Điểm quan trọng là execution plan cho thấy cách database truy xuất dữ liệu đã thay đổi sau khi có Index.

## 6. Output

Module hoàn thành khi chứng minh được:

```text
- cùng một query trước và sau Index
- tạo Index thành công
- execution plan thay đổi
- giải thích được lợi ích và chi phí của Index
```
