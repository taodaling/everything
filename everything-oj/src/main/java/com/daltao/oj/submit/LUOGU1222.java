package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.TreeSet;

public class LUOGU1222 {
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
            Triangle[] ts = new Triangle[n];
            for (int i = 0; i < n; i++) {
                ts[i] = new Triangle();
                ts[i].x = io.readInt();
                ts[i].y = io.readInt();
                ts[i].a = io.readInt();
            }

            Triangle[] sortByTop = ts.clone();
            Arrays.sort(sortByTop, (a, b) -> -(a.top() - b.top()));
            Triangle[] sortByBot = ts.clone();
            Arrays.sort(sortByBot, (a, b) -> -(a.bottom() - b.bottom()));
            SimpsonRule.Function function = new SimpsonRule.Function() {
                @Override
                public double y(double x) {
                    double total = 0;
                    double last = -inf;
                    int topStack = 0;

                    int sortByTopIter = 0;
                    int sortByBotIter = 0;

                    while (sortByTopIter < n && sortByBotIter < n) {
                        if (!sortByTop[sortByTopIter].intersect(x)) {
                            sortByTopIter++;
                            continue;
                        }
                        if (!sortByBot[sortByBotIter].intersect(x)) {
                            sortByBotIter++;
                            continue;
                        }
                        double yt = sortByTop[sortByTopIter].intersectTopAt(x);
                        double yb = sortByBot[sortByBotIter].intersectBotAt(x);
                        double top = Math.max(yt, yb);
                        if (topStack > 0) {
                            total += Math.max(0, last - top);
                        }
                        if (yt > yb) {
                            topStack++;
                            sortByTopIter++;
                        } else {
                            topStack--;
                            sortByBotIter++;
                        }
                        last = top;
                    }

                    while (sortByBotIter < n) {
                        if (!sortByBot[sortByBotIter].intersect(x)) {
                            sortByBotIter++;
                            continue;
                        }
                        double yt = sortByBot[sortByBotIter].intersectBotAt(x);
                        if (topStack > 0) {
                            total += Math.max(0, last - yt);
                        }
                        last = yt;
                        topStack--;
                        sortByBotIter++;
                    }

//                    debug.debug("x", x);
//                    debug.debug("len", total);
                    return total;
                }
            };
            SimpsonRule simpsonRule = new SimpsonRule(1e-5, function);

            TreeSet<Integer> pts = new TreeSet<>();
            for (Triangle t : ts) {
                pts.add(t.x);
                pts.add(t.x + t.a);
            }
            Integer[] xs = pts.toArray(new Integer[0]);
            double ans = 0;
            for (int i = 1; i < xs.length; i++) {
                ans += simpsonRule.integral(xs[i - 1], xs[i]);
            }

            Double[] maybe = new Double[]{(double) (int) ans, (double) (int) ans + 0.5, (double) (int) ans + 1};
            double finalAns = ans;
            Arrays.sort(maybe, (a, b) -> Double.compare(Math.abs(a - finalAns), Math.abs(b - finalAns)));
            io.cache.append(String.format("%.1f", maybe[0]));
        }


    }

    public static class Triangle {
        int x;
        int y;
        int a;

        public int top() {
            return y + a + x;
        }

        public int bottom() {
            return y;
        }

        public boolean intersect(double p) {
            return x <= p && x + a >= p;
        }

        public double intersectTopAt(double p) {
            return y + a - (p - x);
        }

        public double intersectBotAt(double p) {
            return y;
        }
    }

    public static class SimpsonRule {
        private final double eps;
        private Function function;

        public SimpsonRule(double eps, Function function) {
            this.eps = eps;
            this.function = function;
        }

        public static interface Function {
            double y(double x);
        }


        private double simpson(double l, double r) {
            return (r - l) / 6 * (function.y(l) + 4 * function.y((l + r) / 2) + function.y(r));
        }

        private double integral(double l, double r, double totalArea) {
            double m = (l + r) / 2;
            double lArea = simpson(l, m);
            double rArea = simpson(m, r);
            if (Math.abs(lArea + rArea - totalArea) <= 15 * eps) {
                return lArea + rArea + (lArea + rArea - totalArea) / 15;
            }
            return integral(l, m, lArea) + integral(m, r, rArea);
        }

        public double integral(double l, double r) {
            return integral(l, r, simpson(l, r));
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
