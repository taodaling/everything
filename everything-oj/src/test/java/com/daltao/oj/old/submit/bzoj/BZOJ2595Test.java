package com.daltao.oj.old.submit.bzoj;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * BZOJ2595 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>四月 8, 2019</pre>
 */
public class BZOJ2595Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(1000)
                        .setInputFactory(new Generator())
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2595.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\_BZOJ2595.exe")))
                        .setCheckerFactory(() -> new Checker() {
                            @Override
                            public boolean check(Input expected, Input actual, Input input) {
                                return actual.available() && expected.available() && expected.read().equals(actual.read());
                            }
                        })
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(1, 3);
            input.add(String.format("%d %d", n, m));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                builder.setLength(0);
                for (int j = 0; j < m; j++) {
                    builder.append(nextInt(0, 1)).append(' ');
                }
                input.add(builder.toString());
            }
            return input.end();
        }
    }

} 
