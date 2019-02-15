package com.daltao.log;

import com.daltao.exception.UnexpectedException;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestCaseGenerator {
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public static void main(String[] args) {
        generate("D:/Temp/log1", "log1");
        generate("D:/Temp/log2", "log2");
        generate("D:/Temp/log3", "log3");
        generate("D:/Temp/log4", "log4");
    }

    private static void generate(String fileName, String userName) {
        long current = System.currentTimeMillis();
        String delimiter = "\nABNIWOOMCAWNINJLLMXOQJUOQRHHAYZNIQOQW\n";
        Charset charset = Charset.forName("utf8");
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(fileName))) {
            for (int i = 0; i < 25000000; i++) {
                os.write(String.format("%s - [%s] - %s%s", format.format(new Date(current + i)), userName, "Hello, world!", delimiter).getBytes(charset));
            }
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }
}
