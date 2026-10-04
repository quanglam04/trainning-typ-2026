# Database
## CSDL là gì
CSDL là kho lưu trữ thông tin số hoá, giúp tổ chức dữ liệu có cấu trúc nhằm phục vụ yêu cầu khai thác của nhiều người dùng cùng lúc.
CSDL có CSDL quan hệ và không quan hệ hay SQL và NoSQL.
## SQL là gì
SQL viết tắt của Structured Query Language.
SQL dùng để truy vấn, thêm mới, cập nhật, quản lý và xoá dữ liệu.
## SQL Cơ Bản 
### DDL (Data Definition Language) dùng để định nghĩa, tạo mới, sửa đổi và xóa cấu trúc hoặc đối tượng của cơ sở dữ liệu như bảng, chỉ mục, và khung nhìn.
DDL có các lệnh cơ bản như *Create*, *Alter*, *Drop*, cụ thể như sau:
- CREATE TABLE: Tạo bảng mới.
- ALTER TABLE: Chỉnh sửa bảng.
- DROP TABLE: Xoá bảng ra khỏi CSDL.
### DML (Data Manipulation Language) dùng để thao tác dữ liệu
DML có các lệnh cơ bản như sau:
- SELECT: Truy vấn hoặc tìm kiếm từ bảng.
- INSERT: Thêm mới một bản ghi vào bảng.
- UPDATE: Cập nhật hoặc sửa đổi dữ liệu đã có.
- DELETE: Xoá dữ liệu khỏi bảng.
### Query dùng để giao tiếp, truy xuất, phân tích hoặc thao tác dữ liệu trong một hệ quản trị cơ sở dữ liệu quan hệ.
Các lệnh Query cơ bản trong SQL như sau:
- WHERE: Lọc các dòng thoả mãn điều kiện cơ bản.
- JOIN
	- INNER JOIN: Lấy phần trùng nhau của cả hai bảng.
	- LEFT JOIN: Lấy tất cả bảng bên trái và phần trùng của bảng bên phải.
	- RIGHT JOIN: :Lấy tất cả bảng bên phải và phần trùng của bảng bên phải.
- GROUP BY: Gom nhóm các dữ liệu
- HAVING: Lọc các dòng thoả mãn điều kiện sau ghi gom nhóm:
- ORDER BY: Sắp xếp thứ tự hiển thị.
### Aggregate functions (Hàm tổng hợp) là hàm toán học thực hiện tính toán trên nhiều giá trị hoặc nhiều hàng dữ liệu để trả về một
Các hàm tổng hợp phổ biến:
- COUNT: Đếm số lượng các hàng khác rỗng.
- SUM: Tính tổng giá trị trong cột.
- AVG: Tính giá trị trung bình trong cột. 
- MIN: Tìm giá trị nhỏ nhất trong cột.
- MAX: Tìm giá trị lớn nhất trong cột.
## Index
chạy theo từng page 8->16kb
một index page gồm 2 cột 1 giá trị của cột đánh theo index, một cột trỏ tới bảng ghi trong bảng
### Index là gì
MySQL thường tìm kiếm thuần tự trong cột nên nó mất nhiều thời gian, Index dùng để tìm giá trị trong một cột cụ thể nhanh hơn. Nó dùng cấu trúc dữ liệu B-Tree, việc truy vấn hoặc tìm sẽ nhanh hơn, nhưng cập nhật lại tốn nhiều thời gian hơn.
### Khi nào nên dùng
Khi ta cần tăng tốc độ truy vấn trên một cơ sở dữ liệu lớn. Khi nối bảng, lọc điều kiện trong tìm kiếm nhiều. Không nên dùng khi cập nhật quá nhiều vì như đã
nói ở trên *Việc truy vấn hoặc tìm kiếm sẽ nhanh hơn nhưng cập nhật lại tốn nhiều thời gian hơn*.
## Phân Trang 
Nếu dữ liệu trong bảng có 1000 dòng mà thường người dùng chỉ muốn xem 3-4 dòng thôi, mình k nên đưa người dùng 1000 dòng vì nó quá nhiều và cũng rối lúc đó ta nên phân trang để dữ liệu gửi cho user nó gọn gàng và dễ nhìn hơn.
### Offset-based pagination
- Để phân trang ta cần đầu tiên ta cần những dữ liệu cần thiết:
	- Tổng số phần tử là bao nhiêu?
	- Số trang muốn hiển thị một dòng là bao nhiêu?
	- Bạn muốn xem trang mấy?
- Rồi ta đến công thức để tính offset và tổng số trang
	- Tổng số trang được tính như sau: totalpage = ceil(total_elements / pagesize)
	- Công thức tính offset được tính như sau
- Cuối cùng ta dùng Select để query
```
select * 
from ...
limit {offset}, {pagesize}
```
## ### Transaction
Là một nhóm gồm một hoặc nhiều câu lệnh chạy trong một khối duy nhất, tuân theo nguyên tắc tất cả thành công hoặc quay trở lại vị trí ban đầu.
Ta nên gom nhiều thao tác vào một nhóm khi chúng có liên quan chặt chẽ với nhau hoặc là cùng thành công hoặc là cùng mất và quay lại vị trí ban đầu.
ROLLBACK khi ta muốn CSDL quay lại trạng thái trước khi thực hiện Transaction
COMMIT khi ta thấy nhóm lệnh trong Transaction đã hợp lý và muốn xác nhận lưu vĩnh viễn chúng, một khi đã COMMIT thì không thể quay về điểm trước đó nữa.

