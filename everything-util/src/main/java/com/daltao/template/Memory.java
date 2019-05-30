package com.daltao.template;

import java.util.Arrays;
import java.util.Comparator;

public class Memory {
    public static <T> void swap(T[] data, int i, int j) {
        T tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    public static void swap(char[] data, int i, int j) {
        char tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    public static void swap(int[] data, int i, int j) {
        int tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    public static void swap(long[] data, int i, int j) {
        long tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    public static void swap(double[] data, int i, int j) {
        double tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

    public static <T> int min(T[] data, int from, int to, Comparator<T> cmp) {
        int m = from;
        for (int i = from + 1; i < to; i++) {
            if (cmp.compare(data[m], data[i]) > 0) {
                m = i;
            }
        }
        return m;
    }

    public static <T> void move(T[] data, int from, int to, int step) {
        int len = to - from;
        step = len - (step % len + len) % len;
        Object[] buf = new Object[len];
        for (int i = 0; i < len; i++) {
            buf[i] = data[(i + step) % len + from];
        }
        System.arraycopy(buf, 0, data, from, len);
    }

    public static <T> void reverse(T[] data, int f, int t) {
        int l = f, r = t - 1;
        while (l < r) {
            swap(data, l, r);
            l++;
            r--;
        }
    }

    public static void reverse(int[] data, int f, int t) {
        int l = f, r = t - 1;
        while (l < r) {
            swap(data, l, r);
            l++;
            r--;
        }
    }

    public static void copy(Object[] src, Object[] dst, int srcf, int dstf, int len) {
        if (len < 8) {
            for (int i = 0; i < len; i++) {
                dst[dstf + i] = src[srcf + i];
            }
        } else {
            System.arraycopy(src, srcf, dst, dstf, len);
        }
    }

    public static void fill(int[][] x, int val) {
        for (int[] v : x) {
            Arrays.fill(v, val);
        }
    }

    public static void fill(int[][][] x, int val) {
        for (int[][] v : x) {
            fill(v, val);
        }
    }
}
