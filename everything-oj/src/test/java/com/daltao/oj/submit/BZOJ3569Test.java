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

public class BZOJ3569Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ3569.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                        .setInputFactory(new Generator())
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int n = io.readInt();
            int m = io.readInt();

            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
            }
            int[][] edges = new int[m + 1][3];
            for (int i = 1; i <= m; i++) {
                edges[i][0] = io.readInt();
                edges[i][1] = io.readInt();
            }

            int q = io.readInt();
            for (int i = 1; i <= q; i++) {
                for (int j = 1; j <= n; j++) {
                    nodes[j].rank = 0;
                    nodes[j].p = nodes[j];
                }
                int k = io.readInt();
                for (int j = 0; j < k; j++) {
                    edges[io.readInt()][2] = i;
                }
                for (int j = 1; j <= m; j++) {
                    if (edges[j][2] == i) {
                        continue;
                    }
                    Node.union(nodes[edges[j][0]], nodes[edges[j][1]]);
                }
                boolean connected = true;
                for (int j = 1; j < n; j++) {
                    if (nodes[j].find() != nodes[j + 1].find()) {
                        connected = false;
                        break;
                    }
                }
                io.cache.append(connected ? "Connected" : "Disconnected").append('\n');
            }
            io.flush();
        }

        public static class Node {
            Node p;
            int rank;

            public Node find() {
                return p == p.p ? p : (p = p.find());
            }

            public static void union(Node a, Node b) {
                a = a.find();
                b = b.find();
                if (a == b) {
                    return;
                }
                if (a.rank == b.rank) {
                    a.rank++;
                }
                if (a.rank > b.rank) {
                    b.p = a;
                } else {
                    a.p = b;
                }
            }
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 2);
            int m = nextInt(1, 3);
            input.add(String.format("%d %d", n, m));
            for (int i = 0; i < m; i++) {
                int a = nextInt(1, n - 1);
                int b = nextInt(a + 1, n);
                input.add(String.format("%d %d", a, b));
            }
            int q = nextInt(1, 1);
            input.add(q);

            for (int i = 0; i < q; i++) {
                List<Integer> set = new ArrayList<>();
                for (int j = 1; j <= m; j++) {
                    set.add(j);
                }
                int k = nextInt(1, Math.min(15, m));
                input.add(k);
                while (k > 0) {
                    input.add(set.remove(nextInt(0, set.size() - 1)));
                    k--;
                }
            }

            return input.end();
        }
    }
}
