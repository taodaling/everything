package com.daltao.lsm.impl;

import com.daltao.lsm.LSMLogReader;
import com.daltao.util.ConfigUtils;
import com.daltao.utils.Precondition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LSMManager {
    private static final String logPrefix = "log";
    private static final String dbPrefix = "db";
    private static final String dbTmpPrefix = "db-tmp";

    private Map<String, LSMFile> cache = new ConcurrentHashMap<>();
    private File root;

    private LSMManager() {
        root = new File(ConfigUtils.getInstance().getDbDataDirectory());
        if (root.isFile()) {
            throw new IllegalStateException(root.getAbsolutePath() + " is a file rather than a directory");
        }
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    private synchronized LSMFile buildFromExistingLSMFile(String name) {
        File file = new File(root, name);
        if (!file.exists()) {
            return null;
        }
        LSMFile lsmFile = new LSMFile();
        lsmFile.setFile(file);
        lsmFile.setLock(new ReentrantLock());

        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            lsmFile.setRaf(raf);
            FileChannel fileChannel = raf.getChannel();
            lsmFile.setChannel(fileChannel);
            fileChannel.lock();
            long entryCount = raf.readLong();
            long dataOffset = raf.readLong();
            lsmFile.setLocationOffset(8);
            lsmFile.setDataOffset(dataOffset);
            lsmFile.setEntryCount(entryCount);
        } catch (IOException e) {
            if (lsmFile.getRaf() != null) {
                try {
                    lsmFile.getRaf().close();
                } catch (IOException e1) {
                }
            }
            throw new RuntimeException(e);
        }

        return lsmFile;
    }

    public LSMLogReader getLogReader(int i) {

    }
}
