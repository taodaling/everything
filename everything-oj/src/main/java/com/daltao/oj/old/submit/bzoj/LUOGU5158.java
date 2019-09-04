package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LUOGU5158 {
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
            int n = io.readInt();
            GravityModLagrangeInterpolation interpolation = new GravityModLagrangeInterpolation(new NumberTheory.Modular(998244353), n);
            for (int i = 0; i < n; i++) {
                interpolation.addPoint(io.readInt(), io.readInt());
            }
            GravityModLagrangeInterpolation.Polynomial p = interpolation.preparePolynomial();
            p.setN(n);
            for (int i = 0; i < n; i++) {
                io.cache.append(p.getCoefficient(i)).append(' ');
            }
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

    public static class GravityModLagrangeInterpolation {
        private NumberTheory.Power power;
        private NumberTheory.Modular modular;

        public GravityModLagrangeInterpolation(NumberTheory.Modular modular, int expect) {
            this(new NumberTheory.Power(modular), expect);
        }

        public GravityModLagrangeInterpolation(NumberTheory.Power power, int expect) {
            this.modular = power.getModular();
            this.power = power;
            xs = new Polynomial(expect);
            ys = new Polynomial(expect);
            lx = new Polynomial(expect);
            lxBuf = new Polynomial(expect);
            invW = new Polynomial(expect);
            lx.setN(1);
            lx.coes[0] = 1;
        }

        /**
         * O(n)
         */
        public void addPoint(int x, int y) {
            x = modular.valueOf(x);
            y = modular.valueOf(y);
            Integer exist = points.get(x);
            if (exist != null) {
                if (exist != y) {
                    throw new RuntimeException();
                }
                return;
            }
            points.put(x, y);

            xs.setN(n + 1);
            xs.coes[n] = x;
            ys.setN(n + 1);
            ys.coes[n] = y;
            lx.multiply(modular.valueOf(-x), lxBuf);
            switchBuf();
            invW.setN(n + 1);
            invW.coes[n] = 1;
            for (int i = 0; i < n; i++) {
                invW.coes[i] = modular.mul(invW.coes[i], modular.subtract(xs.coes[i], x));
                invW.coes[n] = modular.mul(invW.coes[n], modular.subtract(x, xs.coes[i]));
            }
            n++;
        }

        /**
         * O(n)
         */
        public int getYByInterpolation(int x) {
            x = modular.valueOf(x);
            if (points.containsKey(x)) {
                return points.get(x);
            }

            int y = lx.function(x);
            int sum = 0;
            for (int i = 0; i < n; i++) {
                int val = modular.mul(invW.coes[i], modular.subtract(x, xs.coes[i]));
                val = modular.mul(power.inverse(val), ys.coes[i]);
                sum = modular.plus(sum, val);
            }

            return modular.mul(y, sum);
        }

        /**
         * O(n^2)
         */
        public Polynomial preparePolynomial() {
            Polynomial ans = new Polynomial(n);
            Polynomial ansBuf = new Polynomial(n);
            for (int i = 0; i < n; i++) {
                int c = modular.mul(ys.coes[i], power.inverse(invW.coes[i]));
                lx.div(modular.valueOf(-xs.coes[i]), ansBuf);
                ansBuf.mulConstant(c, ansBuf);
                ans.plus(ansBuf, ans);
            }
            return ans;
        }

        private void switchBuf() {
            Polynomial tmp = lx;
            lx = lxBuf;
            lxBuf = tmp;
        }


        Map<Integer, Integer> points = new HashMap<>();
        Polynomial xs;
        Polynomial ys;
        Polynomial lx;
        Polynomial lxBuf;
        Polynomial invW;
        int n;


        public class Polynomial {
            private int[] coes;
            private int n;

            public int getCoefficient(int i) {
                return coes[i];
            }

            public int getRank() {
                return n - 1;
            }

            public int function(int x) {
                int ans = 0;
                int xi = 1;
                for (int i = 0; i < n; i++) {
                    ans = modular.plus(ans, modular.mul(xi, coes[i]));
                    xi = modular.mul(xi, x);
                }
                return ans;
            }

            public Polynomial(int n) {
                this.n = 0;
                coes = new int[n];
            }

            public void ensureLength() {
                if (coes.length >= n) {
                    return;
                }
                int proper = coes.length;
                while (proper < n) {
                    proper *= 2;
                }
                coes = Arrays.copyOf(coes, proper);
            }

            public void setN(int n) {
                this.n = n;
                ensureLength();
            }

            public void clear() {
                Arrays.fill(coes, 0, n, 0);
            }

            /**
             * this * (x + b) => ans
             */
            public void multiply(int b, Polynomial ans) {
                ans.setN(n + 1);
                for (int i = 0; i < n; i++) {
                    ans.coes[i] = modular.mul(coes[i], b);
                }
                ans.coes[n] = 0;
                for (int i = 0; i < n; i++) {
                    ans.coes[i + 1] = modular.plus(ans.coes[i + 1], coes[i]);
                }
            }

            /**
             * this * b => ans
             */
            public void mulConstant(int b, Polynomial ans) {
                ans.setN(n);
                for (int i = 0; i < n; i++) {
                    ans.coes[i] = modular.mul(coes[i], b);
                }
            }

            /**
             * this + a => ans
             */
            public void plus(Polynomial a, Polynomial ans) {
                ans.setN(Math.max(n, a.n));
                for (int i = 0; i < n; i++) {
                    ans.coes[i] = coes[i];
                }
                for (int i = 0; i < a.n; i++) {
                    ans.coes[i] = modular.plus(ans.coes[i], a.coes[i]);
                }
            }

            /**
             * this / (x + b) => ans
             */
            public void div(int b, Polynomial ans) {
                ans.setN(n - 1);
                int affect = 0;
                for (int i = n - 1; i >= 1; i--) {
                    affect = modular.plus(affect, coes[i]);
                    ans.coes[i - 1] = affect;
                    affect = modular.mul(-affect, b);
                }
            }

            @Override
            public String toString() {
                return Arrays.toString(Arrays.copyOfRange(coes, 0, n));
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
