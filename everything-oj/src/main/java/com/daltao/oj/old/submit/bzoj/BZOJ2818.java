package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/6.
 */
public class BZOJ2818 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int LIMIT = (int) (1e7 + 1);

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            //   System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\916E.in"));
        }


        boolean[] isComposite = new boolean[LIMIT];
        int[] primes = new int[LIMIT / 10];
        int primesLen = 0;
        long[] phi = new long[LIMIT];

        isComposite[1] = true;
        phi[1] = 1;
        for (int i = 2; i < LIMIT; i++) {
            if (!isComposite[i]) {
                primes[primesLen++] = i;
                phi[i] = i - 1;
            }

            //i * p < LIMIT -> p < LIMIT / i -> p < ceil(LIMIT / i)
            for (int j = 0, bound = (LIMIT + i - 1) / i; j < primesLen && primes[j] < bound; j++) {
                int p = primes[j];
                int ip = i * p;
                isComposite[ip] = true;

                if (i % p == 0){
                    phi[ip] = phi[i] * p;
                    break;
                }
                phi[ip] = phi[i] * phi[p];
            }
        }

        //prefix sum
        for (int i = 2; i < LIMIT; i++) {
            phi[i] += phi[i - 1];
        }
        input = new BlockReader(System.in);
        int n = input.nextInteger();
        long sum = 0;
        for (int i = 0; i < primesLen && primes[i] <= n; i++) {
            sum = sum + phi[n / primes[i]] * 2 - 1;
        }

        System.out.println(sum);
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