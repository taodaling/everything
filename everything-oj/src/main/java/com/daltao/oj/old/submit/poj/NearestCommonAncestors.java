package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/29.
 */
public class NearestCommonAncestors {
    static final int INF = (int) 1e8;
    static final int MAX_N = 10000;
    static BlockReader input;
    static int[] fathers = new int[MAX_N + 1];
    static int[] colors = new int[MAX_N + 1];
    static int color = 0;


    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\NearestCommonAncestors.in"));

        input = new BlockReader(System.in);
        for (int i = 1, bound = input.nextInteger(); i <= bound; i++) {
            NearestCommonAncestors solution = new NearestCommonAncestors();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
    }

    public int solve() {
        int n = input.nextInteger();

        for (int i = 1; i <= n; i++) {
            fathers[i] = 0;
            colors[i] = color;
        }
        color++;
        for (int i = 1; i < n; i++) {
            int f = input.nextInteger();
            int s = input.nextInteger();
            fathers[s] = f;
        }

        int n1 = input.nextInteger();
        int n2 = input.nextInteger();
        while (n1 != 0) {
            colors[n1] = color;
            n1 = fathers[n1];
        }
        while (colors[n2] != color) {
            n2 = fathers[n2];
        }
        return n2;
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
