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

public class BZOJ4031 {
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
            int m = io.readInt();
            NumberTheory.Modular modular = new NumberTheory.Modular((int)1e9);

            int numberOfRoom = 0;
            int[][] mat = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (io.readChar() == '.') {
                        mat[i][j] = numberOfRoom++;
                    } else {
                        mat[i][j] = -1;
                    }
                }
            }

            if (numberOfRoom == 0) {
                io.cache.append(1);
            }

            ModMatrix matrix = new ModMatrix(numberOfRoom, numberOfRoom);
            int[][] ways = new int[][]{
                    {1, 0},
                    {-1, 0},
                    {0, 1},
                    {0, -1}
            };
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (mat[i][j] == -1) {
                        continue;
                    }
                    int deg = 0;
                    for (int[] way : ways) {
                        int ii = i + way[0];
                        int jj = j + way[1];
                        if (ii < 0 || jj < 0 || ii >= n || jj >= m
                                || mat[ii][jj] == -1) {
                            continue;
                        }
                        deg++;
                        matrix.set(mat[i][j], mat[ii][jj], -1);
                    }
                    matrix.set(mat[i][j], mat[i][j], deg);
                }
            }

            ModMatrix region = ModMatrix.region(matrix, 0, numberOfRoom - 2, 0, numberOfRoom - 2);
            int st = ModMatrix.determinant(region, modular);
            io.cache.append(st);
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

        public void set(int i, int j, int val){
            mat[i][j] = val;
        }

        public void normalize(NumberTheory.Modular mod) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = mod.valueOf(mat[i][j]);
                }
            }
        }


        public static ModMatrix region(ModMatrix x, int b, int t, int l, int r) {
            ModMatrix y = new ModMatrix(t - b + 1, r - l + 1);
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
        public static int determinant(ModMatrix x, NumberTheory.Modular modular) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            int n = x.n;
            ModMatrix l = new ModMatrix(x);
            l.normalize(modular);
            int ans = 1;
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
        public static int determinant(ModMatrix x, NumberTheory.Power power) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            NumberTheory.Modular modular = power.getModular();
            int n = x.n;
            ModMatrix l = new ModMatrix(x);
            l.normalize(modular);
            int ans = 1;
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
                    int f = l.mat[j][i];
                    l.subtractRow(j, i, f, modular);
                }
            }

            return ans;
        }

        public static ModMatrix inverse(ModMatrix x, NumberTheory.Power power) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            NumberTheory.Modular modular = power.getModular();
            int n = x.n;
            ModMatrix l = new ModMatrix(x);
            l.normalize(modular);
            ModMatrix r = new ModMatrix(n, n);
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

                int inv = power.inverse(l.mat[i][i]);
                r.mulRow(i, inv, modular);
                l.mulRow(i, inv, modular);

                for (int j = 0; j < n; j++) {
                    if (j == i) {
                        continue;
                    }
                    if (l.mat[j][i] == 0) {
                        continue;
                    }
                    int f = l.mat[j][i];
                    r.subtractRow(j, i, f, modular);
                    l.subtractRow(j, i, f, modular);
                }
            }
            return r;
        }

        void swapRow(int i, int j) {
            int[] row = mat[i];
            mat[i] = mat[j];
            mat[j] = row;
        }

        void subtractRow(int i, int j, int f, NumberTheory.Modular modular) {
            for (int k = 0; k < m; k++) {
                mat[i][k] = modular.subtract(mat[i][k], modular.mul(mat[j][k], f));
            }
        }

        void mulRow(int i, int f, NumberTheory.Modular modular) {
            for (int k = 0; k < m; k++) {
                mat[i][k] = modular.mul(mat[i][k], f);
            }
        }

        public static ModMatrix mul(ModMatrix a, ModMatrix b, NumberTheory.Modular modular) {
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

        public static ModMatrix pow(ModMatrix x, long n, NumberTheory.Modular modular) {
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

        static ModMatrix transposition(ModMatrix x, NumberTheory.Modular modular) {
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
