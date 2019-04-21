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

/**
 * LOJ114 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ËÄÔÂ 20, 2019</pre>
 */
public class LOJ114Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(1000)
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\_LOJ114.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LOJ114.class)))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            long limit = (long) 10;
            input.add(n);
            for (int i = 0; i < n; i++) {
                input.add(nextLong(1, limit));
            }
            int m = nextInt(1, 3);
            input.add(m);
            for (int i = 0; i < m; i++) {
                input.add(nextLong(1, 1L << n));
            }

            return input.end();
        }
    }

} 
