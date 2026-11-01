IF DB_ID(N'EShopping') IS NULL CREATE DATABASE EShopping;
GO
USE EShopping;
GO
IF OBJECT_ID('ThuEmail','U') IS NOT NULL DROP TABLE ThuEmail;
IF OBJECT_ID('ChiTietDonHang','U') IS NOT NULL DROP TABLE ChiTietDonHang;
IF OBJECT_ID('DonDatHang','U') IS NOT NULL DROP TABLE DonDatHang;
IF OBJECT_ID('LoaiThe','U') IS NOT NULL DROP TABLE LoaiThe;
IF OBJECT_ID('BangPhiGiao','U') IS NOT NULL DROP TABLE BangPhiGiao;
IF OBJECT_ID('LoaiGiaoHang','U') IS NOT NULL DROP TABLE LoaiGiaoHang;
IF OBJECT_ID('TinhThanh','U') IS NOT NULL DROP TABLE TinhThanh;
IF OBJECT_ID('KhuVucGiao','U') IS NOT NULL DROP TABLE KhuVucGiao;
IF OBJECT_ID('NhanVien','U') IS NOT NULL DROP TABLE NhanVien;
IF OBJECT_ID('KhachHang','U') IS NOT NULL DROP TABLE KhachHang;
IF OBJECT_ID('HinhAnhSanPham','U') IS NOT NULL DROP TABLE HinhAnhSanPham;
IF OBJECT_ID('SanPham','U') IS NOT NULL DROP TABLE SanPham;
IF OBJECT_ID('NhomSanPham','U') IS NOT NULL DROP TABLE NhomSanPham;
GO

/* ---------- Dữ liệu sản phẩm (đồng bộ / đọc từ Hệ thống quản lý sản phẩm) ---------- */
CREATE TABLE NhomSanPham(
    MaNhom varchar(20) NOT NULL PRIMARY KEY,
    TenNhom nvarchar(100) NOT NULL UNIQUE
);
CREATE TABLE SanPham(
    MaSP varchar(20) NOT NULL PRIMARY KEY,
    MaNhom varchar(20) NOT NULL,
    TenSP nvarchar(200) NOT NULL,
    NhaSanXuat nvarchar(120) NOT NULL,
    MoTa nvarchar(2000) NULL,
    ThongSoKyThuat nvarchar(2000) NULL,
    GiaBan decimal(18,0) NOT NULL CHECK(GiaBan >= 0),                  -- giá hiện hành, có thể thay đổi
    TinhTrang nvarchar(20) NOT NULL DEFAULT N'Còn hàng',
    CONSTRAINT CK_SanPham_TinhTrang CHECK(TinhTrang IN (N'Còn hàng',N'Hết hàng')),
    CONSTRAINT FK_SanPham_Nhom FOREIGN KEY(MaNhom) REFERENCES NhomSanPham(MaNhom)
);
CREATE TABLE HinhAnhSanPham(
    MaHinh int IDENTITY(1,1) NOT NULL PRIMARY KEY,
    MaSP varchar(20) NOT NULL,
    DuongDan varchar(260) NOT NULL,
    ThuTu int NOT NULL DEFAULT 1,
    CONSTRAINT FK_HinhAnh_SanPham FOREIGN KEY(MaSP) REFERENCES SanPham(MaSP)
);

/* ---------- Tài khoản ---------- */
CREATE TABLE KhachHang(
    MaKH varchar(20) NOT NULL PRIMARY KEY,
    HoTen nvarchar(120) NOT NULL,
    NgaySinh date NOT NULL,
    SoCMND varchar(20) NOT NULL UNIQUE,                                 -- CMND / CCCD / Passport
    DiaChi nvarchar(250) NOT NULL,
    DienThoai varchar(20) NOT NULL,
    TenDangNhap varchar(50) NOT NULL UNIQUE,
    MatKhau varchar(100) NOT NULL,                                      -- {bcrypt}...
    Email varchar(150) NULL,                                            -- không bắt buộc
    NgayDangKy datetime NOT NULL DEFAULT GETDATE()
);
CREATE TABLE NhanVien(
    TenDangNhap varchar(50) NOT NULL PRIMARY KEY,
    MatKhau varchar(100) NOT NULL,
    HoTen nvarchar(120) NOT NULL
);

