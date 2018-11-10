package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/24.
 */
public class PowerStrings {
    static BlockReader input;
    static char[] data = new char[1000000];
    static int[] matches = new int[1000000];
    static int n;
    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\PowerStrings.in"));

        input = new BlockReader(System.in);
        while (true) {
            n = input.nextBlock(data, 0);
            if (n == 1 && data[0] == '.') {
                break;
            }
            PowerStrings solution = new PowerStrings();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
    }

    public int[] getMatches() {
        matches[0] = 0;
        for (int i = 1, bound = n; i < bound; i++) {
            int k = matches[i - 1];
            while (k > 0 && data[k] != data[i]) {
                k = matches[k - 1];
            }
            if (data[k] == data[i]) {
                k++;
            }
            matches[i] = k;
        }
        return matches;
    }

    public int solve() {
        int[] matches = getMatches();

        int x = n - matches[n - 1];
        if ((x << 1) <= n && n % x == 0) {
            return n / x;
        } else {
            return 1;
        }
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
