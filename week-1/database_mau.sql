
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'manager', 'cashier', 'kitchen', 'waiter')),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'active'
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    category_id INT NOT NULL REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(12, 2) NOT NULL CHECK (price >= 0),
    quantity INT DEFAULT 0 CHECK (quantity >= 0),
    status VARCHAR(20) DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE restaurant_tables (
    id SERIAL PRIMARY KEY,
    table_number VARCHAR(10) UNIQUE NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(20) DEFAULT 'available' CHECK (status IN ('available', 'occupied', 'reserved', 'maintenance'))
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES customers(id),
    table_id INT NOT NULL REFERENCES restaurant_tables(id),
    reservation_time TIMESTAMP NOT NULL,
    number_of_people INT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'cancelled', 'completed')),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(id),
    table_id INT NOT NULL REFERENCES restaurant_tables(id),
    created_by INT NOT NULL REFERENCES users(id),
    total_amount DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'cooking', 'ready', 'served', 'completed', 'cancelled')),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id),
    product_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    subtotal DECIMAL(12, 2) NOT NULL,
    note TEXT
);

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    order_id INT UNIQUE NOT NULL REFERENCES orders(id),
    cashier_id INT NOT NULL REFERENCES users(id),
    amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(20) CHECK (payment_method IN ('cash', 'banking', 'card', 'e_wallet')),
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'paid', 'failed', 'refunded')),
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_status_history (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    changed_by INT NOT NULL REFERENCES users(id),
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    note TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Thêm Người dùng (10 Nhân viên)
INSERT INTO users (username, password_hash, full_name, email, phone, role) VALUES 
('admin01', 'hash1', 'Nguyễn Quản Trị', 'admin@restaurant.com', '0901000001', 'admin'),
('manager01', 'hash2', 'Trần Quản Lý', 'manager@restaurant.com', '0901000002', 'manager'),
('cashier01', 'hash3', 'Lê Thu Ngân', 'cashier1@restaurant.com', '0901000003', 'cashier'),
('cashier02', 'hash4', 'Vũ Thu Ngân 2', 'cashier2@restaurant.com', '0901000099', 'cashier'),
('kitchen01', 'hash5', 'Phạm Đầu Bếp 1', 'kitchen1@restaurant.com', '0901000004', 'kitchen'),
('kitchen02', 'hash6', 'Trịnh Đầu Bếp 2', 'kitchen2@restaurant.com', '0901000088', 'kitchen'),
('waiter01', 'hash7', 'Hoàng Phục Vụ 1', 'waiter01@restaurant.com', '0901000005', 'waiter'),
('waiter02', 'hash8', 'Đinh Phục Vụ 2', 'waiter02@restaurant.com', '0901000006', 'waiter'),
('waiter03', 'hash9', 'Bùi Phục Vụ 3', 'waiter03@restaurant.com', '0901000007', 'waiter'),
('waiter04', 'hash10', 'Ngô Phục Vụ 4', 'waiter04@restaurant.com', '0901000008', 'waiter');

-- Thêm Khách hàng (10 Khách hàng)
INSERT INTO customers (full_name, phone, email, address) VALUES 
('Nguyễn Văn Khách', '0987111222', 'khach1@gmail.com', 'Hà Nội'),
('Trần Thị Hằng', '0987333444', 'khach2@gmail.com', 'Đà Nẵng'),
('Lê Khách Vip', '0987555666', 'khachvip@gmail.com', 'TP.HCM'),
('Đỗ Hùng Dũng', '0912123123', 'dunghd@gmail.com', 'Hải Phòng'),
('Phạm Thị Lan', '0944555666', 'lanpt@gmail.com', 'Cần Thơ'),
('Hoàng Anh Tú', '0933111222', 'tuha@gmail.com', 'Huế'),
('Võ Minh Trí', '0966777888', 'trivm@gmail.com', 'Nha Trang'),
('Bùi Xuân Bách', '0977888999', 'bachbx@gmail.com', 'Hà Nội'),
('Đặng Thu Thảo', '0999000111', 'thaodt@gmail.com', 'TP.HCM'),
('Lý Tiểu Long', '0900111222', 'longlt@gmail.com', 'Đà Nẵng');

-- Thêm Danh mục (6 Danh mục)
INSERT INTO categories (name, description) VALUES 
('Món chính', 'Các món ăn chính no bụng'),
('Khai vị', 'Món ăn nhẹ trước bữa chính'),
('Đồ uống', 'Nước giải khát và bia'),
('Tráng miệng', 'Bánh ngọt và trái cây'),
('Combo', 'Các set đồ ăn gia đình'),
('Lẩu & Nướng', 'Thích hợp cho hội nhóm');

-- Thêm Sản phẩm (22 Món ăn phong phú)
INSERT INTO products (category_id, name, description, price, quantity) VALUES 
(2, 'Salad Cá Ngừ', 'Salad rau xanh và cá ngừ', 45000, 30),
(2, 'Súp Cua', 'Súp cua biển', 35000, 20),
(2, 'Khoai Tây Chiên', 'Khoai tây chiên giòn', 30000, 50),
(2, 'Gỏi Ngó Sen', 'Gỏi ngó sen tôm thịt', 55000, 15),
(1, 'Beefsteak Thăn Bò', 'Bò bít tết sốt tiêu đen', 150000, 15),
(1, 'Cá Hồi Áp Chảo', 'Cá hồi Na Uy áp chảo', 180000, 10),
(1, 'Mì Ý Hải Sản', 'Mì xào hải sản tổng hợp', 120000, 25),
(1, 'Cơm Rang Dưa Bò', 'Cơm rang dưa bò chuẩn vị', 60000, 40),
(1, 'Gà Quay Mật Ong', 'Nửa con gà quay', 140000, 20),
(3, 'Nước Ép Dưa Hấu', 'Dưa hấu tươi', 30000, 50),
(3, 'Bia Heineken', 'Bia lạnh', 25000, 100),
(3, 'Bia Tiger Bạc', 'Bia lạnh', 22000, 100),
(3, 'Trà Đào Cam Sả', 'Trà giải nhiệt', 35000, 40),
(3, 'Sinh Tố Xoài', 'Sinh tố đá xay', 40000, 30),
(4, 'Tiramisu', 'Bánh phô mai cà phê', 40000, 15),
(4, 'Trái Cây Theo Mùa', 'Đĩa trái cây tươi', 50000, 20),
(4, 'Kem Vani', 'Kem ly vani', 25000, 30),
(5, 'Combo Tình Nhân', 'Gồm 2 món chính, 2 nước, 1 salad', 350000, 10),
(5, 'Combo Gia Đình', 'Gồm 4 món chính, 4 nước, 2 khai vị', 650000, 10),
(6, 'Lẩu Thái Hải Sản', 'Lẩu chua cay cỡ lớn', 399000, 15),
(6, 'Lẩu Nấm Chim Câu', 'Lẩu thanh mát', 450000, 10),
(6, 'Bò Nướng Tảng', 'Bò nướng tảng kèm sốt', 250000, 20);

-- Thêm Bàn (15 Bàn với các sức chứa khác nhau)
INSERT INTO restaurant_tables (table_number, capacity, status) VALUES 
('T1', 2, 'available'), ('T2', 2, 'available'), ('T3', 2, 'occupied'), 
('T4', 4, 'occupied'), ('T5', 4, 'reserved'), ('T6', 4, 'available'), 
('T7', 4, 'available'), ('T8', 6, 'occupied'), ('T9', 6, 'available'), 
('T10', 8, 'reserved'), ('T11', 8, 'available'), ('T12', 10, 'maintenance'),
('VIP1', 12, 'available'), ('VIP2', 12, 'occupied'), ('VIP3', 20, 'available');

-- Thêm Đặt bàn (7 Lượt đặt bàn)
INSERT INTO reservations (customer_id, table_id, reservation_time, number_of_people, status, note) VALUES 
(1, 3, '2026-10-01 19:00:00', 2, 'completed', 'Khách đã đến'),
(2, 5, '2026-10-01 20:00:00', 4, 'confirmed', 'Gần cửa sổ'),
(4, 10, '2026-10-02 11:30:00', 8, 'pending', 'Đặt tiệc công ty'),
(6, 1, '2026-10-03 18:00:00', 2, 'cancelled', 'Khách bận đột xuất'),
(8, 14, '2026-10-01 19:30:00', 10, 'completed', 'Khách VIP, cần phục vụ riêng'),
(9, 7, '2026-10-04 19:00:00', 4, 'pending', 'Ăn tối gia đình'),
(10, 8, '2026-10-01 18:00:00', 6, 'completed', NULL);

--  Thêm Đơn hàng (10 Đơn hàng với các trạng thái khác nhau)
-- Các đơn hàng đã hoàn thành (completed)
INSERT INTO orders (id, customer_id, table_id, created_by, total_amount, status, note) VALUES 
(1, 3, 1, 7, 215000, 'completed', 'Khách ăn nhanh'),
(2, 8, 14, 8, 1250000, 'completed', 'Tiệc VIP'),
(3, 10, 8, 9, 878000, 'completed', NULL),
(4, 1, 3, 7, 450000, 'completed', NULL),
-- Các đơn hàng đang phục vụ / chờ nấu
(5, 5, 4, 10, 560000, 'cooking', 'Khách giục món lẩu'),
(6, 7, 2, 8, 290000, 'served', 'Khách đang ăn'),
(7, NULL, 6, 9, 130000, 'ready', 'Khách vãng lai, bếp đã làm xong'),
(8, NULL, 9, 7, 850000, 'pending', 'Vừa order xong'),
(9, 2, 5, 10, 399000, 'cancelled', 'Khách đổi ý không ăn nữa'),
(10, 4, 13, 8, 1500000, 'cooking', 'Giao hỏa tốc cho sếp');
-- Khôi phục sequence cho bảng orders
SELECT setval('orders_id_seq', 10);

-- Thêm Chi tiết Đơn hàng (Cho 10 đơn hàng trên)
INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity, subtotal) VALUES 
-- Đơn 1: 215k
(1, 4, 'Gỏi Ngó Sen', 55000, 1, 55000), (1, 6, 'Cá Hồi Áp Chảo', 180000, 1, 160000), -- Khuyến mãi giảm giá 20k
-- Đơn 2: 1250k (Tiệc VIP)
(2, 19, 'Combo Gia Đình', 650000, 1, 650000), (2, 21, 'Lẩu Nấm Chim Câu', 450000, 1, 450000), (2, 11, 'Bia Heineken', 25000, 6, 150000),
-- Đơn 3: 878k
(3, 20, 'Lẩu Thái Hải Sản', 399000, 1, 399000), (3, 22, 'Bò Nướng Tảng', 250000, 1, 250000), (3, 12, 'Bia Tiger Bạc', 22000, 10, 220000), (3, 15, 'Tiramisu', 40000, 1, 40000),
-- Đơn 4: 450k
(4, 18, 'Combo Tình Nhân', 350000, 1, 350000), (4, 16, 'Trái Cây Theo Mùa', 50000, 2, 100000),
-- Đơn 5: 560k
(5, 5, 'Beefsteak Thăn Bò', 150000, 2, 300000), (5, 9, 'Gà Quay Mật Ong', 140000, 1, 140000), (5, 13, 'Trà Đào Cam Sả', 35000, 2, 70000), (5, 16, 'Trái Cây Theo Mùa', 50000, 1, 50000),
-- Đơn 6: 290k
(6, 8, 'Cơm Rang Dưa Bò', 60000, 2, 120000), (6, 2, 'Súp Cua', 35000, 2, 70000), (6, 14, 'Sinh Tố Xoài', 40000, 2, 80000), (6, 3, 'Khoai Tây Chiên', 30000, 1, 30000),
-- Đơn 7: 130k
(7, 7, 'Mì Ý Hải Sản', 120000, 1, 120000), (7, 10, 'Nước Ép Dưa Hấu', 30000, 1, 30000),
-- Đơn 8: 850k
(8, 19, 'Combo Gia Đình', 650000, 1, 650000), (8, 22, 'Bò Nướng Tảng', 250000, 1, 200000), 
-- Đơn 9: Bị hủy
(9, 20, 'Lẩu Thái Hải Sản', 399000, 1, 399000),
-- Đơn 10: 1500k
(10, 21, 'Lẩu Nấm Chim Câu', 450000, 2, 900000), (10, 5, 'Beefsteak Thăn Bò', 150000, 4, 600000);

