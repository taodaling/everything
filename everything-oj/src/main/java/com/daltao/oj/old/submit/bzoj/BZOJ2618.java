package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by dalt on 2018/3/1.
 */
public class BZOJ2618 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2618.in"));
        }

        input = new BlockReader(System.in);

        solve();
    }

    public static void solve() {
        int g = input.nextInteger();
        List<Line> lineList = new ArrayList();
        for (int i = 0; i < g; i++) {
            int v = input.nextInteger();
            Point start = new Point(input.nextInteger(), input.nextInteger());
            Point end = start;
            for (int j = 1; j < v; j++) {
                Point next = new Point(input.nextInteger(), input.nextInteger());
                lineList.add(new Line(end, next));
                end = next;
            }
            lineList.add(new Line(end, start));
        }


        Comparator<Line> cmp = new Comparator<Line>() {
            @Override
            public int compare(Line a, Line b) {
                if (a.alpha.y > 0 && b.alpha.y < 0) {
                    return -1;
                }
                if (a.alpha.y < 0 && b.alpha.y > 0) {
                    return 1;
                }
                if (a.alpha.y == 0 && b.alpha.y == 0) {
                    return a.alpha.x < 0 && b.alpha.x > 0 ? 1 : a.alpha.x > 0 && b.alpha.x < 0 ? -1 : 0;
                }
                double r = cmul(a.alpha, b.alpha);
                return r < 0 ? -1 : r > 0 ? 1 : 0;
            }
        };
        //Sort by angle
        Line[] aLotOfLines = lineList.toArray(new Line[0]);
        Arrays.sort(aLotOfLines, cmp);
        int lastIndex = 0;
        for (int i = 1, bound = aLotOfLines.length; i < bound; i++) {
            if (cmp.compare(aLotOfLines[lastIndex], aLotOfLines[i]) == 0) {
                if (relationBetween(aLotOfLines[lastIndex].src, aLotOfLines[lastIndex].dst, aLotOfLines[i].dst) > 0) {
                    aLotOfLines[lastIndex] = aLotOfLines[i];
                }
            } else {
                aLotOfLines[++lastIndex] = aLotOfLines[i];
            }
        }

        Deque<Line> deque = new ArrayDeque<Line>();
        deque.addLast(aLotOfLines[0]);
        deque.addLast(aLotOfLines[1]);
        for (int i = 2; i <= lastIndex; i++) {
            removeFirst(deque, aLotOfLines[i]);
            removeLast(deque, aLotOfLines[i]);
            deque.addLast(aLotOfLines[i]);
        }
        Line first = deque.removeFirst();
        removeLast(deque, first);
        deque.addLast(first);

        Line[] lines = deque.toArray(new Line[deque.size()]);
        List<Point> pointList = new ArrayList();
        for (int i = 0, bound = lines.length - 1; i < bound; i++) {
            pointList.add(Line.intersectAt(lines[i], lines[i + 1]));
        }
        pointList.add(Line.intersectAt(lines[0], lines[lines.length - 1]));

        Point[] points = pointList.toArray(new Point[0]);
        int lowestRightest = 0;
        for (int i = 1, bound = points.length; i < bound; i++) {
            if (points[lowestRightest].y > points[i].y) {
                lowestRightest = i;
            } else if (points[lowestRightest].y == points[i].y && points[lowestRightest].x > points[i].x) {
                lowestRightest = i;
            }
        }
        final Point lowestRightestPoint = points[lowestRightest];
        points[lowestRightest] = points[0];
        points[0] = lowestRightestPoint;

        Arrays.sort(points, 1, points.length, new Comparator<Point>() {
            @Override
            public int compare(Point a, Point b) {
                double r = relationBetween(lowestRightestPoint, b, a);
                return r > 0 ? 1 : r < 0 ? -1 : 0;
            }
        });

        double area = 0;
        for (int i = 2, bound = points.length; i < bound; i++) {
            area += relationBetween(lowestRightestPoint, points[i - 1], points[i]);
        }

        System.out.println(String.format("%.3f", area / 2));
    }

    public static void removeFirst(Deque<Line> deque, Line line) {
        while (deque.size() >= 2) {
            Line first = deque.removeFirst();
            Line first2 = deque.getFirst();

            Point intersect = Line.intersectAt(first, first2);
            if (relationBetween(line.src, line.dst, intersect) >= 0) {
                deque.addFirst(first);
                break;
            }
        }
    }

    public static void removeLast(Deque<Line> deque, Line line) {
        while (deque.size() >= 2) {
            Line last = deque.removeLast();
            Line last2 = deque.getLast();

            Point intersect = Line.intersectAt(last, last2);
            if (relationBetween(line.src, line.dst, intersect) >= 0) {
                deque.addLast(last);
                break;
            }
        }
    }

    //a x b
    public static double cmul(Point a, Point b) {
        return a.x * b.y - a.y * b.x;
    }

    //calculate the relation between vector ab and ac
    public static double relationBetween(Point a, Point b, Point c) {
        return cmul(new Point(b.x - a.x, b.y - a.y), new Point(c.x - a.x, c.y - a.y));
    }

    public static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Point sub(Point other) {
            return new Point(x - other.x, y - other.y);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    public static class Line {
        Point src;
        Point dst;
        Point alpha;

        public Line(Point src, Point dst) {
            this.src = src;
            this.dst = dst;
            alpha = new Point(src.y - dst.y, dst.x - src.x);
        }

        public static Point intersectAt(Line x, Line y) {
            double a = x.dst.x - x.src.x;
            double b = -(y.dst.x - y.src.x);
            double c = x.dst.y - x.src.y;
            double d = -(y.dst.y - y.src.y);
            double e = y.src.x - x.src.x;
            double f = y.src.y - x.src.y;

            double base = a * d - b * c;
            double k = (d * e - b * f) / base;
            return new Point((1 - k) * x.src.x + k * x.dst.x, (1 - k) * x.src.y + k * x.dst.y);
        }

        @Override
        public String toString() {
            return src + "->" + dst;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }
    }
}