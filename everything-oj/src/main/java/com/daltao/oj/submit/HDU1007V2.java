package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class HDU1007V2 {
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
            int n = io.readInt();
            GeometryUtils.Point2D[] points = new GeometryUtils.Point2D[n];
            for (int i = 0; i < n; i++) {
                points[i] = new GeometryUtils.Point2D(io.readDouble(), io.readDouble());
            }
            GeometryUtils.Segment2D nearest =
                    new GeometryUtils.PointPolygon(Arrays.asList(points))
                            .theNearestPointPair();
            io.cache.append(String.format("%.4f", nearest.a.distanceBetween(nearest.b)));

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
    }

    public static class GeometryUtils {
        private static final double PREC = 1e-8;
        private static final double INF = 1e30;

        public static double valueOf(double x) {
            return x > -PREC && x < PREC ? 0 : x;
        }

        public static double pow2(double x) {
            return x * x;
        }
        public static class Point2D {
            final double x;
            final double y;
            static final Point2D ORIGIN = new Point2D(0, 0);
            static final Comparator<Point2D> SORT_BY_X_AND_Y = new Comparator<Point2D>() {
                @Override
                public int compare(Point2D a, Point2D b) {
                    return a.x == b.x ? Double.compare(a.y, b.y) : Double.compare(a.x, b.x);
                }
            };
            static final Comparator<Point2D> SORT_BY_Y_AND_X = new Comparator<Point2D>() {
                @Override
                public int compare(Point2D a, Point2D b) {
                    return a.y == b.y ? Double.compare(a.x, b.x) : Double.compare(a.y, b.y);
                }
            };

            public Point2D(double x, double y) {
                this.x = x;
                this.y = y;
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

            public Point2D add(Point2D vector) {
                return new Point2D(x + vector.x, y + vector.y);
            }

            public Point2D add(Point2D vector, double times) {
                return new Point2D(x + vector.x * times, y + vector.y * times);
            }

            @Override
            public String toString() {
                return String.format("(%f, %f)", x, y);
            }

            public static double cross(Point2D a, Point2D b, Point2D c, Point2D d) {
                return GeometryUtils.cross(b.x - a.x, b.y - a.y, d.x - c.x, d.y - c.y);
            }

            @Override
            public int hashCode() {
                return (int) (Double.doubleToLongBits(x) * 31 + Double.doubleToLongBits(y));
            }

            @Override
            public boolean equals(Object obj) {
                Point2D other = (Point2D) obj;
                return x == other.x && y == other.y;
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

            public Line2D moveAlong(Point2D vector) {
                return new Line2D(a.add(vector), b.add(vector));
            }

            public Line2D moveAlong(Point2D vector, double times) {
                return new Line2D(a.add(vector, times), b.add(vector, times));
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

        public static class LinePolygon extends Polygon<Line2D> {
            public LinePolygon(List<Line2D> points) {
                super(points);
            }
        }

        public static class LineConvexHull extends LinePolygon {
            public LineConvexHull(List<Line2D> points) {
                super(points);
            }
        }

        public static class PointConvexHull extends PointPolygon {
            public PointConvexHull(List<Point2D> points) {
                super(points);
            }
        }


        public static class PointPolygon extends Polygon<Point2D> {
            public PointPolygon(List<Point2D> points) {
                super(points);
            }

            public LinePolygon asLines() {
                int n = data.size();
                Line2D[] lines = new Line2D[n];
                lines[0] = new Line2D(data.get(n - 1), data.get(0));
                for (int i = 1; i < n; i++) {
                    lines[i] = new Line2D(data.get(i - 1), data.get(i));
                }
                return new LinePolygon(Arrays.asList(lines));
            }

            Point2D theNearestPointX;
            Point2D theNearestPointY;
            double nearestDistance2;

            public Segment2D theNearestPointPair() {
                //最近点对，分而治之
                int n = data.size();
                pointOrderByX = data.toArray(new Point2D[n]);
                Arrays.sort(pointOrderByX, Point2D.SORT_BY_X_AND_Y);
                pointOrderByY = data.toArray(new Point2D[n]);
                Arrays.sort(pointOrderByY, Point2D.SORT_BY_Y_AND_X);
                buf = new Point2D[4 * n];
                nearestDistance2 = INF;
                theNearestPointPair(0, n, 0);
                return new Segment2D(theNearestPointX, theNearestPointY);
            }

            private void theNearestPointPair(Point2D a, Point2D b) {
                double d = a.distance2Between(b);
                if (d < nearestDistance2) {
                    nearestDistance2 = d;
                    theNearestPointX = a;
                    theNearestPointY = b;
                }
            }

            private Point2D[] buf;
            private Point2D[] pointOrderByX;
            Point2D[] pointOrderByY;

            public void theNearestPointPair(int from, int to, int bufFrom) {
                if (to - from <= 3) {
                    for (int i = from; i < to; i++) {
                        for (int j = i + 1; j < to; j++) {
                            theNearestPointPair(pointOrderByY[i], pointOrderByY[j]);
                        }
                    }
                    return;
                }

                int bufTo = bufFrom + to - from;
                System.arraycopy(pointOrderByY, from, buf, bufFrom, to - from);

                int m = (from + to) >> 1;
                int lwpos = from;
                int rwpos = m;
                for (int i = bufFrom; i < bufTo; i++) {
                    if (Point2D.SORT_BY_X_AND_Y.compare(buf[i], pointOrderByX[m]) < 0) {
                        pointOrderByY[lwpos++] = buf[i];
                    } else {
                        pointOrderByY[rwpos++] = buf[i];
                    }
                }
                theNearestPointPair(from, m, bufTo);
                theNearestPointPair(m, to, bufTo);

                lwpos = from;
                rwpos = m;
                for (int i = bufFrom; i < bufTo; i++) {
                    double dx2 = pow2(buf[i].x - pointOrderByX[m].x);
                    if (Point2D.SORT_BY_X_AND_Y.compare(buf[i], pointOrderByX[m]) < 0) {
                        if (nearestDistance2 > dx2) {
                            pointOrderByY[lwpos++] = buf[i];
                        }
                    } else {
                        if (nearestDistance2 < dx2) {
                            pointOrderByY[rwpos++] = buf[i];
                        }
                    }
                }

                for (int i = from, j = m; i < lwpos; i++) {
                    int k = j - 1;
                    while (j < rwpos && pointOrderByY[j].y < pointOrderByY[i].y) {
                        j++;
                    }
                    while (k >= m && pow2(pointOrderByY[k].y - pointOrderByY[i].y) < nearestDistance2) {
                        theNearestPointPair(pointOrderByY[i], pointOrderByY[k]);
                        k--;
                    }
                    k = j;
                    while (k < rwpos && pow2(pointOrderByY[k].y - pointOrderByY[i].y) < nearestDistance2) {
                        theNearestPointPair(pointOrderByY[i], pointOrderByY[k]);
                        k++;
                    }
                }
            }
        }

        public static class Polygon<T> {
            List<T> data;

            private Polygon(List<T> data) {
                this.data = data;
            }
        }
    }
}
