# Hướng dẫn chạy - Transaction

## 1. Thứ tự chạy

`transaction.sql` thực hiện:

```text
1. tạo bảng accounts
2. reset dữ liệu
3. insert 2 tài khoản mẫu
4. kiểm tra số dư ban đầu
5. demo commit
6. kiểm tra số dư sau commit
7. demo rollback
8. kiểm tra số dư sau rollback
```

Nên chạy từng block để quan sát rõ.

## 2. Dữ liệu ban đầu

Sau:

```sql
select *
from accounts
order by id;
```

kết quả:

```text
Account A = 1000
Account B = 500
```

## 3. Demo commit

```sql
begin;

update accounts
set balance = balance - 100
where id = 1;

update accounts
set balance = balance + 100
where id = 2;

commit;
```

Sau commit:

```text
Account A = 900
Account B = 600
```

Phân tích:

```text
A giảm 100
B tăng 100
tổng tiền không đổi
```

Hai thao tác cùng được xác nhận.

## 4. Demo rollback

Bắt đầu transaction mới:

```sql
begin;

update accounts
set balance = balance - 50
where id = 1;
```

Nếu query trong cùng session lúc này, A đã giảm tạm thời.

Sau:

```sql
rollback;
```

query lại:

```sql
select *
from accounts
order by id;
```

A quay về số dư trước transaction rollback.

Nếu chạy tiếp ngay sau demo commit:

```text
Account A = 900
Account B = 600
```

## 5. Phân tích

`commit`:

```text
xác nhận thay đổi
```

`rollback`:

```text
hủy toàn bộ thay đổi chưa commit
```

Điều này quan trọng với chuyển tiền vì không được có trạng thái:

```text
đã trừ A
nhưng chưa cộng B
```

Nếu có lỗi giữa hai bước, transaction phải rollback.

## 6. Output

Cần giải thích được:

```text
- transaction là gì
- khi nào cần transaction
- begin / commit / rollback
- vì sao chuyển tiền phải gom nhiều update vào cùng transaction
- kết quả trước và sau commit
- kết quả trước và sau rollback
```
