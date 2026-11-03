# Week 5 - Cách chạy và demo

Bản code này được kế thừa từ gói Week 4 có sẵn trong cuộc trò chuyện (bản RabbitMQ). Nếu bạn đã đổi riêng Week 4 sang Kafka trên máy, đừng chép đè source Kafka; cần chuyển phần Redis sang bản Kafka đó.

## 1. Chạy project

Mở terminal tại `week-5/CourseManagement_FinalProjectModule03`.

```powershell
Copy-Item .env.example .env
```

Mở `.env` và sửa `POSTGRES_PASSWORD`, `JWT_SECRET` cùng các biến cần thiết. `JWT_SECRET` phải là Base64 đủ độ dài; giá trị ví dụ không dùng cho production. Nếu muốn tạo tài khoản demo trên **database Week 5 còn trống**, đổi `SEED_DEMO_DATA=true`. Không bật tùy tiện trên database có dữ liệu.

```powershell
docker compose --env-file .env up -d --build
```

Mặc định bản Week 5 dùng port riêng, không đụng container Week 4:

| Dịch vụ | Máy bạn |
| --- | --- |
| API | `http://localhost:8085` |
| PostgreSQL | `localhost:5437` |
| RabbitMQ UI | `http://localhost:15673` |
| Redis | `localhost:6381` |

Xem log ứng dụng:

```powershell
docker compose logs -f app
```

Nếu chỉ muốn chạy Java bằng IntelliJ, bật ba container `postgres rabbitmq redis` rồi đặt `DB_URL=jdbc:postgresql://localhost:5437/final_project_03`, `RABBITMQ_PORT=5673`, `REDIS_PORT=6381` cùng các env còn lại vào Run Configuration.

## 2. Demo bằng Bruno

Mở collection trong [bruno](../CourseManagement_FinalProjectModule03/bruno/). Tạo môi trường Local với `baseUrl=http://localhost:8085` và `password` tương ứng. Nhóm request nằm trong [week5-cache](../CourseManagement_FinalProjectModule03/bruno/week5-cache/).

1. Chạy **01 - Login ADMIN**, copy `accessToken` vào biến Bruno `adminToken`.
2. Chạy **02 - Find PUBLISHED Course**, lấy `courseId` của khóa học `PUBLISHED`. Nếu chưa có thì tạo khóa học rồi chuyển trạng thái sang `PUBLISHED` bằng API Admin cũ.
3. Điền `courseId` và xóa key cũ để có kết quả miss thật:

   ```powershell
   docker exec course_management_week5_redis redis-cli DEL week5:course:detail:1
   ```

   Đổi `1` thành ID bạn chọn.
4. Chạy **03 - Course Detail (MISS)**: response header `X-Cache: MISS`.
5. Chạy lại **04 - Course Detail (HIT)**: `X-Cache: HIT`. Bài này không query DB khi hit.
6. Chạy **05 - Detail Without Cache (DB)**: `X-Cache: BYPASS`.
7. Xem header `X-Elapsed-Ms` của ba request, ghi số liệu thật vào bảng bên dưới. Nên chạy vài lần để so sánh tương đối.

| Lần gọi | X-Cache | X-Elapsed-Ms |
| --- | --- | --- |
| Lần 1 | MISS | ... |
| Lần 2 | HIT | ... |
| Không dùng cache | BYPASS | ... |

Đọc trực tiếp Redis:

```powershell
docker exec course_management_week5_redis redis-cli TTL week5:course:detail:1
docker exec course_management_week5_redis redis-cli GET week5:course:detail:1
```

## 3. Kiểm tra cache có bị cũ không

- Với ADMIN, sửa tiêu đề khóa học bằng `PUT /api/courses/{id}` hoặc `PATCH /api/courses/{id}`.
- Gọi lại `GET /api/courses/{id}`. Kết quả phải là `MISS` với tiêu đề mới; sau đó `HIT`.
- Sửa bài học hoặc review cũng phải xóa cache của khóa học.
- Với khóa học `DRAFT`, API không ghi cache; không dùng vai trò STUDENT để truy cập draft.
- Nếu tắt Redis **sau khi app đã chạy**, request vẫn đọc PostgreSQL (nhưng log sẽ có cảnh báo). Không chạy `docker compose down` vì lệnh đó cũng dừng DB và app.

## 4. Chạy test

```powershell
.\gradlew.bat test --tests "*Week5CourseCacheTest" --tests "*Week5CourseControllerTest"
```

Test này dùng Mockito, không cần Redis thật. Để kiểm tra cả hệ thống, chạy thêm `.\gradlew.bat test`.

## 5. Ảnh cần chụp để nộp

- Redis đang chạy (`docker compose ps`).
- Bruno lần 1 MISS, lần 2 HIT và BYPASS, kèm thời gian.
- Lệnh Redis `TTL` / `GET` thấy key.
- Sau khi update, request đầu tiên lại MISS.

**Ghi chú:** Số đo phụ thuộc máy và DB, không nên ghi sẵn số liệu chưa chạy. Nếu dùng Redis/PostgreSQL ở mạng khác thì thời gian cũng khác.
