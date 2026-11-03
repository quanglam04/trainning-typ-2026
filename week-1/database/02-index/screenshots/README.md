# Screenshot - Index

## 01-before-index.png

Chạy `explain (analyze, buffers)` trước khi tạo index.

Cần nhìn thấy:
- `Seq Scan` hoặc dạng quét tuần tự;
- `Execution Time`.

## 02-after-index.png

Tạo index rồi chạy lại **đúng cùng query**.

Cần nhìn thấy:
- `Index Scan` hoặc `Bitmap Index Scan`;
- `Execution Time`.

Hai ảnh dùng cùng query để so sánh hợp lý.
