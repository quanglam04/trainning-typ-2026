# BÁO CÁO TỔNG KẾT GIAI ĐOẠN TRAINING & PROJECT THỰC HÀNH

- **Người thực hiện:** Học viên Training Backend
- **Dự án thực hành:** Hệ thống Quản lý Nhà hàng (Restaurant Management System)
- **Công nghệ sử dụng:** Node.js (Express), Prisma ORM, PostgreSQL, Redis, Apache Kafka, React

---

## 1. Trong 5 tuần vừa qua em đã học được những gì?

Năm tuần training trôi qua rất nhanh nhưng lượng kiến thức nạp vào thực sự rất lớn và thực tế, đi từ việc viết code chạy được cho đến việc tổ chức hệ thống sao cho chịu tải và an toàn:

- **Tuần 1 & Tuần 2 (Nền tảng Node.js & REST API):**
  - Em hiểu sâu hơn về cơ chế Event Loop, Non-blocking I/O của Node.js, biết vì sao không được viết các tác vụ đồng bộ gây nghẽn (blocking) luồng chính.
  - Học cách tổ chức source code theo mô hình nhiều lớp (Routes -> Controllers -> Services -> Database) để code rõ ràng, không bị nhồi nhét logic vào controller.
  - Biết cách bắt lỗi tập trung qua Middleware thay vì chỗ nào cũng `try/catch` bừa bãi, chuẩn hóa lại mã HTTP (`200`, `201`, `400`, `401`, `403`, `409`, `500`).

- **Tuần 3 (PostgreSQL & Prisma ORM):**
  - Biết cách thiết kế bảng, khóa chính, khóa ngoại và quan hệ giữa các thực thể (1-n, n-n).
  - Làm quen với Prisma ORM, thấy việc tương tác với database tiện và ít lỗi chính tả hơn hẳn so với viết raw query SQL.
  - Học về Database Transaction: hiểu tại sao những thao tác liên quan đến tiền bạc, kho bãi thì bắt buộc phải bọc trong Transaction để nếu một bước lỗi thì toàn bộ tự rollback, không làm sai lệch số liệu.

- **Tuần 4 (Authentication & Phân quyền RBAC):**
  - Hiểu bản chất của JWT. Trước đây em chỉ biết dùng 1 token duy nhất, sau tuần này em mới hiểu cơ chế Token kép: Access Token chỉ nên sống ngắn hạn (vài phút) để giảm thiểu rủi ro nếu bị lộ, còn Refresh Token sống dài hơn (vài ngày) và phải lưu vào Database để khi cần (người dùng đổi mật khẩu hoặc đăng xuất) thì Server có thể thu hồi (revoke) ngay lập tức.
  - Phân quyền (RBAC) chặt chẽ theo role để chặn người dùng gọi bậy vào các API không được phép.

- **Tuần 5 (Redis Caching & Message Queue Kafka):**
  - **Redis:** Biết cách dùng Redis làm tầng đệm nằm trước Database (Cache-Aside). Thấm thía nhất là bài toán "Cache Invalidation" - xóa cache đúng lúc khi dữ liệu thay đổi, nếu không người dùng sẽ nhìn thấy dữ liệu cũ rích.
  - **Kafka:** Học tư duy lập trình hướng sự kiện (Event-Driven). Tách những việc nặng, tốn thời gian ra chạy ngầm phía sau thông qua Message Queue thay vì bắt người dùng phải đứng chờ.

---

## 2. Em đã áp dụng các kiến thức đó vào Project Nhà hàng như thế nào?

Em không làm dàn trải quá nhiều tính năng râu ria, mà tập trung làm sâu các luồng nghiệp vụ cốt lõi của một nhà hàng:

