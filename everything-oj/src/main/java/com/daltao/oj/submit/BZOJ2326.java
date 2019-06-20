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

public class BZOJ2326 {
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
            long n = io.readLong();
            int m = io.readInt();
            Modular modular = new Modular(m);
            boolean over = n == (long) 1e18;
            if (over) {
                n--;
            }


            Helper helper = new Helper(n);
            ModMatrix vector = new ModMatrix(3, 1);
            vector.mat[0][0] = 0;
            vector.mat[1][0] = 1;
            vector.mat[2][0] = 1;
            while (helper.hasNext()) {
                helper.next();
                ModMatrix t = new ModMatrix(new int[][]{
                        {modular.valueOf(helper.pow), 1, 0},
                        {0, 1, 1},
                        {0, 0, 1}
                });

                vector = ModMatrix.mul(ModMatrix.pow(t, helper.max - helper.min + 1, modular), vector, modular);
            }

            int ans = vector.mat[0][0];
            if (over) {
                ans = modular.mul(ans, (long) 1e18);
                ans = modular.mul(ans, 10);
                ans = modular.plus(ans, (long) 1e18);
            }

            io.cache.append(ans);
        }
    }

    public static class Helper {
        long min;
        long max;
        int digit;
        long n;
        long pow;

        public Helper(long n) {
            min = max = 0;
            digit = 0;
            pow = 1;
            this.n = n;
        }

        public boolean hasNext() {
            return max < n;
        }

        public void next() {
            min = max + 1;
            max = Math.min(min * 10 - 1, n);
            digit += 1;
            pow *= 10;
        }
    }


    public static class ModMatrix {
        int[][] mat;
        int n;
        int m;

        public ModMatrix(ModMatrix model) {
            n = model.n;
            m = model.m;
            mat = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = model.mat[i][j];
                }
            }
        }

        public ModMatrix(int n, int m) {
            this.n = n;
            this.m = m;
            mat = new int[n][m];
        }

        public ModMatrix(int[][] mat) {
            this.n = mat.length;
            this.m = mat[0].length;
            this.mat = mat;
        }

        public void fill(int v) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = v;
                }
            }
        }

        public void asStandard() {
            fill(0);
            for (int i = 0; i < n && i < m; i++) {
                mat[i][i] = 1;
            }
        }

        public static ModMatrix mul(ModMatrix a, ModMatrix b, Modular modular) {
            ModMatrix c = new ModMatrix(a.n, b.m);
            for (int i = 0; i < c.n; i++) {
                for (int j = 0; j < c.m; j++) {
                    for (int k = 0; k < a.m; k++) {
                        c.mat[i][j] = modular.plus(c.mat[i][j], modular.mul(a.mat[i][k], b.mat[k][j]));
                    }
                }
            }
            return c;
        }

        public static ModMatrix pow(ModMatrix x, long n, Modular modular) {
            if (n == 0) {
                ModMatrix r = new ModMatrix(x.n, x.m);
                r.asStandard();
                return r;
            }
            ModMatrix r = pow(x, n >> 1, modular);
            r = ModMatrix.mul(r, r, modular);
            if (n % 2 == 1) {
                r = ModMatrix.mul(r, x, modular);
            }
            return r;
        }

        static ModMatrix transposition(ModMatrix x, Modular modular) {
            int n = x.n;
            int m = x.m;
            ModMatrix t = new ModMatrix(m, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    t.mat[j][i] = x.mat[i][j];
                }
            }
            return t;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    builder.append(mat[i][j]).append(' ');
                }
                builder.append('\n');
            }
            return builder.toString();
        }
    }


    /**
     * 模运算
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
