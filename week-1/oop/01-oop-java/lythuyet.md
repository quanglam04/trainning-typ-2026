# OOP trong Java

## 1. Tổng quan

OOP (Object-Oriented Programming - Lập trình hướng đối tượng) là mô hình lập trình tổ chức chương trình xoay quanh **đối tượng**. Mỗi đối tượng kết hợp:

- **Dữ liệu**: thuộc tính (fields/attributes).
- **Hành vi**: phương thức (methods/behaviors).

Trong Java, hai khái niệm nền tảng là **Class** và **Object**:

- **Class** là khuôn mẫu/bản thiết kế mô tả dữ liệu và hành vi chung.
- **Object** là một thể hiện (instance) cụ thể được tạo từ Class.

Ví dụ: `NhanVien` là Class, còn một nhân viên cụ thể có mã `NV01` là Object.

## 2. Bốn tính chất của OOP

### 2.1. Encapsulation - Tính đóng gói

Đóng gói là việc che giấu dữ liệu và chi tiết xử lý bên trong đối tượng, chỉ cung cấp ra ngoài những thao tác cần thiết.

Trong Java, cách thường dùng:

- Thuộc tính để `private`.
- Truy cập hoặc thay đổi dữ liệu thông qua constructor, getter/setter hoặc các phương thức nghiệp vụ.
- Có thể đặt kiểm tra dữ liệu trước khi thay đổi trạng thái đối tượng.

Ví dụ:

```java
class Student {
    private double score;

    public void setScore(double score) {
        if (score >= 0 && score <= 10) {
            this.score = score;
        }
    }

    public double getScore() {
        return score;
    }
}
```

Ý nghĩa chính: bảo vệ dữ liệu, giảm truy cập tùy tiện và giúp đối tượng tự kiểm soát trạng thái của mình.

### 2.2. Inheritance - Tính kế thừa

Kế thừa cho phép một lớp con tái sử dụng và mở rộng thuộc tính/phương thức của lớp cha.

Trong Java dùng từ khóa `extends`.

```java
class Employee {
    void work() {
        System.out.println("Đang làm việc");
    }
}

class Developer extends Employee {
    void code() {
        System.out.println("Đang viết code");
    }
}
```

Quan hệ kế thừa thường biểu diễn quan hệ **IS-A**: `Developer` là một `Employee`.

Một số điểm cần nhớ:

- Java hỗ trợ đơn kế thừa đối với Class.
- Constructor của lớp cha không được kế thừa, nhưng có thể được gọi bằng `super(...)`.
- Thành phần `private` của lớp cha không được lớp con truy cập trực tiếp.
- Lớp con có thể ghi đè (override) phương thức của lớp cha.

### 2.3. Polymorphism - Tính đa hình

Đa hình cho phép cùng một tên hành động nhưng cách thực hiện có thể khác nhau.

Trong Java có hai dạng chính:

#### Compile-time Polymorphism

Thực hiện bằng **Method Overloading**: cùng tên phương thức nhưng khác danh sách tham số.

```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }
}
```

Phương thức cần gọi được xác định khi biên dịch.

#### Runtime Polymorphism

Thực hiện bằng **Method Overriding** kết hợp kế thừa và tham chiếu lớp cha trỏ tới đối tượng lớp con.

```java
Employee e = new FullTimeEmployee(...);
e.calculateSalary();
```

JVM quyết định phương thức thực tế cần chạy dựa trên đối tượng thật tại runtime.

### 2.4. Abstraction - Tính trừu tượng

Trừu tượng tập trung vào đối tượng **làm gì** thay vì chi tiết **làm như thế nào**.

Trong Java, hai công cụ chính là:

- `abstract class`
- `interface`

Mục tiêu là che giấu chi tiết triển khai, tạo bộ khung/hợp đồng và giúp hệ thống dễ mở rộng.

## 3. Class, Abstract Class và Interface

### 3.1. Class

Class thông thường mô tả một kiểu đối tượng đầy đủ và có thể khởi tạo bằng `new`.

