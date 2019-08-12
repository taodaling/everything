package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class BZOJ1050 {
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
            Gcd gcd = new Gcd();

            int n = io.readInt();
            int m = io.readInt();
            Edge[] edges = new Edge[m];
            for (int i = 0; i < m; i++) {
                edges[i] = new Edge();
                edges[i].u = io.readInt() - 1;
                edges[i].v = io.readInt() - 1;
                edges[i].w = io.readInt();
            }
            Arrays.sort(edges, Edge.sortByW);
            int r = 0;
            ConnectionChecker cc = new ConnectionChecker(n);
            int s = io.readInt() - 1;
            int t = io.readInt() - 1;

            int a = 40000;
            int b = 1;

            for (int i = 0; i < m; i++) {
                cc.elapse(i);
                while (!cc.check(s, t) && r < m) {
                    cc.addEdge(edges[r].u, edges[r].v, r + 1);
                    r++;
                }
                if (cc.check(s, t)) {
                    int x = edges[r - 1].w;
                    int y = edges[i].w;
                    if (a * y > x * b) {
                        a = x;
                        b = y;
                    }
                } else {
                    break;
                }
            }

            if (a == 40000) {
                io.cache.append("IMPOSSIBLE");
            } else {
                int g = gcd.gcd(a, b);
                io.cache.append(a / g);
                if (b / g != 1) {
                    io.cache.append("/").append(b / g);
                }
            }
        }
    }

    public static class Gcd {
        public long gcd(long a, long b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private long gcd0(long a, long b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        public int gcd(int a, int b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private int gcd0(int a, int b) {
            return b == 0 ? a : gcd0(b, a % b);
        }
    }

    public static class Edge {
        int u;
        int v;
        int w;
        public static Comparator<Edge> sortByW = new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.w - o2.w;
            }
        };
    }

    public static class ConnectionChecker {
        private LCTNode[] nodes;
        private int time = -1;

        public ConnectionChecker(int n) {
            nodes = new LCTNode[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new LCTNode();
                nodes[i].id = i;
                nodes[i].dieTime = Integer.MAX_VALUE;
                nodes[i].pushUp();
            }
            for (int i = 1; i < n; i++) {
                LCTNode node = new LCTNode();
                node.dieTime = time;
                node.a = nodes[i - 1];
                node.b = nodes[i];
                node.pushUp();
                LCTNode.join(node.a, node);
                LCTNode.join(node.b, node);
            }
        }

        /**
         * 增加一条有效期截止到dieTime的边
         */
        public void addEdge(int aId, int bId, int dieTime) {
            LCTNode a = nodes[aId];
            LCTNode b = nodes[bId];
            LCTNode.findRoute(a, b);
            LCTNode.splay(a);
            if (a.eldest.dieTime >= dieTime) {
                return;
            }
            LCTNode eldest = a.eldest;
            LCTNode.splay(eldest);
            LCTNode.cut(eldest.a, eldest);
            LCTNode.cut(eldest.b, eldest);

            LCTNode node = new LCTNode();
            node.dieTime = dieTime;
            node.a = a;
            node.b = b;
            node.pushUp();
            LCTNode.join(node.a, node);
            LCTNode.join(node.b, node);
        }

        /**
         * 检查两个顶点之间是否存在一条路径
         */
        public boolean check(int aId, int bId) {
            LCTNode a = nodes[aId];
            LCTNode b = nodes[bId];
            LCTNode.findRoute(a, b);
            LCTNode.splay(b);
            return b.eldest.dieTime > time;
        }


        public void elapse(int t) {
            time = t;
        }

        private static class LCTNode {
            public static final LCTNode NIL = new LCTNode();

            static {
                NIL.left = NIL;
                NIL.right = NIL;
                NIL.father = NIL;
                NIL.treeFather = NIL;
                NIL.dieTime = Integer.MAX_VALUE;
                NIL.eldest = NIL;
            }

            LCTNode left = NIL;
            LCTNode right = NIL;
            LCTNode father = NIL;
            LCTNode treeFather = NIL;
            boolean reverse;
            int id;

            LCTNode a;
            LCTNode b;
            LCTNode eldest;
            int dieTime;

            public static LCTNode elder(LCTNode a, LCTNode b) {
                return a.dieTime < b.dieTime ? a : b;
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
                eldest = elder(this, left.eldest);
                eldest = elder(eldest, right.eldest);
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
