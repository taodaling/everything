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

public class BZOJ4870Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ4870_oj.exe")))
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ4870.class)))
        .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 10);
            int k = nextInt(1, 10);
            int p = nextInt(1, 10);
            int r = nextInt(0, k - 1);
            return input.add(n).add(p).add(k).add(r).end();
        }
    }
}