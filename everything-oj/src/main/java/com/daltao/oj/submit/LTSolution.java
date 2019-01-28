package com.daltao.oj.submit;

import java.util.Arrays;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Solution()
        .uniquePathsIII(new int[][]{
                {1, -1},
                {0, 2}
        }));
    }


    static class Solution {

        public int uniquePathsIII(int[][] grid) {
            int n = grid.length;
            int m = grid[0].length;

            int total = n * m;
            int mask = (1 << total) - 1;

            int[][][] mem = new int[n][m][mask + 1];
            boolean[][][] visit = new boolean[n][m][mask + 1];

            int targetX = 0;
            int targetY = 0;
            for(int i = 0; i < n; i++)
            {
                for(int j = 0; j < m; j++)
                {
                    if(grid[i][j] == 1)
                    {
                        visit[i][j][1 << index(i, j, m)] = true;
                        mem[i][j][1 << index(i, j, m)] = 1;
                    }
                    else if(grid[i][j] == -1)
                    {
                        Arrays.fill(mem[i][j], 0);
                        Arrays.fill(visit[i][j], true);
                        mask = removeBit(mask, index(i, j, m));
                    }
                    else if(grid[i][j] == 2)
                    {
                        targetX = j;
                        targetY = i;
                    }
                }
            }

            //find(mem, visit, 1, 0, 5);
            int result = find(mem, visit, targetY, targetX, mask);
            return result;
        }

        public static int index(int i, int j, int w)
        {
            return i * w + j;
        }

        public static boolean containBit(int i, int bit)
        {
            return ((i >> bit) & 1) == 1;
        }

        public static int removeBit(int i, int bit)
        {
            return i & ~(1 << bit);
        }

        public static int find(int[][][] mem, boolean[][][] visit, int i, int j, int mask)
        {
            if(!containBit(mask, index(i, j, mem[0].length)))
            {
                return 0;
            }
            if(i < 0 || i >= mem.length || j < 0 || j >= mem[0].length)
            {
                return 0;
            }
            if(!visit[i][j][mask])
            {
                visit[i][j][mask] = true;
                int preMask = removeBit(mask, index(i, j, mem[0].length));
                mem[i][j][mask] = find(mem, visit, i - 1, j, preMask) + find(mem, visit, i + 1, j, preMask)
                        + find(mem, visit, i, j - 1, preMask) + find(mem, visit, i, j + 1, preMask);
                System.out.println(i + ":" + j + ":" + mask + ":" + mem[i][j][mask]);
            }
            return mem[i][j][mask];
        }
    }

}
