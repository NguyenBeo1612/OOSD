package com.eshop.web;

import com.eshop.service.NghiepVuException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Hiển thị lỗi nghiệp vụ không bắt riêng (ví dụ sản phẩm / đơn không tồn tại). */
@ControllerAdvice
public class LoiController {

    @ExceptionHandler(NghiepVuException.class)
    public String loi(NghiepVuException ex, Model model) {
        model.addAttribute("loi", ex.getMessage());
        return "error/loi";
    }
}
