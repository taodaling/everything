package com.daltao.oj.old.submit.bzoj;


import com.daltao.oj.submit.BZOJ1010;
import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1010Test {

    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setTestTime(1000).setInputFactory(new Generator())
                .setActualSolution(new OJMainSolution(BZOJ1010.class))
                .setExpectedSolution(new OJMainSolution(Solution.class))
                .build().call());
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            long l = io.readInt();
            long[] dp = new long[n + 1];
            int[] trace = new int[n + 1];
            long[] sum = new long[n + 1];
            for (int i = 1; i <= n; i++) {
                int c = io.readInt();
                sum[i] = sum[i - 1] + c;
                dp[i] = Long.MAX_VALUE;
                for (int j = 0; j < i; j++) {
                    long possible = dp[j] + pow2(sum[i] - sum[j] + i - j - 1 - l);
                    if (possible < dp[i]) {
                        dp[i] = possible;
                        trace[i] = j;
                    }
                }
            }
            io.cache.append(dp[n]);
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
            int n = nextInt(1, 30);
            input.add(n);
            input.add(nextInt(1, 10000000));
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 10000000));
            }
            //input = new QueueInput();
           // input.add(3).add(8536589).add(2506081).add(9558575).add(7781881);
            input.end();
            return input;
        }
    }
}