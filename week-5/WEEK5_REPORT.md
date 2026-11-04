# Báo cáo tuần 5 — Caching và Redis

**Học viên:** Nguyễn Tiến Tuấn. **Ngày thực hành:** 04/11/2026, Asia/Bangkok.
**Dự án:** EruSentia — Sentia Hub Backend.
**Trạng thái:** đã chốt nguồn tuần 5. [Snapshot](SOURCE_SNAPSHOT.md) ghi commit
backend đầy đủ, permalink và SHA-256 nguồn đã đối chiếu.
Tag lưu trữ báo cáo: `typ-2026-week-5`. [Đề gốc](README.md) giữ nguyên.

## 1. Mục tiêu và lựa chọn

Phát triển server hiện có bằng cache-aside cho chi tiết Template public, nơi HTML/
JSON có thể lớn và được đọc lại. PostgreSQL là nguồn sự thật. Redis không thay DB
cho authentication/authorization; App chưa tích hợp API. Source/SQL/tests ở server,
TYP giữ lý thuyết, hướng dẫn, báo cáo và minh chứng.

| Yêu cầu | Thực hiện |
|---|---|
| Caching, application/distributed | Bài 01, phân biệt HTTP cache với Redis nội bộ |
| Cache-aside/write-through | Bài 02, triển khai cache-aside; không gọi DB/Redis writes là atomic |
| Redis/Docker/client | Redis 8.10.3, node-redis 5.12.1, Compose local riêng |
| Data types và lệnh | String/Hash/List/Set/Sorted Set; GET/SET/DEL/EXPIRE/TTL |
| Tích hợp API | GET /templates/:id MISS → full query/fill → HIT |
| TTL/invalidation | EX + jitter, DB revision trigger, DEL sau PATCH/DELETE |
| Avalanche/penetration | Giải thích và nêu giới hạn; chưa negative cache/rate limiting |
| Demo và latency | 5 ảnh Swagger, benchmark HTTP thật với cache tắt/hit/miss |
| Sorted Set ranking | CLI dùng public views_count thật, chưa API leaderboard/App |

## 2. Hành vi và bảo vệ quyền

CACHE_ENABLED=false mặc định giữ flow tuần trước. Bật yêu cầu PostgreSQL và migration
07; memory không hỗ trợ cache. Guard đọc user/session hiện tại, service đọc metadata
visibility/revision trước Redis. Private được phép đọc thì bypass; người khác nhận
404. Cache chỉ giữ public projection, không auth/error/list/private hoặc credential.

Body/11 operation giữ hợp đồng cũ, GET chi tiết thêm X-Cache. HIT bỏ full query lấy
HTML/JSON/category projection, không bỏ auth hoặc metadata DB lookup. HTTP vẫn đặt
Cache-Control:no-store, không đồng nhất với cache nội bộ server.

Migration 07 thêm cache_revision UUID và trigger đổi revision mỗi UPDATE, gồm direct
SQL/soft-delete. Key theo namespace/template/id/revision. PATCH/DELETE ghi DB rồi DEL
key cũ; Redis lỗi lúc invalidation không làm lần đọc sau chọn revision cũ. Miss recheck
revision trước fill. GET đã bắt đầu trước mutation có thể hoàn tất snapshot trước đó.

Schema/index/seed và migrations tuần trước giữ nguyên; không auto migrate/synchronize.
07 không thêm bảng: đủ 05/06/07 vẫn 12 bảng; cache tests không cần 06 khi queue tắt.
Category cập nhật qua SQL ngoài API có thể còn projection cũ tới TTL nếu không bump
Template revision; bài chưa có category update API.

## 3. TTL, concurrency và lỗi

Base TTL 60 giây + jitter 0–11, SET EX atomic. Single-flight gộp full load cùng key
trong một process, chưa distributed lock hoặc điều tiết unique-key storm. Không
cache 404: penetration vẫn có thể gây DB lookups, cần biện pháp bổ sung khi vận hành.

Redis outage/timeout fallback full DB query; startup degraded nếu Redis chưa ready.
Client tắt offline queue, giới hạn command queue 100; connect 250 ms, command abort
200 ms, reconnect tới 1 giây. DB hỏng không phục vụ bằng cached permissions. Corrupt
entry được bỏ/rebuild. Redis dev 128 MB/allkeys-lru, không persistence, loopback only.

