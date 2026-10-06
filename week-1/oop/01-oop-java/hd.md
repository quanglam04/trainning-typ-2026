# Hướng dẫn chạy - OOP Java

## 1. Mục tiêu

Chạy một chương trình Java nhỏ để quan sát trực tiếp bốn tính chất OOP và cách kết hợp Class, Abstract Class, Interface.

## 2. Yêu cầu

Máy đã cài JDK và kiểm tra được:

```bash
java -version
javac -version
```

## 3. Chạy chương trình

Di chuyển vào thư mục:

```bash
cd code
```

Biên dịch:

```bash
javac OopDemo.java
```

Chạy:

```bash
java OopDemo
```

## 4. Kết quả mong đợi

Kết quả có dạng:

```text
Nguyen Van A - luong: 1.5E7
Tran Thi B - luong: 5000000.0
Tong luong: 2.0E7
Cong int: 5
Cong double: 6.0
```

Giá trị chính cần quan sát:

- Hai đối tượng lớp con được đặt trong cùng mảng `Employee[]`.
- Cùng lệnh `employee.calculateSalary()` nhưng kết quả được tính theo lớp thực tế.
- Dữ liệu của từng lớp được đóng gói bằng thuộc tính `private`.
- Lớp `Employee` không thể `new` trực tiếp vì là `abstract`.
- `Employee` thực thi hợp đồng `Payable`.
- `Calculator.add(...)` minh họa Overloading.

## 5. Luồng chương trình

```text
Payable (Interface)
        ▲
        │ implements
Employee (Abstract Class)
        ▲
        │ extends
   ┌────┴─────────────┐
   │                  │
FullTimeEmployee  PartTimeEmployee
   │                  │
   └───────┬──────────┘
           │
           ▼
     Employee[] list
           │
           ▼
calculateSalary() được quyết định theo object thực tế
```

## 6. Ý nghĩa bài demo

Bài demo cho thấy OOP không phải bốn khái niệm tách rời. Trong một thiết kế nhỏ:

- Encapsulation bảo vệ trạng thái.
- Inheritance tái sử dụng cấu trúc chung.
- Abstraction tạo khung và hợp đồng.
- Polymorphism giúp xử lý nhiều loại đối tượng bằng một giao diện chung.
