# Tiến độ học tập Backend TYP 2026

Repo này lưu báo cáo, lý thuyết, hướng dẫn, ảnh demo và ví dụ minh họa. Mã nguồn thực thi, SQL và kiểm thử được quản lý tại [EruSentia-Server](https://github.com/Tunhoclaptrinh/EruSentia-Server).

| Tuần | Chủ đề | Trạng thái | Báo cáo / yêu cầu |
|---|---|---|---|
| 1 | Database, OOP và DI | Đã hiệu đính và kiểm chứng trên Docker Ubuntu / PostgreSQL 16 | [Báo cáo](../week-1/WEEK1_REPORT.md), [hướng dẫn](../week-1/docs/00_GUIDE_INDEX.md), [mốc nguồn](../week-1/SOURCE_SNAPSHOT.md) |
| 2 | Framework, RESTful API và ORM | CRUD/ORM, kiểm thử, minh chứng Postman và Swagger cơ bản; nguồn ghim theo commit | [Yêu cầu](../week-2/README.md), [báo cáo](../week-2/WEEK2_REPORT.md), [nguồn](../week-2/SOURCE_SNAPSHOT.md) |
| 3 | Authentication và Authorization | Đã chốt nguồn: JWT, Argon2id, RBAC/ownership, refresh rotation; đã kiểm chứng memory/PostgreSQL và Swagger | [Yêu cầu](../week-3/README.md), [báo cáo](../week-3/WEEK3_REPORT.md), [hướng dẫn](../week-3/docs/00_GUIDE_INDEX.md), [nguồn](../week-3/SOURCE_SNAPSHOT.md) |
| 4 | Message Queue | Đã chốt nguồn: RabbitMQ, transactional outbox, welcome worker, retry/DLQ; đã kiểm chứng và có ảnh demo | [Yêu cầu](../week-4/README.md), [báo cáo](../week-4/WEEK4_REPORT.md), [hướng dẫn](../week-4/docs/00_GUIDE_INDEX.md), [nguồn](../week-4/SOURCE_SNAPSHOT.md) |
| 5 | Caching và Redis | Theo lộ trình | [Yêu cầu tuần 5](../week-5/README.md) |
| 6 | Project tổng hợp | Theo lộ trình | [Yêu cầu project](../week-6/README.md) |

- [Lộ trình đào tạo](TRAINING_SCHEDULE.md).
- [Tài liệu tham khảo](LEARNING_RESOURCES.md).
- [Mẫu báo cáo](WEEKLY_REPORT_TEMPLATE.md).

README tổng và README tuần 1–6 giữ nguyên đề bài từ commit gốc `1e4c528`.
Liên kết `./project` trong README tổng thuộc bản gốc nhưng hiện không có thư mục
tương ứng; yêu cầu project được xem tại [tuần 6](../week-6/README.md).
Hướng dẫn thực hành bổ sung nằm trong mục lục tài liệu từng tuần.

Mỗi báo cáo giữ ngày đo, điều kiện thử nghiệm và bằng chứng thực tế. Khi tái hiện, ghi thêm commit của backend để phân biệt kết quả lịch sử với phiên bản hiện tại.

Mỗi mốc báo cáo đã chốt dùng commit backend đầy đủ và một tag lưu trữ của TYP. Giữ nguyên tag cũ; cập nhật báo cáo thành mốc mới khi cần đối chiếu phiên bản mới.
