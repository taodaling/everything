package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * BZOJ2244 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ËÄÔÂ 13, 2019</pre>
 */
public class BZOJ2244Test {

    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2244.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\_BZOJ2244.exe")))
                .setCheckerFactory(Checker::new)
                .setTestTime(1000).build().call());
    }

    public static class Checker implements com.daltao.test.Checker {
        @Override
        public boolean check(Input expected, Input actual, Input input) {
            if (!expected.read().equals(actual.read())) {
                return false;
            }
            while (expected.available() && actual.available()) {
                if (Math.abs(Double.parseDouble(expected.read().toString()) - Double.parseDouble(actual.read().toString())) > 1e-5) {
                    return false;
                }
            }
            return expected.available() == actual.available();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 30);
            input.add(n);
            for (int i = 0; i < n; i++) {
                input.add(String.format("%d %d", nextInt(0, (int) 1e9), nextInt(0, (int) 1e9)));
            }
            return input.end();
        }
    }

} 
