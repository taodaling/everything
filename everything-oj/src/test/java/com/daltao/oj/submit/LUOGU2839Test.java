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

import java.util.HashSet;
import java.util.Set;

public class LUOGU2839Test {
    @Test
    public void test(){
        Assertions.assertTrue(new TestCaseExecutor.Builder()
        .setInputFactory(new Generator())
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(LUOGU2839.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\LUOGU2839_oj.exe")))
        .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(4, 5);
            input.add(n);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(0, 100));
            }
            int q = nextInt(1, 5);
            input.add(q);
            for (int i = 0; i < q; i++) {
                Set<Integer> remainderSet = new HashSet<>();
                while (remainderSet.size() < 4) {
                    int r = nextInt(0, n - 1);
                    remainderSet.add(r);
                }
                StringBuilder builder = new StringBuilder();
                for (Integer r : remainderSet) {
                    builder.append(r).append(' ');
                }
                input.add(builder.toString());
            }
            return input.end();
        }
    }
}
