package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ1818 {
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
            int[] allPos = new int[n * 2];
            int[][] pos = new int[n][2];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    pos[i][j] = io.readInt();
                    allPos[i * 2 + j] = pos[i][j];
                }
            }

            DiscreteMap discreteMap = new DiscreteMap(allPos, 0, 2 * n);
            int m = discreteMap.maxRank();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 2; j++) {
                    pos[i][j] = discreteMap.rankOf(pos[i][j]);
                }
            }

            int[][] xLines = new int[m + 1][3];
            int[][] yLines = new int[m + 1][3];
            for (int i = 0; i <= m; i++) {
                yLines[i][0] = xLines[i][0] = m + 1;
                yLines[i][1] = xLines[i][1] = -1;
                yLines[i][2] = xLines[i][2] = i;
            }
            for (int i = 0; i < n; i++) {
                int x = pos[i][0];
                int y = pos[i][1];
                xLines[y][0] = Math.min(xLines[y][0], x);
                xLines[y][1] = Math.max(xLines[y][1], x);
                yLines[x][0] = Math.min(yLines[x][0], y);
                yLines[x][1] = Math.max(yLines[x][1], y);
            }

            int[][] xLinesOrderByRightPointByR = xLines.clone();
            Arrays.sort(xLinesOrderByRightPointByR, new Comparator<int[]>() {
                @Override
                public int compare(int[] a, int[] b) {
                    return a[1] - b[1];
                }
            });
            int[][] xLinesOrderByRightPointByL = xLines.clone();
            Arrays.sort(xLinesOrderByRightPointByL, new Comparator<int[]>() {
                @Override
                public int compare(int[] a, int[] b) {
                    return a[0] - b[0];
                }
            });

            Deque<int[]> eventsOfL = new ArrayDeque(m);
            Deque<int[]> eventsOfR = new ArrayDeque(m);
            for (int i = 0; i <= m; i++) {
                if (xLinesOrderByRightPointByR[i][1] != -1) {
                    eventsOfR.addLast(xLinesOrderByRightPointByR[i]);
                }
                if (xLinesOrderByRightPointByL[i][0] != m + 1) {
                    eventsOfL.addLast(xLinesOrderByRightPointByL[i]);
                }
            }

            BIT bit = new BIT(m + 1);
            long total = 0;
            for (int i = 0; i <= m; i++) {
                while (!eventsOfL.isEmpty() && eventsOfL.peekFirst()[0] == i) {
                    bit.update(eventsOfL.removeFirst()[2] + 1, 1);
                }

                if (yLines[i][1] != -1) {
                    int u = yLines[i][1] + 1;
                    int d = yLines[i][0] + 1;
                    total += bit.query(u) - bit.query(d - 1);
                }

                while (!eventsOfR.isEmpty() && eventsOfR.peekFirst()[1] == i) {
                    bit.update(eventsOfR.removeFirst()[2] + 1, -1);
                }
            }

            io.cache.append(total);
        }
    }

    /**
     * Created by dalt on 2018/6/1.
     */
    public static class Randomized {
        static Random random = new Random(12345678);

        public static double nextDouble(double min, double max) {
            return random.nextDouble() * (max - min) + min;
        }

        public static void randomizedArray(int[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                int tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(long[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                long tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(double[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                double tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(float[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                float tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static <T> void randomizedArray(T[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                T tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static int nextInt(int l, int r) {
            return random.nextInt(r - l + 1) + l;
        }
    }

    public static class DiscreteMap {
        int[] val;
        int f;
        int t;

        public DiscreteMap(int[] val, int f, int t) {
            Randomized.randomizedArray(val, f, t);
            Arrays.sort(val, f, t);
            int wpos = f + 1;
            for (int i = f + 1; i < t; i++) {
                if (val[i] == val[i - 1]) {
                    continue;
                }
                val[wpos++] = val[i];
            }
            this.val = val;
            this.f = f;
            this.t = wpos;
        }

        public int minRank() {
            return 0;
        }

        public int maxRank() {
            return t - f;
        }

        /**
         * Return 0, 1, so on
         */
        public int rankOf(int x) {
            return Arrays.binarySearch(val, f, t, x) - f;
        }

        /**
         * Get the i-th smallest element
         */
        public int iThElement(int i) {
            return val[f + i];
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = f; i < t; i++) {
                builder.append(String.format("%d => %d\n", i - f, val[i]));
            }
            return builder.toString();
        }
    }

    /**
     * Created by dalt on 2018/5/20.
     */
    public static class BIT {
        private int[] data;
        private int n;

        /**
         * 创建大小A[1...n]
         */
        public BIT(int n) {
            this.n = n;
            data = new int[n + 1];
        }

        /**
         * 查询A[1]+A[2]+...+A[i]
         */
        public int query(int i) {
            int sum = 0;
            for (; i > 0; i -= i & -i) {
                sum += data[i];
            }
            return sum;
        }

        /**
         * 将A[i]更新为A[i]+mod
         */
        public void update(int i, int mod) {
            for (; i <= n; i += i & -i) {
                data[i] += mod;
            }
        }

        /**
         * 将A全部清0
         */
        public void clear() {
            Arrays.fill(data, 0);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                builder.append(query(i) - query(i - 1)).append(' ');
            }
            return builder.toString();
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