Nên dùng Class khi:

- Đối tượng có thể tồn tại độc lập và có đầy đủ trạng thái/hành vi.
- Không cần ép lớp con phải cài đặt một hành vi chưa xác định.
- Đây là một lớp cụ thể (concrete class).

### 3.2. Abstract Class

Abstract Class là lớp không thể khởi tạo trực tiếp. Nó có thể chứa:

- Thuộc tính instance.
- Constructor.
- Phương thức thường có phần thân.
- Phương thức `abstract` không có phần thân.

```java
abstract class Employee {
    protected String name;

    public Employee(String name) {
        this.name = name;
    }

    public abstract double calculateSalary();
}
```

Nên dùng Abstract Class khi:

- Các lớp con có quan hệ bản chất rõ ràng kiểu **IS-A**.
- Muốn chia sẻ trạng thái và code chung giữa các lớp con.
- Muốn vừa có phần cài đặt chung, vừa bắt buộc lớp con tự cài đặt một số hành vi.

### 3.3. Interface

Interface đóng vai trò như một **hợp đồng hành vi**. Lớp thực thi dùng từ khóa `implements`.

```java
interface Payable {
    double calculateSalary();
}
```

Một lớp có thể `implements` nhiều Interface.

Nên dùng Interface khi:

- Muốn mô tả một khả năng/hành vi mà nhiều lớp có thể thực hiện.
- Muốn tạo chuẩn giao tiếp mà không phụ thuộc vào cách cài đặt cụ thể.
- Muốn giảm phụ thuộc và hỗ trợ thiết kế linh hoạt.
- Cần một lớp thực thi nhiều hợp đồng hành vi.

## 4. So sánh nhanh

| Tiêu chí | Class | Abstract Class | Interface |
|---|---|---|---|
| Khởi tạo trực tiếp | Có | Không | Không |
| Trạng thái instance | Có | Có | Không dùng để lưu trạng thái instance |
| Constructor | Có | Có | Không |
| Phương thức có phần thân | Có | Có | Có thể có `default`/`static` từ Java 8 |
| Phương thức trừu tượng | Không bắt buộc | Có thể có | Là vai trò chính của hợp đồng |
| Quan hệ | Đối tượng cụ thể | IS-A, lớp cha khung | CAN-DO/hợp đồng hành vi |
| Từ khóa sử dụng | `new` | `extends` | `implements` |
| Số lượng kế thừa/thực thi | Một class cha trực tiếp | Một class cha trực tiếp | Có thể thực thi nhiều interface |

## 5. Chọn loại nào?

Quy tắc ngắn gọn:

```text
Cần tạo đối tượng cụ thể
        ↓
      Class

Có quan hệ IS-A + cần dùng chung trạng thái/code
        ↓
  Abstract Class

Cần một hợp đồng/khả năng + muốn giảm phụ thuộc
        ↓
    Interface
```

Trong thực tế, Abstract Class và Interface có thể phối hợp. Abstract Class cung cấp logic dùng chung, còn Interface định nghĩa hợp đồng mà các thành phần khác làm việc dựa vào.

## 6. Liên hệ với file code

File `code/OopDemo.java` minh họa đồng thời:

- Encapsulation: các thuộc tính được để `private`.
- Inheritance: `FullTimeEmployee` và `PartTimeEmployee` kế thừa `Employee`.
- Polymorphism: mảng `Employee[]` chứa nhiều loại nhân viên và gọi cùng `calculateSalary()`.
- Abstraction: `Employee` là abstract class, `Payable` là interface.
- Overloading: lớp `Calculator` có hai phương thức `add` khác tham số.

## 7. Tài liệu tham khảo đi kèm

Các PDF chọn lọc nằm trong thư mục `references/`:

- `01-oop-overview.pdf`
- `02-four-oop-pillars.pdf`
- `03-inheritance.pdf`
- `04-polymorphism.pdf`
- `05-abstraction.pdf`
- `06-interface.pdf`
- `07-abstract-class-vs-interface.pdf`
