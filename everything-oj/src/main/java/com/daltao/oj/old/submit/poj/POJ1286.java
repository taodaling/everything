package com.daltao.oj.old.submit.poj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/3.
 */
public class POJ1286 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            //System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\CF919F.in"));
        }
        input = new BlockReader(System.in);

        while (true) {
            int c = input.nextInteger();
            int s = input.nextInteger();
            if (s == 0 && c == 0) {
                break;
            }
            System.out.println(solve(s, c));
        }
    }

    public static int solve(int s, int c) {
        int sum = 0;
        sum += pow(c, s);
        for (int i = 1; i < s; i++) {
            sum += pow(c, gcd(i, s));
        }
        if (s > 2) {
            //flip
            sum += pow(c, (s + 1) >> 1);
            return sum / (s + 1);
        }
        return sum / s;
    }

    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    //Calc a ^ b
    public static int pow(int a, int b) {
        int s = 1;
        for (int i = 1; i <= b; i++) {
            s *= a;
        }
        return s;
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
