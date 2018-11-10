package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/1/19.
 */
public class BZOJ1068 {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ1068.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            BZOJ1068 solution = new BZOJ1068();
            System.out.println(solution.solve());
        }
    }

    public int solve() {
        char[] data = new char[51];
        int n = input.nextBlock(data, 1);
        int[][] dp = new int[n + 1][n + 1];
        dp[0][0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = i - 1; j >= 0; j--) {
                int len = i - j;
                if ((len & 1) == 1) {
                    dp[i][j] = dp[i - 1][j] + 1;
                } else {
                    int halfLen = len >> 1;
                    int k = j + 1;
                    int bound = k + halfLen;
                    for (; k < bound && data[k] == data[k + halfLen]; k++) ;
                    if (k == bound) {
                        dp[i][j] = dp[bound - 1][j] + 1;
                    } else {
                        dp[i][j] = dp[i - 1][j] + 1;
                    }
                }
            }
            dp[i][i] = min(dp[i], 0, i) + 1;
        }

        return min(dp[n], 0, n);
    }

    public static int min(int[] data, int from, int to) {
        int v = Integer.MAX_VALUE;
        for (int i = from; i < to; i++) {
            v = Math.min(v, data[i]);
        }
        return v;
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

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
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
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

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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
    }
}
