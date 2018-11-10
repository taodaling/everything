package com.daltao.oj.old.submit.codeforces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/1/29.
 */
public class CF918C {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int orderAllocator = 1;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            //   System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\916E.in"));
        }
        input = new BlockReader(System.in);

        char[] data = input.nextBlock().toCharArray();
        int n = data.length;
        int dpLen = n / 2 + 1;
        int[] dp = new int[n / 2 + 1];
        int[] dpFix = new int[n / 2 + 1];
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            char c = data[i - 1];
            if (c == '(') {
                for (int j = 2; j < dpLen; j++) {
                    dpFix[j] = dp[j - 1];
                }
                dpFix[1] = 1;
                dpFix[0] = 0;
            } else if (c == ')') {
                for (int j = 0, bound = dpLen - 1; j < bound; j++) {
                    dpFix[j] = dp[j + 1];
                }
                dpFix[dpLen - 1] = 0;
            } else {
                for (int j = 1, bound = dpLen - 1; j < bound; j++) {
                    dpFix[j] = dp[j + 1] + dp[j - 1] + 1;
                }
                dpFix[0] = dp[1];
                dpFix[dpLen - 1] = dpFix[dpLen - 2] + 1;
            }

            int[] tmp = dp;
            dp = dpFix;
            dpFix = tmp;
            sum += dp[0];
        }
        System.out.println(sum);
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
