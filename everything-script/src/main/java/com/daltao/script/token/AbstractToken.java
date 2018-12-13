package com.daltao.script.token;

public abstract class AbstractToken implements Token {
    private final String value;

    protected AbstractToken(String value) {
        this.value = value;
    }

    @Override
    public String getText() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isIdentifier() {
        return false;
    }
}
