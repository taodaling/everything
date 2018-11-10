package com.daltao.utils;

public class MathUtils {
    public static int ceilLog2(int x) {
        return 32 - Integer.numberOfLeadingZeros(x - 1);
    }

    public static int floorLog2(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }
}
