# 🐳 SỔ TAY KỸ THUẬT TUẦN 1: DOCKER & MÔI TRƯỜNG DATABASE THỰC CHIẾN

> Đối chiếu thực hành: [Docker Compose](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/docker/docker-compose.dev.yml), [Biến môi trường mẫu](https://github.com/Tunhoclaptrinh/EruSentia-Server/blob/b851e1576ae48fe62efcec10dd2d912e011a6220/.env.example). Các lệnh shell trong bài chạy từ thư mục gốc repo `EruSentia-Server` sau khi setup theo [README backend](https://github.com/Tunhoclaptrinh/EruSentia-Server/tree/b851e1576ae48fe62efcec10dd2d912e011a6220).
> Ghi chép các kiến thức "ngầm", bài học thực tế và thông số kết nối quan trọng cần nhớ trong Tuần 1.

> Máy hiện tại đã chuyển sang Ubuntu native: xem [phần chạy thực tế trên Ubuntu](#7-chạy-thực-tế-trên-ubuntu-native). Mục WSL 2 phía dưới giữ ghi chép môi trường Windows cũ.

---

## 📌 1. BẢNG TRA CỨU NHANH THÔNG SỐ KẾT NỐI (CHEATSHEET)

Mỗi khi cần mở giao diện quản trị Database hoặc kết nối code, hãy mở bảng này:

| Thông số | Giá trị | Giải thích |
|---|---|---|
| **URL Giao diện Web** | `http://localhost:8080` | Mở bằng trình duyệt Chrome/Edge (Adminer GUI) |
| **System** | `PostgreSQL` | Chọn trong dropdown của Adminer (mặc định hay là MySQL) |
| **Server** *(Khi dùng Adminer)* | `postgres` | **Tên service** trong Docker network nội bộ (xem mục 3) |
| **Server** *(Khi dùng code Node/DBeaver)* | `localhost` hoặc `127.0.0.1` | Vì truy cập từ ngoài máy tính của bạn vào |
| **Port** | `5432` | Cổng mặc định của PostgreSQL |
| **Username** | `postgres` | Tài khoản superuser mặc định |
| **Password** | `postgres_dev_password` | Mật khẩu dev đang ghi trong Compose; `.env.example` phải khớp cấu hình này |
| **Database Name** | `sentia_hub_db` | Tên cơ sở dữ liệu của dự án |

---

## 🧠 2. BÀI HỌC KINH NGHIỆM: ẢO HÓA (VIRTUALIZATION) & WSL 2

Ví dụ sự cố môi trường Windows khi dùng Docker Desktop với WSL 2 backend:

```
[Phần cứng CPU]              ➔ [Hệ điều hành Windows]         ➔ [Ứng dụng Docker]
Intel VT-x / AMD-V              WSL 2 Subsystem                  Docker Desktop
(Bật trong BIOS Mainboard)      (Lệnh: wsl --install)            (Chạy mượt mà)
```

- **Sự cố vừa gặp:** Task Manager báo `Virtualization: Enabled` (phần cứng đã sẵn sàng), nhưng Docker vẫn báo `Virtualization support not detected`.
- **Bản chất nguyên nhân:** Với Linux containers và WSL 2 backend, Docker Desktop sử dụng môi trường Linux do WSL 2 cung cấp. CPU bật ảo hóa chưa đủ; cần kiểm tra cài đặt WSL và các Windows features liên quan. Docker Desktop còn có Hyper-V backend; không thể kết luận mọi lỗi ảo hóa đều do thiếu WSL.
- **Câu lệnh thần thánh để sửa:**
  ```powershell
  wsl --install --no-distribution
  ```
  *(Cờ `--no-distribution` giúp cài nhân WSL 2 nhẹ nhất có thể mà không bị ép tải bản Ubuntu 1GB về máy).*

---

## 🔌 3. BÍ MẬT CONTAINER NETWORKING: TẠI SAO LẠI LÀ `postgres` MÀ KHÔNG PHẢI `localhost`?

Khi đăng nhập trên Adminer (`http://localhost:8080`), nhiều bạn hay gõ Server là `localhost` và bị báo lỗi **Connection Refused**. Tại sao?

```
┌──────────────────────── MÁY TÍNH CỦA BẠN (HOST) ────────────────────────┐
│                                                                          │
│  Trình duyệt Chrome (gõ http://localhost:8080)                           │
│                                │                                         │
│                      ┌─────────┴─────────────────────────┐               │
│                      │ DOCKER BRIDGE NETWORK NỘI BỘ      │               │
│                      │                                   │               │
│                      │   [Container Adminer]             │               │
│                      │          │                        │               │
│                      │          │ (gọi tên "postgres")   │               │
│                      │          ▼                        │               │
│                      │   [Container PostgreSQL]          │               │
│                      │   (Service name: postgres)        │               │
│                      └───────────────────────────────────┘               │
└──────────────────────────────────────────────────────────────────────────┘
```

1. **Từ máy bạn (Host) gọi vào:** Bạn gõ `localhost:8080` (trình duyệt) hoặc kết nối Node.js `localhost:5432` vì cổng đã được **Map (chuyển tiếp)** ra ngoài máy tính.
2. **Từ Adminer gọi sang PostgreSQL:** Cả 2 container nằm chung một **mạng riêng Docker**. Trong mạng riêng này, `localhost` của Adminer chính là... bản thân cái Adminer! Muốn nói chuyện với PostgreSQL, Adminer phải gọi theo **tên Service** được đặt trong file docker-compose: chính là chữ **`postgres`**!

---

## ✨ 4. "PHÉP MÀU" TỰ ĐỘNG CHẠY SQL (`/docker-entrypoint-initdb.d/`)

Hãy nhìn lại 2 dòng này trong file cấu hình `docker-compose.dev.yml`:

```yaml
volumes:
  - ../sql/01_schema.sql:/docker-entrypoint-initdb.d/01_schema.sql:ro
  - ../sql/02_indexes.sql:/docker-entrypoint-initdb.d/02_indexes.sql:ro
```

- **Nguyên lý hoạt động:** Image chính thức của PostgreSQL được lập trình sẵn một quy tắc: *Mỗi khi container được khởi tạo lần đầu tiên với volume trống, nó sẽ tự động xử lý các file init được hỗ trợ (như `.sql`, `.sh`) trong `/docker-entrypoint-initdb.d/` theo thứ tự tên file.*
- **Lợi ích:** Bạn không cần phải mở giao diện lên rồi paste từng dòng lệnh SQL bằng tay. Chỉ cần `docker compose -f docker/docker-compose.dev.yml up -d`, toàn bộ 9 bảng nghiệp vụ và các Index đã được tạo sẵn trong nháy mắt!

---

## ⚠️ 5. XỬ LÝ SỰ CỐ: XUNG ĐỘT CỔNG (PORT COLLISION)

- **Hiện tượng:** Khi chạy `docker compose up`, terminal báo lỗi:  
  `Bind for 0.0.0.0:8080 failed: port is already allocated`.
- **Nguyên nhân:** Cổng `8080` (hoặc `5432`) đang bị một ứng dụng khác chiếm dụng (ví dụ: container tutorial cũ của Docker, hoặc một dịch vụ Oracle/Tomcat đang chạy ngầm).
- **Cách xử lý chuẩn:**
  1. Kiểm tra xem container nào đang chạy:
     ```bash
     docker ps
     ```
  2. Dừng và xóa container chiếm cổng:
     ```bash
     docker stop <ID_hoặc_Tên_container>
     docker rm <ID_hoặc_Tên_container>
     ```

---

## 🛡️ 6. 4 LỆNH DOCKER THỰC CHIẾN NẰM LÒNG

| Lệnh | Khi nào dùng? |
|---|---|
| `docker compose -f docker/docker-compose.dev.yml up -d` | **Mỗi sáng bắt đầu code:** Bật hệ thống DB lên |
| `docker compose -f docker/docker-compose.dev.yml down` | **Tối hết giờ làm:** Tắt container để giải phóng RAM cho máy (Dữ liệu vẫn còn nguyên) |
| `docker compose -f docker/docker-compose.dev.yml ps` | **Khi code báo lỗi mất kết nối DB:** Kiểm tra xem container có đang `Up` không |
| `docker compose -f docker/docker-compose.dev.yml down -v` | ⚠️ **Khi muốn RESET DB về số 0:** Xóa sạch volume dữ liệu cũ để nạp lại từ đầu |

Nguồn: [Docker Desktop WSL 2 backend](https://docs.docker.com/desktop/features/wsl/), [Postgres image initialization](https://hub.docker.com/_/postgres). Init scripts chỉ chạy khi data directory trống; sửa file SQL không tự cập nhật volume đã tồn tại. Các giá trị dev trong bảng trên không tự đổi chỉ bằng sửa `.env` khi Compose đang ghi cứng chúng.

## 7. Chạy thực tế trên Ubuntu native

Ngày cập nhật **20/10/2026**, môi trường đã chạy: **Ubuntu 26.04.1 LTS x86_64**,
Docker Engine **29.1.3**, Compose **2.40.3**, context **default**. Đây là Ubuntu native;
phần WSL 2 ở mục 2 là ghi chép môi trường Windows trước đây, không phải bước cài cho máy này.
[Báo cáo tuần 1](../WEEK1_REPORT.md) ghi kết quả chạy và ảnh Adminer thật trên Ubuntu.

### Kiểm tra Docker trên Ubuntu

```bash
docker --version
docker compose version
docker context ls
docker info
```

`docker --version` chỉ xác nhận CLI; `docker info` cần có phần **Server** mới xác nhận
kết nối được daemon. Máy này đã có Docker/Compose, nên không cần cài lại hoặc trộn gói
Ubuntu `docker.io` với Docker CE. Nếu cài máy mới, chọn một nguồn cài đặt và làm theo
[hướng dẫn Docker Engine cho Ubuntu](https://docs.docker.com/engine/install/ubuntu/).

Nếu daemon chưa chạy, thực hiện trong terminal Ubuntu của máy:

```bash
sudo systemctl start docker
systemctl is-active docker
docker info
```

Mật khẩu sudo nhập tại terminal của bạn. Nếu báo permission denied ở socket, kiểm tra
quyền của user theo [Linux post-install](https://docs.docker.com/engine/install/linux-postinstall/);
nhóm `docker` cấp quyền tương đương root. Nếu chọn thêm user vào nhóm, cần đăng xuất/đăng
nhập lại. Không dùng `chmod 666 /var/run/docker.sock` để bỏ qua quyền truy cập.
Nếu đang dùng Docker Desktop for Linux, mở ứng dụng và chọn đúng context của Desktop;
Docker Desktop và Engine có kho container/volume riêng theo context.

### Chạy đúng dự án hiện có

Chạy trong repo backend; giữ nguyên TypeScript, package scripts và Compose hiện tại:

```bash
cd /home/tuans-ubuntu/Projects/Personal/EruSentia/erusentia-server
# Chỉ tạo .env nếu chưa có, không ghi đè cấu hình đang dùng.
if [ ! -f .env ]; then cp .env.example .env; fi
pnpm install --frozen-lockfile
docker compose -f docker/docker-compose.dev.yml config --quiet
docker compose -f docker/docker-compose.dev.yml up -d --wait
docker compose -f docker/docker-compose.dev.yml ps
pnpm db:check
pnpm db:schema
pnpm db:indexes
pnpm db:seed
pnpm db:seed
pnpm typecheck
pnpm test
pnpm build
pnpm start
pnpm demo:oop
pnpm demo:di
pnpm db:benchmark
```

Nhóm lệnh kiểm chứng database/build/test/demo ở trên đã chạy đạt ngày 20/10/2026. Khi kiểm chứng, cấu hình `DB_*` được truyền
cho riêng tiến trình để trỏ đúng container, không ghi đè `.env` cá nhân. Nếu `.env` của
bạn đang trỏ DB khác, đối chiếu `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`,
`DB_DATABASE` với Compose trước khi chạy seed. `pnpm start` hiện chỉ kiểm tra DB rồi
thoát thành công, chưa mở HTTP API.

Kết nối từ Node chạy trên Ubuntu dùng `127.0.0.1:5432`; Adminer chạy **trong container**
dùng server **postgres**, không dùng localhost. Truy cập [Adminer](http://localhost:8080):
PostgreSQL, server `postgres`, user `postgres`, password dev `postgres_dev_password`,
database `sentia_hub_db`. Chỉ các thông tin dev của Compose được ghi ở đây.

### Trạng thái đã kiểm chứng trên máy Ubuntu

- `sentia_postgres_dev`: PostgreSQL **16.15**, image `postgres:16-alpine`, trạng thái healthy.
- `sentia_adminer_dev`: image `adminer:latest`, chạy ở `localhost:8080`; đăng nhập và SQL thành công.
- Schema **9 bảng**, seed **10 users**, **8 categories**, **1.012 templates**; seed lần hai không thêm trùng.
- CLI tests **6/6**, build/typecheck và các demo TypeScript đạt.
- Benchmark/locking hai connection chạy trong DB lab riêng; không thêm 100.000 users hoặc
  bảng slots vào DB nghiệp vụ. Kết quả được ghi trong báo cáo.
- Container Portainer có sẵn được giữ nguyên. PostgreSQL/Adminer và volume dev được giữ
  chạy để tiếp tục sử dụng; không reset volume để lấy ảnh mới.

### Xử lý các lỗi dễ gặp sau khi chuyển từ Windows

| Hiện tượng | Kiểm tra / cách xử lý |
|------------|----------------------|
| CLI có nhưng `docker info` không có Server | Khởi động Docker Engine bằng systemctl hoặc mở Docker Desktop, kiểm tra context. |
| Không thấy container từng chạy | Xác nhận context và máy đang dùng; volume Windows/WSL không tự xuất hiện trên Ubuntu mới. |
| Port 5432 hoặc 8080 đã được dùng | Xem `docker ps` và `ss -ltnp`; xác định dịch vụ đang giữ cổng trước khi quyết định đổi cổng hoặc dừng nó. |
| Adminer báo connection refused | Server phải là service `postgres`; xem `docker compose -f docker/docker-compose.dev.yml logs --tail=50 postgres`. |
| SQL sửa rồi nhưng schema không tự cập nhật | Init scripts chỉ chạy trên data directory trống; dùng script/migration phù hợp, không xóa volume đang có dữ liệu để áp thay đổi. |
| Backend seed kết nối nhầm DB | Kiểm tra `.env`/DB_*; database dev Compose là `sentia_hub_db` tại localhost:5432. |
