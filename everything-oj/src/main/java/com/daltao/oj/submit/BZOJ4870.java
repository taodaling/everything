package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class BZOJ4870 {
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
//            int p = (int) 7;
//            bruteForce(1, p, 7, 0);
//            for (int n = 1; ; n++) {
//                for (int k = 1; k <= 50; k++) {
//                    for (int r = 0; r < k; r++) {
//                        int exp = bruteForce(n, p, k, r);
//                        int ans = solve(n, p, k, r);
//                        debug.debug("n", n);
//                        debug.debug("k", k);
//                        debug.debug("r", r);
//                        debug.debug("exp", exp);
//                        debug.debug("ans", ans);
//                        debug.assertTrue(exp == ans);
//                    }
//                }
//            }

            solve();
        }

        NumberTheory.Modular mod;
        int k;
        Remainder once;


        public int bruteForce(int n, int p, int k, int r) {
            NumberTheory.Modular mod = new NumberTheory.Modular(p);
            NumberTheory.Composite composite = new NumberTheory.Composite(n * k, mod);
            int sum = 0;
            for (int i = 0; i <= n; i++) {
                sum = mod.plus(sum, composite.composite(n * k, i * k + r));
            }
            return sum;
        }

        public void solve() {
            int n = io.readInt();
            int p = io.readInt();
            int k = io.readInt();
            int r = io.readInt();
            io.cache.append(solve(n, p, k, r));
        }


        int[][] comp = new int[51][51];

        public int comp(int m, int n) {
            if (comp[m][n] == -1) {
                if (n > m) {
                    comp[m][n] = 0;
                } else if (n == 0) {
                    comp[m][n] = 1;
                } else {
                    comp[m][n] = mod.plus(comp(m - 1, n), comp(m - 1, n - 1));
                }
            }
            return comp[m][n];
        }


        public int solve(int n, int p, int k, int r) {
            this.k = k;
            mod = new NumberTheory.Modular(p);

            once = new Remainder(k);
            for (int[] c : comp) {
                Arrays.fill(c, -1);
            }

            for (int i = 1; i < k; i++) {
                once.cnts[i] = comp(k, i);
            }
            once.cnts[0] = mod.valueOf(2);
            return (pow(n).cnts[r]);
        }

        public Remainder pow(int n) {
            if (n == 0) {
                Remainder ans = new Remainder(k);
                ans.cnts[0] = mod.valueOf(1);
                return ans;
            }
            Remainder r = pow(n / 2);
            r = Remainder.mul(r, r, k, mod);
            if (n % 2 == 1) {
                r = Remainder.mul(r, once, k, mod);
            }
            return r;
        }
    }

    public static class Remainder {
        int[] cnts;

        public Remainder(int k) {
            cnts = new int[k];
        }

        public static Remainder mul(Remainder a, Remainder b, int k, NumberTheory.Modular mod) {
            Remainder c = new Remainder(k);
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < k; j++) {
                    c.cnts[(i + j) % k] = mod.plus(c.cnts[(i + j) % k], mod.mul(a.cnts[i], b.cnts[j]));
                }
            }
            return c;
        }

        @Override
        public String toString() {
            return Arrays.toString(cnts);
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
         * Euler sieve for filter primes
         */
        public static class EulerSieve {
            int[] primes;
            boolean[] isComp;
            int primeLength;

            public EulerSieve(int limit) {
                isComp = new boolean[limit + 1];
                primes = new int[limit + 1];
                primeLength = 0;
                for (int i = 2; i <= limit; i++) {
                    if (!isComp[i]) {
                        primes[primeLength++] = i;
                    }
                    for (int j = 0, until = limit / i; j < primeLength && primes[j] <= until; j++) {
                        int pi = primes[j] * i;
                        isComp[pi] = true;
                        if (i % primes[j] == 0) {
                            break;
                        }
                    }
                }
            }
        }

        /**
         * Euler sieve for multiplicative function
         */
        public static class MultiplicativeFunctionSieve {
            int[] primes;
            boolean[] isComp;
            int primeLength;
            int[] mobius;
            int[] euler;
            int[] factors;
            int[] smallestPrimeFactor;
            int[] numberOfSmallestPrimeFactor;

            public MultiplicativeFunctionSieve(int limit, boolean enableMobius, boolean enableEuler, boolean enableFactors) {
                isComp = new boolean[limit + 1];
                primes = new int[limit + 1];
                numberOfSmallestPrimeFactor = new int[limit + 1];
                smallestPrimeFactor = new int[limit + 1];
                primeLength = 0;
                for (int i = 2; i <= limit; i++) {
                    if (!isComp[i]) {
                        primes[primeLength++] = i;
                        numberOfSmallestPrimeFactor[i] = smallestPrimeFactor[i] = i;
                    }
                    for (int j = 0, until = limit / i; j < primeLength && primes[j] <= until; j++) {
                        int pi = primes[j] * i;
                        smallestPrimeFactor[pi] = primes[j];
                        numberOfSmallestPrimeFactor[pi] = smallestPrimeFactor[i] == primes[j]
                                ? (numberOfSmallestPrimeFactor[i] * numberOfSmallestPrimeFactor[primes[j]])
                                : numberOfSmallestPrimeFactor[primes[j]];
                        isComp[pi] = true;
                        if (i % primes[j] == 0) {
                            break;
                        }
                    }
                }

                if (enableMobius) {
                    mobius = new int[limit + 1];
                    mobius[1] = 1;
                    for (int i = 2; i <= limit; i++) {
                        if (!isComp[i]) {
                            mobius[i] = -1;
                        } else {
                            if (numberOfSmallestPrimeFactor[i] != smallestPrimeFactor[i]) {
                                mobius[i] = 0;
                            } else {
                                mobius[i] = mobius[numberOfSmallestPrimeFactor[i]] *
                                        mobius[i / numberOfSmallestPrimeFactor[i]];
                            }
                        }
                    }
                }

                if (enableEuler) {
                    euler = new int[limit + 1];
                    euler[1] = 1;
                    for (int i = 2; i <= limit; i++) {
                        if (!isComp[i]) {
                            euler[i] = i - 1;
                        } else {
                            if (numberOfSmallestPrimeFactor[i] == i) {
                                euler[i] = i - i / smallestPrimeFactor[i];
                            } else {
                                euler[i] = euler[numberOfSmallestPrimeFactor[i]] *
                                        euler[i / numberOfSmallestPrimeFactor[i]];
                            }
                        }
                    }
                }

                if (enableFactors) {
                    factors = new int[limit + 1];
                    factors[1] = 1;
                    for (int i = 2; i <= limit; i++) {
                        if (!isComp[i]) {
                            factors[i] = 2;
                        } else {
                            if (numberOfSmallestPrimeFactor[i] == i) {
                                factors[i] = 1 + factors[i / smallestPrimeFactor[i]];
                            } else {
                                factors[i] = factors[numberOfSmallestPrimeFactor[i]] *
                                        factors[i / numberOfSmallestPrimeFactor[i]];
                            }
                        }
                    }
                }
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
         * Find all inverse number
         */
        public static class InverseNumber {
            int[] inv;

            public InverseNumber(int[] inv, int limit, Modular modular) {
                this.inv = inv;
                inv[1] = 1;
                int p = modular.m;
                for (int i = 2; i <= limit; i++) {
                    int k = p / i;
                    int r = p % i;
                    inv[i] = modular.mul(-k, inv[r]);
                }
            }

            public InverseNumber(int limit, Modular modular) {
                this(new int[limit + 1], limit, modular);
            }
        }

        /**
         * Factorial
         */
        public static class Factorial {
            int[] fact;
            int[] inv;

            public Factorial(int[] fact, int[] inv, InverseNumber in, int limit, Modular modular) {
                this.fact = fact;
                this.inv = inv;
                fact[0] = inv[0] = 1;
                for (int i = 1; i <= limit; i++) {
                    fact[i] = modular.mul(fact[i - 1], i);
                    inv[i] = modular.mul(inv[i - 1], in.inv[i]);
                }
            }

            public Factorial(int limit, Modular modular) {
                this(new int[limit + 1], new int[limit + 1], new InverseNumber(limit, modular), limit, modular);
            }
        }

        /**
         * Composition
         */
        public static class Composite {
            final Factorial factorial;
            final Modular modular;

            public Composite(Factorial factorial, Modular modular) {
                this.factorial = factorial;
                this.modular = modular;
            }

            public Composite(int limit, Modular modular) {
                this(new Factorial(limit, modular), modular);
            }

            public int composite(int m, int n) {
                if (n > m) {
                    return 0;
                }
                return modular.mul(modular.mul(factorial.fact[m], factorial.inv[n]), factorial.inv[m - n]);
            }
        }

        public static class ModExpression {
            ExtGCD extGCD = new ExtGCD();
            Modular modular = new Modular(1);

            /**
             * Find ka=b(mod c) where k is the minimum possible non negative integer.
             * <br>
             * If it's impossible, -1 will be returned.
             */
            public long solve(long a, long b, int c) {
                modular.m = c;
                a = modular.valueOf(a);
                b = modular.valueOf(b);
                int g = (int) extGCD.extgcd((int) a, c);
                if (b % g != 0) {
                    return -1;
                }
                modular.m = c / g;
                return modular.valueOf(b / g * extGCD.getX());
            }
        }

        /**
         * \sum_{i=1}^{limit}f(\lfloor n/i \rfloor)
         */
        public static class FloorDivisionOptimizer {
            int l;
            int r;
            int n;
            int limit;


            public FloorDivisionOptimizer(int n, int l, int limit) {
                this.n = n;
                this.l = 0;
                this.limit = limit;
                this.r = l - 1;
            }

            public boolean hasNext() {
                return r < limit;
            }

            public int next() {
                l = r + 1;
                r = n / (n / l);
                return n / l;
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
}
