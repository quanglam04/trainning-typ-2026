# Restaurant Project ORM

Hệ thống quản lý nhà hàng gồm Express/Prisma/PostgreSQL, React, Redis và Kafka.

## Chạy local

### 1. Khởi động Hạ tầng (Database, Cache & Message Queue)
- Sao chép `be/.env.example` thành `be/.env` và cập nhật thông số kết nối.
- Khởi động PostgreSQL, Redis và Kafka bằng Docker:
  ```bash
  cd be
  docker compose up -d
  ```

### 2. Khởi động Backend (Express API)
```bash
cd be
npm install
npx prisma migrate deploy
npm run seed:admin
npm run dev # Chạy server tại http://localhost:3000
```

### 3. Khởi động Frontend (React + Vite SPA)
```bash
cd ../fe
npm install
npm run dev # Chạy ứng dụng tại http://localhost:5173
```
> **Lưu ý:** Giao diện SPA hiện đại được quản lý độc lập tại thư mục `fe` (port 5173) và giao tiếp với Backend qua REST API (`http://localhost:3000/api`). Bạn có thể tùy biến địa chỉ API qua file `fe/.env` bằng biến `VITE_API_URL`.

## Kiểm tra

- Backend: `cd be && npm test && npm run validate`
- Health check: `GET /api/health`

## Contract nghiệp vụ quan trọng

- Xem/cập nhật đặt bàn yêu cầu JWT và role `admin`, `manager` hoặc `waiter`; tạo đặt bàn vẫn là API public.
- Đơn đi theo luồng `pending -> confirmed/cooking -> ready -> served` và chỉ payment mới chuyển sang `completed`.
- Chỉ đơn `served` được thanh toán.
- Một bàn không nhận hai lượt đặt có thời gian cách nhau dưới hai giờ và số khách không được vượt sức chứa.
