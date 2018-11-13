package com.daltao.lsm;

public interface LSMWriter extends AutoCloseable {
    void append(byte[] key, byte value);
}
