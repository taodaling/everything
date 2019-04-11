package com.daltao.oj.submit;


import com.daltao.oj.old.submit.bzoj.BZOJ1857;
import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1857V2Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1857.class)))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1857V3.class)))
                        .setTestTime(1000)
                        .build().call()
        );
    }

    private static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            for (int i = 0; i < 8; i++) {
                input.add(nextInt(1, 10));
            }
            for (int i = 0; i < 3; i++) {
                input.add(nextInt(1, 1));
            }
            return input.end();
        }
    }
}
