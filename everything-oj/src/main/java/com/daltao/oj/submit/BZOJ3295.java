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

public class BZOJ3295 {
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
        BIT bit;
        int n;

        Comparator<Node> sortByI = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.i - o2.i;
            }
        };
        Comparator<Node> sortByT = new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.t - o2.t;
            }
        };

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            n = io.readInt();
            int m = io.readInt();
            Node[] original = new Node[n + 1];
            Node[] nodes = new Node[n + 1];
            bit = new BIT(n);
            List<Node> result = new ArrayList(m);
            for (int i = 1; i <= n; i++) {
                original[i] = new Node();
                original[i].id = i;
                original[i].v = n + 1 - i;
            }
            for (int i = 1; i <= n; i++) {
                nodes[i] = original[io.readInt()];
                nodes[i].i = i;
                nodes[i].t = 0;
            }
            for (int i = 1; i <= m; i++) {
                Node node = original[io.readInt()];
                node.t = m - i + 1;
                result.add(node);
            }

            long total = 0;
            for (int i = 1; i <= n; i++) {
                total += bit.query(nodes[i].v - 1);
                bit.update(nodes[i].v, 1);
            }

            Arrays.sort(nodes, 1, n, sortByI);
            cdq(nodes, 1, n);
            for (Node node : result) {
                io.cache.append(total).append('\n');
                total -= node.count;
            }
        }

        public void cdq(Node[] nodes, int f, int t) {
            if (f == t) {
                return;
            }
            int m = (f + t) >> 1;
            cdq(nodes, f, m);
            cdq(nodes, m + 1, t);
            Arrays.sort(nodes, f, m + 1, sortByT);
            Arrays.sort(nodes, m + 1, t + 1, sortByT);
            count1(nodes, f, m, m + 1, t);
            count2(nodes, m + 1, t, f, m);
        }

        public void count1(Node[] nodes, int f1, int t1, int f2, int t2) {
            bit.reset();
            int i = f1;
            int j = f2;
            while (j <= t2) {
                while (i <= t1 && nodes[i].t <= nodes[j].t) {
                    bit.update(nodes[i].v, 1);
                    i++;
                }
                nodes[j].count += bit.query(nodes[j].v - 1);
                j++;
            }
        }

        public void count2(Node[] nodes, int f1, int t1, int f2, int t2) {
            bit.reset();
            int i = f1;
            int j = f2;
            while (j <= t2) {
                while (i <= t1 && nodes[i].t <= nodes[j].t) {
                    bit.update(nodes[i].v, 1);
                    i++;
                }
                nodes[j].count += bit.query(n) - bit.query(nodes[j].v);
                j++;
            }
        }

    }

    public static class Node {
        int t;
        int i;
        int v;
        long count;
        int id;

        @Override
        public String toString() {
            return "" + id;
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

        public void inc(int i, int v) {
            visit(i);
            data[i] += v;
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

    public static class BIT {
        private VersionArray data;
        private int n;

        /**
         * 创建大小A[1...n]
         */
        public BIT(int n) {
            this.n = n;
            data = new VersionArray(n + 1);
        }

        /**
         * 查询A[1]+A[2]+...+A[i]
         */
        public int query(int i) {
            int sum = 0;
            for (; i > 0; i -= i & -i) {
                sum += data.get(i);
            }
            return sum;
        }

        /**
         * 将A[i]更新为A[i]+mod
         */
        public void update(int i, int mod) {
            for (; i <= n; i += i & -i) {
                data.inc(i, mod);
            }
        }

        public void reset() {
            data.clear();
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
