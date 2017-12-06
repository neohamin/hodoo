package com.hodoo.webapp.framework.exception;

/**
 * 업로드 파일이 저장 오류.
 */
public class UploadFileStoreException extends RuntimeException {

    /**
     * 생성자
     * @param message 예외 메시지
     */
    public UploadFileStoreException(String message) {
        super(message);
    }

    /**
     * 생성자
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public UploadFileStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
