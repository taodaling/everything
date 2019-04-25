package com.daltao.template;

public class FastWalshHadamardTransform {
    public static void OrFWT(int[] p, int l, int r) {
        if (l == r) {
            return;
        }
        int m = (l + r) >> 1;
        OrFWT(p, l, m);
        OrFWT(p, m + 1, r);
        for (int i = 0, until = m - l; i <= until; i++) {
            int a = p[l + i];
            int b = p[m + 1 + i];
            p[m + 1 + i] = a + b;
        }
    }

    public static void OrIFWT(int[] p, int l, int r) {
        if (l == r) {
            return;
        }
        int m = (l + r) >> 1;
        for (int i = 0, until = m - l; i <= until; i++) {
            int a = p[l + i];
            int b = p[m + 1 + i];
            p[m + 1 + i] = b - a;
        }
        OrIFWT(p, l, m);
        OrIFWT(p, m + 1, r);
    }

    public static void AndFWT(int[] p, int l, int r) {
        if (l == r) {
            return;
        }
        int m = (l + r) >> 1;
        AndFWT(p, l, m);
        AndFWT(p, m + 1, r);
        for (int i = 0, until = m - l; i <= until; i++) {
            int a = p[l + i];
            int b = p[m + 1 + i];
            p[l + i] = a + b;
        }
    }

    public static void AndIFWT(int[] p, int l, int r) {
        if (l == r) {
            return;
        }
        int m = (l + r) >> 1;
        for (int i = 0, until = m - l; i <= until; i++) {
            int a = p[l + i];
            int b = p[m + 1 + i];
            p[l + i] = a - b;
        }
        AndIFWT(p, l, m);
        AndIFWT(p, m + 1, r);
    }

    public static void XorFWT(int[] p, int l, int r) {
        if (l == r) {
            return;
        }
        int m = (l + r) >> 1;
        XorFWT(p, l, m);
        XorFWT(p, m + 1, r);
        for (int i = 0, until = m - l; i <= until; i++) {
            int a = p[l + i];
            int b = p[m + 1 + i];
            p[l + i] = a + b;
            p[m + 1 + i] = a - b;
        }
    }

    public static void XorIFWT(int[] p, int l, int r) {
        if (l == r) {
            return;
        }
        int m = (l + r) >> 1;
        for (int i = 0, until = m - l; i <= until; i++) {
            int a = p[l + i];
            int b = p[m + 1 + i];
            p[l + i] = (a + b) / 2;
            p[m + 1 + i] = (a - b) / 2;
        }
        XorIFWT(p, l, m);
        XorIFWT(p, m + 1, r);
    }

    public static void DotMul(int[] a, int[] b, int n) {
        for (int i = 0; i < n; i++) {
            a[i] = a[i] * b[i];
        }
    }
}
