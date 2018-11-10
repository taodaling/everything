package com.daltao.oj.old.submit.codeforces;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/22.
 */
public class CF915E {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\CF915E.in"));
        }
        input = new BlockReader(System.in);

        solve();
    }

    public static void solve() {
        int n = input.nextInteger();
        int q = input.nextInteger();
        BinTree tree = new BinTree(1, n);

        StringBuilder buffer = new StringBuilder();
        for (int i = 1; i <= q; i++) {

            int l = input.nextInteger();
            int r = input.nextInteger();
            int k = input.nextInteger();
            tree.set(l, r, k);
            int sum = tree.sumOf(1, n);
            buffer.append(sum).append('\n');
        }

        System.out.print(buffer);
    }

    public static class BinTree {
        Node root;
        int left, right;

        public BinTree(int l, int r) {
            root = new Node();
            root.setDirty(2, l, r);
            this.left = l;
            this.right = r;
        }

        public void set(int f, int t, int dirty) {
            set(f, t, dirty, left, right, root);
        }

        public int sumOf(int f, int t) {
            return sumOf(f, t, left, right, root);
        }

        public int sumOf(int f, int t, int l, int r, Node node) {
            if (f <= l && t >= r) {
                return node.sum;
            }

            node.ensureChildren(l, r);
            node.pushDown(l, r);
            int mid = (l + r) >> 1;
            int sum = 0;
            if (mid >= f) {
                sum += sumOf(f, t, l, mid, node.left);
            }
            if (t > mid) {
                sum += sumOf(f, t, mid + 1, r, node.right);
            }
            return sum;
        }

        private void set(int f, int t, int dirty, int l, int r, Node node) {
            if (f <= l && t >= r) {
                node.setDirty(dirty, l, r);
                return;
            }
            int mid = (l + r) >> 1;
            node.ensureChildren(l, r);
            node.pushDown(l, r);
            if (mid >= f) {
                set(f, t, dirty, l, mid, node.left);
            }
            if (mid < t) {
                set(f, t, dirty, mid + 1, r, node.right);
            }
            node.pushUp(l, r);
        }

        public static class Node {
            Node left;
            Node right;
            int sum;
            int dirty;


            public void ensureChildren(int l, int r) {
                if (left == null) {
                    left = new Node();
                    right = new Node();
                }
            }

            public void pushUp(int l, int r) {
                sum = left.sum + right.sum;
            }

            public void pushDown(int l, int r) {
                if(dirty != 0) {
                    int mid = (l + r) >> 1;
                    left.setDirty(dirty, l, mid);
                    right.setDirty(dirty, mid + 1, r);
                    dirty = 0;
                }
            }

            public void setDirty(int dirty, int l, int r) {
                this.dirty = dirty;
                if (dirty == 1) {
                    sum = 0;
                } else {
                    sum = r - l + 1;
                }
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

