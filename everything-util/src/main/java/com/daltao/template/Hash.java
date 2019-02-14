package com.daltao.template;

public class Hash {
    private static int MOD = (int) (1e9 + 7);
    private int[] inverse;
    private int[] hash;
    private int n;
    private int x;

    public Hash(char[] data, int x) {
        n = data.length;
        inverse = new int[n];
        this.x = x;
        inverse[0] = 1;
        long inv = pow(x, MOD - 2);
        for (int i = 1; i < n; i++) {
            this.inverse[i] = (int) (this.inverse[i - 1] * inv % MOD);
        }

        hash = new int[n];
        hash[n - 1] = data[n - 1];
        long xn = 1;
        for (int i = n - 2; i >= 0; i--) {
            xn = xn * x % MOD;
            hash[i] = (int) (((long) hash[i + 1] + data[i] * xn) % MOD);
        }
    }

    public static long pow(int x, int n) {
        int bit = 31 - Integer.numberOfLeadingZeros(n);
        long product = 1;
        for (; bit >= 0; bit--) {
            product = product * product % MOD;
            if (((1 << bit) & n) != 0) {
                product = product * x % MOD;
            }
        }
        return product;
    }

    public int hash(int l, int r) {
        long hash = this.hash[l];
        if (r < n - 1) {
            hash = hash - this.hash[r + 1];
            if (hash < 0) {
                hash += MOD;
            }
            hash = hash * inverse[n - 1 - r] % MOD;
        }
        return (int) hash;
    }
}