Báo Cáo Kỹ Thuật: Framework, RESTful API & ORM
Phần 1: Cơ Sở Lý Thuyết & Kiến Trúc Thiết Kế
1. Công Nghệ Lựa Chọn & Lý Do Đề Xuất
Ngôn ngữ & Runtime: JavaScript / Node.js (v18+)
Framework: Express.js
ORM: Prisma ORM
Hệ quản trị cơ sở dữ liệu: PostgreSQL (hoặc MySQL)
Lý do lựa chọn:
Express.js cung cấp kiến trúc tối giản (minimalist), cấu hình linh hoạt qua middleware pipeline, giúp hiểu sâu luồng Request/Response lifecycle thay vì bị che giấu bởi các abstraction phức tạp.
Prisma ORM mang lại cơ chế type-safe truy vấn, hệ thống migration tự động hóa minh bạch thông qua file schema khai báo dạng declarative (schema.prisma), hạn chế tối đa lỗi runtime và tối ưu hóa performance truy vấn.
2. Cấu Trúc Thư Mục Dự Án (Layered Architecture)
Tách biệt trách nhiệm (Separation of Concerns) theo mô hình chuẩn Controller - Service - Data Access:
Plaintext
rest-api-orm-project/
├── prisma/
│   ├── schema.prisma          # Khai báo Models, Enums, Database connection
│   └── migrations/            # Lịch sử thay đổi schema SQL
├── src/
│   ├── controllers/           # Tiếp nhận Request, parse params/body, gọi Service, trả Response
│   │   ├── student.controller.js
│   │   └── classroom.controller.js
│   ├── services/              # Xử lý toàn bộ Business Logic & gọi Prisma Client
│   │   ├── student.service.js
│   │   └── classroom.service.js
│   ├── routes/                # Định nghĩa URL Endpoints & mapping middleware
│   │   ├── index.js
│   │   └── student.routes.js
│   ├── middlewares/           # Global Error Handler, Validation, Logger
│   │   └── errorHandler.js
│   ├── utils/                 # Chuẩn hóa cấu trúc ApiResponse, AppError
│   │   ├── apiResponse.js
│   │   └── appError.js
│   ├── prisma.js              # Khởi tạo singleton instance PrismaClient
│   └── app.js                 # Cấu hình Express app và load routes
├── .env                       # Biến môi trường (DATABASE_URL, PORT)
├── package.json
└── server.js                  # Entry point lắng nghe port

3. Chuẩn Hóa RESTful API Design
HTTP Methods & Status Codes
GET: Lấy dữ liệu (Idempotent, Safe). Mã: 200 OK.
POST: Tạo mới tài nguyên. Mã: 201 Created.
PUT: Cập nhật toàn bộ tài nguyên (Idempotent). Mã: 200 OK.
DELETE: Xóa tài nguyên (Idempotent). Mã: 200 OK hoặc 204 No Content.
400 Bad Request: Dữ liệu đầu vào sai định dạng.
404 Not Found: Không tìm thấy tài nguyên theo Identifier.
500 Internal Server Error: Lỗi phát sinh ngoài dự kiến từ hệ thống.
Cấu Trúc Response Thống Nhất
Success Response (Phân trang):
JSON
{
  "success": true,
  "statusCode": 200,
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "size": 10,
    "totalPages": 5,
    "totalElements": 48
  }
}

Error Response (Chuẩn RFC 7807 đơn giản hóa):
JSON
{
  "success": false,
  "statusCode": 404,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Không tìm thấy sinh viên có ID 15",
    "details": null
  }
}

4. Khái Niệm ORM & Mô Hình Quan Hệ (Relationship)
ORM (Object-Relational Mapping): Là kỹ thuật lập trình cầu nối giữa mô hình lập trình hướng đối tượng (Object-Oriented) và cơ sở dữ liệu quan hệ (Relational Database). ORM ánh xạ bảng (Table) thành Lớp/Mô hình (Class/Model), các dòng (Row) thành Thực thể (Entity Object).
Lợi ích: Tránh viết SQL thuần dễ gây lỗi SQL Injection, nâng cao tốc độ phát triển, quản lý migration có phiên bản rõ ràng.
Quan hệ thực hành: Thiết lập quan hệ One-to-Many (1-N) giữa Classroom (Lớp học) và Student (Sinh viên):
Một Classroom có nhiều Student.
Một Student thuộc về một Classroom (khóa ngoại classroomId).

