package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by dalt on 2018/1/16.
 */
public class POJ1379 {
    public static BlockReader input;
    public static Random random = new Random();
    public static int m;
    public static double[][] holes = new double[1000][2];
    public static DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\poj\\POJ1379.in"));

        input = new BlockReader(System.in);

        POJ1379 solution = new POJ1379();
        StringBuilder builder = new StringBuilder(4096);
        double[] res = new double[2];
        for (int i = 0, bound = input.nextInteger(); i < bound; i++) {
            solution.solve(res);
            builder.append("The safest point is (").append(decimalFormat.format(res[0])).append(", ").append(decimalFormat.format(res[1]))
                    .append(").\n");
        }
        System.out.print(builder.toString());
    }

    public void solve(double[] res) {
        double xLimit = input.nextInteger();
        double yLimit = input.nextInteger();
        m = input.nextInteger();
        for (int i = 0; i < m; i++) {
            holes[i][0] = input.nextInteger();
            holes[i][1] = input.nextInteger();
        }


        double x = nextDouble(0, xLimit);
        double y = nextDouble(0, yLimit);
        double w = evaluate(x, y);
        double t = Math.max(x, y);
        double r = 1e-2;
        double k = 0.98;
        while (t > 1e-1) {
            double nx = x + nextDouble(-t, t);
            double ny = y + nextDouble(-t, t);

            nx = Math.max(0, nx);
            nx = Math.min(nx, xLimit);
            ny = Math.max(0, ny);
            ny = Math.min(ny, yLimit);

            double nw = evaluate(nx, ny);
            if (nw > w || random.nextDouble() < Math.exp((nw - w) / (t * r))) {
                x = nx;
                y = ny;
                w = nw;
            }
            t *= k;
        }

        res[0] = x;
        res[1] = y;
    }

    public static double nextDouble(double from, double to) {
        return random.nextDouble() * (to - from) + from;
    }

    public double evaluate(double x, double y) {
        double nearest = 1e18;
        for (int i = 0; i < m; i++) {
            double[] dot = holes[i];
            nearest = Math.min(nearest, dist(x, y, dot));
        }
        return nearest;
    }

    public static double dist(double x, double y, double[] pos) {
        double dx = x - pos[0];
        double dy = y - pos[1];
        return dx * dx + dy * dy;
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
