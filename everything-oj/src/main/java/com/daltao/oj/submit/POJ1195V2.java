package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class POJ1195V2 {
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
            if (debug.allowDebug) {
                Segment2D bit = Segment2D.create(1, 3, 1, 3);
                debug.assertTrue(Segment2D.query(1, 3, 1, 3, bit, 1, 3, 1, 3) == 0);
                Segment2D.update(1, 3, 1, 3, bit, 1, 1, 2, 2, 1);
                debug.assertTrue(Segment2D.query(1, 3, 1, 3, bit, 1, 1, 1, 2) == 1);
                debug.assertTrue(Segment2D.query(1, 3, 1, 3, bit, 1, 1, 1, 1) == 0);
                debug.assertTrue(Segment2D.query(1, 3, 1, 3, bit, 1, 3, 1, 3) == 1);
            }

            io.readInt();
            int c = io.readInt();
            Segment2D bit = Segment2D.create(1, c, 1, c);
            int cmd;
            while ((cmd = io.readInt()) != 3) {
                if (cmd == 1) {
                    int x = io.readInt() + 1;
                    int y = io.readInt() + 1;
                    int v = io.readInt();
                    Segment2D.update(1, c, 1, c, bit, x, x, y, y, v);
                } else {
                    int l = io.readInt() + 1;
                    int b = io.readInt() + 1;
                    int r = io.readInt() + 1;
                    int t = io.readInt() + 1;

                    io.cache.append(
                            Segment2D.query(1, c, 1, c, bit, l, r, b, t)
                    ).append('\n');
                }
            }
        }
    }

    public static class Segment2D {
        Segment2D l;
        Segment2D r;
        Segment1D sum;

        public static Segment2D create(int l, int r, int b, int t) {
            Segment2D segment2D = new Segment2D();
            segment2D.sum = Segment1D.create(b, t);
            if (l != r) {
                int m = (l + r) >> 1;
                segment2D.l = create(l, m, b, t);
                segment2D.r = create(m + 1, r, b, t);
            }
            return segment2D;
        }

        public static int query(int ll, int rr, int bb, int tt, Segment2D segment2D, int l, int r, int b, int t) {
            if (rr < l || ll > r) {
                return 0;
            }
            if (ll >= l && rr <= r) {
                return Segment1D.query(bb, tt, segment2D.sum, b, t);
            }
            int m = (ll + rr) >> 1;
            return query(ll, m, bb, tt, segment2D.l, l, r, b, t) + query(m + 1, rr, bb, tt, segment2D.r, l, r, b, t);
        }

        public static void update(int ll, int rr, int bb, int tt, Segment2D segment2D, int l, int r, int b, int t, int v) {
            if (rr < l || ll > r) {
                return;
            }
            if (ll >= l && rr <= r) {
                Segment1D.update(bb, tt, segment2D.sum, b, t, v);
                return;
            }
            int m = (ll + rr) >> 1;
            update(ll, m, bb, tt, segment2D.l, l, r, b, t, v);
            update(m + 1, rr, bb, tt, segment2D.r, l, r, b, t, v);
            Segment1D.update(bb, tt, segment2D.sum, b, t, v);
        }
    }

    public static class Segment1D {
        Segment1D l;
        Segment1D r;
        int sum;

        public static Segment1D create(int l, int r) {
            Segment1D segment1D = new Segment1D();
            if (l != r) {
                int m = (l + r) >> 1;
                segment1D.l = create(l, m);
                segment1D.r = create(m + 1, r);
            }
            return segment1D;
        }

        public void pushUp() {
            sum = l.sum + r.sum;
        }

        public static int query(int ll, int rr, Segment1D segment1D, int l, int r) {
            if (rr < l || ll > r) {
                return 0;
            }
            if (ll >= l && rr <= r) {
                return segment1D.sum;
            }
            int m = (ll + rr) >> 1;
            return query(ll, m, segment1D.l, l, r) + query(m + 1, rr, segment1D.r, l, r);
        }

        public static void update(int ll, int rr, Segment1D segment1D, int l, int r, int v) {
            if (rr < l || ll > r) {
                return;
            }
            if (ll >= l && rr <= r) {
                segment1D.sum += v;
                return;
            }
            int m = (ll + rr) >> 1;
            update(ll, m, segment1D.l, l, r, v);
            update(m + 1, rr, segment1D.r, l, r, v);
            segment1D.pushUp();
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
