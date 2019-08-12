package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ1052 {
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


        Point[] pts;
        int n;

        public void solve() {
            n = io.readInt();
            pts = new Point[n];
            for (int i = 0; i < n; i++) {
                pts[i] = new Point();
                pts[i].x = io.readInt();
                pts[i].y = io.readInt();
            }

            io.cache.append(solve1());
        }


        public int solve1() {
            RectBuilder builder = new RectBuilder();
            for (Point pt : pts) {
                builder.add(pt);
            }
            Rect rect = builder.build();

            int l = 0;
            int r = rect.length();
            while (r > l) {
                int m = (r + l) >> 1;
                Rect lt = new Rect();
                lt.l = rect.l;
                lt.t = rect.t;
                lt.r = lt.l + m;
                lt.b = lt.t - m;

                Rect rb = new Rect();
                rb.r = rect.r;
                rb.b = rect.b;
                rb.l = rb.r - m;
                rb.t = rb.b + m;

                Rect rt = new Rect();
                rt.r = rect.r;
                rt.t = rect.t;
                rt.l = rt.r - m;
                rt.b = rt.t - m;

                Rect lb = new Rect();
                lb.l = rect.l;
                lb.b = rect.b;
                lb.r = lb.l + m;
                lb.t = lb.b + m;

                if (possible(m, lt) ||
                        possible(m, rb) ||
                        possible(m, rt) ||
                        possible(m, lb)) {
                    r = m;
                } else {
                    l = m + 1;
                }
            }
            return l;
        }


        public boolean possible(int m, Rect removed) {
            RectBuilder builder = new RectBuilder();
            for (Point pt : pts) {
                if (removed.contain(pt)) {
                    continue;
                }
                builder.add(pt);
            }
            Rect rect = builder.build();

            Rect lt = new Rect();
            lt.l = rect.l;
            lt.t = rect.t;
            lt.r = lt.l + m;
            lt.b = lt.t - m;

            Rect rb = new Rect();
            rb.r = rect.r;
            rb.b = rect.b;
            rb.l = rb.r - m;
            rb.t = rb.b + m;

            Rect rt = new Rect();
            rt.r = rect.r;
            rt.t = rect.t;
            rt.l = rt.r - m;
            rt.b = rt.t - m;

            Rect lb = new Rect();
            lb.l = rect.l;
            lb.b = rect.b;
            lb.r = lb.l + m;
            lb.t = lb.b + m;

            boolean flag = true;
            for (Point pt : pts) {
                if (lt.contain(pt) || rb.contain(pt) || removed.contain(pt)) {
                    continue;
                }
                flag = false;
            }

            if (flag) {
                return true;
            }

            flag = true;

            for (Point pt : pts) {
                if (lb.contain(pt) || rt.contain(pt) || removed.contain(pt)) {
                    continue;
                }
                flag = false;
            }

            return flag;
        }
    }

    public static class Rect {
        int l, r, t, b;

        public boolean contain(Point pt) {
            return l <= pt.x && pt.x <= r
                    && b <= pt.y && pt.y <= t;
        }

        public int length() {
            return Math.max(r - l, t - b);
        }

        public boolean valid() {
            return l <= r && b <= t;
        }
    }

    public static class RectBuilder {
        int l = Integer.MAX_VALUE;
        int r = Integer.MIN_VALUE;
        int t = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;

        public RectBuilder add(Point pt) {
            l = Math.min(l, pt.x);
            r = Math.max(r, pt.x);
            t = Math.max(t, pt.y);
            b = Math.min(b, pt.y);
            return this;
        }

        public Rect build() {
            Rect rect = new Rect();
            rect.l = l;
            rect.r = r;
            rect.t = t;
            rect.b = b;
            return rect;
        }
    }

    public static class Point {
        int x;
        int y;
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
