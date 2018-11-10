package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/26.
 */
public class BoardCut {
    static final double INF = 1e15;
    static BlockReader input;
    int n;
    int[][] board = new int[8][8];

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\BoardCut.in"));

        input = new BlockReader(System.in);
        for (; input.hasMore(); ) {
            BoardCut solution = new BoardCut();
            solution.init();
            System.out.println(String.format("%.3f", solution.solve()));
        }
    }

    public void init() {
        n = input.nextInteger();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = input.nextInteger();
            }
        }
    }


    public double solve() {
        double[][][][][] dp = new double[8][8][8][8][n];

        double avg = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                avg += board[i][j];
            }
        }
        avg /= n;

        for (int x1 = 0; x1 < 8; x1++) {
            for (int y1 = 0; y1 < 8; y1++) {
                for (int x2 = x1; x2 < 8; x2++) {
                    for (int y2 = y1; y2 < 8; y2++) {
                        double sum = 0;
                        for (int i = x1; i <= x2; i++) {
                            for (int j = y1; j <= y2; j++) {
                                sum += board[i][j];
                            }
                        }
                        double value = power2(sum - avg);
                        for (int i = 0; i < n; i++) {
                            dp[x1][y1][x2][y2][i] = value;
                        }
                    }
                }
            }
        }


        for (int i = 1; i < n; i++) {
            for (int x1 = 0; x1 < 8; x1++) {
                for (int y1 = 0; y1 < 8; y1++) {
                    for (int x2 = x1; x2 < 8; x2++) {
                        for (int y2 = y1; y2 < 8; y2++) {
                            double min = INF;
                            for (int j = x1; j < x2; j++) {
                                min = Math.min(min, dp[x1][y1][j][y2][0] + dp[j + 1][y1][x2][y2][i - 1]);
                                min = Math.min(min, dp[x1][y1][j][y2][i - 1] + dp[j + 1][y1][x2][y2][0]);
                            }

                            for (int j = y1; j < y2; j++) {
                                min = Math.min(min, dp[x1][y1][x2][j][0] + dp[x1][j + 1][x2][y2][i - 1]);
                                min = Math.min(min, dp[x1][y1][x2][j][i - 1] + dp[x1][j + 1][x2][y2][0]);
                            }

                            dp[x1][y1][x2][y2][i] = min;
                        }
                    }
                }
            }
        }

        double res = dp[0][0][7][7][n - 1];
        return Math.sqrt(res / n);
    }

    public double power2(double a) {
        return a * a;
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
