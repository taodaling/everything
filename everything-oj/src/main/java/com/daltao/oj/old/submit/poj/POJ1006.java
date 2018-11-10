package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/2/25.
 */
public class POJ1006 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ1006.in"));
        }
        input = new BlockReader(System.in);

        solve();
    }

    public static void solve() {
        StringBuilder builder = new StringBuilder(1 << 13);

        final int M = 23 * 28 * 33;

        final int M23 = M / 23 * 6;
        final int M28 = M / 28 * 19;
        final int M33 = M / 33 * 2;


        for (int i = 1; ; i++) {
            int a = input.nextInteger();
            int b = input.nextInteger();
            int c = input.nextInteger();
            int d = input.nextInteger();

            if (a == -1 && b == -1 && c == -1 && d == -1) {
                break;
            }

            int base = (a) * M23 + (b) * M28 + (c) * M33 - d;
            int x = base % M;
            if (x < 0) {
                x += M;
            }
            if (x == 0) {
                x = M;
            }
            builder.append("Case ").append(i).append(": the next triple peak occurs in ").append(x).append(" days.").append('\n');
        }

        System.out.print(builder.toString());
    }


    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 1 << 13);
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
