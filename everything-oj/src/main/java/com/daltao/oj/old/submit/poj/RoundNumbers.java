package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/8.
 */
public class RoundNumbers {
    private static final int INF = (int) 1e8;
    static int[][] gcache = new int[32][64];
    static int[][] fcache = new int[32][64];
    private static BlockReader input;

    static {
        for (int i = 0; i < 32; i++) {
            Arrays.fill(gcache[i], -1);
            Arrays.fill(fcache[i], -1);
        }
    }

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\RoundNumbers.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    int rangeFrom;
    int rangeTo;

    public static void main(String[] args) {

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            RoundNumbers roundNumbers = new RoundNumbers();
            roundNumbers.init();
            System.out.println(roundNumbers.solve());
        }
    }

    public static boolean test() {
        int rnNum = 0;
        for (int i = 0; ; i++) {
            rnNum += isRN(i) ? 1 : 0;
            System.out.println(i);
            if (rn(i) != rnNum) {
                throw new RuntimeException("rn(" + i + ")!=");
            }
        }
    }

    public static boolean isRN(int v) {
        if (v == 0) {
            return true;
        }
        int zero = 0;
        int one = 0;
        while (v != 0) {
            if ((v & 1) == 0) {
                zero++;
            } else {
                one++;
            }
            v >>= 1;
        }
        return zero >= one;
    }

    public static int f(int t, int x) {
        int actualT = t + 32;
        if (fcache[x][actualT] == -1) {
            if (x == 0) {
                fcache[x][actualT] = t > 1 ? 0 : 1;
            } else if (x == 1) {
                fcache[x][actualT] = t > 1 ? 0 : t >= 0 ? 1 : 2;
            } else {
                fcache[x][actualT] = f(t, x - 1) + g(t + 1, x - 1);
            }
        }
        return fcache[x][actualT];
    }

    public static int g(int t, int x) {
        int actualT = t + 32;
        if (gcache[x][actualT] == -1) {
            if (x == 0) {
                gcache[x][actualT] = 0;
            } else if (x == 1) {
                gcache[x][actualT] = t > 1 ? 0 : t >= 0 ? 1 : 2;
            } else {
                gcache[x][actualT] = g(t + 1, x - 1) + g(t - 1, x - 1);
            }
        }
        return gcache[x][actualT];
    }


    public static int rn(int v) {
        if (v == 0) {
            return 1;
        }
        int i = 31;
        while ((v & (1 << i)) == 0) {
            i--;
        }
        int sum = f(0, i);

        i--;
        int oneBitNum = 1;
        int zeroBitNum = 0;
        while (i >= 0) {
            while (i >= 0 && (v & (1 << i)) == 0) {
                i--;
                zeroBitNum++;
            }
            if (i < 0) {
                break;
            }
            if (i == 0) {
                if (zeroBitNum - oneBitNum >= 1) {
                    sum += 1;
                }
                if (zeroBitNum - oneBitNum >= -1) {
                    sum += 1;
                }
            } else {
                sum += g(oneBitNum - zeroBitNum - 1, i);
            }
            oneBitNum++;
            i--;
        }

        if ((v & 1) == 0) {
            if (zeroBitNum >= oneBitNum) {
                sum++;
            }
        }
        return sum;
    }


    public void init() {
        rangeFrom = input.nextInteger();
        rangeTo = input.nextInteger();
    }

    public int solve() {
        return rn(rangeTo) - rn(rangeFrom - 1);
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
