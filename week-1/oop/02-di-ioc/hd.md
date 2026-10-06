# Hướng dẫn chạy - DI & IoC

## 1. Mục tiêu

Demo bằng Java thuần để thấy rõ:

- Dependency là gì.
- Constructor Injection hoạt động thế nào.
- Đổi implementation mà không sửa `NotificationService`.
- Dùng fake dependency để test.
- Liên hệ với cách Spring IoC Container quản lý bean.

## 2. Chạy demo Java thuần

Di chuyển vào thư mục:

```bash
cd code
```

Biên dịch:

```bash
javac DiIocDemo.java
```

Chạy:

```bash
java DiIocDemo
```

## 3. Kết quả mong đợi

```text
Email: Don hang da duoc xac nhan
SMS: Don hang da duoc xac nhan
Fake captured: Kiem thu DI
Test result: PASS
```

## 4. Phân tích luồng chạy

### Lần 1 - Email

```text
Main
 │
 ├── new EmailSender()
 │
 └── new NotificationService(emailSender)
                 │
                 ▼
            notifyUser()
                 │
                 ▼
          EmailSender.send()
```

### Lần 2 - SMS

Chỉ thay dependency được inject:

```text
new EmailSender()
        ↓ thay bằng
new SmsSender()
```

`NotificationService` giữ nguyên.

### Lần 3 - Test bằng Fake

```text
FakeMessageSender
        │
        ▼
NotificationService
        │
        ▼
notifyUser("Kiem thu DI")
        │
        ▼
Fake chỉ lưu message, không gửi thật
```

Nếu message lưu được đúng nội dung, demo in `Test result: PASS`.

## 5. Liên hệ với Spring

Trong demo Java thuần:

```text
Main = nơi tạo object và nối dependency
```

Trong Spring:

```text
ApplicationContext = container tạo, cấu hình, quản lý bean và inject dependency
```

Vì vậy file Java thuần giúp hiểu bản chất DI trước khi dùng Spring: framework tự động hóa việc mà `main` đang làm thủ công.
