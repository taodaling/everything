package com.daltao.oj.submit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Integer(5) == Integer.valueOf(5));

        /*System.out.println(new Solution()
                .gridIllumination(5,
                        new int[][]{{0, 0}, {4, 4}},
                        new int[][]{{1, 1}, {1, 1}}
                ));*/
    }


    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    static class Solution {
        public static class Point {
            int x;
            int y;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public boolean equals(Object o) {
                Point other = (Point) o;
                return x == other.x && y == other.y;
            }

            public int hashCode() {
                return x * 31 + y;
            }
        }

        public static class Grid {
            Set<Point> points = new HashSet<>();
            Map<Integer, Integer> h = new HashMap<>();
            Map<Integer, Integer> v = new HashMap<>();
            Map<Integer, Integer> d1 = new HashMap<>();
            Map<Integer, Integer> d2 = new HashMap<>();

            static int getD1(int x, int y) {
                return x - y;
            }

            static int getD2(int x, int y) {
                return x + y;
            }

            public void inc(Map<Integer, Integer> map, Integer key) {
                map.put(key, map.getOrDefault(key, 0) + 1);
            }

            public void dec(Map<Integer, Integer> map, Integer key) {
                map.put(key, map.getOrDefault(key, 0) - 1);
            }


            public Grid(int[][] lamps) {
                for (int[] lamp : lamps) {
                    inc(h, lamp[0]);
                    inc(v, lamp[1]);
                    inc(d1, getD1(lamp[0], lamp[1]));
                    inc(d2, getD2(lamp[0], lamp[1]));
                    points.add(new Point(lamp[0], lamp[1]));
                }
            }

            public int answer(int x, int y) {
                if (h.getOrDefault(x, 0) + v.getOrDefault(y, 0) + d1.getOrDefault(getD1(x, y), 0)
                        + d2.getOrDefault(getD2(x, y), 0) > 0) {
                    return 1;
                }
                return 0;
            }

            public void turnOff(int x, int y) {
                if (!points.remove(new Point(x, y))) {
                    return;
                }
                dec(h, x);
                dec(v, y);
                dec(d1, getD1(x, y));
                dec(d2, getD2(x, y));
            }
        }

        public int[] gridIllumination(int N, int[][] lamps, int[][] queries) {
            Grid grid = new Grid(lamps);
            int n = queries.length;
            int[] ans = new int[n];

            int[][] directions = new int[][]{
                    {0, 0},
                    {1, 0},
                    {0, 1},
                    {-1, 0},
                    {0, -1},
                    {1, 1},
                    {-1, -1},
                    {1, -1},
                    {-1, 1}
            };

            for (int i = 0; i < n; i++) {
                int x = queries[i][0];
                int y = queries[i][1];
                ans[i] = grid.answer(x, y);
                for (int[] direction : directions) {
                    grid.turnOff(x + direction[0], y + direction[1]);
                }
            }

            return ans;
        }
    }

}
