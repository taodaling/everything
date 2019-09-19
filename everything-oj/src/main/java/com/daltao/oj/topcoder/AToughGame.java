package com.daltao.oj.topcoder;

public class AToughGame {
    double expectedGain(int[] A, int[] B) {
        int n = A.length;
        double[] probs = new double[n];
        for (int i = 0; i < n; i++) {
            probs[i] = A[i] / 1000D;
        }
        double[] preProd = new double[n];
        preProd[0] = probs[0];
        for (int i = 1; i < n; i++) {
            preProd[i] = probs[i] * preProd[i - 1];
        }
        double[] dieProb = new double[n];
        dieProb[0] = 1 - probs[0];
        for (int i = 1; i < n; i++) {
            dieProb[i] = preProd[i - 1] * (1 - probs[i]);
        }
        double[] preSumOfDieProb = new double[n];
        preSumOfDieProb[0] = dieProb[0];
        for (int i = 1; i < n; i++) {
            preSumOfDieProb[i] = preSumOfDieProb[i - 1] + dieProb[i];
        }

        double[] exp = new double[n];
        double[] probEnter = new double[n];
        for (int i = 0; i < n; i++) {
            probEnter[i] = dieProb[i];
            for (int j = 0; j < i; j++) {
                double pji = dieProb[i] / (1 - preSumOfDieProb[j]);
                probEnter[i] += probEnter[j] * pji;
            }

            double fail = dieProb[i] / (1 - (i >= 1 ? preSumOfDieProb[i - 1] : 0));
            exp[i] = 1 / (1 - fail);
        }
        double ans = 0;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            ans += exp[i] * probEnter[i] * sum;
            sum += B[i];
        }
        ans += 1 * sum;
        return ans;
    }
}
