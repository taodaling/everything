package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dalt on 2017/12/8.
 */
public class LongestOrderedSubsequence {
    static BlockReader input = null;

    public static void main(String[] args) throws IOException {
        input = new BlockReader(new FileInputStream("D:/input.txt"));

        while (input.hasMore()) {
            LongestOrderedSubsequence longestOrderedSubsequence = new LongestOrderedSubsequence();
            longestOrderedSubsequence.init();
            System.out.println(longestOrderedSubsequence.solve());
        }
    }

    int n;
    int[] seq;

    public void init() throws IOException {
        n = BlockParser.parseInt(input.nextBlock());
        seq = new int[n];
        for (int i = 0; i < n; i++) {
            seq[i] = BlockParser.parseInt(input.nextBlock());
        }
    }

    public int solve() {
        int[] dp = new int[n];
        dp[0] = 1;
        int ret = 1;
        for (int i = 1; i < n; i++) {
            dp[i] = 1;
            for (int j = i - 1; j >= 0; j--) {
                if (seq[j] < seq[i]) {
                    dp[i] = Math.max(dp[i], 1 + dp[j]);
                }
            }
            ret = Math.max(ret, dp[i]);
        }
        return ret;
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
            while (EOF != next && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public int nextBlock(char[] data, int offset) throws IOException {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (EOF != next && index < bound && !Character.isWhitespace(next)) {
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
