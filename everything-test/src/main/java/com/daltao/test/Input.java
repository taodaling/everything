package com.daltao.test;

public interface Input<T> {
    T read();

    /**
     * Has more element?
     */
    boolean available();

    default int readInt() {
        return Integer.parseInt(read().toString());
    }

    default double readDouble() {
        return Double.parseDouble(read().toString());
    }

    default String readString() {
        return read().toString();
    }
}