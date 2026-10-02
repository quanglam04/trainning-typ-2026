-- CREATE TABLE
CREATE TABLE nha_xuat_ban(
	ma_nxb INT AUTO_INCREMENT PRIMARY KEY,
    ten_nxb varchar(50) not null,
    dia_chi varchar(300),
    sdt varchar(20) 
);
Create table sach(
	ma_sach INT AUTO_INCREMENT PRIMARY KEY,
    ten_sach varchar(300) not null,
    ten_tac_gia varchar(300) not null,
    the_loai varchar(100), 
    ma_nxb int not null,
    ma_kho int not null,
    tg_xb datetime not null
);
CREATE TABLE thu_thu (
    ma_thu_thu    INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten        VARCHAR(100) NOT NULL,
    so_dien_thoai VARCHAR(15),
    email         VARCHAR(120) UNIQUE,
    ngay_vao_lam  DATE
);
CREATE TABLE nguoi_muon (
    ma_nguoi_muon INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten        VARCHAR(100) NOT NULL,
    ngay_sinh     DATE,
    gioi_tinh     ENUM('Nam','Nữ','Khác'),
    dia_chi       VARCHAR(255),
    sdt VARCHAR(15),
    email         VARCHAR(120) UNIQUE
);
CREATE TABLE phieu_muon (
    ma_phieu      INT AUTO_INCREMENT PRIMARY KEY,
    ma_nguoi_muon INT NOT NULL,
    ma_thu_thu    INT NOT NULL,
    ngay_muon     DATE NOT NULL,
    han_tra       DATE NOT NULL,
    ngay_tra      DATE NULL,
    FOREIGN KEY (ma_nguoi_muon) REFERENCES nguoi_muon(ma_nguoi_muon),
    FOREIGN KEY (ma_thu_thu)    REFERENCES thu_thu(ma_thu_thu)
);
CREATE TABLE chi_tiet_phieu_muon (
    ma_phieu INT,
    ma_sach  INT,
    PRIMARY KEY (ma_phieu, ma_sach),
    FOREIGN KEY (ma_phieu) REFERENCES phieu_muon(ma_phieu),
    FOREIGN KEY (ma_sach)  REFERENCES sach(ma_sach)
);
CREATE TABLE phieu_phat (
    ma_phat       INT AUTO_INCREMENT PRIMARY KEY,
    ma_phieu      INT NOT NULL,
    ma_thu_thu    INT NOT NULL,
    ngay_lap      DATE NOT NULL,
    li_do	      varchar(300) not null,
    FOREIGN KEY (ma_phieu)   REFERENCES phieu_muon(ma_phieu),
    FOREIGN KEY (ma_thu_thu) REFERENCES thu_thu(ma_thu_thu)
);
CREATE TABLE kho_sach (
    ma_kho   INT AUTO_INCREMENT PRIMARY KEY,
	ma_thu_thu INT not null,
    FOREIGN KEY(ma_thu_thu) references thu_thu(ma_thu_thu)
);
-- INSERT

INSERT INTO thu_thu (ho_ten, so_dien_thoai, email, ngay_vao_lam) VALUES
('Nguyễn Minh Anh', '0901234001', 'minhanh01@gmail.com', '2021-03-15'),
('Trần Quốc Bảo', '0901234002', 'quocbao02@gmail.com', '2020-07-20'),
('Lê Hoàng Nam', '0901234003', 'hoangnam03@gmail.com', '2022-01-10'),
('Phạm Thùy Linh', '0901234004', 'thuylinh04@gmail.com', '2021-11-05'),
('Võ Thành Trung', '0901234005', 'thanhtrung05@gmail.com', '2019-06-18'),
('Đặng Ngọc Hà', '0901234006', 'ngocha06@gmail.com', '2023-02-12'),
('Bùi Đức Long', '0901234007', 'duclong07@gmail.com', '2020-09-01'),
('Ngô Khánh Vy', '0901234008', 'khanhvy08@gmail.com', '2022-05-25'),
('Dương Gia Huy', '0901234009', 'giahuy09@gmail.com', '2023-08-14'),
('Hoàng Mai Phương', '0901234010', 'maiphuong10@gmail.com', '2021-12-20'),
('Đỗ Quang Hưng', '0901234011', 'quanghung11@gmail.com', '2018-10-30'),
('Mai Thanh Tùng', '0901234012', 'thanhtung12@gmail.com', '2022-09-16');

