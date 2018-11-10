package com.daltao.oj.template;

/**
 * Created by dalt on 2018/5/20.
 */
public class SplayNode {
    public static final SplayNode NIL = new SplayNode();

    static {
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.father = NIL;
    }

    SplayNode left = NIL;
    SplayNode right = NIL;
    SplayNode father = NIL;

    public static void merge(SplayNode a, SplayNode b) {

    }

    public static void splay(SplayNode x) {
        if (x == NIL) {
            return;
        }
        SplayNode y, z;
        while ((y = x.father) != NIL) {
            if ((z = y.father) == NIL) {
                y.pushDown();
                x.pushDown();
                if (x == y.left) {
                    zig(x);
                } else {
                    zag(x);
                }
            } else {
                z.pushDown();
                y.pushDown();
                x.pushDown();
                if (x == y.left) {
                    if (y == z.left) {
                        zig(y);
                        zig(x);
                    } else {
                        zig(x);
                        zag(x);
                    }
                } else {
                    if (y == z.left) {
                        zag(x);
                        zig(x);
                    } else {
                        zag(y);
                        zag(x);
                    }
                }
            }
        }

        x.pushDown();
        x.pushUp();
    }

    public static void zig(SplayNode x) {
        SplayNode y = x.father;
        SplayNode z = y.father;
        SplayNode b = x.right;

        y.setLeft(b);
        x.setRight(y);
        z.changeChild(y, x);

        y.pushUp();
    }

    public static void zag(SplayNode x) {
        SplayNode y = x.father;
        SplayNode z = y.father;
        SplayNode b = x.left;

        y.setRight(b);
        x.setLeft(y);
        z.changeChild(y, x);

        y.pushUp();
    }

    public void setLeft(SplayNode x) {
        left = x;
        x.father = this;
    }

    public void setRight(SplayNode x) {
        right = x;
        x.father = this;
    }

    public void changeChild(SplayNode y, SplayNode x) {
        if (left == y) {
            setLeft(x);
        } else {
            setRight(x);
        }
    }

    public void pushUp() {
    }

    public void pushDown() {
    }
}
