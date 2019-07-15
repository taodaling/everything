package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


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


        NumberTheory.Modular mod = new NumberTheory.Modular(0);
        NumberTheory.QuadraticResidue qr = new NumberTheory.QuadraticResidue(mod);

        public void solve() {
            mod.m = io.readInt();
            io.cache.append(qr.minPrimitiveRoot()).append('\n');
        }
    }

    public static class NumberTheory {
        private static final Random RANDOM = new Random();


        /**
         * Extend gcd
         */
        public static class ExtGCD {
            private long x;
            private long y;
            private long g;

            public long getX() {
                return x;
            }

            public long getY() {
                return y;
            }

            /**
             * Get g = Gcd(a, b) and find a way to set x and y to match ax+by=g
             */
            public long extgcd(long a, long b) {
                if (a >= b) {
                    g = extgcd0(a, b);
                } else {
                    g = extgcd0(b, a);
                    long tmp = x;
                    x = y;
                    y = tmp;
                }
                return g;
            }


            private long extgcd0(long a, long b) {
                if (b == 0) {
                    x = 1;
                    y = 0;
                    return a;
                }
                long g = extgcd0(b, a % b);
                long n = x;
                long m = y;
                x = m;
                y = n - m * (a / b);
                return g;
            }
        }

        public static class Gcd {
            public long gcd(long a, long b) {
                return a >= b ? gcd0(a, b) : gcd0(b, a);
            }

            private long gcd0(long a, long b) {
                return b == 0 ? a : gcd0(b, a % b);
            }

            public int gcd(int a, int b) {
                return a >= b ? gcd0(a, b) : gcd0(b, a);
            }

            private int gcd0(int a, int b) {
                return b == 0 ? a : gcd0(b, a % b);
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
         * Log operations
         */
        public static class Log2 {
            public int ceilLog(int x) {
                return 32 - Integer.numberOfLeadingZeros(x - 1);
            }

            public int floorLog(int x) {
                return 31 - Integer.numberOfLeadingZeros(x);
            }

            public int ceilLog(long x) {
                return 64 - Long.numberOfLeadingZeros(x - 1);
            }

            public int floorLog(long x) {
                return 63 - Long.numberOfLeadingZeros(x);
            }
        }

        /**
         * Test whether a number is primes
         */
        public static class MillerRabin {
            Modular modular;
            Power power;

            /**
             * Check whether n is a prime s times
             */
            public boolean mr(int n, int s) {
                if (n == 2) {
                    return true;
                }
                if (n % 2 == 0) {
                    return false;
                }
                modular = new Modular(n);
                power = new Power(modular);
                for (int i = 0; i < s; i++) {
                    int x = RANDOM.nextInt(n - 2) + 2;
                    if (!mr0(x, n)) {
                        return false;
                    }
                }
                return true;
            }

            private boolean mr0(int x, int n) {
                int exp = n - 1;
                while (true) {
                    int y = power.pow(x, exp);
                    if (y != 1 && y != n - 1) {
                        return false;
                    }
                    if (y != 1 || exp % 2 == 1) {
                        break;
                    }
                    exp = exp / 2;
                }
                return true;
            }
        }

        /**
         * Find all factors of a number
         */
        public static class PollardRho {
            MillerRabin mr = new MillerRabin();
            Gcd gcd = new Gcd();
            Random random = new Random();

            public int findFactor(int n) {
                if (mr.mr(n, 10)) {
                    return n;
                }
                while (true) {
                    int f = findFactor0(random.nextInt(n), random.nextInt(n), n);
                    if (f != -1) {
                        return f;
                    }
                }
            }

            /**
             * Find all prime factor of n
             * <br>
             * p1 => p1^c1
             * <br>
             * ...
             * <br>
             * pk => pk^ck
             */
            public Map<Integer, Integer> findAllFactors(int n) {
                Map<Integer, Integer> map = new HashMap();
                findAllFactors(map, n);
                return map;
            }

            private void findAllFactors(Map<Integer, Integer> map, int n) {
                if (n == 1) {
                    return;
                }
                int f = findFactor(n);
                if (f == n) {
                    Integer value = map.get(f);
                    if (value == null) {
                        value = 1;
                    }
                    map.put(f, value * f);
                    return;
                }
                findAllFactors(map, f);
                findAllFactors(map, n / f);
            }

            private int findFactor0(int x, int c, int n) {
                int xi = x;
                int xj = x;
                int j = 2;
                int i = 1;
                while (i < n) {
                    i++;
                    xi = (int) ((long) xi * xi + c) % n;
                    int g = gcd.gcd(n, Math.abs(xi - xj));
                    if (g != 1 && g != n) {
                        return g;
                    }
                    if (i == j) {
                        j = j << 1;
                        xj = xi;
                    }
                }
                return -1;
            }
        }

        public static class QuadraticResidue {
            final Modular modular;
            final BitOperator bitOperator = new BitOperator();
            Power power;
            final PollardRho rho = new PollardRho();


            public QuadraticResidue(Modular modular) {
                this.modular = modular;
                power = new Power(modular);
            }

            /**
             * return \sqrt{n} or -1 if it doesn't exist
             */
            public int square(int n) {
                n = modular.valueOf(n);
                if (n == 0) {
                    return 0;
                }
                int p = modular.m;
                if (power.pow(n, (p - 1) / 2) != 1) {
                    return -1;
                }
                while (true) {
                    int a = RANDOM.nextInt(p);
                    int w = modular.plus(modular.mul(a, a), -n);
                    if (power.pow(w, (p - 1) / 2) == 1) {
                        continue;
                    }


                    int pow = (p + 1) / 2;
                    int i = 31 - Integer.numberOfLeadingZeros(pow);
                    int real = 1;
                    int img = 0;
                    for (; i >= 0; i--) {
                        int nReal = modular.plus(modular.mul(real, real),
                                modular.mul(modular.mul(img, img), w));
                        int nImg = modular.mul(modular.mul(real, img), 2);
                        real = nReal;
                        img = nImg;
                        if (bitOperator.bitAt(pow, i) == 1) {
                            nReal = modular.plus(modular.mul(real, a), modular.mul(img, w));
                            nImg = modular.plus(modular.mul(img, a), real);
                            real = nReal;
                            img = nImg;
                        }
                    }

                    return real;
                }
            }

            public int minPrimitiveRoot() {
                if (modular.m == 2) {
                    return 1;
                }
                Map<Integer, Integer> factorMap = rho.findAllFactors(modular.m - 1);
                int[] factors = factorMap.keySet().stream().mapToInt(Integer::intValue).toArray();
                for (int i = 2; ; i++) {
                    boolean valid = true;
                    for (int factor : factors) {
                        if (power.pow(i, (modular.m - 1) / factor) == 1) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        return i;
                    }
                }
            }
        }
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