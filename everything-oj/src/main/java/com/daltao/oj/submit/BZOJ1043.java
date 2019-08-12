package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class BZOJ1043 {
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
        double eps = 1e-8;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        private boolean near(double a, double b) {
            return Math.abs(a - b) <= eps;
        }

        public void solve() {
            int n = io.readInt();
            double[] rs = new double[n];
            double[] xs = new double[n];
            double[] ys = new double[n];
            double[] uncovered = new double[n];
            for (int i = 0; i < n; i++) {
                rs[i] = io.readDouble();
                xs[i] = io.readDouble();
                ys[i] = io.readDouble();
            }

            for (int i = 0; i < n; i++) {
                TreeSet<Interval> set = new TreeSet(Interval.sortByL);
                for (int j = i + 1; j < n; j++) {
                    double dist = distance(xs[i], ys[i], xs[j], ys[j]);
                    if (dist >= rs[i] + rs[j]) {
                        continue;
                    }
                    if (near(dist, 0) && near(rs[i], rs[j])) {
                        addInterval(set, 0, 2 * Math.PI);
                        continue;
                    }
                    if (dist + rs[j] <= rs[i]) {
                        continue;
                    }
                    if (dist + rs[i] <= rs[j]) {
                        addInterval(set, 0, 2 * Math.PI);
                        continue;
                    }
                    double angleBetweenTwoCenter = Math.atan2(ys[j] - ys[i], xs[j] - xs[i]) + Math.PI;
                    double angle = triangleAngle(rs[j], rs[i], dist);
                    double l = angleBetweenTwoCenter - angle;
                    double r = angleBetweenTwoCenter + angle;
                    if (l < 0) {
                        l += 2 * Math.PI;
                    }
                    if (r > 2 * Math.PI) {
                        r -= 2 * Math.PI;
                    }
                    addInterval(set, l, r);
                }
                uncovered[i] = 2 * Math.PI;
                for (Interval interval : set) {
                    uncovered[i] -= interval.r - interval.l;
                }
            }

            double ans = 0;
            for (int i = 0; i < n; i++) {
                ans += uncovered[i] * rs[i];
            }

            io.cache.append(String.format("%.3f", ans));
        }


        /**
         * For triangle ABC, the edge is a, b, and c. Return A as result.
         */
        public static double triangleAngle(double a, double b, double c) {
            double cosa = (b * b + c * c - a * a) / (2 * b * c);
            return Math.acos(cosa);
        }

        public static Interval intervalOf(double l, double r) {
            Interval interval = new Interval();
            interval.l = l;
            interval.r = r;
            return interval;
        }

        public void addInterval(TreeSet<Interval> set, double l, double r) {
            if (l < r) {
                addInterval(set, intervalOf(l, r));
                return;
            }
            addInterval(set, intervalOf(l, 2 * Math.PI));
            addInterval(set, intervalOf(0, r));
        }

        public void addInterval(TreeSet<Interval> set, Interval interval) {
            while (true) {
                Interval f = set.floor(interval);
                if (f == null || f.r < interval.l) {
                    break;
                }
                set.remove(f);
                interval.l = Math.min(f.l, interval.l);
                interval.r = Math.max(interval.r, f.r);
            }
            while (true) {
                Interval c = set.ceiling(interval);
                if (c == null || interval.r < c.l) {
                    break;
                }
                set.remove(c);
                interval.l = Math.min(c.l, interval.l);
                interval.r = Math.max(interval.r, c.r);
            }
            set.add(interval);
        }

        public double distance(double x1, double y1, double x2, double y2) {
            return Math.sqrt(pow2(x1 - x2) + pow2(y1 - y2));
        }

        public double pow2(double x) {
            return x * x;
        }
    }

    public static class Interval {
        double l;
        double r;

        public static Comparator<Interval> sortByL = new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                return o1.l > o2.l ? 1 : o1.l < o2.l ? -1 : 0;
            }
        };
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
