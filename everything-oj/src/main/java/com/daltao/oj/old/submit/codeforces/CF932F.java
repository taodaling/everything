package com.daltao.oj.old.submit.codeforces;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by dalt on 2018/3/20.
 */
public class CF932F {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static double INF = 1e30;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\CF932F.in"));
        }
        input = new BlockReader(System.in);

        int n = input.nextInteger();
        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].a = input.nextInteger();
        }

        for (int i = 1; i <= n; i++) {
            nodes[i].b = input.nextInteger();
        }

        for (int i = 1; i < n; i++) {
            Node a = nodes[input.nextInteger()];
            Node b = nodes[input.nextInteger()];
            a.nodeList.add(b);
            b.nodeList.add(a);
        }

        makeDp(nodes[1], null);

        StringBuilder builder = new StringBuilder(1 << 20);
        for (int i = 1; i <= n; i++) {
            builder.append(nodes[i].dp).append('\n');
        }

        System.out.print(builder);
    }

    public static ConvexHull makeDp(Node node, Node father) {
        node.nodeList.remove(father);

        ConvexHull convexHull;
        if (node.nodeList.size() == 0) {
            convexHull = new ConvexHull();
        } else {
            convexHull = makeDp(node.nodeList.get(0), node);
        }

        for (int i = 1, bound = node.nodeList.size(); i < bound; i++) {
            convexHull = combine(convexHull, makeDp(node.nodeList.get(i), node));
        }

        if (!convexHull.lineSet.isEmpty()) {
            node.dp = lowY(convexHull, node.a);
        }

        Line line = new Line();
        line.k = node.b;
        line.b = node.dp;

        addLine(convexHull, line);
        return convexHull;
    }

    public static ConvexHull combine(ConvexHull a, ConvexHull b) {
        if (a.lineSet.size() >= b.lineSet.size()) {
            combine0(a, b);
            return a;
        } else {
            combine0(b, a);
            return b;
        }
    }

    public static void combine0(ConvexHull a, ConvexHull b) {
        for (Line line : b.lineSet) {
            addLine(a, line);
        }
    }

    public static void addLine(ConvexHull convexHull, Line line) {
        TreeSet<Line> set = convexHull.lineSet;

        //Remove the last element
        Line sameK = set.floor(line);
        if (sameK != null && sameK.k == line.k) {
            if (sameK.b < line.b) {
                return;
            }
            convexHull.remove(sameK);
        }

        removeFront(set.headSet(line, false).descendingSet(), convexHull.intervalSet, line);
        removeFront(set.tailSet(line, false), convexHull.intervalSet, line);

        Line floor = set.floor(line);
        Line ceil = set.ceiling(line);
        if (floor != null && ceil != null && outOfRange(line, ceil, floor)) {
            return;
        }

        convexHull.add(line);
    }

    //determine whether the intersection of a and b is out of range built by b and c
    public static boolean outOfRange(Line a, Line b, Line c) {
        double x = Line.intersectAt(a, b);
        double y = a.getY(x);
        return y >= c.getY(x);
    }

    public static void removeFront(NavigableSet<Line> set, NavigableSet<Line> intervalNavigableSet, Line line) {
        while (set.size() >= 2) {
            Line first = set.first();
            Line second = set.higher(first);
            if (!outOfRange(first, second, line)) {
                break;
            } else {
                set.remove(first);
                intervalNavigableSet.remove(first);
            }
        }
    }

    public static long lowY(ConvexHull convexHull, long x) {
        Line line = new Line();
        line.left = x;

        line = convexHull.intervalSet.floor(line);
        return line.getY(x);
    }

    public static class ConvexHull {
        TreeSet<Line> lineSet = new TreeSet<>();
        TreeSet<Line> intervalSet = new TreeSet<>(Line.LEFT_COMP);

        public void remove(Line line) {
            lineSet.remove(line);
            intervalSet.remove(line);
        }

        public void add(Line line) {
            Line former = lineSet.floor(line);
            if (former == null) {
                line.left = -INF;
            } else {
                line.left = Line.intersectAt(line, former);
            }

            Line later = lineSet.ceiling(line);
            if (later == null) {
            } else {
                later.left = Line.intersectAt(line, later);
            }

            lineSet.add(line);
            intervalSet.add(line);
        }
    }

    public static class Node {
        List<Node> nodeList = new ArrayList<>(1);
        long dp;
        int a;
        int b;
    }


    public static class Line implements Comparable<Line> {
        public static final Comparator<Line> LEFT_COMP = new Comparator<Line>() {
            @Override
            public int compare(Line o1, Line o2) {
                return Double.compare(o1.left, o2.left);
            }
        };
        int k;
        long b;
        double left;

        public static double intersectAt(Line a, Line b) {
            return (double) (b.b - a.b) / (a.k - b.k);
        }

        public long getY(long x) {
            return k * x + b;
        }

        public double getY(double x) {
            return k * x + b;
        }

        @Override
        public int compareTo(Line o) {
            return o.k - k;
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

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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
    }
}