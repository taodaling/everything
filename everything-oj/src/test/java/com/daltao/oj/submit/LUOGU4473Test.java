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

public class LUOGU4473Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\LUOGU4473_oj.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4473.class)))
                .setInputFactory(new Generator())
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(1, 3);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < m; j++) {
                    builder.append(nextInt(0, 3)).append(' ');
                }
                input.add(builder.toString());
            }
            for (int i = 0; i < n; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < m; j++) {
                    builder.append(nextInt(0, 3)).append(' ');
                }
                input.add(builder.toString());
            }
            for (int i = 0; i < 3; i++) {
                input.add(String.format("%d %d", nextInt(1, n), nextInt(1, m)));
            }
            return input.end();
        }
    }
}
