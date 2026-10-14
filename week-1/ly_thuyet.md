1. Database Cơ Bản (PostgreSQL / MySQL)
DDL & DML: Khởi tạo Schema và Dữ liệu Mẫu
SQL
-- 1. DDL: Tạo bảng
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(id) ON DELETE CASCADE,
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    version INT DEFAULT 0, -- Dùng cho Optimistic Locking
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. DML: Thêm dữ liệu mẫu
INSERT INTO customers (full_name, email) VALUES
('Nguyen Van A', 'a.nguyen@example.com'),
('Tran Thi B', 'b.tran@example.com'),
('Le Van C', 'c.le@example.com');

INSERT INTO orders (customer_id, total_amount, status, created_at) VALUES
(1, 150.00, 'COMPLETED', NOW() - INTERVAL '5 days'),
(1, 200.50, 'COMPLETED', NOW() - INTERVAL '3 days'),
(2, 50.00,  'CANCELLED', NOW() - INTERVAL '2 days'),
(2, 320.00, 'COMPLETED', NOW() - INTERVAL '1 day'),
(3, 80.00,  'PENDING',   NOW());

Truy vấn SQL Tổng hợp: JOIN, Aggregate, GROUP BY, HAVING, ORDER BY
Tính tổng chi tiêu của mỗi khách hàng cho các đơn hàng hoàn tất, chỉ lấy người chi từ 200 trở lên:
SQL
SELECT 
    c.id AS customer_id,
    c.full_name,
    COUNT(o.id) AS total_orders,
    SUM(o.total_amount) AS total_spent,
    ROUND(AVG(o.total_amount), 2) AS average_order_value
FROM customers c
INNER JOIN orders o ON c.id = o.customer_id
WHERE o.status = 'COMPLETED'
GROUP BY c.id, c.full_name
HAVING SUM(o.total_amount) >= 200.00
ORDER BY total_spent DESC;

2. Database Index
Khái niệm & Nguyên lý Hoạt động
Index là cấu trúc dữ liệu bổ trợ (phổ biến nhất là B-Tree) lưu trữ giá trị của một hoặc nhiều cột kèm con trỏ (pointer/RowID) trỏ tới dòng dữ liệu gốc trên đĩa. Nó giảm độ phức tạp tìm kiếm từ quét toàn bộ bảng (Full Table Scan - O(N)) xuống duyệt cây (Index Scan - O(N)).
Khi nào NÊN và KHÔNG NÊN đánh Index?
Trường hợp nên tạo Index
Trường hợp không nên tạo Index
Cột xuất hiện thường xuyên trong WHERE, JOIN, ORDER BY
Bảng quá nhỏ (vài chục đến vài trăm dòng)
Cột có độ phân tán cao (High Cardinality - UUID, email)
Cột có độ phân tán rất thấp (Low Cardinality - boolean, gender)
Bảng có tỷ lệ đọc áp đảo so với ghi (RW)
Bảng ghi/chèn liên tục (Index làm chậm INSERT/UPDATE/DELETE)

Demo Hiệu Năng Thực Tế
Tạo bảng 1 triệu dòng để kiểm chứng:
SQL
-- Tạo bảng log test
CREATE TABLE activity_logs (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    action VARCHAR(50),
    logged_at TIMESTAMP DEFAULT NOW()
);

-- Sinh 1,000,000 dòng ngẫu nhiên
INSERT INTO activity_logs (user_id, action, logged_at)
SELECT 
    (random() * 50000)::INT,
    'CLICK_BANNER',
    NOW() - (random() * interval '30 days')
FROM generate_series(1, 1000000);

Trước khi đánh Index:
SQL
EXPLAIN ANALYZE 
SELECT * FROM activity_logs WHERE user_id = 42890;

Kết quả kế hoạch thực thi:
Plaintext
Seq Scan on activity_logs  (cost=0.00..17906.00 rows=21 width=29) (actual time=11.452..84.215 rows=18 loops=1)
  Filter: (user_id = 42890)
  Rows Removed by Filter: 999982
Execution Time: 84.250 ms

Đánh Index B-Tree:
SQL
CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);

Sau khi đánh Index:
SQL
EXPLAIN ANALYZE 
SELECT * FROM activity_logs WHERE user_id = 42890;

