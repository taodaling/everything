package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ1104 {
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
            int m = io.readInt();
            int n = io.readInt();

            Node[][] grids = new Node[m][n];

            Chain[] heights = new Chain[1001];
            for (int i = 1; i <= 1000; i++) {
                heights[i] = new Chain();
            }

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    grids[i][j] = new Node();
                    grids[i][j].id = i * n + j;
                    grids[i][j].r = i;
                    grids[i][j].c = j;
                    int h = io.readInt();
                    grids[i][j].height = Math.abs(h);
                    heights[grids[i][j].height].addLast(grids[i][j]);
                    if (h > 0) {
                        grids[i][j].isCity = true;
                        grids[i][j].minCity = grids[i][j];
                    }
                }
            }

            int[][] ways = new int[][]{
                    {1, 0},
                    {-1, 0},
                    {0, 1},
                    {0, -1},
//                    {1, 1},
//                    {-1, 1},
//                    {1, -1},
//                    {-1, -1}
            };

            int sum = 0;
            Chain handleTwice = new Chain();
            for (int i = 1; i <= 1000; i++) {
                while (!heights[i].isEmpty()) {
                    Node head = heights[i].removeHead();
                    if (head.isCity) {
                        handleTwice.addLast(head);
                    }
                    for (int[] way : ways) {
                        int r = head.r + way[0];
                        int c = head.c + way[1];
                        if (r < 0 || c < 0 || r >= m || c >= n) {
                            continue;
                        }
                        if (grids[r][c].height > i) {
                            continue;
                        }
                        Node.merge(grids[r][c], head);
                    }
                }
                while (!handleTwice.isEmpty()) {
                    Node head = handleTwice.removeHead();
                    if (head.find().minCity == head) {
                        sum++;
//                        debug.debug("r", head.r);
//                        debug.debug("c", head.c);
                    }
                }
            }

            io.cache.append(sum);
        }
    }

    public static class Chain {
        Node head;
        Node tail;

        boolean isEmpty() {
            return head == null;
        }

        void addLast(Node node) {
            if (head == null) {
                head = tail = node;
                return;
            }
            tail.next = node;
            node.prev = tail;
            tail = node;
        }

        Node removeHead() {
            Node ans = head;
            head = head.next;
            if (head == null) {
                tail = null;
            } else {
                head.prev = null;
            }
            ans.next = null;
            return ans;
        }
    }

    public static class Node {
        int height;
        boolean isCity;
        int id;

        Node p = this;
        int rank = 0;
        Node minCity = null;

        int r;
        int c;

        Node next;
        Node prev;

        Node find() {
            return p == p.p ? p : (p = p.find());
        }

        static Node minCity(Node a, Node b) {
            if (a == null) {
                return b;
            }
            if (b == null) {
                return a;
            }
            int cmp = a.height - b.height;
            if (cmp == 0) {
                cmp = a.id - b.id;
            }
            return cmp < 0 ? a : b;
        }

        static void merge(Node a, Node b) {
            a = a.find();
            b = b.find();
            if (a == b) {
                return;
            }
            if (a.rank == b.rank) {
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
                a.minCity = minCity(a.minCity, b.minCity);
            } else {
                a.p = b;
                b.minCity = minCity(a.minCity, b.minCity);
            }
        }

        @Override
        public String toString() {
            return String.format("(%d, %d, %d)", r, c, height);
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
