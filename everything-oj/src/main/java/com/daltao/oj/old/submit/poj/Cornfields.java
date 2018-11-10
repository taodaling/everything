package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/30.
 */
public class Cornfields {
    static final int INF = (int) 1e8;
    static final int MAX_KNIGHTS = 1000000;
    static BlockReader input;

    int n;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Cornfields.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            Cornfields solution = new Cornfields();
            solution.init();
            System.out.print(solution.solve());
        }
    }


    public void init() {
    }


    public String solve() {
        StringBuilder builder = new StringBuilder();
        int size = input.nextInteger();
        int width = input.nextInteger();
        int questionNum = input.nextInteger();
        int[][] map = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = input.nextInteger();
            }
        }

        IntervalTree2D tree = new IntervalTree2D(map);
        for (int i = 0; i < questionNum; i++) {
            int b = input.nextInteger() - 1;
            int l = input.nextInteger() - 1;

            int r = l + width - 1;
            int t = b + width - 1;
            int[] maxAndMin = tree.query(l, r, b, t);
            builder.append(maxAndMin[0] - maxAndMin[1]).append('\n');
        }
        return builder.toString();
    }

    public static class IntervalTree2D {
        int leftBound;
        int rightBound;
        int topBound;
        int bottomBound;
        Interval root;

        public IntervalTree2D(int[][] map) {
            int maxSize = Math.max(map.length, map[0].length);
            int properSize = maxSize;
            int v;
            while (properSize != (v = properSize & -properSize)) {
                properSize -= v;
            }
            if (properSize < maxSize) {
                properSize <<= 1;
            }

            this.leftBound = 0;
            this.rightBound = properSize - 1;
            this.topBound = properSize - 1;
            this.bottomBound = 0;
            root = buildTree(leftBound, rightBound, bottomBound, topBound, map);
        }

        public int[] query(int l, int r, int b, int t) {
            int[] res = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
            query(l, r, b, t, leftBound, rightBound, bottomBound, topBound, root, res);
            return res;
        }

        private void query(int l, int r, int b, int t, int cl, int cr, int cb, int ct, Interval interval, int[] maxAndMin) {
            if (cl >= l && cr <= r && cb >= b && ct <= t) {
                maxAndMin[0] = Math.max(maxAndMin[0], interval.max);
                maxAndMin[1] = Math.min(maxAndMin[1], interval.min);
                return;
            }
            if (r < cl || t < cb || l > cr || b > ct) {
                return;
            }
            int lrc = (cl + cr) >> 1;
            int btc = (cb + ct) >> 1;
            query(l, r, b, t, cl, lrc, cb, btc, interval.lb, maxAndMin);
            query(l, r, b, t, lrc + 1, cr, cb, btc, interval.rb, maxAndMin);
            query(l, r, b, t, cl, lrc, btc + 1, ct, interval.lt, maxAndMin);
            query(l, r, b, t, lrc + 1, cr, btc + 1, ct, interval.rt, maxAndMin);
        }

        public Interval buildTree(int left, int right, int bottom, int top, int[][] map) {
            Interval interval = new Interval();
            if (left == right) {
                if (map.length <= bottom || map[bottom].length <= left) {
                    interval.min = Integer.MAX_VALUE;
                    interval.max = Integer.MIN_VALUE;
                } else {
                    interval.min = interval.max = map[bottom][left];
                }
                return interval;
            }
            int lrc = (left + right) >> 1;
            int btc = (bottom + top) >> 1;
            interval.lb = buildTree(left, lrc, bottom, btc, map);
            interval.lt = buildTree(left, lrc, btc + 1, top, map);
            interval.rb = buildTree(lrc + 1, right, bottom, btc, map);
            interval.rt = buildTree(lrc + 1, right, btc + 1, top, map);
            interval.min = Math.min(interval.lb.min, interval.lt.min);
            interval.min = Math.min(interval.min, interval.rb.min);
            interval.min = Math.min(interval.min, interval.rt.min);
            interval.max = Math.max(interval.lb.max, interval.lt.max);
            interval.max = Math.max(interval.max, interval.rb.max);
            interval.max = Math.max(interval.max, interval.rt.max);
            return interval;
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = bottomBound; i <= topBound; i++) {
                for (int j = leftBound; j <= rightBound; j++) {
                    s += query(j, j, i, i)[0] + ", ";
                }
                s += "\n";
            }
            return s;
        }

        private static class Interval {
            Interval rb;
            Interval rt;
            Interval lb;
            Interval lt;
            int min;
            int max;
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
