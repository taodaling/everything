package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/12/9.
 */
public class NumberSequence {
    static final int[] TEN_BASE = new int[10];
    private static final int INF = (int) 1e8;
    private static BlockReader input;

    static {
        int base = 1;
        for (int i = 0, bound = TEN_BASE.length; i < bound; i++) {
            TEN_BASE[i] = base;
            base *= 10;
        }
    }

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\NumberSequence.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    int i;

    public static void main(String[] args) {

        input = new BlockReader(System.in);

        int testCase = input.nextInteger();
        while (testCase-- > 0) {
            NumberSequence roundNumbers = new NumberSequence();
            roundNumbers.init();
            System.out.println(roundNumbers.solve());
        }
    }

    public void init() {
        i = input.nextInteger();
    }

    public long ll(int y) {
        int t = 0;
        long len = 0;
        while (TEN_BASE[t] <= y) {
            len += y - TEN_BASE[t] + 1;
            t++;
        }
        return len;
    }

    public long l(int x, int y) {
        return ((long) (x - y + 2) * (long) (x - y + 1)) >> 1;
    }

    public long l(int x) {
        int k = 0;
        long len = 0;
        while (TEN_BASE[k] <= x) {
            len += l(x, TEN_BASE[k]);
            k++;
        }
        return len;
    }

    public int solve() {
        //Binary search for x while l(x - 1) < i <= l(x)
        if (i == 1) {
            return 1;
        }
        int upper = 1000000000;
        int low = 1;
        while (low + 1 < upper) {
            int half = (low + upper) >> 1;
            long l_half = l(half);
            if (l_half < i) {
                low = half;
            } else {
                upper = half;
            }
        }
        int x = low + 1;

        //Then find y that satisfy ll(y-1)<j<=ll(y)
        int j = (int) (i - l(x - 1));
        if (j == 1) {
            return 1;
        }
        upper = 100000000;
        low = 1;
        while (low + 1 < upper) {
            int half = (low + upper) >> 1;
            long l_half = ll(half);
            if (l_half < j) {
                low = half;
            } else {
                upper = half;
            }
        }
        int y = low + 1;
        int k = (int) (j - ll(y - 1));
        return Integer.toString(y).toCharArray()[k - 1] - '0';
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
