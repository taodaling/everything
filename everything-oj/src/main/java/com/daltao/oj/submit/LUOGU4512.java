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

public class LUOGU4512 {
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

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt() + 1;
            int m = io.readInt() + 1;
            int ceilLog = 32 - Integer.numberOfLeadingZeros(Math.max(n, m) - 1) + 1;
            int len = 1 << ceilLog;
            int[] a = new int[len];
            int[] b = new int[len];
            int[] c = new int[len];
            int[] r = new int[len];
            int[] rev = new int[len];

            for (int i = 0; i < n; i++) {
                a[i] = io.readInt();
            }
            for (int j = 0; j < m; j++) {
                b[j] = io.readInt();
            }
            NumberTheoryTransform.prepareReverse(rev, ceilLog);
            NumberTheoryTransform.divide(rev, a, b, c, r, ceilLog);
            for (int i = 0; i <= n - m; i++) {
                io.cache.append(c[i]).append(' ');
            }
            io.cache.append('\n');
            for (int i = 0; i < m - 1; i++) {
                io.cache.append(r[i]).append(' ');
            }
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

            public int subtract(int x, int y) {
                return valueOf(x - y);
            }

            public int subtract(long x, long y) {
                return valueOf(x - y);
            }

            @Override
            public String toString() {
                return "mod " + m;
            }
        }

        /**
         * Bit operations
         */
        public static class BitOperator {
            public int bitAt(int x, int i) {
                return (x >> i) & 1;
            }

            public int bitAt(long x, int i) {
                return (int) ((x >> i) & 1);
            }

            public int setBit(int x, int i, boolean v) {
                if (v) {
                    x |= 1 << i;
                } else {
                    x &= ~(1 << i);
                }
                return x;
            }

            public long setBit(long x, int i, boolean v) {
                if (v) {
                    x |= 1L << i;
                } else {
                    x &= ~(1L << i);
                }
                return x;
            }

            /**
             * Determine whether x is subset of y
             */
            public boolean subset(long x, long y) {
                return intersect(x, y) == x;
            }

            /**
             * Merge two set
             */
            public long merge(long x, long y) {
                return x | y;
            }

            public long intersect(long x, long y) {
                return x & y;
            }

            public long differ(long x, long y) {
                return x - intersect(x, y);
            }
        }

        /**
         * Power operations
         */
        public static class Power {
            public Modular getModular() {
                return modular;
            }

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

    public static class NumberTheoryTransform {
        private static final NumberTheory.Modular MODULAR = new NumberTheory.Modular(998244353);
        private static final NumberTheory.Power POWER = new NumberTheory.Power(MODULAR);
        private static final int G = 3;
        private static int[] wCache = new int[23];
        private static int[] invCache = new int[23];

        static {
            for (int i = 0, until = wCache.length; i < until; i++) {
                int s = 1 << i;
                wCache[i] = POWER.pow(G, (MODULAR.m - 1) / 2 / s);
                invCache[i] = POWER.inverse(s);
            }
        }

        public static void dotMul(int[] a, int[] b, int[] c, int m) {
            for (int i = 0, n = 1 << m; i < n; i++) {
                c[i] = MODULAR.mul(a[i], b[i]);
            }
        }

        public static void prepareReverse(int[] r, int b) {
            int n = 1 << b;
            r[0] = 0;
            for (int i = 1; i < n; i++) {
                r[i] = (r[i >> 1] >> 1) | ((1 & i) << (b - 1));
            }
        }

        private static void dft(int[] r, int[] p, int m) {
            int n = 1 << m;

            for (int i = 0; i < n; i++) {
                if (r[i] > i) {
                    int tmp = p[i];
                    p[i] = p[r[i]];
                    p[r[i]] = tmp;
                }
            }

            int w = 0;
            int t = 0;
            for (int d = 0; d < m; d++) {
                int w1 = wCache[d];
                int s = 1 << d;
                int s2 = s << 1;
                for (int i = 0; i < n; i += s2) {
                    w = 1;
                    for (int j = 0; j < s; j++) {
                        int a = i + j;
                        int b = a + s;
                        t = MODULAR.mul(w, p[b]);
                        p[b] = MODULAR.plus(p[a], -t);
                        p[a] = MODULAR.plus(p[a], t);
                        w = MODULAR.mul(w, w1);
                    }
                }
            }
        }

        private static void idft(int[] r, int[] p, int m) {
            dft(r, p, m);

            int n = 1 << m;
            int invN = invCache[m];

            p[0] = MODULAR.mul(p[0], invN);
            p[n / 2] = MODULAR.mul(p[n / 2], invN);
            for (int i = 1, until = n / 2; i < until; i++) {
                int a = p[n - i];
                p[n - i] = MODULAR.mul(p[i], invN);
                p[i] = MODULAR.mul(a, invN);
            }
        }

        public static void reverse(int[] p, int l, int r) {
            while (l < r) {
                int tmp = p[l];
                p[l] = p[r];
                p[r] = tmp;
                l++;
                r--;
            }
        }

        public static int rankOf(int[] p) {
            for (int i = p.length - 1; i >= 0; i--) {
                if (p[i] > 0) {
                    return i;
                }
            }
            return 0;
        }

        /**
         * calc a = b * c + remainder
         */
        public static void divide(int[] r, int[] a, int[] b, int[] c, int[] remainder, int m) {
            int rankA = rankOf(a);
            int rankB = rankOf(b);
            reverse(a, 0, rankA);
            reverse(b, 0, rankB);
            inverse(r, b, c, remainder, m - 1);
            dft(r, a, m);
            dft(r, c, m);
            dotMul(a, c, c, m);
            idft(r, a, m);
            idft(r, c, m);
            reverse(a, 0, rankA);
            reverse(b, 0, rankB);
            for (int i = rankA - rankB + 1; i < c.length; i++) {
                c[i] = 0;
            }
            reverse(c, 0, rankA - rankB);

            dft(r, a, m);
            dft(r, b, m);
            dft(r, c, m);
            for (int i = 0; i < remainder.length; i++) {
                remainder[i] = MODULAR.subtract(a[i], MODULAR.mul(b[i], c[i]));
            }
            idft(r, a, m);
            idft(r, b, m);
            idft(r, c, m);
            idft(r, remainder, m);
        }

        /**
         * return polynomial g while p * g = 1 (mod x^m).
         * <br>
         * You are supposed to guarantee the lengths of all arrays are greater than or equal to 2^{ceil(log2(m)) + 1}
         */
        private static void inverse(int[] r, int[] p, int[] inv, int[] buf, int m) {
            if (m == 0) {
                inv[0] = POWER.inverse(p[0]);
                return;
            }
            inverse(r, p, inv, buf, m - 1);
            int n = 1 << (m + 1);
            System.arraycopy(p, 0, buf, 0, 1 << m);
            Arrays.fill(buf, 1 << m, 1 << (m + 1), 0);
            prepareReverse(r, (m + 1));
            dft(r, buf, (m + 1));
            dft(r, inv, (m + 1));
            for (int i = 0; i < n; i++) {
                inv[i] = MODULAR.mul(inv[i], 2 - MODULAR.mul(buf[i], inv[i]));
            }
            idft(r, inv, m + 1);
            for (int i = 1 << m; i < n; i++) {
                inv[i] = 0;
            }
        }
    }

}
