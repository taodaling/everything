package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dalt on 2018/1/12.
 */
public class POJ2831 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2831.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            POJ2831 solution = new POJ2831();
            //   solution.before();
            System.out.print(solution.solve());
        }
    }

    public String solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        int q = input.nextInteger();
        SNode[] nodes = new SNode[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new SNode();
            nodes[i].id = "" + i;
        }

        int[][] edges = new int[m + 1][3];

        for (int i = 1; i <= m; i++) {
            for (int j = 0; j < 3; j++) {
                edges[i][j] = input.nextInteger();
            }
        }


        int[][] edgesClone = edges.clone();
        Arrays.sort(edges, 1, edges.length, new Comparator<int[]>() {
            public int compare(int[] o1, int[] o2) {
                return o1[2] - o2[2];
            }
        });

        for (int i = 1; i <= m; i++) {
            int[] edge = edges[i];
            SNode a = nodes[edge[0]];
            SNode b = nodes[edge[1]];
            if (a.getRepr() != b.getRepr()) {
                SNode.union(a, b);
                SNode middle = new SNode();
                middle.val = edge[2];
                middle.id = a.id + "-" + b.id;
                middle.update();
                middle.join(b);
                a.join(middle);
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < q; i++) {
            int[] edge = edgesClone[input.nextInteger()];
            SNode a = nodes[edge[0]];
            SNode b = nodes[edge[1]];
            int newCost = input.nextInteger();
            int maxEdge = SNode.lca(a, b);
            result.append(newCost <= maxEdge ? "Yes\n" : "No\n");
        }
        return result.toString();
    }

    public static class SNode {
        private static final SNode NIL = new SNode();
        SNode left = NIL;
        SNode right = NIL;
        SNode father = NIL;
        SNode treeFather = NIL;
        int maxVal;
        int val;
        String id;
        boolean revFlag;
        SNode p = this;
        int rank = 0;

        public static void access(SNode x) {
            SNode y = NIL;
            while (x != NIL) {
                splay(x);
                x.right.father = NIL;
                x.right.treeFather = x;
                x.asRight(y);
                x.update();

                y = x;
                x = x.treeFather;
            }
        }

        public static void makeRoot(SNode x) {
            access(x);
            splay(x);
            x.reverse();
        }

        public static void zig(SNode x) {
            SNode y = x.father;
            SNode z = y.father;
            SNode b = x.right;

            x.asRight(y);
            y.asLeft(b);
            z.replaceChild(y, x);

            y.update();
        }

        public static void zag(SNode x) {
            SNode y = x.father;
            SNode z = y.father;
            SNode b = x.left;

            x.asLeft(y);
            y.asRight(b);
            z.replaceChild(y, x);

            y.update();
        }

        public static void splay(SNode x) {
            if (x == NIL) {
                return;
            }
            SNode y, z;
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
                            zag(y);
                            zag(x);
                        }
                    }
                }
            }
            x.consume();
            x.update();
        }

        public static int lca(SNode x, SNode y) {
            makeRoot(x);
            access(y);
            splay(x);
            return x.maxVal;
        }

        public static void union(SNode a, SNode b) {
            a = a.getRepr();
            b = b.getRepr();
            if (a == b) {
                return;
            }
            if (a.rank == b.rank) {
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
            } else {
                a.p = b;
            }
        }

        @Override
        public String toString() {
            return id;
        }

        public SNode getRepr() {
            return p == p.p ? p : (p = p.getRepr());
        }

        public void join(SNode x) {
            makeRoot(this);
            treeFather = x;
        }

        public void reverse() {
            revFlag = !revFlag;
        }

        public void consume() {
            if (revFlag) {
                revFlag = false;

                SNode tmp = left;
                left = right;
                right = tmp;

                left.reverse();
                right.reverse();
            }

            left.treeFather = treeFather;
            right.treeFather = treeFather;
        }

        public void asRight(SNode right) {
            this.right = right;
            right.father = this;
        }

        public void asLeft(SNode left) {
            this.left = left;
            left.father = this;
        }

        public void replaceChild(SNode x, SNode y) {
            if (left == x) {
                asLeft(y);
            } else {
                asRight(y);
            }
        }

        public void update() {
            maxVal = Math.max(left.maxVal, right.maxVal);
            maxVal = Math.max(maxVal, val);
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
