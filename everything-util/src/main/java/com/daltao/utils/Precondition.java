package com.daltao.utils;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;

public class Precondition {
    private Precondition() {
    }

    public static void isNotNull(Object x, String msg) {
        isTrue(x != null, msg);
    }

    public static void equal(Object a, Object b, String msg) {
        isTrue(Objects.equals(a, b), msg);
    }


    public static void equal(Object a, Object b) {
        equal(a, b, "");
    }

    public static void equal(byte a, byte b, String msg) {
        isTrue(a == b, msg);
    }

    public static void equal(boolean a, boolean b, String msg) {
        isTrue(a == b, msg);
    }

    public static void equal(boolean a, boolean b) {
        equal(a, b, "");
    }

    public static void equal(char a, char b, String msg) {
        isTrue(a == b, msg);
    }

    public static void equal(short a, short b, String msg) {
        isTrue(a == b, msg);
    }

    public static void ge(int a, int b, String msg) {
        isTrue(a >= b, msg);
    }

    public static void ge(int a, int b) {
        ge(a, b, "");
    }

    public static void equal(int a, int b, String msg) {
        isTrue(a == b, msg);
    }

    public static void equal(float a, float b, String msg) {
        isTrue(a == b, msg);
    }

    public static void equal(double a, double b, String msg) {
        isTrue(a == b, msg);
    }

    public static void equal(long a, long b, String msg) {
        isTrue(a == b, msg);
    }

    public static void isNull(Object x, String msg) {
        isTrue(x == null, msg);
    }

    public static void isNotEmpty(Collection<?> c, String msg) {
        isTrue(c != null && !c.isEmpty(), msg);
    }

    public static void isEmpty(Collection<?> c, String msg) {
        isTrue(c == null || c.isEmpty(), msg);
    }

    public static void isEmpty(String s, String msg) {
        isTrue(s == null || s.isEmpty(), msg);
    }

    public static void isNotEmpty(String s, String msg) {
        isTrue(s != null && !s.isEmpty(), msg);
    }

    public static void isTrue(boolean b, String msg) {
        if (!b) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void isTrue(boolean b) {
        isTrue(b, "");
    }

    public static void isFalse(boolean b) {
        isFalse(b, "");
    }

    public static void isFalse(boolean b, String msg) {
        if (b) {
            throw new IllegalArgumentException(msg);
        }
    }
}
