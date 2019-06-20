package com.daltao.template;

public class ModMatrix {
    int[][] mat;
    int n;
    int m;

    public ModMatrix(ModMatrix model) {
        n = model.n;
        m = model.m;
        mat = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                mat[i][j] = model.mat[i][j];
            }
        }
    }

    public ModMatrix(int n, int m) {
        this.n = n;
        this.m = m;
        mat = new int[n][m];
    }

    public ModMatrix(int[][] mat) {
        this.n = mat.length;
        this.m = mat[0].length;
        this.mat = mat;
    }

    public void fill(int v) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                mat[i][j] = v;
            }
        }
    }

    public void asStandard() {
        fill(0);
        for (int i = 0; i < n && i < m; i++) {
            mat[i][i] = 1;
        }
    }

    public static ModMatrix mul(ModMatrix a, ModMatrix b, MathUtils.Modular modular) {
        ModMatrix c = new ModMatrix(a.n, b.m);
        for (int i = 0; i < c.n; i++) {
            for (int j = 0; j < c.m; j++) {
                for (int k = 0; k < a.m; k++) {
                    c.mat[i][j] = modular.plus(c.mat[i][j], modular.mul(a.mat[i][k], b.mat[k][j]));
                }
            }
        }
        return c;
    }

    public static ModMatrix pow(ModMatrix x, long n, MathUtils.Modular modular) {
        if (n == 0) {
            ModMatrix r = new ModMatrix(x.n, x.m);
            r.asStandard();
            return r;
        }
        ModMatrix r = pow(x, n >> 1, modular);
        r = ModMatrix.mul(r, r, modular);
        if (n % 2 == 1) {
            r = ModMatrix.mul(r, x, modular);
        }
        return r;
    }

    static ModMatrix transposition(ModMatrix x, MathUtils.Modular modular) {
        int n = x.n;
        int m = x.m;
        ModMatrix t = new ModMatrix(m, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                t.mat[j][i] = x.mat[i][j];
            }
        }
        return t;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                builder.append(mat[i][j]).append(' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}