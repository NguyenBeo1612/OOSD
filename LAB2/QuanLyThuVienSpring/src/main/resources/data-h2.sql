-- Dữ liệu mẫu H2 (giống script SQL Server)
INSERT INTO NhanVien(MaNhanVien,Ho,Ten,Phai,NgaySinh,ChucVu,SoDienThoai) VALUES
(N'NV001',N'Nguyễn',N'An',N'Nam','1990-02-15',N'Thủ thư',N'0901000001'),
(N'NV002',N'Trần',N'Bình',N'Nữ','1992-08-20',N'Nhân viên quản lý sách',N'0901000002');

INSERT INTO TheLoai(MaTheLoai,TenTheLoai) VALUES
(N'TL001',N'Tin học'),(N'TL002',N'Tiểu thuyết'),(N'TL003',N'Anh văn'),(N'TL004',N'Truyện ngắn');

INSERT INTO NhaXuatBan(MaNhaXuatBan,DiaChi,SoDienThoai) VALUES
(N'NXB001',N'Quận 1, TP.HCM',N'0283000001'),
(N'NXB002',N'Quận Cầu Giấy, Hà Nội',N'0243000002');

INSERT INTO DauSach(MaDauSach,TenSach,NamXuatBan,SoLuongHienCo,MaTheLoai,MaNhaXuatBan) VALUES
(N'S001',N'Lập trình C# căn bản',2025,5,N'TL001',N'NXB001'),
(N'S002',N'Cơ sở dữ liệu',2024,4,N'TL001',N'NXB001'),
(N'S003',N'Mạng máy tính',2023,3,N'TL001',N'NXB002'),
(N'S004',N'Tiếng Anh chuyên ngành',2024,2,N'TL003',N'NXB002'),
(N'S005',N'Lập trình Java Spring Boot',2026,3,N'TL001',N'NXB001');

INSERT INTO DocGia(MaDocGia,Ho,Ten,NgaySinh,Phai,SoDienThoai,DiaChi,Email,Anh3x4) VALUES
(N'DG001',N'Lê',N'Minh','2003-05-12',N'Nam',N'0911000001',N'TP.HCM',N'minh@example.com',NULL),
(N'DG002',N'Phạm',N'Lan','2002-10-23',N'Nữ',N'0911000002',N'TP.HCM',N'lan@example.com',NULL),
(N'DG003',N'Võ',N'Hùng','2004-01-30',N'Nam',N'0911000003',N'Bình Dương',N'hung@example.com',NULL);

-- Thẻ còn hạn 1 năm tính từ ngày chạy script
INSERT INTO TheDocGia(MaThe,MaDocGia,NgayCap,HanSuDung,DaDongLePhi,TrangThai) VALUES
(N'THE_DG001_01',N'DG001',CURRENT_DATE,DATEADD(YEAR,1,CURRENT_DATE),1,1),
(N'THE_DG002_01',N'DG002',CURRENT_DATE,DATEADD(YEAR,1,CURRENT_DATE),1,1);
-- DG003 chưa có thẻ: dùng để thử chức năng Cấp thẻ (TC01)

-- Mật khẩu của cả hai tài khoản: 123456 (BCrypt)
INSERT INTO TaiKhoan(TenDangNhap,MatKhau,MaNhanVien,VaiTro,HoatDong) VALUES
(N'thuthu', N'{bcrypt}$2a$10$.ZlYzq9tvM6z/minJp0GoepfdtfVFCtEytLDDQObNcHBpz3V5FzWq', N'NV001', N'THU_THU', 1),
(N'qlsach', N'{bcrypt}$2a$10$.ZlYzq9tvM6z/minJp0GoepfdtfVFCtEytLDDQObNcHBpz3V5FzWq', N'NV002', N'QL_SACH', 1);

