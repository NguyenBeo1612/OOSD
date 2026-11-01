package com.thuvien.qltv.service;

/**
 * Lỗi vi phạm quy định nghiệp vụ (Business Rule). Là RuntimeException nên khi ném ra
 * trong phương thức @Transactional, Spring tự động rollback toàn bộ giao dịch
 * (thay cho tx.Rollback() thủ công trong bản C#).
 */
public class NghiepVuException extends RuntimeException {

    public NghiepVuException(String message) {
        super(message);
    }
}
