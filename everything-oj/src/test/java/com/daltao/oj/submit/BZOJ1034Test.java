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

import java.util.Arrays;

public class BZOJ1034Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1034.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                .build().call());
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO fastIO = new FastIO();
            solve(fastIO);
            fastIO.flush();
        }

        public static void solve(FastIO io) {
            int n = io.readInt();
            int[] a = new int[n + 1];
            int[] b = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                a[i] = io.readInt();
            }
            for (int i = 1; i <= n; i++) {
                b[i] = io.readInt();
            }
            Arrays.sort(a);
            Arrays.sort(b);
            io.cache.append(maxProfit(a, b, n)).append(' ')
                    .append(2 * n - maxProfit(b, a, n));
        }

        public static int maxProfit(int[] a, int[] b, int n) {
            int[][] dp = new int[n + 1][n + 1];
            dp[0][0] = 0;
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    dp[i][j] = dp[i][j - 1];
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j]);
                    int cmp = a[i] > b[i] ? 2 : a[i] == b[i] ? 1 : 0;
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 1] + cmp);
                }
            }
            return dp[n][n];
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 2);
            input.add(n);
            for (int i = 0; i < n + n; i++) {
                input.add(nextInt(0, 9));
            }
            return input.end();
        }
    }
}
