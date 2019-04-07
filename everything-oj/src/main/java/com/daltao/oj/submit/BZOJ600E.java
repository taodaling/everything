package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ600E {
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
                nodes[i].color = io.readInt();
            }
            for (int i = 1; i < n; i++) {
                Node a = nodes[io.readInt()];
                Node b = nodes[io.readInt()];
                a.children.add(b);
                b.children.add(a);
            }
            heavyLightSplit(nodes[1], null);
            DomainColors colors = new DomainColors(n + 1);
            long[] result = new long[n + 1];
            dfs(nodes[1], null, colors, false, result);
            for (int i = 1; i <= n; i++) {
                io.cache.append(result[i]).append(' ');
            }
        }

        public static void contribute(Node root, Node parent, DomainColors domainColors) {
            for (Node node : root.children) {
                if (node == parent) {
                    continue;
                }
                contribute(node, root, domainColors);
            }
            domainColors.addColor(root.color);
        }

        public static void dfs(Node root, Node parent, DomainColors domainColors, boolean delete, long[] records) {
            for (Node node : root.children) {
                if (node == parent || node == root.heavyNode) {
                    continue;
                }
                dfs(node, root, domainColors, true, records);
            }
            if (root.heavyNode != null) {
                dfs(root.heavyNode, root, domainColors, false, records);
            }
            for (Node node : root.children) {
                if (node == parent || node == root.heavyNode) {
                    continue;
                }
                contribute(node, root, domainColors);
            }
            domainColors.addColor(root.color);
            records[root.id] = domainColors.sum;
            if (delete) {
                domainColors.clear();
            }
        }

        public static void heavyLightSplit(Node root, Node parent) {
            int heavyNodeSize = 0;
            root.size = 1;
            for (Node node : root.children) {
                if (node == parent) {
                    continue;
                }
                heavyLightSplit(node, root);
                root.size += node.size;
                if (heavyNodeSize < node.size) {
                    heavyNodeSize = node.size;
                    root.heavyNode = node;
                }
            }
            return;
        }
    }

    public static class DomainColors {
        VersionArray array;
        long sum;
        int max;

        public DomainColors(int cap) {
            array = new VersionArray(cap);
            max = 0;
            sum = 0;
        }

        public void clear() {
            array.clear();
            sum = 0;
            max = 0;
        }

        public void addColor(int c) {
            int count = array.inc(c);
            if (count >= max) {
                if (count == max) {
                    sum += c;
                } else {
                    max = count;
                    sum = c;
                }
            }
        }
    }

    public static class VersionArray {
        int[] data;
        int[] version;
        int now;

        public VersionArray(int cap) {
            data = new int[cap];
            version = new int[cap];
            now = 0;
        }

        public void clear() {
            now++;
        }

        public void visit(int i) {
            if (version[i] < now) {
                version[i] = now;
                data[i] = 0;
            }
        }

        public void set(int i, int v) {
            version[i] = now;
            data[i] = v;
        }

        public int get(int i) {
            visit(i);
            return data[i];
        }

        public int inc(int i) {
            visit(i);
            return ++data[i];
        }
    }

    public static class Node {
        List<Node> children = new ArrayList<>();
        Node heavyNode;
        int size;
        int color;
        int id;

        @Override
        public String toString() {
            return "" + id;
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
