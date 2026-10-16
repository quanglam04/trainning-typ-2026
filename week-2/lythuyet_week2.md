# Framework & Xây dựng RESTful API
## Spring Boot
### Khởi tạo Project
**Spring Initializr** là một trình tạo project cho Spring Boot một cách nhanh chóng và tiện lợi.
Các mục có trong Spring Initializr:
- Project:
	Trong Spring Initializr có 3 loại Gradle - Groovy / Gradle - Kotlin / Maven là các trình quản lý thư viện trong Spring Boot, được ưa chuộng sử dụng nhiều nhất là **Maven**.
- Spring Boot
	Đơn giản là chọn phiên bản của Spring Boot
- Project Metadata
	- Group: là tên của package làm việc.
	- Artifact: là tên của dự án.
	- Package name: là tên thư mục gốc chứa toàn bộ dự án (tên thường là kết hợp của Group + Artifact).
	- Packaging: có hai loại Jar hoặc War, là định dạng đầu ra khi build ứng dụng.
	- Configuration: là file Config của dự án để thêm hoặc định nghĩa cho Project.
	- Java: Phiên bản Java.
- Dependencies: các gói thư viện cài từ bên ngoài nhằm phục vụ Project.
### Cấu trúc thư mục
Trong Spring Boot tổ chức theo mô hình phân lớp (Layered Architecture) kết hợp với mô hình MVC (Model - View - Controller)
Những cấu trúc chính trong một Project bao gồm:
- .src/
	trong src là nơi lưu trữ các file code bao gồm:
	- .main/
		- .java/
			- lưu trữ các mã nguồn Java và file Application để chạy Project.
		- .resources/
			- lưu trữ các file mã nguồn không phải Java.
			- lưu file application.yaml hoặc application.properties dùng để Config Project.
	- .test/java
		- gồm file Application test *(hiện tại chưa tìm hiểu đến)*.
- .target
	là nơi chứa tất cả file sau khi Compile và file Packaging có đuôi loại chúng ta đã chọn ở  **Spring Initializr**.
- pom.xml (project object model).
	được chia làm ba phần, phần đầu là thông tin kế thừa mặc định của Spring Boot, phần thứ hai là các dependency thêm vào dự án phần cuối là các build và plugin để đóng gói dự án.
- .mvn
	Chứa maven-wrapper.properties là phiên bảng maven đang sử dụng, tránh trường hợp sau này sử dụng máy khác bị lỗi.
- Có 2 tệp maven 1 cái cho mac hoặc linux và 1 cái .cmd cho win đều là 2 lệnh shell, nó sẽ tự động tải phiên bản của maven trong tệp config.
- Help.md để hướng dẫn.
	
