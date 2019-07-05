package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ2245Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ2245.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2245.class)))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int m = nextInt(1, 1);
            int n = nextInt(1, 1);
            input.add(m).add(n);

            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 3));
            }

            int require = (1 << n) - 1;
            int mask = 0;
            for (int i = 0; i < m; i++) {
                int t = nextInt(0, require);
                if (t == m - 1) {
                    t |= require ^ mask;
                }
                mask |= t;
                for (int j = 0; j < n; j++) {
                    input.add((t >> j) & 1);
                }
            }

            for (int i = 0; i < m; i++) {
                int s = nextInt(0, 5);
                input.add(s);
                int last = 0;
                for (int j = 0; j < s; j++) {
                    int next = nextInt(last + 1, last + 4);
                    input.add(next);
                }
                int weight = -1;
                for (int j = 0; j <= s; j++) {
                    weight = nextInt(weight + 1, weight + 4);
                    input.add(weight);
                }
            }

            return input.end();
        }
    }
}
