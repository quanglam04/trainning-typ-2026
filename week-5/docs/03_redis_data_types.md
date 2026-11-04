# 03 — Redis, data types và các lệnh cơ bản

Redis là data structure server trong bộ nhớ, có các tùy chọn persistence/replication;
không đồng nhất mọi deployment với “không có durability”. Bài dùng Redis 8.10.3 trong
Docker, cache có thể dựng lại nên chủ động tắt RDB/AOF và giới hạn 128 MB/allkeys-lru.
Chỉ bind localhost:6379, chưa ACL/TLS/HA. node-redis 5.12.1 phù hợp peer TypeORM hiện tại.

## Kiểu dữ liệu

| Type | Lệnh ví dụ | Áp dụng trong demo |
|---|---|---|
| String | SET, GET | Title hoặc serialized public Template |
| Hash | HSET, HGETALL | Metadata id/title |
| List | RPUSH, LRANGE | Danh sách IDs có thứ tự, có thể trùng |
| Set | SADD, SMEMBERS | Category IDs duy nhất, không bảo đảm thứ tự |
| Sorted Set | ZADD, ZRANGE REV WITHSCORES | Template ID theo score views_count |

Members của Sorted Set là duy nhất; ZADD cùng member cập nhật score. Score bằng
nhau có tie-break theo member, không phải ngẫu nhiên hoặc chắc theo thời điểm insert.
List ở đây là demo data type, không thay RabbitMQ của tuần 4.

## Lệnh nền tảng

```redis
SET lesson:template:title "Weekly planner" EX 60
GET lesson:template:title
TTL lesson:template:title
EXPIRE lesson:template:title 30
DEL lesson:template:title
```

TTL trả số giây còn lại, -1 nếu key tồn tại không expiry, -2 nếu không tồn tại.
SET EX đặt value/expiry trong một lệnh; SET rồi EXPIRE tách riêng có failure window.
Eviction có thể xóa key trước TTL nếu bộ nhớ cần giải phóng; expiry và eviction khác nhau.

## Demo trên dữ liệu thật

pnpm cache:types đọc top 5 public templates trong DB đã chọn, tạo scratch namespace
UUID, MULTI/EXEC các cấu trúc + TTL rồi đọc lại và DEL. Không FLUSHDB. Sorted Set top
ba ở lượt kiểm chứng có score 3420, 2980, 2590 từ seed views_count hiện tại.

Đây là demo ranking bằng dữ liệu EruSentia thật, chưa có API leaderboard hoặc flow
đồng bộ lượt xem. GET Template không tự tăng views_count và App chưa tích hợp API.
Không coi dữ liệu demo Redis là bảng xếp hạng production đã hoàn thiện.

Nguồn: [Data types](https://redis.io/docs/latest/develop/data-types/),
[Sorted sets](https://redis.io/docs/latest/develop/data-types/sorted-sets/),
[TTL](https://redis.io/docs/latest/commands/ttl/), [SET](https://redis.io/docs/latest/commands/set/).

[Trước](02_cache_strategies.md) · [Tiếp: tích hợp](04_template_cache.md)
