# 06 — Pagination trong API và tính ổn định

## Offset đang triển khai

Client gửi page bắt đầu từ 1 và size. `offset=(page-1)*size`; SQL tương ứng dùng
LIMIT/OFFSET, TypeORM dùng skip/take. Chỉ gọi getManyAndCount sau khi áp dụng filter
để total phản ánh cùng điều kiện lọc. Không lấy tất cả dữ liệu về Node rồi slice
trong adapter PostgreSQL.

```json
{
  "data": [],
  "page": 1,
  "size": 10,
  "totalPages": 0,
  "totalElements": 0
}
```

Đây là ví dụ page rỗng. totalPages=ceil(totalElements/size); không ép tối thiểu một
trang khi không có row. Trang vượt cuối trả data=[] nhưng giữ total. Memory và
PostgreSQL sử dụng cùng cấu trúc response này.

## Input và sắp xếp

page 1..10000, size 1..100; mặc định 1 và 10. `page=1.5`, query lặp thành array,
field lạ hoặc size=101 trả 400. Không dùng parseInt để chấp nhận `2abc` thành 2.
Sort chỉ createdAt/title; order asc/desc. Thêm id cùng chiều làm tiebreaker.

Ví dụ `ORDER BY created_at DESC, id DESC`: nếu hai row có cùng created_at vẫn có
thứ tự xác định. Memory và PostgreSQL có thể khác thứ tự Unicode khi sort title do
collation; contract không hứa cùng thứ tự title cho mọi locale.

## Hạn chế offset

OFFSET lớn vẫn phải tính/bỏ các row trước đó; index không làm biến mất chi phí này.
Nếu thêm/xóa row trước trang đang xem, các request liên tiếp có thể lặp hoặc bỏ sót
row (page drift). ORDER BY duy nhất giải quyết tie order, không tạo snapshot xuyên
request. Data và count còn có thể thấy dữ liệu khác nhau khi ghi đồng thời, vì
getManyAndCount không có transaction snapshot riêng ở adapter này.

Giới hạn page/size hạn chế request vô lý, không phải bằng chứng API chịu được tải
production. Chưa đo load hoặc latency, không đưa số ms cố định vào báo cáo.

## Cursor/keyset: kiến thức mở rộng, chưa có route chạy

Dùng khóa cuối trang trước thay OFFSET, với cặp `(created_at,id)`:

```sql
SELECT id, title, created_at
FROM templates
WHERE deleted_at IS NULL
  AND (created_at, id) < ($1::timestamptz, $2::uuid)
ORDER BY created_at DESC, id DESC
LIMIT $3;
```

Trang đầu bỏ điều kiện cursor; lấy size+1 để xác định hasNext, trả size row và encode
cặp khóa của row cuối. Nếu chỉ dùng created_at < timestamp cuối, các row có cùng
timestamp ở mép trang có thể bị bỏ qua. Cursor phải chứa đủ hai giá trị, được kiểm
tra format và gắn với cùng filters/sort; base64 chỉ là encoding, không mã hóa bảo mật.
Index phù hợp có thể giúp seek; tốc độ còn phụ thuộc query plan, filter và dữ liệu,
không cam kết trang nào cũng vài ms hoặc cursor luôn tạo snapshot ổn định.

Cursor không thuận tiện nhảy thẳng trang 100 và thường không biết totalPages nếu
không COUNT thêm. Bài thực hành triển khai offset; đoạn SQL trên minh họa keyset.

## Minh chứng cần đọc

HTTP contract tạo dữ liệu có tag riêng, kiểm tra total, hai trang không trùng ID,
trang rỗng/vượt cuối, boolean filter, author/category và search `%` literal. Postman
list kiểm tra page envelope và chứa đúng ID của lần chạy. Các kiểm tra này không
thay kiểm thử ghi đồng thời/phân trang dưới tải production.

[Mã nguồn/tests](../SOURCE_SNAPSHOT.md) · [Postman](09_postman.md).
Nguồn: [PostgreSQL 16 LIMIT/OFFSET](https://www.postgresql.org/docs/16/queries-limit.html),
[row comparison](https://www.postgresql.org/docs/16/functions-comparisons.html).

[Trước](05_repository_pattern.md) · [Tiếp: Error handling](07_error_handling.md)
