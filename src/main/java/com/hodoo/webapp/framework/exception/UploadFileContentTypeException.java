package com.hodoo.webapp.framework.exception;

/**
 * 업로드 파일이 허용된 파일 컨텐츠가 아닐 경우 발생하는 exception.
 */
public class UploadFileContentTypeException extends RuntimeException {

    /**
     * 생성자
     * @param message 예외 메시지
     */
    public UploadFileContentTypeException(String message) {
        super(message);
    }
}
