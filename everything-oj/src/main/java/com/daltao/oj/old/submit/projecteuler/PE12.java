package com.daltao.oj.old.submit.projecteuler;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by dalt on 2018/4/2.
 */
public class PE12 {
    public static final int MOD = (int) (1e9 + 7);
    public static BlockReader input;
    public static PrintStream output;

    public static void main(String[] args) throws FileNotFoundException {

        init();

        solve();

        output.flush();
    }

    public static void init() throws FileNotFoundException {
        //input = new BlockReader(System.in);
        output = System.out;
    }

    public static void solve() {
        final int LIMIT = (int) 1e7;
        boolean[] isComposite = new boolean[LIMIT];
        int[] primes = new int[LIMIT];
        int primeCnt = 0;
        int[] factorNum = new int[LIMIT];
        int[] minFactorCnt = new int[LIMIT];
        factorNum[1] = 1;
        minFactorCnt[1] = 0;
        isComposite[1] = true;
        for (int i = 2; i < LIMIT; i++) {
            if (!isComposite[i]) {
                primes[primeCnt++] = i;
                factorNum[i] = 2;
                minFactorCnt[i] = 1;
            }
            //primes[j] * i < LIMIT
            for (int j = 0, until = (LIMIT + i - 1) / i; primes[j] < until; j++) {
                int ij = i * primes[j];
                isComposite[ij] = true;

                if (i % primes[j] == 0) {
                    minFactorCnt[ij] = minFactorCnt[i] + 1;
                    factorNum[ij] = factorNum[i] / (minFactorCnt[i] + 1) * (minFactorCnt[ij] + 1);
                    break;
                }
                minFactorCnt[ij] = 1;
                factorNum[ij] = factorNum[i] * factorNum[primes[j]];
            }
        }

        for (int i = 1; ; i++) {
            int n1 = i;
            int n2 = i + 1;
            if ((n1 & 1) == 0) {
                n1 >>= 1;
            } else {
                n2 >>= 1;
            }

            if (factorNum[n1] * factorNum[n2] >= 5000) {
                output.println((long)n1 * n2);
                return;
            }
        }
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
