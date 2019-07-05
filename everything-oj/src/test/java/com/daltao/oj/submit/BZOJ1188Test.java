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
 * BZOJ1188 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 15, 2019</pre>
 */
public class BZOJ1188Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1188.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\taoda\\Documents\\oj-c\\BZOJ1188_oj.exe")))
                .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(1);
            int n = nextInt(1, 6);
            input.add(n);
            for (int i = 0; i < n; i++) {
                int v = nextInt(0, 1);
                input.add(v);
            }
            return input.end();
        }
    }

} 
