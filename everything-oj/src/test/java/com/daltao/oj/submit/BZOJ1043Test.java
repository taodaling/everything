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
 * BZOJ1043 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 10, 2019</pre>
 */
public class BZOJ1043Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1043.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("D:\\DATABASE\\CODE\\oj-c\\_BZOJ1043_OJ.exe")))
                .setTestTime(-1)
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 2);
            input.add(n);
            for (int i = 0; i < n; i++) {
                double r = nextInt(1, 5);
                double x = nextInt(0, 5);
                double y = nextInt(0, 5);
                input.add(String.format("%f %f %f", r, x, y));
            }
            return input.end();
        }
    }
} 
