package com.daltao.simple;

import net.bytebuddy.ByteBuddy;

public class ByteBuddyStudy {
    public static void main(String[] args) throws Throwable{
        System.out.println(
                new ByteBuddy()
                .subclass(Object.class)
                .name("example.Type")
                .make()
                .load(Thread.currentThread().getContextClassLoader())
                .getLoaded()
                .newInstance()
        );
    }
}
