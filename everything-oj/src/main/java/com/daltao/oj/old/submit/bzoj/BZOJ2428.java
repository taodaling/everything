package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by dalt on 2018/1/16.
 */
public class BZOJ2428 {
    public static BlockReader input;
    Random random = new Random(19950823);

    double[] sums;
    double[] nums;
    int[] numIns;
    int n;
    int m;
    double average;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ2428.in"));

        input = new BlockReader(System.in);

        BZOJ2428 solution = new BZOJ2428();
        //   solution.before();
        System.out.print(String.format("%.2f", solution.solve()));
    }

    public int nextInt(int from, int to) {
        return random.nextInt(to - from) + from;
    }

    public double solve() {
        n = input.nextInteger();
        m = input.nextInteger();
        sums = new double[m];
        nums = new double[n * 2];
        numIns = new int[n * 2];
        for (int i = 0; i < n; i++) {
            nums[i] = input.nextInteger();
            average += nums[i];
        }
        n = n * 2;
        average /= m;

        double res = 1e15;

        for (int time = 0; time < 20; time++) {
            Arrays.fill(sums, 0);

            for (int i = 0; i < n; i++) {
                numIns[i] = random.nextInt(m);
                sums[numIns[i]] += nums[i];
            }

            double temperature = 100;
            double k = 1e-3;
            double r = 0.98;
            double w = calc();
            int half_n = n / 2;
            while (temperature > 1e-3) {

                int id1 = random.nextInt(n);
                int id2 = random.nextInt(n);
                if (id1 > half_n) {
                    id1 -= half_n;
                }


                sums[numIns[id1]] += nums[id2] - nums[id1];
                sums[numIns[id2]] += nums[id1] - nums[id2];

                double nw = calc();
                if (nw < w || random.nextDouble() < Math.exp((w - nw) / (temperature * k))) {
                    int tmp = numIns[id1];
                    numIns[id1] = numIns[id2];
                    numIns[id2] = tmp;
                    w = nw;
                } else {
                    sums[numIns[id1]] -= nums[id2] - nums[id1];
                    sums[numIns[id2]] -= nums[id1] - nums[id2];
                }

                temperature *= r;
            }
            res = Math.min(res, w);
        }


        return Math.sqrt(res / m);
    }

    public double calc() {
        double sum = 0;
        for (int i = 0; i < m; i++) {
            double d = sums[i] - average;
            sum += d * d;
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