# 04 — Tích hợp cache vào GET Template

## Cấu hình và phạm vi

CACHE_ENABLED=false mặc định; bật yêu cầu PostgreSQL và migration 07. Redis URL,
namespace và TTL được cấu hình qua environment; không commit .env hoặc key thật.
Swagger vẫn 11 operation, không thêm API chỉ để xóa cache hoặc xem internal keys.

Global JWT guard giữ nguyên. GET chi tiết gọi TemplatesService.read → TemplateCache;
PATCH/DELETE vẫn dùng DB để kiểm tra resource/ownership. Cache không mang role hoặc
trạng thái phiên để dùng thay quyết định quyền.

## Revision và schema

Migration sql/07_template_cache_revision.sql thêm cache_revision UUID vào templates
và BEFORE UPDATE trigger đổi UUID mỗi lần update, gồm direct SQL và soft-delete.
Không dùng updatedAt đã chuyển milliseconds làm revision vì có thể mất độ phân giải.
Cột không map/serialize vào HTTP DTO; không thêm bảng.

Key có dạng namespace:template:v1:id:revision. Key cũ và mới khác nhau sau mutation,
không phụ thuộc Redis còn hoạt động lúc DB commit. Migration chạy lại được; không
auto migrate khi bootstrap và không thay schema/index/seed tuần 1.

## Flow đọc

Metadata query theo ID chỉ lấy authorId/isPublic/revision từ row chưa deleted. Nếu
không đọc được thì 404 trước cache; private của owner/admin đọc DB và bypass. Public
lookup Redis; miss full DB query, kiểm tra lại revision và SET EX khi còn phù hợp.
Corrupt JSON/shape bị loại và rebuild; projection không có credential hoặc trường thừa.

HIT phục vụ HTML/JSON từ Redis nhưng vẫn có user/session queries và metadata DB query.
Với category name/slug đổi trực tiếp ngoài API, projection cũ có thể còn tới TTL nếu
không bump template revision. Bài chưa có API cập nhật category.

## Header quan sát

| X-Cache | Ý nghĩa |
|---|---|
| HIT | Đọc public projection từ Redis |
| MISS | Không có entry hợp lệ, full DB query; có thể fill |
| COALESCED | Đợi full load cùng revision đang chạy trong process |
| BYPASS_DISABLED | Cache tắt |
| BYPASS_PRIVATE | Private được phép, không cache |
| BYPASS_REDIS | Redis không ready/lỗi, fallback DB |

Cache-Control:no-store là HTTP cache policy, không cấm Redis cache nội bộ server.
Error/404 không được lưu; không có header HIT trên lỗi để lộ private key.

## Quyền và ranh giới

Public → private/deleted được kiểm tra qua metadata hiện tại, kể cả stale key được
khôi phục sau invalidation. GET đã qua check trước mutation có thể hoàn tất snapshot
trước đó; chưa hủy in-flight request hoặc cung cấp linearizable HTTP consistency.

[Snapshot nguồn](../SOURCE_SNAPSHOT.md) dẫn service/cache/controller/SQL/tests.
Nguồn: [Redis cache-aside](https://redis.io/docs/latest/develop/use-cases/cache-aside/).

[Trước](03_redis_data_types.md) · [Tiếp: failure modes](05_ttl_and_failure_modes.md)
