package com.daltao.oj.submit;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;


public class CFContest {
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
        long lInf = (long) 1e18;

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
            int w = io.readInt();

            Segment segment = new Segment(1, w);
            TreeSet<Interval> set = new TreeSet<Interval>(Interval.sortByL);
            for (int i = 0; i < n; i++) {
                int li = io.readInt();
                set.clear();
                for (int j = 1; j <= li; j++) {
                    int val = io.readInt();
                    Interval floatArea = new Interval();
                    floatArea.l = j;
                    floatArea.r = w + j - li;
                    floatArea.max = val;

                    addInterval(set, floatArea);
                }

                if (li < w) {
                    Interval prefix = new Interval();
                    prefix.l = 1;
                    prefix.r = w - li;
                    prefix.max = 0;

                    Interval suffix = new Interval();
                    suffix.l = li + 1;
                    suffix.r = w;
                    suffix.max = 0;

                    addInterval(set, prefix);
                    addInterval(set, suffix);
                }

                for (Interval interval : set) {
                    segment.update(interval.l, interval.r, 1, w, interval.max);
                }
            }

            segment.query(1, w, 1, w, io);
        }

        public void splitAndAdd(TreeSet<Interval> set, Interval which, Interval middle) {
            set.remove(which);
            if (which.r <= middle.r) {
                which.r = middle.l - 1;
                if (which.valid()) {
                    set.add(which);
                }
            } else if (which.l >= middle.l) {
                which.l = middle.r + 1;
                if (which.valid()) {
                    set.add(which);
                }
            } else {
                Interval l = new Interval();
                l.l = which.l;
                l.r = middle.l - 1;
                l.max = which.max;

                Interval r = which;
                r.l = middle.r + 1;

                if (l.valid()) {
                    set.add(l);
                }
                if (r.valid()) {
                    set.add(r);
                }
            }
        }

        public void addInterval(TreeSet<Interval> set, Interval interval) {
            while (!set.isEmpty() && interval.valid()) {
                Interval floor = set.floor(interval);
                if (floor == null) {
                    break;
                }
                if (floor.r < interval.l) {
                    break;
                }
                if (floor.max >= interval.max) {
                    interval.l = floor.r + 1;
                } else {
                    splitAndAdd(set, floor, interval);
                    break;
                }
            }

            while (!set.isEmpty() && interval.valid()) {
                Interval ceil = set.ceiling(interval);
                if (ceil == null) {
                    break;
                }
                if (ceil.l > interval.r) {
                    break;
                }
                if (ceil.max >= interval.max) {
                    interval.r = ceil.l - 1;
                    break;
                } else {
                    splitAndAdd(set, ceil, interval);
                }
            }

            if (interval.valid()) {
                set.add(interval);
            }
        }
    }

    public static class Segment implements Cloneable {
        private Segment left;
        private Segment right;
        private long val;
        private long plus;

        public void setPlus(long p) {
            plus += p;
            val += p;
        }


        public void pushUp() {
        }

        public void pushDown() {
            if (plus != 0) {
                left.setPlus(plus);
                right.setPlus(plus);
                plus = 0;
            }
        }

        public Segment(int l, int r) {
            if (l < r) {
                int m = (l + r) >> 1;
                left = new Segment(l, m);
                right = new Segment(m + 1, r);
                pushUp();
            } else {

            }
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void update(int ll, int rr, int l, int r, long p) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setPlus(p);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.update(ll, rr, l, m, p);
            right.update(ll, rr, m + 1, r, p);
            pushUp();
        }

        public void query(int ll, int rr, int l, int r, FastIO  io) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (l == r) {
                io.cache.append(val).append(' ');
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.query(ll, rr, l, m, io);
            right.query(ll, rr, m + 1, r, io);
        }
    }


    public static class Interval {
        int l;
        int r;
        int max;

        public boolean valid() {
            return r >= l;
        }

        public static Comparator<Interval> sortByL = (a, b) -> a.l - b.l;
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(20 << 20);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        private byte[] buf = new byte[1 << 20];
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