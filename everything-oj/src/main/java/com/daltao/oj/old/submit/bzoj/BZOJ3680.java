package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by dalt on 2018/1/16.
 */
public class BZOJ3680 {
    public static BlockReader input;
    public static Random random = new Random(19950823);
    double[][] guys;
    int n;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ3680.in"));

        input = new BlockReader(System.in);

        BZOJ3680 solution = new BZOJ3680();
        //   solution.before();
        double[] res = new double[2];
        solution.solve(res);
        System.out.println(String.format("%.3f %.3f", res[0], res[1]));
    }

    public void solve(double[] result) {
        n = input.nextInteger();
        guys = new double[n][3];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                guys[i][j] = input.nextInteger();
            }
        }

        double x = random.nextDouble() * 200000 - 100000;
        double y = random.nextDouble() * 200000 - 100000;
        double w = calc(x, y);
        double t = 100000;
        double r = 0.98;
        double k = 1e-20;
        while (t > 1e-3) {
            double nx = x + (random.nextDouble() * 2 - 1) * t;
            double ny = y + (random.nextDouble() * 2 - 1) * t;
            double nw = calc(nx, ny);

            if (nw < w || random.nextDouble() < Math.exp((w - nw) / (k * t))) {
                x = nx;
                y = ny;
                w = nw;
            }

            t *= r;
        }

        result[0] = x;
        result[1] = y;
    }


    public double calc(double x, double y) {
        double sum = 0;
        for (int i = 0; i < n; i++) {
            double[] guy = guys[i];
            double dx = x - guy[0];
            double dy = y - guy[1];
            double dist = Math.sqrt(dx * dx + dy * dy);
            sum += dist * guy[2];
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
