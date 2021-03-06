package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class BZOJ1568 {
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

        public void solve() {
            LowerConvex convex = new LowerConvex();
            convex.insert(0, 0);
            int n = io.readInt();
            char[] command = new char[20];
            for (int i = 0; i < n; i++) {
                io.readString(command, 0);
                if (command[0] == 'Q') {
                    int x = io.readInt();
                    int y = (int) (convex.query(x) / 100);
                    io.cache.append(y).append('\n');
                } else {
                    double s = io.readDouble();
                    double p = io.readDouble();
                    convex.insert(p, s - p);
                }
            }
        }
    }

    public static class LowerConvex {
        public static class Line {
            // y = ax + b
            double a;
            double b;
            double lx;
            double rx;

            static Comparator<Line> orderByA = new Comparator<Line>() {
                @Override
                public int compare(Line o1, Line o2) {
                    return Double.compare(o1.a, o2.a);
                }
            };
            static Comparator<Line> orderByLx = new Comparator<Line>() {
                @Override
                public int compare(Line o1, Line o2) {
                    return Double.compare(o1.lx, o2.lx);
                }
            };

            public Line(double a, double b) {
                this.a = a;
                this.b = b;
            }

            public double y(double x) {
                return a * x + b;
            }

            //a1x+b1=a2x+b2=>(a1-a2)x=b2-b1=>x=(b2-b1)/(a1-a2)
            public static double intersectAt(Line a, Line b) {
                return (b.b - a.b) / (a.a - b.a);
            }

            @Override
            public String toString() {
                return a + "x+" + b;
            }
        }

        private TreeSet<Line> setOrderByA = new TreeSet(Line.orderByA);
        private TreeSet<Line> setOrderByLx = new TreeSet(Line.orderByLx);

        private Line queryLine = new Line(0, 0);

        public double query(double x) {
            queryLine.lx = x;
            Line line = setOrderByLx.floor(queryLine);
            return line.y(x);
        }

        public void insert(double a, double b) {
            Line newLine = new Line(a, b);
            boolean add = true;
            while (true) {
                Line prev = setOrderByA.floor(newLine);
                if (prev == null) {
                    newLine.lx = Double.MIN_VALUE;
                    break;
                }
                if (prev.a == newLine.a) {
                    if (prev.b >= newLine.b) {
                        add = false;
                        break;
                    } else {
                        setOrderByA.remove(prev);
                        setOrderByLx.remove(prev);
                    }
                } else {
                    double lx = Line.intersectAt(prev, newLine);
                    if (lx <= prev.lx) {
                        setOrderByA.remove(prev);
                        setOrderByLx.remove(prev);
                    } else if (lx >= prev.rx) {
                        add = false;
                        break;
                    } else {
                        prev.rx = lx;
                        newLine.lx = lx;
                        break;
                    }
                }
            }

            while (true) {
                Line next = setOrderByA.ceiling(newLine);
                if (next == null) {
                    newLine.rx = Double.MAX_VALUE;
                    break;
                }
                double rx = Line.intersectAt(newLine, next);
                if (rx >= next.rx) {
                    setOrderByA.remove(next);
                    setOrderByLx.remove(next);
                } else if (rx <= next.lx) {
                    add = false;
                    break;
                } else {
                    next.lx = rx;
                    newLine.rx = rx;
                    break;
                }
            }

            if (add) {
                setOrderByA.add(newLine);
                setOrderByLx.add(newLine);
            }
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
