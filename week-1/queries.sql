-- TAO BANG

create table nguoidung(
	nguoidung_id int primary key auto_increment,
    nickname varchar(50) not null unique,
    sodutaikhoan decimal(12, 2) not null default 0,
    hanghoivien varchar(20) not null default "DONG"
);

create table maytinh(
	maytinh_id int primary key auto_increment,
    khuvuc varchar(50) not null,
    giatien decimal (10, 2) not null,
    trangthai varchar(20) not null default "TRONG"
);

create table thuemay(
	thuemay_id int primary key auto_increment,
    nguoidung_id int not null,
    maytinh_id int not null,
    start_time datetime not null default current_timestamp,
    end_time datetime null,
    tongtien DECIMAL(12, 2) NOT NULL DEFAULT 0,
    foreign key (nguoidung_id) references nguoidung(nguoidung_id),
    foreign key (maytinh_id) references maytinh(maytinh_id)
);

--------------------------------------------------------------------------
-- THEM GIA TRI

INSERT INTO nguoidung (nickname, sodutaikhoan, hanghoivien) VALUES
('yasuo_gank_tem', 50000, 'DONG'),
('faker_ha_noi', 250000, 'KIM CUONG'),
('chua_hm', 20000, 'DONG'),
('streamer_no1', 120000, 'VANG'),
('khach_vang_lai', 10000, 'DONG');

INSERT INTO maytinh (khuvuc, giatien, trangthai) VALUES
('Khu Thuong', 8000, 'DANG_SU_DUNG'),
('Khu Thuong', 8000, 'TRONG'),
('Khu VIP', 12000, 'DANG_SU_DUNG'),
('Khu VIP', 12000, 'TRONG'),
('Khu Thi Dau', 20000, 'DANG_SU_DUNG'),
('Khu Thi Dau', 20000, 'BAO_TRI');

INSERT INTO thuemay (nguoidung_id, maytinh_id, start_time, end_time, tongtien) VALUES
-- Các phiên đã chơi xong và thanh toán
(1, 1, '2026-10-01 08:00:00', '2026-10-01 10:00:00', 16000),
(2, 5, '2026-10-01 09:00:00', '2026-10-01 14:00:00', 100000),
(1, 3, '2026-10-01 19:00:00', '2026-10-01 22:00:00', 36000),
(4, 3, '2026-10-02 08:00:00', '2026-10-02 11:00:00', 36000),
-- Các phiên ĐANG NGỒI CHƠI (end_time = NULL, tongtien = 0)
(1, 1, '2026-10-02 18:00:00', NULL, 0),
(3, 3, '2026-10-02 19:30:00', NULL, 0),
(2, 5, '2026-10-02 20:00:00', NULL, 0);

--------------------------------------------------------------------------
-- THAY DOI BANG

alter table nguoidung
add column sdt varchar(15) null;

select * from nguoidung

--------------------------------------------------------------------------
-- DROP COT

alter table nguoidung
drop column sdt

update maytinh
set giatien = 5000.00
where khuvuc = "Khu Thuong";

select * from nguoidung

--------------------------------------------------------------------------
-- THEM GIA TRI

insert into nguoidung (nickname, sodutaikhoan, hanghoivien)
values ("haibeo", 36000.00, "KIM CUONG");

select * from nguoidung

--------------------------------------------------------------------------
-- XOA BAN GHI

delete from nguoidung
where nguoidung_id = 6;

select * from nguoidung;

create table tmp(
	id int primary key,
    ten varchar(50)
);

select * from tmp;


--------------------------------------------------------------------------
-- XOA BANG

drop table tmp

--------------------------------------------------------------------------
-- WHERE

select *
from nguoidung
where sodutaikhoan >= 200000;

--------------------------------------------------------------------------
-- ORDER BY

select *
from nguoidung
order by sodutaikhoan desc;

--------------------------------------------------------------------------
-- INNER JOIN

select maytinh.maytinh_id, maytinh.khuvuc, nguoidung.nickname as nguoidangchoi, thuemay.start_time
from thuemay
inner join nguoidung on thuemay.nguoidung_id = nguoidung.nguoidung_id
inner join maytinh on thuemay.maytinh_id = maytinh.maytinh_id
where thuemay.end_time is null;

--------------------------------------------------------------------------
-- LEFT JOIN

select maytinh.maytinh_id, maytinh.khuvuc, nguoidung.nickname as nguoidangchoi, thuemay.start_time
from maytinh
left join thuemay on thuemay.maytinh_id = maytinh.maytinh_id and thuemay.end_time is null
left join nguoidung on thuemay.nguoidung_id = nguoidung.nguoidung_id

--------------------------------------------------------------------------
-- RIGHT JOIN

select nguoidung.nguoidung_id,nguoidung.nickname, nguoidung.sodutaikhoan, thuemay.thuemay_id, thuemay.start_time
from thuemay
right join nguoidung on thuemay.nguoidung_id = nguoidung.nguoidung_id;

--------------------------------------------------------------------------
-- Query VA Aggregate functions
select maytinh.khuvuc, count(thuemay.thuemay_id) as so_luot_choi, sum(thuemay.tongtien) as tong_doanh_thu, 
		avg(thuemay.tongtien) as so_tien_choi_trung_binh, min(thuemay.tongtien) as so_tien_choi_it_nhat, 
        max(thuemay.tongtien) as so_tien_choi_nhieu_nhat
from maytinh
inner join thuemay on thuemay.maytinh_id = maytinh.maytinh_id
where thuemay.end_time is not null
group by maytinh.khuvuc
having sum(thuemay.tongtien) > 20000
order by tong_doanh_thu desc

--------------------------------------------------------------------------
-- TRAINSACTION ROLLBACK

start transaction;
update nguoidung 
set sodutaikhoan = sodutaikhoan - 50000 
where nguoidung_id = 2;
select * from nguoidung
where nguoidung_id = 2;
rollback;

select * from nguoidung
where nguoidung_id = 2

--------------------------------------------------------------------------
-- TRAINSACTION COMMIT

start transaction;
update nguoidung 
set sodutaikhoan = sodutaikhoan - 50000 
where nguoidung_id = 2;
select * from nguoidung
where nguoidung_id = 2;
commit;

select * from nguoidung
where nguoidung_id = 2

--------------------------------------------------------------------------
-- PHAN TRANG BANG OFFSET

select * from thuemay 
order by thuemay_id asc
limit 0, 3;

--------------------------------------------------------------------------
-- INDEX

create index idx_thuemay_starttime on thuemay(start_time);
