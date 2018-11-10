package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/8/20.
 */
public class SudokuSolver {
    short[][] rowContain = new short[9][10];
    short[][] colContain = new short[9][10];
    short[][][] regionContain = new short[3][3][10];
    char[][] result;

    public void solveSudoku(char[][] board) {
        result = board;
        Invocation last = new TrueInvocation();
        for (int i = 8; i >= 0; i--) {
            int rowEmptyCount = 0;
            for (int j = 8; j >= 0; j--) {
                if (board[i][j] != '.') {
                    rowContain[i][board[i][j] - '0'] = 1;
                    regionContain[i / 3][j / 3][board[i][j] - '0'] = 1;
                } else {
                    last = new Invocation(i, j, last);
                }
                if (board[j][i] != '.') {
                    colContain[i][board[j][i] - '0'] = 1;
                }
            }
        }

        if (!last.execute()) {
            throw new IllegalArgumentException();
        }
    }

    public class Invocation {
        int row;
        int col;
        Invocation next = null;

        public Invocation(int row, int col, Invocation next) {
            this.row = row;
            this.col = col;
            this.next = next;
        }

        public Invocation() {
        }

        public boolean execute() {
            short[] regionContain = SudokuSolver.this.regionContain[row / 3][col / 3];
            for (int i = 9; i >= 1; i--) {
                if (rowContain[row][i] + colContain[col][i] + regionContain[i] > 0) {
                    continue;
                }
                result[row][col] = (char) (i + '0');
                rowContain[row][i] = 1;
                colContain[col][i] = 1;
                regionContain[i] = 1;
                if (next.execute()) {
                    return true;
                }
                rowContain[row][i] = 0;
                colContain[col][i] = 0;
                regionContain[i] = 0;
            }
            return false;
        }
    }

    public class TrueInvocation extends Invocation {
        @Override
        public boolean execute() {
            return true;
        }
    }
}
