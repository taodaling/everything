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

public class BZOJ1337Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ1337.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1337.class)))
                .build().call());
    }


    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 1000);
            input.add(n);
            int max = (int) 1e8;
            for (int i = 0; i < n; i++) {
                input.add(String.format("%d %d", nextInt(-max, max), nextInt(-max, max)));
            }
            return input.end();
        }
    }

}
