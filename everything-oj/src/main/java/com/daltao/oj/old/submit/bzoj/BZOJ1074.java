package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class BZOJ1074 {
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
        private double EPS = 0.000001;

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
            Coordination2D[] transforms = new Coordination2D[n];
            for (int i = 0; i < n; i++) {
                double x1 = io.readDouble();
                double y1 = io.readDouble();
                double x2 = io.readDouble();
                double y2 = io.readDouble();
                transforms[i] = Coordination2D.merge(Coordination2D.ofXAxis(x2 - x1, y2 - y1),
                        Coordination2D.ofOrigin(x1, y1));
            }

            int m = io.readInt();
            for (int i = 0; i < m; i++) {
                double x = io.readDouble();
                double y = io.readDouble();
                io.cache.append(find(transforms, n - 1, x, y)).append('\n');
            }
        }

        Matrix source = new Matrix(3, 1);
        Matrix target = new Matrix(3, 1);

        {
            source.mat[2][0] = target.mat[2][0] = 1;
        }

        public int find(Coordination2D[] transform, int i, double x, double y) {
            if (i < 0) {
                return x >= EPS && x <= 100 - EPS && y >= EPS && y <= 100 - EPS ? 1 : 0;
            }
            int ans = find(transform, i - 1, x, y);
            source.set(0, 0, x);
            source.set(1, 0, y);
            transform[i].toCurrentCoordination(source, target);
            if (target.get(1, 0) <= EPS) {
                return 0;
            }
            target.set(1, 0, -target.get(1, 0));
            transform[i].toNormalCoordination(target, source);
            return ans
                    + find(transform, i - 1, source.get(0, 0), source.get(1, 0));
        }
    }

    public static class Matrix implements Cloneable {
        double[][] mat;
        int n;
        int m;

        public void set(int i, int j, double val) {
            mat[i][j] = val;
        }

        public double get(int i, int j) {
            return mat[i][j];
        }

        public Matrix(Matrix model) {
            n = model.n;
            m = model.m;
            mat = new double[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = model.mat[i][j];
                }
            }
        }

        public Matrix(int n, int m) {
            this.n = n;
            this.m = m;
            mat = new double[n][m];
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

        public static Matrix mul(Matrix a, Matrix b, Matrix c) {
            c.fill(0);
            for (int i = 0; i < c.n; i++) {
                for (int j = 0; j < c.m; j++) {
                    for (int k = 0; k < a.m; k++) {
                        c.mat[i][j] = c.mat[i][j] + a.mat[i][k] * b.mat[k][j];
                    }
                }
            }
            return c;
        }

        public static Matrix mul(Matrix a, Matrix b) {
            Matrix c = new Matrix(a.n, b.m);
            return mul(a, b, c);
        }

        public static Matrix pow(Matrix x, int n) {
            if (n == 0) {
                Matrix r = new Matrix(x.n, x.m);
                r.asStandard();
                return r;
            }
            Matrix r = pow(x, n >> 1);
            r = Matrix.mul(r, r);
            if (n % 2 == 1) {
                r = Matrix.mul(r, x);
            }
            return r;
        }

        public static Matrix inverse(Matrix x) {
            if (x.n != x.m) {
                throw new RuntimeException("Matrix is not square");
            }
            int n = x.n;
            Matrix l = new Matrix(x);
            Matrix r = new Matrix(n, n);
            r.asStandard();
            for (int i = 0; i < n; i++) {
                int maxRow = i;
                for (int j = i; j < n; j++) {
                    if (Math.abs(l.mat[j][i]) > Math.abs(l.mat[maxRow][i])) {
                        maxRow = j;
                    }
                }

                if (l.mat[maxRow][i] == 0) {
                    throw new RuntimeException("Can't inverse current matrix");
                }
                r.swapRow(i, maxRow);
                l.swapRow(i, maxRow);

                r.divideRow(i, l.mat[i][i]);
                l.divideRow(i, l.mat[i][i]);

                for (int j = 0; j < n; j++) {
                    if (j == i) {
                        continue;
                    }
                    if (l.mat[j][i] == 0) {
                        continue;
                    }
                    double f = l.mat[j][i];
                    r.subtractRow(j, i, f);
                    l.subtractRow(j, i, f);
                }
            }
            return r;
        }

        static Matrix transposition(Matrix x) {
            int n = x.n;
            int m = x.m;
            Matrix t = new Matrix(m, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    t.mat[j][i] = x.mat[i][j];
                }
            }
            return t;
        }

        void swapRow(int i, int j) {
            double[] row = mat[i];
            mat[i] = mat[j];
            mat[j] = row;
        }

        void subtractRow(int i, int j, double f) {
            for (int k = 0; k < m; k++) {
                mat[i][k] -= mat[j][k] * f;
            }
        }

        void divideRow(int i, double f) {
            for (int k = 0; k < m; k++) {
                mat[i][k] /= f;
            }
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

    public static class Coordination2D {
        private Matrix mat;
        private Matrix inv;

        private static final double EPS = 1e-8;


        private static boolean near(double x, double y) {
            return Math.abs(x - y) <= EPS;
        }

        private Coordination2D(Matrix mat) {
            this.mat = mat;
            this.inv = Matrix.inverse(mat);
        }

        //set x coordination
        public static Coordination2D ofXAxis(double x, double y) {
            if (near(x * x + y * y, 1)) {
                double d = Math.sqrt(x * x + y * y);
                x /= d;
                y /= d;
            }
            Matrix mat = new Matrix(3, 3);
            mat.asStandard();
            mat.set(0, 0, x);
            mat.set(1, 0, y);
            mat.set(0, 1, -y);
            mat.set(1, 1, x);
            return new Coordination2D(mat);
        }

        public static Coordination2D ofOrigin(double x, double y) {
            Matrix mat = new Matrix(3, 3);
            mat.asStandard();
            mat.set(0, 2, x);
            mat.set(1, 2, y);
            return new Coordination2D(mat);
        }

        //As a * b
        public static Coordination2D merge(Coordination2D a, Coordination2D b) {
            return new Coordination2D(Matrix.mul(b.mat, a.mat));
        }

        public void toNormalCoordination(Matrix vec, Matrix ans) {
            Matrix.mul(mat, vec, ans);
        }

        public void toCurrentCoordination(Matrix vec, Matrix ans) {
            Matrix.mul(inv, vec, ans);
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
