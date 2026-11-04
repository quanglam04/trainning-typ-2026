# 08 — Câu hỏi ôn tập và đối chiếu thực hành

## 1. DTO là gì? Vì sao không trả nguyên entity?

DTO định nghĩa hợp đồng qua ranh giới; entity map persistence. Client không được sửa
ID/author/metrics chỉ vì entity có các property đó. DTO class + validation runtime
kiểm tra input; output cần projection chủ động, không tự được bảo vệ bởi request DTO.
Ví dụ API trả author chỉ id, không query user password/email. Whitelist giảm field
ngoài hợp đồng nhưng không thay authorization, kiểm tra reference hoặc DB constraint.

Tự kiểm tra: gửi PATCH authorId hoặc viewsCount phải nhận 400. Gửi
allowHeightResize=false là boolean hợp lệ, chuỗi "false" phải bị từ chối.

## 2. N+1 là gì? Cách phát hiện và xử lý?

Một query lấy N row rồi N lần query relation tạo 1+N. Join hoặc batch theo ID có thể
tránh query tăng theo từng row. Trong adapter bài này category được JOIN; author chỉ
trả ID nên không cần lookup user cho mỗi template.

Bật query logging có kiểm soát để đếm query trong một request, tách query bootstrap;
không suy từ `.leftJoinAndSelect` rằng tổng request chỉ có một SQL, vì pagination và
COUNT có thể cần thêm. Đánh giá mức cải thiện latency cần benchmark cùng điều kiện.
21 query tuần tự có thể tái dùng connection, không đồng nghĩa 21 connection đồng thời.

## 3. 401 khác 403 thế nào?

401 liên quan yêu cầu authentication hợp lệ, thường kèm challenge theo HTTP; 403 là
server từ chối xử lý dù hiểu request. Có thể cần tránh tiết lộ sự tồn tại resource
trong thiết kế quyền, nhưng đó là quyết định API phải nhất quán.
Tuần 2 chưa có hai luồng bảo vệ này. authorId do client gửi không chứng minh client
là tác giả. isPublic=false cũng không tự khóa truy cập nếu controller không áp quyền.

## 4. Khi nào dùng path và query?

Path `/templates/:id` xác định một Template. Query dùng filter/sort/pagination như
`?page=2&size=10&isPublic=false`. Body chứa dữ liệu POST/PATCH. Không gửi password
trong query; không dùng GET để cập nhật nội dung. Path ID sai syntax 400 khác với
ID hợp lệ nhưng thiếu row 404.

## 5. Vì sao DELETE dùng 204? DELETE có idempotent không?

204 diễn đạt thành công không có response content; không bọc JSON vào response đó.
DELETE cũng có thể dùng 200 khi trả representation hoặc 202 khi xử lý bất đồng bộ,
nên 204 là lựa chọn hợp đồng bài này, không bắt buộc cho mọi API.
Lần đầu 204, lần sau 404 vẫn giữ tác động resource đã bị loại khỏi tập đang đọc.
Soft-delete giữ physical row trong DB không làm mất ý nghĩa xóa resource HTTP.

## Câu hỏi bổ sung

**PUT và PATCH:** PUT là tạo/thay representation tại URI, PATCH áp dụng thay đổi;
PATCH không được đảm bảo idempotent nhưng có thể được thiết kế idempotent. API này
chỉ có PATCH và contentJson được thay cả document, không merge metadata sâu.

**Memory và ORM có cùng contract không?** Cùng routes/DTO/envelope và các test cases;
memory mất dữ liệu khi restart, postgres giữ dữ liệu. Collation title có thể khác,
không suy ra performance/transaction guarantees của DB từ Map.

**JSONB có tự validate nội dung App không?** Không. DTO validate envelope HTML/metadata;
DB đảm bảo JSONB và constraint. GET row seed legacy vẫn có thể trả format khác.
Kiểm thử API hiện xác nhận lưu/đọc chuỗi HTML, chưa bao gồm render trong App.

**Tại sao không bật synchronize?** Mapping một phần không phải full schema. Dùng SQL
bootstrap hiện có; schema change cần migration được review. Lần này không đổi schema.

**Offset khác cursor?** Offset dễ nhảy trang/COUNT, tốn chi phí khi sâu và có page drift.
Cursor seek theo cặp khóa duy nhất, không tự tạo snapshot hoặc đảm bảo mọi trang có
latency giống nhau. Chỉ offset đã triển khai trong bài này.

**Đã đáp ứng production chưa?** Chưa: auth/ownership, public feed, HTML preview qua App,
optimistic concurrency, load tests và migration rollout còn ngoài phạm vi tuần 2.
Swagger/OpenAPI đã có ở mức cơ bản để tra cứu hợp đồng và gửi request khi phát triển.

[Mốc nguồn và tests](../SOURCE_SNAPSHOT.md) · [Báo cáo](../WEEK2_REPORT.md).
Nguồn: [RFC 9110](https://www.rfc-editor.org/rfc/rfc9110.html),
[HTTP PATCH](https://www.rfc-editor.org/rfc/rfc5789.html).

[Trước](07_error_handling.md) · [Tiếp: Postman](09_postman.md)
