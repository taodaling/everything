package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BZOJ3669 {
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
            try {
                solve();
            }catch (IndexOutOfBoundsException e)
            {}
        }

        LCTNode[] nodes;
        int n;
        int m;
        int xyLimit = 50000;

        public void addEdge(Edge edge) {
            LCTNode a = nodes[edge.a];
            LCTNode b = nodes[edge.b];

            LCTNode edgeNode = new LCTNode();
            edgeNode.y = edge.y;
            edgeNode.a = a;
            edgeNode.b = b;
            edgeNode.pushUp();

            LCTNode.findRoute(a, b);
            LCTNode.splay(a);
            if (LCTNode.findRoot(a) != b) {
                LCTNode.join(a, edgeNode);
                LCTNode.join(edgeNode, b);
                return;
            }

            LCTNode.splay(a);
            LCTNode rep = a.maxYNode;
            if (edge.y >= rep.maxYNode.y) {
                return;
            }

            LCTNode.cut(rep, rep.a);
            LCTNode.cut(rep.b, rep);

            LCTNode.join(a, edgeNode);
            LCTNode.join(edgeNode, b);
            return;
        }

        public void removeEdge(Edge e) {

        }

        public boolean isConnected(LCTNode a, LCTNode b, int y) {
            LCTNode.findRoute(a, b);
            LCTNode.splay(a);
            if (LCTNode.findRoot(a) != b) {
                return false;
            }
            LCTNode.splay(a);
            LCTNode rep = a.maxYNode;
            return rep.y <= y;
        }

        public void solve() {
            n = io.readInt();
            m = io.readInt();

            nodes = new LCTNode[n + 1];
            for (int i = 1; i <= n; i++) {
                LCTNode node = new LCTNode();
                node.id = i;
                node.y = -1;
                nodes[i] = node;
            }

            List<Edge>[] edgeIndexByX = new List[xyLimit + 1];
            List<Edge>[] edgeIndexByY = new List[xyLimit + 1];
            for (int i = 0; i <= xyLimit; i++) {
                edgeIndexByX[i] = new ArrayList();
                edgeIndexByY[i] = new ArrayList();
            }

            Edge[] edges = new Edge[m];
            for (int i = 0; i < m; i++) {
                Edge edge = new Edge();
                edge.a = io.readInt();
                edge.b = io.readInt();
                edge.x = io.readInt();
                edge.y = io.readInt();
                edges[i] = edge;
            }

            Arrays.sort(edges, Edge.sortByY);
            for (int i = 0; i < m; i++) {
                Edge edge = edges[i];
                edgeIndexByX[edge.x].add(edge);
            }

            Arrays.sort(edges, Edge.sortByX);
            for (int i = 0; i < m; i++) {
                Edge edge = edges[i];
                edgeIndexByY[edge.y].add(edge);
            }

            int minSum = Integer.MAX_VALUE;
            int x = -1;
            int y = xyLimit;
            boolean status = false;
            while (y >= 0 && x <= xyLimit) {
                if (!status) {
                    x++;
                    if (x > xyLimit) {
                        break;
                    }
                    for (Edge edge : edgeIndexByX[x]) {
                        if (edge.y > y) {
                            break;
                        }
                        addEdge(edge);
                    }
                } else {
                    for (Edge edge : edgeIndexByY[y]) {
                        if (edge.x > x) {
                            break;
                        }
                        removeEdge(edge);
                    }
                    y--;
                    if (y < 0) {
                        break;
                    }
                }
                status = isConnected(nodes[1], nodes[n], y);
                if (status) {
                    minSum = Math.min(x + y, minSum);
                }
            }

            if (minSum == Integer.MAX_VALUE) {
                io.cache.append(-1);
            } else {
                io.cache.append(minSum);
            }
        }
    }

    public static class Edge {
        int x;
        int y;
        int a;
        int b;

        public static Comparator<Edge> sortByX = new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.x - o2.x;
            }
        };
        public static Comparator<Edge> sortByY = new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return o1.y - o2.y;
            }
        };

        @Override
        public String toString() {
            return String.format("(%d,%d)[%d,%d]", a, b, x, y);
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
            NIL.y = -1;
            NIL.maxYNode = NIL;
        }

        LCTNode left = NIL;
        LCTNode right = NIL;
        LCTNode father = NIL;
        LCTNode treeFather = NIL;
        boolean reverse;
        int id;
        int y;
        LCTNode maxYNode;
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
            maxYNode = this;
            if (left.maxYNode.y > maxYNode.y) {
                maxYNode = left.maxYNode;
            }
            if (right.maxYNode.y > maxYNode.y) {
                maxYNode = right.maxYNode;
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
