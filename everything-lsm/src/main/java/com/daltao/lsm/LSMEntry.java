package com.daltao.lsm;

import lombok.Data;

import java.util.Map;

@Data
public class LSMEntry implements Map.Entry<byte[], byte[]> {
    private byte[] key;
    private byte[] value;
}
