package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CF1149DV2Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CF1149D.class)))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CF1149DV2.class)))
                        .setInputFactory(new Generator())
                        .setTestTime(10000)
                        .setTimeLimitForEachTestCase(5000)
                        .build().call()
        );
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 6);
            int m = nextInt(n - 1, n * (n - 1) / 2);
            int a = nextInt(1, 1);
            int b = nextInt(2, 2);
            if (a == b) {
                a--;
            }
            if (a > b) {
                int tmp = a;
                a = b;
                b = tmp;
            }

            List<List<Integer>> edges = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (i >= j) {
                        continue;
                    }
                    edges.add(Arrays.asList(i, j));
                }
            }
            CollectionUtils.shuffle(edges);

            int[] ab = new int[]{a, b};
            input.add(n).add(m).add(a).add(b);
            for (int i = 2; i <= n; i++) {
                List<Integer> edge = Arrays.asList(nextInt(1, i - 1), i);
                input.add(String.format("%d %d %d", edge.get(0), edge.get(1), ab[nextInt(0, 1)]));
                edges.remove(edge);
            }


            for (int i = n - 1; i < m; i++) {
                List<Integer> tail = edges.remove(edges.size() - 1);
                input.add(String.format("%d %d %d", tail.get(0), tail.get(1), ab[nextInt(0, 1)]));
            }

            return input.end();
        }

    }
}
