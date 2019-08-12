package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class BZOJ1021 {
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

        int[][][] dp = new int[7][200 + 1][200 + 1];

        public void solve() {
            int[] xs = new int[4];
            for (int i = 1; i <= 3; i++) {
                xs[i] = io.readInt();
            }
            int[] base = new int[]{0, 100, 50, 20, 10, 5, 1};
            int[][] abc = new int[4][7];
            int[] ys = new int[4];

            int[] preSum = new int[7];
            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j <= 6; j++) {
                    abc[i][j] = io.readInt();
                    ys[i] += base[j] * abc[i][j];
                    preSum[j] += abc[i][j];
                }
            }

            for (int i = 1; i <= 6; i++) {
                preSum[i] = preSum[i] * base[i] + preSum[i - 1];
            }

            int[] targets = new int[4];
            targets[1] = ys[1] - xs[1] + xs[3];
            targets[2] = ys[2] + xs[1] - xs[2];
            targets[3] = ys[3] + xs[2] - xs[3];

            int[] dpBound = new int[4];
            for (int i = 1; i <= 3; i++) {
                dpBound[i] = targets[i] / 5;
            }

            for (int i = 1; i <= 2; i++) {
                if (targets[i] > targets[3]) {
                    Memory.swap(xs, i, 3);
                    Memory.swap(abc, i, 3);
                    Memory.swap(ys, i, 3);
                    Memory.swap(targets, i, 3);
                    Memory.swap(dpBound, i, 3);
                }
            }

            for (int i = 1; i <= 3; i++) {
                if (targets[i] < 0) {
                    io.cache.append("impossible");
                    return;
                }
            }

            dp[0][0][0] = 0;
            for (int i = 1; i < 6; i++) {
                int val = base[i] / 5;
                int m = 0;
                for (int j = 1; j <= 3; j++) {
                    m += abc[j][i];
                }
                for (int i1 = 0; i1 <= dpBound[1]; i1++) {
                    for (int i2 = 0; i2 <= dpBound[2]; i2++) {
                        dp[i][i1][i2] = inf;
                        int i3 = preSum[i] / 5 - i1 - i2;
                        for (int j1 = 0; j1 <= m; j1++) {
                            for (int j2 = 0; j1 + j2 <= m; j2++) {
                                int j3 = m - j1 - j2;
                                if (i1 < j1 * val
                                        || i2 < j2 * val ||
                                        i3 < j3 * val) {
                                    continue;
                                }

                                int fee = Math.abs(j1 - abc[1][i])
                                        + Math.abs(j2 - abc[2][i])
                                        + Math.abs(j3 - abc[3][i]);

                                dp[i][i1][i2] = Math.min(dp[i][i1][i2], dp[i - 1][i1 - j1 * val][i2 - j2 * val] + fee);
                            }
                        }
                    }
                }
            }

            int minFee = inf;
            for (int i1 = 0; i1 <= dpBound[1]; i1++) {
                for (int i2 = 0; i2 <= dpBound[2]; i2++) {
                    if (dp[5][i1][i2] >= inf) {
                        continue;
                    }

                    int i3 = preSum[5] / 5 - i1 - i2;
                    int need1 = targets[1] - i1 * 5;
                    int need2 = targets[2] - i2 * 5;
                    int need3 = targets[3] - i3 * 5;

                    if (need1 < 0 || need2 < 0 || need3 < 0) {
                        continue;
                    }

                    minFee = Math.min(minFee, dp[5][i1][i2] + Math.abs(abc[1][6] - need1)
                            + Math.abs(abc[2][6] - need2) + Math.abs(abc[3][6] - need3)
                    );
                }
            }

            if (minFee == inf) {
                io.cache.append("impossible");
                return;
            }

            io.cache.append(minFee / 2);
        }
    }


    public static class Memory {
        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(char[] data, int i, int j) {
            char tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(int[] data, int i, int j) {
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(long[] data, int i, int j) {
            long tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(double[] data, int i, int j) {
            double tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> int min(T[] data, int from, int to, Comparator<T> cmp) {
            int m = from;
            for (int i = from + 1; i < to; i++) {
                if (cmp.compare(data[m], data[i]) > 0) {
                    m = i;
                }
            }
            return m;
        }

        public static <T> void move(T[] data, int from, int to, int step) {
            int len = to - from;
            step = len - (step % len + len) % len;
            Object[] buf = new Object[len];
            for (int i = 0; i < len; i++) {
                buf[i] = data[(i + step) % len + from];
            }
            System.arraycopy(buf, 0, data, from, len);
        }

        public static <T> void reverse(T[] data, int f, int t) {
            int l = f, r = t - 1;
            while (l < r) {
                swap(data, l, r);
                l++;
                r--;
            }
        }

        public static void reverse(int[] data, int f, int t) {
            int l = f, r = t - 1;
            while (l < r) {
                swap(data, l, r);
                l++;
                r--;
            }
        }

        public static void copy(Object[] src, Object[] dst, int srcf, int dstf, int len) {
            if (len < 8) {
                for (int i = 0; i < len; i++) {
                    dst[dstf + i] = src[srcf + i];
                }
            } else {
                System.arraycopy(src, srcf, dst, dstf, len);
            }
        }

        public static void fill(int[][] x, int val) {
            for (int[] v : x) {
                Arrays.fill(v, val);
            }
        }

        public static void fill(int[][][] x, int val) {
            for (int[][] v : x) {
                fill(v, val);
            }
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
