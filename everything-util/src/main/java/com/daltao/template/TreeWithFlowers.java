package com.daltao.template;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TreeWithFlowers {
    private static class Node {
        List<Node> next = new ArrayList<>(2);
        Node nextElementInCircle;
        Node entry;
        Node partner;
        int id;
        int color;

        Node p = this;
        int rank = 0;

        Node currentLevel = this;

        public Node find() {
            return p == p.p ? p : (p = p.find());
        }

        public static void merge(Node a, Node b) {
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

    private Node[] nodes;

    public TreeWithFlowers(int n) {
        nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
    }

    public void addEdge(int i, int j) {
        nodes[i].next.add(nodes[j]);
        nodes[j].next.add(nodes[i]);
    }

    public int partnerOf(int nodeId) {
        if (nodes[nodeId].partner == null) {
            return -1;
        }
        return nodes[nodeId].partner.id;
    }

    public boolean isMatched(int nodeId) {
        return nodes[nodeId].partner != null;
    }

    private void prehandle() {
        for (Node node : nodes) {
            node.color = -1;
            node.p = node;
            node.rank = 0;
            node.currentLevel = node;
        }
    }

    public boolean match(int nodeId) {
        return match(nodes[nodeId], 0);
    }

    Deque<Node> stack = new ArrayDeque<>();

    private boolean match(Node root, int color) {
        if (root.color != -1) {
            if (color != root.color) {
                shrinkSince(root);
            }
            return false;
        }
        root.color = color;
        for (Node node : root.next) {
            if (node.find() == root.find()) {
                continue;
            }
            if (release(node, color ^ 1)) {
                root.partner = node;
                node.partner = root;
                return true;
            }
        }
        return false;
    }


    public void shrinkSince(Node root) {
        Node proxy = new Node();
        proxy.color = 0;
        Node next = root;
        Node breakCondition = root.find().currentLevel;
        while (true) {
            Node last = stack.removeLast();
            last.nextElementInCircle = next;
            next = last;
            Node.merge(proxy, last);
            if (last == breakCondition) {
                break;
            }
        }
        proxy.find().currentLevel = proxy;
        stack.addLast(proxy);
    }


    private boolean release(Node root, int color) {
        if (root.color != -1) {
            if (root.color != color) {
                shrinkSince(root);
            }
            return false;
        }
        root.color = color;
        if (root.partner != null) {
            match(root.partner, color ^ 1);
        }
        return root.partner == null;
    }
}