## OOP Trong Java
### Khái Niệm
Trong OOP, ta có các định nghĩa như Class và Object.
- Class như là một cái khuôn, nó sẽ gom tất cả những thứ chung nhất vào trong một cái hộp.
	- Object như là một vật cụ thể sờ nắn được, chúng là những thằng được đúc ra từ cái khuôn đó. Từ một cái khuôn ra nhiều Object khác nhau.
VD Dễ hiểu: Ví dụ như ta muốn chế tạo chiếc oto VinFast, đầu tiên ta cần những thứ chung nhất của oto VinFast như 4 cái lốp, hãng xe, loại xe thì đó là Class, những thứ chung nhất của oto, xong oto có thể có nhiều mã như VF3, VF5 là những thằng con, nó gọi là Object, là những sản phẩm thực tế từ Class.
### Access modifiers (mức độ bảo vệ)
Có 3 mức độ bảo vệ trong OOP
- Public:  ai cũng xem được và chỉnh sửa được.
- Private: chỉ mình class đó xem được.
- Protected: chỉ những class con mới truy cập được.
### Các tính chất
- **Encapsulation**: Tính đóng gói
	- Cái gì quan trọng thì cất nó vào trong.
	- Giấu dữ liệu để bảo vệ để dữ liệu an toàn.
	- Muốn thay đổi thì phải xin phép.
	- Nhờ đó ta có thể kiểm tra xem có ai chỉnh sửa hay truy cập trái phép vào đó để thay đổi hay không.
- **Abstraction**: Tính trừu tượng
	- Nghệ thuật giấu sự phức tạp, giấu bằng phức tạp.
	- Giấu quy trình và logic để đơn giản hoá
	- Chỉ lộ ra thứ người dùng cần, tránh ảnh hưởng khi cập nhật hàm. Cam kết rằng dùng hàm này thì hàm này sẽ hoạt động mà người dùng không cần quan tâm làm bằng cách nào.
	- Nhờ có cam kết ở trên, ta có thể linh hoạt tối ưu hoặc thay đổi logic bên trong lúc nào cũng được mà không sợ sập hệ thống
- **Inheritance**: Tính kế thừa
	- Đừng viết lại những thứ đã có, kế thừa và mở rộng thêm
	- Tạo ra một class con rồi cho các class con kế thừa lại rồi mở rộng tính năng ra.
	- **Dùng Composition thay vì kế thừa sâu**
	- Composition là quan hệ thành phần, ta tạo nhiều class con là các thành phần xong ta khởi tạo chung vào một class tổng. -> *nó cần thêm cái gì thì tạo.*
- **Polymorphism**: Tính đa hình
	- Một lệnh nhiều hành vi. (kiểu như ghi đè)
	- Chỉ cần nói một lệnh chung mỗi người tự làm việc của nó.
	- Giúp code dễ đọc dễ sửa và dễ mở rộng.
### Class, Abstract Class, Interface
- Interface: Chỉ yêu cầu, không cho gì, ta phải cam kết làm được (cam kết bao nhiêu class). Ta có thể hiểu như ký hợp đồng, cam kết có method đó.
- Class: là một khuôn mẫu dùng để xác định thuộc tính và hành vi chung của các nhóm đối tượng cụ thể, dùng để khởi tạo trực tiếp các đối tượng.
- Abstract Class (Lớp Trừu Tượng) chỉ được dùng thông qua lớp con của lớp trừu tượng, Class bình thường phải có hàm rõ ràng (phương thức triển khai), Abstract Class không có trình triển khai hay body (phương thức trừu tượng), Class nào mở rộng từ phương thức trừu tượng thì phải ghi đè phương thức trong lớp cha (@override).
-> Ta nên sử dụng class khi muốn tạo ra một đối tượng hoàn chỉnh, biết rõ cụ thể mình đang làm gì 100%, ta dùng abstract class khi các đối tượng con có quan hệ họ hàng với nhau và muốn chia sẻ code chung (IS-A), interface ta dùng khi ta muốn cam kết, định nghĩa một tập hành vi (can-do) cho các đối tượng không cùng huyết thống. 
## ###  Dependency Injection (DI) & Inversion of Control (IoC)
DI không tự khởi tạo các đối tượng mà nó cần dùng mà nhận các đối tượng truyền từ bên ngoài truyền vào.
Nếu A phụ thuộc vào B thì B là mộit dependency của A
thêm các tham số khác vào constructor cũng không ảnh hưởng 
chúng ta tách biệt được việc sử dụng dependency và việc chúng ta xây dựng nó
chúng ta ko cần quan tâm dependency nó ntn chúng ta chỉ cần biết sd ntn thôi
tăng khả năng bảo trì, làm code gọn nhẹ hơn.
