package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ3811Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\BZOJ3811.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\_BZOJ3811.exe")))
                        .setInputFactory(new Generator())
                        .setCheckerFactory(Checker::new)
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Checker implements com.daltao.test.Checker {
        @Override
        public boolean check(Input expected, Input actual, Input input) {
            if (!actual.available()) {
                return false;
            }
            return Math.abs(Double.parseDouble(expected.read().toString())
                    - Double.parseDouble(actual.read().toString())) < 1e-2;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 1000);
            int k = nextInt(1, 2);

            input.add(n).add(k);
            for (int i = 0; i < n; i++) {
                input.add(nextLong(1, 1L << 32));
            }

            return input.end();
        }
    }
}
