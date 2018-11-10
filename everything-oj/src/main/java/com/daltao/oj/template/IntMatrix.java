package com.daltao.oj.template;

public class IntMatrix implements Cloneable {
    private int[][] data;

    public IntMatrix(int r, int c) {
        this.data = new int[r][c];
    }

    public void set(int i, int j, int val) {
        data[i][j] = val;
    }

    public int get(int i, int j) {
        return data[i][j];
    }

    public void fill(int v) {
        int r = getRowCount();
        int c = getColumnCount();
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                data[i][j] = v;
            }
        }
    }

    public void asStandard() {
        int r = getRowCount();
        int c = getColumnCount();
        if (r != c) {
            throw new UnsupportedOperationException();
        }

        fill(0);
        for (int i = 0; i < r; i++) {
            data[i][i] = 1;
        }
    }

    public static void mul(IntMatrix a, IntMatrix b, IntMatrix output) {
        int h = output.data.length;
        int w = output.data[0].length;
        int t = b.data.length;

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                output.data[i][j] = 0;
                for (int k = 0; k < t; k++) {
                    output.data[i][j] += a.data[i][k] * b.data[k][j];
                }
            }
        }
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return data[0].length;
    }

    public void copy(IntMatrix a) {
        int r = a.data.length;
        int c = a.data[0].length;
        for (int i = 0; i < r; i++) {
            System.arraycopy(a.data[i], 0, data[i], 0, c);
        }
    }

    public static IntMatrix copyOf(IntMatrix a) {
        IntMatrix matrix = new IntMatrix(a.getRowCount(), a.getColumnCount());
        matrix.copy(a);
        return matrix;
    }

    public static IntMatrix mul(IntMatrix a, IntMatrix b) {
        IntMatrix result = new IntMatrix(a.data.length, b.data[0].length);
        mul(a, b, result);
        return result;
    }

    /**
     * Get x^n, you are supposed to pass a loop with length at least 2.
     * You can get the result(x^n) by matrixLoop.get(0).
     */
    public static void pow(IntMatrix x, int n, Loop<IntMatrix> matrixLoop) {
        int offset = 31 - Integer.numberOfLeadingZeros(n);

        matrixLoop.get(0).asStandard();
        for (; offset >= 0; offset--) {
            mul(matrixLoop.get(0), matrixLoop.get(0), matrixLoop.turn(1));
            if (((n >> offset) & 1) != 0) {
                mul(matrixLoop.get(0), x, matrixLoop.turn(1));
            }
        }
    }
}
