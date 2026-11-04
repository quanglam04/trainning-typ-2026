# 07 — Error handling và ExceptionFilter

## Phân loại lỗi

- Request sai: JSON hỏng, DTO/path/query không hợp lệ → 400.
- Không có Template hoặc đã soft-delete → 404.
- Slug vi phạm UNIQUE → 409.
- JSON body vượt 2 MiB → 413.
- Lỗi không biết/DB không được ánh xạ → 500 với thông báo chung.

401 thường là thiếu/sai authentication, 403 là server hiểu nhưng từ chối quyền;
chưa có auth guard ở tuần 2. Status cần phản ánh đúng loại lỗi để client quyết định
sửa request, xác thực lại hoặc xử lý sự cố server.

## Hợp đồng thực tế

```json
{
  "statusCode": 400,
  "code": "BAD_REQUEST",
  "message": "Request validation failed",
  "details": ["size must not be greater than 100"],
  "path": "/templates",
  "timestamp": "2026-11-03T00:00:00.000Z"
}
```

Đây là ví dụ cấu trúc, timestamp minh họa; không phải log chạy thật. `details` luôn
là array, có thể rỗng nếu lỗi không do validation. `code` là mã ứng dụng, không phải
PostgreSQL SQLSTATE gửi cho client. Cấu trúc này khác Problem Details của RFC 9457.

## Cách filter xử lý

1. HttpException: lấy status và message; validation message array vào details.
2. QueryFailedError: đọc driverError.code, 23505 → 409 CONFLICT; 23503 → 400
   INVALID_REFERENCE. Lỗi DB khác vẫn 500, không đưa raw driver error vào response.
3. Lỗi không biết: log phía server và trả thông báo chung.
4. Gửi status đúng và JSON envelope; path không chứa body/credentials/query params.

Pre-check author/category thiếu trả BAD_REQUEST; lỗi FK ở DB trả INVALID_REFERENCE.
Đây là hai thời điểm phát hiện cùng vấn đề tham chiếu, không cam kết mọi lỗi 400 có
cùng code chi tiết. Request malformed JSON/body quá lớn cũng qua error handling.

## Filter không thay transaction hoặc constraint

Filter chỉ chuyển lỗi thành HTTP response. Nó không rollback nhiều query đã commit,
không chặn race giữa kiểm tra slug và insert, không xác thực ownership. UNIQUE/FK
của PostgreSQL vẫn là lớp đảm bảo cuối. PATCH/DELETE kiểm tra affected để trả 404
nếu không cập nhật được row, thay vì báo thành công khi không có tài nguyên.

## Thành công và exception không phải cùng một luồng

Không có global success wrapper interceptor trong bản này. List trả page envelope;
POST/PATCH/detail trả Template; DELETE 204 body rỗng. Một interceptor có thể biến
đổi response thành công. ExceptionFilter xử lý exception chưa được bắt, không chạy
như một bước bắt buộc sau mỗi response thành công.

## Những gì bộ kiểm thử xác nhận

HTTP contract chạy thật qua listener: malformed JSON, path/query sai, null, nested
metadata sai, unknown fields, body quá lớn; slug conflict kể cả concurrent POST;
404 sau soft-delete. Error có statusCode/code/message/details/timestamp, không lộ
password_hash. Chưa tiêm mọi kiểu lỗi DB hoặc kiểm thử hệ thống quan sát/log production.

[Mốc nguồn](../SOURCE_SNAPSHOT.md) · [Hướng dẫn Postman](09_postman.md).
Nguồn: [Nest exception filters](https://docs.nestjs.com/exception-filters),
[PostgreSQL error codes](https://www.postgresql.org/docs/16/errcodes-appendix.html).

[Trước](06_pagination_api.md) · [Tiếp: Câu hỏi ôn tập](08_interview_qa.md)
