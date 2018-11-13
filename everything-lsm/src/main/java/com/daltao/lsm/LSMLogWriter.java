package com.daltao.lsm;

public interface LSMLogWriter extends AutoCloseable{
    void append(byte[] key, byte[] value);
}
