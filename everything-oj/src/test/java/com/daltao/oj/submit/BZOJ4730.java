package com.daltao.oj.submit;

import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BZOJ4730 {
    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_BZOJ4730.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ4730.exe")))
                .setTestTime(1000)
                .setInputFactory(new Generator())
                .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(1);
            int n = nextInt(1, 10);
            input.add(n);

            List<int[]> edges = new ArrayList<>();
            for (int i = 2; i <= n; i++) {
                if (nextInt(0, 1) == 1) {
                    edges.add(new int[]{nextInt(1, i - 1), i});
                }
            }

            input.add(edges.size());
            for (int[] edge : edges) {
                input.add(String.format("%d %d", edge[0], edge[1]));
            }
            return input.end();
        }
    }
}
