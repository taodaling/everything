package com.daltao.template;

import java.util.Arrays;

public class NumberTheoryTransform {
    private static final NumberTheory.Modular MODULAR = new NumberTheory.Modular(998244353);
    private static final NumberTheory.Power POWER = new NumberTheory.Power(MODULAR);
    private static final int G = 3;
    private static int[] wCache = new int[23];
    private static int[] invCache = new int[23];

    static {
        for (int i = 0, until = wCache.length; i < until; i++) {
            int s = 1 << i;
            wCache[i] = POWER.pow(G, (MODULAR.m - 1) / 2 / s);
            invCache[i] = POWER.inverse(s);
        }
    }

    public static void dotMul(int[] a, int[] b, int[] c, int m) {
        for (int i = 0, n = 1 << m; i < n; i++) {
            c[i] = MODULAR.mul(a[i], b[i]);
        }
    }

    public static void reverse(int[] r, int b) {
        int n = 1 << b;
        r[0] = 0;
        for (int i = 1; i < n; i++) {
            r[i] = (r[i >> 1] >> 1) | ((1 & i) << (b - 1));
        }
    }

    private static void dft(int[] r, int[] p, int m) {
        int n = 1 << m;

        for (int i = 0; i < n; i++) {
            if (r[i] > i) {
                int tmp = p[i];
                p[i] = p[r[i]];
                p[r[i]] = tmp;
            }
        }

        int w = 0;
        int t = 0;
        for (int d = 0; d < m; d++) {
            int w1 = wCache[d];
            int s = 1 << d;
            int s2 = s << 1;
            for (int i = 0; i < n; i += s2) {
                w = 1;
                for (int j = 0; j < s; j++) {
                    int a = i + j;
                    int b = a + s;
                    t = MODULAR.mul(w, p[b]);
                    p[b] = MODULAR.plus(p[a], -t);
                    p[a] = MODULAR.plus(p[a], t);
                    w = MODULAR.mul(w, w1);
                }
            }
        }
    }

    private static void idft(int[] r, int[] p, int m) {
        dft(r, p, m);

        int n = 1 << m;
        int invN = invCache[m];

        p[0] = MODULAR.mul(p[0], invN);
        p[n / 2] = MODULAR.mul(p[n / 2], invN);
        for (int i = 1, until = n / 2; i < until; i++) {
            int a = p[n - i];
            p[n - i] = MODULAR.mul(p[i], invN);
            p[i] = MODULAR.mul(a, invN);
        }
    }

    /**
     * return polynomial g while p * g = 1 (mod x^m).
     * <br>
     * You are supposed to guarantee the lengths of all arrays are greater than or equal to 2^{ceil(log2(m)) + 1}
     */
    private static void inverse(int[] r, int[] p, int[] inv, int[] buf, int m) {
        if (m == 0) {
            inv[0] = POWER.inverse(p[0]);
            return;
        }
        inverse(r, p, inv, buf, m - 1);
        int n = 1 << (m + 1);
        System.arraycopy(p, 0, buf, 0, 1 << m);
        Arrays.fill(buf, 1 << m, 1 << (m + 1), 0);
        reverse(r, (m + 1));
        dft(r, buf, (m + 1));
        dft(r, inv, (m + 1));
        for (int i = 0; i < n; i++) {
            inv[i] = MODULAR.mul(inv[i], 2 - MODULAR.mul(buf[i], inv[i]));
        }
        idft(r, inv, m + 1);
        for (int i = 1 << m; i < n; i++) {
            inv[i] = 0;
        }
    }
}
