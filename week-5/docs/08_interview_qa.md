# 08 — Câu hỏi ôn tập Caching và Redis

## Cache HIT có nghĩa không query DB không?

Không trong thiết kế này. JWT guard vẫn lookup user/session; metadata quyền/revision
vẫn lấy từ DB. Hit bỏ full Template HTML/JSON projection query. Chấp nhận chi phí này
để public cache không vượt quyền khi tài nguyên chuyển private hoặc bị xóa.

## Cache-aside khác write-through ở đâu?

Cache-aside fill khi read miss, app tự quản invalidation. Write-through có luồng ghi
qua lớp quản lý cả nguồn/cache theo hợp đồng. Hai lệnh DB/Redis độc lập không tự tạo
atomic transaction. Bài triển khai cache-aside, không write-through/write-behind.

## Vì sao TTL chưa đủ cho private data?

Nếu chỉ chờ TTL, public cũ có thể bị đọc tới khi key hết hạn sau khi chuyển private.
Bài check metadata DB trước Redis; private owner/admin bypass, người khác nhận 404.
Không cache authorization để đổi lấy hit rate.

## DEL sau UPDATE có race không?

Có thể có reader fill bản cũ sau DEL. Revision UUID do DB trigger đổi trên update
làm cache bản cũ không được chọn ở lần đọc revision mới. Recheck trước fill giảm
stale fill; request đã bắt đầu trước mutation vẫn có thể hoàn tất snapshot cũ.

## Vì sao không dùng updatedAt milliseconds hoặc Redis INCR làm version?

Timestamp chuyển qua JavaScript có thể mất độ phân giải. Redis INCR có thể không
thực hiện được khi Redis down đúng lúc DB write. Revision sinh trong DB tránh phụ
thuộc đó, nhưng thêm migration/metadata query và không giải mọi consistency issue.

## Avalanche, stampede và penetration khác nhau thế nào?

Avalanche: nhiều key/cache mất gây tải dồn. Stampede: nhiều request miss cùng key
nóng. Penetration: truy vấn không tồn tại liên tục qua cache xuống nguồn. Jitter và
single-flight process chỉ xử lý một phần; bài chưa negative cache hoặc rate limit.

## Redis có persistence không? Cache mất thì sao?

Redis có tùy chọn persistence; deployment cache local chủ động tắt vì dữ liệu có thể
xây lại. Redis lỗi thì fallback DB, nhưng DB vẫn phải chịu tải và hoạt động để check
quyền. Không dùng cached permission để tiếp tục khi PostgreSQL hỏng.

## Đã có leaderboard của App chưa?

Chưa. CLI cache:types dùng dữ liệu public views_count thật để demo Sorted Set ranking.
Chưa có ranking API hay luồng đồng bộ lượt xem; GET Template không tăng view ngầm.
App chưa tích hợp API. Không đổi List Redis thành RabbitMQ queue để bỏ bài tuần 4.

## Cache có luôn nhanh hơn không?

Không. Lượt cuối payload 580.786 byte cho hit median 7,715 ms so với disabled 10,801 ms;
miss 15,000 ms. Đây là đo HTTP local, concurrency=1, không phải kết quả mọi workload.
Có network/parse/fill/metadata overhead và chi phí vận hành mới.

## Đã đủ production chưa?

Chưa ACL/TLS/HA Redis, distributed stampede control, retention/metrics/alerting,
load test hoặc sizing. Cache không sửa các giới hạn auth/MQ đã ghi ở tuần trước.

[Mục lục](00_GUIDE_INDEX.md) · [Báo cáo](../WEEK5_REPORT.md) · [Nguồn](../SOURCE_SNAPSHOT.md).
