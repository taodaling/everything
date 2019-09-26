package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class LUOGU1707 {
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
        long lInf = (long) 1e18;
        double dInf = 1e50;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int d1 = 1;
            int d2 = 3;
            long n = io.readLong();
            long k = io.readLong();
            long p = io.readLong();
            long q = io.readLong();
            long r = io.readLong();
            long t = io.readLong();
            long u = io.readLong();
            long v = io.readLong();
            long w = io.readLong();
            long x = io.readLong();
            long y = io.readLong();
            long z = io.readLong();

            long[][] mat = new long[11][11];
            mat[0][0] = 1;
            mat[1][1] = z;
            mat[2][2] = w;
            mat[3][0] = 1;
            mat[3][3] = 1;
            mat[4][0] = 1;
            mat[4][3] = 2;
            mat[4][4] = 1;
            mat[5][6] = 1;
            mat[6][0] = 2;
            mat[6][1] = 1;
            mat[6][3] = 1;
            mat[6][5] = y;
            mat[6][6] = x;
            mat[6][8] = 1;
            mat[6][10] = 1;
            mat[7][8] = 1;
            mat[8][2] = 1;
            mat[8][6] = 1;
            mat[8][7] = v;
            mat[8][8] = u;
            mat[8][10] = 1;
            mat[9][10] = 1;
            mat[10][0] = 1;
            mat[10][3] = t;
            mat[10][4] = r;
            mat[10][6] = 1;
            mat[10][8] = 1;
            mat[10][9] = q;
            mat[10][10] = p;

            LongModMatrix single = new LongModMatrix(mat);
            NumberTheory.LongModular mod = new NumberTheory.LongModular(k);
            LongModMatrix transform = LongModMatrix.pow(single, n - 1, mod);

            long[][] vec = new long[11][1];
            vec[0][0] = 1;
            vec[1][0] = z;
            vec[2][0] = w;
            vec[3][0] = 1;
            vec[4][0] = 1;
            vec[5][0] = d1;
            vec[6][0] = d2;
            vec[7][0] = d1;
            vec[8][0] = d2;
            vec[9][0] = d1;
            vec[10][0] = d2;
            LongModMatrix init = new LongModMatrix(vec);

            LongModMatrix ans = LongModMatrix.mul(transform, init, mod);
            io.cache.append("nodgd ").append(ans.mat[9][0]).append('\n');
            io.cache.append("Ciocio ").append(ans.mat[7][0]).append('\n');
            io.cache.append("Nicole ").append(ans.mat[5][0]);
        }
    }

    public static class NumberTheory {
        private static final Random RANDOM = new Random();

        /**
         * Modular operation for long version
         */
        public static class LongModular {
            final long m;

            public LongModular(long m) {
                this.m = m;
            }

            public long mul(long a, long b) {
                return b == 0 ? 0 : ((mul(a, b >> 1) << 1) % m + a * (b & 1)) % m;
            }

            public long plus(long a, long b) {
                return valueOf(a + b);
            }

            public long subtract(long a, long b) {
                return valueOf(a - b);
            }

            public long valueOf(long a) {
                a %= m;
                if (a < 0) {
                    a += m;
                }
                return a;
            }
        }

        public static class LongPower {
            public LongModular getModular() {
                return modular;
            }

            final LongModular modular;

            public LongPower(LongModular modular) {
                this.modular = modular;
            }

            long pow(long x, long n) {
                if (n == 0) {
                    return 1;
                }
                long r = pow(x, n >> 1);
                r = modular.mul(r, r);
                if ((n & 1) == 1) {
                    r = modular.mul(r, x);
                }
                return r;
            }

            long inverse(long x) {
                return pow(x, modular.m - 2);
            }
        }

    }

    public static class LongModMatrix {
        long[][] mat;
        int n;
        int m;

        public LongModMatrix(LongModMatrix model) {
            n = model.n;
            m = model.m;
            mat = new long[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = model.mat[i][j];
                }
            }
        }

        public LongModMatrix(int n, int m) {
            this.n = n;
            this.m = m;
            mat = new long[n][m];
        }

        public LongModMatrix(long[][] mat) {
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

        public void set(int i, int j, int val) {
            mat[i][j] = val;
        }

        public void normalize(NumberTheory.LongModular mod) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = mod.valueOf(mat[i][j]);
                }
            }
        }


        public static LongModMatrix region(LongModMatrix x, int b, int t, int l, int r) {
            LongModMatrix y = new LongModMatrix(t - b + 1, r - l + 1);
            for (int i = b; i <= t; i++) {
                for (int j = l; j <= r; j++) {
                    y.mat[i - b][j - l] = x.mat[i][j];
                }
            }
            return y;
        }

        /**
         * |x| while mod a non-zero number
         */
        public static long determinant(LongModMatrix x, NumberTheory.LongModular modular) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            int n = x.n;
            LongModMatrix l = new LongModMatrix(x);
            l.normalize(modular);
            long ans = 1;
            for (int i = 0; i < n; i++) {
                int maxRow = i;
                for (int j = i + 1; j < n; j++) {
                    if (modular.valueOf(l.mat[j][i]) == 0) {
                        continue;
                    }
                    if (l.mat[i][i] == 0 || l.mat[i][i] > l.mat[j][i]) {
                        l.swapRow(i, j);
                        ans = -ans;
                    }
                    l.subtractRow(j, i, l.mat[j][i] / l.mat[i][i], modular);
                    j--;
                }

                if (l.mat[i][i] == 0) {
                    return 0;
                }
                ans = modular.mul(ans, l.mat[i][i]);
            }

            return ans;
        }

        /**
         * |x| while mod prime
         */
        public static long determinant(LongModMatrix x, NumberTheory.LongPower power) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            NumberTheory.LongModular modular = power.getModular();
            int n = x.n;
            LongModMatrix l = new LongModMatrix(x);
            l.normalize(modular);
            long ans = 1;
            for (int i = 0; i < n; i++) {
                int maxRow = i;
                for (int j = i; j < n; j++) {
                    if (modular.valueOf(l.mat[j][i]) != 0) {
                        maxRow = j;
                        break;
                    }
                }

                if (l.mat[maxRow][i] == 0) {
                    return 0;
                }
                if (i != maxRow) {
                    l.swapRow(i, maxRow);
                    ans = -ans;
                }
                ans = modular.mul(ans, l.mat[i][i]);
                l.mulRow(i, power.inverse(l.mat[i][i]), modular);

                for (int j = i + 1; j < n; j++) {
                    if (j == i) {
                        continue;
                    }
                    if (l.mat[j][i] == 0) {
                        continue;
                    }
                    long f = l.mat[j][i];
                    l.subtractRow(j, i, f, modular);
                }
            }

            return ans;
        }

        public static LongModMatrix inverse(LongModMatrix x, NumberTheory.LongPower power) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            NumberTheory.LongModular modular = power.getModular();
            int n = x.n;
            LongModMatrix l = new LongModMatrix(x);
            l.normalize(modular);
            LongModMatrix r = new LongModMatrix(n, n);
            r.asStandard();
            for (int i = 0; i < n; i++) {
                int maxRow = i;
                for (int j = i; j < n; j++) {
                    if (modular.valueOf(l.mat[j][i]) != 0) {
                        maxRow = j;
                        break;
                    }
                }

                if (l.mat[maxRow][i] == 0) {
                    throw new RuntimeException("Can't inverse current matrix");
                }
                r.swapRow(i, maxRow);
                l.swapRow(i, maxRow);

                long inv = power.inverse(l.mat[i][i]);
                r.mulRow(i, inv, modular);
                l.mulRow(i, inv, modular);

                for (int j = 0; j < n; j++) {
                    if (j == i) {
                        continue;
                    }
                    if (l.mat[j][i] == 0) {
                        continue;
                    }
                    long f = l.mat[j][i];
                    r.subtractRow(j, i, f, modular);
                    l.subtractRow(j, i, f, modular);
                }
            }
            return r;
        }

        void swapRow(int i, int j) {
            long[] row = mat[i];
            mat[i] = mat[j];
            mat[j] = row;
        }

        void subtractRow(int i, int j, long f, NumberTheory.LongModular modular) {
            for (int k = 0; k < m; k++) {
                mat[i][k] = modular.subtract(mat[i][k], modular.mul(mat[j][k], f));
            }
        }

        void mulRow(int i, long f, NumberTheory.LongModular modular) {
            for (int k = 0; k < m; k++) {
                mat[i][k] = modular.mul(mat[i][k], f);
            }
        }

        public static LongModMatrix mul(LongModMatrix a, LongModMatrix b, NumberTheory.LongModular modular) {
            LongModMatrix c = new LongModMatrix(a.n, b.m);
            for (int i = 0; i < c.n; i++) {
                for (int j = 0; j < c.m; j++) {
                    for (int k = 0; k < a.m; k++) {
                        c.mat[i][j] = modular.plus(c.mat[i][j], modular.mul(a.mat[i][k], b.mat[k][j]));
                    }
                }
            }
            return c;
        }

        public static LongModMatrix pow(LongModMatrix x, long n, NumberTheory.LongModular modular) {
            if (n == 0) {
                LongModMatrix r = new LongModMatrix(x.n, x.m);
                r.asStandard();
                return r;
            }
            LongModMatrix r = pow(x, n >> 1, modular);
            r = LongModMatrix.mul(r, r, modular);
            if (n % 2 == 1) {
                r = LongModMatrix.mul(r, x, modular);
            }
            return r;
        }

        static LongModMatrix transposition(LongModMatrix x, NumberTheory.LongModular modular) {
            int n = x.n;
            int m = x.m;
            LongModMatrix t = new LongModMatrix(m, n);
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
