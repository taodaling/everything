package com.daltao.oj.old.submit.poj;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class POJ2007 {
    public static void main(String[] args) throws FileNotFoundException {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        IOUtil io;
        if (local) {
            io = new IOUtil(new FileInputStream("E:\\DATABASE\\TESTCASE\\POJ2007.in"), new FileOutputStream("E:\\DATABASE\\TESTCASE\\POJ2007.out"));
        } else {
            io = new IOUtil(System.in, System.out);
        }

        Task task = new Task(io);
        task.run();
        io.flush();
    }

    public static class Task implements Runnable {
        IOUtil io;
        double prec = 1e-6;

        public Task(IOUtil io) {
            this.io = io;
        }

        @Override
        public void run() {
            List<Geometry.Point2> pointList = new ArrayList();
            while (io.hasMore()) {
                pointList.add(new Geometry.Point2(io.readInt(), io.readInt()));
            }
            pointList = Geometry.grahamScan(pointList, prec);
            Geometry.Point2[] points = pointList.toArray(new Geometry.Point2[0]);

            int offset = 0;
            int n = points.length;
            for (int i = 0; i < n; i++) {
                if (Geometry.distance2(points[i]) < prec) {
                    offset = i;
                    break;
                }
            }

            Utils.move(points, 0, n, -offset);
            for (int i = 0; i < n; i++) {
                io.write('(');
                io.write(Mathematics.intRound(points[i].x));
                io.write(',');
                io.write(Mathematics.intRound(points[i].y));
                io.write(")\n");
            }
        }
    }

    public static class Geometry {
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

        public static int compare(double a, double b, double prec) {
            return Math.abs(a - b) < prec ? 0 : a < b ? -1 : 1;
        }

        /**
         * 从point2s点集中获取逆时针序构成凸包的外部点集
         */
        public static List<Point2> grahamScan(List<Point2> point2s, final double prec) {
            final Point2[] points = point2s.toArray(new Point2[0]);
            int n = points.length;

            Utils.swap(points, 0, Utils.min(points, 0, n, new Comparator<Point2>() {
                @Override
                public int compare(Point2 o1, Point2 o2) {
                    return o1.y != o2.y ? Double.compare(o1.y, o2.y) : Double.compare(o1.x, o2.x);
                }
            }));

            Comparator<Point2> cmp = new Comparator<Point2>() {
                @Override
                public int compare(Point2 o1, Point2 o2) {
                    return Geometry.compare(0, cross(o1.sub(points[0]), o2.sub(points[0])), prec);
                }
            };
            Arrays.sort(points, 1, n, cmp);

            int shrinkSize = 2;
            for (int i = 2; i < n; i++) {
                if (cmp.compare(points[i], points[shrinkSize - 1]) == 0) {
                    if (distance2(points[i].sub(points[0])) > distance2(points[shrinkSize - 1].sub(points[0]))) {
                        points[shrinkSize - 1] = points[i];
                    }
                } else {
                    points[shrinkSize++] = points[i];
                }
            }

            n = shrinkSize;
            Deque<Point2> stack = new ArrayDeque(n);
            stack.addLast(points[0]);
            for (int i = 1; i < n; i++) {
                while (stack.size() >= 2) {
                    Point2 last = stack.removeLast();
                    Point2 second = stack.peekLast();
                    if (cross(points[i].sub(second), last.sub(second)) < -prec) {
                        stack.addLast(last);
                        break;
                    }
                }
                stack.addLast(points[i]);
            }

            return new ArrayList(stack);
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

        /**
         * 判断p是否落在线段section上
         */
        public static boolean onSection(Point2 p, Line2 section, double precision) {
            return Math.abs(cross(p.sub(section.begin), section.getVector())) < precision
                    && p.x >= Math.min(section.begin.x, section.end.x) - precision && p.x <= Math.max(section.begin.x, section.end.x) + precision;
        }

        /**
         * 求p向量长度的平方
         */
        public static double distance2(Point2 p) {
            return p.x * p.x + p.y * p.y;
        }

        /**
         * 求p向量长度
         */
        public static double distance(Point2 p) {
            return Math.sqrt(distance2(p));
        }

        /**
         * 如果直线s1与s2有交点，则返回交点，若平行，则返回null。
         */
        public static Point2 lineIntersection(Line2 s1, Line2 s2, double prec) {
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

            return s1.getPointAt(alpha);
        }

        /**
         * 如果线段s1与s2有交点，则返回交点，否则返回null。
         */
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
    }

    public static class Mathematics {
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
            if (x < 0) {
                return -(int) (-x + 0.5);
            }
            return (int) (x + 0.5);
        }

        /**
         * Round x into long
         */
        public static long longRound(double x) {
            if (x < 0) {
                return -(long) (-x + 0.5);
            }
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
            while (r_next >= 0 && r_next <= 32) {
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

        public boolean hasMore() {
            skipBlank();
            return r_next != -1;
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

    public static class Utils {
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
    }
}
