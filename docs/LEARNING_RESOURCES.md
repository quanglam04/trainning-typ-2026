# 📚 LỘ TRÌNH HỌC TẬP & DANH MỤC TÀI LIỆU CHỌN LỌC (6 TUẦN)
> Tổng hợp lộ trình học thực chiến, phương pháp học mỗi tuần và kho tài liệu tham khảo chính thức cho từng chủ đề.

---

> Các liên kết tài liệu bên ngoài được đối chiếu ngày 20/10/2026. Tài liệu PostgreSQL dùng nhánh phiên bản 16 để khớp cấu hình dự án.

## 🧭 1. CHIẾN LƯỢC HỌC TẬP MỖI TUẦN (NHỊP ĐIỆU LẶP LẠI)

Để không bị đuối và đảm bảo có sản phẩm chạy thật mỗi tuần:

```
Thứ 2 – Thứ 3                 Thứ 4 – Thứ 5                Thứ 6                  Cuối tuần
─────────────────────────────────────────────────────────────────────────────────────────────
• Học lý thuyết ngắn          • Áp dụng ngay vào code      • Viết báo cáo tuần    • Nghỉ ngơi & đệm bù
• Làm demo nhỏ độc lập          của Sentia Hub             • Chụp số liệu chạy    • Ôn 5 câu hỏi phỏng
• "Mỗi khái niệm phải         • Tự giải thích: "Không có     thật / Benchmark       vấn với Mentor
  chạy được thứ gì đó"          nó thì hệ thống hỏng ở đâu?"• Push code nộp bài
```

---

## 📖 2. DANH MỤC TÀI LIỆU HỌC TẬP THEO TỪNG TUẦN

### 📌 TUẦN 1: CƠ SỞ DỮ LIỆU & LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (28/09 – 02/10)

#### 🎯 Trọng tâm:
SQL nâng cao, Indexing, Database Locking, ACID Transactions, OOP & Dependency Injection.

