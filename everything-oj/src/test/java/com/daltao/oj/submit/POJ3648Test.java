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

import java.util.ArrayList;
import java.util.List;

public class POJ3648Test {
    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(1000)
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(POJ3648.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                        .setInputFactory(new Generator())
                        .setCheckerFactory(() -> new Checker())
                        .build().call()
        );
    }

    public static class Checker implements com.daltao.test.Checker {
        @Override
        public boolean check(Input expected, Input actual, Input input) {
            if (!expected.available() || !actual.available() || expected.read().equals("bad luck") != actual.read().equals("bad luck")) {
                return false;
            }

            return true;
        }
    }

    public static class Solution {
        Node[][] all;
        FastIO io = new FastIO(System.in, System.out);

        public static void main(String[] args) {
            new Solution().solve();
        }

        public void solve() {

            int n = io.readInt();
            int m = io.readInt();
            all = new Node[n][2];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    all[i][j] = new Node();
                }
                all[i][0].notTogether.add(all[i][1]);
                all[i][1].notTogether.add(all[i][0]);
            }

            for (int i = 0; i < m; i++) {
                Node a = all[io.readInt()][readGender()];
                Node b = all[io.readInt()][readGender()];
                a.notTogether.add(b);
                b.notTogether.add(a);
            }

            all[0][0].withWife = false;
            all[0][1].withWife = true;

            if (!dfs(1)) {
                io.cache.append("bad luck\n");
                return;
            }

            for (int i = 1; i < n; i++) {
                if (all[i][0].withWife) {
                    io.cache.append(i).append('h').append(' ');
                } else {
                    io.cache.append(i).append('w').append(' ');
                }
            }

            io.flush();
            return;
        }

        public boolean dfs(int i) {
            if (i == all.length) {
                for (Node[] pair : all) {
                    for (Node node : pair) {
                        if (!node.check()) {
                            return false;
                        }
                    }
                }
                return true;
            }
            all[i][0].withWife = false;
            all[i][1].withWife = true;
            if (dfs(i + 1)) {
                return true;
            }

            all[i][0].withWife = true;
            all[i][1].withWife = false;
            if (dfs(i + 1)) {
                return true;
            }
            return false;
        }


        public int readGender() {
            return io.readChar() == 'h' ? 0 : 1;
        }


        public static class Node {
            boolean withWife;
            List<Node> notTogether = new ArrayList<>();

            public boolean check() {
                if (!withWife) {
                    for (Node node : notTogether) {
                        if (!node.withWife) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }

    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 3);
            int m = nextInt(1, 3);
            input.add(String.format("%d %d", n, m));
            for (int i = 0; i < m; i++) {
                String s = "";
                for (int j = 0; j < 2; j++) {
                    int g = nextInt(0, n - 1);
                    String gender = nextInt(0, 1) == 1 ? "h" : "w";
                    s += g + gender + " ";
                }
                input.add(s);
            }
            input.add("0 0");
            return input.end();
        }
    }

}