### a. Luồng tạo đơn & xử lý trừ kho an toàn (Prisma Transaction)
- **Vấn đề thực tế:** Giả sử một món chỉ còn đúng 2 phần, mà có 2 bồi bàn cùng lúc bấm tạo đơn 2 phần trên điện thoại. Nếu chỉ `find` rồi mới `update` kiểu thông thường thì chắc chắn kho sẽ bị trừ thành âm (Race Condition).
- **Cách em giải quyết:** Em dùng `prisma.$transaction` mức cô lập `Serializable` kết hợp với câu lệnh update có điều kiện:
  ```javascript
  await tx.product.updateMany({
    where: { 
      id: item.product_id, 
      status: 'available', 
      quantity: { gte: item.quantity } 
    },
    data: { quantity: { decrement: item.quantity } }
  });
  ```
  Nếu món bị ai đó mua trước khiến số lượng không còn đủ, câu lệnh sẽ không update được dòng nào, em lập tức cho throw lỗi `409 Conflict` để rollback toàn bộ đơn hàng. Đồng thời kiểm tra luôn trạng thái bàn ăn, chặn việc 2 bồi bàn vô tình mở 2 đơn khác nhau trên cùng một bàn.

### b. Quản lý trạng thái đơn hàng (Order Lifecycle) theo đúng vai trò
- Em dựng một ma trận chuyển đổi trạng thái rất chặt chẽ:
  $$\text{Đơn mới (pending)} \rightarrow \text{Bếp đang nấu (cooking)} \rightarrow \text{Nấu xong (ready)} \rightarrow \text{Đã bưng ra bàn (served)} \rightarrow \text{Đã thanh toán (completed)}$$
- Ai làm việc nấy: Bếp chỉ được bấm từ `pending` sang `cooking` và `ready`; Bồi bàn chỉ được bấm chuyển sang `served`; và chỉ có Thu ngân mới được bấm xác nhận thanh toán (`completed`).
- Nếu khách đổi ý hủy đơn: Chỉ có Quản lý (`manager`) hoặc `admin` mới có quyền bấm Hủy. Khi hủy, hệ thống tự động cộng trả lại toàn bộ số lượng món vào kho và tự động trả bàn về trạng thái trống (`available`).

### c. Tăng tốc hệ thống bằng Redis Cache
- Khách vào quán thường xuyên xem menu, nếu mỗi lần xem lại query Database thì rất lãng phí tài nguyên. Em đưa danh sách món ăn vào Redis cache với key phân trang động (`products:list:...`).
- Nhưng cái quan trọng nhất là **xóa cache**: Cứ mỗi khi có đơn hàng được tạo (kho bị trừ), món ăn bị sửa giá, hay đơn hàng bị hủy (kho được hoàn lại), em đều gọi hàm xóa các key `products:*` trong Redis. Nhờ vậy menu luôn hiển thị số lượng tồn kho chuẩn xác mà database lại rất nhàn.

---

## 3. Phần khó nhất trong Project: Tích hợp Message Queue (Apache Kafka)

Trong cả dự án, phần làm em mất nhiều thời gian tìm hiểu, debug và cũng thấy "đã" nhất khi thông suốt chính là **tích hợp Apache Kafka để gửi hóa đơn và thư đặt bàn bất đồng bộ**.

```
[Thu ngân bấm Thanh toán]
          │
          ▼ (Xử lý DB cực nhanh: < 30ms)
   [Payment Service] ──────► [PostgreSQL] (Lưu hóa đơn, đổi trạng thái)
          │
          ▼ (Bắn tin nhắn rồi trả về kết quả ngay)
   [Kafka Producer]  ──────► [Topic: 'order-paid']
                                    │
                                    ▼ (Consumer nhặt tin nhắn chạy ngầm)
                             [Order Consumer] ──────► [Gửi Email SMTP]
```

### a. Vấn đề gặp phải lúc chưa có Kafka (Bị nghẽn mạng)
- Ban đầu, em viết code theo cách thông thường: Khi khách thanh toán xong, em gọi luôn hàm của Nodemailer để gửi email hóa đơn chi tiết cho khách.
- Khi test thực tế, em thấy giao diện quay vòng vòng mất tận **2 đến 3.5 giây** mới báo thành công. Lý do là server Node.js phải chờ kết nối và bắt tay mạng với máy chủ SMTP của Gmail.
- Thực tế ở nhà hàng giờ cao điểm, khách đứng xếp hàng chờ tính tiền mà thu ngân bấm một cái phải đứng nhìn màn hình xoay 3 giây thì trải nghiệm rất tệ. Chưa kể lỡ mạng chập chờn hoặc Gmail bị lỗi thì đơn hàng thanh toán trong database cũng dễ bị lỗi oan theo.

