package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class LC850V2 {
    static final int LX = 0;
    static final int LY = 1;
    static final int RX = 2;
    static final int RY = 3;

    public int rectangleArea(int[][] rectangles) {
        int n = rectangles.length;

        Arrays.sort(rectangles, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[LY] - o2[LY];
            }
        });

        int[] scanLine = new int[2 * n];
        for (int i = 0; i < n; i++) {
            scanLine[i] = rectangles[i][LX];
            scanLine[i + n] = rectangles[i][RX];
        }

        Randomized.randomizedArray(scanLine, 0, 2 * n);
        Arrays.sort(scanLine);

        long lastX = -1;
        long area = 0;
        for (int x : scanLine) {

            long beginHeight = -1;
            long endHeight = -1;
            long lastHeight = 0;
            for (int[] rect : rectangles) {
                if (rect[LX] >= x || rect[RX] < x) {
                    continue;
                }
                if (rect[LY] <= endHeight) {
                    endHeight = Math.max(endHeight, rect[RY]);
                } else {
                    lastHeight += endHeight - beginHeight;
                    beginHeight = rect[LY];
                    endHeight = rect[RY];
                }
            }
            lastHeight += endHeight - beginHeight;
            area += lastHeight * (x - lastX);


            lastX = x;

        }

        return (int) (area % (int)(1e9 + 7));
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

}
