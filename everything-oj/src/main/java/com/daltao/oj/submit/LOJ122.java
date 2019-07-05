package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LOJ122 {
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

            LCTNode[] nodes = new LCTNode[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new LCTNode();
                nodes[i].id = i;
                nodes[i].time = Integer.MAX_VALUE;
                nodes[i].pushUp();
            }

            Map<Pair, Integer> disappearTimeMap = new HashMap<>();
            int[][] operations = new int[m][4];
            int[] deletionTime = new int[m];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < 3; j++) {
                    operations[i][j] = io.readInt();
                }
            }

            for (int i = m - 1; i >= 0; i--) {
                if (operations[i][0] == 0) {
                    deletionTime[i] = disappearTimeMap.getOrDefault(Pair.create(operations[i][1], operations[i][2]), Integer.MAX_VALUE);
                } else if (operations[i][0] == 1) {
                    disappearTimeMap.put(Pair.create(operations[i][1], operations[i][2]), i);
                }
            }

            for (int i = 0; i < m; i++) {
                LCTNode a = nodes[operations[i][1]];
                LCTNode b = nodes[operations[i][2]];
                LCTNode.findRoute(a, b);
                if (operations[i][0] == 0) {
                    LCTNode edge = new LCTNode();
                    edge.time = deletionTime[i];
                    edge.id = 10000 * a.id + b.id;
                    edge.pushUp();
                    edge.a = a;
                    edge.b = b;
                    LCTNode.splay(a);
                    if (LCTNode.findRoot(a) != b) {
                        LCTNode.join(a, edge);
                        LCTNode.join(edge, b);
                    } else {
                        LCTNode.splay(a);
                        LCTNode replacement = a.minTimeNode;
                        if (replacement.time < edge.time) {
                            LCTNode.cut(replacement, replacement.a);
                            LCTNode.cut(replacement, replacement.b);
                            LCTNode.join(a, edge);
                            LCTNode.join(edge, b);
                        }
                    }
                } else if (operations[i][0] == 1) {

                } else {
                    int time = a.minTimeNode.time;
                    LCTNode.splay(a);
                    if (LCTNode.findRoot(a) != b || time < i) {
                        io.cache.append("N\n");
                    } else {
                        io.cache.append("Y\n");
                    }
                }
            }
        }
    }

    public static class Pair {
        final Integer a;
        final Integer b;

        public Pair(Integer a, Integer b) {
            this.a = a;
            this.b = b;
        }

        public static Pair create(Integer a, Integer b) {
            if (a > b) {
                Integer t = a;
                a = b;
                b = t;
            }
            return new Pair(a, b);
        }

        @Override
        public int hashCode() {
            return a * 31 + b;
        }


        @Override
        public boolean equals(Object obj) {
            Pair p = (Pair) obj;
            return a.equals(p.a) && b.equals(p.b);
        }
    }

    /**
     * Created by dalt on 2018/5/20.
     */
    public static class LCTNode {
        public static final LCTNode NIL = new LCTNode();

        static {
            NIL.left = NIL;
            NIL.right = NIL;
            NIL.father = NIL;
            NIL.treeFather = NIL;
            NIL.time = Integer.MAX_VALUE;
            NIL.minTimeNode = NIL;
        }

        LCTNode left = NIL;
        LCTNode right = NIL;
        LCTNode father = NIL;
        LCTNode treeFather = NIL;
        boolean reverse;
        int id;
        int time;
        LCTNode minTimeNode;

        LCTNode a;
        LCTNode b;

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
            minTimeNode = this;
            if (minTimeNode.time > left.minTimeNode.time) {
                minTimeNode = left.minTimeNode;
            }
            if (minTimeNode.time > right.minTimeNode.time) {
                minTimeNode = right.minTimeNode;
            }
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder();
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
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
