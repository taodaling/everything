package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/30.
 */
public class BZOJ3223 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3223.in"));
        input = new BlockReader(System.in);
        BZOJ3223 solution = new BZOJ3223();
        System.out.print(solution.solve());
    }

    public String solve() {
        StringBuilder builder = new StringBuilder();
        int intervalSize = input.nextInteger();
        SplayTree tree = new SplayTree(intervalSize);
        int revTimes = input.nextInteger();
        for (int i = 0; i < revTimes; i++) {
            int from = input.nextInteger();
            int to = input.nextInteger();
            tree.reverse(from, to);
        }
        AppendableIntList list = new AppendableIntList(intervalSize + 2);
        tree.dfs(list);
        for (int i = 1; i <= intervalSize; i++) {
            builder.append(list.get(i)).append(' ');
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    public static class AppendableIntList {
        int[] data;
        int tail;

        public AppendableIntList(int cap) {
            data = new int[cap];
        }

        public void clear() {
            tail = 0;
        }

        public int size() {
            return tail;
        }

        public void append(int v) {
            data[tail++] = v;
        }

        public int get(int i) {
            return data[i];
        }

        @Override
        public String toString() {
            return Arrays.toString(data);
        }
    }

    public static class SplayTree {
        static final Node NIL = new Node();
        Node root = NIL;
        SplayTree auxiliary;

        public SplayTree(int size) {
            Node last = NIL;
            size++;
            for (int i = 0; i <= size; i++) {
                Node node = new Node();
                node.val = i;
                node.asLeft(last);
                node.right = NIL;
                node.update();
                last = node;
            }

            last.father = NIL;
            root = last;
            auxiliary = new SplayTree();
        }

        private SplayTree() {
        }

        private static void dfs(Node node, AppendableIntList list) {
            if (node == NIL) {
                return;
            }
            node.consume();
            dfs(node.left, list);
            list.append(node.val);
            dfs(node.right, list);
        }

        public void dfs(AppendableIntList list) {
            dfs(root, list);
        }

        public Node theKth(int k) {
            Node trace = root;
            while (true) {
                trace.consume();
                if (trace.left.size >= k) {
                    trace = trace.left;
                } else {
                    k -= trace.left.size + 1;
                    if (k == 0) {
                        break;
                    } else {
                        trace = trace.right;
                    }
                }
            }

            splay(trace);
            return trace;
        }

        public Node interval(int from, int to) {
            from++;
            to++;
            theKth(from - 1);
            auxiliary.root = root.right;
            auxiliary.root.father = NIL;
            auxiliary.theKth(to - from + 2);
            root.asRight(auxiliary.root);
            return root.right.left;
        }

        public void reverse(int from, int to) {
            Node interval = interval(from, to);
            interval.rev = interval.rev == false;
        }

        public void splay(Node x) {
            if (x == NIL) {
                return;
            }
            Node y, z;
            while ((y = x.father) != NIL) {
                if ((z = y.father) == NIL) {
                    y.consume();
                    x.consume();
                    if (x == y.left) {
                        zig(x);
                    } else {
                        zag(x);
                    }
                } else {
                    z.consume();
                    y.consume();
                    x.consume();
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
                            zag(x);
                            zag(x);
                        }
                    }
                    //zf.replace(z, x);
                }
            }
            x.update();
            root = x;
        }

        public void zig(Node x) {
            Node y = x.father;
            Node b = x.right;
            Node z = y.father;
            x.asRight(y);
            y.asLeft(b);

            y.update();
            z.replace(y, x);
        }

        public void zag(Node x) {
            Node y = x.father;
            Node b = x.left;
            Node z = y.father;

            x.asLeft(y);
            y.asRight(b);

            y.update();
            z.replace(y, x);
        }

        public void dfs(Node node, boolean rev, AppendableIntList list) {
            if (node == NIL) {
                return;
            }
            if (node.rev) {
                rev = !rev;
            }

            if (!rev) {
                dfs(node.left, rev, list);
                list.append(node.val);
                dfs(node.right, rev, list);
            } else {
                dfs(node.right, rev, list);
                list.append(node.val);
                dfs(node.left, rev, list);
            }
        }

        @Override
        public String toString() {
            AppendableIntList list = new AppendableIntList(root.size);
            dfs(root, false, list);
            return Arrays.toString(list.data);
        }

        private static class Node {
            Node left;
            Node right;
            Node father;
            int size;
            boolean rev;
            int val;

            public void consume() {
                if (rev) {
                    rev = false;
                    Node tmp = left;
                    left = right;
                    right = tmp;
                    left.rev = !left.rev;
                    right.rev = !right.rev;
                }
            }

            public void asRight(Node node) {
                right = node;
                node.father = this;
            }

            public void asLeft(Node node) {
                left = node;
                node.father = this;
            }

            public void replace(Node oldNode, Node newNode) {
                if (left == oldNode) {
                    asLeft(newNode);
                } else {
                    asRight(newNode);
                }
            }

            public void update() {
                size = 1 + left.size + right.size;
            }

            @Override
            public String toString() {
                return ":" + val;
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
