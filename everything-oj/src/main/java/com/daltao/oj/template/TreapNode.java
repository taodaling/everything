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
        if (root == null) {
            return new TreapNode[2];
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
        if (a == null) {
            return b;
        }
        if (b == null) {
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
}
