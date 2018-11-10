package com.daltao.oj.old.submit.codeforces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/1/19.
 */
public class JamieandAlarmSnooze {
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        input = new BlockReader(System.in);
        JamieandAlarmSnooze solution = new JamieandAlarmSnooze();
        System.out.println(solution.solve());
    }

    public int solve() {

        int min = 10000;


        int x = input.nextInteger();
        int h = input.nextInteger();
        int m = input.nextInteger();
        int unit = ((m - 7) % 10 + 10) % 10;

        if (unit == 0) {
            return 0;
        }

        int[] coes = new int[2];
        int g = gcd(10, x % 10, coes);


        if (unit % g == 0) {
            int step = unit / g;
            int[] modified = new int[]{coes[0] * step, coes[1] * step};

            int ten_g = 10 / g;
            int res = (modified[1] % ten_g + ten_g) % ten_g;

            min = Math.min(res, min);
        }

        int targetHour = h < 17 && h >= 7 ? 7 : 17;
        int time = 0;
        while (h != targetHour) {
            m -= x;
            if (m < 0) {
                m += 60;
                h = ((h - 1) % 24) % 24;
            }
            time++;
        }

        min = Math.min(min, time);


        return min;
    }

    public int gcd(int a, int b, int[] res) {
        if (a > b) {
            return gcd0(a, b, res);
        }
        return gcd0(b, a, res);
    }

    public int gcd0(int a, int b, int[] res) {
        if (b == 0) {
            res[0] = 1;
            res[1] = 0;
            return a;
        }

        int v = gcd0(b, a % b, res);
        int n = res[0];
        int m = res[1];
        res[0] = m;
        res[1] = n - m * (a / b);
        return v;
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
