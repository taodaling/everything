package com.daltao.template;

public class FastFourierTransform {
    private static double[][] wCache = new double[31][2];

    static {
        for (int i = 0, until = wCache.length; i < until; i++) {
            double s = 1 << i;
            wCache[i][0] = Math.cos(Math.PI / s);
            wCache[i][1] = Math.sin(Math.PI / s);
        }
    }

    public static void fft(int[] r, double[][] a, double[][] b, int m) {
        reverse(r, m);
        dft(r, a, m);
        dft(r, b, m);
        int n = 1 << m;
        for (int i = 0; i < n; i++) {
            mul(a[i][0], a[i][1], b[i][0], b[i][1], a[i]);
        }
        idft(r, a, m);
    }

    private static void reverse(int[] r, int b) {
        int n = 1 << b;
        r[0] = 0;
        for (int i = 1; i < n; i++) {
            r[i] = (r[i >> 1] >> 1) | ((1 & i) << (b - 1));
        }
    }

    private static void dft(int[] r, double[][] p, int m) {
        int n = 1 << m;

        for (int i = 0; i < n; i++) {
            if (r[i] > i) {
                Memory.swap(p, i, r[i]);
            }
        }

        double[] w = new double[2];
        double[] t = new double[2];
        for (int d = 0; d < m; d++) {
            double[] w1 = wCache[d];
            int s = 1 << d;
            int s2 = s << 1;
            for (int i = 0; i < n; i += s2) {
                w[0] = 1;
                w[1] = 0;

                for (int j = 0; j < s; j++) {
                    int a = i + j;
                    int b = a + s;
                    mul(w[0], w[1], p[b][0], p[b][1], t);
                    sub(p[a][0], p[a][1], t[0], t[1], p[b]);
                    add(p[a][0], p[a][1], t[0], t[1], p[a]);
                    mul(w[0], w[1], w1[0], w1[1], w);
                }
            }
        }
    }

    private static void idft(int[] r, double[][] p, int m) {
        dft(r, p, m);

        int n = 1 << m;
        div(p[0][0], p[0][1], n, p[0]);
        div(p[n / 2][0], p[n / 2][1], n, p[n / 2]);
        for (int i = 1, until = n / 2; i < until; i++) {
            double a = p[n - i][0];
            double b = p[n - i][1];
            div(p[i][0], p[i][1], n, p[n - i]);
            div(a, b, n, p[i]);
        }
    }

    private static void add(double r1, double i1, double r2, double i2, double[] r) {
        r[0] = r1 + r2;
        r[1] = i1 + i2;
    }

    private static void sub(double r1, double i1, double r2, double i2, double[] r) {
        r[0] = r1 - r2;
        r[1] = i1 - i2;
    }

    private static void mul(double r1, double i1, double r2, double i2, double[] r) {
        r[0] = r1 * r2 - i1 * i2;
        r[1] = r1 * i2 + i1 * r2;
    }

    private static void div(double r1, double i1, double r2, double[] r) {
        r[0] = r1 / r2;
        r[1] = i1 / r2;
    }
}
