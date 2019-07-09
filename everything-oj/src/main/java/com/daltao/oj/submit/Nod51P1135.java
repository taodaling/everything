package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Nod51P1135 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io);

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
        int inf = (int) 1e8;

        public Task(FastIO io) {
            this.io = io;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();
            if (n == 2) {
                io.cache.append(1);
                return;
            }
            MathUtils.Modular modular = new MathUtils.Modular(n);
            MathUtils.Power power = new MathUtils.Power(modular);
            Map<Integer, Integer> factors = new MathUtils.PollardRho()
                    .findAllFactors(n - 1);
            int[] allFactors = factors.keySet().stream()
                    .mapToInt(Integer::intValue).toArray();
            for (int i = 2; ; i++) {
                boolean valid = true;
                for (int f : allFactors) {
                    if (power.pow(i, (n - 1) / f) == 1) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    io.cache.append(i);
                    return;
                }
            }
        }
    }

    public static class MathUtils {
        private static Random random = new Random();

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
                    int x = random.nextInt(n - 2) + 2;
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
            Random random = new Random(123456789);

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
}
