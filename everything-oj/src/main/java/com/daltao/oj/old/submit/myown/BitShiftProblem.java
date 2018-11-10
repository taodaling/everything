package com.daltao.oj.old.submit.myown;

public class BitShiftProblem {
    public static void main(String[] args)
    {
        System.out.println(new BitShiftProblem().solve(Integer.parseInt("1110111", 2)));
    }

    public int solve(int n) {
        if (n < 0) {
            n = -n;
        }
        if (n == 0) {
            return 0;
        }

        while ((n & 1) == 0) {
            n >>= 1;
        }

        n >>= 1;
        int p = 1;
        int r = 1;

        while (n != 0) {
            int lowest = n & 1;
            n >>= 1;

            int lastP = p;
            int lastR = r;
            if (lowest == 0) {
                p = Math.min(lastP, lastR + 1);
                r = Math.min(lastP + 1, lastR + 1);
            } else {
                p = Math.min(lastP + 1, lastR + 1);
                r = Math.min(lastP + 1, lastR);
            }
        }

        return p;
    }
}
