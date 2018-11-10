package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/6/21.
 */
public class DivideTwoIntegers {
    public int divide(int dividend, int divisor) {
        if (divisor == 0) {
            throw new ArithmeticException();
        }
        if (divisor == Integer.MIN_VALUE) {
            return dividend == Integer.MIN_VALUE ? 1 : 0;
        }
        if (dividend == Integer.MIN_VALUE) {
            if (divisor == -1) {
                return Integer.MAX_VALUE;
            }
            if (divisor == 1) {
                return Integer.MIN_VALUE;
            }
            if (divisor < 0) {
                return divide(dividend - divisor, divisor) + 1;
            }
            return divide(dividend + divisor, divisor) - 1;
        }
        if (divisor < 0) {
            return divide(-dividend, -divisor);
        }
        if (dividend < 0) {
            return -divide(-dividend, divisor);
        }
        return div(dividend, divisor);
    }

    /**
     * Calculate floor(a/b)
     *
     * @param a a positive number
     * @param b a positive number
     * @return floor(a/b)
     */
    public int div(int a, int b) {
        int[] v = new int[32];
        int[] u = new int[32];
        v[0] = 1;
        u[0] = b;
        int limit = 0;
        for (; u[limit] <= a && u[limit] > 0; limit++) {
            u[limit + 1] = u[limit] + u[limit];
            v[limit + 1] = v[limit] + v[limit];
        }
        int c = 0;
        int r = a;
        for (limit--; r >= b; limit--) {
            if (r >= u[limit]) {
                c += v[limit];
                r -= u[limit];
            }
        }
        return c;
    }
}
