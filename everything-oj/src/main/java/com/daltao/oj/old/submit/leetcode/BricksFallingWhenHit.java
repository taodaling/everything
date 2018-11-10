package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;

public class BricksFallingWhenHit {
    public static void main(String[] args){
        BricksFallingWhenHit solution = new BricksFallingWhenHit();
        int[][] grid = new int[][]{{1, 0, 0}, {1, 1, 1}, {0, 1, 0}};
        int[][] hits = new int[][]{{1, 1}, {0, 0}};

        System.out.println(Arrays.toString(solution.hitBricks(grid, hits)));
    }



    public int[] hitBricks(int[][] grid, int[][] hits) {

        int hitsTime = hits.length;
        int[] result = new int[hitsTime];

        int row = grid.length;
        int col = grid[0].length;
        int[][] gridStatus = new int[row][col];

        for (int i = 0; i < hitsTime; i++) {
            int[] hit = hits[i];
            if (gridStatus[hit[0]][hit[1]] == 0) {
                gridStatus[hit[0]][hit[1]] = (i + 1);
            }
        }

        for (int i = 0; i < col; i++) {
            active(grid, gridStatus, 0, i);
        }

        for (int i = hitsTime - 1; i >= 0; i--) {
            int[] hit = hits[i];
            result[i] = restore(grid, gridStatus, hit[0], hit[1], i + 1);
            result[i] = result[i] == 0 ? 0 : (result[i] - 1);
        }

        return result;
    }

    public static int restore(int[][] grid, int[][] gridStatus, int x, int y, int version) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
            return 0;
        }
        if (grid[x][y] == 0) {
            return 0;
        }
        if (gridStatus[x][y] == EXIST) {
            return 0;
        }
        if (gridStatus[x][y] != NOT_EXIST && gridStatus[x][y] < version) {
            return 0;
        }
        if ((exist(gridStatus, x - 1, y) ||
                exist(gridStatus, x + 1, y) ||
                exist(gridStatus, x, y - 1) ||
                exist(gridStatus, x, y + 1)) == false) {
            return 0;
        }
        gridStatus[x][y] = EXIST;
        return 1 + restore(grid, gridStatus, x - 1, y, version) +
                restore(grid, gridStatus, x + 1, y, version) +
                restore(grid, gridStatus, x, y - 1, version) +
                restore(grid, gridStatus, x, y + 1, version);
    }

    public static boolean exist(int[][] gridStatus, int x, int y) {
        if (x >= gridStatus.length || y < 0 || y >= gridStatus[0].length) {
            return false;
        }
        return x < 0 || gridStatus[x][y] == EXIST;
    }

    static final int EXIST = 100000000;
    static final int NOT_EXIST = 0;

    public static void active(int[][] grid, int[][] gridStatus, int x, int y) {
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
            return;
        }
        if (gridStatus[x][y] != NOT_EXIST || grid[x][y] == 0) {
            return;
        }
        gridStatus[x][y] = EXIST;
        active(grid, gridStatus, x - 1, y);
        active(grid, gridStatus, x + 1, y);
        active(grid, gridStatus, x, y - 1);
        active(grid, gridStatus, x, y + 1);
    }
}
