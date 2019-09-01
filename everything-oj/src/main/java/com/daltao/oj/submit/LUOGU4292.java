package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LUOGU4292 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
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

        int atLeast;
        int noMore;

        public void solve() {
            int n = io.readInt();
            atLeast = io.readInt();
            noMore = io.readInt();
            Node[] nodes = new Node[n + 1];
            Edge[] edges = new Edge[n - 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            double l = 0;
            double r = Integer.MIN_VALUE;
            for (int i = 0; i < n - 1; i++) {
                edges[i] = new Edge();
                edges[i].a = nodes[io.readInt()];
                edges[i].b = nodes[io.readInt()];
                edges[i].w = io.readInt();

                edges[i].a.edgeList.add(edges[i]);
                edges[i].b.edgeList.add(edges[i]);

                r = Math.max(r, edges[i].w);
            }

            prepare(nodes[1], null);
            allocate(nodes[1], null, nodes[1]);
            while (l + 1e-4 < r) {
                double m = (l + r) / 2;
                for (Edge edge : edges) {
                    edge.fixW = edge.w - m;
                }
                dfs(nodes[1], null);
                if (nodes[1].maxDiameter <= 0) {
                    r = m;
                } else {
                    l = m;
                }
            }

            io.cache.append(String.format("%.3f", l));
        }

        public void prepare(Node root, Edge from) {
            root.size = 0;
            for (Edge e : root.edgeList) {
                if (e == from) {
                    continue;
                }
                Node node = e.other(root);
                prepare(node, e);
                if (root.size < node.size + 1) {
                    root.size = node.size + 1;
                    root.heavy = e;
                }
            }
        }

        public void allocate(Node root, Edge from, Node tip) {
            root.tip = tip;
            if (tip == root) {
                tip.segment = new Segment(0, root.size);
            }
            for (Edge e : root.edgeList) {
                if (e == from || e == root.heavy) {
                    continue;
                }
                Node node = e.other(root);
                allocate(node, e, node);
            }
            if (root.heavy != null) {
                allocate(root.heavy.other(root), root.heavy, tip);
            }
        }

        public void dfs(Node root, Edge from) {
            root.maxDiameter = 0;
            Node tip = root.tip;
            int offset = root.tip.size - root.size;
            if (root == root.tip) {
                tip.segment.updateSolid(0, root.size, 0, root.size, -1e50);
            }
            if (root.heavy != null) {
                dfs(root.heavy.other(root), root.heavy);
                root.maxDiameter = Math.max(root.maxDiameter, root.heavy.other(root).maxDiameter);
                tip.segment.updateModify(offset + 1, tip.size, 0, tip.size, root.heavy.fixW);
                int l = Math.max(atLeast, 0);
                int r = Math.min(noMore, root.size);
                root.maxDiameter = Math.max(root.maxDiameter, tip.segment.query(l + offset, r + offset, 0, tip.size));
            }
            tip.segment.updateSolid(offset, offset, 0, tip.size, 0);
            for (Edge e : root.edgeList) {
                if (e == from || e == root.heavy) {
                    continue;
                }
                Node node = e.other(root);
                dfs(node, e);
                root.maxDiameter = Math.max(root.maxDiameter, node.maxDiameter);
                for (int i = 0; i <= node.size; i++) {
                    double w = node.segment.query(i, i, 0, node.size) + e.fixW;
                    int l = atLeast - 1 - i;
                    int r = noMore - 1 - i;
                    l = Math.max(l, 0);
                    r = Math.min(r, root.size);
                    double path = w + tip.segment.query(l + offset, r + offset, 0, tip.size);
                    root.maxDiameter = Math.max(root.maxDiameter, path);
                }
                for (int i = 0; i <= node.size; i++) {
                    double w = node.segment.query(i, i, 0, node.size) + e.fixW;
                    tip.segment.updateMax(offset + i + 1, offset + i + 1, 0, tip.size, w);
                }
            }
        }
    }

    public static class Edge {
        int w;
        double fixW;
        Node a;
        Node b;

        Node other(Node x) {
            return x == a ? b : a;
        }
    }

    public static class Segment implements Cloneable {
        private Segment left;
        private Segment right;
        private double max;
        private double solid;
        private boolean dirty;
        private double modify;

        public void setSolid(double s) {
            dirty = true;
            solid = s;
            max = s;
            modify = 0;
        }

        public void setModify(double m) {
            modify += m;
            max += m;
        }

        public void pushUp() {
            max = Math.max(left.max, right.max);
        }

        public void pushDown() {
            if (dirty) {
                left.setSolid(solid);
                right.setSolid(solid);
                dirty = false;
            }
            if (modify != 0) {
                left.setModify(modify);
                right.setModify(modify);
                modify = 0;
            }
        }

        public Segment(int l, int r) {
            if (l < r) {
                int m = (l + r) >> 1;
                left = new Segment(l, m);
                right = new Segment(m + 1, r);
                pushUp();
            } else {

            }
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void updateSolid(int ll, int rr, int l, int r, double val) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setSolid(val);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.updateSolid(ll, rr, l, m, val);
            right.updateSolid(ll, rr, m + 1, r, val);
            pushUp();
        }

        public void updateModify(int ll, int rr, int l, int r, double val) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setModify(val);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.updateModify(ll, rr, l, m, val);
            right.updateModify(ll, rr, m + 1, r, val);
            pushUp();
        }

        public void updateMax(int ll, int rr, int l, int r, double val) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                max = Math.max(max, val);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.updateMax(ll, rr, l, m, val);
            right.updateMax(ll, rr, m + 1, r, val);
            pushUp();
        }

        public double query(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return Long.MIN_VALUE;
            }
            if (covered(ll, rr, l, r)) {
                return max;
            }
            pushDown();
            int m = (l + r) >> 1;
            return Math.max(left.query(ll, rr, l, m),
                    right.query(ll, rr, m + 1, r));
        }

    }


    public static class Node {
        List<Edge> edgeList = new ArrayList(2);
        double maxDiameter;
        Edge heavy;
        Node tip;
        int size;
        Segment segment;
        int offset;
        int id;

        @Override
        public String toString() {
            return "" + id;
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
