package com.thuvien.qltv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Điểm khởi động ứng dụng – thay cho Program.cs / FrmMain của bản WinForms. */
@SpringBootApplication
public class QuanLyThuVienApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanLyThuVienApplication.class, args);
    }
}
