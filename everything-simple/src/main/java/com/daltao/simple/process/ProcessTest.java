package com.daltao.simple.process;

import java.io.IOException;

public class ProcessTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        new ProcessBuilder()
                .command("java", "-?")
                .inheritIO()
                .start()
                .waitFor();
    }
}
