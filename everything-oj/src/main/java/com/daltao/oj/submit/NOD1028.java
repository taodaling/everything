package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;

public class NOD1028 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("E:\\DATABASE\\TESTCASE\\NOD1028.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
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
            io.cache.append("\n\n--memory -- " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;

        public Task(FastIO io) {
            this.io = io;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            char[] aChars = new char[1000000];
            char[] bChars = new char[1000000];

            int aLen = io.readString(aChars, 0);
            int bLen = io.readString(bChars, 0);

            int cLen = aLen + bLen;
            int properCLenBit = 32 - Integer.numberOfLeadingZeros(cLen - 1);
            int properCLen = 1 << properCLenBit;

            double[][] a = new double[properCLen][2];
            double[][] b = new double[properCLen][2];
            int[] r = new int[properCLen];
            int[] result = new int[cLen];
            for (int i = 0; i < aLen; i++) {
                a[i][0] = aChars[aLen - 1 - i] - '0';
            }
            for (int i = 0; i < bLen; i++) {
                b[i][0] = bChars[bLen - 1 - i] - '0';
            }

            FastFourierTransform.fft(r, a, b, properCLenBit);

            int remain = 0;
            for (int i = 0; i < cLen; i++) {
                remain += eval(a[i][0]);
                result[i] = remain % 10;
                remain /= 10;
            }

            int i = cLen - 1;
            while (i > 0 && result[i] == 0) {
                i--;
            }
            for (; i >= 0; i--) {
                io.cache.append((char) (result[i] + '0'));
            }
        }

        public static int eval(double x) {
            return (int) (x + 0.5);
        }
    }

    public static class FastFourierTransform {
        private static double[][] wCache = new double[31][2];

        static {
            for (int i = 0, until = wCache.length; i < until; i++) {
                double s = 1 << i;
                wCache[i][0] = Math.cos(Math.PI / s);
                wCache[i][1] = Math.sin(Math.PI / s);
            }
        }

        public static void fft(int[] r, double[][] a, double[][] b, int m) {
            reverse(r, m);
            dft(r, a, m);
            dft(r, b, m);
            int n = 1 << m;
            for (int i = 0; i < n; i++) {
                mul(a[i][0], a[i][1], b[i][0], b[i][1], a[i]);
            }
            idft(r, a, m);
        }

        private static void reverse(int[] r, int b) {
            int n = 1 << b;
            r[0] = 0;
            for (int i = 1; i < n; i++) {
                r[i] = (r[i >> 1] >> 1) | ((1 & i) << (b - 1));
            }
        }

        private static void dft(int[] r, double[][] p, int m) {
            int n = 1 << m;

            for (int i = 0; i < n; i++) {
                if (r[i] > i) {
                    Memory.swap(p, i, r[i]);
                }
            }

            double[] w = new double[2];
            double[] t = new double[2];
            for (int d = 0; d < m; d++) {
                double[] w1 = wCache[d];
                int s = 1 << d;
                int s2 = s << 1;
                for (int i = 0; i < n; i += s2) {
                    w[0] = 1;
                    w[1] = 0;

                    for (int j = 0; j < s; j++) {
                        int a = i + j;
                        int b = a + s;
                        mul(w[0], w[1], p[b][0], p[b][1], t);
                        sub(p[a][0], p[a][1], t[0], t[1], p[b]);
                        add(p[a][0], p[a][1], t[0], t[1], p[a]);
                        mul(w[0], w[1], w1[0], w1[1], w);
                    }
                }
            }
        }

        private static void idft(int[] r, double[][] p, int m) {
            dft(r, p, m);

            int n = 1 << m;
            div(p[0][0], p[0][1], n, p[0]);
            div(p[n / 2][0], p[n / 2][1], n, p[n / 2]);
            for (int i = 1, until = n / 2; i < until; i++) {
                double a = p[n - i][0];
                double b = p[n - i][1];
                div(p[i][0], p[i][1], n, p[n - i]);
                div(a, b, n, p[i]);
            }
        }

        private static void add(double r1, double i1, double r2, double i2, double[] r) {
            r[0] = r1 + r2;
            r[1] = i1 + i2;
        }

        private static void sub(double r1, double i1, double r2, double i2, double[] r) {
            r[0] = r1 - r2;
            r[1] = i1 - i2;
        }

        private static void mul(double r1, double i1, double r2, double i2, double[] r) {
            r[0] = r1 * r2 - i1 * i2;
            r[1] = r1 * i2 + i1 * r2;
        }

        private static void div(double r1, double i1, double r2, double[] r) {
            r[0] = r1 / r2;
            r[1] = i1 / r2;
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

    public static class Memory {
        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> int min(T[] data, int from, int to, Comparator<T> cmp) {
            int m = from;
            for (int i = from + 1; i < to; i++) {
                if (cmp.compare(data[m], data[i]) > 0) {
                    m = i;
                }
            }
            return m;
        }

        public static <T> void move(T[] data, int from, int to, int step) {
            int len = to - from;
            step = len - (step % len + len) % len;
            Object[] buf = new Object[len];
            for (int i = 0; i < len; i++) {
                buf[i] = data[(i + step) % len + from];
            }
            System.arraycopy(buf, 0, data, from, len);
        }

        public static <T> void reverse(T[] data, int f, int t) {
            int l = f, r = t - 1;
            while (l < r) {
                swap(data, l, r);
                l++;
                r--;
            }
        }

        public static void copy(Object[] src, Object[] dst, int srcf, int dstf, int len) {
            if (len < 8) {
                for (int i = 0; i < len; i++) {
                    dst[dstf + i] = src[srcf + i];
                }
            } else {
                System.arraycopy(src, srcf, dst, dstf, len);
            }
        }
    }
}
