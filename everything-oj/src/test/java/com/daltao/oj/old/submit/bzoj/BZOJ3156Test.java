package com.daltao.oj.old.submit.bzoj;

import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ3156Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setExpectedSolution(new OJMainSolution(Solution.class))
                        .setActualSolution(new OJMainSolution(BZOJ3156.class))
                        .setInputFactory(new Generator())
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            long[] dp = new long[n + 1];
            for (int i = 1; i <= n; i++) {
                int fee = io.readInt();
                dp[i] = Long.MAX_VALUE;
                for (int j = 0; j < i; j++) {
                    dp[i] = Math.min(dp[i], dp[j] + fee + L(i - j - 1));
                }
            }
            io.cache.append(dp[n]);
            io.flush();
        }

        public static long L(long n) {
            return n * (n + 1) / 2;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 30);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                builder.append(nextInt(1, 1000000000)).append(' ');
            }
            input.add(n).add(builder.toString());
            input.end();
            return input;
        }
    }
}
