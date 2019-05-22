package com.daltao.oj.submit;

import com.daltao.oj.tool.InteractiveTask;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CF1158ETest {

    @Test
    public void test() {
        Assertions.assertTrue(
                TestCaseExecutor.Builder.newBuilder()
                        .setInputFactory(new Generator())
                        .setTestTime(1000)
                        .setActualSolution(() -> new Runnable2OJSolution(new Counter(new MainMethod2Runnable(CF1158E.class))))
                        .setExpectedSolution(() -> new Runnable2OJSolution(() -> {
                        }))
                        .setCheckerFactory(() -> InteractiveTask.newChecker())
                        .setTimeLimitForEachTestCase(1000000)
                        .build().call()

        );
    }

    public static class Counter extends InteractiveTask {
        @Override
        protected boolean interact(FastIO progIO, FastIO sysin) {
            int n = sysin.readInt();
            progIO.cache.append(n).append('\n');
            progIO.flush();

            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            Set<Edge> set = new HashSet<>();
            for (int i = 1; i < n; i++) {
                int f = sysin.readInt();
                int t = sysin.readInt();
                Node a = nodes[f];
                Node b = nodes[t];
                a.nodes.add(b);
                b.nodes.add(a);
                set.add(new Edge(f, t));
            }

            int time = 0;
            while (progIO.hasMore()) {
                char c = progIO.readChar();
                if (c == '!') {
                    break;
                }
                time++;
                if (time > 80) {
                    return false;
                }
                for (int i = 1; i <= n; i++) {
                    nodes[i].dist = progIO.readInt();
                    nodes[i].tag = false;
                }

                for (int i = 1; i <= n; i++) {
                    now++;
                    visit(nodes[i], nodes[i].dist);
                }

                for (int i = 1; i <= n; i++) {
                    progIO.cache.append(nodes[i].tag ? '1' : '0');
                }
                progIO.cache.append('\n');
                progIO.flush();
            }

            for (int i = 1; i < n; i++) {
                int a = progIO.readInt();
                int b = progIO.readInt();
                Edge edge = new Edge(a, b);
                set.remove(edge);
            }

            return set.isEmpty();
        }

        public void visit(Node root, int dist) {
            if (dist <= 0) {
                return;
            }
            for (Node node : root.nodes) {
                if (node.visited == now) {
                    continue;
                }
                node.tag = true;
                root.visited = now;
                visit(node, dist - 1);
            }
        }

        int now;

        protected Counter(Runnable prog) {
            super(prog);
        }

        private static class Node {
            List<Node> nodes = new ArrayList<>();
            boolean tag;
            int visited;
            int dist;
            int id;
        }

        private static class Edge {
            private final int a;
            private final int b;

            private Edge(int a, int b) {
                this.a = Math.min(a, b);
                this.b = Math.max(a, b);
            }

            @Override
            public int hashCode() {
                return a * 31 + b;
            }

            @Override
            public boolean equals(Object obj) {
                Edge edge = (Edge) obj;
                return a == edge.a && b == edge.b;
            }
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 6);
            input.add(n);
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d", i, nextInt(1, i - 1)));
            }
            input = new QueueInput().add("6\n" +
                    "2 1\n" +
                    "3 2\n" +
                    "4 3\n" +
                    "5 4\n" +
                    "6 5\n");
            return input.end();
        }
    }
}
