# 01 — Caching là gì và khi nào dùng?

Cache giữ bản sao dữ liệu có thể tính/đọc lại để giảm công việc lặp. Hit lấy được dữ
liệu từ cache; miss phải về nguồn và có thể fill lại. Cache không mặc nhiên là nguồn
sự thật hoặc giải pháp làm mọi request nhanh hơn.

## Cache ở đâu?

| Loại | Đặc điểm |
|---|---|
| Trong process | Đọc nhanh, mất khi restart, mỗi instance có bản riêng |
| Distributed cache | Nhiều instance chia sẻ, có network hop và dependency mới |
| HTTP/browser/CDN | Cache response theo HTTP semantics; khác cache nội bộ server |

Redis trong bài là distributed cache, dù demo chỉ chạy một node Docker local.
Cache-Control:no-store trên response vẫn hợp lý: client không cache response có
xác thực, trong khi server chỉ cache projection public và kiểm tra quyền trước đó.
Không dùng Redis client-side caching; chỉ dùng lệnh Redis server thông thường.

## Áp dụng EruSentia

Template có HTML/JSON có thể lớn và được đọc nhiều. Cache public projection giúp
bỏ full query/transfer từ PostgreSQL trên hit. Guard vẫn tra user/session; service
đọc metadata quyền/revision trước Redis. Do đó hit không đồng nghĩa không query DB.

Không cache password/hash/JWT/refresh token hoặc private template. List có filter,
pagination/visibility riêng và chưa cache. App chưa gọi API; không thay frontend.
Cache mặc định tắt để giữ cách chạy các tuần trước.

## Khi có lợi và khi chưa phù hợp

Read-heavy, payload đắt và chấp nhận bản sao có hạn là ứng viên tốt. Dữ liệu nhỏ,
ít đọc lại, write-heavy, quyết định quyền hoặc nghiệp vụ cần trạng thái hiện tại có
thể không phù hợp. Redis GET/SET/parse, metadata query và miss fill đều có chi phí.

Bài đo với response khoảng 581 KB trên localhost, không suy ra mọi template nhỏ đều
nhanh hơn. Redis outage có thể dồn tải về DB; không dùng cache để che DB lỗi khi cần
quyền hiện tại. Chưa có load test hoặc production sizing.

Nguồn: [Redis cache-aside](https://redis.io/docs/latest/develop/use-cases/cache-aside/),
[Redis node client](https://redis.io/docs/latest/develop/clients/nodejs/connect/).

[Tiếp: chiến lược](02_cache_strategies.md)
