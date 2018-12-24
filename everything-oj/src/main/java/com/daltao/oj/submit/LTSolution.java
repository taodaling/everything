package com.daltao.oj.submit;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Solution().minAreaFreeRect(new int[][]{
                {1,2},{2,1},{1,0},{0,1}
        }));
    }



    static class Solution {
        public double minAreaFreeRect(int[][] points) {
            Arrays.sort(points, (a, b) -> a[0] != b[0] ? (a[0] - b[0]) : (a[1] - b[1]));

            int n = points.length;
            double minArea = 1e12;
            for(int i = 0; i < n; i++)
            {
                for(int j = i + 1; j < n; j++)
                {
                    int dx1 = points[i][0] - points[j][0];
                    int dy1 = points[i][1] - points[j][1];
                    for(int k = i + 1; k < n; k++)
                    {
                        for(int t = k + 1; t < n; t++)
                        {
                            int dx2 = points[k][0] - points[t][0];
                            int dy2 = points[k][1] - points[t][1];
                            if(!isParallel(dx1, dy1, dx2, dy2))
                            {
                                continue;
                            }
                            long len1 = distance(dx1, dy1);
                            long len2 = distance(dx2, dy2);
                            if(len1 != len2)
                            {
                                continue;
                            }
                            if(!isVertical(dx1, dy1, points[i][0] - points[k][0], points[i][1] - points[k][1]))
                            {
                                continue;
                            }

                            long height = distance(points[i][0] - points[k][0], points[i][1] - points[k][1]);
                            if(height * len1 == 0L)
                            {
                                continue;
                            }

                            minArea = Math.min(minArea, height * len1);
                        }
                    }
                }
            }

            if(minArea == 1e12)
            {
                return 0;
            }
            return Math.sqrt(minArea);
        }

        public static boolean isParallel(int x1,int y1, int x2, int y2)
        {
            return (long)(x1 * y2) - (x2 * y1) == 0L;
        }

        public static boolean isVertical(int x1, int y1, int x2, int y2)
        {
            return (long)x1 * x2 + y1 * y2 == 0L;
        }

        public static long distance(int x1, int y1)
        {
            return (long)x1 * x1 + y1 * y1;
        }
    }

}
