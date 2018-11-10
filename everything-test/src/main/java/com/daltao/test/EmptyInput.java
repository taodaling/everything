package com.daltao.test;

public class EmptyInput implements Input {
    private EmptyInput() {
    }

    private static EmptyInput instance = new EmptyInput();

    public static EmptyInput getInstance() {
        return instance;
    }

    @Override
    public Object read() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean available() {
        return false;
    }
}
