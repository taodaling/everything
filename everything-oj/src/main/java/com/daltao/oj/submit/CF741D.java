package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CF741D {
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
            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
            char[] s = new char[1];
            for (int i = 2; i <= n; i++) {
                Node a = nodes[i];
                Node b = nodes[io.readInt()];
                io.readString(s, 0);
                buildEdgeBetween(a, b, s[0]);
            }
            heavyLightSplit(nodes[1], null);
            dfs(nodes[1], null, false, 0, 0, new Summary(1 << ('v' - 'a' + 1)));
            for (int i = 1; i <= n; i++) {
                io.cache.append(nodes[i].dp).append(' ');
            }
        }

        public static void contribute(Node root, Node parent, int mask, int depth, Summary summary) {
            for (Edge edge : root.children) {
                Node node = edge.anotherSide(root);
                if (node == parent) {
                    continue;
                }
                contribute(node, root, mask ^ edge.bit, depth + 1, summary);
            }
            summary.add(mask, depth);
        }

        public static void calculate(Node root, Node parent, int mask, int depth, Summary summary) {
            for (Edge edge : root.children) {
                Node node = edge.anotherSide(root);
                if (node == parent) {
                    continue;
                }
                calculate(node, root, mask ^ edge.bit, depth + 1, summary);
            }
            summary.record(mask, depth);
        }

        public static void dfs(Node root, Node parent, boolean delete, int mask, int depth, Summary summary) {
            for (Edge edge : root.children) {
                if (edge == root.heavyEdge) {
                    continue;
                }
                Node node = edge.anotherSide(root);
                if (node == parent) {
                    continue;
                }
                dfs(node, root, true, mask ^ edge.bit, depth + 1, summary);
            }
            if (root.heavyEdge != null) {
                dfs(root.heavyEdge.anotherSide(root), root, false, mask ^ root.heavyEdge.bit, depth + 1, summary);
            }
            summary.max = 0;
            for (Edge edge : root.children) {
                if (edge == root.heavyEdge) {
                    continue;
                }
                Node node = edge.anotherSide(root);
                if (node == parent) {
                    continue;
                }
                calculate(node, root, mask ^ edge.bit, depth + 1, summary);
                contribute(node, root, mask ^ edge.bit, depth + 1, summary);
            }
            summary.record(mask, depth);
            summary.add(mask, depth);
            root.dp = Math.max(0, summary.max - 2 * depth);
            for (Edge edge : root.children) {
                Node node = edge.anotherSide(root);
                if (node == parent) {
                    continue;
                }
                root.dp = Math.max(root.dp, node.dp);
            }
            if (delete) {
                summary.clear();
            }
        }

        public static void heavyLightSplit(Node root, Node parent) {
            root.size = 1;
            int heavyNodeSize = 0;
            for (Edge edge : root.children) {
                Node node = edge.anotherSide(root);
                if (node == parent) {
                    continue;
                }
                heavyLightSplit(node, root);
                root.size += node.size;
                if (heavyNodeSize < node.size) {
                    heavyNodeSize = node.size;
                    root.heavyEdge = edge;
                }
            }
        }

        public static void buildEdgeBetween(Node a, Node b, char c) {
            Edge edge = new Edge();
            edge.a = a;
            edge.b = b;
            edge.c = c;
            edge.bit = 1 << (c - 'a');
            a.children.add(edge);
            b.children.add(edge);
        }
    }

    public static class Summary {
        VersionArray array;
        int max;
        int largestBit = 1 << ('v' - 'a');

        public Summary(int cap) {
            array = new VersionArray(cap);
        }

        public void clear() {
            array.clear();
            max = 0;
        }

        public void record(int x, int l) {
            max = Math.max(max, array.get(x) + l);
            for (int i = 1; i <= largestBit; i <<= 1) {
                max = Math.max(max, array.get(x ^ i) + l);
            }
        }

        public void add(int i, int l) {
            array.setMax(i, l);
        }
    }

    public static class Node {
        List<Edge> children = new ArrayList<>();
        int size;
        Edge heavyEdge;
        int id;
        int dp;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class Edge {
        Node a;
        Node b;
        char c;
        int bit;

        public Node anotherSide(Node src) {
            return a == src ? b : a;
        }
    }

    public static class VersionArray {
        int[] data;
        int[] version;
        int now;

        public VersionArray(int cap) {
            data = new int[cap];
            version = new int[cap];
            now = 1;
        }

        public void clear() {
            now++;
        }

        public void visit(int i) {
            if (version[i] < now) {
                version[i] = now;
                data[i] = -10000000;
            }
        }

        public void setMax(int i, int v) {
            visit(i);
            data[i] = Math.max(data[i], v);
        }

        public int get(int i) {
            visit(i);
            return data[i];
        }
    }

    public static class FastIO {
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        public final StringBuilder cache = new StringBuilder();

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
                sign = next == '+' ? true : false;
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

        public void flush() {
            try {
                os.write(cache.toString().getBytes(charset));
                os.flush();
                cache.setLength(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean hasMore() {
            skipBlank();
            return next != -1;
        }
    }

    public static class Debug {
        private boolean allowDebug;

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

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
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
