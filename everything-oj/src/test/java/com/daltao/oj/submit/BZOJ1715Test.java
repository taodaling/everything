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

public class BZOJ1715Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1715.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ1715.exe")))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(1);
            int n = nextInt(1, 5);
            int m = nextInt(1, 2 * n);
            int w = nextInt(1, 2 * n);
            input.add(n).add(m).add(w);
            for (int i = 0; i < m; i++) {
                input.add(String.format("%d %d %d", nextInt(1, n), nextInt(1, n), nextInt(0, 10)));
            }
            for (int i = 0; i < w; i++) {
                input.add(String.format("%d %d %d", nextInt(1, n), nextInt(1, n), nextInt(0, 10)));
            }
            return input.end();
        }
    }
}
