# Phần I. 

## 1. Cấu trúc

### 1.1 Cấu trúc thư mục

#### routes

`routes` dùng để định nghĩa đường dẫn API. Routes cho biết URL nào sẽ gọi chức năng nào.

Ví dụ:

- GET /api/users
- GET /api/users/:id
- POST /api/food
- PUT /api/food/:id
- DELETE /api/food/:id

Routes chủ yếu để xác định HTTP Method, URL, Controller

Ví dụ: POST /api/food -> createFood()

#### controllers

`controllers` dùng để xử lý request và response. Khi API được gọi thì controllers quyết định lấy dữ liệu gì từ request và trả response như thế nào.
Controller thường xử lý:
- req.params
- req.query
- req.body
- HTTP status code
- Response JSON
- Gọi Service

#### service

`service` dùng để xử lý Business Logic. Đây là tầng xử lý logic nghiệp vụ. Service sẽ quyết định phải làm gì với dữ liệu được đưa vào.

Controller nhận request và lấy dữ liệu từ request. Sau đó sẽ chuyển dữ liệu để service xử lý.

#### models

`models` thường dùng để định nghĩa cấu trúc dữ liệu / entity mà ứng dụng làm việc. Models thể hiện dữ liệu của hệ thống có cấu trúc như thế nào.

Ví dụ 1 User:

```text
User
-------------
id
name
password_hash
phone
email
role
```

#### migrations

`migration` dùng để lưu lại lịch sử thay đổi cấu trúc database.

Ví dụ ban đầu nhà hàng quy mô nhỏ chỉ có 1 tầng. Bảng restaurant_table chỉ cần lưu id, table_number, capicity, status. Sau nhà hàng mở rộng thêm vài tầng thì cần thêm cột floor để dễ phân biệt. Migration sẽ lưu lại:
```sql
ALTER TABLE restaurant_tables
ADD COLUMN floor INT NOT NULL DEFAULT 1;
```

### 1.2 File cấu hình

File `.env` là file dùng để lưu các biến cấu hình và thông tin môi trường của ứng dụng, đặc biệt là những thông tin không nên ghi trực tiếp vào source code. Lý do sử dụng file `.env`

- Bảo mật thông tin nhạy cảm (Security): Nếu bạn gõ trực tiếp mật khẩu database hay API Key vào file code , bất kỳ ai xem mã nguồn hoặc khi bạn đẩy code lên GitHub, hacker đều có thể đánh cắp thông tin này để phá hoại hệ thống. File .env giải quyết việc này bằng cách tách rời cấu hình bảo mật ra khỏi code.
- Linh hoạt giữa các môi trường (Flexibility): Một dự án thường có nhiều môi trường chạy khác nhau (Máy tính cá nhân - Dev, Máy chủ thử nghiệm - Staging, Máy chủ thật - Production). Khi đổi môi trường, chỉ cần thay đổi nội dung file .env thay vì phải vào sửa hàng loạt file code.
- Quản lý tập trung: Giúp lập trình viên biết ngay dự án này cần những thông số kết nối nào chỉ bằng cách nhìn vào một file duy nhất.

## 2. Xây dựng RESTful API

### 2.1 HTTP Methods

#### GET (READ)

**Mục đích:** Yêu cầu Server trả về thông tin của một tài nguyên hoặc một danh sách tài nguyên.

#### POST (CREATE)

**Mục đích:** Gửi dữ liệu (payload) từ Client lên Server để yêu cầu tạo ra một tài nguyên hoàn toàn mới. Dữ liệu cần tạo mới được đặt trong Request Body, thường được mã hóa dưới định dạng chuỗi JSON.

#### PUT - PATCH (UPDATE)

**PUT (Thay thế toàn bộ):** cập nhật bằng cách đè (replace) một phiên bản hoàn toàn mới lên tài nguyên cũ. Client phải gửi toàn bộ các thuộc tính của đối tượng. Những trường nào Client không gửi lên có thể bị set thành null hoặc giá trị mặc định.

**PATCH (Cập nhật một phần):** chỉ sửa đổi những trường dữ liệu cụ thể, giữ nguyên các trường còn lại.

#### DELETE 

**Mục đích:** Yêu cầu Server xóa bỏ một tài nguyên cụ thể.

### 2.2 Mapping URL

Trong một ứng dụng Web Backend, khi Client gửi một Request, Server cần biết chính xác đoạn code nào sẽ chịu trách nhiệm xử lý Request đó. Quá trình kết nối một yêu cầu (bao gồm HTTP Method + URL Path) tới một hàm cụ thể trong code được gọi là Mapping URL hay Routing.

