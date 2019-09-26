package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class LUOGU1224 {
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
        Random random = new Random(19950823);

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
            int d = io.readInt();
            int k = io.readInt();
            int[][] mat = new int[n][d];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < d; j++) {
                    mat[i][j] = io.readInt() % k;
                }
            }

            if (k == 2) {
                solve2(mat);
            } else {
                solve3(mat);
            }
        }


        public void solve2(int[][] mat) {
            int n = mat.length;
            int m = mat[0].length;
            //check AA^T is all 1
            int whichRow = -1;
            for (int t = 0; t < 5 && whichRow == -1; t++) {
                int[] bits = new int[n];
                int[] vec = new int[n];
                int[] vertical = new int[n];
                int[] mul = new int[m];
                int total = 0;
                for (int i = 0; i < n; i++) {
                    bits[i] = vec[i] = random.nextInt(2);
                    total += bits[i];
                }
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < n; j++) {
                        vertical[j] = mat[j][i];
                    }
                    mul[i] = dotmul(vec, vertical, 2);
                }

                for (int j = 0; j < n; j++) {
                    vec[j] = dotmul(mul, mat[j], 2);
                }

                for (int i = 0; i < n; i++) {
                    int exp = total;
                    if (bits[i] == 1) {
                        exp -= dotmul(mat[i], mat[i], 2);
                    }
                    if (Math.abs(vec[i] - exp) % 2 != 0) {
                        whichRow = i;
                        break;
                    }
                }
            }

            if (whichRow == -1) {
                io.cache.append("-1 -1");
                return;
            }

            for (int i = 0; i < n; i++) {
                if (whichRow == i) {
                    continue;
                }
                if (dotmul(mat[whichRow], mat[i], 2) == 0) {
                    answer(i, whichRow);
                    return;
                }
            }
        }

        public void answer(int p, int q){
            if(p > q){
                int tmp = p;
                p = q;
                q = tmp;
            }
            p++;
            q++;
            io.cache.append(p).append(' ').append(q);
        }

        public void solve3(int[][] mat) {
            int n = mat.length;
            int m = mat[0].length;
            //check AA^T is all 1
            int whichRow = -1;
            for (int t = 0; t < 5 && whichRow == -1; t++) {
                int[] bits = new int[n];
                int[] vec = new int[n];
                int[] vertical = new int[n];
                int[] mul = new int[m * m];
                int total = 0;
                for (int i = 0; i < n; i++) {
                    bits[i] = vec[i] = random.nextInt(2);
                    total += bits[i];
                }
                for (int i = 0; i < m; i++) {
                    for(int k = 0; k < m; k++) {
                        for (int j = 0; j < n; j++) {
                            vertical[i * m + k] = mat[j][i] * mat[j][k] ;
                        }
                    }
                    mul[i] = dotmul(vec, vertical, 2);
                }

                for (int j = 0; j < n; j++) {
                    vec[j] = dotmul(mul, mat[j], 2);
                }

                for (int i = 0; i < n; i++) {
                    int exp = total;
                    if (bits[i] == 1) {
                        exp -= dotmul(mat[i], mat[i], 2);
                    }
                    if (exp != vec[i]) {
                        whichRow = i;
                        break;
                    }
                }
            }

            if (whichRow == -1) {
                io.cache.append("-1 -1");
                return;
            }

            for (int i = 0; i < n; i++) {
                if (whichRow == i) {
                    continue;
                }
                if (dotmul(mat[whichRow], mat[i], 2) == 0) {
                    answer(i, whichRow);
                    return;
                }
            }
        }

        public static int dotmul(int[] a, int[] b, int mod) {
            int n = a.length;
            int ans = 0;
            for (int i = 0; i < n; i++) {
                ans += a[i] * b[i] % mod;
            }
            return ans;
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