### b. Cách em tư duy và giải quyết bằng Kafka
Em quyết định tách đôi công việc này ra:
1. **Việc cần làm ngay (Đồng bộ):** Thu ngân cần biết tiền đã trừ chưa, bàn đã được trả chưa. Việc này chỉ cần ghi vào PostgreSQL và trả lời cho thu ngân ngay lập tức.
2. **Việc có thể làm sau (Bất đồng bộ):** Gửi email hóa đơn cho khách. Khách nhận email chậm hơn 2-3 giây cũng không sao, miễn là có hóa đơn gửi về hòm thư.

**Luồng triển khai cụ thể:**
- **Phía Producer:** Sau khi Database commit xong xuôi việc thanh toán, em cho gọi `sendOrderPaidEvent()` để đẩy một gói tin JSON (gồm mã đơn, tên khách, email, danh sách món, tổng tiền) vào Kafka topic tên là `order-paid`. Bắn xong là API trả về `200 OK` cho thu ngân ngay, không cần chờ ai cả.
- **Phía Broker:** Em cấu hình topic có 3 partitions để sẵn sàng cho việc mở rộng sau này nếu đơn hàng tăng cao.
- **Phía Consumer:** Em tạo một worker chạy nền độc lập (`order.consumer.js`). Nó liên tục lắng nghe topic `order-paid`. Hễ thấy có tin nhắn mới rơi vào là nó lấy ra, gắn dữ liệu vào template HTML rồi tự túc gửi email đi.

### c. Những "cú vấp" và kinh nghiệm xương máu khi làm Kafka
- **Lỗi biến ngoài phạm vi (Scope):** Lúc đầu em bắn event ở ngoài transaction nhưng lại gọi nhầm biến nội bộ của transaction khiến server báo lỗi `ReferenceError: customer is not defined` làm treo luôn tiến trình. Em phải bình tĩnh đọc log và sửa lại cách lấy dữ liệu từ kết quả trả về của Prisma.
- **Xử lý khi Kafka gặp sự cố:** Em đặt toàn bộ thao tác bắn Kafka vào khối `try/catch` độc lập. Nếu lỡ container Kafka bị mất kết nối, hệ thống vẫn ghi nhận khách đã trả tiền trong Database (không làm mất tiền của khách), chỉ in ra log cảnh báo để gửi mail bù sau, chứ không được để lỗi gửi mail làm sập luôn API thanh toán.
- **Kết quả đo đạc:** 
  - Trước khi dùng Kafka: Bấm thanh toán mất khoảng **2.500ms**.
  - Sau khi đưa Kafka vào: Bấm thanh toán chỉ mất **khoảng 30ms** (nhanh hơn gần 90 lần). Thu ngân ấn một phát là xong ngay, còn email vẫn túc tắc gửi đến hộp thư của khách sau đó vài giây.

---

## 4. Bài học em rút ra sau đợt training này

1. **Hiểu bản chất quan trọng hơn copy code:** Bây giờ có nhiều công cụ AI hỗ trợ viết code rất nhanh, nhưng nếu mình không hiểu cơ chế (như tại sao chỗ này phải dùng Transaction, tại sao chỗ kia phải xóa Cache, event loop hoạt động thế nào) thì khi code chạy sai hoặc gặp lỗi tương tranh (Race Condition) sẽ hoàn toàn bất lực không biết debug từ đâu.
2. **Biết cách debug hệ thống phân tán:** Em học được thói quen xem log tuần tự: lỗi phát sinh ở tầng Express, ở câu lệnh SQL của Prisma, ở bộ nhớ Redis hay ở Message Queue Kafka.
3. **Những điểm em thấy dự án có thể làm tốt hơn nếu có thêm thời gian:**
   - Dùng thêm mẫu thiết kế Outbox Pattern để chắc chắn 100% không bao giờ bị lệch dữ liệu giữa Database và Kafka.
   - Thêm cơ chế Dead Letter Queue (DLQ) để tự động gửi lại các email bị lỗi nhiều lần.

**Tóm lại:** Khóa training 5 tuần đã giúp em thay đổi tư duy rất nhiều, từ việc chỉ biết viết CRUD đơn giản sang việc biết cách tư duy thiết kế một hệ thống backend bài bản, an toàn dữ liệu và tối ưu hiệu năng. Em tự tin mình đã hiểu rõ toàn bộ luồng code và sẵn sàng cho buổi phỏng vấn sắp tới.
