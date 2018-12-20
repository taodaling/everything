package com.daltao.script.error;

public class ScriptException extends RuntimeException {
    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
