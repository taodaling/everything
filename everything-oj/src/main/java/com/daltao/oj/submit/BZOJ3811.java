package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ3811 {
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
        LinearBasis linearBasis = new LinearBasis();
        SubsetGenerator subsetGenerator = new SubsetGenerator();
        BigDecimal[] pow2cache = new BigDecimal[65];

        {
            pow2cache[0] = BigDecimal.ONE;
            pow2cache[1] = BigDecimal.valueOf(2);
            for (int i = 2; i < 64; i++) {
                pow2cache[i] = pow2cache[i - 1].multiply(pow2cache[1]);
            }
        }

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
            for (int i = 0; i < n; i++) {
                linearBasis.add(io.readLong());
            }
            BigDecimal result;
            if (k == 1) {
                result = solve1();
            } else if (k == 2) {
                result = solve2();
            } else {
                result = solveK(k);
            }


            if (result.remainder(pow2cache[1]).equals(BigDecimal.ONE)) {
                io.cache.append(result.divide(pow2cache[1]).setScale(1).toPlainString());
            } else {
                io.cache.append(result.divide(pow2cache[1]).setScale(0).toPlainString());
            }
        }

        public long or() {
            long or = 0;
            for (int i = 0; i < 64; i++) {
                or = or | linearBasis.map[i];
            }
            return or;
        }

        public long bitAt(long val, int i) {
            return (val >> i) & 1;
        }

        public BigDecimal solve1() {
            BigDecimal sum = BigDecimal.ZERO;
            long or = or();
            for (int i = 0; i < 64; i++) {
                if (bitAt(or, i) == 0) {
                    continue;
                }
                sum = sum.add(pow2cache[i]);
            }
            return sum;
        }

        public BigDecimal solve2() {
            BigDecimal sum = BigDecimal.ZERO;
            long or = or();
            for (int i = 0; i <= 32; i++) {
                for (int j = 0; j <= 32; j++) {
                    if (bitAt(or, i) == 0 || bitAt(or, j) == 0) {
                        continue;
                    }
                    boolean independent = false;
                    for (int k = 0; k < 64; k++) {
                        independent = independent || bitAt(linearBasis.map[k], i) != bitAt(linearBasis.map[k], j);
                    }
                    if (independent) {
                        //1/4
                        sum = sum.add(pow2cache[i + j - 1]);
                    } else {
                        sum = sum.add(pow2cache[i + j]);
                    }
                }
            }
            return sum;
        }

        public BigDecimal solveK(int k) {
            BigDecimal sum = BigDecimal.ZERO;
            int size = 0;
            for (int i = 0; i < 64; i++) {
                if (linearBasis.map[i] == 0) {
                    continue;
                }
                subsetGenerator.meanings[size++] = linearBasis.map[i];
            }
            subsetGenerator.setSet(size);
            while (subsetGenerator.hasNext()) {
                sum = sum.add(BigDecimal.valueOf(subsetGenerator.next()).pow(k));
            }
            return sum.divide(pow2cache[size - 1]);
        }
    }

    public static class SubsetGenerator {
        private long[] meanings = new long[33];
        private int[] bits = new int[33];
        private int remain;
        private long next;

        public void setSet(int size) {
            int bitCount = 0;
            Arrays.fill(bits, 0, 0, size);
            remain = 1 << size;
            next = 0;
        }

        public boolean hasNext() {
            return remain > 0;
        }

        private void consume() {
            remain = remain - 1;
            int i;
            for (i = 0; bits[i] == 1; i++) {
                bits[i] = 0;
                next ^= meanings[i];
            }
            bits[i] = 1;
            next ^= meanings[i];
        }

        public long next() {
            long returned = next;
            consume();
            return returned;
        }
    }

    public static class LinearBasis {
        private long[] map = new long[64];
        private int size;

        public int size() {
            return size;
        }

        public void clear() {
            size = 0;
            Arrays.fill(map, 0);
        }

        private void afterAddBit(int bit) {
            for (int i = 63; i >= 0; i--) {
                if (i == bit || map[i] == 0) {
                    continue;
                }
                if (bitAt(map[i], bit) == 1) {
                    map[i] ^= map[bit];
                }
            }
        }

        public boolean add(long val) {
            for (int i = 63; i >= 0 && val != 0; i--) {
                if (bitAt(val, i) == 0) {
                    continue;
                }
                val ^= map[i];
            }
            if (val != 0) {
                int log = 63 - Long.numberOfLeadingZeros(val);
                map[log] = val;
                size++;
                afterAddBit(log);
                return true;
            }
            return false;
        }

        private long bitAt(long val, int i) {
            return (val >>> i) & 1;
        }

        /**
         * Find the k-th smallest possible generated number, and we consider 0 is the 0-th smallest.
         */
        public long theKthSmallestNumber(long k) {
            int id = 0;
            long num = 0;
            for (int i = 0; i < 64; i++) {
                if (map[i] == 0) {
                    continue;
                }
                if (bitAt(k, id) == 1) {
                    num ^= map[i];
                }
                id++;
            }
            return num;
        }

        /**
         * The rank of n in all generated numbers, 0's rank is 0
         */
        long theRankOfNumber(long n) {
            int index = size - 1;
            long rank = 0;
            for (int i = 63; i >= 0; i--) {
                if (map[i] == 0) {
                    continue;
                }
                if (bitAt(n, i) == 1) {
                    rank |= 1L << index;
                    n ^= map[i];
                }
                index--;
            }
            return rank;
        }

        /**
         * Find the maximun value x ^ v where v is generated
         */
        long theMaximumNumberXor(long x) {
            for (int i = 0; i < 64; i++) {
                if (map[i] == 0) {
                    continue;
                }
                if (bitAt(x, i) == 0) {
                    x ^= map[i];
                }
            }
            return x;
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
