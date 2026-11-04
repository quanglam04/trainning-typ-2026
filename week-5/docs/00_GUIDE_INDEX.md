# Tuần 5 — Caching và Redis cho EruSentia

Cache-aside cho chi tiết Template public trên NestJS/TypeORM/PostgreSQL hiện có.
App chưa tích hợp API. [Đề gốc](../README.md) giữ nguyên; source ở erusentia-server.

| Bài | Nội dung |
|---|---|
| [01 — Caching](01_caching_basics.md) | Mục đích, application/distributed cache, phạm vi nên cache |
| [02 — Chiến lược](02_cache_strategies.md) | Cache-aside, write-through và consistency |
| [03 — Redis](03_redis_data_types.md) | Docker, 5 data types, GET/SET/DEL/EXPIRE/TTL, Sorted Set |
| [04 — Tích hợp](04_template_cache.md) | Guard, metadata, revision key, private bypass |
| [05 — Các vấn đề](05_ttl_and_failure_modes.md) | TTL, invalidation/race, avalanche/stampede, penetration, outage |
| [06 — Đo hiệu năng](06_performance.md) | Kết quả HTTP thật, median/p95 và điều kiện đo |
| [07 — Thực hành](07_swagger_demo.md) | Swagger MISS/HIT/PATCH/private, 5 ảnh và tái hiện |
| [08 — Ôn tập](08_interview_qa.md) | Câu hỏi và giới hạn thiết kế |

[Báo cáo](../WEEK5_REPORT.md) · [Nguồn đối chiếu](../SOURCE_SNAPSHOT.md) ·
[Tiến độ](../../docs/LEARNING_TRACKER.md).

Nhánh server feat/week-5-redis-cache, TYP week-5. Snapshot ghim commit server đầy đủ
và hash nội dung; tag báo cáo typ-2026-week-5. Commit/tag cần được push để các link
GitHub truy cập được. Các mốc tuần trước giữ nguyên.
Mở bài 07 để chạy thực hành bằng Swagger; không cần Postman hoặc Redis Cloud.
