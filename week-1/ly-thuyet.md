# PHẦN 1: INDEX

## I. Index là gì?

Index (chỉ mục) trong Database là một cấu trúc dữ liệu đặc biệt được tạo dựa trên một hoặc nhiều cột của bảng, giúp Database tìm kiếm và truy xuất các bản ghi nhanh hơn, từ đó có thể giảm lượng dữ liệu cần kiểm tra.

Có thể hiểu đơn giản rằng Index giống như mục lục của một cuốn sách. Ví dụ, một cuốn sách có 500 trang. Nếu muốn tìm một chủ đề mà không có mục lục, chúng ta có thể phải lật từng trang để tìm. Ngược lại, nếu có mục lục, chúng ta có thể nhanh chóng xác định chủ đề nằm ở trang nào và đi trực tiếp đến đó.

Database cũng tương tự, Index cung cấp cho Database một cấu trúc được tổ chức để có thể nhanh chóng tìm kiếm các giá trị cần thiết thay vì phải kiểm tra lần lượt toàn bộ dữ liệu trong bảng.

## II. Index hoạt động như thế nào?

Khi một Index được tạo trên một cột trong Database, hệ quản trị cơ sở dữ liệu sẽ xây dựng một cấu trúc dữ liệu riêng dựa trên các giá trị của cột đó. Cấu trúc này cho phép Database nhanh chóng tìm kiếm và xác định các bản ghi phù hợp, từ đó có thể giảm lượng dữ liệu cần kiểm tra so với việc quét toàn bộ bảng.

Về cơ bản, có thể hình dung Index gồm hai thành phần chính:

Key: giá trị được lấy từ cột mà Index được tạo trên đó, dùng để tìm kiếm trong Index.

Pointer: thông tin tham chiếu giúp Database xác định dữ liệu tương ứng với Key.

## III. Cấu trúc của Index

Index có thể được xây dựng dựa trên nhiều loại cấu trúc dữ liệu khác nhau. Mỗi cấu trúc có cách tổ chức dữ liệu và đặc điểm tìm kiếm riêng và loại thường được nhắc đến khi tìm hiểu Index là B-Tree / B+Tree.

# PHẦN 2: INDEX

## I. B+ Tree là gì?

B+ Tree (B+ Tree) là một cấu trúc dữ liệu dạng cây tìm kiếm cân bằng, được sử dụng phổ biến trong các hệ quản trị cơ sở dữ liệu và hệ thống lưu trữ để tổ chức và truy xuất dữ liệu hiệu quả. Trong B+ Tree, các nút trung gian chủ yếu chứa các khóa dùng để định hướng quá trình tìm kiếm, trong khi dữ liệu hoặc con trỏ đến dữ liệu được lưu tại các nút lá. Các nút lá được liên kết với nhau theo thứ tự tăng dần của khóa. Đặc điểm này giúp B+ Tree hỗ trợ hiệu quả các thao tác tìm kiếm theo khoảng và duyệt tuần tự dữ liệu.

## II. Cấu trúc của B+ Tree?

Một B+ Tree gồm ba thành phần chính:

Nút gốc (Root Node): là nút ở vị trí cao nhất của cây, chứa các khóa và các con trỏ đến các nút con, giúp xác định nhánh cần đi xuống trong quá trình tìm kiếm.

Nút trung gian (Internal Node): là nơi chứa các khóa phân chia phạm vi giá trị và các con trỏ đến các nút con, được sử dụng để định hướng quá trình tìm kiếm, không phải nơi lưu trữ bản ghi dữ liệu cuối cùng.

Nút lá (Leaf Node): là nơi chứa các khóa của dữ liệu và thường chứa con trỏ đến bản ghi tương ứng trong cơ sở dữ liệu.

## III. Một số tính chất của B+ Tree

B+ Tree luôn duy trì trạng thái cân bằng và tất cả các nút lá đều nằm trên cùng một mức của cây. Dữ liệu hoặc con trỏ đến dữ liệu được lưu tại các nút lá trong khi các nút trung gian chủ yếu chứa khóa và con trỏ dùng để điều hướng quá trình tìm kiếm. Các nút lá được liên kết theo thứ tự của khóa. Các khóa trong B+ Tree được duy trì theo thứ tự.

