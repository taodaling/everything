package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2018/4/7.
 */
public class LC327 {
    public static void main(String[] args) {
        System.out.println(new LC327().countRangeSum(new int[]{-2, 5, -1}, -2, 2));
    }

    public int countRangeSum(int[] nums, int lower, int upper) {
        int n = nums.length;
        long s = 0;
        int cnt = 0;
        int[] val = new int[1];
        Node root = Node.add(Node.NIL, 0);
        for (int i = 0; i < n; i++) {
            s += nums[i];
            //lower <= s - a <= upper
            //that's meaning s-upper<=a<=s-lower
            root = Node.count(root, s - upper, s - lower, val);
            cnt += val[0];
            root = Node.add(root, s);
        }
        return cnt;
    }

    public static class Node {
        static final Node NIL = new Node();

        static {
            NIL.left = NIL.right = NIL.father = NIL;
        }

        Node left = NIL;
        Node right = NIL;
        Node father = NIL;
        int count = 0;
        long key;

        public static Node add(Node root, long val) {
            Node newNode = new Node();
            newNode.key = val;

            if (root == NIL) {
                newNode.pushUp();
                return newNode;
            }

            Node trace = root;
            Node father = NIL;
            while (trace != NIL) {
                father = trace;
                trace.pushDown();
                if (trace.key < val) {
                    trace = trace.right;
                } else {
                    trace = trace.left;
                }
            }

            if (father.key < val) {
                father.asRight(newNode);
            } else {
                father.asLeft(newNode);
            }

            splay(newNode);
            return newNode;
        }

        public static Node count(Node root, long left, long right, int[] val) {
            root.pushDown();

            Node add1 = add(root, left);
            Node add2 = add1.right;
            add2.father = NIL;
            add2 = add(add2, right + 1);

            val[0] = add2.left.count;

            add1.asRight(removeRoot(add2));
            return removeRoot(add1);
        }

        public static Node removeRoot(Node root) {
            root.pushDown();
            Node left = root.left;
            Node right = root.right;
            left.father = NIL;
            right.father = NIL;
            if (left == NIL) {
                return right;
            }
            if (right == NIL) {
                return left;
            }
            left.pushDown();
            while (left.right != NIL) {
                left = left.right;
                left.pushDown();
            }

            splay(left);
            left.asRight(right);
            left.pushUp();
            return left;
        }

        public static void splay(Node x) {
            if (x == NIL) {
                return;
            }
            Node y, z;
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

        public static void zig(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node b = x.right;

            z.replaceChild(y, x);
            x.asRight(y);
            y.asLeft(b);

            y.pushUp();
        }

        public static void zag(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node b = x.left;

            z.replaceChild(y, x);
            x.asLeft(y);
            y.asRight(b);

            y.pushUp();
        }

        public void pushDown() {
        }

        public void asRight(Node x) {
            this.right = x;
            x.father = this;
        }

        public void asLeft(Node x) {
            this.left = x;
            x.father = this;
        }

        public void replaceChild(Node ori, Node income) {
            if (left == ori) {
                asLeft(income);
            } else {
                asRight(income);
            }
        }

        public void pushUp() {
            this.count = left.count + right.count + 1;
        }
    }
}
