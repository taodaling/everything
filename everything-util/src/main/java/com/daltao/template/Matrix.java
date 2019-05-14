package com.daltao.template;

public class Matrix {
        int[][] mat;
        int n;
        int m;
        static int mod = (int) 1e9 + 7;

        public Matrix(int n, int m) {
            this.n = n;
            this.m = m;
            mat = new int[n][m];
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

        public static Matrix mul(Matrix a, Matrix b) {
            Matrix c = new Matrix(a.n, b.m);
            for (int i = 0; i < c.n; i++) {
                for (int j = 0; j < c.m; j++) {
                    for (int k = 0; k < a.m; k++) {
                        c.mat[i][j] = (int) ((c.mat[i][j] + (long) a.mat[i][k] * b.mat[k][j]) % mod);
                    }
                }
            }
            return c;
        }

        public static Matrix pow(Matrix x, int n) {
            if (n == 0) {
                Matrix r = new Matrix(x.n, x.m);
                r.asStandard();
                return r;
            }
            Matrix r = pow(x, n >> 1);
            r = Matrix.mul(r, r);
            if (n % 2 == 1) {
                r = Matrix.mul(r, x);
            }
            return r;
        }

    }