/* ---------- Giao hàng ---------- */
CREATE TABLE KhuVucGiao(
    MaKV varchar(20) NOT NULL PRIMARY KEY,
    TenKV nvarchar(100) NOT NULL
);
CREATE TABLE TinhThanh(
    MaTinh varchar(20) NOT NULL PRIMARY KEY,
    TenTinh nvarchar(100) NOT NULL,
    MaKV varchar(20) NOT NULL,
    CONSTRAINT FK_Tinh_KV FOREIGN KEY(MaKV) REFERENCES KhuVucGiao(MaKV)
);
CREATE TABLE LoaiGiaoHang(                                              -- 3 loại phiếu đặt hàng
    MaLoai varchar(20) NOT NULL PRIMARY KEY,
    TenLoai nvarchar(100) NOT NULL,
    SoNgayXuLy int NOT NULL CHECK(SoNgayXuLy >= 0),
    NguongMienPhi decimal(18,0) NULL                                    -- tổng hàng >= ngưỡng thì miễn phí giao
);
CREATE TABLE BangPhiGiao(
    MaKV varchar(20) NOT NULL,
    MaLoai varchar(20) NOT NULL,
    Phi decimal(18,0) NOT NULL CHECK(Phi >= 0),
    PRIMARY KEY(MaKV, MaLoai),
    CONSTRAINT FK_Phi_KV FOREIGN KEY(MaKV) REFERENCES KhuVucGiao(MaKV),
    CONSTRAINT FK_Phi_Loai FOREIGN KEY(MaLoai) REFERENCES LoaiGiaoHang(MaLoai)
);

/* ---------- Thẻ tín dụng ---------- */
CREATE TABLE LoaiThe(
    MaLoaiThe varchar(20) NOT NULL PRIMARY KEY,
    TenLoaiThe nvarchar(50) NOT NULL,
    SoChuSo int NOT NULL,                                               -- Visa/Master/Discover 16, AmEx 15
    SoChuSoCSV int NOT NULL,                                            -- Visa/Master/Discover 3, AmEx 4
    PhiGiaoDich decimal(18,0) NOT NULL CHECK(PhiGiaoDich >= 0)          -- lệ phí mỗi lần thanh toán
);

