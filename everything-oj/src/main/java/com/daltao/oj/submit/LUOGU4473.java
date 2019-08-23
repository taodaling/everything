package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class LUOGU4473 {
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

        int n;
        int m;
        int[][] A;
        int[][] B;
        int[][] dist;

        public void solve() {
            n = io.readInt();
            m = io.readInt();
            A = new int[n][m];
            B = new int[n][m];
            dist = new int[n][m];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    B[i][j] = io.readInt();
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    A[i][j] = io.readInt();
                }
            }

            int[][] positions = new int[3][4];
            positions[0][3] = 'X';
            positions[1][3] = 'Y';
            positions[2][3] = 'Z';
            for (int i = 0; i < 3; i++) {
                positions[i][1] = io.readInt() - 1;
                positions[i][2] = io.readInt() - 1;
            }

            for (int i = 0; i < 3; i++) {
                findShortestPath(positions[i][1], positions[i][2]);
                for (int j = 0; j < 3; j++) {
                    positions[j][0] += dist[positions[j][1]][positions[j][2]];
                }
            }

            Arrays.sort(positions, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return o1[0] == o2[0] ? o1[3] - o2[3] : o1[0] - o2[0];
                }
            });

            if (positions[0][0] >= inf) {
                io.cache.append("NO");
                return;
            }

            io.cache.append((char) positions[0][3]).append('\n')
                    .append(positions[0][0]);

        }

        public int idOf(int x, int y) {
            return x * m + y;
        }

        public int xOf(int v) {
            return v / m;
        }

        public int yOf(int v) {
            return v % m;
        }

        public void findShortestPath(int x, int y) {
            int l = 0;
            int r = n * m - 1;
            Segment segment = new Segment(l, r);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    dist[i][j] = inf;
                }
            }
            segment.update(idOf(x, y), idOf(x, y), l, r, 0);

            while (segment.minDistSegment.dist < inf) {
                int i = xOf(segment.minDistSegment.id);
                int j = yOf(segment.minDistSegment.id);

                int a = A[i][j];
                int b = B[i][j];
                dist[i][j] = segment.minDistSegment.dist;
                segment.freeze(segment.minDistSegment.id, segment.minDistSegment.id, l, r);

                for (int top = Math.max(i - b, 0), bot = Math.min(i + b, n - 1); top <= bot; top++) {
                    int left = Math.max(0, j - b + Math.abs(top - i));
                    int right = Math.min(m - 1, j + b - Math.abs(top - i));
                    segment.update(idOf(top, left), idOf(top, right), l, r, dist[i][j] + a);
                }
            }
        }
    }

    public static class Segment implements Cloneable {
        static final int inf = (int) 1e8;
        private Segment left;
        private Segment right;
        private int dist;
        private int setMin = inf;
        private boolean freeze;
        private int id;
        private Segment minDistSegment;

        public void setMin(int min) {
            setMin = Math.min(setMin, min);
            if (!freeze) {
                dist = Math.min(dist, min);
            }
            if (minDistSegment != this) {
                minDistSegment.setMin(min);
            }
        }

        public void pushUp() {
            minDistSegment = left.minDistSegment;
            if (minDistSegment.dist > right.minDistSegment.dist) {
                minDistSegment = right.minDistSegment;
            }
        }

        public void pushDown() {
            if (setMin != inf) {
                left.setMin(setMin);
                right.setMin(setMin);
                setMin = inf;
            }
        }

        public Segment(int l, int r) {
            if (l < r) {
                int m = (l + r) >> 1;
                left = new Segment(l, m);
                right = new Segment(m + 1, r);
                pushUp();
            } else {
                id = l;
                dist = inf;
                minDistSegment = this;
            }
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void update(int ll, int rr, int l, int r, int min) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setMin(min);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.update(ll, rr, l, m, min);
            right.update(ll, rr, m + 1, r, min);
            pushUp();
        }

        public void freeze(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                dist = inf + 1;
                freeze = true;
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.freeze(ll, rr, l, m);
            right.freeze(ll, rr, m + 1, r);
            pushUp();
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
