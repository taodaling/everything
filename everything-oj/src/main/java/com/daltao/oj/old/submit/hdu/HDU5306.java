package com.daltao.oj.old.submit.hdu;

import java.io.*;

/**
 * Created by dalt on 2018/3/25.
 */
public class HDU5306 {
    public static BlockReader input;
    public static PrintStream out;


    public static void main(String[] args) {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            try {
                input = new BlockReader(new FileInputStream("D:\\DataBase\\TESTCASE\\hdu\\HDU5306.in"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            out = System.out;
        } else {
            input = new BlockReader(System.in);
            out = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        int testcase = input.nextInteger();
        for (int i = 0; i < testcase; i++) {
            solve();
        }
        out.flush();
    }

    public static void solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();

        int[] data = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            data[i] = input.nextInteger();
        }
        Node root = Node.build(1, n, data);
        for (int i = 0; i < m; i++) {
            /**
             * 0 x y t: For every x≤i≤y, we use min(ai,t) to replace the original ai's value.
             * 1 x y: Print the maximum value of ai that x≤i≤y.
             * 2 x y: Print the sum of ai that x≤i≤y.
             */
            int op = input.nextInteger();
            int l = input.nextInteger();
            int r = input.nextInteger();
            if (op == 0) {
                int min = input.nextInteger();
                Node.setMin(l, r, 1, n, min, root);
            } else if (op == 1) {
                out.println(Node.queryMax(l, r, 1, n, root));
            } else {
                out.println(Node.querySum(l, r, 1, n, root));
            }
        }
    }

    public static class Node {
        Node l;
        Node r;
        int max;
        int maxCnt;
        int second;
        long sum;

        public static Node build(int l, int r, int[] vals) {
            Node node = new Node();
            if (l == r) {
                node.sum = node.max = vals[l];
                node.maxCnt = 1;
                node.second = Integer.MIN_VALUE;
            } else {
                int m = (l + r) >> 1;
                node.l = build(l, m, vals);
                node.r = build(m + 1, r, vals);
                node.pushUp();
            }
            return node;
        }

        public static void setMin(int f, int t, int l, int r, int min, Node node) {
            if (f > r || t < l || node.max <= min) {
                return;
            }
            if (f <= l && t >= r && node.second < min) {
                node.addMinTag(min);
                return;
            }
            node.pushDown();
            int m = (l + r) >> 1;
            setMin(f, t, l, m, min, node.l);
            setMin(f, t, m + 1, r, min, node.r);
            node.pushUp();
        }

        public static int queryMax(int f, int t, int l, int r, Node node) {
            if (f > r || t < l) {
                return Integer.MIN_VALUE;
            }
            if (f <= l && r <= t) {
                return node.max;
            }
            node.pushDown();
            int m = (l + r) >> 1;
            return Math.max(queryMax(f, t, l, m, node.l), queryMax(f, t, m + 1, r, node.r));
        }

        public static long querySum(int f, int t, int l, int r, Node node) {
            if (f > r || t < l) {
                return 0;
            }
            if (f <= l && r <= t) {
                return node.sum;
            }
            node.pushDown();
            int m = (l + r) >> 1;
            return querySum(f, t, l, m, node.l) + querySum(f, t, m + 1, r, node.r);
        }

        public void addMinTag(int min) {
            if (min >= max) {
                return;
            }
            sum -= (long) (max - min) * maxCnt;
            max = min;
        }

        public void pushDown() {
            l.addMinTag(max);
            r.addMinTag(max);
        }

        public void pushUp() {
            max = Math.max(l.max, r.max);
            if (l.max == r.max) {
                second = Math.max(l.second, r.second);
            } else {
                second = Math.max(Math.max(l.second, r.second), Math.min(l.max, r.max));
            }
            maxCnt = (l.max == max ? l.maxCnt : 0) + (r.max == max ? r.maxCnt : 0);
            sum = l.sum + r.sum;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 4096);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
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

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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
    }
}