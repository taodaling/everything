package com.daltao.lsm;

public interface LSMWriter {
    void append(byte[] key, byte value);
}
