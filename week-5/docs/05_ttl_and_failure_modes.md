# 05 — TTL, invalidation và các tình huống lỗi

## TTL và invalidation

Base TTL 60 giây + jitter nguyên 0–11 giây làm key không hết hạn hoàn toàn cùng lúc.
Chọn TTL dựa vào freshness, kích thước/tần suất đọc, chi phí DB và memory; không có
một giá trị đúng cho mọi tài nguyên. TTL không thay invalidation khi public/private đổi.

PATCH/DELETE ghi DB rồi DEL key theo stamp cũ. DEL lỗi vẫn trả kết quả business write;
lần GET sau lấy revision mới. Miss recheck revision trước fill; nếu write chen sau
recheck, entry cũng chỉ nằm ở revision cũ. GET không tăng view hoặc sửa timestamp.

## Avalanche và stampede

Avalanche là nhiều key/cache cùng mất/hết hạn dẫn đến tải dồn về nguồn. Jitter giúp
rải expiry, eviction/mất Redis cả node vẫn có thể gây avalanche.

Stampede là nhiều request cùng miss một key nóng rồi load DB trùng. Single-flight
trong bài gộp full query cùng key/revision trong một process; mỗi request vẫn check
quyền. Chưa distributed lock hoặc điều tiết mọi instance/unique-key storm.

## Penetration

Truy vấn ID không tồn tại lặp lại vẫn có thể đánh DB. Negative caching TTL ngắn,
rate limit hoặc membership filter có thể hỗ trợ tùy trường hợp, nhưng phải xét dữ
liệu mới tạo và lỗi phân quyền. Bài không cache 404/permission errors, chỉ validate
UUID trước service; không tuyên bố đã loại bỏ penetration.

## Redis và DB outage

Redis không ready/lỗi command → BYPASS_REDIS, full DB query. Startup chỉ chờ Redis
khoảng 300 ms rồi phục vụ degraded; client nối lại nền. Offline queue tắt, queue
command giới hạn 100, connect timeout 250 ms, command abort 200 ms. Lỗi không log URL.

DB lỗi không phục vụ cache bằng quyền cũ, vì metadata/auth vẫn phải hiện tại. Nếu
Redis down lâu, DB chịu thêm tải; cần capacity/rate limiting/monitoring trước production.
Single Redis dev không có HA, maxmemory policy có thể evict bất kỳ cache key nào.

## Kiểm chứng cụ thể

Tests cố ý khôi phục stale key sau public→private/deleted, tạo direct SQL update,
corrupt JSON, expire key, 8 concurrent misses, disconnect client và startup Redis
port không truy cập được. Gate/delay kiểm thử race chỉ dùng trong test, không đưa
vào benchmark latency. Cache/private/auth không bị đánh đồng.

Nguồn: [Redis cache-aside](https://redis.io/docs/latest/develop/use-cases/cache-aside/),
[SET](https://redis.io/docs/latest/commands/set/),
[node client](https://redis.io/docs/latest/develop/clients/nodejs/connect/).

[Trước](04_template_cache.md) · [Tiếp: đo hiệu năng](06_performance.md)
