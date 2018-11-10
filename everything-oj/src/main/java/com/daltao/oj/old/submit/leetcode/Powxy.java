package com.daltao.oj.old.submit.leetcode;

public class Powxy {
    public double myPow(double x, int n) {
        if(n >= 0)
        {
            return pow(x, n);
        }
        return 1 / pow(x, -(long)n);
    }

    public static double pow(double x, long n) {
        int bit = 63 - Long.numberOfLeadingZeros(n);
        double prod = 1;
        for (; bit >= 0; bit--) {
            prod = prod * prod;
            if (((1L << bit) & n) != 0)
            {
                prod = prod * x;
            }
        }
        return prod;
    }
}