INSERT INTO nguoi_muon
(ho_ten, ngay_sinh, gioi_tinh, dia_chi, sdt, email) VALUES
('Nguyễn Văn Khang', '2004-05-12', 'Nam', 'Hà Nội', '0911000001', 'khang01@gmail.com'),
('Trần Thị Mai', '2003-08-21', 'Nữ', 'Hải Phòng', '0911000002', 'mai02@gmail.com'),
('Lê Đức Anh', '2005-02-14', 'Nam', 'Nam Định', '0911000003', 'ducanh03@gmail.com'),
('Phạm Ngọc Linh', '2004-11-09', 'Nữ', 'Thái Bình', '0911000004', 'ngoclinh04@gmail.com'),
('Vũ Minh Tuấn', '2002-06-30', 'Nam', 'Ninh Bình', '0911000005', 'minhtuan05@gmail.com'),
('Đặng Thu Trang', '2003-09-17', 'Nữ', 'Hà Nam', '0911000006', 'thutrang06@gmail.com'),
('Bùi Quốc Việt', '2004-01-25', 'Nam', 'Hà Nội', '0911000007', 'quocviet07@gmail.com'),
('Hoàng Khánh Ngân', '2005-07-11', 'Nữ', 'Bắc Ninh', '0911000008', 'khanhngan08@gmail.com'),
('Đỗ Thành Đạt', '2003-03-19', 'Nam', 'Hải Dương', '0911000009', 'thanhdat09@gmail.com'),
('Ngô Thùy Dương', '2004-12-01', 'Nữ', 'Quảng Ninh', '0911000010', 'thuyduong10@gmail.com'),
('Dương Nhật Minh', '2002-10-08', 'Nam', 'Vĩnh Phúc', '0911000011', 'nhatminh11@gmail.com'),
('Mai Hoài An', '2005-04-23', 'Khác', 'Hà Nội', '0911000012', 'hoaian12@gmail.com'),
('Phan Minh Quân', '2004-02-11', 'Nam', 'Hà Nội', '0911000013', 'minhquan13@gmail.com'),
('Nguyễn Thảo My', '2005-06-24', 'Nữ', 'Hải Phòng', '0911000014', 'thaomy14@gmail.com'),
('Trần Gia Bảo', '2003-12-17', 'Nam', 'Bắc Ninh', '0911000015', 'giabao15@gmail.com');

INSERT INTO nha_xuat_ban
(ten_nxb, dia_chi, sdt) VALUES
('NXB Kim Đồng', '55 Quang Trung, Hà Nội', '02411110001'),
('NXB Giáo dục Việt Nam', '81 Trần Hưng Đạo, Hà Nội', '02411110002'),
('NXB Trẻ', '161B Lý Chính Thắng, TP.HCM', '02811110003'),
('NXB Văn Học', '18 Nguyễn Trường Tộ, Hà Nội', '02411110004'),
('NXB Lao Động', '175 Giảng Võ, Hà Nội', '02411110005'),
('NXB Khoa Học Kỹ Thuật', '70 Trần Hưng Đạo, Hà Nội', '02411110006'),
('NXB Tổng Hợp TP.HCM', '62 Nguyễn Thị Minh Khai, TP.HCM', '02811110007'),
('NXB Đại Học Quốc Gia Hà Nội', '144 Xuân Thủy, Hà Nội', '02411110008'),
('NXB Phụ Nữ Việt Nam', '39 Hàng Chuối, Hà Nội', '02411110009'),
('NXB Thanh Niên', '64 Bà Triệu, Hà Nội', '02411110010'),
('NXB Công Thương', '655 Phạm Văn Đồng, Hà Nội', '02411110011'),
('NXB Thống Kê', '86 Nguyễn Thái Học, Hà Nội', '02411110012');

INSERT INTO kho_sach (ma_thu_thu) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10),
(11),
(12);

INSERT INTO sach
(ten_sach, ten_tac_gia, the_loai, ma_nxb, ma_kho, tg_xb) VALUES
('Tuổi Trẻ Đáng Giá Bao Nhiêu',
 'Rosie Nguyễn',
 'Kỹ năng sống',
 1, 1, '2018-03-15 00:00:00'),

('Dế Mèn Phiêu Lưu Ký',
 'Tô Hoài',
 'Văn học thiếu nhi',
 1, 2, '2019-06-20 00:00:00'),

