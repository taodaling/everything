package com.daltao.template;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class EdmondBlossom {
    private static class Node {
        List<Node> next = new ArrayList<>(2);
        Node pre;
        boolean inTree;
        Node mate;

        Node p;
        int rank;
        Node tip;
        int depth;
        int id;

        @Override
        public String toString() {
            if(mate == null) {
                return "" + id;
            }
            return "" + id + "(" + mate.id + ")";
        }

        Node find() {
            return p.p == p ? p : (p = p.find());
        }

        static Node min(Node a, Node b) {
            return a.depth <= b.depth ? a : b;
        }

        static void merge(Node a, Node b) {
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
                a.tip = min(a.tip, b.tip);
            } else {
                a.p = b;
                b.tip = min(a.tip, b.tip);
            }
        }
    }

    Node[] nodes;
    int n;
    Deque<Node> deque;

    public EdmondBlossom(int n) {
        this.n = n;
        deque = new ArrayDeque<>(n);
        nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
    }

    public void addEdge(int aId, int bId) {
        nodes[aId].next.add(nodes[bId]);
        nodes[bId].next.add(nodes[aId]);
        if (nodes[aId].mate == null && nodes[bId].mate == null) {
            nodes[aId].mate = nodes[bId];
            nodes[bId].mate = nodes[aId];
        }
    }

    public int maxMatch() {
        for (Node node : nodes) {
            if (node.mate == null) {
                match(node);
            }
        }
        int match = 0;
        for (Node node : nodes) {
            if (node.mate != null) {
                match++;
            }
        }
        return match / 2;
    }

    private void expand(Node tail) {
        while (tail != null) {
            Node next = tail.pre.mate;
            tail.mate = tail.pre;
            tail.pre.mate = tail;
            tail = next;
        }
    }


    private boolean match(Node since) {
        for (Node node : nodes) {
            node.inTree = false;
            node.pre = null;
            node.p = node;
            node.rank = 0;
            node.tip = node;
            node.depth = 0;
        }
        deque.clear();
        since.inTree = true;
        deque.add(since);

        while (!deque.isEmpty()) {
            Node head = deque.removeFirst();
            Node tip = head.find().tip;
            boolean even = tip.depth % 2 == 0;
            for (Node next : head.next) {
                if ((next == head.mate) == even) {
                    continue;
                }
                if (next.mate == null) {
                    next.pre = head;
                    expand(next);
                    return true;
                }
                if (!next.inTree) {
                    next.pre = head;
                    next.depth = tip.depth + 1;
                    next.inTree = true;
                    deque.addLast(next);
                    continue;
                }
                //even circle
                if (!even) {
                    continue;
                }
                //odd circle
                else {
                    //shrink
                    blossom(head, next);
                }
            }
        }

        return false;
    }

    private void blossom(Node a, Node b) {
        a = a.find().tip;
        b = b.find().tip;
        while (a.depth != b.depth) {
            if (a.depth < b.depth) {
                Node tmp = a;
                a = b;
                b = tmp;
            }
            //find odd node
            if (a.depth % 2 == 1) {
                deque.addFirst(a);
            }
            Node.merge(a, a.pre);
            a = a.find().tip;
        }
        Node.merge(a, b);
    }
}
