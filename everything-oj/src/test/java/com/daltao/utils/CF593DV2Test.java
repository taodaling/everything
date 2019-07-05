package com.daltao.utils;


import com.daltao.oj.submit.CF593D;
import com.daltao.oj.submit.CF593DV2;
import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * CF593DV2 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>五月 29, 2019</pre>
 */
public class CF593DV2Test {

    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CF593DV2.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CF593D.class)))
                .setInputFactory(new Generator())
                .build().call()
        );
    }

    static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 2);
            int m = nextInt(5, 10);
            input.add(String.format("%d %d", n, m));
            for (int i = 1; i < n; i++) {
                int a = nextInt(1, i);
                int b = i + 1;
                long x = nextLong(1, (long) 1e18);
                input.add(String.format("%d %d %d", a, b, x));
            }
            for (int i = 0; i < m; i++) {
                int t = nextInt(1, 2);
                if (t == 1) {
                    int a = nextInt(1, n);
                    int b = nextInt(1, n);
                    long y = nextLong(1, (long) 1e18);
                    input.add(String.format("%d %d %d %d", t, a, b, y));
                } else {
                    int p = nextInt(1, n - 1);
                    long c = nextLong(1, (long) 1e18);
                    input.add(String.format("%d %d %d", t, p, c));
                }
            }
            return input.end();
        }
    }

}
