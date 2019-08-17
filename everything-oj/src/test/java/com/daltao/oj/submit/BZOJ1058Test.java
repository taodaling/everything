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

public class BZOJ1058Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_BZOJ1058_oj.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1058.class)))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 5);
            int m = nextInt(1, 10);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 10));
            }


            for (int i = 0; i < m; i++) {
                switch (nextInt(1, 3)) {
                    case 1:
                        input.add(String.format("INSERT %d %d", nextInt(1, n), nextInt(1, 10)));
                        break;
                    case 2:
                        input.add("MIN_GAP");
                        break;
                    case 3:
                        input.add("MIN_SORT_GAP");
                        break;
                }
            }

            return input.end();
        }
    }
}