/* ---------- Đơn đặt hàng ---------- */
CREATE TABLE DonDatHang(
    SoDon varchar(30) NOT NULL PRIMARY KEY,
    MaKH varchar(20) NOT NULL,
    NgayDat datetime NOT NULL,
    MaLoaiGiao varchar(20) NOT NULL,
    DuKienGiao date NOT NULL,
    TenNguoiNhan nvarchar(120) NOT NULL,                                -- người nhận có thể khác người mua
    DiaChiNhan nvarchar(250) NOT NULL,
    MaTinh varchar(20) NOT NULL,
    DienThoaiNhan varchar(20) NOT NULL,
    TongTienHang decimal(18,0) NOT NULL CHECK(TongTienHang > 0),
    PhiGiaoHang decimal(18,0) NOT NULL CHECK(PhiGiaoHang >= 0),
    MaLoaiThe varchar(20) NOT NULL,
    PhiThe decimal(18,0) NOT NULL CHECK(PhiThe >= 0),
    TongThanhToan AS (TongTienHang + PhiGiaoHang + PhiThe) PERSISTED,
    SoTheAn varchar(25) NOT NULL,                                       -- **** **** **** 1234
    TenChuThe nvarchar(120) NOT NULL,
    ThangHetHan varchar(7) NOT NULL,                                    -- yyyy-MM
    MaGiaoDich varchar(50) NOT NULL,
    TrangThai nvarchar(20) NOT NULL DEFAULT N'Đã đặt',
    CONSTRAINT CK_Don_TrangThai CHECK(TrangThai IN (N'Đã đặt',N'Đang xử lý',N'Đang giao',N'Hoàn tất',N'Hủy')),
    CONSTRAINT FK_Don_KH FOREIGN KEY(MaKH) REFERENCES KhachHang(MaKH),
    CONSTRAINT FK_Don_LoaiGiao FOREIGN KEY(MaLoaiGiao) REFERENCES LoaiGiaoHang(MaLoai),
    CONSTRAINT FK_Don_Tinh FOREIGN KEY(MaTinh) REFERENCES TinhThanh(MaTinh),
    CONSTRAINT FK_Don_LoaiThe FOREIGN KEY(MaLoaiThe) REFERENCES LoaiThe(MaLoaiThe)
);
CREATE TABLE ChiTietDonHang(
    SoDon varchar(30) NOT NULL,
    MaSP varchar(20) NOT NULL,
    SoLuong int NOT NULL CHECK(SoLuong > 0),
    DonGia decimal(18,0) NOT NULL CHECK(DonGia >= 0),                  -- giá tại thời điểm đặt
    PRIMARY KEY(SoDon, MaSP),
    CONSTRAINT FK_CTDH_Don FOREIGN KEY(SoDon) REFERENCES DonDatHang(SoDon),
    CONSTRAINT FK_CTDH_SP FOREIGN KEY(MaSP) REFERENCES SanPham(MaSP)
);
CREATE TABLE ThuEmail(
    MaThu int IDENTITY(1,1) NOT NULL PRIMARY KEY,
    SoDon varchar(30) NOT NULL,
    DenEmail varchar(150) NOT NULL,
    TieuDe nvarchar(200) NOT NULL,
    NoiDung nvarchar(max) NOT NULL,                                     -- không chứa thông tin thẻ
    ThoiDiemGui datetime NOT NULL,
    CONSTRAINT FK_Thu_Don FOREIGN KEY(SoDon) REFERENCES DonDatHang(SoDon)
);
GO
CREATE INDEX IX_SanPham_Nhom ON SanPham(MaNhom);
CREATE INDEX IX_Don_KH ON DonDatHang(MaKH, NgayDat);
GO

/* ------------------------- DỮ LIỆU MẪU ------------------------- */
INSERT INTO NhomSanPham(MaNhom,TenNhom) VALUES
('MAYANH',N'Máy chụp hình kỹ thuật số'),('DOCHOI',N'Đồ chơi'),('GIADUNG',N'Thiết bị điện gia dụng'),('MAYTINH',N'Thiết bị máy tính');
INSERT INTO SanPham(MaSP,MaNhom,TenSP,NhaSanXuat,MoTa,ThongSoKyThuat,GiaBan,TinhTrang) VALUES
('SP001','MAYANH',N'Máy ảnh compact Z50',N'Nikon',N'Máy ảnh nhỏ gọn cho du lịch.',N'20.9 MP; zoom 5x; Wi-Fi',8900000,N'Còn hàng'),
('SP002','MAYANH',N'Máy ảnh mirrorless A6400',N'Sony',N'Lấy nét nhanh, quay 4K.',N'24.2 MP; 4K30p; 425 điểm AF',19500000,N'Còn hàng'),
('SP003','DOCHOI',N'Bộ xếp hình thành phố',N'Lego',N'Bộ 500 chi tiết cho bé 6+.',N'500 chi tiết; nhựa ABS',890000,N'Còn hàng'),
('SP004','DOCHOI',N'Gấu bông Noel 40cm',N'Teddy',N'Quà Giáng Sinh mềm mại.',N'40 cm; bông PP',250000,N'Còn hàng'),
('SP005','DOCHOI',N'Xe điều khiển địa hình',N'Maisto',N'Xe RC tỉ lệ 1:16.',N'Pin sạc; 2.4 GHz',650000,N'Hết hàng'),
('SP006','GIADUNG',N'Nồi chiên không dầu 5L',N'Philips',N'Chiên giòn ít dầu.',N'1700 W; 5 lít',2490000,N'Còn hàng'),
('SP007','GIADUNG',N'Máy xay sinh tố',N'Panasonic',N'Cối thủy tinh 1.5L.',N'600 W; 2 tốc độ',1190000,N'Còn hàng'),
('SP008','MAYTINH',N'Chuột không dây M720',N'Logitech',N'Kết nối 3 thiết bị.',N'Bluetooth + USB; pin AA',790000,N'Còn hàng'),
('SP009','MAYTINH',N'Bàn phím cơ K8',N'Keychron',N'Bàn phím cơ 87 phím.',N'Switch Brown; Bluetooth 5.1',2100000,N'Còn hàng');
INSERT INTO HinhAnhSanPham(MaSP,DuongDan,ThuTu) VALUES
('SP001','/img/sp/SP001.svg',1),('SP002','/img/sp/SP002.svg',1),('SP003','/img/sp/SP003.svg',1),('SP004','/img/sp/SP004.svg',1),
('SP005','/img/sp/SP005.svg',1),('SP006','/img/sp/SP006.svg',1),('SP007','/img/sp/SP007.svg',1),('SP008','/img/sp/SP008.svg',1),
('SP009','/img/sp/SP009.svg',1);

