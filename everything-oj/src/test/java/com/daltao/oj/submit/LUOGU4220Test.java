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

public class LUOGU4220Test {

    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\LUOGU4220_oj.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4220.class)))
                .setTimeLimitForEachTestCase(10000).setTestTime(10000).setFailInputRecord((x) -> {})
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(100000, 100000);
            input.add(n);
            for (int i = 0; i < 3; i++) {
                buildTree(input, n);
            }
            return input.end();
        }

        public void buildTree(QueueInput input, int n) {
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d %d", nextInt(1, i - 1), i, nextInt(0, 10)));
            }
        }
    }
}