Kết quả kế hoạch thực thi:
Plaintext
Bitmap Heap Scan on activity_logs  (cost=4.44..84.12 rows=21 width=29) (actual time=0.045..0.082 rows=18 loops=1)
  Recheck Cond: (user_id = 42890)
  ->  Bitmap Index Scan on idx_activity_logs_user_id  (cost=0.00..4.44 rows=21 width=0) (actual time=0.031..0.031 rows=18 loops=1)
Execution Time: 0.112 ms

Đánh giá: Thời gian thực thi giảm từ 84.25 ms xuống 0.11 ms (~750 lần nhanh hơn). Database chuyển từ quét toàn bộ 1 triệu bản ghi sang tìm trực tiếp trên cây chỉ mục.
3. Kỹ Thuật Phân Trang: Offset-based vs Cursor-based
Offset-based Pagination
Cơ chế: Dùng LIMIT và OFFSET.
Cú pháp:
SQL
SELECT * FROM orders ORDER BY id ASC LIMIT 20 OFFSET 100000;
Ưu điểm: Nhảy trang tùy ý (Page 1 -> Page 50), dễ tích hợp UI có thanh số trang.
Nhược điểm:
Kém hiệu năng ở trang sâu: Hệ thống phải đọc qua 100,000 dòng rồi mới lấy 20 dòng tiếp theo.
Dữ liệu bị trùng hoặc sót khi có bản ghi mới chèn vào giữa các lần tải.
Cursor-based Pagination (Keyset Pagination)
Cơ chế: Dùng một mốc giá trị duy nhất tuần tự (thường là id hoặc created_at) của phần tử cuối cùng ở trang trước để làm điều kiện lọc cho trang sau.
Cú pháp:
SQL
-- Lấy trang kế tiếp (với cursor là id của bản ghi cuối cùng = 100452)
SELECT * FROM orders 
WHERE id > 100452 
ORDER BY id ASC 
LIMIT 20;
Ưu điểm: Tận dụng triệt để B-Tree index (O(N)), tốc độ không đổi dù ở trang thứ nhất hay trang thứ 1 triệu; không bị trùng lặp dữ liệu khi có ghi mới.
Nhược điểm: Không thể nhảy trực tiếp đến trang N bất kỳ; chỉ hỗ trợ cuộn tiếp (Next/Previous hoặc Infinite Scroll như Facebook, TikTok).
4. Transaction & Database Locking
Thuộc Tính ACID
Atomicity (Nguyên tử): Tất cả câu lệnh trong transaction thành công, hoặc không có lệnh nào được áp dụng (Rollback hoàn toàn).
Consistency (Nhất quán): Dữ liệu luôn tuân thủ toàn vẹn ràng buộc schema, khóa ngoại, unique constraint trước và sau transaction.
Isolation (Cô lập): Các transaction chạy đồng thời không can thiệp lẫn nhau (được kiểm soát bởi các cấp độ: Read Uncommitted, Read Committed, Repeatable Read, Serializable).
Durability (Bền vững): Một khi đã Commit, dữ liệu được ghi nhận vào ổ đĩa và không bị mất ngay cả khi sập nguồn (nhờ WAL/Redo Log).
Demo Thực Tế: Xử Lý Chuyển Tiền Giữa 2 Tài Khoản
SQL
BEGIN;

-- 1. Trừ tiền tài khoản gửi
UPDATE accounts 
SET balance = balance - 500.00 
WHERE id = 1 AND balance >= 500.00;

-- 2. Kiểm tra nếu tài khoản gửi không đủ tiền, chủ động hủy
-- (Trên code backend sẽ kiểm tra số dòng bị ảnh hưởng)
-- IF affected_rows = 0 THEN ROLLBACK;

-- 3. Cộng tiền tài khoản nhận
UPDATE accounts 
SET balance = balance + 500.00 
WHERE id = 2;

