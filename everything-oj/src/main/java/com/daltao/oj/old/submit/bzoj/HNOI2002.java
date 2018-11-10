package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/30.
 */
public class HNOI2002 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\HNOI2002.in"));

        input = new BlockReader(System.in);

        HNOI2002 solution = new HNOI2002();
        System.out.println(solution.solve());
    }

    public int solve() {
        int n = input.nextInteger();
        SplayTree tree = new SplayTree();
        int firstDay = input.nextInteger();
        int sum = firstDay;

        tree.findAndInsert(firstDay);

        for (int i = 1; i < n; i++) {
            int v = input.nextInteger();
            sum += tree.findAndInsert(v);
        }

        return sum;
    }

    public static class SplayTree {

        Node root = Node.getNull();

        void splay(Node x) {
            Node y, z;
            while (!(y = x.father).isNull()) {
                if ((z = y.father).isNull()) {
                    //zig or zag
                    if (y.left == x) {
                        zig(x);
                    } else {
                        zag(x);
                    }
                } else {
                    //zigzig or zagzag or zigzag or zagzig
                    if (y.left == x) {
                        if (z.left == y) {
                            zigzig(x);
                        } else {
                            zigzag(x);
                        }
                    } else {
                        if (z.left == y) {
                            zagzig(x);
                        } else {
                            zagzag(x);
                        }
                    }
                }
            }
            root = x;
        }

        int findAndInsert(int value) {
            Node trace = root;
            int minAbs = Integer.MAX_VALUE;
            while (!trace.isNull()) {
                int abs = Math.abs(value - trace.val);
                minAbs = Math.min(abs, minAbs);
                if (trace.val < value && !trace.right.isNull()) {
                    trace = trace.right;
                } else if (trace.val > value && !trace.left.isNull()) {
                    trace = trace.left;
                } else {
                    break;
                }
            }

            //Insert if necessary
            if (trace.val < value) {
                Node node = new Node(value);
                trace.asRight(node);
                trace = node;
            } else if (trace.val > value) {
                Node node = new Node(value);
                trace.asLeft(node);
                trace = node;
            }

            //splay optimal
            splay(trace);
            return minAbs;
        }

        private void zig(Node x) {
            Node y = x.father;
            Node b = x.right;
            Node p = y.father;

            x.asRight(y);
            y.asLeft(b);

            p.replace(y, x);
        }

        private void zag(Node y) {
            Node x = y.father;
            Node b = y.left;
            Node p = x.father;

            x.asRight(b);
            y.asLeft(x);

            p.replace(x, y);
        }

        public void zigzig(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node b = x.right;
            Node c = y.right;
            Node p = z.father;

            x.asRight(y);
            y.asLeft(b);
            y.asRight(z);
            z.asLeft(c);

            p.replace(z, x);
        }

        public void zagzag(Node z) {
            Node y = z.father;
            Node x = y.father;
            Node c = z.left;
            Node b = y.left;
            Node p = x.father;

            z.asLeft(y);
            y.asLeft(x);
            y.asRight(c);
            x.asRight(b);

            p.replace(x, z);
        }

        public void zigzag(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node a = x.left;
            Node b = x.right;
            Node p = z.father;

            x.asRight(y);
            y.asLeft(b);
            x.asLeft(z);
            z.asRight(a);

            p.replace(z, x);
        }

        public void zagzig(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node a = x.left;
            Node b = x.right;
            Node p = z.father;

            x.asLeft(y);
            y.asRight(a);
            x.asRight(z);
            z.asLeft(b);

            p.replace(z, x);
        }

        public static class Node {
            static final Node DUMMY = new Node(Integer.MIN_VALUE);

            static {
                DUMMY.father = DUMMY;
            }

            Node father = DUMMY;
            Node left = DUMMY;
            Node right = DUMMY;
            int val;
            int key;

            public Node(int val) {
                this.val = val;
            }

            public static Node getNull() {
                return DUMMY;
            }

            public boolean isNull() {
                return this == DUMMY;
            }

            public void asRight(Node node) {
                right = node;
                node.father = this;
            }

            public void asLeft(Node node) {
                left = node;
                node.father = this;
            }

            public void replace(Node original, Node fashion) {
                if (original == left) {
                    asLeft(fashion);
                } else {
                    asRight(fashion);
                }
            }

            @Override
            public String toString() {
                return isNull() ? "null" : "" + val;
            }
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }
}
