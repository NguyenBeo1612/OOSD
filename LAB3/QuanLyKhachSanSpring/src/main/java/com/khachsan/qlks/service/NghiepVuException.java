package com.khachsan.qlks.service;

/** Vi phạm quy tắc nghiệp vụ; ném ra trong @Transactional sẽ rollback toàn bộ (thay KetQuaXuLy.Fail + tx.Rollback()). */
public class NghiepVuException extends RuntimeException {

    public NghiepVuException(String message) {
        super(message);
    }
}
