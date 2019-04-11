package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dalt on 2018/1/16.
 */
public class BZOJ1857 {
    public static final double PREC = 1e-5;
    public static BlockReader input;
    double ax, ay, bx, by, cx, cy, dx, dy;

    double l1, l2;

    double v1, v2, v3;


    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ1857.in"));

        input = new BlockReader(System.in);

        BZOJ1857 solution = new BZOJ1857();
        //   solution.before();
        System.out.println(String.format("%.2f", solution.solve()));
    }

    public double solve() {
        ax = input.nextInteger();
        ay = input.nextInteger();
        bx = input.nextInteger();
        by = input.nextInteger();
        cx = input.nextInteger();
        cy = input.nextInteger();
        dx = input.nextInteger();
        dy = input.nextInteger();
        v1 = input.nextInteger();
        v2 = input.nextInteger();
        v3 = input.nextInteger();

        l1 = distance(ax, ay, bx, by);
        l2 = distance(cx, cy, dx, dy);

        double l = 0, r = 1;
        while (r - l > PREC) {
            double space = (r - l) / 3;
            double k1 = l + space;
            double k2 = r - space;
            double v1 = f2(k1);
            double v2 = f2(k2);

            if (v1 > v2) {
                l = k1;
            } else {
                r = k2;
            }
        }

        return f2((l + r) / 2);
    }

    public double distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double f2(double t) {
        double l = 0, r = 1;
        while (r - l > PREC) {
            double space = (r - l) / 3;
            double k1 = l + space;
            double k2 = r - space;
            double v1 = f1(t, k1);
            double v2 = f1(t, k2);

            if (v1 > v2) {
                l = k1;
            } else {
                r = k2;
            }
        }
        return f1(t, (l + r) / 2);
    }

    public double f1(double t, double k) {
        double k_1 = 1 - k;
        double t_1 = 1 - t;
        return k * l1 / v1
                + t_1 * l2 / v2
                + distance(k_1 * ax + k * bx, k_1  * ay + k * by,
                t_1 * cx + t * dx, t_1 * cy + t * dy) / v3;
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
