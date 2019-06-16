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

import java.util.Set;
import java.util.TreeSet;

/**
 * BZOJ1818 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 14, 2019</pre>
 */
public class BZOJ1818Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\taoda\\Documents\\oj-c\\BZOJ1818_oj.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1818.class)))
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 5);
            input.add(n);
            Set<int[]> set = new TreeSet<>((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);
            for (int i = 0; i < n; i++) {
                int[] xy = new int[2];
                while (true) {
                    xy[0] = nextInt(-10, 10);
                    xy[1] = nextInt(-10, 10);
                    if (!set.contains(xy)) {
                        break;
                    }
                }

                input.add(String.format("%d %d", xy[0], xy[1]));
            }

            return input.end();
        }
    }
} 
