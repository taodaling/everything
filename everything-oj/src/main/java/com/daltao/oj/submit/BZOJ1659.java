package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ1659 {
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
            int k = io.readInt();
            char[] signs = new char[n];
            int[] lenCnts = new int[n + 1];
            io.readString(signs, 0);

            Hash[] hashes = new Hash[2];
            hashes[0] = new Hash(n, 31);
            hashes[0].populate(signs, n);
            hashes[1] = new Hash(n, 67);
            hashes[1].populate(signs, n);

            for (int l = 0, r = n - 1; l < r; l++, r--) {
                char tmp = signs[l];
                signs[l] = signs[r];
                signs[r] = tmp;
            }

            Hash[] rev = new Hash[2];
            rev[0] = new Hash(n, 31);
            rev[0].populate(signs, n);
            rev[1] = new Hash(n, 67);
            rev[1].populate(signs, n);

            for (int i = 0; i < n; i++) {
                int l = 1;
                int r = Math.min(i + 1, n - i);
                while (l < r) {
                    int m = (l + r + 1) >> 1;
                    int f = i - m + 1;
                    int t = i + m - 1;
                    int rf = n - 1 - f;
                    int rt = n - 1 - t;
                    if (hashes[0].partial(f, t) == rev[0].partial(rt, rf)
                            && hashes[1].partial(f, t) == rev[1].partial(rt, rf)) {
                        l = m;
                    } else {
                        r = m - 1;
                    }
                }
                lenCnts[l * 2 - 1]++;
            }

            NumberTheory.Modular mod = new NumberTheory.Modular(19930726);
            NumberTheory.Power power = new NumberTheory.Power(mod);
            for (int i = n - 1; i >= 0; i--) {
                lenCnts[i] = mod.plus(lenCnts[i], lenCnts[i + 1]);
            }

            int remain = k;
            int prod = 1;
            for (int i = n; i >= 0 && remain > 0; i--) {
                if (i % 2 == 0 || lenCnts[i] == 0) {
                    continue;
                }
                int alloc = Math.min(remain, lenCnts[i]);
                remain -= alloc;
                prod = mod.mul(prod, power.pow(i, alloc));
            }

            if (remain > 0) {
                io.cache.append(-1);
                return;
            }

            io.cache.append(prod);
        }
    }

    public static class NumberTheory {
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
