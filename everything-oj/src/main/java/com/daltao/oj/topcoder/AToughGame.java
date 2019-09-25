package com.daltao.oj.topcoder;

public class AToughGame {
    double expectedGain(int[] A, int[] B) {
        int n = A.length;
        double[] p = new double[n + 1];
        double pass = 1;
        for (int i = 0; i < n; i++) {
            p[i] = pass * (1 - A[i] / 1000D);
            pass *= A[i] / 1000D;
        }
        p[n] = pass;
        double[] x = new double[n + 1];
        x[n] = 1;
        double suffix = x[n] * p[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = suffix / (1 - p[i]);
            suffix += x[i] * p[i];
        }

        int[] sum = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            sum[i] = sum[i - 1] + B[i - 1];
        }

        double exp = 0;
        for (int i = 0; i < n; i++) {
            exp += p[i] * x[i] * sum[i];
        }
        exp = exp / p[n];
        return exp + sum[n];
    }
}
