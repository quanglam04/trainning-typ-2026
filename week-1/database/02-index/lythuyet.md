# Index

## 1. Index là gì?

Index là cấu trúc dữ liệu giúp database tìm kiếm bản ghi nhanh hơn.

Có thể hình dung Index giống **mục lục của một cuốn sách**: thay vì đọc từ đầu đến cuối, ta tra mục lục rồi đi thẳng đến vị trí cần tìm.

## 2. Khi không có Index

Khi query lọc theo một cột chưa có Index, PostgreSQL có thể phải quét toàn bộ bảng.

Execution plan thường có:

```text
Seq Scan
```

Với bảng lớn, việc quét tuần tự toàn bộ dữ liệu có thể tốn nhiều thời gian.

## 3. Khi có Index

Khi cột được đánh Index và query phù hợp, PostgreSQL có thể sử dụng:

```text
Index Scan
```

hoặc:

```text
Bitmap Index Scan
```

Database tìm vị trí thông qua cấu trúc Index thay vì đọc tuần tự toàn bộ bảng.

## 4. Khi nào nên đánh Index?

Nên cân nhắc Index với các cột:

- thường xuyên xuất hiện trong `where`;
- thường xuyên dùng trong `join ... on`;
- có độ chọn lọc tốt;
- thường xuyên phục vụ tìm kiếm hoặc sắp xếp.

`primary key` và `unique` đã có Index phục vụ ràng buộc.

## 5. Khi nào không nên đánh Index?

Không nên tạo Index cho mọi cột vì:

- tốn thêm dung lượng;
- `insert`, `update`, `delete` phải cập nhật Index;
- quá nhiều Index làm thao tác ghi chậm hơn;
- bảng nhỏ có thể không cần Index bổ sung;
- cột có ít giá trị khác nhau có thể không mang lại lợi ích đáng kể.

## 6. Đánh đổi

Index thường giúp:

```text
select nhanh hơn
```

nhưng làm:

```text
insert / update / delete tốn thêm chi phí
```

Vì vậy cần cân bằng giữa tốc độ đọc và tốc độ ghi.
