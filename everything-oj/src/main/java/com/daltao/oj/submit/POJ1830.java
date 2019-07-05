package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class POJ1830 {
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
            int t = io.readInt();
            while (t-- > 0)
                solve();
        }

        public void solve() {
            int n = io.readInt();
            int[] status = new int[n];
            for (int i = 0; i < n; i++) {
                status[i] = io.readInt();
            }
            for (int i = 0; i < n; i++) {
                status[i] ^= io.readInt();
            }
            XorGuassianElimination xge = new XorGuassianElimination(n, n);
            for (int i = 0; i < n; i++) {
                xge.mat[i][i] = 1;
                xge.setRight(i, status[i]);
            }

            while (true) {
                int i = io.readInt();
                int j = io.readInt();
                if (i == 0 && j == 0) {
                    break;
                }
                xge.mat[j - 1][i - 1] = 1;
            }

            if (!xge.solve()) {
                io.cache.append("Oh,it's impossible~!!").append('\n');
                return;
            }

            io.cache.append(1 << (n - xge.rank)).append('\n');
        }
    }

    public static class XorGuassianElimination {
        int[][] mat;
        int[] solutions;
        int rank;

        public XorGuassianElimination(int n, int m) {
            mat = new int[n + 1][m + 1];
            solutions = mat[n];
        }

        public void clear() {
            for (int[] row : mat) {
                Arrays.fill(row, 0);
            }
        }

        public void swapRow(int i, int j) {
            int[] tmp = mat[i];
            mat[i] = mat[j];
            mat[j] = tmp;
        }

        public void setRight(int row, int val) {
            mat[row][mat[row].length - 1] = val;
        }

        /**
         * Let a[i] = a[i] ^ a[j]
         */
        public void xorRow(int i, int j) {
            int m = mat[0].length;
            for (int k = 0; k < m; k++) {
                mat[i][k] ^= mat[j][k];
            }
        }

        public boolean solve() {
            int n = mat.length - 1;
            int m = mat[0].length - 1;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] &= 1;
                }
            }
            int now = 0;
            for (int i = 0; i < n; i++) {
                int swapRow = -1;
                for (int j = now; j < m; j++) {
                    if (mat[j][i] != 0) {
                        swapRow = j;
                        break;
                    }
                }
                if (swapRow == -1) {
                    continue;
                }
                swapRow(now, swapRow);
                for (int j = now + 1; j < m; j++) {
                    if (mat[j][i] == 1) {
                        xorRow(j, now);
                    }
                }
                now++;
            }

            for (int i = now; i < n; i++) {
                if (mat[i][m] != 0) {
                    return false;
                }
            }

            rank = now;
            for (int i = now - 1; i >= 0; i--) {
                int x = -1;
                for (int j = 0; j < m; j++) {
                    if (mat[i][j] != 0) {
                        x = j;
                        break;
                    }
                }
                mat[n][x] = mat[i][m];
                for (int j = i - 1; j >= 0; j--) {
                    if (mat[j][x] == 0) {
                        continue;
                    }
                    mat[j][x] = 0;
                    mat[j][m] ^= mat[n][x];
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            int n = mat.length - 1;
            int m = mat[0].length - 1;
            for (int i = 0; i < n; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < m; j++) {
                    if (mat[i][j] == 0) {
                        continue;
                    }
                    row.append("x").append(j).append('+');
                }
                if (row.length() > 0) {
                    row.setLength(row.length() - 1);
                } else {
                    row.append(0);
                }
                row.append("=").append(mat[i][m]);
                builder.append(row).append('\n');
            }
            return builder.toString();
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
