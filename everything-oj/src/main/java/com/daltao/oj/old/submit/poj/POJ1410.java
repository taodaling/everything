package com.daltao.oj.old.submit.poj;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

public class POJ1410 {
    public static void main(String[] args) throws FileNotFoundException {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        IOUtil io;
        if (local) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\poj\\POJ1410.in"), new FileOutputStream("E:\\DATABASE\\TESTCASE\\poj\\POJ1410.out"));
        } else {
            io = new IOUtil(System.in, System.out);
        }

        Task task = new Task(io);
        task.run();
        io.flush();
    }

    public static class Task implements Runnable {
        IOUtil io;
        final double PREC = 1e-6;

        public Task(IOUtil io) {
            this.io = io;
        }

        @Override
        public void run() {
            for (int i = 0, n = io.readInt(); i < n; i++) {
                solveOne();
            }
        }

        public void solveOne() {

/*            Mathematics.Point2 pt = Mathematics.sectionIntersection(
                    new Mathematics.Line2(new Mathematics.Point2(1, 0), new Mathematics.Point2(0, 1)),
                    new Mathematics.Line2(new Mathematics.Point2(0, 0), new Mathematics.Point2(1, 1)),
                    PREC
            );*/

            Mathematics.Line2 line = new Mathematics.Line2(
                    new Mathematics.Point2(io.readInt(), io.readInt()),
                    new Mathematics.Point2(io.readInt(), io.readInt())
            );

            double[] rectX = new double[2];
            double[] rectY = new double[2];
            rectX[0] = io.readInt();
            rectY[0] = io.readInt();
            rectX[1] = io.readInt();
            rectY[1] = io.readInt();

            Arrays.sort(rectX);
            Arrays.sort(rectY);

            if (Math.min(line.begin.x, line.end.x) >= rectX[0] && Math.max(line.begin.x, line.end.x) <= rectX[1]
                    && Math.min(line.begin.y, line.end.y) >= rectY[0] && Math.max(line.begin.y, line.end.y) <= rectY[1]) {
                io.write("T\n");
                return;
            }

            Mathematics.Point2 lb = new Mathematics.Point2(rectX[0], rectY[0]);
            Mathematics.Point2 rb = new Mathematics.Point2(rectX[1], rectY[0]);
            Mathematics.Point2 lt = new Mathematics.Point2(rectX[0], rectY[1]);
            Mathematics.Point2 rt = new Mathematics.Point2(rectX[1], rectY[1]);

            if (Mathematics.sectionIntersection(line, new Mathematics.Line2(lb, lt), PREC) == null &&
                    Mathematics.sectionIntersection(line, new Mathematics.Line2(lb, rb), PREC) == null &&
                    Mathematics.sectionIntersection(line, new Mathematics.Line2(lt, rt), PREC) == null &&
                    Mathematics.sectionIntersection(line, new Mathematics.Line2(rt, rb), PREC) == null) {
                io.write("F\n");
                return;
            }

            io.write("T\n");
            return;
        }
    }

    public static class Mathematics {
        public static class Point2 {
            final double x, y;

            public Point2(double x, double y) {
                this.x = x;
                this.y = y;
            }

            public Point2 add(Point2 other) {
                return new Point2(x + other.x, y + other.y);
            }

            public Point2 sub(Point2 other) {
                return new Point2(x - other.x, y - other.y);
            }

            @Override
            public String toString() {
                return String.format("(%f,%f)", x, y);
            }
        }

        public static class Line2 {
            final Point2 begin, end;

            public Line2(Point2 begin, Point2 end) {
                this.begin = begin;
                this.end = end;
            }

            public Point2 getVector() {
                return end.sub(begin);
            }

            public Point2 getPointAt(double alpha) {
                return new Point2((1 - alpha) * begin.x + alpha * end.x, (1 - alpha) * begin.y + alpha * end.y);
            }

            @Override
            public String toString() {
                return String.format("%s->%s", begin.toString(), end.toString());
            }
        }

        /**
         * Get the greatest common divisor of a and b
         */
        public static int gcd(int a, int b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private static int gcd0(int a, int b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        public static int extgcd(int a, int b, int[] coe) {
            return a >= b ? extgcd0(a, b, coe) : extgcd0(b, a, coe);
        }

        private static int extgcd0(int a, int b, int[] coe) {
            if (b == 0) {
                coe[0] = 1;
                coe[1] = 0;
                return a;
            }
            int g = extgcd0(b, a % b, coe);
            int n = coe[0];
            int m = coe[1];
            coe[0] = m;
            coe[1] = n - m * (a / b);
            return g;
        }

        /**
         * Get the greatest common divisor of a and b
         */
        public static long gcd(long a, long b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private static long gcd0(long a, long b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        public static long extgcd(long a, long b, long[] coe) {
            return a >= b ? extgcd0(a, b, coe) : extgcd0(b, a, coe);
        }

        private static long extgcd0(long a, long b, long[] coe) {
            if (b == 0) {
                coe[0] = 1;
                coe[1] = 0;
                return a;
            }
            long g = extgcd0(b, a % b, coe);
            long n = coe[0];
            long m = coe[1];
            coe[0] = m;
            coe[1] = n - m * (a / b);
            return g;
        }

        /**
         * Get y where x * y = 1 (% mod)
         */
        public static int inverse(int x, int mod) {
            return pow(x, mod - 2, mod);
        }

        /**
         * Get x^n(% mod)
         */
        public static int pow(int x, int n, int mod) {
            n = mod(n, mod - 1);
            x = mod(x, mod);
            int bit = 31 - Integer.numberOfLeadingZeros(n);
            long product = 1;
            for (; bit >= 0; bit--) {
                product = product * product % mod;
                if (((1 << bit) & n) != 0) {
                    product = product * x % mod;
                }
            }
            return (int) product;
        }

        /**
         * Get x % mod
         */
        public static int mod(int x, int mod) {
            return x >= 0 ? x % mod : (((x % mod) + mod) % mod);
        }

        /**
         * Get (x1, y1)·(x2, y2)
         */
        public static long cross(int x1, int y1, int x2, int y2) {
            return (long) x1 * y2 - (long) y1 * x2;
        }

        /**
         * Get (x1, y1)·(x2, y2)
         */
        public static double cross(double x1, double y1, double x2, double y2) {
            return x1 * y2 - y1 * x2;
        }

        /**
         * Get (x1, y1)·(x2, y2)
         */
        public static double cross(Point2 a, Point2 b) {
            return a.x * b.y - a.y * b.x;
        }

        public static boolean onSection(Point2 p, Line2 section, double precision) {
            return Math.abs(cross(p.sub(section.begin), section.getVector())) < precision
                    && p.x >= Math.min(section.begin.x, section.end.x) - precision && p.x <= Math.max(section.begin.x, section.end.x) + precision;
        }

        public static Point2 lineIntersection(Line2 s1, Line2 s2) {
            double m11 = s1.end.x - s1.begin.x;
            double m10 = s2.begin.x - s2.end.x;
            double m01 = s1.end.y - s1.begin.y;
            double m00 = s2.begin.y - s2.end.y;

            double div = m00 * m11 - m01 * m10;
            double v0 = (s2.begin.x - s1.begin.x) / div;
            double v1 = (s2.begin.y - s1.begin.y) / div;

            double alpha = m00 * v0 + m01 * v1;

            return s1.getPointAt(alpha);
        }

        public static Point2 sectionIntersection(Line2 s1, Line2 s2, double prec) {
            double m11 = s1.end.x - s1.begin.x;
            double m01 = s2.end.x - s2.begin.x;
            double m10 = s1.begin.y - s1.end.y;
            double m00 = s2.begin.y - s2.end.y;

            double div = m00 * m11 - m01 * m10;
            if (Math.abs(div) < prec) {
                return null;
            }

            double v0 = (s2.begin.x - s1.begin.x) / div;
            double v1 = (s2.begin.y - s1.begin.y) / div;

            double alpha = m00 * v0 + m01 * v1;
            double beta = m10 * v0 + m11 * v1;

            if (-prec <= alpha && alpha <= 1 + prec && -prec <= beta && beta <= 1 + prec) {
                return s1.getPointAt(alpha);
            }
            return null;
        }

        /**
         * Get n!/(n-m)!
         */
        public static long permute(int n, int m) {
            return m == 0 ? 1 : n * permute(n - 1, m - 1);
        }

        /**
         * Put all primes less or equal to limit into primes after offset
         */
        public static int eulerSieve(int limit, int[] primes, int offset) {
            boolean[] isComp = new boolean[limit + 1];
            int wpos = offset;
            for (int i = 2; i <= limit; i++) {
                if (!isComp[i]) {
                    primes[wpos++] = i;
                }
                for (int j = offset, until = limit / i; j < wpos && primes[j] <= until; j++) {
                    int pi = primes[j] * i;
                    isComp[pi] = true;
                    if (i % primes[j] == 0) {
                        break;
                    }
                }
            }
            return wpos - offset;
        }

        /**
         * Round x into integer
         */
        public static int intRound(double x) {
            return (int) (x + 0.5);
        }

        /**
         * Round x into long
         */
        public static long longRound(double x) {
            return (long) (x + 0.5);
        }
    }

    public static class IOUtil {
        private static int BUF_SIZE = 1 << 13;

        private byte[] r_buf = new byte[BUF_SIZE];
        private int r_cur;
        private int r_total;
        private int r_next;
        private final InputStream in;
        private StringBuilder temporary = new StringBuilder();

        StringBuilder w_buf = new StringBuilder();
        private final OutputStream out;

        public IOUtil(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        private void skipBlank() {
            while (r_next <= 32) {
                r_next = read();
            }
        }

        public int readString(char[] data, int offset, int limit) {
            skipBlank();

            int originalLimit = limit;
            while (limit > 0 && r_next > 32) {
                data[offset++] = (char) r_next;
                limit--;
                r_next = read();
            }

            return originalLimit - limit;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (r_next > 32) {
                builder.append((char) r_next);
                r_next = read();
            }

            return builder.toString();
        }

        public String readString() {
            temporary.setLength(0);
            return readString(temporary);
        }

        public long readUnsignedLong() {
            skipBlank();

            long num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public long readLong() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign == 1 ? readUnsignedLong() : readNegativeLong();
        }

        public long readNegativeLong() {
            skipBlank();

            long num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 - r_next + '0';
                r_next = read();
            }
            return num;
        }

        public int readUnsignedInt() {
            skipBlank();

            int num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public int readNegativeInt() {
            skipBlank();

            int num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 - r_next + '0';
                r_next = read();
            }
            return num;
        }

        public int readInt() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign == 1 ? readUnsignedInt() : readNegativeInt();
        }

        public int read() {
            while (r_total <= r_cur) {
                try {
                    r_total = in.read(r_buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                r_cur = 0;
                if (r_total == -1) {
                    return -1;
                }
            }
            return r_buf[r_cur++];
        }

        public void write(char c) {
            w_buf.append(c);
        }

        public void write(int n) {
            w_buf.append(n);
        }

        public void write(String s) {
            w_buf.append(s);
        }

        public void write(long s) {
            w_buf.append(s);
        }

        public void write(double s) {
            w_buf.append(s);
        }

        public void write(float s) {
            w_buf.append(s);
        }

        public void write(Object s) {
            w_buf.append(s);
        }

        public void write(char[] data, int offset, int cnt) {
            for (int i = offset, until = offset + cnt; i < until; i++) {
                write(data[i]);
            }
        }

        public void flush() {
            try {
                out.write(w_buf.toString().getBytes(Charset.forName("ascii")));
                w_buf.setLength(0);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public double readDouble() {
            return Double.parseDouble(readString());
        }
    }
}