-- 4. Ghi log giao dịch
INSERT INTO transfer_logs (from_account, to_account, amount) 
VALUES (1, 2, 500.00);
COMMIT;
Locking: Pessimistic vs Optimistic
1. Pessimistic Locking (Khóa Bi Quan)
Giữ khóa độc quyền trên dòng dữ liệu ngay từ lúc đọc, ngăn mọi transaction khác sửa đổi cho đến khi hoàn tất. Phù hợp với hệ thống tranh chấp cao (vé concert, Flash Sale số lượng cực ít).
Kịch bản 2 Transaction chạy đồng thời:
SQL
-- ===== TRANSACTION 1 =====
BEGIN;
-- Đặt khóa dòng id = 1 (Exclusive Lock)
SELECT id, total_amount, status 
FROM orders 
WHERE id = 1 
FOR UPDATE;

-- Giả lập xử lý nghiệp vụ trong 5 giây...
UPDATE orders SET total_amount = total_amount + 10 WHERE id = 1;
COMMIT;

SQL
-- ===== TRANSACTION 2 (Chạy ngay sau Transaction 1 vài ms) =====
BEGIN;
-- Lệnh này sẽ bị BLOCK và chuyển sang trạng thái chờ (Wait state)
SELECT id, total_amount, status 
FROM orders 
WHERE id = 1 
FOR UPDATE;
-- Transaction 2 chỉ được tiếp tục chạy sau khi Transaction 1 COMMIT hoặc ROLLBACK
UPDATE orders SET total_amount = total_amount + 20 WHERE id = 1;
COMMIT;

2. Optimistic Locking (Khóa Lạc Quan)
Không khóa dữ liệu ở tầng database khi đọc. Thay vào đó, dùng một cột version để phát hiện xung đột lúc cập nhật. Phù hợp với hệ thống đọc nhiều, tranh chấp thấp.
SQL
-- Cả 2 Transaction cùng đọc đơn hàng id = 1 (version hiện tại là 3)
SELECT id, total_amount, version FROM orders WHERE id = 1;

-- ===== TRANSACTION 1 UPDATE THÀNH CÔNG =====
UPDATE orders 
SET total_amount = 250.00, version = version + 1 
WHERE id = 1 AND version = 3;
-- Affected rows: 1 (version được nâng lên 4)

-- ===== TRANSACTION 2 UPDATE THẤT BẠI =====
UPDATE orders 
SET total_amount = 300.00, version = version + 1 
WHERE id = 1 AND version = 3;
-- Affected rows: 0 (vì version trong DB đã là 4, không còn là 3 nữa)
-- Backend bắt trường hợp rows == 0 -> throw OptimisticLockException -> Thử lại (Retry) hoặc báo lỗi người dùng

5. Lập Trình Hướng Đối Tượng (OOP) Trong Java
4 trụ cột lập trình hướng đối tượng:
Encapsulation (Đóng gói): Che giấu trạng thái nội tại thông qua private fields và cung cấp quyền truy cập qua getter/setter có validation.
Inheritance (Kế thừa): Tái sử dụng và mở rộng logic từ lớp cha (extends).
Polymorphism (Đa hình): Cùng một thông điệp nhưng hành vi thực thi khác nhau tại runtime (thông qua Override phương thức interface/lớp cha).
Abstraction (Trừu tượng): Tập trung vào việc đối tượng làm được gì thay vì đối tượng làm việc đó như thế nào (interface, abstract class).
Phân biệt Class vs Abstract Class vs Interface
Tiêu chí
Interface
Abstract Class
Concrete Class
Bản chất
Hợp đồng hành vi ("Can-do")
Khung xương lớp cơ sở ("Is-a")
Thực thể hoàn chỉnh có thể khởi tạo
Đa kế thừa
Một class implements được nhiều interface
Một class chỉ extends một class khác
Một class chỉ extends một class khác
Thuộc tính
Mặc định public static final
Có thể khai báo mọi phạm vi truy cập
Mọi phạm vi truy cập
Constructor
Không có
Có (dùng cho lớp con gọi super())
Có

Minh Họa Code Java OOP
Java
// 1. Abstraction qua Interface
public interface PaymentMethod {
    void pay(double amount);
}
// 2. Abstraction & Inheritance qua Abstract Class
public abstract class Account {
    // Encapsulation: private field
    private String accountNumber;
    protected double balance;

    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    // Abstract method bắt buộc lớp con triển khai
    public abstract void withdraw(double amount);
}

