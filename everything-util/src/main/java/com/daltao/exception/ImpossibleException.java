package com.daltao.exception;

public class ImpossibleException extends UnexpectedException {
    public ImpossibleException() {
        super();
    }

    public ImpossibleException(String message) {
        super(message);
    }

    public ImpossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpossibleException(Throwable cause) {
        super(cause);
    }

    protected ImpossibleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
