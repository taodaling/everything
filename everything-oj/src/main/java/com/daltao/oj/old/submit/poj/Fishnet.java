package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/10.
 */
public class Fishnet {
    static final double PREC = 1e-6;
    static BlockReader input;

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Fishnet.in"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        input = new BlockReader(System.in);
    }

    List<Segment> horizontalLineList;
    List<Segment> verticalLineList;

    public static void main(String[] args) {
        int n;
        while ((n = input.nextInteger()) != 0) {
            Fishnet fishnet = new Fishnet();
            fishnet.init(n);
            System.out.println(String.format("%.6f", fishnet.solve()));
        }
    }

    public static double area(Vector2 v1, Vector2 v2, Vector2 v3, Vector2 v4) {
        return area(v1, v2, v3) + area(v3, v4, v1);
    }

    public static double area(Vector2 v1, Vector2 v2, Vector2 v3) {
        return Math.abs(cmul(v3.sub(v1), v2.sub(v1))) / 2.0;
    }

    public static double cmul(Vector2 a, Vector2 b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    public static Vector2 intersectAt(Segment s1, Segment s2) {
        double a = s1.p1.getX() - s1.p2.getX();
        double b = -(s2.p1.getX() - s2.p2.getX());
        double c = s1.p1.getY() - s1.p2.getY();
        double d = -(s2.p1.getY() - s2.p2.getY());
        double e = s2.p2.getX() - s1.p2.getX();
        double f = s2.p2.getY() - s1.p2.getY();
        double t = (d * e - b * f) / (a * d - b * c);
        return new Vector2((s1.p1.getX() - s1.p2.getX()) * t + s1.p2.getX(), (s1.p1.getY() - s1.p2.getY()) * t + s1.p2.getY());
    }

    public void init(int n) {
        double[] bottomXAxis = new double[n + 2];
        double[] topXAxis = new double[n + 2];
        bottomXAxis[0] = topXAxis[0] = 0;
        bottomXAxis[n + 1] = topXAxis[n + 1] = 1;
        for (int i = 1; i <= n; i++) {
            bottomXAxis[i] = Double.parseDouble(input.nextBlock());
        }
        for (int i = 1; i <= n; i++) {
            topXAxis[i] = Double.parseDouble(input.nextBlock());
        }

        verticalLineList = new ArrayList(n + 2);
        for (int i = 0, bound = n + 2; i < bound; i++) {
            verticalLineList.add(new Segment(
                    new Vector2(topXAxis[i], 1),
                    new Vector2(bottomXAxis[i], 0)
            ));
        }


        double[] leftYAxis = new double[n + 2];
        double[] rightYAxis = new double[n + 2];
        leftYAxis[0] = rightYAxis[0] = 0;
        leftYAxis[n + 1] = rightYAxis[n + 1] = 1;
        for (int i = 1; i <= n; i++) {
            leftYAxis[i] = Double.parseDouble(input.nextBlock());
        }
        for (int i = 1; i <= n; i++) {
            rightYAxis[i] = Double.parseDouble(input.nextBlock());
        }
        horizontalLineList = new ArrayList(n + 2);
        for (int i = 0, bound = n + 2; i < bound; i++) {
            horizontalLineList.add(new Segment(
                    new Vector2(1, rightYAxis[i]),
                    new Vector2(0, leftYAxis[i])
            ));
        }
    }

    public double solve() {
        Vector2[] lastLine = new Vector2[verticalLineList.size()];
        Vector2[] nextLine = new Vector2[verticalLineList.size()];
        for (int i = 0, bound = lastLine.length; i < bound; i++) {
            lastLine[i] = verticalLineList.get(i).getP2();
        }

        double ret = 0;
        for (int i = 1, ibound = horizontalLineList.size(); i < ibound; i++) {
            Segment horionzontalLine = horizontalLineList.get(i);
            nextLine[0] = horionzontalLine.getP2();
            for (int j = 1, jbound = verticalLineList.size(); j < jbound; j++) {
                Segment verticalLine = verticalLineList.get(j);
                nextLine[j] = intersectAt(horionzontalLine, verticalLine);
                double area = area(nextLine[j - 1], nextLine[j], lastLine[j], lastLine[j - 1]);
                ret = Math.max(area, ret);
            }
            Vector2[] tmp = lastLine;
            lastLine = nextLine;
            nextLine = tmp;
        }

        return ret;
    }

    public static class Segment {
        private Vector2 p1;
        private Vector2 p2;

        public Segment(Vector2 p1, Vector2 p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public Vector2 getP1() {
            return p1;
        }

        public Vector2 getP2() {
            return p2;
        }


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

        public Vector2 mul(double c) {
            return new Vector2(c * x, c * y);
        }

        public Vector2 div(double c) {
            return new Vector2(x / c, y / c);
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
