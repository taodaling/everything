package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class POJ1755V2 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io, new Debug(local));

        if (async) {
            Thread t = new Thread(null, task, "dalt", 1 << 27);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
        } else {
            task.run();
        }

        if (local) {
            io.cache.append("\n\n--memory -- \n" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;
        final Debug debug;
        int inf = (int) 1e8;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();
            int[][] speeds = new int[n + 1][4];
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= 3; j++) {
                    speeds[i][j] = io.readInt();
                }
            }

            for (int i = 1; i <= n; i++) {
                LinearProgramming lp = new LinearProgramming(3, n + 1);
                for (int j = 1; j <= n; j++) {
                    if (j == i) {
                        continue;
                    }
                    for (int k = 1; k <= 3; k++) {
                        lp.setConstraintCoefficient(k, j, 1.0 / speeds[j][k] - 1.0 / speeds[i][k]);
                    }
                    lp.setTargetCoefficient(j, 2 * lp.PREC);
                }
                lp.setTargetCoefficient(n + 1, 2 * lp.PREC);
                lp.setConstraintCoefficient(1, 1 + n, 1);
                lp.setConstraintCoefficient(2, 1 + n, 1);
                lp.setConstraintCoefficient(3, 1 + n, 1);
                debug.debug("lp", lp);
                lp.solve();
                if (lp.isUnbound()) {
                    io.cache.append("No\n");
                } else {
                    io.cache.append("Yes\n");
                }
            }

        }
    }


    /**
     * Linear program class.
     * <br>
     * N constraints and M variables.
     * <br>
     * The target is to find an assignment for each variable to make target expression as large as possible.
     * <br>
     * <pre>
     * Maximize t0+t1*x1+...+tm*xm
     * where following constraint satisfied:
     *   c11*x1+...+c1m*xm <= c10
     *   ...
     *   cn1*x1+...+cnm*xm <= cn0
     * </pre>
     */
    public static class LinearProgramming {
        private final double PREC = 1e-12;
        private final double INF = 1e50;
        double[][] mat;
        int[] basicVariables;
        int[] basicVariable2RowIndex;
        boolean unbound;
        boolean infeasible;

        int n;
        int m;

        public LinearProgramming(int n, int m) {
            this.n = n;
            this.m = m + n;
            mat = new double[n + 1][this.m + 2];
            basicVariables = new int[this.m + 2];
            basicVariable2RowIndex = new int[this.m + 2];
            for (int i = 1; i <= this.m; i++) {
                if (i <= m) {
                    basicVariable2RowIndex[i] = -1;
                } else {
                    basicVariable2RowIndex[i] = i - m;
                    basicVariables[i - m] = i;
                }
            }
        }

        public void setConstraintConstant(int constraintId, double noMoreThan) {
            mat[constraintId][0] = noMoreThan;
        }

        public void setConstraintCoefficient(int constraintId, int variableId, double c) {
            mat[constraintId][variableId] = -c;
        }

        public void setTargetConstant(double c) {
            mat[0][0] = c;
        }

        public void setTargetCoefficient(int variableId, double c) {
            mat[0][variableId] = c;
        }

        public double bestSolution() {
            return mat[0][0];
        }

        public boolean isInfeasible() {
            return infeasible;
        }

        public boolean isUnbound() {
            return unbound;
        }

        public double getAssignmentValueForVariable(int i) {
            if (basicVariable2RowIndex[i] == -1) {
                return 0;
            } else {
                return mat[basicVariable2RowIndex[i]][0];
            }
        }

        private boolean initSimplex() {
            if (n == 0) {
                return true;
            }
            int minConstantRow = 1;
            for (int i = 2; i <= n; i++) {
                if (mat[i][0] < mat[minConstantRow][0]) {
                    minConstantRow = i;
                }
            }
            if (mat[minConstantRow][0] >= 0) {
                return true;
            }
            double[] originalTargetExpression = mat[0];
            m++;
            mat[0] = new double[m + 1];
            mat[0][m] = -1;
            basicVariable2RowIndex[m] = -1;
            for (int i = 1; i <= n; i++) {
                mat[i][m] = 1;
            }
            pivot(m, minConstantRow);
            while (simplex()) ;
            if (mat[0][0] != 0 || unbound) {
                infeasible = true;
                unbound = false;
                return false;
            }
            if (basicVariable2RowIndex[m] != -1) {
                int row = basicVariable2RowIndex[m];
                int firstNegativeVariable = -1;
                for (int i = 1; i <= m && firstNegativeVariable == -1; i++) {
                    if (mat[row][i] != 0) {
                        firstNegativeVariable = i;
                    }
                }
                pivot(firstNegativeVariable, row);
            }

            //restore
            m--;
            mat[0] = originalTargetExpression;
            for (int i = 1; i <= m; i++) {
                if (basicVariable2RowIndex[i] == -1) {
                    continue;
                }
                int row = basicVariable2RowIndex[i];
                double c = mat[0][i];
                for (int j = 0; j <= m; j++) {
                    if (j == i) {
                        mat[0][j] = 0;
                        continue;
                    }
                    mat[0][j] += mat[row][j] * c;
                }
            }
            normalize();
            return true;
        }

        public void solve() {
            if (!initSimplex()) {
                return;
            }
            while (simplex()) ;
        }

        private void normalize() {
            for (int i = 0; i <= n; i++) {
                for (int j = 0; j <= m; j++) {
                    if (mat[i][j] >= -PREC && mat[i][j] <= PREC) {
                        mat[i][j] = 0;
                    }
                }
            }
        }

        private void pivot(int variableId, int row) {
            int basicVariableId = basicVariables[row];
            mat[row][basicVariableId] = -1;
            for (int i = 0; i <= m; i++) {
                if (i == variableId) {
                    continue;
                }
                mat[row][i] /= -mat[row][variableId];
            }
            mat[row][variableId] = -1;
            basicVariables[row] = variableId;
            basicVariable2RowIndex[basicVariableId] = -1;
            basicVariable2RowIndex[variableId] = row;
            for (int i = 0; i <= n; i++) {
                if (i == row || mat[i][variableId] == 0) {
                    continue;
                }
                double c = mat[i][variableId];
                for (int j = 0; j <= m; j++) {
                    if (j == variableId) {
                        mat[i][j] = 0;
                        continue;
                    }
                    mat[i][j] += mat[row][j] * c;
                }
            }
            normalize();
        }

        private boolean simplex() {
            int firstPositiveVariableId = -1;
            for (int i = 1; i <= m && firstPositiveVariableId == -1; i++) {
                if (mat[0][i] > 0) {
                    firstPositiveVariableId = i;
                }
            }
            if (firstPositiveVariableId == -1) {
                return false;
            }
            double maxConstraint = INF;
            int maxConstraintRow = -1;
            for (int i = 1; i <= n; i++) {
                if (mat[i][firstPositiveVariableId] >= 0) {
                    continue;
                }
                double constraint = mat[i][0] / (-mat[i][firstPositiveVariableId]);
                if (maxConstraint > constraint) {
                    maxConstraint = constraint;
                    maxConstraintRow = i;
                }
            }
            if (maxConstraintRow == -1) {
                unbound = true;
                return false;
            }
            pivot(firstPositiveVariableId, maxConstraintRow);
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Maximize\n  ").append(mat[0][0]);
            for (int i = 1; i <= m; i++) {
                if (mat[0][i] == 0) {
                    continue;
                }
                builder.append("+").append(mat[0][i]).append("x").append(i);
            }
            builder.append("\n").append("Constraints\n");
            for (int i = 1; i <= n; i++) {
                builder.append("  ");
                for (int j = 1; j <= m; j++) {
                    if (mat[i][j] == 0) {
                        continue;
                    }
                    builder.append(-mat[i][j]).append("x").append(j).append("+");
                }
                if (builder.length() > 0 && builder.charAt(builder.length() - 1) == '+') {
                    builder.setLength(builder.length() - 1);
                }
                builder.append("<=").append(mat[i][0]).append("\n");
            }
            return builder.toString();
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder();
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        private byte[] buf = new byte[1 << 13];
        private int bufLen;
        private int bufOffset;
        private int next;

        public FastIO(InputStream is, OutputStream os, Charset charset) {
            this.is = is;
            this.os = os;
            this.charset = charset;
        }

        public FastIO(InputStream is, OutputStream os) {
            this(is, os, Charset.forName("ascii"));
        }

        private int read() {
            while (bufLen == bufOffset) {
                bufOffset = 0;
                try {
                    bufLen = is.read(buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (bufLen == -1) {
                    return -1;
                }
            }
            return buf[bufOffset++];
        }

        public void skipBlank() {
            while (next >= 0 && next <= 32) {
                next = read();
            }
        }

        public int readInt() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            int val = 0;
            if (sign == 1) {
                while (next >= '0' && next <= '9') {
                    val = val * 10 + next - '0';
                    next = read();
                }
            } else {
                while (next >= '0' && next <= '9') {
                    val = val * 10 - next + '0';
                    next = read();
                }
            }

            return val;
        }

        public long readLong() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            long val = 0;
            if (sign == 1) {
                while (next >= '0' && next <= '9') {
                    val = val * 10 + next - '0';
                    next = read();
                }
            } else {
                while (next >= '0' && next <= '9') {
                    val = val * 10 - next + '0';
                    next = read();
                }
            }

            return val;
        }

        public double readDouble() {
            boolean sign = true;
            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+';
                next = read();
            }

            long val = 0;
            while (next >= '0' && next <= '9') {
                val = val * 10 + next - '0';
                next = read();
            }
            if (next != '.') {
                return sign ? val : -val;
            }
            next = read();
            long radix = 1;
            long point = 0;
            while (next >= '0' && next <= '9') {
                point = point * 10 + next - '0';
                radix = radix * 10;
                next = read();
            }
            double result = val + (double) point / radix;
            return sign ? result : -result;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (next > 32) {
                builder.append((char) next);
                next = read();
            }

            return builder.toString();
        }

        public String readString() {
            defaultStringBuf.setLength(0);
            return readString(defaultStringBuf);
        }

        public int readLine(char[] data, int offset) {
            int originalOffset = offset;
            while (next != -1 && next != '\n') {
                data[offset++] = (char) next;
                next = read();
            }
            return offset - originalOffset;
        }

        public int readString(char[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (char) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public int readString(byte[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (byte) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public char readChar() {
            skipBlank();
            char c = (char) next;
            next = read();
            return c;
        }

        public void flush() throws IOException {
            os.write(cache.toString().getBytes(charset));
            os.flush();
            cache.setLength(0);
        }

        public boolean hasMore() {
            skipBlank();
            return next != -1;
        }
    }

    public static class Debug {
        private boolean allowDebug;

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
        }

        public void assertTrue(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (!flag) {
                fail();
            }
        }

        public void fail() {
            throw new RuntimeException();
        }

        public void assertFalse(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (flag) {
                fail();
            }
        }

        private void outputName(String name) {
            System.out.print(name + " = ");
        }

        public void debug(String name, int x) {
            if (!allowDebug) {
                return;
            }

            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, long x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, double x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, int[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, long[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, double[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, Object x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, Object... x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.deepToString(x));
        }
    }
}
