package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BZOJ3261Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(1000)
                        .setInputFactory(new Generator())
                        .setActualSolution((() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\BZOJ3261.exe"))))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int n = io.readInt();
            int m = io.readInt();
            List<Integer> list = new ArrayList();
            list.add(null);
            for (int i = 0; i < n; i++) {
                list.add(io.readInt());
            }

            for (int i = 0; i < m; i++) {
                char t = io.readChar();
                if (t == 'A') {
                    list.add(io.readInt());
                } else {
                    int l = io.readInt();
                    int r = io.readInt();
                    int x = io.readInt();
                    for (int j = list.size() - 1; j >= r; j--) {
                        x ^= list.get(j);
                    }
                    int max = x;
                    for (int j = r - 1; j >= l; j--) {
                        x ^= list.get(j);
                        max = Math.max(max, x);
                    }
                    io.cache.append(max).append('\n');
                }
            }
            io.flush();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 1000);
            int m = nextInt(1, 1000);
            input.add(n).add(m);
            int total = n;
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 10000000));
            }
            for (int i = 0; i < m; i++) {
                int t = nextInt(1, 2);
                if (t == 1) {
                    input.add(String.format("%c %d", 'A', nextInt(1, 10000000)));
                    total++;
                } else {
                    int l = nextInt(1, total);
                    int r = nextInt(1, total);
                    if (l > r) {
                        int tmp = l;
                        l = r;
                        r = tmp;
                    }
                    input.add(String.format("%c %d %d", 'Q', l, r));
                }
            }


            return input.end();
        }
    }
}
