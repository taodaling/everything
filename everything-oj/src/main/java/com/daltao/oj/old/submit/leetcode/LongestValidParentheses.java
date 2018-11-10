package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/8/26.
 */
public class LongestValidParentheses {
    private static final char LEFT = '(';
    private static final char RIGHT = ')';

    public int longestValidParentheses(String s) {
        int n = s.length();
        char[] data = s.toCharArray();
        int[] dp = new int[n + 1];
        int max = 0;
        for (int i = n - 1; i >= 0; i--) {
            int nextIndex = i + 1;
            //condition 1
            if (data[i] == RIGHT) {
                dp[i] = 0;
            }
            //check for not exceeding the bound
            else if (nextIndex >= n) {
                dp[i] = 0;
            }
            //condition 2
            else if (data[nextIndex] == RIGHT) {
                dp[i] = dp[i + 2] + 2;
            }
            //condition 3 and 4
            else if (data[nextIndex] == LEFT) {
                int endIndex = dp[nextIndex] + nextIndex;
                //check for not exceeding the bound
                if (endIndex >= n || data[endIndex] == LEFT) {
                    dp[i] = 0;
                }
                //conditon 4
                else {
                    //DP[i]=DP[i+1]+2+DP[DP[i+1]+i+2]
                    dp[i] = dp[nextIndex] + 2 + dp[endIndex + 1];
                }
            }

            if (dp[i] > max) {
                max = dp[i];
            }
        }
        return max;
    }
}
