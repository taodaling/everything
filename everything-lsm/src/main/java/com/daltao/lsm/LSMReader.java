package com.daltao.lsm;

public interface LSMReader {
    int recordNum();

    byte[] keyOf(int index);

    byte[] valueOf(int index);
}
