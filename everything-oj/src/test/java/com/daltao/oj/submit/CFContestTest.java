package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class CFContestTest {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(CFContest.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                .setTestTime(10000)
                .build().call());
    }


    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int n = io.readInt();
            int k = io.readInt();
            Set<Integer> set = new HashSet<>();
            Deque<Integer> deque = new ArrayDeque<>();
            int[] data = new int[n];
            for (int i = 0; i < n; i++) {
                data[i] = io.readInt();
            }
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < n; j++) {
                    Integer e = data[j];
                    if (set.contains(e)) {
                        set.remove(e);
                        while (!deque.removeLast().equals(e)) {
                            continue;
                        }
                        continue;
                    } else {
                        set.add(e);
                        deque.addLast(e);
                    }
                }
            }

            while (!deque.isEmpty()) {
                io.cache.append(deque.removeFirst()).append(' ');
            }

            io.flush();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 5);
            int k = nextInt(1, 10);
            input.add(n).add(k);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 10));
            }
            input = new QueueInput().add("\n" +
                    "4\n" +
                    "4\n" +
                    "7\n" +
                    "10\n" +
                    "6\n" +
                    "8\n");
            return input.end();
        }
    }

}