// 3. Concrete class triển khai Kế thừa và Interface (Đa hình)
public class SavingsAccount extends Account implements PaymentMethod {
    private static final double MIN_BALANCE = 50.0;

    public SavingsAccount(String accountNumber, double balance) {
        super(accountNumber, balance);
    }

    @Override
    public void withdraw(double amount) {
        if (balance - amount < MIN_BALANCE) {
            throw new IllegalArgumentException("Không thể rút vượt số dư tối thiểu");
        }
        balance -= amount;
    }

    @Override
    public void pay(double amount) {
        withdraw(amount);
        System.out.println("Thanh toán thành công: $" + amount + " từ tài khoản " + getAccountNumber());
    }
}

6. Dependency Inversion Principle (DIP), IoC & Dependency Injection (DI)
Khái Niệm Cốt Lõi
Inversion of Control (IoC - Đảo ngược điều khiển): Nguyên lý kiến trúc chuyển quyền kiểm soát luồng thực thi và vòng đời khởi tạo đối tượng từ code lập trình viên sang một bộ khung (Framework/Container).
Dependency Injection (DI): Một kỹ thuật/mô hình thực thi cụ thể của IoC. Thay vì một đối tượng tự khởi tạo phụ thuộc của nó (new Dependency()), phụ thuộc được truyền từ bên ngoài vào (qua Constructor, Setter, hoặc Field).
Minh Họa: Từ Khởi Tạo Truyền Thống Sang DI Bằng Java Thuần
1. Code Tightly Coupled (Khởi tạo trực tiếp - Vi phạm IoC/DI)
Java
public class NotificationService {
    // Phụ thuộc bị gắn cứng (tightly coupled) vào SMS
    private EmailService emailService = new EmailService();

    public void alert(String msg) {
        emailService.sendEmail(msg); // Muốn đổi sang SMS hay Slack phải sửa code lớp này
    }
}

2. Áp dụng DI qua Constructor (Java Thuần)
Java
// Bước 1: Trừu tượng hóa phụ thuộc
public interface MessageSender {
    void send(String message);
}

public class EmailSender implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

public class SmsSender implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("SMS: " + message);
    }
}

// Bước 2: Dependency Injection qua Constructor
public class NotificationService {
    private final MessageSender sender;

    // Dependency được "tiêm" vào từ bên ngoài
    public NotificationService(MessageSender sender) {
        this.sender = sender;
    }

    public void alert(String message) {
        sender.send(message);
    }
}

// Bước 3: Đóng vai trò IoC Container thủ công
public class MainApp {
    public static void main(String[] args) {
        MessageSender emailSender = new EmailSender();
        NotificationService emailNotification = new NotificationService(emailSender);
        emailNotification.alert("Hệ thống bảo trì!");

        // Linh hoạt chuyển đổi mà không cần sửa NotificationService
        MessageSender smsSender = new SmsSender();
        NotificationService smsNotification = new NotificationService(smsSender);
        smsNotification.alert("Mã OTP của bạn là 123456");
    }
}

Cách Spring Framework Tự Động Hóa IoC & DI
Spring Framework cung cấp ApplicationContext đóng vai trò là IoC Container chịu trách nhiệm quét, khởi tạo, liên kết các bean và quản lý vòng đời của chúng.
Java
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService implements MessageSender {
    @Override
    public void send(String message) {
        System.out.println("Spring managed Email: " + message);
    }
}

@Service
public class OrderNotificationService {
    private final MessageSender messageSender;

    // Spring tự động tìm Bean phù hợp trong Container và Inject vào đây
    @Autowired
    public OrderNotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void notifyOrderSuccess() {
        messageSender.send("Đơn hàng của bạn đã đặt thành công!");
    }
}

Lợi Ích Của IoC & DI
Loose Coupling: Giảm phụ thuộc trực tiếp giữa các module, thay đổi triển khai bên dưới không làm vỡ các module cấp cao.
Testability (Unit Test): Dễ dàng truyền các bản giả lập (Mock/Stub) vào service khi viết test bằng Mockito/JUnit mà không cần kết nối database hay bên thứ ba.
Maintainability: Tập trung quản lý cấu hình và vòng đời của đối tượng tại một nơi.