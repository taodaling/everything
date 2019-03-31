package com.daltao.oj.old.submit.bzoj;

import com.daltao.oj.tool.OJMainSolution;
import com.daltao.template.FastIO;
import com.daltao.template.Mathematics;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BZOJ2301Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setExpectedSolution(new OJMainSolution(Solution.class))
                        .setActualSolution(new OJMainSolution(BZOJ2301.class))
                        .setInputFactory(new Generator())
                        .setTestTime(10000)
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int t = io.readInt();

            int a = io.readInt();
            int b = io.readInt();
            int c = io.readInt();
            int d = io.readInt();
            int k = io.readInt();
            int cnt = 0;
            for (int i = a; i <= b; i++) {
                for (int j = c; j <= d; j++) {
                    if (Mathematics.gcd(i, j) == k) {
                        cnt++;
                    }
                }
            }
            io.cache.append(cnt).append('\n');
            io.flush();
        }


    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(1);
            int a = nextInt(1, 100);
            int b = nextInt(a, 300);
            int c = nextInt(1, 100);
            int d = nextInt(c, 300);
            int k = nextInt(1, 100);
            input.add(String.format("%d %d %d %d %d", a, b, c, d, k));
            input.end();
            return input;
        }
    }
}