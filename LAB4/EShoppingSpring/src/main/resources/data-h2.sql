-- Dữ liệu mẫu (giống script SQL Server)
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
