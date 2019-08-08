package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1029Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1029V2.class)))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1029.class)))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 5);
            input.add(n);
            for (int i = 0; i < n; i++) {
                int a = nextInt(1, 100);
                int b = nextInt(1, 100);
                input.add(String.format("%d %d", a, b));
            }
            return input.end();
        }
    }
}
