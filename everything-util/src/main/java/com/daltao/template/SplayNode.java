package com.daltao.template;

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
    int key;

    /**
     * Make the largest key node as the root of this tree and return new root.
     * If no node less than or equal to key, then the smallest key node will be root.
     */
    public static SplayNode asRoot(SplayNode node, int key) {
        if (node == NIL) {
            return NIL;
        }

        SplayNode parent = NIL;
        SplayNode trace = node;
        while (trace != NIL && trace.key != key) {
            parent = trace;
            if (trace.key > key) {
                trace = trace.left;
            } else {
                trace = trace.right;
            }
        }

        if (trace != NIL) {
            splay(trace);
            return trace;
        }
        splay(parent);
        return parent;
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

    public static int toArray(SplayNode root, int[] data, int offset) {
        if (root == NIL) {
            return offset;
        }
        offset = toArray(root.left, data, offset);
        data[offset++] = root.key;
        offset = toArray(root.right, data, offset);
        return offset;
    }

    public static void toString(SplayNode root, StringBuilder builder) {
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
