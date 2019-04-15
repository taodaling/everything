package com.daltao.oj.submit;

import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LUOGU4690Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_LUOGU4690.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\LUOGU4690.exe")))
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 100);
            int m = nextInt(1, 100);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 10));
            }
            for (int i = 0; i < m; i++) {
                int t = nextInt(1, 2);
                int l = nextInt(1, n);
                int r = nextInt(l, n);
                int x = nextInt(1, 10);
                if (t == 1) {
                    input.add(String.format("%d %d %d %d", t, l, r, x));
                } else {
                    input.add(String.format("%d %d %d", t, l, r));
                }
            }
            return input.end();
        }
    }
}
