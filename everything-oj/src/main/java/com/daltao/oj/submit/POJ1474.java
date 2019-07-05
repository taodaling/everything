package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

public class POJ1474 {
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
            int t = 0;
            while (true) {
                t++;
                int n = io.readInt();
                if (n == 0) {
                    break;
                }
                GeometryUtils.Point2D[] points = new GeometryUtils.Point2D[n];
                for (int i = 0; i < n; i++) {
                    points[n - i - 1] = new GeometryUtils.Point2D(io.readInt(), io.readInt());
                }
                GeometryUtils.Line2D[] lines = new GeometryUtils.Line2D[n];
                lines[0] = new GeometryUtils.Line2D(points[n - 1], points[0]);
                for (int i = 1; i < n; i++) {
                    lines[i] = new GeometryUtils.Line2D(points[i - 1], points[i]);
                }
                GeometryUtils.HalfPlaneIntersection hpi = new GeometryUtils.HalfPlaneIntersection(Arrays.asList(lines), true);
                io.cache.append("Floor #").append(t).append('\n');
                io.cache.append(hpi.hasSolution ? "Surveillance is possible." : "Surveillance is impossible.").append("\n\n");
            }
        }
    }

    public static class GeometryUtils {
        private static final double PREC = 1e-8;
        private static final double INF = 1e30;

        public static double valueOf(double x) {
            return x > -PREC && x < PREC ? 0 : x;
        }

        public static class Point2D {
            final double x;
            final double y;
            static final Point2D ORIGIN = new Point2D(0, 0);

            public Point2D(double x, double y) {
                this.x = valueOf(x);
                this.y = valueOf(y);
            }

            public double distance2Between(Point2D another) {
                double dx = x - another.x;
                double dy = y - another.y;
                return valueOf(dx * dx + dy * dy);
            }

            public double distanceBetween(Point2D another) {
                return valueOf(Math.sqrt(distance2Between(another)));
            }

            /**
             * 以自己为起点，判断线段a和b的叉乘
             */
            public double cross(Point2D a, Point2D b) {
                return GeometryUtils.cross(a.x - x, a.y - y, b.x - x, b.y - y);
            }

            @Override
            public String toString() {
                return String.format("(%f, %f)", x, y);
            }
        }


        public static class Line2D {
            final Point2D a;
            final Point2D b;
            final Point2D d;
            final double theta;
            /**
             * 按照[0,2pi)极角对线排序
             */
            static final Comparator<Line2D> SORT_BY_ANGLE = new Comparator<Line2D>() {
                @Override
                public int compare(Line2D a, Line2D b) {
                    return Double.compare(a.theta, b.theta);
                }
            };


            public Line2D(Point2D a, Point2D b) {
                this.a = a;
                this.b = b;
                d = new Point2D(valueOf(b.x - a.x), valueOf(b.y - a.y));
                theta = Math.atan2(d.y, d.x);
            }

            /**
             * 判断a处于b的哪个方向，返回1，表示处于逆时针方向，返回-1，表示处于顺时针方向。0表示共线。
             */
            public int onWhichSide(Line2D b) {
                return Double.compare(cross(d.x, d.y, b.d.x, b.d.y), 0);
            }

            /**
             * 判断pt处于自己的哪个方向，返回1，表示处于逆时针方向，返回-1，表示处于顺时针方向。0表示共线。
             */
            public int whichSideIs(Point2D pt) {
                return Double.compare(a.cross(b, pt), 0);
            }

            public double getSlope() {
                return a.y / a.x;
            }

            public double getB() {
                return a.y - getSlope() * a.x;
            }

            public Point2D intersect(Line2D another) {
                double m11 = b.x - a.x;
                double m01 = another.b.x - another.a.x;
                double m10 = a.y - b.y;
                double m00 = another.a.y - another.b.y;

                double div = valueOf(m00 * m11 - m01 * m10);
                if (div == 0) {
                    return null;
                }

                double v0 = (another.a.x - a.x) / div;
                double v1 = (another.a.y - a.y) / div;

                double alpha = m00 * v0 + m01 * v1;
                return getPoint(alpha);
            }

            /**
             * 获取与线段的交点，null表示无交点或有多个交点
             */
            public Point2D getPoint(double alpha) {
                return new Point2D(a.x + d.x * alpha, a.y + d.y * alpha);
            }

            @Override
            public String toString() {
                return d.toString();
            }
        }

        public static class Segment2D extends Line2D {
            public Segment2D(Point2D a, Point2D b) {
                super(a, b);
            }

            /**
             * 判断p是否落在线段section上
             */
            public boolean contain(Point2D p) {
                return cross(p.x - a.x, p.y - a.y, d.x, d.y) == 0
                        && valueOf(p.x - Math.min(a.x, b.x)) >= 0 && valueOf(p.x - Math.min(a.x, b.x)) <= 0
                        && valueOf(p.y - Math.min(a.y, b.y)) >= 0 && valueOf(p.y - Math.min(a.y, b.y)) <= 0;
            }

            /**
             * 获取与线段的交点，null表示无交点或有多个交点
             */
            public Point2D intersect(Segment2D another) {
                Point2D point = super.intersect(another);
                return point != null && contain(point) ? point : null;
            }

        }

        /**
         * 计算两个向量的叉乘
         */
        public static double cross(double x1, double y1, double x2, double y2) {
            return valueOf(x1 * y2 - y1 * x2);
        }

        public static int signOf(double x) {
            return x > 0 ? 1 : x < 0 ? -1 : 0;
        }

        public static class Area {
            public double areaOfRect(Line2D a, Line2D b) {
                return Math.abs(cross(a.d.x, a.d.y, b.d.x, b.d.y));
            }

            public double areaOfTriangle(Line2D a, Line2D b) {
                return areaOfRect(a, b) / 2;
            }
        }

        public static class GrahamScan {
            ConvexHull<Point2D> convex;

            public GrahamScan(List<Point2D> point2s) {
                final Point2D[] points = point2s.toArray(new Point2D[0]);
                int n = points.length;
                for (int i = 1; i < n; i++) {
                    int cmp = points[i].y != points[0].y ? Double.compare(points[i].y, points[0].y)
                            : Double.compare(points[i].x, points[0].x);
                    if (cmp >= 0) {
                        continue;
                    }
                    Point2D tmp = points[0];
                    points[i] = points[0];
                    points[0] = tmp;
                }


                Comparator<Point2D> cmp = new Comparator<Point2D>() {
                    @Override
                    public int compare(Point2D o1, Point2D o2) {
                        return signOf(valueOf(-points[0].cross(o1, o2)));
                    }
                };
                Arrays.sort(points, 1, n, cmp);

                int shrinkSize = 2;
                for (int i = 2; i < n; i++) {
                    if (cmp.compare(points[i], points[shrinkSize - 1]) == 0) {
                        if (points[i].distance2Between(points[0]) > points[shrinkSize - 1].distance2Between(points[0])) {
                            points[shrinkSize - 1] = points[i];
                        }
                    } else {
                        points[shrinkSize++] = points[i];
                    }
                }

                n = shrinkSize;
                Deque<Point2D> stack = new ArrayDeque(n);
                stack.addLast(points[0]);
                for (int i = 1; i < n; i++) {
                    while (stack.size() >= 2) {
                        Point2D last = stack.removeLast();
                        Point2D second = stack.peekLast();
                        if (valueOf(second.cross(points[i], last)) < 0) {
                            stack.addLast(last);
                            break;
                        }
                    }
                    stack.addLast(points[i]);
                }

                convex = new ConvexHull(new ArrayList(stack));
            }
        }

        public static class ConvexHull<T> extends Polygon<T> {
            private ConvexHull(List<T> points) {
                super(points);
            }
        }

        public static class HalfPlaneIntersection {
            ConvexHull<Line2D> convex;
            boolean hasSolution = true;

            public HalfPlaneIntersection(List<Line2D> lineList, boolean close) {
                Line2D[] lines = lineList.toArray(new Line2D[lineList.size()]);
                Arrays.sort(lines, Line2D.SORT_BY_ANGLE);
                int n = lines.length;


                Deque<Line2D> deque = new ArrayDeque(n);
                for (int i = 0; i < n; i++) {
                    Line2D line = lines[i];
                    while (i + 1 < n && line.onWhichSide(lines[i + 1]) == 0) {
                        i++;
                        if (line.whichSideIs(lines[i].b) == 1) {
                            line = lines[i];
                        }
                    }
                    insert(deque, line, close);
                }
                insert(deque, deque.removeFirst(), close);

                //reinsert head
                if (!hasSolution) {
                    return;
                }

                convex = new ConvexHull(new ArrayList(deque));
            }

            private void insert(Deque<Line2D> deque, Line2D line, boolean close) {
                if (!hasSolution) {
                    return;
                }
                while (deque.size() >= 2) {
                    Line2D tail = deque.removeLast();
                    Point2D pt = tail.intersect(deque.peekLast());
                    if (pt == null) {
                        continue;
                    }
                    int side = line.whichSideIs(pt);
                    if (side > 0 || (close && side == 0)) {
                        deque.add(tail);
                        break;
                    }
                    if (line.onWhichSide(deque.peekLast()) != tail.onWhichSide(deque.peekLast())) {
                        hasSolution = false;
                    }

                }
                if (deque.size() == 1 && line.onWhichSide(deque.peekLast()) == 0) {
                    int side = deque.peekLast().whichSideIs(line.b);
                    if (!(side > 0 || (close && side == 0))) {
                        hasSolution = false;
                    }
                }

                deque.addLast(line);
            }
        }

        public static class Polygon<T> {
            List<T> points;

            private Polygon(List<T> points) {
                this.points = points;
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