#### 📚 Tài liệu học chọn lọc:
1. **PostgreSQL Official Tutorial:**
   👉 [PostgreSQL 16 — Official Tutorial](https://www.postgresql.org/docs/16/tutorial.html) — *Ngon và dễ hiểu nhất cho người mới bắt đầu: từ SELECT, JOIN, GROUP BY đến Transaction.*
2. **Kỹ thuật Indexing (Bắt buộc phải đọc):**
   👉 [Use The Index, Luke! (use-the-index-luke.com)](https://use-the-index-luke.com/) — *Kinh thánh về Database Indexing cho lập trình viên backend. Giải thích tại sao B-Tree chạy nhanh, khi nào index bị vô hiệu.*
3. **Database Locking & Transactions (ACID):**
   👉 [PostgreSQL Explicit Locking Documentation](https://www.postgresql.org/docs/16/explicit-locking.html) — *Tìm hiểu về `SELECT ... FOR UPDATE`, Row-level lock và Table-level lock.*
4. **OOP & TypeScript Clean Code:**
   👉 [TypeScript Official Handbook (Classes & Interfaces)](https://www.typescriptlang.org/docs/handbook/2/classes.html)
   👉 [Domain-Driven Design (Martin Fowler)](https://martinfowler.com/bliki/DomainDrivenDesign.html) — *Hiểu về Entity, Invariants và Encapsulation.*
5. **Đọc hiểu `EXPLAIN ANALYZE`:**
   👉 [Tài liệu chính thức EXPLAIN của PostgreSQL](https://www.postgresql.org/docs/16/using-explain.html)

---

### 📌 TUẦN 2: FRAMEWORK, RESTFUL API & ORM (05/10 – 09/10)

#### 🎯 Trọng tâm:
Cấu trúc Controller/Service/Repository, HTTP Status Codes, Error Handling chuẩn hóa, DTO Validation, ORM Mapping và tránh lỗi N+1 Query.

#### 📚 Tài liệu học chọn lọc:
1. **RESTful API Design Best Practices:**
   👉 [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines) — *Chuẩn mực thiết kế URL, mã lỗi và versioning.*
   👉 [RFC 9457: Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc9457) — *Đặc tả Problem Details; thay thế RFC 7807.*
2. **Framework & Architecture:**
   👉 [NestJS Official Documentation](https://docs.nestjs.com/) — *Đọc kỹ phần Overview: Controllers, Providers, Modules, Pipes (Validation).*
3. **ORM & Database Access:**
   👉 [TypeORM Official Docs](https://typeorm.io/) hoặc [Prisma Docs](https://www.prisma.io/docs)
   👉 [TypeORM — Performance and optimization](https://typeorm.io/docs/advanced-topics/performance-optimizing/)

---

### 📌 TUẦN 3: AUTHENTICATION & AUTHORIZATION (12/10 – 16/10)

#### 🎯 Trọng tâm:
Password Hashing (bcrypt/argon2), JWT Token (Header/Payload/Signature), Refresh Token Rotation, Middleware bảo vệ API và Role-based Access Control (RBAC).

#### 📚 Tài liệu học chọn lọc:
1. **Bảo mật mật khẩu:**
   👉 [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html) — *Tại sao tuyệt đối không dùng MD5/SHA, vì sao phải dùng Salt & Cost factor.*
2. **JSON Web Tokens (JWT):**
   👉 [RFC 7519 — JSON Web Token](https://datatracker.ietf.org/doc/html/rfc7519) — *Hiểu cấu trúc 3 phần của token và cơ chế xác thực không lưu trạng thái (Stateless).*
3. **Refresh Token Flow:**
   👉 [Auth0: Refresh Token Rotation](https://auth0.com/docs/secure/tokens/refresh-tokens/refresh-token-rotation) — *Cơ chế cấp lại Access Token an toàn chống bị đánh cắp.*

---

### 📌 TUẦN 4: MESSAGE QUEUE (19/10 – 23/10)

#### 🎯 Trọng tâm:
Giao tiếp Bất đồng bộ (Async), Producer/Consumer, RabbitMQ Architecture (Exchange, Queue, Binding), Idempotency, Dead Letter Queue (DLQ).

#### 📚 Tài liệu học chọn lọc:
1. **RabbitMQ Official Tutorials (Cực hay):**
   👉 [RabbitMQ Tutorials: Hello World & Work Queues](https://www.rabbitmq.com/tutorials/tutorial-one-javascript)
   👉 [RabbitMQ Publish/Subscribe Pattern](https://www.rabbitmq.com/tutorials/tutorial-three-javascript)
2. **Độ tin cậy của thông điệp (Reliability):**
   👉 [Dead Letter Exchanges (DLX) in RabbitMQ](https://www.rabbitmq.com/docs/dlx)
   👉 [What is Idempotence in Distributed Systems?](https://microservices.io/patterns/communication-style/idempotent-consumer.html) — *Chống xử lý lặp message.*

---

### 📌 TUẦN 5: CACHING & REDIS (26/10 – 30/10)

#### 🎯 Trọng tâm:
Cơ chế Cache-Aside, Redis Data Types (String, Hash, Sorted Set), Cache Invalidation, Phòng tránh Cache Avalanche & Cache Penetration.

#### 📚 Tài liệu học chọn lọc:
1. **Redis Fundamentals:**
   👉 [Redis Quick Start](https://redis.io/tutorials/howtos/quick-start/) và [Redis data types](https://redis.io/docs/latest/develop/data-types/) — *Khởi động Redis và tìm hiểu các kiểu dữ liệu cùng lệnh tương ứng.*
2. **Chiến lược Caching:**
   👉 [AWS — Caching patterns: Cache-Aside và Write-Through](https://docs.aws.amazon.com/whitepapers/latest/database-caching-strategies-using-redis/caching-patterns.html)
3. **Các lỗi kinh điển về Cache:**
   👉 [AWS — Caching challenges and strategies](https://builder.aws.com/content/3K9TeXp6strMiJ4vhakpRD7BQWO/caching-challenges-and-strategies)

---

### 📌 TUẦN 6: PROJECT TỔNG HỢP & TRIỂN KHAI (02/11 – 06/11)

#### 🎯 Trọng tâm:
Tích hợp toàn bộ hệ thống, Docker Compose cho Production, Deploy lên VPS, đo kiểm tải (Load Testing với k6 / autocannon), viết Báo cáo tổng kết.

#### 📚 Tài liệu học chọn lọc:
1. **DevOps & Container:**
   👉 [Docker Multi-stage Builds Guide](https://docs.docker.com/build/building/multi-stage/) — *Tách giai đoạn build và runtime để chỉ đưa artifact cần thiết vào image cuối.*
2. **Load Testing:**
   👉 [Grafana k6 documentation](https://grafana.com/docs/k6/latest/) — *Viết script test đo RPS (Requests Per Second) và độ trễ p95/p99.*

---

## 🎯 3. BÍ KÍP CHUẨN BỊ PHỎNG VẤN VỚI MENTOR (08/11)

Mentor khi phỏng vấn cuối khóa **không bao giờ hỏi thuộc lòng cú pháp**, mà luôn hỏi theo 3 mô thức:
1. **"Tại sao em lại chọn công nghệ X mà không dùng công nghệ Y?"**
   *(VD: Tại sao dùng PostgreSQL JSONB mà không dùng MongoDB? Tại sao dùng RabbitMQ mà không dùng Kafka?)*
2. **"Nếu hệ thống gặp sự cố X thì xử lý thế nào?"**
   *(VD: Nếu worker RabbitMQ bị crash khi đang xử lý message thì sao? Nếu Redis bị chết thì DB có bị sập theo không?)*
3. **"Số liệu chứng minh tối ưu của em đâu?"**
   *(Mở [báo cáo tuần 1](../week-1/WEEK1_REPORT.md) để đối chiếu kết quả `EXPLAIN ANALYZE`, điều kiện đo và ảnh minh chứng).*
