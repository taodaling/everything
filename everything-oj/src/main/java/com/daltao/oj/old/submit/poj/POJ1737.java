package com.daltao.oj.old.submit.poj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Created by Administrator on 2018/1/29.
 */
public class POJ1737 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int LIMIT = 50;

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            //   System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\916E.in"));
        }



        BigInteger[] graph = new BigInteger[LIMIT + 1];
        for (int i = 1; i <= LIMIT; i++) {
            graph[i] = BigInteger.ONE.shiftLeft(i * (i - 1) >> 1);
        }

        BigInteger[][] choose = new BigInteger[LIMIT + 1][LIMIT + 1];
        choose[1][0] = choose[1][1] = BigInteger.ONE;
        for (int i = 2; i <= LIMIT; i++) {
            choose[i][0] = choose[i][i] = BigInteger.ONE;
            for (int j = 1; choose[i][j] == null; j++) {
                choose[i][j] = choose[i][i - j] = choose[i - 1][j].add(choose[i - 1][j - 1]);
            }
        }

        BigInteger[] unconnected = new BigInteger[LIMIT + 1];
        BigInteger[] connected = new BigInteger[LIMIT + 1];
        unconnected[1] = BigInteger.ZERO;
        connected[1] = BigInteger.ONE;
        for (int i = 2; i <= LIMIT; i++) {
            BigInteger sum = BigInteger.ZERO;
            for (int j = 1; j < i; j++) {
                sum = sum.add(connected[j].multiply(graph[i - j]).multiply(choose[i - 1][j - 1]));
            }
            unconnected[i] = sum;
            connected[i] = graph[i].subtract(unconnected[i]);
        }


        input = new BlockReader(System.in);
        StringBuilder builder = new StringBuilder();
        while (true) {
            int i = input.nextInteger();
            if (i == 0) {
                break;
            }
            builder.append(connected[i]).append('\n');
        }

        System.out.print(builder);
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
