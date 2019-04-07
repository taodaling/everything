package com.daltao.oj.tool;

import com.daltao.utils.IOUtils;
import com.daltao.utils.RandomUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

public class Process2Runnable implements Runnable {
    static String fileName = RandomUtils.getRandomString(new Random(), 'a', 'z', 10);
    final String[] commands;

    public Process2Runnable(String... commands) {
        this.commands = commands;
    }

    @Override
    public void run() {
        try {
            File inputFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName + ".in");
            File outputFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName + ".out");
            try (FileOutputStream os = new FileOutputStream(inputFile)) {
                IOUtils.copy(System.in, os);
            }
            new ProcessBuilder()
                    .command(commands)
                    .redirectInput(inputFile)
                    .redirectOutput(outputFile)
                    .redirectError(outputFile)
                    .start()
                    .waitFor();
            try (FileInputStream is = new FileInputStream(outputFile)) {
                IOUtils.copy(is, System.out);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
