package com.daltao.oj.submit;


import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;

import java.util.PriorityQueue;

public class LUOGU3377Test {
    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int n = io.readInt();
            int m = io.readInt();

            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].k1 = io.readInt();
                nodes[i].k2 = io.readInt();
                nodes[i].queue = new PriorityQueue<>();
                nodes[i].queue.add(nodes[i]);
            }

            for (int i = 0; i < m; i++) {
                int t = io.readInt();
                if (t == 1) {
                    int x = io.readInt();
                    int y = io.readInt();
                }
            }
        }

        public static class Node implements Comparable<Node> {
            PriorityQueue<Node> queue;
            int k1;
            int k2;

            @Override
            public int compareTo(Node o) {
                return k1 == o.k1 ? Integer.compare(k2, o.k2) : Integer.compare(k1, o.k1);
            }
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 10);
            int m = nextInt(1, 10);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(1, 100));
            }
            for (int i = 0; i < m; i++) {
                int t = nextInt(1, 2);
                if (t == 1) {
                    int x = nextInt(1, n);
                    int y = nextInt(1, n);
                    input.add(String.format("%d %d %d", t, x, y));
                } else {
                    int x = nextInt(1, n);
                    input.add(String.format("%d %d", t, x));
                }
            }
            return input.end();
        }
    }
} 
