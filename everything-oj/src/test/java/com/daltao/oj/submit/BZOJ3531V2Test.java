package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ3531V2Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ3531V2.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ3531.class)))
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 50);
            int q = nextInt(1, 50);
            int cLimit = 3;
            input.add(n).add(q);
            int[] colors = new int[n + 1];
            for (int i = 0; i < n; i++) {
                int w = nextInt(1, cLimit);
                int c = nextInt(1, cLimit);
                input.add(String.format("%d %d", w, c));
                colors[i + 1] = c;
            }
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d", nextInt(1, i - 1), i));
            }

            for (int i = 0; i < q; i++) {
                switch (nextInt(1, 4)) {
                    case 1: {
                        //CC
                        int x = nextInt(1, n);
                        int c = nextInt(1, cLimit);
                        colors[x] = c;
                        input.add(String.format("CC %d %d", x, c));
                        break;
                    }
                    case 2: {
                        //CW
                        int x = nextInt(1, n);
                        int w = nextInt(1, cLimit);
                        input.add(String.format("CW %d %d", x, w));
                        break;
                    }
                    case 3:
                    case 4: {
                        int x = nextInt(1, n);
                        int j;
                        for (j = x + 1; colors[x] != colors[(j - 1) % n + 1]; j++) ;
                        j = (j - 1) % n + 1;
                        if (nextInt(0, 1) == 0) {
                            input.add(String.format("QS %d %d", x, j));
                        } else {
                            input.add(String.format("QM %d %d", x, j));
                        }
                        break;
                    }
                }
            }
            return input.end();
        }
    }
}
