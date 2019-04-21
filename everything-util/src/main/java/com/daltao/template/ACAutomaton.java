package com.daltao.template;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by dalt on 2018/5/25.
 */
public class ACAutomaton {
    static final int MIN_CHARACTER = 'a';
    static final int MAX_CHARACTER = 'z';
    static final int RANGE_SIZE = MAX_CHARACTER - MIN_CHARACTER + 1;
    int size;
    Node root;
    Node buildLast;
    Node matchLast;

    public ACAutomaton() {
        root = new Node();
        size = 1;
    }

    public void beginBuild() {
        buildLast = root;
    }

    public void endBuild() {
        Deque<Node> deque = new ArrayDeque<>(size);

        for (Node next : root.next) {
            if (next == null) {
                continue;
            }
            deque.addLast(next);
        }

        while (!deque.isEmpty()) {
            Node head = deque.removeFirst();
            Node fail = visit(head.father.fail, head.index);
            if (fail == null) {
                head.fail = root;
            } else {
                head.fail = fail.next[head.index];
            }

            for (Node next : head.next) {
                if (next == null) {
                    continue;
                }
                deque.addLast(next);
            }
        }
    }

    public Node visit(Node trace, int index) {
        while (trace != null && trace.next[index] == null) {
            trace = trace.fail;
        }
        return trace;
    }

    public void build(char c) {
        int index = c - MIN_CHARACTER;
        if (buildLast.next[index] == null) {
            Node node = new Node();
            node.father = buildLast;
            node.index = index;
            buildLast.next[index] = node;
            size++;
        }
        buildLast = buildLast.next[index];
    }

    public void beginMatch() {
        matchLast = root;
    }

    public void match(char c) {
        int index = c - MIN_CHARACTER;
        Node fail = visit(matchLast, index);
        if (fail == null) {
            matchLast = root;
        } else {
            matchLast = fail.next[index];
        }
    }

    public static class Node {
        Node[] next = new Node[RANGE_SIZE];
        Node fail;
        Node father;
        int index;

        @Override
        public String toString() {
            return father == null ? "" : (father.toString() + (char) (MIN_CHARACTER + index));
        }
    }
}
