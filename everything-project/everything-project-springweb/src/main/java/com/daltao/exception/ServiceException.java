package com.daltao.exception;

import lombok.Builder;

public class ServiceException extends BasicException {
    public ServiceException(Integer status, String errorCode, String errorMessage, String prompt) {
        super(status, errorCode, errorMessage, prompt);
    }
}
