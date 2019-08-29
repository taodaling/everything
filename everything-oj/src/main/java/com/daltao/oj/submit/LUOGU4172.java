package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class LUOGU4172 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io, new Debug(local));

        if (async) {
            Thread t = new Thread(null, task, "dalt", 1 << 27);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
        } else {
            task.run();
        }

        if (local) {
            io.cache.append("\n\n--memory -- \n" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;
        final Debug debug;
        int inf = (int) 1e8;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();
            int q = io.readInt();

            int[][] edges = new int[n][n];
            boolean[][] hasEdge = new boolean[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    edges[i][j] = -1;
                }
            }

            for (int i = 0; i < m; i++) {
                int a = io.readInt() - 1;
                int b = io.readInt() - 1;
                if (a > b) {
                    int tmp = a;
                    a = b;
                    b = tmp;
                }
                edges[a][b] = io.readInt();
                hasEdge[a][b] = true;
            }

            int[][] queries = new int[q][4];
            for (int i = 0; i < q; i++) {
                for (int j = 0; j < 3; j++) {
                    queries[i][j] = io.readInt();
                }
                queries[i][1]--;
                queries[i][2]--;
                if (queries[i][1] > queries[i][2]) {
                    int tmp = queries[i][1];
                    queries[i][1] = queries[i][2];
                    queries[i][2] = tmp;
                }
                if (queries[i][0] == 2) {
                    hasEdge[queries[i][1]][queries[i][2]] = false;
                }
            }

            DynamicMST mst = new DynamicMST(n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (hasEdge[i][j]) {
                        mst.addEdge(i, j, edges[i][j]);
                    }
                }
            }

            for (int i = q - 1; i >= 0; i--) {
                if (queries[i][0] == 1) {
                    queries[i][3] = mst.maxWeightBetween(queries[i][1], queries[i][2]);
                } else {
                    mst.addEdge(queries[i][1], queries[i][2], edges[queries[i][1]][queries[i][2]]);
                }
            }

            for (int i = 0; i < q; i++) {
                if (queries[i][0] == 1) {
                    io.cache.append(queries[i][3]).append('\n');
                }
            }
        }
    }

    public static class DynamicMST {
        private LCTNode[] nodes;
        private long totalWeight = 0;
        private int edgeNum;

        public DynamicMST(int n) {
            nodes = new LCTNode[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new LCTNode();
                nodes[i].id = i;
                nodes[i].weight = 0;
                nodes[i].pushUp();
            }
            for (int i = 1; i < n; i++) {
                LCTNode node = new LCTNode();
                node.weight = Integer.MAX_VALUE;
                node.a = nodes[i - 1];
                node.b = nodes[i];
                node.pushUp();
                LCTNode.join(node.a, node);
                LCTNode.join(node.b, node);
            }
        }

        public long getTotalWeight() {
            return totalWeight;
        }

        public int getEdgeNum() {
            return edgeNum;
        }

        public void addEdge(int aId, int bId, int weight) {
            LCTNode a = nodes[aId];
            LCTNode b = nodes[bId];
            LCTNode.findRoute(a, b);
            LCTNode.splay(a);
            if (a.largest.weight <= weight) {
                return;
            }
            LCTNode largest = a.largest;
            LCTNode.splay(largest);
            LCTNode.cut(largest.a, largest);
            LCTNode.cut(largest.b, largest);
            if (largest.weight < Integer.MAX_VALUE) {
                edgeNum--;
                totalWeight -= largest.weight;
            }

            LCTNode node = new LCTNode();
            node.weight = weight;
            node.a = a;
            node.b = b;
            node.pushUp();
            LCTNode.join(node.a, node);
            LCTNode.join(node.b, node);
            edgeNum++;
            totalWeight += node.weight;
        }

        /**
         * 检查两个顶点之间是否存在一条路径
         */
        public boolean check(int aId, int bId) {
            return maxWeightBetween(aId, bId) < Integer.MAX_VALUE;
        }

        public int maxWeightBetween(int aId, int bId) {
            LCTNode a = nodes[aId];
            LCTNode b = nodes[bId];
            LCTNode.findRoute(a, b);
            LCTNode.splay(b);
            return b.largest.weight;
        }

        private static class LCTNode {
            public static final LCTNode NIL = new LCTNode();

            static {
                NIL.left = NIL;
                NIL.right = NIL;
                NIL.father = NIL;
                NIL.treeFather = NIL;
                NIL.weight = 0;
                NIL.largest = NIL;
            }

            LCTNode left = NIL;
            LCTNode right = NIL;
            LCTNode father = NIL;
            LCTNode treeFather = NIL;
            boolean reverse;
            int id;

            LCTNode a;
            LCTNode b;
            LCTNode largest;
            int weight;

            public static LCTNode larger(LCTNode a, LCTNode b) {
                return a.weight >= b.weight ? a : b;
            }

            public static void access(LCTNode x) {
                LCTNode last = NIL;
                while (x != NIL) {
                    splay(x);
                    x.right.father = NIL;
                    x.right.treeFather = x;
                    x.setRight(last);
                    x.pushUp();

                    last = x;
                    x = x.treeFather;
                }
            }

            public static void makeRoot(LCTNode x) {
                access(x);
                splay(x);
                x.reverse();
            }

            public static void cut(LCTNode y, LCTNode x) {
                makeRoot(y);
                access(x);
                splay(y);
                y.right.treeFather = NIL;
                y.right.father = NIL;
                y.setRight(NIL);
                y.pushUp();
            }

            public static void join(LCTNode y, LCTNode x) {
                makeRoot(x);
                x.treeFather = y;
            }

            public static void findRoute(LCTNode x, LCTNode y) {
                makeRoot(y);
                access(x);
            }

            public static void splay(LCTNode x) {
                if (x == NIL) {
                    return;
                }
                LCTNode y, z;
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

            public static void zig(LCTNode x) {
                LCTNode y = x.father;
                LCTNode z = y.father;
                LCTNode b = x.right;

                y.setLeft(b);
                x.setRight(y);
                z.changeChild(y, x);

                y.pushUp();
            }

            public static void zag(LCTNode x) {
                LCTNode y = x.father;
                LCTNode z = y.father;
                LCTNode b = x.left;

                y.setRight(b);
                x.setLeft(y);
                z.changeChild(y, x);

                y.pushUp();
            }

            public static LCTNode findRoot(LCTNode x) {
                x.pushDown();
                while (x.left != NIL) {
                    x = x.left;
                    x.pushDown();
                }
                splay(x);
                return x;
            }

            @Override
            public String toString() {
                return "" + id;
            }

            public void pushDown() {
                if (reverse) {
                    reverse = false;

                    LCTNode tmpNode = left;
                    left = right;
                    right = tmpNode;

                    left.reverse();
                    right.reverse();
                }

                left.treeFather = treeFather;
                right.treeFather = treeFather;
            }

            public void reverse() {
                reverse = !reverse;
            }

            public void setLeft(LCTNode x) {
                left = x;
                x.father = this;
            }

            public void setRight(LCTNode x) {
                right = x;
                x.father = this;
            }

            public void changeChild(LCTNode y, LCTNode x) {
                if (left == y) {
                    setLeft(x);
                } else {
                    setRight(x);
                }
            }

            public void pushUp() {
                largest = larger(this, left.largest);
                largest = larger(largest, right.largest);
            }
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(1 << 13);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
        private byte[] buf = new byte[1 << 13];
        private int bufLen;
        private int bufOffset;
        private int next;

        public FastIO(InputStream is, OutputStream os, Charset charset) {
            this.is = is;
            this.os = os;
            this.charset = charset;
        }

        public FastIO(InputStream is, OutputStream os) {
            this(is, os, Charset.forName("ascii"));
        }

        private int read() {
            while (bufLen == bufOffset) {
                bufOffset = 0;
                try {
                    bufLen = is.read(buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (bufLen == -1) {
                    return -1;
                }
            }
            return buf[bufOffset++];
        }

        public void skipBlank() {
            while (next >= 0 && next <= 32) {
                next = read();
            }
        }

        public int readInt() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            int val = 0;
            if (sign == 1) {
                while (next >= '0' && next <= '9') {
                    val = val * 10 + next - '0';
                    next = read();
                }
            } else {
                while (next >= '0' && next <= '9') {
                    val = val * 10 - next + '0';
                    next = read();
                }
            }

            return val;
        }

        public long readLong() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            long val = 0;
            if (sign == 1) {
                while (next >= '0' && next <= '9') {
                    val = val * 10 + next - '0';
                    next = read();
                }
            } else {
                while (next >= '0' && next <= '9') {
                    val = val * 10 - next + '0';
                    next = read();
                }
            }

            return val;
        }

        public double readDouble() {
            boolean sign = true;
            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+';
                next = read();
            }

            long val = 0;
            while (next >= '0' && next <= '9') {
                val = val * 10 + next - '0';
                next = read();
            }
            if (next != '.') {
                return sign ? val : -val;
            }
            next = read();
            long radix = 1;
            long point = 0;
            while (next >= '0' && next <= '9') {
                point = point * 10 + next - '0';
                radix = radix * 10;
                next = read();
            }
            double result = val + (double) point / radix;
            return sign ? result : -result;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (next > 32) {
                builder.append((char) next);
                next = read();
            }

            return builder.toString();
        }

        public String readString() {
            defaultStringBuf.setLength(0);
            return readString(defaultStringBuf);
        }

        public int readLine(char[] data, int offset) {
            int originalOffset = offset;
            while (next != -1 && next != '\n') {
                data[offset++] = (char) next;
                next = read();
            }
            return offset - originalOffset;
        }

        public int readString(char[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (char) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public int readString(byte[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (byte) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public char readChar() {
            skipBlank();
            char c = (char) next;
            next = read();
            return c;
        }

        public void flush() throws IOException {
            os.write(cache.toString().getBytes(charset));
            os.flush();
            cache.setLength(0);
        }

        public boolean hasMore() {
            skipBlank();
            return next != -1;
        }
    }

    public static class Debug {
        private boolean allowDebug;

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
        }

        public void assertTrue(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (!flag) {
                fail();
            }
        }

        public void fail() {
            throw new RuntimeException();
        }

        public void assertFalse(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (flag) {
                fail();
            }
        }

        private void outputName(String name) {
            System.out.print(name + " = ");
        }

        public void debug(String name, int x) {
            if (!allowDebug) {
                return;
            }

            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, long x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, double x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, int[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, long[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, double[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, Object x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, Object... x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.deepToString(x));
        }
    }
}
