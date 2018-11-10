package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class CFContest {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("/Users/daltao/DATABASE/TESTCASE/CFContest.in"), System.out, charset) : new FastIO(System.in,
                System.out, charset);
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
        static final int INF = (int) 1e8;

        public Task(FastIO io) {
            this.io = io;
        }

        @Override
        public void run() {
            while (io.hasMore()) {
                solve();
            }
        }

        public void solve() {

        }

        public static void mulMatrix(int[][] a, int[][] b, int[][] output) {
            int r = output.length;
            int c = output[0].length;
            int t = b.length;
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    output[i][j] = 0;
                    for (int k = 0; k < t; k++) {
                        output[i][j] = output[i][j] + a[i][k] * b[k][j];
                    }
                }
            }
        }

        public static void fastPowMatrix(int[][] a, int n, int[][] output) {

        }
    }

    public static class Loop<T> {
        T[] data;
        int pos;

        public Loop(T... data) {
            this.data = data;
        }

        public T turn(int i) {
            pos += i;
            return get(0);
        }

        public T get(int i) {
            return data[(pos + i) % data.length];
        }
    }

    public static class IntMatrix implements Cloneable {
        private int[][] data;

        public IntMatrix(int r, int c) {
            this.data = new int[r][c];
        }

        public void set(int i, int j, int val) {
            data[i][j] = val;
        }

        public int get(int i, int j) {
            return data[i][j];
        }

        public void fill(int v) {
            int r = getRowCount();
            int c = getColumnCount();
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    data[i][j] = v;
                }
            }
        }

        public void asStandard() {
            int r = getRowCount();
            int c = getColumnCount();
            if (r != c) {
                throw new UnsupportedOperationException();
            }

            fill(0);
            for (int i = 0; i < r; i++) {
                data[i][i] = 1;
            }
        }

        public static void mul(IntMatrix a, IntMatrix b, IntMatrix output) {
            int h = output.data.length;
            int w = output.data[0].length;
            int t = b.data.length;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    output.data[i][j] = 0;
                    for (int k = 0; k < t; k++) {
                        output.data[i][j] += a.data[i][k] * b.data[k][j];
                    }
                }
            }
        }

        public int getRowCount() {
            return data.length;
        }

        public int getColumnCount() {
            return data[0].length;
        }

        public void copy(IntMatrix a) {
            int r = a.data.length;
            int c = a.data[0].length;
            for (int i = 0; i < r; i++) {
                System.arraycopy(a.data[i], 0, data[i], 0, c);
            }
        }

        public static IntMatrix copyOf(IntMatrix a) {
            IntMatrix matrix = new IntMatrix(a.getRowCount(), a.getColumnCount());
            matrix.copy(a);
            return matrix;
        }

        public static IntMatrix mul(IntMatrix a, IntMatrix b) {
            IntMatrix result = new IntMatrix(a.data.length, b.data[0].length);
            mul(a, b, result);
            return result;
        }

        /**
         * Get x^n, you are supposed to pass a loop with length at least 2.
         * You can get the result(x^n) by matrixLoop.get(0).
         */
        public static void pow(IntMatrix x, int n, Loop<IntMatrix> matrixLoop) {
            int offset = 31 - Integer.numberOfLeadingZeros(n);

            matrixLoop.get(0).asStandard();
            for (; offset >= 0; offset--) {
                mul(matrixLoop.get(0), matrixLoop.get(0), matrixLoop.turn(1));
                if (((n >> offset) & 1) != 0) {
                    mul(matrixLoop.get(0), x, matrixLoop.turn(1));
                }
            }
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
            long divisor = 1;
            long later = 0;
            while (next >= '0' && next <= '9') {
                divisor = divisor * 10;
                later = later * 10 + next - '0';
                next = read();
            }

            if (num >= 0) {
                return num + (later / (double) divisor);
            } else {
                return num - (later / (double) divisor);
            }
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
}