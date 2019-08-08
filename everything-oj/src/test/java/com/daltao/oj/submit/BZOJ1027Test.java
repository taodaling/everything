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

public class BZOJ1027Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ1027_oj.exe")))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_BZOJ1027_oj.exe")))
                .setTimeLimitForEachTestCase(3000)
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int m = nextInt(1, 100);
            int n = nextInt(1, 10);

            input.add(m).add(n);

            for (int i = 0; i < m + n; i++) {
                double a = nextDouble();
                double b = nextDouble();
                if (a > b) {
                    double tmp = a;
                    a = b;
                    b = tmp;
                }
                input.add(String.format("%.6f %.6f %.6f", a, b - a, 1 - b));
            }

            return input.end();
        }
    }
}
