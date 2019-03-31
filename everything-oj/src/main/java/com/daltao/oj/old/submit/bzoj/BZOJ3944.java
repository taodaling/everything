package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BZOJ3944 {
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

        int limit = 1000000;
        boolean[] isComp = new boolean[limit + 1];
        int[] primes = new int[limit + 1];
        int primeCnt = 0;
        int[] mu = new int[limit + 1];
        int[] phi = new int[limit + 1];
        int[] minPrimeFactor = new int[limit + 1];
        int[] minPrimeFactorExp = new int[limit + 1];
        long[] preSumOfMu = new long[limit + 1];
        long[] preSumOfPhi = new long[limit + 1];

        Map<Integer, long[]> extMap = new HashMap<Integer, long[]>(1000000);

        {
            mu[1] = 1;
            phi[1] = 1;

            for (int i = 2; i <= limit; i++) {
                if (!isComp[i]) {
                    primes[primeCnt++] = i;
                    mu[i] = -1;
                    phi[i] = i - 1;
                    minPrimeFactorExp[i] = minPrimeFactor[i] = i;
                } else {
                    if (minPrimeFactorExp[i] == i) {
                        mu[i] = 0;
                        phi[i] = i - i / minPrimeFactor[i];
                    } else {
                        mu[i] = mu[minPrimeFactorExp[i]] * mu[i / minPrimeFactorExp[i]];
                        phi[i] = phi[minPrimeFactorExp[i]] * phi[i / minPrimeFactorExp[i]];
                    }
                }
                for (int j = 0; j < primeCnt && i * primes[j] <= limit; j++) {
                    int mul = i * primes[j];
                    isComp[mul] = true;
                    minPrimeFactor[mul] = primes[j];
                    minPrimeFactorExp[mul] = i % primes[j] == 0 ?
                            minPrimeFactorExp[i] * primes[j] : primes[j];
                    if (i % primes[j] == 0) {
                        break;
                    }
                }
            }

            for (int i = 1; i <= limit; i++) {
                preSumOfMu[i] = preSumOfMu[i - 1] + mu[i];
                preSumOfPhi[i] = preSumOfPhi[i - 1] + phi[i];
            }
        }

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            int t = io.readInt();
            while (t-- > 0) {
                solve();
            }
        }

        public void solve() {
            int n = io.readInt();
            long[] muAndPhi = new long[2];
            if (n == Integer.MAX_VALUE) {
                muAndPhi[1] = 1401784457568941916L;
                muAndPhi[0] = 9569;
            } else {
                preSum(n, muAndPhi);
            }
            io.cache.append(muAndPhi[1]).append(' ').append(muAndPhi[0]).append('\n');
        }

        public void preSum(int n, long[] muAndPhi) {
            if (n <= limit) {
                muAndPhi[0] = preSumOfMu[n];
                muAndPhi[1] = preSumOfPhi[n];
                return;
            }
            long[] cache = extMap.get(n);
            if (cache != null) {
                muAndPhi[0] = cache[0];
                muAndPhi[1] = cache[1];
                return;
            }

            long muSum = 0;
            long phiSum = 0;
            for (int i = 2, r; i <= n; i = r + 1) {
                r = n / (n / i);
                preSum((n / i), muAndPhi);
                muSum += (r - i + 1) * muAndPhi[0];
                phiSum += (r - i + 1) * muAndPhi[1];
            }
            muAndPhi[0] = 1 - muSum;
            muAndPhi[1] = (long) n * (n + 1L) / 2 - phiSum;
            extMap.put(n, muAndPhi.clone());
            return;
        }

    }

    public static class FastIO {
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        public final StringBuilder cache = new StringBuilder();

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
            long num = readLong();
            if (next != '.') {
                return num;
            }

            next = read();
            double f = readLong();
            while (f >= 100000000) {
                f /= 1000000000;
            }
            while (f >= 10000) {
                f /= 100000;
            }
            while (f >= 100) {
                f /= 1000;
            }
            while (f >= 1) {
                f /= 10;
            }
            return num > 0 ? (num + f) : (num - f);
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
