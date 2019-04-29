package com.daltao.oj.submit;

import java.util.*;

public class LTSolution {

    public static void main(String[] args) {

    }


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    static class Solution {
        int inf = 10000;
        public int[] numMovesStones(int a, int b, int c) {
            return new int[]{minStep(new int[]{a, b, c}), maxStep(new int[]{a, b, c})};
        }

        public int minStep(int[] data)
        {
            Arrays.sort(data);
            int[][] dp = new int[100][100];
            for(int i = 0; i < 100; i++)
            {
                for(int j = 0; j < 100; j++)
                {
                    if(i == 0 && j == 0)
                    {
                        dp[i][j] = 0;
                        continue;
                    }
                    dp[i][j] = inf;
                    for(int k = 1; k <= i; k++)
                    {
                        dp[i][j] = Math.min(dp[i][j], dp[i - k][j] + 1);
                    }
                    for(int k = 1; k <= j; k++)
                    {
                        dp[i][j] = Math.min(dp[i][j], dp[i][j - k] + 1);
                    }
                    for(int k = 0; k < j; k++)
                    {
                        dp[i][j] = Math.min(dp[i][j], dp[k][j - k - 1] + 1);
                    }
                    for(int k = 0; k < i; k++)
                    {
                        dp[i][j] = Math.min(dp[i][j], dp[k][i - k - 1]);
                    }
                }
            }
            return dp[data[1] - data[0]][data[2] - data[1]];
        }

        public int maxStep(int[] data)
        {
            Arrays.sort(data);
            int[][] dp = new int[100][100];
            for(int i = 0; i < 100; i++)
            {
                for(int j = 0; j < 100; j++)
                {
                    if(i == 0 && j == 0)
                    {
                        dp[i][j] = 0;
                        continue;
                    }
                    dp[i][j] = 0;
                    for(int k = 1; k <= i; k++)
                    {
                        dp[i][j] = Math.max(dp[i][j], dp[i - k][j] + 1);
                    }
                    for(int k = 1; k <= j; k++)
                    {
                        dp[i][j] = Math.max(dp[i][j], dp[i][j - k] + 1);
                    }
                    for(int k = 0; k < j; k++)
                    {
                        dp[i][j] = Math.max(dp[i][j], dp[k][j - k - 1] + 1);
                    }
                    for(int k = 0; k < i; k++)
                    {
                        dp[i][j] = Math.max(dp[i][j], dp[k][i - k - 1]);
                    }
                }
            }
            return dp[data[1] - data[0]][data[2] - data[1]];
        }
    }
}
