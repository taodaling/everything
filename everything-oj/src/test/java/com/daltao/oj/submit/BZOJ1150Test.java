package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1150Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1150.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                        .setTestTime(-1)
                        .build().call()
        );
    }

    public static class Solution {
        static final int INF = (int) 1e9 + 1;

        public static void main(String[] args) {
            new Solution().solve();
        }

        int[][][] dp;
        int[] position;

        public void solve() {
            FastIO io = new FastIO();
            int n = io.readInt();
            int k = io.readInt();
            position = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                position[i] = io.readInt();
            }

            dp = new int[n + 1][k + 1][2];
            for (int i = 0; i <= n; i++) {
                for (int j = 0; j <= k; j++) {
                    for (int t = 0; t < 2; t++) {
                        dp[i][j][t] = -1;
                    }
                }
            }
            for (int i = 0; i <= k; i++) {
                for (int j = 0; j < 2; j++) {
                    dp[0][i][j] = INF;
                }
            }
            dp[0][0][0] = 0;
            int ans = Math.min(dp(n, k, 0), dp(n, k, 1));
            io.cache.append(ans);
            io.flush();
        }


        public int dp(int i, int j, int k) {
            if (i <= 1) {
                return j == 0 && k == 0 ? 0 : INF;
            }
            if (j == 0) {
                return k == 0 ? 0 : INF;
            }
            if (dp[i][j][k] == -1) {
                dp[i][j][k] = INF;
                if (k == 0) {
                    dp[i][j][k] = Math.min(dp[i][j][k], dp(i - 1, j, 0));
                    dp[i][j][k] = Math.min(dp[i][j][k], dp(i - 1, j, 1));
                } else {
                    dp[i][j][k] = Math.min(dp[i][j][k], dp(i - 1, j - 1, 0) + position[i] - position[i - 1]);
                }
            }
            return dp[i][j][k];
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 80);
            input.add(n);
            int k = nextInt(0, n / 2);
            input.add(k);
            int last = 0;
            for (int i = 0; i < n; i++) {
                last += nextInt(1, 5);
                input.add(last);
            }
            return input.end();
        }
    }

}
