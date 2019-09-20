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

public class LUOGU4827Test {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4827.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\LUOGU4827_oj.exe")))
        .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 50000);
            int k = nextInt(1, 150);
            input.add(n).add(k);
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d", nextInt(1, i - 1), i));
            }
            return input.end();
        }
    }
}
