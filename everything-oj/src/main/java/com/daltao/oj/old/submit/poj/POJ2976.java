package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by dalt on 2018/2/2.
 */
public class POJ2976 {
    public static final int LIMIT = 1000;
    public static final double[] A = new double[LIMIT];
    public static final double[] B = new double[LIMIT];
    public static final double[] C = new double[LIMIT];
    public static final double PREC = 1e-4;
    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\poj\\POJ2976.in"));

        input = new BlockReader(System.in);

        int n, k;
        POJ2976 solution = new POJ2976();
        while (true) {
            n = input.nextInteger();
            k = input.nextInteger();
            if (n == 0 && k == 0) {
                break;
            }
            System.out.println((int) (solution.solve(n, k) + 0.5));
        }
    }

    public double solve(int n, int k) {
        for (int i = 0; i < n; i++) {
            A[i] = input.nextInteger();
        }
        for (int i = 0; i < n; i++) {
            B[i] = input.nextInteger();
        }

        double l = 0, r = 1;
        while (r - l > PREC) {
            double c = (l + r) / 2;
            for (int i = 0; i < n; i++) {
                C[i] = A[i] - c * B[i];
            }
            Arrays.sort(C, 0, n);
            double sum = 0;
            for (int i = n - 1; i >= k; i--) {
                sum += C[i];
            }
            if (sum > 0) {
                l = c;
            } else {
                r = c;
            }
        }

        return (l + r) * 50;
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