Cú pháp cơ bản của Express.js: app.METHOD(PATH, HANDLER)

app/router: Thể hiện (instance) của Express hoặc bộ định tuyến.

METHOD: Phương thức HTTP in thường (get, post, put, delete).

PATH: Đường dẫn URL (ví dụ: /api/students).

HANDLER: Hàm xử lý (Controller) sẽ được thực thi khi route khớp.

### 2.3 Xử lý Request

#### Path Variable (Tham số trên đường dẫn)

**Khái niệm:** Là các giá trị động được nhúng trực tiếp vào trong cấu trúc của URL. Trong Express, khai báo Path Variable bằng cách đặt dấu hai chấm : trước tên biến trong quá trình định nghĩa Route. Thường dùng để định danh một tài nguyên cụ thể (ví dụ: lấy chi tiết, cập nhật, hoặc xóa một User/Student thông qua ID).

**Cách lấy dữ liệu:** Lấy thông qua object req.params.

URL Client gọi: GET /api/users/15

Định nghĩa Route: router.get('/api/users/:id', ...)

```javascript
const getUserById = (req, res) => {
    const userId = parseInt(req.params.id); 

    res.status(200).json({
        message: `Đang lấy thông tin của user có ID là ${userId}`
    });
};
```

#### Query Parameter (Tham số truy vấn)

**Khái niệm:** Là các cặp key=value được đính kèm ở cuối URL, bắt đầu bằng dấu chấm hỏi ? và phân cách nhau bằng dấu và &. Khác với Path Variable, Query Param không làm thay đổi cấu trúc định tuyến (route path) của API. Thường sử dụng cho các tác vụ không định danh cụ thể như: Lọc (Filter), Tìm kiếm (Search), Sắp xếp (Sort) và Phân trang (Pagination).

**Cách lấy dữ liệu:** Lấy thông qua object req.query.

Ví dụ thực tế:

URL Client gọi: GET /api/users?age=20&status=active&page=2

Định nghĩa Route: router.get('/api/users', ...) (Không cần khai báo query trên route)

Code xử lý trong Controller:

```javascript
const filterUsers = (req, res) => {
    const age = req.query.age;       // "20"
    const status = req.query.status; // "active"
    const page = req.query.page;     // "2"

    res.status(200).json({
        message: `Lọc user với độ tuổi \({age}, trạng thái\){status}, tại trang ${page}`
    });
};
```

#### Request Body (Nội dung tải trọng - Payload)

**Khái niệm:** Là phần thân của HTTP Request, chứa một khối dữ liệu lớn. Định dạng phổ biến nhất hiện nay là JSON. Dữ liệu trong Body được bảo mật hơn (khi dùng HTTPS) và không bị giới hạn độ dài như URL. Dùng để gửi các form dữ liệu phức tạp khi Tạo mới (POST) hoặc Cập nhật (PUT/PATCH) tài nguyên.

**Cách lấy dữ liệu:** Lấy thông qua object req.body.

Ví dụ thực tế:

Client gọi: POST /api/users

Dữ liệu Client gửi (JSON):

```json
{
  "name": "Nguyen Van A",
  "email": "nva@gmail.com",
  "age": 22
}
```

Code xử lý trong Controller:

```javascript
const createUser = (req, res) => {
    const { name, email, age } = req.body;

    if (!name || !email) {
        return res.status(400).json({ message: "Tên và Email không được để trống" });
    }

    res.status(201).json({
        message: "Tạo user thành công",
        data: { name, email, age }
    });
};
```

### 2.4 Xử lý Response

#### Trả về định dạng JSON

Trong các hệ thống API hiện đại, JSON là chuẩn giao tiếp mặc định vì tính gọn nhẹ và khả năng đọc hiểu dễ dàng bởi mọi ngôn ngữ lập trình (Frontend Web, Mobile App Android/iOS).

Cách Express xử lý: Thay vì dùng res.send() (có thể trả về text/html), chúng ta sử dụng phương thức res.json(). Hàm này sẽ tự động chuyển đổi Object/Array của JavaScript thành chuỗi JSON và thiết lập header Content-Type: application/json ở phía ngầm định.

#### HTTP Status

`200 OK (Nhóm 2xx - Thành công)`

Ý nghĩa: Request đã được tiếp nhận và xử lý thành công.

