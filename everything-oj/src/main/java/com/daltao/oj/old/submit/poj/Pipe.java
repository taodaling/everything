package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/9.
 */
public class Pipe {
    private static final double INF = (int) 1e8;
    private static final double PREC = 1e-6;
    private static BlockReader input;

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Pipe.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    List<Vector2> upperList = new ArrayList();
    List<Vector2> lowerList = new ArrayList();

    public static void main(String[] args) {
        input = new BlockReader(System.in);

        int pointNum;
        while ((pointNum = input.nextInteger()) > 0) {
            Pipe pipe = new Pipe();
            pipe.init(pointNum);
            System.out.println(pipe.solve());
        }
    }

    public static Vector2 intersectPoint(Segment s1, Segment s2) {
        Vector2 p1 = s1.p1;
        Vector2 p2 = s1.p2;
        Vector2 p3 = s2.p1;
        Vector2 p4 = s2.p2;
        double a = p1.x - p2.x;
        double b = -(p3.x - p4.x);
        double c = p1.y - p2.y;
        double d = -(p3.y - p4.y);
        double e = p4.x - p2.x;
        double f = p4.y - p2.y;
        double t = (d * e - b * f) / (a * d - b * c);
        return new Vector2((p1.x - p2.x) * t + p2.x, (p1.y - p2.y) * t + p2.y);
    }

    public static double crossMul(Vector2 a, Vector2 b) {
        return a.x * b.y - a.y * b.x;
    }

    public static boolean isOnSegment(Vector2 v, Segment s) {
        return Math.abs(crossMul(s.p2.sub(s.p1), v.sub(s.p1))) < PREC;
    }

    /**
     * 判断直线a与线段b的位置关系，正数表示相交，负数表示无关系(或交于端点)，
     * 0表示重合。
     */
    public static int isCrossOrCover(Segment a, Segment b) {
        Vector2 v1 = a.p2.sub(a.p1);
        double cm1 = crossMul(v1, b.p1.sub(a.p1));
        double cm2 = crossMul(v1, b.p2.sub(a.p1));
        if (Math.abs(cm1) < PREC && Math.abs(cm2) < PREC) {
            return 0;
        }
        if (cm1 * cm2 < 0) {
            return 1;
        }
        return -1;
    }

    public void init(int pointNum) {
        for (int i = 0; i < pointNum; i++) {
            double x = Double.parseDouble(input.nextBlock());
            double y = Double.parseDouble(input.nextBlock());

            Vector2 upper = new Vector2(x, y);
            Vector2 lower = new Vector2(x, y - 1);

            upperList.add(upper);
            lowerList.add(lower);
        }
    }

    public String solve() {
        List<Segment> segmentList = new ArrayList();
        for (int i = 0, bound = upperList.size() - 1; i < bound; i++) {
            segmentList.add(new Segment(upperList.get(i), upperList.get(i + 1)));
            segmentList.add(new Segment(lowerList.get(i), lowerList.get(i + 1)));
        }

        Vector2 source2 = upperList.get(1);
        segmentList.add(new Segment(new Vector2(source2.x, source2.y),
                new Vector2(source2.x, INF)));
        segmentList.add(new Segment(new Vector2(source2.x, source2.y - 1),
                new Vector2(source2.x, -INF)));
        Vector2 source = upperList.get(0);
        double result = -INF;
        for (Vector2 upper : upperList) {
            for (Vector2 lower : lowerList) {
                if (upper.x == lower.x) {
                    continue;
                }
                Segment maybe = new Segment(upper, lower);
                Vector2 nearestInsection = new Vector2(INF, 0);
                for (Segment segment : segmentList) {
                    int posRelation = isCrossOrCover(maybe, segment);
                    if (posRelation >= 0) {
                        Vector2 pos;
                        if (posRelation > 0) {
                            pos = intersectPoint(maybe, segment);
                        } else {
                            pos = segment.p1;
                        }
                        if (pos.x < nearestInsection.x) {
                            nearestInsection = pos;
                        }
                    }
                }

                for (int i = 1, bound = lowerList.size() - 1; i < bound; i++) {
                    Vector2 vector = lowerList.get(i);
                    if (isOnSegment(vector, maybe) && isCrossOrCover(maybe, new Segment(lowerList.get(i - 1), lowerList.get(i + 1))) > 0) {
                        if (vector.x < nearestInsection.x) {
                            nearestInsection = vector;
                        }
                    }
                }

                for (int i = 1, bound = upperList.size() - 1; i < bound; i++) {
                    Vector2 vector = upperList.get(i);
                    if (isOnSegment(vector, maybe) && isCrossOrCover(maybe, new Segment(upperList.get(i - 1), upperList.get(i + 1))) > 0) {
                        if (vector.x < nearestInsection.x) {
                            nearestInsection = vector;
                        }
                    }
                }

                if (Math.abs(nearestInsection.x - INF) < PREC) {
                    return "Through all the pipe.";
                }
                result = Math.max(result, nearestInsection.x);
            }
        }

        return String.format("%.2f", result);
    }

    public static class Segment {
        Vector2 p1;
        Vector2 p2;

        public Segment(Vector2 p1, Vector2 p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public String toString() {
            return p1 + "-" + p2;
        }
    }

    public static class Vector2 {
        private double x;
        private double y;

        public Vector2(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Vector2 add(Vector2 b) {
            return new Vector2(x + b.x, y + b.y);
        }

        public Vector2 sub(Vector2 b) {
            return new Vector2(x - b.x, y - b.y);
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
