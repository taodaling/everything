package com.daltao.test;

public interface Input<T> {
    T read();

    /**
     * Has more element?
     */
    boolean available();
}