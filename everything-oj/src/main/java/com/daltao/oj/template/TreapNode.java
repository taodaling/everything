package com.daltao.oj.template;

import java.util.Random;


public class TreapNode implements Cloneable {
    private static Random random = new Random();

    private static TreapNode NIL = new TreapNode();

    static {
        NIL.left = NIL.right = NIL;
    }

    TreapNode left = NIL;
    TreapNode right = NIL;
    int key;

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
    }

    private static TreapNode[] split(TreapNode root, int key) {
        if (root == NIL) {
            return new TreapNode[]{NIL, NIL};
        }
        root.pushDown();
        TreapNode[] trees;
        if (root.key > key) {
            trees = split(root.left, key);
            root.left = trees[1];
            trees[1] = root;
        } else {
            trees = split(root.right, key);
            root.right = trees[0];
            trees[0] = root;
        }
        root.pushUp();
        return trees;
    }

    private static TreapNode merge(TreapNode a, TreapNode b) {
        if (a == NIL) {
            return b;
        }
        if (b == NIL) {
            return a;
        }
        if (random.nextBoolean()) {
            TreapNode tmp = a;
            a = b;
            b = tmp;
        }
        a.pushDown();
        if (a.key >= b.key) {
            a.left = merge(a.left, b);
        } else {
            a.right = merge(a.right, b);
        }
        a.pushUp();
        return a;
    }

    public static int toArray(TreapNode root, int[] data, int offset) {
        if (root == NIL) {
            return offset;
        }
        offset = toArray(root.left, data, offset);
        data[offset++] = root.key;
        offset = toArray(root.right, data, offset);
        return offset;
    }

    public static void toString(TreapNode root, StringBuilder builder) {
        if (root == NIL) {
            return;
        }
        toString(root.left, builder);
        builder.append(root.key).append(',');
        toString(root.right, builder);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append(key).append(":");
        toString(this, builder);
        return builder.toString();
    }
}
