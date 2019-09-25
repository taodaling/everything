package com.daltao.oj.topcoder;

public class AbsSequence {
    public String[] getElements(String first, String second, String[] indices) {
        long a = Long.parseLong(first);
        long b = Long.parseLong(second);
        int n = indices.length;
        long[] indexes = new long[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = Long.parseLong(indices[i]);
        }
        String[] ans = new String[n];
        for (int i = 0; i < n; i++) {
            ans[i] = Long.toString(get(a, b, indexes[i]));
        }
        return ans;
    }

    public long get(long a, long b, long x) {
        if (x == 0) {
            return a;
        }
        if (x == 1) {
            return b;
        }
        Helper helper = new Helper();
        helper.a = a;
        helper.b = b;
        helper.step(x);
        return helper.a;
    }

    public static class Helper {
        long a;
        long b;

        public void step(long n) {
            if (n == 0) {
                return;
            }
            if (a < b) {
                swap();
                b = a - b;
                step(n - 1);
                return;
            }
            if (n < 3) {
                for (int i = 0; i < n; i++) {
                    swap();
                    b = Math.abs(a - b);
                }
                return;
            }
            if (b == 0) {
                step(n % 3);
                return;
            }
            if (a <= 2 * b) {
                swap();
                b = b - a;
                step(n - 1);
                return;
            }

            long move = Math.min(n / 3, a / (2 * b));
            a -= 2 * b * move;
            n -= move * 3;
            step(n);
        }

        public void swap() {
            long tmp = a;
            a = b;
            b = tmp;
        }
    }
}
