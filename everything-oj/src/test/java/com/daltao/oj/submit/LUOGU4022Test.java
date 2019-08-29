package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LUOGU4022Test {
    @Test
    public void test(){
        Assertions.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU4022.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("D:\\DATABASE\\CODE\\oj-c\\LUOGU4022_OJ.exe")))
        .setTestTime(10000).build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 2);
            int m = nextInt(1, 2);
            input.add(n).add(m);
            for (int i = 1; i <= m + n; i++) {
                int len = nextInt(1, 10);
                input.add(String.valueOf(RandomUtils.getRandomCharacterSequence(random, '0', '1', len)));
            }
            return input.end();
        }
    }
}
