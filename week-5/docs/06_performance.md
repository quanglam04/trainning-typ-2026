# 06 — Đo response time có và không cache

**Ngày đo cuối:** 04/11/2026, Asia/Bangkok. Node 24.21.0, pnpm 12.11.2, NestJS 12.3.2,
TypeORM 0.3.31, PostgreSQL Docker 16.15, Redis Docker 8.10.3, node-redis 5.12.1.
Đo localhost Ubuntu, HTTP concurrency=1, không phải load test.

## Phương pháp

Script benchmark-cache.ts tạo user/Template public riêng trong DB week5_test_* đã
nạp schema/index/seed/auth/cache. HTML tự sinh, không lấy template riêng của App.
Hai API cùng DB và JWT settings, một cache bật và một tắt. Response **580.786 byte**.

5 warmup cho mỗi nhánh, 40 mẫu disabled/hit xen kẽ thứ tự để giảm lợi thế trình tự;
20 cold misses bằng DEL đúng key. performance.now tính tới lúc đọc hết response,
gồm guard/user/session, metadata, Redis/DB, serialization và HTTP transfer. Không
thêm delay giả vào DB. JWT/password không in ra output.

Median số mẫu chẵn là trung bình hai giá trị giữa; p95 dùng nearest rank. Script
xuất thống kê, không lưu raw execution dump hoặc credential vào TYP.

## Kết quả lượt cuối

| Mode | Số mẫu | Mean ms | Median ms | p95 ms |
|---|---:|---:|---:|---:|
| Cache tắt | 40 | 11,629 | 10,801 | 18,433 |
| Cache HIT | 40 | 8,243 | 7,715 | 10,606 |
| Cache MISS | 20 | 15,963 | 15,000 | 24,088 |

Median hit thấp hơn khoảng **28,6%**, tỷ số disabled/hit khoảng **1,40 lần**. Miss
chậm hơn vì thêm metadata/Redis lookup, recheck và fill. Bảng ghi một lượt đo với
phương pháp và công thức thống kê được mô tả trong script.

## Diễn giải đúng giới hạn

HIT bỏ full catalog query, không bỏ auth/metadata DB lookup. Payload tương đối lớn
và hit rate cao là điều kiện giúp demo có lợi. Không suy ra template nhỏ, tải đồng
thời cao, DB/Redis ở mạng khác hoặc cache ít được dùng lại có cùng mức cải thiện.

p95 của 20/40 mẫu local dễ dao động; đây không phải production SLO hoặc throughput
benchmark. Không có số liệu CPU/memory/độ trễ mạng để kết luận tối ưu toàn hệ thống.
Không chọn lại các mẫu riêng lẻ để ép cache luôn nhanh hơn.

## Tái hiện

Theo [hướng dẫn Swagger](07_swagger_demo.md) chuẩn bị DB test đã nạp SQL rồi chạy
pnpm cache:benchmark với DB_DATABASE=week5_test_local và DB_* phù hợp. Script chỉ
chấp nhận prefix week5_test_ vì có tạo dữ liệu. Nó dọn keys/đóng API; drop DB test
riêng khi kết thúc. Lượt mới cần ghi điều kiện/kết quả mới, không thay số đo lịch sử.

[Script và hash](../SOURCE_SNAPSHOT.md) · [Báo cáo](../WEEK5_REPORT.md).

[Trước](05_ttl_and_failure_modes.md) · [Tiếp: Swagger](07_swagger_demo.md)