Khác với cây nhị phân, một nút trong B+ Tree có thể chứa nhiều khóa và có nhiều nút con. Do các nút lá được liên kết tuần tự và các khóa được sắp xếp, B+ Tree hỗ trợ tốt các thao tác tìm kiếm theo một khoảng giá trị. Khi thực hiện thao tác chèn hoặc xóa, B+ Tree có cơ chế phân chia, gộp hoặc phân phối lại các nút để duy trì các điều kiện của cây.

# PHẦN 3: INDEX

## I. Clustered Index là gì?

Clustered Index là một loại Index dùng để tổ chức dữ liệu trong bảng theo thứ tự của một cột. Có thể hiểu đơn giản là nó vừa là Index để tìm kiếm, vừa quyết định cách dữ liệu trong bảng được tổ chức theo thứ tự của Index đó. 

Ví dụ, nếu sử dụng cột ID làm Clustered Index thì dữ liệu sẽ được tổ chức theo thứ tự của ID. Nhờ dữ liệu được tổ chức theo thứ tự này, Database có thể tìm kiếm dữ liệu dựa trên ID hiệu quả hơn. 

## II. Đặc điểm của Clustered Index

### 1. Dữ liệu được tổ chức theo Index

Các bản ghi trong bảng được tổ chức theo thứ tự của khóa Clustered Index. Vì vậy, Clustered Index không chỉ lưu thông tin để tìm kiếm mà còn liên quan trực tiếp đến cách dữ liệu của bảng được lưu trữ. 

### 2. Một bảng chỉ có một Clustered Index

Một bảng chỉ có thể có một Clustered Index, vì dữ liệu của bảng không thể đồng thời được tổ chức theo nhiều thứ tự khác nhau.
 
### 3. Có thể được tạo trên Primary Key

Trong nhiều hệ quản trị cơ sở dữ liệu, Primary Key thường được sử dụng làm Clustered Index mặc định nếu không có Clustered Index khác. Tuy nhiên, Primary Key và Clustered Index là hai khái niệm khác nhau. 

### 4. Có thể tạo trên một hoặc nhiều cột 

Clustered Index có thể được tạo dựa trên một cột hoặc nhiều cột. Khi có nhiều cột, thứ tự của các cột trong Index sẽ ảnh hưởng đến cách dữ liệu được tổ chức và khả năng sử dụng Index khi truy vấn. 

### 5. Hỗ trợ tốt cho truy vấn theo khoảng 

Do dữ liệu được tổ chức theo thứ tự của Clustered Index, nó đặc biệt hữu ích đối với các truy vấn tìm kiếm theo khoảng giá trị, chẳng hạn như điều kiện BETWEEN, >, <, >=, <= hoặc sắp xếp theo cột được đánh Clustered Index.

# PHẦN 4: INDEX

## I. Non-Clustered Index là gì?

Non-Clustered Index là một loại Index được tạo riêng bên ngoài dữ liệu của bảng, dùng để giúp Database tìm kiếm dữ liệu nhanh hơn. Có thể hiểu đơn giản là Non-Clustered Index giống như một bảng tra cứu, lưu giá trị của cột được đánh Index và thông tin giúp Database tìm đến bản ghi tương ứng trong bảng. 

Ví dụ, nếu tạo Non-Clustered Index trên cột Name, Index sẽ lưu các giá trị của Name theo thứ tự và thông tin để Database tìm đến dữ liệu tương ứng trong bảng.

## II. Đặc điểm của Non-Clustered Index

### 1. Không tổ chức trực tiếp dữ liệu trong bảng

Non-Clustered Index được lưu trữ riêng với dữ liệu của bảng. Nó chứa giá trị của cột được đánh Index và thông tin giúp Database xác định bản ghi tương ứng. 

### 2. Một bảng có thể có nhiều Non-Clustered Index 

Một bảng có thể có nhiều Non-Clustered Index trên các cột khác nhau, tùy vào nhu cầu truy vấn. 

### 3. Không giống Clustered Index

Khác với Clustered Index, Non-Clustered Index không tổ chức dữ liệu của bảng theo thứ tự của Index. Nó chỉ cung cấp một cấu trúc riêng để Database tìm đến dữ liệu cần thiết. 

