package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ1105 {
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

            Stone[] stones = new Stone[n];
            for (int i = 0; i < n; i++) {
                stones[i] = new Stone();
                stones[i].x = io.readInt();
                stones[i].y = io.readInt();
                stones[i].w = io.readInt();
            }

            RectBuilder builder = new RectBuilder();
            for (Stone stone : stones) {
                if (stone.y >= stone.x) {
                    builder.add(stone.x, stone.y);
                } else {
                    builder.add(stone.y, stone.x);
                }
            }

            Rect rect = builder.build();
            io.cache.append(rect.perimeter()).append(' ');

            List<Rect> rects = new ArrayList();
            rects.add(rect);
            rects.add(new RectBuilder().add(rect.t, rect.l)
                    .add(rect.t, rect.r).add(rect.b, rect.l)
                    .add(rect.b, rect.r).build());
            if (rect.r > rect.b) {
                rects.add(new RectBuilder()
                        .add(rect.l, rect.b)
                        .add(rect.t, rect.b)
                        .add(rect.t, rect.r)
                        .add(rect.l, rect.r)
                        .build());
                rects.add(new RectBuilder()
                        .add(rect.r, rect.t)
                        .add(rect.b, rect.l)
                        .add(rect.r, rect.l)
                        .add(rect.b, rect.t)
                        .build());
            }

            int minFee = Integer.MAX_VALUE;
            Rect best = null;
            for (Rect r : rects) {
                int fee = move(stones, r);
                if (minFee > fee) {
                    minFee = fee;
                    best = r;
                }
            }

            io.cache.append(minFee).append('\n');
            record(stones, best);

        }

        public int record(Stone[] stones, Rect rect) {
            int sum = 0;
            for (Stone stone : stones) {
                if (rect.contain(stone)) {
                    io.cache.append('0');
                    continue;
                }
                io.cache.append('1');
            }
            return sum;
        }

        public int move(Stone[] stones, Rect rect) {
            if (rect == null) {
                return Integer.MAX_VALUE;
            }

            int sum = 0;
            for (Stone stone : stones) {
                if (rect.contain(stone)) {
                    continue;
                }
                sum += stone.w;
            }
            return sum;
        }
    }

    public static class Stone {
        int x;
        int y;
        int w;
    }

    public static class Rect {
        int l, r, t, b;

        public boolean contain(Stone s) {
            return s.x >= l && s.x <= r &&
                    s.y >= b && s.y <= t;
        }

        public long perimeter() {
            return (long) (r - l) * 2
                    + (t - b) * 2;
        }
    }

    public static class RectBuilder {
        int l = Integer.MAX_VALUE;
        int r = Integer.MIN_VALUE;
        int t = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;

        public RectBuilder add(int x, int y) {
            l = Math.min(l, x);
            r = Math.max(r, x);
            t = Math.max(t, y);
            b = Math.min(b, y);
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
