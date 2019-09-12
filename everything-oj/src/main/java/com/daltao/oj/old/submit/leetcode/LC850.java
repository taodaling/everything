package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class LC850 {
    static final int MOD = (int) (1e9 + 7);
    static final int LB_X = 0;
    static final int LB_Y = 1;
    static final int RT_X = 2;
    static final int RT_Y = 3;

    public int rectangleArea(int[][] rectangles) {
        int n = rectangles.length;
        //Randomized.randomizedArray(rectangles, 0, n);
        Arrays.sort(rectangles, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[LB_Y] - o2[LB_Y];
            }
        });

        int MIN = 0;
        int MAX = (int) 1e9;

        Segment root = Segment.build(MIN, MAX);

        for (int[] rectangle : rectangles) {
            root = Segment.updatePersistently(rectangle[LB_X], rectangle[RT_X] - 1,
                    MIN, MAX, rectangle[LB_Y], rectangle[RT_Y] - 1, root);
        }

        long area = Segment.query(MIN, MAX, MIN, MAX, root);
        return (int) (area % MOD);
    }

    public static class Randomized {
        static Random random = new Random();

        public static double nextDouble(double min, double max) {
            return random.nextDouble() * (max - min) + min;
        }

        public static void randomizedArray(int[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                int tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(long[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                long tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(double[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                double tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(float[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                float tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static <T> void randomizedArray(T[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                T tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static int nextInt(int l, int r) {
            return random.nextInt(r - l + 1) + l;
        }
    }


    private static class Segment implements Cloneable {
        Segment left;
        Segment right;
        long area;
        boolean lazy;
        int lazyB;
        int lazyT;
        int minHeight;
        int maxHeight;


        public void setHeight(int l, int r, int b, int t) {
            if (t <= minHeight) {
                return;
            }

            pushDown(l, r);

            b = Math.max(b, minHeight + 1);
            area += (long) (t - b + 1) * (r - l + 1);
            lazy = true;
            lazyB = b;
            lazyT = t;
            minHeight = maxHeight = t;
        }

        public void pushDown(int l, int r) {
            if (lazy) {
                lazy = false;

                int m = (l + r) >> 1;
                left = left.clone();
                left.setHeight(l, m, lazyB, lazyT);
                right = right.clone();
                right.setHeight(m + 1, r, lazyB, lazyT);
            }
        }

        public void pushUp() {
            area = left.area + right.area;
            minHeight = Math.min(left.minHeight, right.minHeight);
            maxHeight = Math.max(left.maxHeight, right.maxHeight);
        }

        public static Segment build(int l, int r) {
            Segment segment = new Segment();
            segment.left = segment.right = segment;
            segment.minHeight = segment.maxHeight = -1;
            return segment;
        }

        public static boolean checkOutOfRange(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public static boolean checkCoverage(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }


        public static Segment updatePersistently(int ll, int rr, int l, int r, int b, int t, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return segment;
            }
            segment = segment.clone();


            if (checkCoverage(ll, rr, l, r) && segment.minHeight == segment.maxHeight) {
                segment.setHeight(l, r, b, t);
                return segment;
            }

            int m = (l + r) >> 1;
            segment.pushDown(l, r);
            segment.left = updatePersistently(ll, rr, l, m, b, t, segment.left);
            segment.right = updatePersistently(ll, rr, m + 1, r, b, t, segment.right);
            segment.pushUp();
            return segment;
        }

        public static long query(int ll, int rr, int l, int r, Segment segment) {
            if (checkOutOfRange(ll, rr, l, r)) {
                return 0;
            }
            if (checkCoverage(ll, rr, l, r)) {
                return segment.area;
            }
            int m = (l + r) >> 1;

            segment.pushDown(l, r);
            return query(ll, rr, l, m, segment.left) +
                    query(ll, rr, m + 1, r, segment.right);
        }


        @Override
        public Segment clone() {
            try {
                return (Segment) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
