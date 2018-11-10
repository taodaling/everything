package com.daltao.oj.old.submit.hdu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/1/15.
 */
public class HDU1695 {
    public static final int LIMIT = 100001;
    public static final int[] MU = new int[LIMIT + 1];
    public static final int[] SEG_1 = new int[LIMIT * 2];
    public static final int[] SEG_2 = new int[LIMIT * 2];
    public static BlockReader input;

    static {
        MU[0] = 0;
        MU[1] = 1;
        for (int i = 2, bound = MU.length; i < bound; i++) {
            //i is a prim
            if (MU[i] == 0) {
                MU[i] = -1;
                int i2 = i * i;
                for (int j = i + i; j < bound; j += i) {
                    if (j % i2 == 0) {
                        MU[j] = Integer.MIN_VALUE;
                    } else {
                        MU[j]++;
                    }
                }
            } else if (MU[i] < 0) {
                MU[i] = 0;
            } else {
                MU[i] = 1 - ((MU[i] % 2) << 1);
            }
            MU[i] += MU[i - 1];
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\hdu\\HDU1695.in"));


        input = new BlockReader(System.in);
        StringBuilder builder = new StringBuilder(2048);
        HDU1695 solution = new HDU1695();
        for (int i = 1, bound = input.nextInteger(); i <= bound; i++) {
            //builder.setLength(0);
            builder.append("Case ").append(i).append(": ").append(solution.solve()).append(System.lineSeparator());
        }
        System.out.print(builder.toString());
    }

    public long solve() {
        int a = input.nextInteger();
        int b = input.nextInteger();
        int c = input.nextInteger();
        int d = input.nextInteger();
        int k = input.nextInteger();

        if (k == 0) {
            return 0;
        }

        if (b > d) {
            int tmp = b;
            b = d;
            d = tmp;
        }

        b /= k;
        d /= k;

        for (int i = 1; i <= b; i++) {
            int x = b / i;
            int y = b / x;
            SEG_1[i] = y;
            i = y;
        }

        for (int i = 1; i <= d; i++) {
            int x = d / i;
            int y = d / x;
            SEG_2[i] = y;
            i = y;
        }

        long sum = 0;
        for (int i = 1, j = 1; i <= b && j <= d; ) {
            long bi = b / i;
            long dj = d / j;
            long mul = (((1 + bi) + ((dj - bi) << 1)) * bi) >> 1;
            int begin = Math.max(i, j);
            int end = Math.min(SEG_1[i], SEG_2[j]);
            sum += mul * (MU[end] - MU[begin - 1]);
            if (SEG_1[i] == end) {
                i = end + 1;
            }
            if (SEG_2[j] == end) {
                j = end + 1;
            }
        }

        return sum;
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
