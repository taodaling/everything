package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1051V2Test {
    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1051V2.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1051.class)))
                .setInputFactory(new Generator())
                .build().call()
        );
    }

    public static class Generator extends RandomFactory {

        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(1, 3);
            input.add(n).add(m);
            for (int i = 0; i < m; i++) {
                input.add(String.format("%d %d", nextInt(1, n), nextInt(1, n)));
            }
            return input.end();
        }
    }
}
