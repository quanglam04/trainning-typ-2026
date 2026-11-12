I. SQL cơ bản
1. Mô tả bài toán
Em xây dựng cơ sở dữ liệu cho bài toán quản lý nhà hàng. Hệ thống lưu trữ dữ liệu phục vụ các hoạt động chính của nhà hàng và các nhóm người sử dụng như quản trị viên, quản lý, thu ngân, nhân viên bếp, nhân viên phục vụ và khách hàng.

Các dữ liệu chính cần lưu trữ gồm:

Thông tin tài khoản nhân viên và vai trò của người dùng.
Thông tin khách hàng.
Thông tin danh mục món ăn.
Thông tin món ăn và tình trạng còn/hết món.
Thông tin bàn trong nhà hàng.
Thông tin đặt bàn.
Thông tin đơn hàng và các món trong từng đơn hàng.
Thông tin thanh toán.
Lịch sử thay đổi trạng thái đơn hàng.
Cơ sở dữ liệu tập trung vào việc lưu trữ và liên kết các dữ liệu phát sinh trong quá trình hoạt động hàng ngày của nhà hàng.

2. Các nhóm người sử dụng và dữ liệu liên quan
2.1 Quản trị viên – Admin
Admin là người có quyền quản lý cao nhất đối với dữ liệu nhân viên trong hệ thống.

Các dữ liệu liên quan đến Admin gồm:

Thông tin tài khoản.
Vai trò của người dùng.
Trạng thái tài khoản.
Thông tin các nhân viên.
Thông tin đơn hàng.
Thông tin thanh toán.
Thông tin món ăn và tình trạng món.
Thông tin bàn.
Admin có thể cấp hoặc thay đổi vai trò cho các tài khoản nhân viên.

Các vai trò được lưu trực tiếp trong thuộc tính role của thực thể User, gồm:

admin
manager
cashier
kitchen
waiter
2.2 Quản lý – Manager
Manager là người quản lý các hoạt động của nhà hàng.

Các dữ liệu liên quan gồm:

Thông tin món ăn.
Danh mục món ăn.
Tình trạng còn/hết của món ăn.
Thông tin bàn.
Thông tin đặt bàn.
Thông tin đơn hàng.
Thông tin thanh toán.
Doanh thu của nhà hàng.
Manager không có quyền thay đổi vai trò của người dùng.

2.3 Thu ngân – Cashier
Cashier là người thực hiện các hoạt động liên quan đến thanh toán.

Các dữ liệu liên quan gồm:

Thông tin đơn hàng.
Chi tiết đơn hàng.
Tổng tiền của đơn hàng.
Phương thức thanh toán.
Trạng thái thanh toán.
Thời điểm thanh toán.
2.4 Nhân viên bếp – Kitchen
Kitchen là người tiếp nhận và xử lý các món ăn trong đơn hàng.

Các dữ liệu liên quan gồm:

Thông tin đơn hàng.
Các món ăn trong đơn hàng.
Số lượng món.
Trạng thái đơn hàng.
Lịch sử thay đổi trạng thái đơn hàng.
Nhân viên bếp có thể cập nhật trạng thái đơn hàng trong quá trình chế biến, ví dụ từ CONFIRMED sang COOKING và sau đó sang READY.

2.5 Nhân viên phục vụ – Waiter
Waiter là người phục vụ khách hàng và xử lý các hoạt động liên quan đến bàn và đơn hàng.

Các dữ liệu liên quan gồm:

Thông tin bàn.
Trạng thái bàn.
Thông tin đặt bàn.
Thông tin khách hàng.
Thông tin đơn hàng.
Chi tiết đơn hàng.
Trạng thái đơn hàng.
2.6 Khách hàng – Customer
Khách hàng là người sử dụng dịch vụ của nhà hàng.

Các dữ liệu liên quan gồm:

Thông tin khách hàng.
Thông tin đặt bàn.
Thông tin bàn được đặt.
Các đơn hàng đã tạo.
Các món ăn trong đơn hàng.
Thông tin thanh toán.
3. MÔ HÌNH THỰC THỂ - LIÊN KẾT (E-R)
3.1 Phân tích các quy tắc dữ liệu
Các quy tắc sau được sử dụng làm cơ sở để xác định các thực thể và mối quan hệ trong cơ sở dữ liệu:

