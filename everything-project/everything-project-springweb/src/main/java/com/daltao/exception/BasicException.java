package com.daltao.exception;

public class BasicException extends RuntimeException {
    private Integer status;
    private String errorCode;
    private String errorMessage;
    private String prompt;

    public BasicException(Integer status, String errorCode, String errorMessage, String prompt) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.prompt = prompt;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getPrompt() {
        return prompt;
    }

    public void throwSelf() {
        throw this;
    }

}
