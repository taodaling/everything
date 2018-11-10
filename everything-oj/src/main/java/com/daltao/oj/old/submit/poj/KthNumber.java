package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dalt on 2017/12/28.
 */
public class KthNumber {
    static BlockReader input;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\test\\poj\\KthNumber.in"));

        input = new BlockReader(System.in);
        KthNumber solution = new KthNumber();
        for (int i = 1; input.hasMore(); i++) {
            solution.init();
            System.out.print(solution.solve());
        }

    }

    static final int INF = (int) 1e8;
    int n;
    int m;
    static int[] data = new int[100000];
    static int[][] questions = new int[5000][3];
    static OrderedMap map = new OrderedMap(100000);
    static ChairTree tree = new ChairTree(0, 100000);

    public void init() {
        n = input.nextInteger();
        m = input.nextInteger();
        for (int i = 0; i < n; i++) {
            data[i] = input.nextInteger();
        }
        for (int i = 0; i < m; i++) {
            questions[i][0] = input.nextInteger();
            questions[i][1] = input.nextInteger();
            questions[i][2] = input.nextInteger();
        }
    }


    public String solve() {
        StringBuilder result = new StringBuilder();

        map.reset(data, n);
        tree.reset(map.getMinRank(), map.getMaxRank());

        for (int i = 0; i < n; i++) {
            int rank = map.ranks[i];
            tree.update(rank, rank, 1);
        }
        for (int i = 0; i < m; i++) {
            int v1 = questions[i][0] - 1;
            int v2 = questions[i][1];
            //Binary search
            int right = map.getMaxRank();
            int left = map.getMinRank();
            int target = questions[i][2];
            while (left < right) {
                int half = (left + right) >> 1;
                int halfValue = tree.sumOf(v2, left, half) - tree.sumOf(v1, left, half);
                if (halfValue >= target) {
                    right = half;
                } else {
                    target -= halfValue;
                    left = half + 1;
                }
            }
            int value = data[map.orders[left]];
            result.append(value).append('\n');
        }
        return result.toString();
    }

    public static class OrderedMap {
        int[] data;
        int[] orders;
        int[] ranks;
        int size;
        Integer[] ordersWrapper;

        public OrderedMap(int cap) {
            orders = new int[cap];
            ranks = new int[cap];
            ordersWrapper = new Integer[cap];
        }

        public void reset(final int[] data, int n) {
            this.data = data;
            this.size = n;
            for (int i = 0; i < n; i++) {
                ordersWrapper[i] = i;
            }
            Arrays.sort(ordersWrapper, 0, n, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return data[o1] - data[o2];
                }
            });
            for (int i = 0; i < n; i++) {
                orders[i] = ordersWrapper[i];
            }

            int cnt = 0;
            ranks[orders[0]] = 0;
            for (int i = 1; i < n; i++) {
                if (data[orders[i]] != data[orders[i - 1]]) {
                    cnt = i;
                }
                ranks[orders[i]] = cnt;
            }
        }

        public int getMinRank() {
            return 0;
        }

        public int getMaxRank() {
            return ranks[orders[size - 1]];
        }
    }


    public static class BlockReader {
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        static final int EOF = -1;

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        StringBuilder builder = new StringBuilder();

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

        public BlockReader(InputStream is) {
            this(is, 4096);
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
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }

    private static interface Supplier<T> {
        public T get();
    }


    public static ChairTree.Interval[] queue = new ChairTree.Interval[1000000];
    public static int queue_head = 0;

    static {
        for (int i = 0; i < 1000000; i++) {
            queue[i] = new ChairTree.Interval();
        }
    }

    public static class ChairTree {

        List<Interval> history = new ArrayList();
        Interval original;
        int originalLeft;
        int originalRight;


        public void reset(int leftBound, int rightBound) {
            queue_head = 0;

            history.clear();
            Interval proper = original;
            int left = originalLeft;
            int right = originalRight;
            int center = (left + right) >> 1;
            while (left != right && center >= rightBound) {
                proper = proper.leftHalf;
                right = center;
                center = (left + right) >> 1;
            }
            while (left != right && center < leftBound) {
                proper = proper.rightHalf;
                left = center + 1;
                center = (left + right) >> 1;
            }
            history.add(proper);
            this.leftBound = left;
            this.rightBound = right;
        }

        public static class Interval {
            int sum;
            int dirty;
            Interval leftHalf;
            Interval rightHalf;
//            int left;
//            int right;

            public void copy(Interval interval) {
                sum = interval.sum;
                dirty = interval.dirty;
                leftHalf = interval.leftHalf;
                rightHalf = interval.rightHalf;
//                left = interval.left;
//                right = interval.right;
            }
//
//            @Override
//            public String toString() {
//                return "[" + left + "," + right + "]";
//            }
        }

        int leftBound;
        int rightBound;

        public ChairTree(int left, int right) {
            this.leftBound = left;
            this.rightBound = right;
            history.add(buildTree(left, right));
            original = history.get(0);
            originalLeft = left;
            originalRight = right;
        }

        public void update(int from, int to, int val) {
            Interval last = history.get(history.size() - 1);
            if (from > rightBound || to < leftBound) {
                history.add(last);
            }
            history.add(update(last, leftBound, rightBound, from, to, val));
        }

        private Interval update(Interval interval, int left, int right, int from, int to, int val) {
            Interval clone = queue[queue_head++];
            clone.copy(interval);
            if (left >= from && right <= to) {
                clone.dirty += val;
                return clone;
            }

            int center = (left + right) >> 1;
            if (center >= from) {
                clone.leftHalf = update(clone.leftHalf, left, center, from, to, val);
            }
            if (center < to) {
                clone.rightHalf = update(clone.rightHalf, center + 1, right, from, to, val);
            }
            clone.sum = sumOf(clone.leftHalf, center - left + 1) + sumOf(clone.rightHalf, right - center);
            return clone;
        }

        private int sumOf(Interval interval, int size) {
            return interval.sum + size * interval.dirty;
        }

        public int sumOf(int version, int from, int to) {
            if (leftBound > to && rightBound < to) {
                return 0;
            }
            Interval interval = history.get(version);
            return sumOf(interval, leftBound, rightBound, from, to);
        }

        private int sumOf(Interval interval, int left, int right, int from, int to) {
            if (left >= from && right <= to) {
                return sumOf(interval, right - left + 1);
            }
            int sum = (Math.min(right, to) - Math.min(left, from) + 1) * interval.dirty;
            int center = (left + right) >> 1;
            if (center >= from) {
                sum += sumOf(interval.leftHalf, left, center, from, to);
            }
            if (center < to) {
                sum += sumOf(interval.rightHalf, center + 1, right, from, to);
            }
            return sum;
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = 0; i < history.size(); i++) {
                for (int j = leftBound; j <= rightBound; j++) {
                    s += sumOf(i, j, j) + ", ";
                }
                s += "\n";
            }
            return s;
        }

        private Interval buildTree(int left, int right) {
            Interval interval = new Interval();
//            interval.left = left;
//            interval.right = right;
            if (left != right) {
                int center = (left + right) >> 1;
                interval.leftHalf = buildTree(left, center);
                interval.rightHalf = buildTree(center + 1, right);
            }
            return interval;
        }
    }
}
