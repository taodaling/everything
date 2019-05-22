package com.daltao.oj.submit;

public class TP9915A {
    public class RedIsGood {
        public double getProfit(int r, int b) {
            double[] dp = new double[b + 1];
            for (int i = 0; i <= r; i++) {
                dp[0] = i;
                for (int j = 1; j <= r; j++) {
                    dp[j] = Math.max(0, (1 + dp[j]) * i / (i + j) + (dp[j - 1] - 1) * j / (i + j));
                }
            }
            return dp[b];
        }
    }
}
