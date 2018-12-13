package com.daltao.script.token;

public interface Token {
    String getText();

    boolean isString();

    boolean isNumber();

    boolean isIdentifier();
}
