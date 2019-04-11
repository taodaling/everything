package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class POJ2749 {
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
        int[][] map = new int[1001][1001];
        int n;
        int m;
        int k;
        Node[] nodes;
        Deque<Node> deque = new ArrayDeque(1000);

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
            m = io.readInt();
            k = io.readInt();
            int x1 = io.readInt();
            int y1 = io.readInt();
            int x2 = io.readInt();
            int y2 = io.readInt();
            int distBetweenTransferPoint = Math.abs(x1 - x2) + Math.abs(y1 - y2);

            nodes = new Node[n * 2 + 1];
            for (int i = 1, until = n * 2; i <= until; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
                if (i > n) {
                    nodes[i - n].opposite = nodes[i];
                    nodes[i].opposite = nodes[i - n];
                }
            }
            for (int i = 1; i <= n; i++) {
                int x = io.readInt();
                int y = io.readInt();
                nodes[i].dist = Math.abs(x - x1) + Math.abs(y - y1);
                nodes[i + n].dist = Math.abs(x - x2) + Math.abs(y - y2);
            }
            for (int i = 1, until = n * 2; i <= until; i++) {
                for (int j = 1; j < i; j++) {
                    int ni = opposite(i, n);
                    int nj = opposite(j, n);
                    int len = nodes[i].dist + nodes[j].dist;
                    if (i > n && j <= n) {
                        len += distBetweenTransferPoint;
                    }
                    //!(a && b) => (!a || !b)
                    map[i][nj] = Math.max(map[i][nj], len);
                    map[j][ni] = Math.max(map[j][ni], len);
                }
            }
            for (int i = 0; i < m; i++) {
                //(a != b)
                int a = io.readInt();
                int b = io.readInt();
                int na = a + n;
                int nb = b + n;
                map[a][nb] = inf;
                map[b][na] = inf;
                map[na][b] = inf;
                map[nb][a] = inf;
            }
            for (int i = 0; i < k; i++) {
                //(a == b)
                int a = io.readInt();
                int b = io.readInt();
                int na = a + n;
                int nb = b + n;
                map[a][b] = inf;
                map[b][a] = inf;
                map[na][nb] = inf;
                map[nb][na] = inf;
            }

            int l = 0;
            int r = 10000000;
            while (l < r) {
                int m = (l + r) >> 1;
                if (test(m)) {
                    r = m;
                } else {
                    l = m + 1;
                }
            }

            io.cache.append(l == 10000000 ? -1 : l);
        }

        public boolean test(int limit) {
            for (int i = 1, until = n + n; i <= until; i++) {
                nodes[i].dfn = 0;
            }
            for (int i = 1, until = n + n; i <= until; i++) {
                tarjan(nodes[i], limit);
            }
            for (int i = 1, until = n + n; i <= until; i++) {
                if (nodes[i].set == nodes[i].opposite.set) {
                    return false;
                }
            }
            return true;
        }

        public int opposite(int i, int n) {
            return i > n ? i - n : i + n;
        }

        static int id = 0;

        public static int order() {
            return ++id;
        }

        public void tarjan(Node root, int limit) {
            if (root.dfn != 0) {
                return;
            }
            root.low = root.dfn = order();
            root.inStack = true;
            deque.addLast(root);
            for (int i = 1, until = n + n; i <= until; i++) {
                if (map[root.id][i] <= limit) {
                    continue;
                }
                tarjan(nodes[i], limit);
                if (nodes[i].inStack) {
                    root.low = Math.min(root.low, nodes[i].low);
                }
            }
            if (root.low == root.dfn) {
                while (true) {
                    Node last = deque.removeLast();
                    last.inStack = false;
                    last.set = root;
                    if (last == root) {
                        break;
                    }
                }
            }
        }
    }

    public static class Node {
        int id;
        int dfn;
        int low;
        boolean inStack;
        Node set;
        Node opposite;
        int dist;

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
