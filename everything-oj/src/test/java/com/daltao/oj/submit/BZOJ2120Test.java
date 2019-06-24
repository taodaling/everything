package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class BZOJ2120Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2120.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                .build().call());
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int n = io.readInt();
            int m = io.readInt();
            int[] colors = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                colors[i] = io.readInt();
            }
            for (int i = 1; i <= m; i++) {
                char c = io.readChar();
                if (c == 'Q') {
                    int l = io.readInt();
                    int r = io.readInt();
                    Set<Integer> set = new HashSet<>();
                    for (int j = l; j <= r; j++) {
                        set.add(colors[j]);
                    }
                    io.cache.append(set.size()).append('\n');
                } else {
                    int p = io.readInt();
                    int v = io.readInt();
                    colors[p] = v;
                }
            }
            io.flush();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 4);
            int m = nextInt(1, 4);
            input.add(n).add(m);
            for (int i = 1; i <= n; i++) {
                input.add(nextInt(1, 4));
            }

            for (int i = 0; i < m; i++) {
                if (nextInt(0, 1) == 0) {
                    input.add('Q');
                    int l = nextInt(1, n);
                    int r = nextInt(1, n);
                    if (l > r) {
                        int tmp = l;
                        l = r;
                        r = tmp;
                    }
                    input.add(l).add(r);
                } else {
                    input.add('R');
                    int p = nextInt(1, n);
                    int c = nextInt(1, 4);
                    input.add(p).add(c);
                }
            }
            return input.end();
        }
    }
} 
