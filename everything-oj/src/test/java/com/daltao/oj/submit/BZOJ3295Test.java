package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BZOJ3295Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ3295.class)))
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int m = io.readInt();

            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(io.readInt());
            }

            for (int i = 0; i < m; i++) {
                io.cache.append(count(list)).append(' ');
                list.remove((Object)io.readInt());
            }

            io.flush();
        }

        private static int count(List<Integer> list) {
            int sum = 0;
            int n = list.size();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < i; j++) {
                    if (list.get(i) < list.get(j)) {
                        sum++;
                    }
                }
            }
            return sum;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 1000);
            int m = nextInt(1, n);
            input.add(n).add(m);
            List<Integer> data = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                data.add(i + 1);
            }
            data = new ArrayList<>(CollectionUtils.shuffle(data));
            for (Integer i : data) {
                input.add(i);
            }
            for (int i = 0; i < m; i++) {
                input.add(data.remove(nextInt(0, data.size() - 1)));
            }
            return input.end();
        }
    }
}
