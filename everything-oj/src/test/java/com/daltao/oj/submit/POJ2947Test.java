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

public class POJ2947Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(POJ2947.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_POJ2947.exe")))
                        .setTestTime(10000)
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        String[] date = new String[]{
                "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"
        };

        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(1, 3);
            input.add(n).add(m);
            for (int i = 0; i < m; i++) {
                int num = nextInt(1, 3);
                input.add(num).add(date[nextInt(0, 6)])
                        .add(date[nextInt(0, 6)]);
                for (int j = 0; j < num; j++) {
                    input.add(nextInt(1, n));
                }
            }
            return input.end();
        }
    }
}
