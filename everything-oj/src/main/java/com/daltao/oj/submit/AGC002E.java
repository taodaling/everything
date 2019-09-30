package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AGC002E {
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
        long lInf = (long) 1e18;
        double dInf = 1e50;

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
            int[] data = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                data[i] = io.readInt();
            }
            Randomized.randomizedArray(data, 1, n + 1);
            Arrays.sort(data, 1, n + 1);
            Memory.reverse(data, 1, n + 1);

            int height = data[1];
            int[] tolerance = new int[n + 1];
            tolerance[0] = height - 1;
            int remain = height;
            for (int i = 1; i <= n; i++) {
                if (i + 1 > n) {
                    remain = 0;
                }
                if (i + 1 <= n && data[i] != data[i + 1]) {
                    remain -= data[i] - data[i + 1];
                }
                tolerance[i] = remain - 1;
            }


            boolean win = false;
            for (int i = 0; i <= n; i++) {
                if (tolerance[i] == i) {
                    int distToRight = 0;
                    for (int j = i; j + 1 <= n && tolerance[j + 1] == tolerance[j]; j++) {
                        distToRight++;
                    }
                    if (distToRight % 2 == 1) {
                        win = true;
                    } else {
                        win = false;
                    }
                    break;
                }
                if (i + 1 > n || tolerance[i + 1] <= i) {
                    int distToRight = 0;
                    for (int j = i; j + 1 <= n && tolerance[j + 1] == i; j++) {
                        distToRight++;
                    }
                    int distToTop = tolerance[i] - i;
                    if (i + 1 <= n && distToRight % 2 == 1 || distToTop % 2 == 1) {
                        win = true;
                    } else {
                        win = false;
                    }
                    break;
                }
            }

            if (win) {
                first();
            } else {
                second();
            }
        }

        public void first() {
            io.cache.append("First");
        }

        public void second() {
            io.cache.append("Second");
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

        private static int gcd(int a, int b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private static int gcd0(int a, int b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        public static <T> void rotate(List<T> list, int l, int newBeg, int r) {
            int offset = l;
            int len = r - l + 1;
            int step = len - (newBeg - l);
            int g = gcd0(newBeg, r - l + 1);
            for (int i = 0; i < g; i++) {
                T take = list.get(i + offset);
                int ni = i;
                while ((ni = (ni + step) % len) != i) {
                    T tmp = list.get(ni + offset);
                    list.set(ni + offset, take);
                    take = tmp;
                }
                list.set(i + offset, take);
            }
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


    /**
     * Created by dalt on 2018/6/1.
     */
    public static class Randomized {
        static Random random = new Random();

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
