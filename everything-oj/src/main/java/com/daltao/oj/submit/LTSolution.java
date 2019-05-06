package com.daltao.oj.submit;

import java.util.*;

public class LTSolution {

    public static void main(String[] args) {
        new Solution().minScoreTriangulation(new int[]{3, 7, 4,5});
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
        int[][] dp;
        int n;
        int[] A;
        public int minScoreTriangulation(int[] A) {
            n = A.length;
            dp = new int[n][n];
            this.A = A;
            for(int i = 0; i < n; i++)
            {
                for(int j = 0; j < n; j++)
                {
                    dp[i][j] = -1;
                }
            }

            int ans = find(0, n - 1);
            System.out.println(Arrays.deepToString(dp));
            return ans;
        }

        public int find(int i, int j)
        {
            if(j - i <= 1)
            {
                return 0;
            }
            if(dp[i][j] == -1)
            {
                dp[i][j] = 0;
                for(int k = i + 1; k <= j - 1; k++){
                    dp[i][j] = Math.max(dp[i][j], find(i, k) + find(k, j) + A[i] * A[j] * A[k]);
                }
            }
            return dp[i][j];
        }
    }
}
