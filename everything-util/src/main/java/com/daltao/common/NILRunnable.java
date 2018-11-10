package com.daltao.common;

/**
 * Created by dalt on 2018/3/13.
 */
public class NILRunnable implements Runnable {
    private static NILRunnable INSTANCE = new NILRunnable();

    private NILRunnable() {
    }

    public static NILRunnable getInstance() {
        return INSTANCE;
    }

    @Override
    public void run() {

    }
}
