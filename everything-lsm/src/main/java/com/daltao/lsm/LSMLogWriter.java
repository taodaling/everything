package com.daltao.lsm;

public interface LSMLogWriter {
    void append(byte[] key, byte[] value);
}
