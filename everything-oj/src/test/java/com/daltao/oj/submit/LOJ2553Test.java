package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class LOJ2553Test {

    @Test
    public void test(){
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\LOJ2553_oj.exe")))
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LOJ2553.class)))
        .setTestTime(10000).setTimeLimitForEachTestCase(50000)
                .setFailInputRecord(x -> {}).build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(200000, 200000);
            input.add(n);
            createTree(input, n);
            createTree(input, n);
            return input.end();
        }

        public void createTree(QueueInput input, int n) {
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d %d", nextInt(1, i - 1), i, nextInt(0, 10)));
            }
        }
    }
}
