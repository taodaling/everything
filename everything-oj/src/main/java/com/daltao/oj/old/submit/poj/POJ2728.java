package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/1/14.
 */
public class POJ2728 {
    public static final int N = 1000;
    public static final double PREC = (double) 1e-5;
    public static final double INF = (double) 1e15;
    public static BlockReader input;
    public static double[] villageX = new double[N];
    public static double[] villageY = new double[N];
    public static double[] villageZ = new double[N];
    public static double[] distances = new double[N];
    public static boolean[] visits = new boolean[N];
    public static double[][] edgeLens = new double[N][N];
    public static double[][] edgeWeights = new double[N][N];
    public static double[][] edgeCost = new double[N][N];
    public static int n;

    public static void main(String[] args) throws FileNotFoundException {
        //System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\Code.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            n = input.nextInteger();
            if (n == 0) {
                break;
            }
            POJ2728 solution = new POJ2728();
            System.out.println(String.format("%.3f", solution.solve()));
            // System.out.println(String.format("%.3f", 0.00049));
        }
    }

    public static double power2(double v) {
        return v * v;
    }

    public double solve() {
        //Read the positions of villages
        for (int i = 0; i < n; i++) {
            villageX[i] = input.nextInteger();
            villageY[i] = input.nextInteger();
            villageZ[i] = input.nextInteger();
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                edgeCost[i][j] = edgeCost[j][i] = Math.abs(villageZ[i] - villageZ[j]);
                edgeLens[i][j] = edgeLens[j][i] = Math.sqrt(power2(villageX[i] - villageX[j]) + power2(villageY[i] - villageY[j]));
            }
        }

        double left = 0;
        double right = 10000000;
        while (right - left > PREC) {
            double mid = (right + left) / 2;
            double sum = prim(mid);
            if (sum > 0) {
                left = mid;
            } else {
                right = mid;
            }
        }

        return (left + right) / 2;
    }

    public double prim(double x) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                edgeWeights[i][j] = edgeCost[i][j] - x * edgeLens[i][j];
            }
        }

        for (int i = 0; i < n; i++) {
            distances[i] = INF;
            visits[i] = false;
        }

        double sum = 0;
        distances[0] = 0;
        for (int i = 0; i < n; i++) {
            int minId = -1;
            double minWeight = INF;
            for (int j = 0; j < n; j++) {
                if (!visits[j] && distances[j] < minWeight) {
                    minWeight = distances[j];
                    minId = j;
                }
            }

            visits[minId] = true;
            sum += minWeight;

            for (int j = 0; j < n; j++) {
                distances[j] = Math.min(distances[j], edgeWeights[minId][j]);
            }
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
