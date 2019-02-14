package com.daltao.template;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;


public class TreapNode implements Cloneable {
    private static Random random = new Random();

    private static TreapNode NIL = new TreapNode();

    static {
        NIL.left = NIL.right = NIL;
    }

    TreapNode left = NIL;
    TreapNode right = NIL;
    int size;
    int key;

    public static TreapNode buildFromSortedData(int[] data, int l, int r) {
        Deque<TreapNode> deque = new ArrayDeque(r - l);

        for (int i = l; i < r; i++) {
            TreapNode node = new TreapNode();
            node.key = data[i];
            while (!deque.isEmpty()) {
                if (random.nextBoolean()) {
                    TreapNode tail = deque.removeLast();
                    tail.right = node.left;
                    node.left = tail;
                    tail.pushUp();
                } else {
                    break;
                }
            }

            deque.addLast(node);
        }

        TreapNode last = NIL;
        while (!deque.isEmpty()) {
            TreapNode tail = deque.removeLast();
            tail.right = last;
            tail.pushUp();
            last = tail;
        }

        return last;
    }

    @Override
    public TreapNode clone() {
        try {
            return (TreapNode) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void pushDown() {
    }

    public void pushUp() {
        size = left.size + right.size + 1;
    }

    public static TreapNode[] splitByRank(TreapNode root, int rank) {
        if (root == NIL) {
            return new TreapNode[]{NIL, NIL};
        }
        root.pushDown();
        TreapNode[] result;
        if (root.left.size >= rank) {
            result = splitByRank(root.left, rank);
            root.left = result[1];
            result[1] = root;
        } else {
            result = splitByRank(root.right, rank - (root.size - root.right.size));
            root.right = result[0];
            result[0] = root;
        }
        root.pushUp();
        return result;
    }

    public static TreapNode merge(TreapNode a, TreapNode b) {
        if (a == NIL) {
            return b;
        }
        if (b == NIL) {
            return a;
        }
        if (random.nextBoolean()) {
            a.pushDown();
            a.right = merge(a.right, b);
            a.pushUp();
            return a;
        } else {
            b.pushDown();
            b.left = merge(a, b.left);
            b.pushUp();
            return b;
        }
    }

    public static void toString(TreapNode root, StringBuilder builder) {
        if (root == NIL) {
            return;
        }
        root.pushDown();
        toString(root.left, builder);
        builder.append(root.key).append(',');
        toString(root.right, builder);
    }

    public static TreapNode clone(TreapNode root) {
        if (root == NIL) {
            return NIL;
        }
        TreapNode clone = root.clone();
        clone.left = clone(root.left);
        clone.right = clone(root.right);
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(key).append(":");
        toString(clone(this), builder);
        return builder.toString();
    }

    public static TreapNode[] splitByKey(TreapNode root, int key) {
        if (root == NIL) {
            return new TreapNode[]{NIL, NIL};
        }
        root.pushDown();
        TreapNode[] result;
        if (root.key > key) {
            result = splitByKey(root.left, key);
            root.left = result[1];
            result[1] = root;
        } else {
            result = splitByKey(root.right, key);
            root.right = result[0];
            result[0] = root;
        }
        root.pushUp();
        return result;
    }
}
