package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;

/**
 * Created by Administrator on 2018/2/15.
 */
public class POJ3130 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ3130.in"));
        }
        input = new BlockReader(System.in);

        while (true) {
            int n = input.nextInteger();
            if (n == 0) {
                break;
            }
            solve(n);
        }
    }

    public static void solve(int n) {
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = Point.getInstance(input.nextInteger(), input.nextInteger());
        }
        Line[] lines = new Line[n];
        for (int i = 1; i < n; i++) {
            lines[i] = Line.getInstance(points[i - 1], points[i]);
        }
        lines[0] = Line.getInstance(points[n - 1], points[0]);

        //Sort
        Comparator<Line> cmp = new Comparator<Line>() {
            @Override
            public int compare(Line a, Line b) {
                if (a.region != b.region) {
                    return a.region - b.region;
                }
                double c = cross(a.alpha, b.alpha);
                return c < 0 ? 1 : c > 0 ? -1 : 0;
            }
        };

        Arrays.sort(lines, cmp);
        //Remove duplicated lines
        int validLen = 0;
        for (int i = 1; i < n; i++) {
            if (cmp.compare(lines[validLen], lines[i]) == 0) {
                if (cross(lines[i], lines[validLen].end) < 0) {
                    lines[validLen] = lines[i];
                }
            } else {
                lines[++validLen] = lines[i];
            }
        }

        Deque<Line> deque = new ArrayDeque();
        deque.addLast(lines[0]);
        deque.addLast(lines[1]);
        for (int i = 2; i <= validLen; i++) {
            verifyTail(deque, lines[i]);
            verifyHead(deque, lines[i]);
            deque.addLast(lines[i]);
        }
        verifyTail(deque, deque.getFirst());
        verifyHead(deque, deque.getLast());

        if (deque.size() >= 3) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }

    public static void verifyHead(Deque<Line> que, Line line) {
        while (que.size() >= 2) {
            Line first = que.removeFirst();
            Line second = que.getFirst();

            Point inter = intersectAt(first, second);
            if (cross(line, inter) > 0) {
                que.addFirst(first);
                break;
            }
        }
    }

    public static void verifyTail(Deque<Line> que, Line line) {
        while (que.size() >= 2) {
            Line first = que.removeLast();
            Line second = que.getLast();

            Point inter = intersectAt(first, second);
            if (cross(line, inter) > 0) {
                que.addLast(first);
                break;
            }
        }
    }

    public static double cross(Point a, Point b) {
        return a.x * b.y - a.y * b.x;
    }

    public static Point intersectAt(Line l1, Line l2) {
        double a = l1.end.x - l1.begin.x;
        double b = -(l2.end.x - l2.begin.x);
        double c = l1.end.y - l1.begin.y;
        double d = -(l2.end.y - l2.begin.y);
        double e = l2.begin.x - l1.begin.x;
        double f = l2.begin.y - l1.begin.y;

        double k = (d * e - b * f) / (a * d - b * c);
        return Point.getInstance((1 - k) * l1.begin.x + k * l1.end.x, (1 - k) * l1.begin.y + k * l1.end.y);
    }

    public static double cross(Point ori, Point a, Point b) {
        return cross(a.sub(ori), b.sub(ori));
    }

    public static double cross(Line a, Point b) {
        return cross(a.begin, a.end, b);
    }

    public static class Point {
        double x, y;

        public static Point getInstance(double x, double y) {
            Point pt = new Point();
            pt.x = x;
            pt.y = y;
            return pt;
        }

        public Point sub(Point other) {
            return getInstance(x - other.x, y - other.y);
        }

        @Override
        public String toString() {
            return String.format("(%f, %f)", x, y);
        }
    }

    public static class Line {
        Point begin, end;
        Point alpha;
        int region;

        public static Line getInstance(Point begin, Point end) {
            Line line = new Line();
            line.begin = begin;
            line.end = end;
            line.alpha = Point.getInstance(-(end.y - begin.y), end.x - begin.x);

            if (line.alpha.y > 0) {
                line.region = line.alpha.x > 0 ? 1 : 2;
            } else if (line.alpha.y < 0) {
                line.region = line.alpha.x >= 0 ? 4 : 3;
            } else {
                line.region = line.alpha.x >= 0 ? 1 : 3;
            }

            return line;
        }

        @Override
        public String toString() {
            return begin + "->" + end + "|" + region;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
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

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }
}