Spring mvc là viết tắt của Model-View-Controller giúp xây dựng các ứng dụng web, cung cấp một cách rõ ràng tách biệt các phần khác nhau của ứng dụng để dễ quản lý.
- Model: là logic + data được lưu trữ, thường được kết nối với CSDL hoặc nguồn dữ liệu khác.
- View là những thứ User nhìn thấy.
- Controller: như là bộ điều khiển giao thông, nó xử lý các yêu cầu từ người dùng tương tác với Model để lấy dữ liệu sau đó cho View hiển thị .
Dựa theo mô hình đó, ta chia các thư mục sau:
- Controller: nhận request, gọi service để xử lý và trả về kết quả.
- Entity: viết các Table bằng Class (map Class với DB).
- Dto (Data Transfer Object): *Để sau*
- service: nơi tính toán data và logic và gọi xuống repository.
- repository: tạo interface và giao tiếp với DB.
- Migration: *để sau*
-> Một Request thường đi qua 3 layer: Controller -> Service -> Repository.
## Xây dựng RESTful API
### API và REST
API hay Application Programming Interface thông thường là url hoặc func để các application giao tiếp với nhau. Khi người ta nói đến API, thứ người ta hay nói là Web API.
REST: coi như là một giao thức, những gì mang tính chất REST được gọi là RestFul. phải đủ 5 rằng buộc sau đây:
- Client - Server: mô hình Client - Server: client gửi **request** lên server, server trả về **respone**.
- Stateless: Server sẽ không lưu thông tin gửi tới, mỗi **Request** sẽ được xử lý độc lập và không quan tâm các **Request** trước nó.
- Cacheable: Cho phép lưu trữ lại thông tin vào local cache, những thông tin được lưu trữ lại là những thông tin nhận được của server và server cho phép lưu vào cache. Điều này giúp tăng hiệu suất cho các hệ thống web.
- Uniform Interface: Thiết kế **URL** một cách thống nhất, mỗi tài nguyên chỉ nên có một **URL** của riêng nó.
- Layer system: Giả sử như client gửi **Request** qua một server A, server A lại lưu trữ dữ liệu tại server B, từ đó server A gửi **Request** đến server B và server B gửi respone ngược về client.
- Code on command (không bắt buộc): thông thường **Response** của server sẽ là dạng của XML hoặc JSON nhưng ta cũng có thể gửi lại cho USER những đoạn code có thể thực thi được.
### HTTP Methods
- GET: là lấy dữ liệu từ server, server sẽ phản hồi lại bằng HTML, JSON hoặc IMG,..., GET chỉ đọc, không làm thay đổi dữ liệu trên server.
- POST: là gửi dữ liệu cho server, server sẽ nhận dữ liệu và phản hồi.
- PUT: sử dụng khi cập nhật chúng sẽ thay thế hoàn toàn tài nguyên hiện có, có một số server nếu không có dữ liệu thì chúng sẽ tự tạo ra nó. PUT nhiều lần cũng chỉ cho ra một kết quả, không như POST, 
- PATCH: giống PUT nhưng thay vì thay thế toàn bộ, chúng chỉ cập nhật những phần cập nhật
- DELETE: xoá bỏ mọi tài nguyên khỏi máy chủ.
- HEAD: giống PUT nhưng chỉ lấy phần header, chỉ xem được thông tin chứ ko phải nội dung. Hữu ích khi kiểm tra thứ gì đó.
- OPTIONS: hỏi về các Methods có thể thực hiện trong một endpoint nào đó nhất định. Trình duyệt cũng sử dụng OPTIONS khi xử lý các yêu cầu khác nhau.
### Xử Lý Response
#### HTTPS Status code
- 200 OK: Yêu cầu đã xử lý thành công.
- 201 Created: Yêu cầu thành công và một tài nguyên mới vừa được tạo ra.
- 400 Bad Request: Yêu cầu gửi lên không hợp lệ, sai định dạng hoặc thông tin, khiến server từ chối xử lý.
- 404 Not Found: Máy chủ không tìm thấy dữ liệu hoặc đường dẫn yêu cầu.
- 500 Internal Server Error: Máy chủ gặp sự cố bất ngờ hoặc ngoài ý muốn.

## Pagination trong API
# Tích hợp Database (ORM)
## Cách tiếp cận từ NNLT và DB
Có 2 cách để lấy dữ liệu từ DB đến Java
- JDBC: thư viện được xây dựng từ java core, giúp kết nối đến CSDL
	kết nối đến DB bằng một Driver, sử dụng SQL để truy vấn và trả về dữ liệu qua ResultSet 
- Presistence Framework: là Framework, nó dùng ResultSet từ JDBC (dùng thư viện JDBC).
### Presistence framework
Là những framework kết nối NNLT sang CSDL (thường triển khai dạng ORM).

Mô hình
	App  <-> Object (Entity) <-mapping-> Table <-> DB

Ánh xạ kết quả trả về dữ liệu Object, tương tác qua Class và nó sẽ cố gắng chuyển qua sinh tự động câu SQL và tương tác với Table.
## Tư tưởng ORM
ORM là "cách tiếp cận" lấy dữ liệu đối tượng (Mapping Class với table).
## Chuẩn JPA
Java Persistence API là đặc tả của ORM.
Bao gồm các Interface không triển khai ORM.
Cách triển khai của JPA thông qua ORM Framework (Spring Data JPA, Hibernate,...)
### Cấu trúc của JPA
triển khai theo mô hình:
- Entity Manager Factory
- Entity Manager
- Entity Transaction
- Query
- Persistence

	JPA API -> Entity -> DB
### Hibernate
là một ORM Framework, dùng ngôn ngữ truy vấn HQL và là một chuẩn triển khai JPA.