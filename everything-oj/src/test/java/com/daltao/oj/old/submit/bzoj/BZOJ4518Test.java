package com.daltao.oj.old.submit.bzoj;

import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ4518Test {
    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setTestTime(1000)
                .setExpectedSolution(new OJMainSolution(Solution.class))
                .setActualSolution(new OJMainSolution(BZOJ4518.class))
                .setInputFactory(new Generator())
                .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int m = io.readInt();
            int[] s = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                int b = io.readInt();
                s[i] = s[i - 1] + b;
            }

            long[][] dp = new long[m + 1][n + 1];
            for (int i = 0; i <= m; i++) {
                for (int j = 0; j <= n; j++) {
                    if (i == 0 || j == 0) {
                        dp[i][j] = (long) 1e16;
                    }
                }
            }
            dp[0][0] = 0;

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    dp[i][j] = (long) 1e16;
                    for (int k = 0; k <= j; k++) {
                        dp[i][j] = Math.min(dp[i][j], dp[i - 1][k] + pow2(m * (s[j] - s[k]) - s[n]));
                    }
                }
            }

            io.cache.append(dp[m][n] / m);
            io.flush();
        }

        public static long pow2(long x) {
            return x * x;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 2);
            int m = nextInt(1, 3);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 30000));
            }
            input.end();
            //input = new QueueInput().add(1).add(2).add(2).end();
            return input;
        }
    }
}
