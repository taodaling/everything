package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BZOJ1100 {
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
            int t = io.readInt();
            while (t-- > 0)
                solve();
        }

        int limit = 100000;
        Corner[] l2r = new Corner[limit * 2];
        Corner[] r2l = new Corner[limit * 2];
        Point[] points = new Point[limit];
        Hash l2rHash1 = new Hash(limit * 2, 31);
//        Hash l2rHash2 = new Hash(limit * 2, 11);
        Hash r2lHash1 = new Hash(limit * 2, 31);
//        Hash r2lHash2 = new Hash(limit * 2, 11);
        Hash r2lHash3 = new Hash(limit * 2, 71);
        Hash l2rHash3 = new Hash(limit * 2, 71);

        {
            for (int i = 0; i < limit; i++) {
                l2r[i] = new Corner();
                r2l[i] = new Corner();
                l2r[i + limit] = new Corner();
                r2l[i + limit] = new Corner();
                points[i] = new Point();
            }
        }

        /**
         * 计算两个向量的叉乘
         */
        public static long cross(long x1, long y1, long x2, long y2) {
            return x1 * y2 - y1 * x2;
        }

        public int high(long x) {
            return (int) (x >>> 32);
        }

        public int low(long x) {
            return (int) x;
        }

        public int hashCode(long a, long b, long c, int x, NumberTheory.Modular mod) {
            int ans = 0;
            ans = mod.plus(ans, high(a));
            ans = mod.mul(ans, x);
            ans = mod.plus(ans, low(a));
            ans = mod.mul(ans, x);
            ans = mod.plus(ans, high(b));
            ans = mod.mul(ans, x);
            ans = mod.plus(ans, low(b));
            ans = mod.mul(ans, x);
            ans = mod.plus(ans, high(c));
            ans = mod.mul(ans, x);
            ans = mod.plus(ans, low(c));
            return ans;
        }

        public void solve() {
            int n = io.readInt();
            for (int i = 0; i < n; i++) {
                points[i].x = io.readInt();
                points[i].y = io.readInt();
            }
            for (int i = 0; i < n; i++) {
                Point prev = points[(i + n - 1) % n];
                Point next = points[(i + n + 1) % n];
                Point now = points[i];
                long a = dist2(prev, now);
                long b = dist2(now, next);
                long area = area2(prev, next, now);
                l2r[i + n].h1 = l2r[i].h1 = hashCode(a, b, area, l2rHash1.x, Hash.MOD);
//                l2r[i + n].h2 = l2r[i].h2 = hashCode(a, b, area, l2rHash2.x, Hash.MOD);
                l2r[i + n].h3 = l2r[i].h3 = hashCode(a, b, area, l2rHash3.x, Hash.MOD);
                r2l[n - i - 1].h1 = r2l[n + n - i - 1].h1 = hashCode(b, a, area, l2rHash1.x, Hash.MOD);
//                r2l[n - i - 1].h2 = r2l[n + n - i - 1].h2 = hashCode(b, a, area, l2rHash2.x, Hash.MOD);
                r2l[n - i - 1].h3 = r2l[n + n - i - 1].h3 = hashCode(b, a, area, l2rHash3.x, Hash.MOD);
            }

            l2rHash1.populate(l2r, n + n, Corner.toHash1);
//            l2rHash2.populate(l2r, n + n, Corner.toHash2);
            r2lHash1.populate(r2l, n + n, Corner.toHash1);
//            r2lHash2.populate(r2l, n + n, Corner.toHash2);
            r2lHash3.populate(r2l, n + n, Corner.toHash3);
            l2rHash3.populate(l2r, n + n, Corner.toHash3);

            //by edges
            int ans = 0;
            if (n % 2 == 1) {
                for (int i = 0, j = n; i < n; i++, j--) {
                    int ir = i + n - 1;
                    int jr = j + n - 1;

                    if (l2rHash1.partial(i, ir) == r2lHash1.partial(j, jr) &&
//                            l2rHash2.partial(i, ir) == r2lHash2.partial(j, jr) &&
                            l2rHash3.partial(i, ir) == r2lHash3.partial(j, jr)) {
                        ans++;
//                        debug.debug("edge from", i);
//                        debug.debug("edge to", ir);
                    }
                }
            }

            if (n % 2 == 0) {
                for (int i = 0, j = n; i < n / 2; i++, j--) {
                    int ir = i + n - 1;
                    int jr = j + n - 1;

                    if (l2rHash1.partial(i, ir) == r2lHash1.partial(j, jr) &&
//                            l2rHash2.partial(i, ir) == r2lHash2.partial(j, jr) &&
                            l2rHash3.partial(i, ir) == r2lHash3.partial(j, jr)) {
                        ans++;
//                        debug.debug("edge from", i);
//                        debug.debug("edge to", ir);
                    }
                }
                for (int i = 0, j = n - 1; i < n / 2; i++, j--) {
                    int ir = i + n;
                    int jr = j + n;

                    if (l2rHash1.partial(i, ir) == r2lHash1.partial(j, jr) &&
//                            l2rHash2.partial(i, ir) == r2lHash2.partial(j, jr) &&
                            l2rHash3.partial(i, ir) == r2lHash3.partial(j, jr)) {
                        ans++;
//                        debug.debug("point ", i);
                    }
                }
            }

            io.cache.append(ans).append('\n');
        }

        public long dist2(Point a, Point b) {
            long dx = a.x - b.x;
            long dy = a.y - b.y;
            return dx * dx + dy * dy;
        }

        public long area2(Point a, Point b, Point c) {
            return cross(a.x - c.x, a.y - c.y, b.x - c.x, b.y - c.y);
        }
    }

    public static class Point {
        long x;
        long y;
    }

    public static class Corner {
        int h1;
        int h2;
        int h3;

        public static Hash.ToHash<Corner> toHash1 = new Hash.ToHash<Corner>() {
            @Override
            public int hash(Corner obj) {
                return obj.h1;
            }
        };

        public static Hash.ToHash<Corner> toHash2 = new Hash.ToHash<Corner>() {
            @Override
            public int hash(Corner obj) {
                return obj.h2;
            }
        };

        public static Hash.ToHash<Corner> toHash3 = new Hash.ToHash<Corner>() {
            @Override
            public int hash(Corner obj) {
                return obj.h3;
            }
        };
    }

    public static class NumberTheory {
        private static final Random RANDOM = new Random();

        /**
         * Mod operations
         */
        public static class Modular {
            int m;

            public Modular(int m) {
                this.m = m;
            }

            public int valueOf(int x) {
                x %= m;
                if (x < 0) {
                    x += m;
                }
                return x;
            }

            public int valueOf(long x) {
                x %= m;
                if (x < 0) {
                    x += m;
                }
                return (int) x;
            }

            public int mul(int x, int y) {
                return valueOf((long) x * y);
            }

            public int mul(long x, long y) {
                x = valueOf(x);
                y = valueOf(y);
                return valueOf(x * y);
            }

            public int plus(int x, int y) {
                return valueOf(x + y);
            }

            public int plus(long x, long y) {
                x = valueOf(x);
                y = valueOf(y);
                return valueOf(x + y);
            }

            @Override
            public String toString() {
                return "mod " + m;
            }
        }

        /**
         * Power operations
         */
        public static class Power {
            final Modular modular;

            public Power(Modular modular) {
                this.modular = modular;
            }

            public int pow(int x, long n) {
                if (n == 0) {
                    return 1;
                }
                long r = pow(x, n >> 1);
                r = modular.valueOf(r * r);
                if ((n & 1) == 1) {
                    r = modular.valueOf(r * x);
                }
                return (int) r;
            }

            public int inverse(int x) {
                return pow(x, modular.m - 2);
            }

            public int pow2(int x) {
                return x * x;
            }

            public long pow2(long x) {
                return x * x;
            }

            public double pow2(double x) {
                return x * x;
            }
        }
    }


    public static class Hash {
        private static final NumberTheory.Modular MOD = new NumberTheory.Modular((int) (1e9 + 7));
        private int[] inverse;
        private int[] hash;
        private int n;
        private int x;
        private int invX;

        public static interface ToHash<T> {
            int hash(T obj);
        }

        public Hash(int size, int x) {
            inverse = new int[size];
            hash = new int[size];
            this.x = x;
            this.invX = new NumberTheory.Power(MOD).inverse(x);
            inverse[0] = 1;
            for (int i = 1; i < size; i++) {
                this.inverse[i] = MOD.mul(this.inverse[i - 1], invX);
            }
        }

        public <T> void populate(T[] data, int n, ToHash<T> toHash) {
            this.n = n;
            hash[0] = toHash.hash(data[0]);
            int xn = 1;
            for (int i = 1; i < n; i++) {
                xn = MOD.mul(xn, x);
                hash[i] = MOD.plus(hash[i - 1], MOD.mul(toHash.hash(data[i]), xn));
            }
        }

        public void populate(Object[] data, int n) {
            this.n = n;
            hash[0] = data[0].hashCode();
            int xn = 1;
            for (int i = 1; i < n; i++) {
                xn = MOD.mul(xn, x);
                hash[i] = MOD.plus(hash[i - 1], MOD.mul(data[i].hashCode(), xn));
            }
        }

        public void populate(int[] data, int n) {
            this.n = n;
            hash[0] = data[0];
            int xn = 1;
            for (int i = 1; i < n; i++) {
                xn = MOD.mul(xn, x);
                hash[i] = MOD.plus(hash[i - 1], MOD.mul(data[i], xn));
            }
        }

        public void populate(char[] data, int n) {
            this.n = n;
            hash[0] = data[0];
            int xn = 1;
            for (int i = 1; i < n; i++) {
                xn = MOD.mul(xn, x);
                hash[i] = MOD.plus(hash[i - 1], MOD.mul(data[i], xn));
            }
        }

        public int partial(int l, int r) {
            int h = hash[r];
            if (l > 0) {
                h = MOD.plus(h, -hash[l - 1]);
                h = MOD.mul(h, inverse[l]);
            }
            return h;
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
