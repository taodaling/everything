package com.daltao.oj.old.submit.bzoj;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * BZOJ4006 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ËÄÔÂ 9, 2019</pre>
 */
public class BZOJ4006Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ4006.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\BZOJ4006.exe")))
                .setTestTime(1000)
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(n, 5);
            int p = nextInt(1, n);
            input.add(String.format("%d %d %d", n, m, p));
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d %d", nextInt(1, i - 1), i, nextInt(1, 10)));
            }
            for (int i = n - 1; i < m; i++) {
                input.add(String.format("%d %d %d", nextInt(1, n), nextInt(1, n), nextInt(1, 10)));
            }
            Set<Integer> set = new HashSet<>();
            while (set.size() < p) {
                set.add(nextInt(1, n));
            }
            for (Integer i : set) {
                input.add(String.format("%d %d", nextInt(1, p), i));
            }
            return input.end();
        }
    }
} 