### 4. Có thể được tạo trên một hoặc nhiều cột 

Non-Clustered Index có thể được tạo dựa trên một cột hoặc nhiều cột. 

### 5. Khi nào không nên dùng index

Ta sẽ dùng index khi mà kết quả trả về chọn lọc nó cao (id, email, username), tuyệt đối không đánh index trên cột kết quả trả về lớn (giới tính) vì trả về tỉ lệ sẽ 50% nam và 50% nữ (không có tính chọn lọc cao), lý tưởng đánh index trả về ít hơn 5-7%. 

Giả sử, ta có bảng nhân viên 2000 dòng, ta cần tìm tất cả nhân viên là nam.

Đánh index xong để chọc vào dữ liệu gốc (ta cần truy hết thông tin về nhân viên đó) thì ta gọi là đó Access Random tốn khoảng 0.7s (ví dụ), thì giả sử kết quả trả về tận 1000 kết quả thì thời gian là 700s, trong khi nếu mà ta duyệt tuần tự chỉ tốn 0.2s với 2000 dòng thì là 400s, rõ ràng ta sẽ chọn duyệt tuần tự vì thời gian tốt hơn.

# PHẦN 2: OOP

## I. Đóng gói

Đóng gói là việc che giấu dữ liệu bên trong đối tượng và chỉ cho phép truy cập thông qua những phương thức được cung cấp. Trong đó, các access modifier phổ biến bao gồm: private, public, protected. 

Getter và Setter là các phương thức thường được sử dụng để cung cấp cách truy cập hoặc thay đổi dữ liệu đã được đóng gói. Không phải mọi thuộc tính đều bắt buộc phải có cả Getter và Setter, việc cung cấp phương thức nào phụ thuộc vào yêu cầu của đối tượng và mức độ truy cập dữ liệu mong muốn.

## II. Kế thừa

Kế thừa là cơ chế cho phép một class mới (class con) kế thừa các thuộc tính và phương thức từ một class có sẵn (class cha). Khi đó, class con có thể sử dụng lại những thành phần được kế thừa và có thể bổ sung thêm các thuộc tính, phương thức riêng hoặc thay đổi cách hoạt động của một số phương thức phù hợp với yêu cầu.

Ngoài ra, class con còn có thể giữ lại tên và cấu trúc phương thức phù hợp với phương thức của class cha nhưng có thể thay đổi phần xử lý bên trong thông qua ghi đè phương thức (Method Overriding).

## III. Đa hình

Đa hình là khả năng cho phép cùng một phương thức hoặc cùng một cách sử dụng nhưng có thể thực hiện những hành vi khác nhau tùy thuộc vào đối tượng hoặc ngữ cảnh cụ thể.

Đa hình Runtime là lớp con viết lại phương thức của lớp cha sao cho phù hợp và quyết định hàm nào sẽ được gọi khi chương trình đang chạy.

Đa hình Complie là cùng một tên hàm nhưng khác số lượng, kiểu tham số và quyết định hàm nào được gọi diễn ra ngay khi biên dịch.

## IV. Trừu tượng
Định nghĩa đối tượng làm được gì mà không cần quan tâm nó thực hiện như thế nào.

Sử dụng Abstract cho các đối tượng cùng loại, Interface cho các hành vi chung.

# PHẦN 3: Transaction
Transaction (giao dịch) là một nhóm các thao tác trên Database được thực hiện như một đơn vị duy nhất. Hoặc có thể hiểu đơn giản là tất cả thao tác thành công, hoặc nếu có lỗi thì tất cả được hoàn tác.

Ví dụ:

Một người chuyển 100.000đ cho người khác thì trừ 100.000đ ở tài khoản A và cộng 100.000đ vào tài khoản B.

Hai thao tác này phải thuộc cùng một Transaction.

- Nếu cả 2 thành công: COMMIT → lưu dữ liệu.
- Nếu bước 2 lỗi: ROLLBACK → hoàn tác bước 1.

Nếu không có Transaction thì tiền vừa rời A chưa kịp đến B mà Server lỗi thì A bị trừ tiền và B thì chưa nhận được tiền.
