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

public class BZOJ3938Test {
    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setTestTime(1000)
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ3938.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ3938.exe")))
                .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 100);
            int m = nextInt(1, 1000);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(-1990, 10234));
            }
            int time = 0;
            for (int i = 0; i < m; i++) {
                time += nextInt(0, 100);
                String type = nextInt(0, 1) == 0 ? "command" : "query";
                if (type.equals("command")) {
                    input.add(String.format("%d %s %d %d", time, type, nextInt(1, n), nextInt(-10000, 10000)));
                } else {
                    input.add(String.format("%d %s", time, type));
                }
            }
            return input.end();
        }
    }
}