('Nhà Giả Kim',
 'Paulo Coelho',
 'Tiểu thuyết',
 3, 3, '2020-01-12 00:00:00'),

('Đắc Nhân Tâm',
 'Dale Carnegie',
 'Tâm lý - Kỹ năng',
 4, 4, '2017-08-10 00:00:00'),

('Clean Code',
 'Robert C. Martin',
 'Công nghệ thông tin',
 6, 5, '2008-08-01 00:00:00'),

('Introduction to Algorithms',
 'Thomas H. Cormen',
 'Công nghệ thông tin',
 8, 6, '2022-09-15 00:00:00'),

('Cơ Sở Dữ Liệu',
 'Nguyễn Tuấn Anh',
 'Giáo trình',
 8, 7, '2021-04-10 00:00:00'),

('Lập Trình Java Căn Bản',
 'Trần Minh Đức',
 'Lập trình',
 10, 8, '2020-11-21 00:00:00'),

('Harry Potter Và Hòn Đá Phù Thủy',
 'J. K. Rowling',
 'Fantasy',
 3, 9, '2016-07-15 00:00:00'),

('Những Người Khốn Khổ',
 'Victor Hugo',
 'Văn học kinh điển',
 4, 10, '2015-05-18 00:00:00'),

('Sapiens: Lược Sử Loài Người',
 'Yuval Noah Harari',
 'Lịch sử',
 7, 11, '2021-10-05 00:00:00'),

('Tư Duy Nhanh Và Chậm',
 'Daniel Kahneman',
 'Tâm lý học',
 9, 12, '2019-02-25 00:00:00');
 
 INSERT INTO phieu_muon
(ma_nguoi_muon, ma_thu_thu, ngay_muon, han_tra, ngay_tra) VALUES
(1, 1, '2026-08-01', '2026-08-15', '2026-08-13'),
(2, 2, '2026-08-03', '2026-08-17', '2026-08-18'),
(3, 3, '2026-08-05', '2026-08-19', NULL),
(4, 4, '2026-08-08', '2026-08-22', '2026-08-21'),
(5, 5, '2026-08-12', '2026-08-26', '2026-08-30'),
(6, 6, '2026-08-15', '2026-08-29', NULL),
(7, 7, '2026-08-18', '2026-09-01', '2026-08-31'),
(8, 8, '2026-08-20', '2026-09-03', '2026-09-02'),
(9, 9, '2026-08-23', '2026-09-06', NULL),
(10, 10, '2026-08-25', '2026-09-08', '2026-09-10'),
(11, 11, '2026-08-28', '2026-09-11', '2026-09-09'),
(12, 12, '2026-09-01', '2026-09-15', NULL),
(1, 3, '2026-09-10', '2026-09-24', NULL);

INSERT INTO chi_tiet_phieu_muon (ma_phieu, ma_sach) VALUES
(1, 1),
(1, 2),

(2, 3),
(2, 4),

(3, 5),
(3, 6),
(3, 7),

(4, 8),

(5, 9),
(5, 10),

(6, 11),
(6, 12),

(7, 1),
(7, 5),

(8, 2),
(8, 8),

(9, 3),
(9, 6),
(9, 9),

(10, 4),

(11, 7),
(11, 10),

(12, 11),

(13, 1),
(13, 3),
(13, 5),
(13, 7),
(13, 9);
INSERT INTO phieu_phat
(ma_phieu, ma_thu_thu, ngay_lap, li_do) VALUES
(2, 2, '2026-08-18', 'Trả sách quá hạn 1 ngày'),
(5, 5, '2026-08-30', 'Trả sách quá hạn 4 ngày'),
(7, 7, '2026-09-01', 'Trả sách quá hạn 0 ngày nhưng sách bị hư hỏng nhẹ'),
(10, 10, '2026-09-10', 'Trả sách quá hạn 2 ngày'),
(3, 3, '2026-09-05', 'Làm mất một cuốn sách'),
(6, 6, '2026-09-02', 'Làm hỏng sách'),
(9, 9, '2026-09-08', 'Trả sách quá hạn'),
(12, 12, '2026-09-20', 'Sách bị rách nhiều trang'),
(4, 4, '2026-08-21', 'Sách bị ướt'),
(8, 8, '2026-09-02', 'Trả sách có dấu hiệu hư hỏng'),
(1, 1, '2026-08-13', 'Làm mất phụ kiện kèm theo sách'),
(11, 11, '2026-09-09', 'Trả sách trễ hạn');



