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

public class BZOJ2208Test {

    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2208.class)))
                .build().call());
    }

    public static class Solution {
        public static void main(String[] args) {
            new Solution().solve();
        }

        public static class Node {
            boolean visited;
            List<Node> next = new ArrayList<>();
        }

        public void solve() {
            FastIO io = new FastIO();
            int n = io.readInt();
            Node[] nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if(io.readChar() == '1')
                    nodes[i].next.add(nodes[j]);
                }
            }

            long total = 0;
            for (int i = 0; i < n; i++) {
                for (Node node : nodes) {
                    node.visited = false;
                }
                dfs(nodes[i]);

                for (Node node : nodes) {
                    if (node.visited) {
                        total++;
                    }
                }
            }

            io.cache.append(total);
            io.flush();
        }

        public void dfs(Node root) {
            if (root.visited) {
                return;
            }
            root.visited = true;
            for (Node node : root.next) {
                dfs(node);
            }
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input = new QueueInput();
            int n = nextInt(1, 3);
            input.add(n);
            for (int i = 0; i < n; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < n; j++) {
                    builder.append(nextInt(0, 1));
                }
                input.add(builder.toString());
            }
            return input.end();
        }
    }
}
