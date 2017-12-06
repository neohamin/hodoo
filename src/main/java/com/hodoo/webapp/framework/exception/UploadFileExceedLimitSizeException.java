package com.hodoo.webapp.framework.exception;

/**
 * 사용자 업로드 파일의 용량 초과시 발생되는 exception.
 */
public class UploadFileExceedLimitSizeException extends RuntimeException {

    /**
     * 생성자
     * @param message 예외 메시지
     */
    public UploadFileExceedLimitSizeException(String message) {
        super(message);
    }
}
