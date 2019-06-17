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

public class BZOJ1412Test {

    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1412.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ1412.exe")))
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(3, 3);
            int m = nextInt(3, 3);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    input.add(nextInt(0, 2));
                }
            }
            return input.end();
        }
    }

}