-- Thêm Thanh toán (Cho các đơn hàng có status 'completed')
INSERT INTO payments (order_id, cashier_id, amount, payment_method, status) VALUES 
(1, 3, 215000, 'cash', 'paid'),
(2, 4, 1250000, 'card', 'paid'),
(3, 3, 878000, 'banking', 'paid'),
(4, 4, 450000, 'e_wallet', 'paid');

-- Thêm Lịch sử trạng thái (Order_Status_History)
-- Log cho đơn hàng số 5 (Đang nấu)
INSERT INTO order_status_history (order_id, changed_by, old_status, new_status, note) VALUES 
(5, 10, NULL, 'pending', 'Tạo đơn hàng'),
(5, 10, 'pending', 'confirmed', 'Xác nhận'),
(5, 5, 'confirmed', 'cooking', 'Bếp trưởng tiếp nhận');

-- Log cho đơn hàng số 6 (Đã phục vụ)
INSERT INTO order_status_history (order_id, changed_by, old_status, new_status, note) VALUES 
(6, 8, NULL, 'pending', 'Tạo đơn'),
(6, 8, 'pending', 'confirmed', 'Xác nhận'),
(6, 6, 'confirmed', 'cooking', 'Bếp 2 nấu'),
(6, 6, 'cooking', 'ready', 'Đã xong món'),
(6, 8, 'ready', 'served', 'Phục vụ mang ra');

-- Log cho đơn hàng số 9 (Bị hủy)
INSERT INTO order_status_history (order_id, changed_by, old_status, new_status, note) VALUES 
(9, 10, NULL, 'pending', 'Tạo đơn'),
(9, 10, 'pending', 'cancelled', 'Khách báo hủy vì chờ lâu');