package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class AGC004E {
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

        int[][] mat;
        int[][] rows;
        int[][] cols;
        int[][][][] dp;


        int h;
        int w;

        public int getRow(int i, int l, int r) {
            if(l > r){
                return 0;
            }
            if (l == 0) {
                return rows[i][r];
            }
            return rows[i][r] - rows[i][l - 1];
        }

        public int getCol(int j, int l, int r) {
            if(l > r){
                return 0;
            }
            if (l == 0) {
                return cols[r][j];
            }
            return cols[r][j] - cols[l - 1][j];
        }

        int[] exit = new int[2];

        public void solve() {
            h = io.readInt();
            w = io.readInt();

            mat = new int[h][w];
            rows = new int[h][w];
            cols = new int[h][w];
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    char c = io.readChar();
                    if (c == 'o') {
                        mat[i][j] = 1;
                    } else if (c == 'E') {
                        exit[0] = i;
                        exit[1] = j;
                    }
                }
            }

            for (int i = 0; i < h; i++) {
                rows[i][0] = mat[i][0];
                for (int j = 1; j < w; j++) {
                    rows[i][j] = rows[i][j - 1] + mat[i][j];
                }
            }

            for (int j = 0; j < w; j++) {
                cols[0][j] = mat[0][j];
                for (int i = 1; i < h; i++) {
                    cols[i][j] = cols[i - 1][j] + mat[i][j];
                }
            }


            //lrdu
            dp = new int[exit[1] + 1][w - exit[1] - 1 + 1][exit[0] + 1][h - exit[0] - 1 + 1];
            for (int[][][] x : dp) {
                for (int[][] y : x) {
                    for (int[] z : y) {
                        Arrays.fill(z, -1);
                    }
                }
            }

            int ans = dp(0, 0, 0, 0);
            io.cache.append(ans);
        }

        public int dp(int l, int r, int d, int u) {
            if (dp[l][r][d][u] == -1) {
                dp[l][r][d][u] = 0;

                if (l + r < exit[1]) {
                    dp[l][r][d][u] = Math.max(dp[l][r][d][u],
                            dp(l + 1, r, d, u) + getCol(exit[1] - l - 1, Math.max(u, exit[0] - d), Math.min(h - 1 - d, exit[0] + u)));
                }
                if (exit[1] + l + r < w - 1) {
                    dp[l][r][d][u] = Math.max(dp[l][r][d][u],
                            dp(l, r + 1, d, u) + getCol(exit[1] + r + 1, Math.max(u, exit[0] - d), Math.min(h - 1 - d, exit[0] + u)));
                }
                if (d + u < exit[0]) {
                    dp[l][r][d][u] = Math.max(dp[l][r][d][u],
                            dp(l, r, d + 1, u) + getRow(exit[0] - d - 1, Math.max(r, exit[1] - l), Math.min(w - 1 - l, exit[1] + r)));
                }
                if (exit[0] + d + u < h - 1) {
                    dp[l][r][d][u] = Math.max(dp[l][r][d][u],
                            dp(l, r, d, u + 1) + getRow(exit[0] + u + 1, Math.max(r, exit[1] - l), Math.min(w - 1 - l, exit[1] + r)));
                }
            }
            return dp[l][r][d][u];
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
