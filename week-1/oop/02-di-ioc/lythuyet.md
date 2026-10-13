# Dependency Injection (DI) và Inversion of Control (IoC)

## 1. Dependency là gì?

Một class có **dependency** khi nó cần một class/object khác để hoàn thành công việc.

Ví dụ `NotificationService` cần một đối tượng có khả năng gửi thông báo.

Nếu viết trực tiếp:

```java
class NotificationService {
    private EmailSender sender = new EmailSender();
}
```

thì `NotificationService` bị gắn chặt với `EmailSender`. Muốn đổi sang SMS phải sửa code bên trong class.

## 2. IoC - Inversion of Control

IoC (Đảo ngược điều khiển) là nguyên tắc chuyển trách nhiệm **tạo và quản lý dependency** ra khỏi class đang sử dụng dependency đó.

### Cách truyền thống

```text
NotificationService
       │
       └── tự new EmailSender
```

Class vừa xử lý nghiệp vụ vừa quyết định cách tạo dependency.

### Theo IoC

```text
Bên ngoài / Container
       │
       ├── tạo EmailSender
       │
       └── đưa vào NotificationService
```

`NotificationService` chỉ sử dụng dependency được cấp, không cần biết dependency được tạo như thế nào.

## 3. DI - Dependency Injection

DI (Tiêm phụ thuộc) là một cách hiện thực IoC: dependency được **đưa từ bên ngoài vào object** thay vì object tự `new` dependency.

Ví dụ Constructor Injection:

```java
class NotificationService {
    private final MessageSender sender;

    public NotificationService(MessageSender sender) {
        this.sender = sender;
    }
}
```

Ở đây `NotificationService` phụ thuộc vào abstraction `MessageSender`, không phụ thuộc trực tiếp vào `EmailSender` hay `SmsSender`.

## 4. Ví dụ Java thuần

Thiết kế:

```text
                MessageSender
                     ▲
            ┌────────┴────────┐
            │                 │
      EmailSender         SmsSender
            ▲                 ▲
            └────────┬────────┘
                     │ được inject
                     ▼
             NotificationService
```

Khởi tạo bằng Java thuần:

```java
MessageSender sender = new EmailSender();
NotificationService service = new NotificationService(sender);
```

Không cần framework. Code ở `code/DiIocDemo.java` dùng chính cách này.

## 5. Vì sao DI giúp dễ thay đổi?

Nếu class tự tạo dependency:

```java
private EmailSender sender = new EmailSender();
```

thì class bị phụ thuộc vào một implementation cụ thể.

Với DI:

```java
MessageSender sender = new SmsSender();
NotificationService service = new NotificationService(sender);
```

Muốn đổi cách gửi chỉ thay object được truyền vào. `NotificationService` không phải sửa.

Luồng thay đổi:

```text
EmailSender ──┐
              ├──> NotificationService không đổi
SmsSender   ──┘
```

Đây là ý nghĩa của **Loose Coupling**: giảm phụ thuộc chặt giữa các thành phần.

## 6. Vì sao DI giúp dễ test?

Khi dependency được truyền từ ngoài, test có thể đưa một object giả thay cho dependency thật.

```java
FakeMessageSender fake = new FakeMessageSender();
NotificationService service = new NotificationService(fake);
service.notifyUser("test");
```

Test không cần gửi email/SMS thật. Nó chỉ kiểm tra `NotificationService` có gọi dependency đúng hay không.

```text
Production                    Test
---------                     ----
EmailSender                   FakeMessageSender
    │                               │
    └──> NotificationService <──────┘
```

Do đó DI hỗ trợ:

- Dễ thay thế implementation.
- Dễ unit test bằng fake/mock object.
- Code mô-đun hơn.
- Dễ mở rộng và bảo trì.

## 7. Phân biệt IoC và DI

| Khái niệm | Ý nghĩa |
|---|---|
| IoC | Nguyên tắc đảo trách nhiệm tạo/quản lý dependency ra bên ngoài object |
| DI | Cách cụ thể để thực hiện IoC bằng việc truyền dependency từ bên ngoài vào |

Có thể nhớ ngắn gọn:

```text
IoC = nguyên tắc
DI  = cách triển khai nguyên tắc đó
```

## 8. Các hình thức DI trong Spring

Theo tài liệu, Spring hỗ trợ ba cách chính:

1. **Constructor Injection**: dependency được cung cấp qua constructor.
2. **Setter Injection**: dependency được cung cấp qua setter.
3. **Field Injection**: dependency được tiêm trực tiếp vào field, thường qua `@Autowired`; cách này ít được khuyến khích hơn vì khó kiểm thử và kiểm soát.

## 9. DI/IoC trong Spring hoạt động như thế nào?

Trong Spring:

- Spring IoC Container, thường là `ApplicationContext`, chịu trách nhiệm tạo, cấu hình và quản lý vòng đời object.
- Các object do Spring quản lý được gọi là **bean**.
- Object không cần tự tạo dependency.
- Spring sẽ cung cấp/inject dependency cho object.

Luồng khái quát:

```text
Thông tin cấu hình
       │
       ▼
Spring IoC Container (ApplicationContext)
       │
       ├── tạo bean A
       ├── tạo bean B
       └── inject bean B vào bean A
                    │
                    ▼
                 Ứng dụng
```

Spring Boot được xây dựng trên Spring Framework và hỗ trợ tự động cấu hình, giúp giảm lượng cấu hình cần viết khi xây dựng ứng dụng.

## 10. Liên hệ với Java thuần

Điểm cốt lõi không thay đổi:

```text
Java thuần:
Main tạo dependency → truyền vào Service

Spring:
Spring Container tạo dependency → inject vào bean
```

Khác biệt chính là ở Java thuần, lập trình viên tự đóng vai trò quản lý object. Trong Spring, container đảm nhận công việc này.

## 11. Tài liệu tham khảo đi kèm

Các PDF chọn lọc nằm trong `references/`:

- `01-spring-framework-ioc-di.pdf`
- `02-spring-boot-overview.pdf`
- `03-spring-initializr-project-structure.pdf`
