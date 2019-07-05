package com.daltao.simple;

public class StopTest implements Runnable {
    boolean stop;

    @Override
    public void run() {

        long time = System.nanoTime();

        int i = 0;
        while (!stop) {
            i++;
        }

        System.out.println("Stopped at " + time);
    }

    public void setStop() {
        stop = true;
    }

    public static void main(String[] args) throws InterruptedException {
        StopTest s = new StopTest();
        Thread t = new Thread(s);
        t.start();

        Thread.sleep(10);
        long time = System.nanoTime();

        s.setStop();
        System.out.println("setStop at " + time);
    }
}
