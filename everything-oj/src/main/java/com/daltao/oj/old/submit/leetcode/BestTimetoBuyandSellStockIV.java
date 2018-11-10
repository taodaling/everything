package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2018/5/1.
 */
public class BestTimetoBuyandSellStockIV {
    public static void main(String[] args) {
        System.out.println(new BestTimetoBuyandSellStockIV()
                .maxProfit(2, new int[]{1, 2, 3}));
    }

    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        final int TAKE = 0;
        final int DROP = 1;
        int[][] dp = new int[k + 1][2];
        for (int i = 0; i <= k; i++) {
            dp[i][TAKE] = -100000000;
        }
        for (int i = 1; i <= n; i++) {
            int price = prices[i - 1];
            for (int j = k; j >= 1; j--) {
                dp[j][TAKE] = Math.max(dp[j][TAKE], dp[j - 1][DROP] - price);
                dp[j][DROP] = Math.max(dp[j][TAKE] + price, dp[j][DROP]);
            }
        }

        int max = 0;
        for (int i = 0; i <= k; i++) {
            max = Math.max(dp[i][DROP], max);
            max = Math.max(dp[i][TAKE], max);
        }

        return max;
    }
}