## 4. Kiểm chứng

Node 24.21.0, pnpm 12.11.2, NestJS 12.3.2, TypeORM 0.3.31, PostgreSQL 16.15,
Redis 8.10.3, node-redis 5.12.1. Client được ghim major 5 phù hợp peer TypeORM;
pnpm peers check không báo vấn đề tương thích.

| Kiểm tra | Kết quả |
|---|---|
| Typecheck/build, frozen lockfile, peer dependencies | Đạt |
| pnpm test | 37 pass, 0 fail, 3 integration suites skip mặc định |
| Cache integration PostgreSQL/Redis | 13/13 pass |
| PostgreSQL CRUD/auth hồi quy | 19/19 pass |
| RabbitMQ/outbox/SMTP hồi quy | 13/13 pass |
| Swagger | MISS, HIT, PATCH→MISS, private owner bypass, user khác 404 |
| Data type CLI | 5 types, TTL và Sorted Set với dữ liệu public thật; dọn scratch keys |

Test runner tính cả parent/subtest, không phải số endpoint. Cache suite xác nhận
full store query chỉ xảy ra trên miss; còn quyền vẫn đọc DB. Các ca gồm stale key
khôi phục sau private/delete, direct SQL revision, expiry, corruption, coalescing,
controlled stale-fill race, disconnect và startup Redis không truy cập được. Delay/
gate chỉ dùng trong test race, không chèn vào benchmark để làm DB chậm giả.

## 5. So sánh thời gian

Lượt cuối đo HTTP localhost, concurrency=1, response **580.786 byte** HTML tự sinh;
5 warmup, 40 mẫu disabled/hit xen kẽ, 20 cold misses. Đọc hết body, gồm JWT/user/
session và metadata DB. Median chẵn lấy trung bình hai giá trị giữa, p95 nearest rank.

| Mode | n | Mean ms | Median ms | p95 ms |
|---|---:|---:|---:|---:|
| Cache tắt | 40 | 11,629 | 10,801 | 18,433 |
| HIT | 40 | 8,243 | 7,715 | 10,606 |
| MISS | 20 | 15,963 | 15,000 | 24,088 |

Hit median giảm khoảng 28,6% (tỷ số 1,40 lần); miss đắt hơn do lookup/recheck/fill.
Kết quả chỉ cho lượt/payload/môi trường này, không phải production SLO, load test
hoặc bảo đảm cache luôn nhanh hơn. [Phương pháp và tái hiện](docs/06_performance.md).

## 6. Tài liệu, minh chứng và dọn dẹp

[Hướng dẫn Swagger](docs/07_swagger_demo.md) có 5 ảnh response thật, thấy body và
X-Cache; JSON được mở rộng khi chụp, không sửa dữ liệu hoặc công bố token/cURL.
Các ảnh và benchmark dùng dữ liệu server tự tạo, không lấy source/template riêng App.
[Snapshot](SOURCE_SNAPSHOT.md) dẫn từng file SQL/client/cache/controller/test/script.

DB test: week5_test_20261104_cache_1, cache_2, cache_final và hai DB hồi quy
week3_test_20261104_week5_regression / week4_test_20261104_week5_regression. Đã dọn
DB, namespace Redis/test MQ, email Mailpit hồi quy và script capture tạm; các API/
browser/worker demo đã đóng. DB nghiệp vụ giữ 10 users, 1.012 templates, chưa áp
migration 05/06/07. Container/volume dev được giữ để tái hiện.

## 7. Giới hạn và hướng tiếp theo

Chưa ACL/TLS/HA Redis, distributed stampede control, rate limiting/negative cache,
observability/alerting, load test hoặc sizing. Không che DB outage bằng quyền cũ,
không thay RabbitMQ bằng Redis List, chưa có ranking API hay App integration. Các
giới hạn auth/MQ tuần trước vẫn tồn tại. Đủ yêu cầu thực hành, không kết luận sẵn
sàng production. Nguồn đã ghim permalink commit đầy đủ; commit/tag cần được push
để mở các link GitHub. Giữ nguyên các snapshot/tag tuần 1–4.
