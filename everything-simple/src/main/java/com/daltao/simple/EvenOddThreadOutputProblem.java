package com.daltao.simple;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class EvenOddThreadOutputProblem {
    private static AtomicBoolean isEven = new AtomicBoolean(true);

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> {
            for (int i = 0; i < 5; i++) {
                int num = i * 2 + 1;
                while (!isEven.get()) {
                    Thread.yield();
                }
                System.out.println(num);
                isEven.set(false);
            }
        });

        service.submit(() -> {
            for (int i = 0; i < 5; i++) {
                int num = i * 2 + 2;
                while (isEven.get()) {
                    Thread.yield();
                }
                System.out.println(num);
                isEven.set(true);
            }
        });

        service.shutdown();
    }
}
