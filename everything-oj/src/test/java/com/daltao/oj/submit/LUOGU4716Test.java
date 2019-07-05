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
 * LUOGU4716 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 29, 2019</pre>
 */
public class LUOGU4716Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\taoda\\Documents\\oj-c\\LUOGU4716_oj.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4716.class)))
                .setTestTime(-1)
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(100, 100);
            int m = nextInt(10000, 10000);
            int r = nextInt(1, n);
            input.add(n).add(m).add(r);
            for (int i = 0; i < m; i++) {
                int a = nextInt(1, n);
                int b = nextInt(1, n);
                int w = nextInt(1, m);
                input.add(String.format("%d %d %d", a, b, w));
            }
            return input.end();
        }
    }
} 
