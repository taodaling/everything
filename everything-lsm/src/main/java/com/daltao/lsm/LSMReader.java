package com.daltao.lsm;

public interface LSMReader extends AutoCloseable{
    int recordNum();

    byte[] keyOf(int index);

    byte[] valueOf(int index);
}
