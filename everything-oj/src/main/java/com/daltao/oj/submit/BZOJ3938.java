package com.daltao.oj.submit;

import com.daltao.template.Randomized;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class BZOJ3938 {
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
            int m = io.readInt();
            Robot[] robots = new Robot[n + 1];

            for (int i = 1; i <= n; i++) {
                robots[i] = new Robot();
                robots[i].a = 0;
                robots[i].b = io.readInt();
            }

            int[][] query = new int[4][m];
            int[] times = new int[m + 1];
            int wpos = 1;
            char[] cmd = new char[20];
            for (int i = 0; i < m; i++) {
                int time = io.readInt();
                io.readString(cmd, 0);
                if (cmd[0] == 'q') {
                    query[0][i] = 0;
                    query[1][i] = time;
                } else {
                    query[0][i] = 1;
                    query[1][i] = time;
                    query[2][i] = io.readInt();
                    query[3][i] = io.readInt();
                }
                times[wpos++] = time;
            }

            DiscreteMap map = new DiscreteMap(times, 0, wpos);
            Segment pos = Segment.build(map.minRank(), map.maxRank());
            Segment neg = Segment.build(map.minRank(), map.maxRank());
            for (int i = 0; i < m; i++) {
                if (query[0][i] == 1) {
                    int time = query[1][i];
                    int id = query[2][i];
                    int speed = query[3][i];
                    Segment.update(robots[id].time, map.rankOf(time) - 1, map.minRank(), map.maxRank(), new Segment.Line(robots[id].a, robots[id].b), map, pos);
                    Segment.update(robots[id].time, map.rankOf(time) - 1, map.minRank(), map.maxRank(), new Segment.Line(-robots[id].a, -robots[id].b), map, pos);
                }
            }
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
            return f;
        }

        public int maxRank() {
            return t - 1;
        }

        public int rankOf(int x) {
            return Arrays.binarySearch(val, f, t, x);
        }
    }

    public static class Segment implements Cloneable {
        Segment left;
        Segment right;
        Line line;

        public static class Line {
            // y = ax + b
            double a;
            double b;

            public Line(double a, double b) {
                this.a = a;
                this.b = b;
            }

            public double y(double x) {
                return a * x + b;
            }

            //a1x+b1=a2x+b2=>(a1-a2)x=b2-b1=>x=(b2-b1)/(a1-a2)
            public static double intersectAt(Line a, Line b) {
                return (b.b - a.b) / (a.a - b.a);
            }

            @Override
            public String toString() {
                return a + "x+" + b;
            }
        }

        public static Segment build(int l, int r) {
            Segment segment = new Segment();
            int m = (l + r) >> 1;
            if (l != r) {
                segment.left = build(l, m);
                segment.right = build(m + 1, r);
            }
            return segment;
        }

        public static boolean checkOutOfRange(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public static boolean checkCoverage(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        public static void update(int ll, int rr, int l, int r, Line line, DiscreteMap map, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return;
            }
            int m = (l + r) >> 1;
            if (checkCoverage(ll, rr, l, r)) {
                if (segment.line == null) {
                    segment.line = line;
                    return;
                }
                Line largerA, smallerA;
                if (line.a < segment.line.a) {
                    largerA = segment.line;
                    smallerA = line;
                } else {
                    largerA = line;
                    smallerA = segment.line;
                }
                if (Math.abs(smallerA.a - largerA.a) < 1e-10) {
                    if (smallerA.b >= largerA.b) {
                        segment.line = smallerA;
                    } else {
                        segment.line = largerA;
                    }
                    return;
                }
                double x = Line.intersectAt(smallerA, largerA);
                if (x <= map.val[l]) {
                    segment.line = largerA;
                    return;
                }
                if (x >= map.val[r]) {
                    segment.line = smallerA;
                    return;
                }
                if (x <= map.val[m]) {
                    segment.line = largerA;
                    update(ll, rr, l, m, smallerA, map, segment.left);
                } else {
                    segment.line = smallerA;
                    update(ll, rr, m + 1, r, largerA, map, segment.right);
                }
                return;
            }
            update(ll, rr, l, m, line, map, segment.left);
            update(ll, rr, m + 1, r, line, map, segment.right);
        }

        public static Segment updatePersistently(int ll, int rr, int l, int r, Line line, DiscreteMap map, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return segment;
            }
            segment = segment.clone();
            int m = (l + r) >> 1;
            if (checkCoverage(ll, rr, l, r)) {
                if (segment.line == null) {
                    segment.line = line;
                    return segment;
                }
                Line largerA, smallerA;
                if (line.a < segment.line.a) {
                    largerA = segment.line;
                    smallerA = line;
                } else {
                    largerA = line;
                    smallerA = segment.line;
                }
                if (Math.abs(smallerA.a - largerA.a) < 1e-10) {
                    if (smallerA.b >= largerA.b) {
                        segment.line = smallerA;
                    } else {
                        segment.line = largerA;
                    }
                    return segment;
                }
                double x = Line.intersectAt(smallerA, largerA);
                if (x <= map.val[l]) {
                    segment.line = largerA;
                    return segment;
                }
                if (x >= map.val[r]) {
                    segment.line = smallerA;
                    return segment;
                }
                if (x <= map.val[m]) {
                    segment.line = largerA;
                    update(ll, rr, l, m, smallerA, map, segment.left);
                } else {
                    segment.line = smallerA;
                    update(ll, rr, m + 1, r, largerA, map, segment.right);
                }
                return segment;
            }
            update(ll, rr, l, m, line, map, segment.left);
            update(ll, rr, m + 1, r, line, map, segment.right);
            return segment;
        }

        public static double query(int x, int l, int r, DiscreteMap map, Segment segment) {
            if (segment.line == null) {
                return Double.MIN_VALUE;
            }
            if (checkOutOfRange(x, x, l, r)) {
                return Double.MIN_VALUE;
            }
            if (checkCoverage(x, x, l, r)) {
                return segment.line.y(map.val[x]);
            }
            int m = (l + r) >> 1;
            return Math.max(Math.max(
                    query(x, l, m, map, segment.left),
                    query(x, m + 1, r, map, segment.right)), segment.line.y(x));
        }


        @Override
        public Segment clone() {
            try {
                return (Segment) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Robot {
        long a;
        long b;
        int time;
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
