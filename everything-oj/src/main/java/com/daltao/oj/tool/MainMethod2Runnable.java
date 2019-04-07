package com.daltao.oj.tool;

import java.lang.reflect.InvocationTargetException;

public class MainMethod2Runnable implements Runnable {
    private Class cls;

    public MainMethod2Runnable(Class cls) {
        this.cls = cls;
    }

    @Override
    public void run() {
        try {
            cls.getMethod("main", String[].class).invoke(null, new Object[]{null});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