Áp dụng: Dùng làm kết quả trả về mặc định cho các lệnh GET (lấy dữ liệu), PUT/PATCH (cập nhật), và DELETE (xóa).

Code ví dụ: res.status(200).json({ data: user })

`201 Created (Nhóm 2xx - Tạo mới)`

Ý nghĩa: Request thành công và hệ thống đã tạo ra một tài nguyên hoàn toàn mới.

Áp dụng: Chỉ dùng riêng cho lệnh POST khi dữ liệu đã được Insert thành công vào Database.

Code ví dụ: res.status(201).json({ message: "Đã tạo", data: newUser })

`400 Bad Request (Nhóm 4xx - Lỗi phía Client)`

Ý nghĩa: Server từ chối xử lý vì dữ liệu Client gửi lên bị sai cú pháp hoặc không hợp lệ.

Áp dụng: Dùng khi Client gửi thiếu trường dữ liệu bắt buộc (Validation Error), sai định dạng email, hoặc sai kiểu dữ liệu (gửi chữ thay vì số).

Code ví dụ: res.status(400).json({ message: "Thiếu trường name" })

`404 Not Found (Nhóm 4xx - Không tìm thấy)`

Ý nghĩa: Tài nguyên Client đang yêu cầu không tồn tại trên hệ thống.

Áp dụng: Dùng khi truy vấn bằng ID trên URL nhưng trong Database không có bản ghi đó (ví dụ truyền ID ảo vào hàm GET, PUT, hoặc DELETE).

Code ví dụ: res.status(404).json({ message: "User not found" })

`500 Internal Server Error (Nhóm 5xx - Lỗi máy chủ)`

Ý nghĩa: Server gặp sự cố nội bộ ngoài ý muốn (Crash) và không thể hoàn thành request.

Áp dụng: Dùng khi mất kết nối Database, lỗi logic code (chia cho 0, gọi hàm không tồn tại), hoặc các lỗi không bắt được (Unhandled Exception).

Code ví dụ: res.status(500).json({ message: "Lỗi hệ thống" })

### 2.5 Error Handling

#### Cấu trúc error response thống nhất

Cấu trúc JSON chuẩn hóa được áp dụng chặt chẽ cho mọi lỗi trong dự án:

```json
{
  "error": true,
  "code": 400,
  "message": "Dữ liệu đầu vào không hợp lệ",
  "details": [
    { "field": "email", "message": "Email không đúng định dạng" },
    { "field": "age", "message": "Tuổi phải lớn hơn 18" }
  ]
}
```

- code (hoặc status): Mã HTTP Status Code giúp Client lập trình logic rẽ nhánh (ví dụ: gặp 401 thì tự động chuyển hướng ra trang Đăng nhập).

- message: Thông báo ngắn gọn, rõ ràng, ưu tiên ngôn ngữ người dùng để Frontend có thể hiển thị trực tiếp lên UI (Ví dụ: "Tài khoản không tồn tại").

- details: Cung cấp nguyên nhân chi tiết. Trường này rất hữu ích khi validate form (trả về danh sách cụ thể các ô nhập sai) hoặc dùng để chứa Stack Trace (dòng code gây lỗi) dành cho Developer debug.

#### Phân biệt Client Error (4xx) vs Server Error (5xx)

`Client Error (Nhóm lỗi 4xx - Lỗi do người gửi):`

- **Bản chất:** Lỗi xuất phát từ phía Client. Client đã gửi sai định dạng (JSON hỏng), thiếu dữ liệu bắt buộc, hoặc cố tình truy cập tài nguyên không tồn tại/không có quyền.

**Cách xử lý:** Client bắt buộc phải sửa lại nội dung Request trước khi gửi lại. Việc cố chấp gửi lại y hệt Request cũ chắc chắn sẽ tiếp tục thất bại.

Ví dụ tiêu biểu:

- 400 Bad Request: Truyền sai kiểu dữ liệu (nhập chữ vào trường tuổi).

- 401 Unauthorized: Gọi API cần bảo mật nhưng không gắn Token.

- 403 Forbidden: User bình thường cố gắng gọi API xóa dữ liệu của Admin.

- 404 Not Found: Truy vấn chi tiết một ID không có trong Database.

`Server Error (Nhóm lỗi 5xx - Lỗi do hệ thống xử lý):`

- **Bản chất:** Request của Client hoàn toàn đúng chuẩn, đầy đủ thông tin, nhưng Server gặp sự cố trong quá trình thực thi và không thể hoàn thành nhiệm vụ.

