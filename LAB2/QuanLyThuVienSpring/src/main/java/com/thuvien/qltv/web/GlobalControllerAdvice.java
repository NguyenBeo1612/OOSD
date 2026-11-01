package com.thuvien.qltv.web;

import com.thuvien.qltv.service.NghiepVuException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Bắt lỗi chung: vi phạm ràng buộc CSDL (FK, UNIQUE, CHECK) được chuyển thành thông báo dễ hiểu
 * thay vì hiển thị raw SQL exception (mục 8 – kịch bản sai).
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String loiRangBuoc(DataIntegrityViolationException ex, HttpServletRequest req, RedirectAttributes ra) {
        ra.addFlashAttribute("loi", "Dữ liệu vi phạm ràng buộc CSDL (trùng mã, đang được sử dụng hoặc giá trị không hợp lệ).");
        return "redirect:" + quayLai(req);
    }

    @ExceptionHandler(NghiepVuException.class)
    public String loiNghiepVu(NghiepVuException ex, HttpServletRequest req, RedirectAttributes ra) {
        ra.addFlashAttribute("loi", ex.getMessage());
        return "redirect:" + quayLai(req);
    }

    private String quayLai(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String path = uri.substring(ctx.length());
        int i = path.indexOf('/', 1);
        return i > 0 ? path.substring(0, i) : path;
    }
}
