package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dalt on 2017/12/8.
 */
public class HumanGeneFunctions {

    static {
        try {
            System.setIn(new FileInputStream("D:/test/poj/HumanGeneFunctions.in"));
        } catch (FileNotFoundException e) {
        }
    }

    static BlockReader input = new BlockReader(System.in);

    public static void main(String[] args) throws IOException {
        int testCaseNum = BlockParser.parseInt(input.nextBlock());
        while (testCaseNum-- > 0) {
            HumanGeneFunctions humanGeneFunctions = new HumanGeneFunctions();
            humanGeneFunctions.init();
            System.out.println(humanGeneFunctions.solve());
        }
    }

    static final int INF = 100000000;

    static int[][] scores;
    static final int SPACE = 4;
    static final int A = 0;
    static final int C = 1;
    static final int G = 2;
    static final int T = 3;

    static {
        scores = new int[][]{
                {5, -1, -2, -1, -3},
                {-1, 5, -3, -2, -4},
                {-2, -3, 5, -2, -2},
                {-1, -2, -2, 5, -1},
                {-3, -4, -2, -1, INF},
        };
    }

    int[] s1;
    int[] s2;

    public int castGene(char c) {
        switch (c) {
            case 'A':
                return A;
            case 'C':
                return C;
            case 'G':
                return G;
            case 'T':
                return T;
        }
        return -1;
    }

    public void init() throws IOException {
        input.nextBlock();
        char[] gene1 = input.nextBlock().toCharArray();
        input.nextBlock();
        char[] gene2 = input.nextBlock().toCharArray();

        s1 = new int[gene1.length];
        s2 = new int[gene2.length];
        for (int i = 0, bound = gene1.length; i < bound; i++) {
            s1[i] = castGene(gene1[i]);
        }

        for (int i = 0, bound = gene2.length; i < bound; i++) {
            s2[i] = castGene(gene2[i]);
        }
    }

    public int solve() {
        int[][] dp = new int[s1.length + 1][s2.length + 1];
        for (int i = 1, ibound = s1.length; i <= ibound; i++) {
            dp[i][0] = dp[i - 1][0] + scores[s1[i - 1]][SPACE];
        }
        for (int i = 1, ibound = s2.length; i <= ibound; i++) {
            dp[0][i] = dp[0][i - 1] + scores[s2[i - 1]][SPACE];
        }

        for (int i = 1, ibound = s1.length; i <= ibound; i++) {
            for (int j = 1, jbound = s2.length; j <= jbound; j++) {
                int max = -INF;
                int iIndex = i - 1;
                int jIndex = j - 1;
                max = Math.max(dp[i][jIndex] + scores[s2[jIndex]][SPACE], max);
                max = Math.max(dp[iIndex][j] + scores[s1[iIndex]][SPACE], max);
                max = Math.max(dp[iIndex][jIndex] + scores[s1[iIndex]][s2[jIndex]], max);
                dp[i][j] = max;
            }
        }

        return dp[s1.length][s2.length];
    }

    public static class BlockParser {
        public static int parseInt(char[] data, int offset, int length) {
            int result = 0;
            int bound = offset + length;
            boolean rev = false;
            if (!Character.isDigit(data[offset])) {
                rev = data[offset++] == '-';
            }
            for (int i = offset; i < bound; i++) {
                result = (result << 3) + (result << 1) + data[i] - '0';
            }
            return rev ? -result : result;
        }

        public static int parseInt(String s) {
            return parseInt(s.toCharArray(), 0, s.length());
        }
    }

    public static class BlockReader {
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        static final int EOF = -1;

        public void skipBlank() throws IOException {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        StringBuilder builder = new StringBuilder();

        public String nextBlock() throws IOException {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public int nextBlock(char[] data, int offset) throws IOException {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() throws IOException {
            skipBlank();
            return next != EOF;
        }

        public BlockReader(InputStream is) {
            this(is, 1024);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            try {
                next = nextByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public int nextByte() throws IOException {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                dSize = is.read(dBuf);
            }
            return dBuf[dPos++];
        }
    }
}
