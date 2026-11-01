package com.eshop.service;

/** Vi phạm quy tắc nghiệp vụ; ném ra trong @Transactional sẽ rollback toàn bộ . */
public class NghiepVuException extends RuntimeException {

    public NghiepVuException(String message) {
        super(message);
    }
}
