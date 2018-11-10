package com.daltao.oj.old.submit.uoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/21.
 */
public class UOJ179 {
    public static double PREC = 1e-8;
    public static double INF = 1e16;

    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\uoj\\UOJ179.in"));
        }
        input = new BlockReader(System.in);

        int n = input.nextInteger();
        int m = input.nextInteger();
        int t = input.nextInteger();

        LP lp = new LP(n, m);
        for (int i = 1; i <= n; i++) {
            lp.notBasicVarId[i] = i;
        }
        for (int i = 1; i <= m; i++) {
            lp.basicVarId[i] = n + i;
        }
        for (int i = 1; i <= n; i++) {
            lp.rows[0][i] = input.nextInteger();
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                lp.rows[i][j] = -input.nextInteger();
            }
            lp.rows[i][0] = input.nextInteger();
        }

        Double r = lp.solve();
        if (r == LP.INFEASIBLE) {
            System.out.println("Infeasible");
        } else if (r == LP.UNBOUND) {
            System.out.println("Unbounded");
        } else {
            System.out.println(r);
            if (t == 1) {
                double[] vars = new double[n + 1];
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i <= m; i++) {
                    if (lp.basicVarId[i] <= n) {
                        vars[lp.basicVarId[i]] = lp.rows[i][0];
                    }
                }

                for (int i = 1; i <= n; i++) {
                    builder.append(vars[i]).append(' ');
                }
                System.out.println(builder);
            }
        }
    }

    public static class LP {
        public static final Double UNBOUND = new Double(0);
        public static final Double INFEASIBLE = new Double(0);
        double[][] rows;
        int rowHeight;
        int rowWidth;
        int[] basicVarId;
        int[] notBasicVarId;
        int appendRowHeight;

        public LP(int varNum, int constrainNum) {
            rowWidth = varNum + 1;
            rowHeight = constrainNum + 1;
            rows = new double[rowHeight + 1][rowWidth + 1];
            basicVarId = new int[rowHeight + 1];
            notBasicVarId = new int[rowWidth + 1];
        }

        public static void swap(int[] data, int i, int j) {
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static void swap(double[] data, int i, int j) {
            double tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static boolean nearEnough(double a, double b) {
            return Math.abs(a - b) < PREC;
        }

        public void pivot(int basicVarIndex, int rowIndex) {
            {
                int tmp = notBasicVarId[basicVarIndex];
                notBasicVarId[basicVarIndex] = basicVarId[rowIndex];
                basicVarId[rowIndex] = tmp;
            }

            {
                double fac = -rows[rowIndex][basicVarIndex];
                rows[rowIndex][basicVarIndex] = -1;
                for (int i = 0; i < rowWidth; i++) {
                    rows[rowIndex][i] /= fac;
                }
            }

            for (int i = 0; i < rowHeight; i++) {
                if (i == rowIndex) {
                    continue;
                }
                double fac = rows[i][basicVarIndex];
                rows[i][basicVarIndex] = 0;
                for (int j = 0; j < rowHeight; j++) {
                    rows[i][j] += rows[rowIndex][j] * fac;
                }
            }
        }

        public Double simplex() {
            while (true) {
                //choose the min id variable xi that satisfy ci > 0
                int notBasicVarId = Integer.MAX_VALUE;
                int notBasicVarIndex = -1;
                for (int i = 1; i < rowWidth; i++) {
                    if (rows[0][i] > PREC && this.notBasicVarId[i] < notBasicVarId) {
                        notBasicVarIndex = i;
                        notBasicVarId = this.notBasicVarId[i];
                    }
                }

                if (notBasicVarIndex == -1) {
                    return rows[0][0];
                }

                //Find the minimum constraint
                double limit = INF;
                int minRowIndex = -1;
                for (int i = 1, bound = rowHeight - appendRowHeight; i < bound; i++) {
                    if (rows[i][notBasicVarIndex] < -PREC && rows[i][0] / -rows[i][notBasicVarIndex] < limit) {
                        minRowIndex = i;
                        limit = rows[i][0] / -rows[i][notBasicVarIndex];
                    }
                }

                if (minRowIndex == -1) {
                    return UNBOUND;
                }

                pivot(notBasicVarIndex, minRowIndex);
            }
        }

        public int indexOfBasicVariable(int id) {
            for (int i = 1; i < rowHeight; i++) {
                if (basicVarId[i] == id) {
                    return i;
                }
            }
            return -1;
        }

        public int indexOfNotBasicVariable(int id) {
            for (int i = 1; i < rowWidth; i++) {
                if (notBasicVarId[i] == id) {
                    return i;
                }
            }
            return -1;
        }

        public Double solve() {
            //Init at first
            //find the min bi in each constraint
            int minRowIndex = 1;
            for (int i = 2; i < rowHeight; i++) {
                if (rows[i][0] < rows[minRowIndex][0]) {
                    minRowIndex = i;
                }
            }

            if (rows[minRowIndex][0] >= 0) {
                return simplex();
            }

            //add variable x0 for each constraint
            swap(rows, 0, rowHeight);
            rowHeight++;
            appendRowHeight = 1;

            rows[0][rowWidth] = -1;
            int auxVarId = 100000000;
            notBasicVarId[rowWidth] = auxVarId;
            for (int i = 1; i < rowHeight; i++) {
                rows[i][rowWidth] = 1;
            }
            rowWidth++;

            //pivot x0 and minRowIndex
            pivot(rowWidth - 1, minRowIndex);
            //solve the lp
            Double r = simplex();
            if (r == UNBOUND || Math.abs(r.doubleValue()) > PREC) {
                return INFEASIBLE;
            }

            int basicVarIndex = indexOfBasicVariable(auxVarId);
            if (basicVarIndex != -1) {
                //pivot x0 with any variable whose coefficient is not zero
                for (int i = 1; i < rowWidth; i++) {
                    if (!nearEnough(rows[basicVarIndex][i], 0)) {
                        pivot(i, basicVarIndex);
                        break;
                    }
                }
            }

            //Remove x0 from all constraint
            int notBasicVarIndex = indexOfNotBasicVariable(auxVarId);
            swap(notBasicVarId, rowWidth - 1, notBasicVarIndex);
            rowWidth--;
            for (int i = 1; i < rowHeight; i++) {
                swap(rows[i], rowWidth, notBasicVarIndex);
            }

            //restore target line
            swap(rows, 0, rowHeight - 1);
            rowHeight--;
            appendRowHeight = 0;
            return solve();
        }
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
