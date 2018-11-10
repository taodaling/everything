package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Administrator on 2017/12/10.
 */
public class BeautyContest2 {
    static final double PREC = 1e-6;
    static BlockReader input;

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\BeautyContest.in"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        input = new BlockReader(System.in);
    }

    List<Vector2> allFarmPositions;

    public static void main(String[] args) {
        while (input.hasMore()) {
            BeautyContest2 beautyContest = new BeautyContest2();
            beautyContest.init();
            System.out.println(beautyContest.solve());
        }
    }

    public static long dist2(Vector2 v1, Vector2 v2) {
        long xo = v1.x - v2.x;
        long yo = v1.y - v2.y;
        return xo * xo + yo * yo;
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

    public void init() {
        int n = input.nextInteger();
        allFarmPositions = new ArrayList(n);
        for (int i = 0; i < n; i++) {
            allFarmPositions.add(new Vector2(
                    input.nextInteger(), input.nextInteger()
            ));
        }
    }

    /**
     * Graham扫描算法，结果集合按逆时针排序
     */
    public List<Vector2> convexHull(List<Vector2> pointSet) {
        Vector2 base = pointSet.get(0);
        //Find the lowest and leftest point
        for (Vector2 vec : pointSet) {
            if (vec.y <= base.y) {
                if (vec.y < base.y || vec.x < base.x) {
                    base = vec;
                }
            }
        }

        {
            List<Pair<Double, Vector2>> angleList = new ArrayList(pointSet.size() - 1);
            for (Vector2 vec : pointSet) {
                if (vec.x == base.x && vec.y == base.y) {
                    continue;
                }
                double xdiff = vec.x - base.x;
                double ydiff = vec.y - base.y;
                double cos2 = ((double) xdiff) * xdiff / (xdiff * xdiff + ydiff * ydiff);
                if (xdiff < 0) {
                    cos2 = -cos2;
                }

                angleList.add(new Pair<Double, Vector2>(cos2, vec));
            }

            Collections.sort(angleList, new Comparator<Pair<Double, Vector2>>() {
                public int compare(Pair<Double, Vector2> o1, Pair<Double, Vector2> o2) {
                    return -o1.e1.compareTo(o2.e1);
                }
            });

            //Remove the same vector with the same angle
            pointSet = new ArrayList<Vector2>();
            Pair<Double, Vector2> lastOne = new Pair(-1D, base);
            for (Pair<Double, Vector2> entry : angleList) {
                if (Math.abs(lastOne.e1 - entry.e1) >= PREC) {
                    pointSet.add(lastOne.e2);
                    lastOne = entry;
                } else {
                    if (dist2(base, entry.e2) > dist2(base, lastOne.e2)) {
                        lastOne = entry;
                    }
                }
            }
            pointSet.add(lastOne.e2);
        }

        LinkedList<Vector2> stack = new LinkedList();
        stack.addLast(pointSet.get(0));
        stack.addLast(pointSet.get(1));
        for (int i = 2, bound = pointSet.size(); i < bound; i++) {
            Vector2 vec = pointSet.get(i);
            while (stack.size() > 1) {
                Vector2 top1 = stack.removeLast();
                Vector2 top2 = stack.getLast();
                if (cmul(top1.sub(top2), vec.sub(top1)) > 0) {
                    stack.addLast(top1);
                    break;
                }
            }
            stack.addLast(vec);
        }

        return stack;
    }

    /**
     * Shamos算法，结果集合为踵点对
     */
    public List<Pair<Vector2, Vector2>> shamos(List<Vector2> vectorList) {
        List<Pair<Vector2, Vector2>> result = new ArrayList();
        vectorList = new ArrayList(vectorList);
        int n = vectorList.size();
        vectorList.add(vectorList.get(0));
        int vectorScanIndex = 2;
        int segmentScanIndex = 0;
        for (int bound = vectorList.size() - 1; segmentScanIndex < bound; segmentScanIndex++) {
            Vector2 v1 = vectorList.get(segmentScanIndex);
            Vector2 v2 = vectorList.get(segmentScanIndex + 1);
            while (true) {
                Vector2 scanVector = vectorList.get(vectorScanIndex % n);
                Vector2 nextScannedVector = vectorList.get((vectorScanIndex + 1) % n);

                double diff = area(v1, v2, scanVector) - area(v1, v2, nextScannedVector);
                if (diff >= -PREC) {
                    result.add(new Pair<Vector2, Vector2>(v1, scanVector));
                    result.add(new Pair<Vector2, Vector2>(v2, scanVector));

                    //System.out.println(v1 + "-" + v2 + ":" + scanVector);
                    if (Math.abs(diff) < PREC) {
                        result.add(new Pair<Vector2, Vector2>(v1, nextScannedVector));
                        result.add(new Pair<Vector2, Vector2>(v2, nextScannedVector));
                        //System.out.println(v1 + "-" + v2 + ":" + nextScannedVector);
                    }
                    break;
                } else {
                    vectorScanIndex = (vectorScanIndex + 1) % n;
                }
            }
        }

        return result;
    }

    public long castToInt(double v) {
        long t = (int) v;
        if (v - t > PREC) {
            t++;
        }
        return t;
    }

    public long solve() {
        long res = 0;
        for (Vector2 v1 : this.allFarmPositions)
        {
            for (Vector2 v2 : this.allFarmPositions)
                res = Math.max(res, dist2(v1, v2));
        }

        return res;
    }

    public static class Pair<T1, T2> {
        T1 e1;
        T2 e2;

        public Pair(T1 e1, T2 e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        @Override
        public String toString() {
            return "(" + e1 + "," + e2 + ")";
        }
    }

    public static class Vector2 {
        private long x;
        private long y;

        public Vector2(long x, long y) {
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