Một người dùng có một vai trò trong hệ thống. Vai trò có thể là admin, manager, cashier, kitchen hoặc waiter.
Một người dùng có thể tạo nhiều đơn hàng, nhưng mỗi đơn hàng được tạo bởi một nhân viên cụ thể.
Một khách hàng có thể có nhiều đơn hàng, nhưng mỗi đơn hàng thuộc về một khách hàng.
Một danh mục có thể có nhiều món ăn, nhưng mỗi món ăn thuộc về một danh mục.
Một món ăn có số lượng tồn tại để xác định món còn hay hết.
Một bàn có thể xuất hiện trong nhiều lần đặt bàn theo thời gian.
Một khách hàng có thể đặt nhiều lần, mỗi lần đặt gắn với một bàn cụ thể.
Một đơn hàng có thể bao gồm nhiều món ăn và một món ăn có thể xuất hiện trong nhiều đơn hàng. Quan hệ này được thể hiện thông qua Order_Item.
Một đơn hàng có thể có một thông tin thanh toán.
Một đơn hàng có thể có nhiều bản ghi lịch sử trạng thái.
Mỗi bản ghi lịch sử trạng thái thuộc về một đơn hàng và được thực hiện bởi một nhân viên.
Một nhân viên thu ngân có thể thực hiện nhiều lần thanh toán.
Một bàn tại một thời điểm chỉ có một trạng thái như available, occupied, reserved hoặc maintenance.
3.2 Xác định thực thể và thuộc tính
Thực thể 1: User - Người dùng

Lưu thông tin tài khoản của các nhân viên sử dụng hệ thống.

Các thuộc tính:

id: Khóa chính.
username: Tên đăng nhập.
password_hash: Mật khẩu đã mã hóa.
full_name: Họ và tên.
email: Email.
phone: Số điện thoại.
role: Vai trò người dùng.
status: Trạng thái tài khoản.
created_at: Thời điểm tạo.
role xác định nhóm người dùng trong hệ thống:

admin
manager
cashier
kitchen
waiter
email được đặt UNIQUE để tránh trùng dữ liệu.

Thực thể 2: Customer - Khách hàng

Lưu thông tin khách hàng sử dụng dịch vụ của nhà hàng.

Các thuộc tính:

id: Khóa chính.
full_name: Họ và tên.
phone: Số điện thoại.
email: Email.
address: Địa chỉ.
created_at: Thời điểm tạo.
Thông tin khách hàng được sử dụng để liên kết với các lần đặt bàn và đơn hàng.

Thực thể 3: Category - Danh mục món ăn

Lưu thông tin các nhóm món ăn trong nhà hàng.

Các thuộc tính:

id: Khóa chính.
name: Tên danh mục.
description: Mô tả danh mục.
status: Trạng thái danh mục.
Ví dụ các danh mục:

Món chính
Khai vị
Đồ uống
Tráng miệng
Một danh mục có thể chứa nhiều món ăn.

Thực thể 4: Product - Món ăn

Lưu thông tin các món ăn được phục vụ trong nhà hàng.

Các thuộc tính:

id: Khóa chính.
category_id: ID danh mục.
name: Tên món ăn.
description: Mô tả món ăn.
price: Giá món ăn.
quantity: Số lượng món còn lại.
status: Trạng thái món ăn.
created_at: Thời điểm tạo.
updated_at: Thời điểm cập nhật.
category_id là khóa ngoại tham chiếu đến Category.id.

quantity được sử dụng để theo dõi số lượng món còn lại.

status có thể nhận các giá trị:

available
unavailable
Khi số lượng món bằng 0, món có thể được chuyển sang trạng thái unavailable.

Thực thể 5: Restaurant_Table - Bàn

Lưu thông tin các bàn trong nhà hàng.

Các thuộc tính:

id: Khóa chính.
table_number: Số bàn.
capacity: Số người tối đa.
status: Trạng thái bàn.
Các trạng thái của bàn:

available
occupied
reserved
maintenance
Bảng này giúp nhân viên biết bàn nào đang trống, đang được sử dụng hoặc đã được đặt trước.

Thực thể 6: Reservation - Đặt bàn

Lưu thông tin các lần đặt bàn của khách hàng.

Các thuộc tính:

id: Khóa chính.
customer_id: ID khách hàng.
table_id: ID bàn.
reservation_time: Thời gian đặt bàn.
number_of_people: Số người.
status: Trạng thái đặt bàn.
note: Ghi chú.
created_at: Thời điểm tạo.
customer_id là khóa ngoại tham chiếu đến Customer.id.

table_id là khóa ngoại tham chiếu đến Restaurant_Table.id.

Các trạng thái đặt bàn có thể gồm:

pending
confirmed
cancelled
completed
Thực thể 7: Order - Đơn hàng

Lưu thông tin các đơn hàng phát sinh trong nhà hàng.

Các thuộc tính:

id: Khóa chính.
customer_id: ID khách hàng.
table_id: ID bàn.
created_by: ID nhân viên tạo đơn.
total_amount: Tổng tiền đơn hàng.
status: Trạng thái đơn hàng.
note: Ghi chú.
created_at: Thời điểm tạo.
updated_at: Thời điểm cập nhật.
customer_id là khóa ngoại tham chiếu đến Customer.id.

table_id là khóa ngoại tham chiếu đến Restaurant_Table.id.

created_by là khóa ngoại tham chiếu đến User.id.

Các trạng thái của đơn hàng:

