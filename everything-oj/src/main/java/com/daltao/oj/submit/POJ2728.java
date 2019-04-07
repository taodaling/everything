package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;

public class POJ2728 {
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
        int limit = 1000;
        Node[] points = new Node[limit];
        Edge[][] edges = new Edge[limit][limit];

        {
            for (int i = 0; i < limit; i++) {
                points[i] = new Node();
                points[i].id = i;
            }

            for (int i = 0; i < limit; i++) {
                for (int j = 0; j < limit; j++) {
                    edges[i][j] = new Edge();
                    edges[i][j].src = points[i];
                    edges[i][j].dst = points[j];
                }
            }
        }

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            int n;
            while ((n = io.readInt()) != 0) {
                new Solver(n).solve();
            }
        }

        public class Solver {
            int n;

            public Solver(int n) {
                this.n = n;
            }

            public void solve() {
                for (int i = 0; i < n; i++) {
                    points[i].x = io.readInt();
                    points[i].y = io.readInt();
                    points[i].z = io.readInt();
                    for (int j = 0; j < i; j++) {
                        edges[i][j].length = edges[j][i].length = Math.sqrt(pow2(points[i].x - points[j].x) + pow2(points[i].y - points[j].y));
                    }
                }

                io.cache.append(String.format("%.3f", -fp())).append('\n');
            }

            public double fp() {
                double l = -10000000;
                double r = 0;
                while (r - l > 1e-6) {
                    double m = (l + r) / 2;
                    double hm = h(m);
                    if (hm >= 0) {
                        l = m;
                    } else {
                        r = m;
                    }
                }
                return l;
            }

            public double h(double m) {
                for (int i = 0; i < n; i++) {
                    points[i].merged = false;
                    points[i].distance = 1e30;
                    for (int j = i + 1; j < n; j++) {
                        edges[i][j].fee = edges[j][i].fee = Math.abs(points[i].z - points[j].z) + edges[i][j].length * m;
                    }
                }


                double sum = 0;
                Node lastPeek = points[0];
                lastPeek.merged = true;
                lastPeek.distance = 0;
                for (int i = 1; i < n; i++) {
                    Node nearest = null;
                    for (int j = 0; j < n; j++) {
                        if (points[j].merged) {
                            continue;
                        }
                        double distance = edges[lastPeek.id][j].fee;
                        if (points[j].distance > distance) {
                            points[j].distance = distance;
                        }
                        if (nearest == null || nearest.distance > points[j].distance) {
                            nearest = points[j];
                        }
                    }
                    sum += nearest.distance;
                    nearest.merged = true;
                    lastPeek = nearest;
                }

                return -sum;
            }


            public double pow2(double x) {
                return x * x;
            }
        }
    }

    public static class Node {
        double x;
        double y;
        double z;
        int id;
        boolean merged;
        double distance;

        @Override
        public String toString() {
            return "" + id;
        }

    }

    public static class Edge {
        Node src;
        Node dst;
        double length;
        double fee;

        @Override
        public String toString() {
            return src + "->" + dst;
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
                sign = next == '+' ? true : false;
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

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
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
