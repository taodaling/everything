package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * BZOJ4405 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>八月 29, 2019</pre>
 */
public class BZOJ4405Test {

    @Test
    public void test() {
        Assert.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setCheckerFactory(() -> new SChecker())
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ4405.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("D:\\DATABASE\\CODE\\oj-c\\LUOGU4528_OJ.exe")))
                        .setTestTime(10000)
                        .build().call()
        );
    }

    public static class SChecker extends ErrorChecker {
        @Override
        public boolean checkError(FastIO expected, FastIO actual, FastIO input) throws Exception {
            int ans = actual.readInt();
            if (expected.readInt() != ans) {
                return false;
            }
            int t = input.readInt();
            int n = input.readInt();
            int m = input.readInt();
            int e = input.readInt();
            boolean[][] edges = new boolean[n + 1][m + 1];
            int[] used = new int[m + 1];
            for (int i = 0; i < e; i++) {
                int v = input.readInt();
                int u = input.readInt();
                edges[v][u] = true;
            }

            for (int i = 1; i <= n; i++) {
                int which = actual.readInt();
                if (used[which] == 3 || !edges[i][which]) {
                    return false;
                }
                used[which]++;
            }

            int total = 0;
            for (int i = 1; i <= m; i++) {
                if (used[i] <= 1) {
                    total++;
                }
            }

            return total == ans;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();

            input.add(1);
            int n = nextInt(1, 5);
            int m = nextInt((n + 2) / 3, 5);
            int e = nextInt(n, n * m);

            input.add(n).add(m).add(e);

            int[] used = new int[m + 1];
            for (int i = 0; i < n; i++) {
                int which = nextInt(1, m);
                while (used[which] == 3) {
                    which = nextInt(1, m);
                }
                used[which]++;
                input.add(String.format("%d %d", i + 1, which));
            }

            for (int i = n; i < e; i++) {
                input.add(String.format("%d %d", nextInt(1, n), nextInt(1, m)));
            }


            return input.end();
        }
    }
} 
