package com.eshop.web;

import com.eshop.service.NghiepVuException;
import java.util.function.Supplier;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Chạy một thao tác, đưa thông báo thành công / lỗi nghiệp vụ vào flash rồi chuyển trang. */
final class Flash {

    private Flash() {
    }

    static String chay(Supplier<String> f, RedirectAttributes ra, String ve) {
        try {
            ra.addFlashAttribute("ok", f.get());
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:" + ve;
    }
}
