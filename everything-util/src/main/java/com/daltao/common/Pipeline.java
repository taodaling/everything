package com.daltao.common;

public interface Pipeline<I, O> {
    O read();
    void write(I data);
}
