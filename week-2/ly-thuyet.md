**PHẦN 1**

**I. Repository là gì?**

Trong Spring Boot, Repository là thành phần thuộc tầng truy cập dữ liệu (Data Access Layer), có nhiệm vụ trung gian giữa tầng xử lý nghiệp vụ và cơ sở dữ liệu. Repository cung cấp các phương thức để thực hiện những thao tác như thêm mới, truy vấn, cập nhật và xóa dữ liệu mà không cần phải tự viết toàn bộ câu lệnh SQL cho các thao tác thông thường.

Spring Data JPA là một dự án thuộc hệ sinh thái Spring, hỗ trợ đơn giản hóa quá trình xây dựng Repository bằng cách cung cấp các interface và cơ chế tự động triển khai những phương thức truy cập dữ liệu. Khi sử dụng Spring Data JPA, lập trình viên có thể kế thừa các interface có sẵn như JpaRepository để sử dụng nhiều chức năng truy vấn và quản lý dữ liệu.

**II. Vai trò của Repository**

Repository đóng vai trò quan trọng trong việc tách biệt hoạt động truy cập dữ liệu khỏi logic nghiệp vụ của ứng dụng. Thành phần này cung cấp các phương thức thao tác với dữ liệu, giúp tầng Service không cần trực tiếp quản lý các câu lệnh SQL hoặc chi tiết triển khai việc truy vấn cơ sở dữ liệu.

Ngoài ra, Repository hỗ trợ xây dựng các truy vấn tùy chỉnh theo yêu cầu của hệ thống thông qua quy tắc đặt tên phương thức, annotation @Query hoặc các cơ chế truy vấn khác của Spring Data JPA. Việc tổ chức truy cập dữ liệu thông qua Repository giúp mã nguồn rõ ràng, dễ kiểm thử, bảo trì và mở rộng.

**III. JpaRepository trong Spring Data JPA**

JpaRepository là một interface được cung cấp bởi Spring Data JPA, hỗ trợ các thao tác phổ biến trên Entity và cơ sở dữ liệu. Khi một Repository kế thừa JpaRepository, lập trình viên có thể sử dụng nhiều phương thức được định nghĩa sẵn mà không cần tự triển khai từng phương thức.

Một số phương thức thường được sử dụng bao gồm:

* ***save()***: Lưu một Entity mới hoặc cập nhật một Entity đang được quản lý theo cơ chế của JPA.
* ***findById()***: Tìm kiếm một Entity dựa trên khóa chính.
* ***findAll()***: Truy xuất danh sách các Entity.
* ***existsById()***: Kiểm tra sự tồn tại của một Entity dựa trên khóa chính.
* ***deleteById()***: Xóa Entity dựa trên khóa chính.
* ***count()***: Đếm số lượng bản ghi tương ứng với Entity.

Các phương thức này giúp giảm lượng mã cần viết và tạo điều kiện để tập trung vào những truy vấn đặc thù của ứng dụng.

Bên cạnh các phương thức có sẵn, Spring Data JPA còn hỗ trợ xây dựng truy vấn tùy chỉnh nhằm đáp ứng những yêu cầu truy xuất dữ liệu cụ thể. Một phương pháp là sử dụng quy tắc đặt tên phương thức. Spring Data JPA có thể phân tích tên phương thức để tạo truy vấn tương ứng. Ví dụ, phương thức ***findByEmail()*** có thể được sử dụng để tìm kiếm dữ liệu dựa trên thuộc tính email của Entity.

Ngoài ra, annotation ***@Query*** cho phép khai báo truy vấn bằng JPQL hoặc SQL gốc. JPQL truy vấn dựa trên Entity và thuộc tính của Entity, trong khi SQL gốc làm việc trực tiếp với bảng và cột trong cơ sở dữ liệu. Việc lựa chọn phương pháp phụ thuộc vào độ phức tạp của truy vấn và yêu cầu thực tế của hệ thống.

**IV. Mối quan hệ giữa Repository và các tầng khác**

Trong kiến trúc phân tầng của ứng dụng Spring Boot, Repository thường được Service sử dụng để truy xuất và thao tác với dữ liệu. Service chịu trách nhiệm xử lý logic nghiệp vụ, còn Repository tập trung vào hoạt động truy cập dữ liệu. Controller tiếp nhận các yêu cầu từ Client và gọi đến Service để xử lý.

Cách tổ chức này giúp mỗi tầng đảm nhiệm một trách nhiệm riêng biệt, hạn chế sự phụ thuộc giữa logic nghiệp vụ và cơ chế lưu trữ dữ liệu. Nhờ đó, ứng dụng trở nên dễ quản lý, thuận tiện cho việc kiểm thử và có khả năng mở rộng tốt hơn khi yêu cầu nghiệp vụ thay đổi.

**PHẦN 2**

**I. Entity là gì?**

Trong Spring Boot, Entity là một lớp Java được sử dụng để ánh xạ với bảng tương ứng trong cơ sở dữ liệu thông qua JPA (Java Persistence API). Mỗi đối tượng của lớp Entity đại diện cho một bản ghi trong bảng dữ liệu, trong khi các thuộc tính của lớp thường tương ứng với các cột trong bảng.

Trong dự án, Entity được sử dụng để mô hình hóa các đối tượng dữ liệu của hệ thống, phục vụ việc lưu trữ và quản lý thông tin trên cơ sở dữ liệu MySQL. Thông qua Hibernate, một framework triển khai JPA phổ biến, hệ thống có thể thực hiện các thao tác lưu trữ, truy vấn, cập nhật và xóa dữ liệu thông qua các đối tượng Java.

**II. Vai trò của Entity**

Entity đóng vai trò mô hình hóa và quản lý dữ liệu trong ứng dụng. Việc sử dụng Entity giúp tổ chức cấu trúc dữ liệu thành các lớp Java có ý nghĩa rõ ràng, đồng thời hỗ trợ ánh xạ giữa các đối tượng trong chương trình và các bảng trong cơ sở dữ liệu.

Bên cạnh đó, Entity cho phép xác định các thuộc tính dữ liệu, khóa chính, các ràng buộc và mối quan hệ giữa những đối tượng có liên quan. Khi kết hợp với Spring Data JPA và Repository, Entity hỗ trợ quá trình thao tác với cơ sở dữ liệu một cách thuận tiện, giảm sự phụ thuộc vào việc viết các câu lệnh SQL thủ công và giúp mã nguồn dễ bảo trì, mở rộng hơn.

**III. Entity trong kiến trúc ứng dụng Spring Boot**

Trong kiến trúc phân tầng của ứng dụng Spring Boot, Entity thường thuộc nhóm mô hình dữ liệu và chịu trách nhiệm biểu diễn cấu trúc dữ liệu được lưu trữ trong cơ sở dữ liệu. Entity thường được sử dụng kết hợp với Repository để thực hiện các thao tác truy xuất dữ liệu, trong khi Service đảm nhiệm việc xử lý nghiệp vụ và Controller tiếp nhận, xử lý các yêu cầu HTTP.

Ngoài ra, Entity có thể được sử dụng cùng với DTO (Data Transfer Object) để phân biệt mô hình dữ liệu được quản lý trong hệ thống với dữ liệu trao đổi giữa các thành phần hoặc giữa Backend và Client. Cách tổ chức này giúp phân tách trách nhiệm giữa các tầng, hạn chế sự phụ thuộc không cần thiết và nâng cao khả năng bảo trì, kiểm thử cũng như mở rộng ứng dụng.

