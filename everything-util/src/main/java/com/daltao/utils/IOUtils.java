package com.daltao.utils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Administrator on 2017/11/11.
 */
public class IOUtils {

    public static void silenceCopy(InputStream is, OutputStream os) {
        try {
            copy(is, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[4096];
        int offset = 0;
        int readnum;
        while ((readnum = is.read(buf, offset, buf.length - offset)) != -1) {
            offset += readnum;
            if (offset == buf.length) {
                os.write(buf);
                offset = 0;
            }
        }
        if (offset > 0) {
            os.write(buf, 0, offset);
        }
    }

    public static void sendFile(File file, OutputStream os) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            copy(is, os);
        }
    }

    public static int readBulk(InputStream is, byte[] data, int offset, int len) throws IOException {
        int totalRead = 0;
        int readNum;
        while (totalRead < len && (readNum = is.read(data, offset + totalRead, len - totalRead)) != -1) {
            totalRead += readNum;
        }
        return totalRead;
    }

    public static int readBulk(RandomAccessFile is, byte[] data, int offset, int len) throws IOException {
        int totalRead = 0;
        int readNum;
        while (totalRead < len && (readNum = is.read(data, offset + totalRead, len - totalRead)) != -1) {
            totalRead += readNum;
        }
        return totalRead;
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String readAll(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[8096];
        int len = 0;
        while ((len = reader.read(buf)) >= 0) {
            builder.append(buf, 0, len);
        }
        return builder.toString();
    }

    public static byte[] readFile(String path) throws IOException {
        return readFile(new File(path));
    }

    public static void writeFile(File file, byte[] data, int offset, int length) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data, offset, length);
        }
    }

    public static byte[] readFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return readAll(is);
        }
    }
}
