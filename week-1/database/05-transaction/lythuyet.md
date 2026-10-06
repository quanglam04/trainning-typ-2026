# Transaction

## 1. Transaction là gì?

Transaction là một nhóm thao tác database được xem như **một đơn vị công việc**.

Các thao tác trong transaction nên:

```text
cùng thành công
hoặc
cùng thất bại
```

## 2. Khi nào cần Transaction?

Transaction cần khi một nghiệp vụ có nhiều thao tác phụ thuộc nhau.

Ví dụ:

- chuyển tiền;
- đặt hàng và trừ tồn kho;
- tạo đơn hàng và thanh toán;
- mượn sách và giảm số lượng sách còn lại.

## 3. begin

`begin` bắt đầu một transaction.

```sql
begin;
```

Các thay đổi sau đó chưa được xác nhận vĩnh viễn cho đến khi `commit`.

## 4. commit

`commit` xác nhận transaction.

```sql
commit;
```

Sau `commit`, các thay đổi được lưu.

## 5. rollback

`rollback` hủy các thay đổi chưa commit.

```sql
rollback;
```

Database quay lại trạng thái trước khi transaction bắt đầu.

## 6. Ví dụ chuyển tiền

Giả sử:

```text
Account A = 1000
Account B = 500
```

Chuyển 100 từ A sang B gồm:

```text
A = A - 100
B = B + 100
```

Hai thao tác phải nằm trong cùng một transaction.

Nếu chỉ trừ A thành công nhưng cộng B thất bại thì dữ liệu bị sai.

Nếu có lỗi, toàn bộ transaction phải rollback.

## 7. Transaction và Locking

Hai khái niệm liên quan nhưng không giống nhau.

- Transaction quản lý nhiều thao tác như một đơn vị.
- Locking kiểm soát xung đột khi nhiều transaction truy cập cùng dữ liệu.