- **Cách xử lý:** Client không cần (và không thể) sửa Request. Hành động duy nhất Client có thể làm là thử lại sau (Retry later). Ở phía Backend, lập trình viên/DevOps phải nhận được cảnh báo (alert) và kiểm tra log để vá lỗi.

Ví dụ tiêu biểu:

- 500 Internal Server Error: Code Node.js bị lỗi logic (chia cho 0, gọi hàm không tồn tại) hoặc không thể kết nối tới cơ sở dữ liệu PostgreSQL.

- 502 Bad Gateway / 503 Service Unavailable: Server bị quá tải hoặc đang trong thời gian bảo trì nâng cấp.

### 2.6 Pagination trong API

#### Response Format

Tiêu chuẩn hóa Response Format

Để Frontend có thể xây dựng được các thanh điều hướng phân trang (Pagination UI) một cách dễ dàng, API không chỉ trả về mảng dữ liệu mà phải đính kèm các thông tin siêu dữ liệu (Metadata).

Theo yêu cầu của hệ thống, định dạng Response chuẩn được thiết kế nghiêm ngặt như sau:

```json
{
  "data": [
    { "id": 1, "name": "Nguyen Van A" },
    { "id": 2, "name": "Tran Thi B" }
  ],
  "page": 1,            // Trang hiện tại
  "size": 10,           // Số lượng phần tử trên mỗi trang
  "totalPages": 5,      // Tổng số trang tính toán được
  "totalElements": 48   // Tổng số bản ghi thực tế trong Database
}
```

# Phần II. Tích hợp Database (ORM)

## 1. Khái niệm
**ORM (Object-Relational Mapping)** là một kỹ thuật lập trình giúp tự động ánh xạ (mapping) các đối tượng trong mã nguồn (objects) với các bảng dữ liệu (tables) trong cơ sở dữ liệu quan hệ (RDBMS).

Thay vì phải viết các câu lệnh SQL thô (Raw SQL) để thực hiện các thao tác CRUD (Create, Read, Update, Delete), ORM cho phép bạn tương tác trực tiếp với cơ sở dữ liệu thông qua các đối tượng và phương thức của ngôn ngữ lập trình. ORM sẽ tự động dịch các thao tác này thành các câu lệnh SQL tương ứng để thực thi dưới database.

Ví dụ phổ biến: Hibernate / Spring Data JPA (Java), Prisma / Sequelize (Node.js), SQLAlchemy (Python).

## 2. Các lợi ích chính của ORM
Tăng tốc độ phát triển (Productivity): Giảm thiểu khối lượng lớn code SQL lặp đi lặp lại (boilerplate). Lập trình viên có thể tập trung vào logic nghiệp vụ thay vì tốn thời gian nối chuỗi SQL và ánh xạ thủ công dữ liệu trả về vào các object.

Trừu tượng hóa cơ sở dữ liệu (Database Agnostic): ORM xử lý các khác biệt về phương ngữ SQL (SQL dialects) giữa các hệ quản trị CSDL. Bạn có thể dễ dàng chuyển đổi từ MySQL sang PostgreSQL hoặc Oracle mà chỉ cần thay đổi file cấu hình, mã nguồn logic gần như giữ nguyên.

Ngăn chặn SQL Injection: Hầu hết các ORM framework hiện đại tự động xử lý việc escape dữ liệu và sử dụng prepared statements, giúp ứng dụng an toàn hơn trước các cuộc tấn công SQL Injection.

Code sạch và dễ bảo trì hơn: Dữ liệu được quản lý đồng nhất theo mô hình hướng đối tượng (OOP). Mỗi bảng (Table) là một Lớp (Class), mỗi cột (Column) là một thuộc tính (Attribute), và mỗi dòng (Record) là một Đối tượng (Object), giúp code dễ đọc, dễ hiểu và tuân thủ các nguyên tắc thiết kế phần mềm.

Quản lý quan hệ dễ dàng: Các mối quan hệ phức tạp như 1-1, 1-N, N-N trong CSDL được thể hiện trực quan qua các collection hoặc object tham chiếu trong mã nguồn, đi kèm với các tính năng tự động tải dữ liệu (Lazy Loading / Eager Loading).

Hỗ trợ Transaction và Caching: Các ORM mạnh mẽ thường tích hợp sẵn cơ chế quản lý giao dịch (Transaction management) và bộ nhớ đệm (First-level/Second-level Cache) để tối ưu hiệu suất truy vấn.