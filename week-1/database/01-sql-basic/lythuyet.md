# SQL cơ bản

## 1. Bài toán

Cơ sở dữ liệu được xây dựng cho bài toán **quản lý thư viện**.

Các bảng chính:

- `authors`: lưu tác giả.
- `books`: lưu sách.
- `members`: lưu bạn đọc.
- `loans`: lưu phiếu mượn.
- `loan_items`: lưu các sách thuộc từng phiếu mượn.

Quan hệ chính:

```text
authors 1 --- n books

members 1 --- n loans
loans   1 --- n loan_items
books   1 --- n loan_items
```

## 2. DDL

DDL (Data Definition Language) là nhóm lệnh dùng để định nghĩa hoặc thay đổi cấu trúc cơ sở dữ liệu.

Các lệnh trong bài:

- `create table`: tạo bảng.
- `alter table`: thay đổi cấu trúc bảng.
- `drop table`: xóa bảng.

Một số ràng buộc sử dụng:

- `primary key`: định danh duy nhất cho mỗi bản ghi.
- `foreign key`: tạo quan hệ giữa các bảng.
- `unique`: không cho phép giá trị bị trùng.
- `not null`: bắt buộc có dữ liệu.
- `check`: kiểm tra dữ liệu theo điều kiện.

## 3. DML

DML (Data Manipulation Language) là nhóm lệnh thao tác với dữ liệu.

- `select`: truy vấn dữ liệu.
- `insert`: thêm bản ghi.
- `update`: cập nhật bản ghi.
- `delete`: xóa bản ghi.

## 4. where

`where` dùng để lọc những bản ghi thỏa mãn điều kiện.

```sql
select id, title, category
from books
where category = 'Technology';
```

## 5. join

`join` dùng để kết hợp dữ liệu từ nhiều bảng dựa trên một điều kiện liên kết, thường là quan hệ giữa khóa ngoại và khóa chính.

### inner join

Chỉ trả về các bản ghi có dữ liệu khớp ở cả hai bảng.

### left join

Trả về toàn bộ bản ghi của bảng bên trái. Nếu không có dữ liệu khớp ở bảng phải thì các cột của bảng phải nhận `null`.

### right join

Trả về toàn bộ bản ghi của bảng bên phải. Nếu không có dữ liệu khớp ở bảng trái thì các cột của bảng trái nhận `null`.

## 6. group by và having

`group by` gom các bản ghi có cùng giá trị thành từng nhóm.

`where` lọc dữ liệu thô trước khi nhóm.

`having` lọc kết quả sau khi dữ liệu đã được nhóm và tính toán tổng hợp.

```sql
select category, count(*) as total_books
from books
group by category
having count(*) >= 2;
```

## 7. order by

`order by` dùng để sắp xếp kết quả.

- `asc`: tăng dần.
- `desc`: giảm dần.

Nếu không có `order by`, database không đảm bảo thứ tự bản ghi trả về.

## 8. Aggregate Functions

Các hàm tổng hợp:

- `count()`: đếm.
- `sum()`: tính tổng.
- `avg()`: tính trung bình.
- `min()`: giá trị nhỏ nhất.
- `max()`: giá trị lớn nhất.

Lưu ý:

```text
count(*)      -> đếm tất cả bản ghi
count(column) -> bỏ qua các giá trị null của column
```