INSERT INTO KhuVucGiao(MaKV,TenKV) VALUES ('NOITHANH',N'Nội thành TP.HCM'),('MIENNAM',N'Các tỉnh phía Nam'),('MIENBAC',N'Miền Trung – Bắc');
INSERT INTO TinhThanh(MaTinh,TenTinh,MaKV) VALUES
('HCM',N'TP. Hồ Chí Minh','NOITHANH'),('BD',N'Bình Dương','MIENNAM'),('CT',N'Cần Thơ','MIENNAM'),
('DN',N'Đà Nẵng','MIENBAC'),('HN',N'Hà Nội','MIENBAC');
-- Quy tắc của đề: tổng >= 1.000.000 đ miễn phí chuyển phát nhanh; >= 5.000.000 đ miễn phí chuyển phát nhanh trong ngày.
-- Quyết định triển khai: phiếu thường cũng miễn phí từ 1.000.000 đ (không đắt hơn chuyển phát nhanh).
INSERT INTO LoaiGiaoHang(MaLoai,TenLoai,SoNgayXuLy,NguongMienPhi) VALUES
('THUONG',N'Phiếu đặt hàng thường',5,1000000),
('CPN',N'Chuyển phát nhanh',2,1000000),
('CPN_NGAY',N'Chuyển phát nhanh trong ngày',0,5000000);
INSERT INTO BangPhiGiao(MaKV,MaLoai,Phi) VALUES
('NOITHANH','THUONG',15000),('NOITHANH','CPN',30000),('NOITHANH','CPN_NGAY',60000),
('MIENNAM','THUONG',30000),('MIENNAM','CPN',50000),('MIENNAM','CPN_NGAY',120000),
('MIENBAC','THUONG',40000),('MIENBAC','CPN',70000),('MIENBAC','CPN_NGAY',200000);
INSERT INTO LoaiThe(MaLoaiThe,TenLoaiThe,SoChuSo,SoChuSoCSV,PhiGiaoDich) VALUES
('VISA',N'VISA',16,3,5000),('MASTER',N'MasterCard',16,3,5000),('DISCOVER',N'Discover',16,3,8000),('AMEX',N'American Express',15,4,15000);

-- Mật khẩu mẫu: 123456 (BCrypt)
INSERT INTO KhachHang(MaKH,HoTen,NgaySinh,SoCMND,DiaChi,DienThoai,TenDangNhap,MatKhau,Email) VALUES
('KH0001',N'Nguyễn Minh Khang','2000-03-15','079200001111',N'12 Lê Lợi, Quận 1, TP.HCM','0909000111','khang',
 '{bcrypt}$2a$10$.ZlYzq9tvM6z/minJp0GoepfdtfVFCtEytLDDQObNcHBpz3V5FzWq','khang@example.com');
INSERT INTO NhanVien(TenDangNhap,MatKhau,HoTen) VALUES
('nhanvien','{bcrypt}$2a$10$.ZlYzq9tvM6z/minJp0GoepfdtfVFCtEytLDDQObNcHBpz3V5FzWq',N'Trần Thu Trang');
GO
