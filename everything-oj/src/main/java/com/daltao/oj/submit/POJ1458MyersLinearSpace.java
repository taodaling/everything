package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class POJ1458MyersLinearSpace {
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
        char[] a = new char[250];
        char[] b = new char[250];
        RangeArray furthest00 = new RangeArray(-500, 500);
        RangeArray furthestnm = new RangeArray(-500, 500);


        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            while (io.hasMore()) {
                solve();
            }
        }

        public void solve() {
            int n = io.readString(a, 0);
            int m = io.readString(b, 0);
            lcs(new CharArray(a, 0, n), new CharArray(b, 0, m));
            io.cache.append('\n');
        }



        public void output(CharArray array) {
            for (int i = 0, until = array.size(); i < until; i++) {
                io.cache.append(array.get(i));
            }
        }

        public void lcs0(CharArray a, CharArray b, int i, int u, int x, int d) {
            int n = a.size();
            int m = b.size();
            int y = x - i;
            int v = u - i;
            if (d > 1) {
                lcs(a.subArray(0, u), b.subArray(0, v));
                output(a.subArray(u, x));
                lcs(a.subArray(x, n), b.subArray(y, m));
            } else if (n < m) {
                output(a);
            } else {
                output(b);
            }
        }

        public void lcs(CharArray a, CharArray b) {
            debug.debug("a", a);
            debug.debug("b", b);
            int n = a.size();
            int m = b.size();
            if (n == 0 || m == 0) {
                return;
            }
            furthest00.fill(-1, -m, n);
            furthestnm.fill(n + 1, -m, n);
            furthest00.set(0, 0);
            furthestnm.set(0, n + 1);
            for (int d = 0; ; d++) {
                for (int left = -d, right = d, i = left; i <= right; i += 2) {
                    if (i > n) {
                        continue;
                    }
                    if (i < -m) {
                        continue;
                    }
                    int x = 0;
                    if (i > left && i > -m) {
                        x = Math.max(x, Math.min(furthest00.get(i - 1) + 1, n));
                    }
                    if (i < right && i < n) {
                        x = Math.max(x, furthest00.get(i + 1));
                    }
                    int y = x - i;
                    while (x < n && y < m && a.get(x) == b.get(y)) {
                        x++;
                        y++;
                    }
                    furthest00.set(i, x);
                    if (furthest00.get(i) >= furthestnm.get(i)) {
                        lcs0(a, b, i, furthestnm.get(i), furthest00.get(i), d * 2 - 1);
                        return;
                    }
                }
                for (int left = -d + n - m, right = d + n - m, i = left; i <= right; i += 2) {
                    if (i > n) {
                        continue;
                    }
                    if (i < -m) {
                        continue;
                    }
                    int x = n;
                    if (i > left && i > -m) {
                        x = Math.min(x, furthestnm.get(i - 1));
                    }
                    if (i < right && i < n) {
                        x = Math.min(x, Math.max(0, furthestnm.get(i + 1) - 1));
                    }
                    int y = x - i;
                    while (x > 0 && y > 0 && a.get(x - 1) == b.get(y - 1)) {
                        x--;
                        y--;
                    }
                    furthestnm.set(i, x);
                    if (furthest00.get(i) >= furthestnm.get(i)) {
                        lcs0(a, b, i, furthestnm.get(i), furthest00.get(i), d * 2);
                        return;
                    }
                }
            }
        }
    }

    public static class CharArray {
        private int offset;
        private int length;
        private char[] data;

        public CharArray(char[] data, int offset, int length) {
            this.data = data;
            this.offset = offset;
            this.length = length;
        }

        public char get(int i) {
            return data[i + offset];
        }

        public void set(int i, char c) {
            data[i + offset] = c;
        }

        public int size() {
            return length;
        }

        public CharArray subArray(int begin, int end) {
            return new CharArray(data, offset + begin, end - begin);
        }

        @Override
        public String toString() {
            return String.valueOf(data, offset, length);
        }
    }

    public static class RangeArray {
        private int offset;
        private int[] data;

        public RangeArray(int left, int right) {
            this.offset = -left;
            this.data = new int[right - left + 1];
        }

        public int get(int i) {
            return data[i + offset];
        }

        public void set(int i, int val) {
            data[i + offset] = val;
        }

        public void fill(int val, int l, int r) {
            Arrays.fill(data, offset + l, offset + r + 1, val);
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
            long num = readLong();
            if (next != '.') {
                return num;
            }

            next = read();
            double f = readLong();
            while (f >= 100000000) {
                f /= 1000000000;
            }
            while (f >= 10000) {
                f /= 100000;
            }
            while (f >= 100) {
                f /= 1000;
            }
            while (f >= 1) {
                f /= 10;
            }
            return num > 0 ? (num + f) : (num - f);
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
    }

    public static class Debug {
        private boolean allowDebug;

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
