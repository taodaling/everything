package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class LC818 {
    public static class Solution {
        public int racecar(int target) {

            if (target == 0) {
                return 0;
            }
            int log2 = 32 - Integer.numberOfLeadingZeros(target);
            int length = 1 << (log2);

            final int INC = 0;
            final int DEC = 1;
            Node[][] nodes = new Node[length][2];
            for (int i = 0; i < length; i++) {
                for (int k = 0; k < 2; k++) {
                    nodes[i][k] = new Node();
                    nodes[i][k].neighbors = new ArrayList<>(log2 + 1);
                    //nodes[i][j][k].id = String.format("x:%d,speed:%d", i, (k == INC ? 1 : -1) * (1 << j));
                }
            }

            for (int i = 0; i < length; i++) {
                nodes[i][0].neighbors.add(new Edge(nodes[i][1], 1));
                nodes[i][1].neighbors.add(new Edge(nodes[i][0], 1));

                for (int k = 1; k <= log2; k++) {
                    int change = (1 << k) - 1;
                    if (i + change < length) {
                        nodes[i][INC].neighbors.add(new Edge(nodes[i + change][DEC], k + 1));
                    }
                    if (i - change >= 0) {
                        nodes[i][DEC].neighbors.add(new Edge(nodes[i - change][INC], k + 1));
                    }
                }
            }

            Deque<Node> deque = new ArrayDeque<>(length);
            deque.addLast(nodes[0][INC]);
            nodes[0][INC].dp = 0;
            nodes[0][INC].inque = true;
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                head.inque = false;
                for (Edge edge : head.neighbors) {
                    if (optimize(head, edge.dst, edge.fee)) {
                        if (!edge.dst.inque) {
                            edge.dst.inque = true;
                            deque.addLast(edge.dst);
                        }
                    }
                }
            }

            int min = Math.min(nodes[target][0].dp, nodes[target][1].dp);

            return min - 1;
        }

        public static boolean optimize(Node from, Node to, int fee) {
            if (to.dp <= from.dp + fee) {
                return false;
            }
            to.dp = from.dp + fee;
            /*to.last = from;*/
            return true;
        }

        public static class Node {
            List<Edge> neighbors;
            int dp = (int) 1e8;
            boolean inque;
            /*Node last;*/
            //String id;

            /*@Override
            public String toString() {
                return (last != null ? last.toString() + "->" : "") + id;
            }*/
        }

        public static class Edge {
            final Node dst;
            final int fee;

            public Edge(Node dst, int fee) {
                this.dst = dst;
                this.fee = fee;
            }
        }
    }


}
