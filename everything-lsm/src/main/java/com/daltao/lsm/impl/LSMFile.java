package com.daltao.lsm.impl;

import lombok.Data;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@Data
public class LSMFile {
    private File file;
    private FileChannel channel;
    private RandomAccessFile raf;
    private Lock lock;
    private long entryCount;
    private long locationOffset;
    private long dataOffset;
}