pending
confirmed
cooking
ready
served
completed
cancelled
Thực thể 8: Order_Item - Chi tiết đơn hàng

Lưu các món ăn cụ thể thuộc một đơn hàng.

Các thuộc tính:

id: Khóa chính.
order_id: ID đơn hàng.
product_id: ID món ăn.
product_name: Tên món tại thời điểm đặt.
unit_price: Giá món tại thời điểm đặt.
quantity: Số lượng.
subtotal: Thành tiền.
note: Ghi chú.
order_id là khóa ngoại tham chiếu đến Order.id.

product_id là khóa ngoại tham chiếu đến Product.id.

product_name và unit_price có thể được lưu lại để giữ thông tin món tại thời điểm đơn hàng được tạo, tránh việc thay đổi thông tin món sau này làm ảnh hưởng đến dữ liệu lịch sử đơn hàng.

Thực thể 9: Payment - Thanh toán

Lưu thông tin thanh toán của đơn hàng.

Các thuộc tính:

id: Khóa chính.
order_id: ID đơn hàng.
cashier_id: ID nhân viên thu ngân.
amount: Số tiền thanh toán.
payment_method: Phương thức thanh toán.
status: Trạng thái thanh toán.
paid_at: Thời điểm thanh toán.
order_id là khóa ngoại tham chiếu đến Order.id.

cashier_id là khóa ngoại tham chiếu đến User.id.

Các phương thức thanh toán:

cash
banking
card
e_wallet
Các trạng thái thanh toán:

pending
paid
failed
refunded
Thực thể 10: Order_Status_History - Lịch sử trạng thái đơn hàng

Lưu lại quá trình thay đổi trạng thái của đơn hàng.

Các thuộc tính:

id: Khóa chính.
order_id: ID đơn hàng.
changed_by: ID nhân viên thực hiện thay đổi.
old_status: Trạng thái cũ.
new_status: Trạng thái mới.
note: Ghi chú.
changed_at: Thời điểm thay đổi.
order_id là khóa ngoại tham chiếu đến Order.id.

changed_by là khóa ngoại tham chiếu đến User.id.

Ví dụ một đơn hàng có thể thay đổi trạng thái:

pending
   ↓
confirmed
   ↓
cooking
   ↓
ready
   ↓
served
   ↓
completed
Mỗi lần trạng thái thay đổi, một bản ghi mới được lưu vào Order_Status_History.

3.3 Phân tích các mối quan hệ
Quan hệ Many-to-Many

Order – Product

Một đơn hàng có thể chứa nhiều món ăn.
Một món ăn có thể xuất hiện trong nhiều đơn hàng.
Quan hệ M-N được thể hiện thông qua Order_Item.
Quan hệ One-to-Many

Category – Product

Một danh mục có thể có nhiều món ăn.
Mỗi món ăn thuộc một danh mục.
Customer – Reservation

Một khách hàng có thể có nhiều lần đặt bàn.
Mỗi lần đặt bàn thuộc về một khách hàng.
Restaurant_Table – Reservation

Một bàn có thể xuất hiện trong nhiều lần đặt bàn theo thời gian.
Mỗi lần đặt bàn gắn với một bàn.
Customer – Order

Một khách hàng có thể tạo nhiều đơn hàng.
Mỗi đơn hàng thuộc về một khách hàng.
Restaurant_Table – Order

Một bàn có thể được sử dụng cho nhiều đơn hàng theo các thời điểm khác nhau.
Mỗi đơn hàng gắn với một bàn.
User – Order

Một nhân viên có thể tạo nhiều đơn hàng.
Mỗi đơn hàng được tạo bởi một nhân viên.
Order – Order_Item

Một đơn hàng có nhiều chi tiết món ăn.
Mỗi Order_Item thuộc về một đơn hàng.
Product – Order_Item

Một món ăn có thể xuất hiện trong nhiều chi tiết đơn hàng.
Mỗi Order_Item tham chiếu đến một món ăn.
Order – Payment

Một đơn hàng có một thông tin thanh toán.
Mỗi thông tin thanh toán thuộc về một đơn hàng.
User – Payment

Một nhân viên thu ngân có thể thực hiện nhiều lần thanh toán.
Mỗi lần thanh toán được thực hiện bởi một nhân viên thu ngân.
Order – Order_Status_History

Một đơn hàng có thể có nhiều bản ghi lịch sử trạng thái.
Mỗi bản ghi lịch sử thuộc về một đơn hàng.
User – Order_Status_History

Một nhân viên có thể thực hiện nhiều lần thay đổi trạng thái.
Mỗi bản ghi lịch sử được thực hiện bởi một nhân viên.
Quan hệ One-to-One

Order – Payment

Trong phạm vi bài toán, mỗi đơn hàng có tối đa một thông tin thanh toán.

Quan hệ này có thể được hỗ trợ bằng ràng buộc UNIQUE trên Payment.order_id.