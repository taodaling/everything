package com.daltao.oj.old.submit.projecteuler;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Created by dalt on 2018/4/2.
 */
public class PE17 {
    public static final int MOD = (int) (1e9 + 7);
    public static PE13.BlockReader input;
    public static PrintStream output;
    static String[] oneDigit = new String[]{
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
    };
    static String[] twoDigitSpecial = new String[]{
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
            "sixteen", "seventeen", "eighteen", "nineteen"
    };
    static String[] twoDigit = new String[]{
            "zero", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };
    static String hundred = "hundred";

    static String toString(int n) {
        if (n >= 100) {
            return oneDigit[n / 100] + "hundred" + (n % 100 == 0 ? "" : "and" + toString(n % 100));
        }
        if (n >= 20) {
            return twoDigit[n / 10] + (n % 10 == 0 ? "" : toString(n % 10));
        }
        if (n >= 10) {
            return twoDigitSpecial[n - 10];
        }
        return oneDigit[n];
    }

    public static void main(String[] args) throws FileNotFoundException {

        new Thread(null, new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                solve();

                output.flush();
            }
        }, "", 1 << 27).start();

    }

    public static void init() throws FileNotFoundException {
        input = new PE13.BlockReader(new ByteArrayInputStream((
                ""
        ).getBytes(Charset.forName("ascii"))));
        output = System.out;
    }

    public static void solve() {
        int s = "onethousand".length();
        String[] data = new String[1001];
        data[1000] = "onethousand";
        for (int i = 1; i < 1000; i++) {
            data[i] = toString(i);
            s += data[i].length();
        }
        output.println(s);
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

        public long nextLong() {
            skipBlank();
            long ret = 0;
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
