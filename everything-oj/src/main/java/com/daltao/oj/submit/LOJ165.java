package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class LOJ165 {
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
            MathUtils.Modular modular = new MathUtils.Modular(998244353);
            ModLagrangeInterpolation lagrangeInterpolation = new ModLagrangeInterpolation(modular);
            int n = io.readInt();
            for (int i = 0; i < n; i++) {
                int t = io.readInt();
                if (t == 1) {
                    int x = io.readInt();
                    int y = io.readInt();
                    lagrangeInterpolation.addPoint(x, y);
                } else {
                    io.cache.append(lagrangeInterpolation.interpolate(io.readInt())).append('\n');
                }
            }
        }
    }


    public static class MathUtils {

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
    }

    public static class ModLagrangeInterpolation {
        private Map<Integer, Integer> points = new LinkedHashMap<>();
        private MathUtils.Modular modular;
        private MathUtils.Power power;

        public ModLagrangeInterpolation(MathUtils.Modular modular) {
            this.modular = modular;
            this.power = new MathUtils.Power(modular);
        }


        public void addPoint(int x, int y) {
            x = modular.valueOf(x);
            y = modular.valueOf(y);
            if (points.containsKey(x)) {
                int val = points.get(x);
                if (val != y) {
                    throw new IllegalStateException();
                }
                return;
            }
            points.put(x, y);
        }

        public int interpolate(int x) {
            int sum = 0;
            for (Map.Entry<Integer, Integer> point : points.entrySet()) {
                int up = point.getValue();
                int bottom = 1;
                for (Map.Entry<Integer, Integer> otherPoint : points.entrySet()) {
                    if (otherPoint.getKey().intValue() == point.getKey()) {
                        continue;
                    }
                    up = modular.mul(up, modular.plus(x, -otherPoint.getKey()));
                    bottom = modular.mul(bottom, modular.plus(point.getKey(), -otherPoint.getKey()));
                }
                int addition = modular.mul(up, power.inverse(bottom));
                sum = modular.plus(sum, addition);
            }
            return sum;
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
