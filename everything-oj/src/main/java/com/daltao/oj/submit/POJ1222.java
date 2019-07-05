package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class POJ1222 {
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


        XorGuassianElimination ge = new XorGuassianElimination(5 * 6, 5 * 6);
        ArrayIndex arrayIndex = new ArrayIndex(5, 6);
        int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        public void solve() {
            int t = io.readInt();
            for (int i = 1; i <= t; i++) {
                solveSingle(i);
            }
        }

        public void solveSingle(int testCaseNum) {
            ge.clear();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 6; j++) {
                    int row = arrayIndex.indexOf(i, j);
                    ge.setRight(arrayIndex.indexOf(i, j), io.readInt());
                    ge.mat[row][row] = 1;
                    for (int[] dir : dirs) {
                        if (!arrayIndex.isValidIndex(i + dir[0], j + dir[1])) {
                            continue;
                        }
                        ge.mat[row][arrayIndex.indexOf(i + dir[0], j + dir[1])] = 1;
                    }
                }
            }

            io.cache.append("PUZZLE #").append(testCaseNum).append('\n');
            ge.solve();
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 6; j++) {
                    io.cache.append(ge.solutions[arrayIndex.indexOf(i, j)]).append(' ');
                }
                io.cache.append('\n');
            }
        }
    }

    public static class ArrayIndex {
        int[] dimensions;

        public ArrayIndex(int... dimensions) {
            this.dimensions = dimensions;
        }

        public int indexOf(int a, int b) {
            return a * dimensions[1] + b;
        }

        public int indexOf(int a, int b, int c) {
            return indexOf(a, b) * dimensions[2] + c;
        }

        public int indexOf(int a, int b, int c, int d) {
            return indexOf(a, b, c) * dimensions[3] + d;
        }

        public int indexOf(int a, int b, int c, int d, int e) {
            return indexOf(a, b, c, d) * dimensions[4] + e;
        }

        public boolean isValid(int a, int d) {
            return dimensions[d] > a && a >= 0;
        }

        public boolean isValidIndex(int a) {
            return isValid(a, 0);
        }

        public boolean isValidIndex(int a, int b) {
            return isValidIndex(a) && isValid(b, 1);
        }

        public boolean isValidIndex(int a, int b, int c) {
            return isValidIndex(a, b) && isValid(c, 2);
        }

        public boolean isValidIndex(int a, int b, int c, int d) {
            return isValidIndex(a, b, c) && isValid(d, 3);
        }

        public int indexOfSpecifiedDimension(int index, int d) {
            return indexOfSpecifiedDimension0(index, d, dimensions.length - 1);
        }

        private int indexOfSpecifiedDimension0(int index, int t, int now) {
            return now == t ? index % dimensions[now] : indexOfSpecifiedDimension0(index / dimensions[now], t, now - 1);
        }
    }

    public static class XorGuassianElimination {
        int[][] mat;
        int[] solutions;

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
