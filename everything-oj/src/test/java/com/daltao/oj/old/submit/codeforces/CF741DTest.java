package com.daltao.oj.old.submit.codeforces;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.InputMismatchException;

public class CF741DTest {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\CF741D.exe")))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(D6.class)))
                .setInputFactory(new Generator())
                .setTimeLimitForEachTestCase(100000000)
                .setTestTime(1000)
                .build().call()
        );
    }

    public static class D6 {
        InputStream is;
        PrintWriter out;
        String INPUT = "";
        int Z = 22;

        void solve() {
            int n = ni();
            int[] from = new int[n - 1];
            int[] to = new int[n - 1];
            int[] w = new int[n - 1];
            for (int i = 0; i < n - 1; i++) {
                from[i] = i + 1;
                to[i] = ni() - 1;
                w[i] = nc() - 'a';
            }
            int[][][] g = packWU(n, from, to, w);
            int[][] pars = parents(g, 0);
            int[] par = pars[0], ord = pars[1], dep = pars[2], pw = pars[4];
            int[] des = new int[n];
            for (int i = n - 1; i >= 0; i--) {
                int cur = ord[i];
                des[cur]++;
                if (i > 0) des[par[cur]] += des[cur];
            }
            int[] ans = new int[n];
            int[] maxdeps = new int[1 << Z];
            Arrays.fill(maxdeps, SMALL);
            dfs(0, -1, 0, g, maxdeps, true, des, dep, ans);
            for (int v : ans) {
                out.print(v + " ");
            }
            out.println();
        }

        void dfs(int cur, int pre, int x, int[][][] g, int[] maxdeps, boolean preserve, int[] des, int[] dep, int[] ans) {
            int big = -1;
            int bigw = -1;
            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                if (big == -1 || des[e[0]] > des[big]) {
                    big = e[0];
                    bigw = e[1];
                }
            }
            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                if (e[0] != big) {
                    dfs(e[0], cur, x ^ 1 << e[1], g, maxdeps, false, des, dep, ans);
                }
            }
            if (big != -1) dfs(big, cur, x ^ 1 << bigw, g, maxdeps, true, des, dep, ans);

            // postprocess
            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                ans[cur] = Math.max(ans[cur], ans[e[0]]);
            }

            ans[cur] = Math.max(ans[cur], maxdeps[x] + dep[cur] - dep[cur] * 2);
            for (int i = 0; i < Z; i++) {
                ans[cur] = Math.max(ans[cur], maxdeps[x ^ 1 << i] + dep[cur] - dep[cur] * 2);
            }
            maxdeps[x] = Math.max(maxdeps[x], dep[cur]);

            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                if (e[0] != big) {
                    ans[cur] = Math.max(ans[cur], ansdfs(e[0], cur, x ^ 1 << e[1], g, maxdeps, dep) - dep[cur] * 2);
                    paintdfs(e[0], cur, x ^ 1 << e[1], g, maxdeps, dep);
                }
            }
            if (!preserve) {
                for (int[] e : g[cur]) {
                    if (e[0] == pre) continue;
                    cleardfs(e[0], cur, x ^ 1 << e[1], g, maxdeps);
                }
                maxdeps[x] = SMALL;
            }
        }

        int SMALL = -99999999;

        void paintdfs(int cur, int pre, int x, int[][][] g, int[] maxdeps, int[] dep) {
            maxdeps[x] = Math.max(maxdeps[x], dep[cur]);
            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                paintdfs(e[0], cur, x ^ 1 << e[1], g, maxdeps, dep);
            }
        }

        void cleardfs(int cur, int pre, int x, int[][][] g, int[] maxdeps) {
            maxdeps[x] = SMALL;
            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                cleardfs(e[0], cur, x ^ 1 << e[1], g, maxdeps);
            }
        }

        int ansdfs(int cur, int pre, int x, int[][][] g, int[] maxdeps, int[] dep) {
            int max = maxdeps[x] + dep[cur];
            for (int i = 0; i < Z; i++) {
                max = Math.max(max, maxdeps[x ^ 1 << i] + dep[cur]);
            }
            for (int[] e : g[cur]) {
                if (e[0] == pre) continue;
                max = Math.max(max, ansdfs(e[0], cur, x ^ 1 << e[1], g, maxdeps, dep));
            }
            return max;
        }

        public static int[][] parents(int[][][] g, int root) {
            int n = g.length;
            int[] par = new int[n];
            Arrays.fill(par, -1);
            int[] dw = new int[n];
            int[] pw = new int[n];
            int[] dep = new int[n];

            int[] q = new int[n];
            q[0] = root;
            for (int p = 0, r = 1; p < r; p++) {
                int cur = q[p];
                for (int[] nex : g[cur]) {
                    if (par[cur] != nex[0]) {
                        q[r++] = nex[0];
                        par[nex[0]] = cur;
                        dep[nex[0]] = dep[cur] + 1;
                        dw[nex[0]] = dw[cur] + nex[1];
                        pw[nex[0]] = nex[1];
                    }
                }
            }
            return new int[][]{par, q, dep, dw, pw};
        }


        public static int[][][] packWU(int n, int[] from, int[] to, int[] w) {
            int[][][] g = new int[n][][];
            int[] p = new int[n];
            for (int f : from)
                p[f]++;
            for (int t : to)
                p[t]++;
            for (int i = 0; i < n; i++)
                g[i] = new int[p[i]][2];
            for (int i = 0; i < from.length; i++) {
                --p[from[i]];
                g[from[i]][p[from[i]]][0] = to[i];
                g[from[i]][p[from[i]]][1] = w[i];
                --p[to[i]];
                g[to[i]][p[to[i]]][0] = from[i];
                g[to[i]][p[to[i]]][1] = w[i];
            }
            return g;
        }


        void run() throws Exception {
            is = oj ? System.in : new ByteArrayInputStream(INPUT.getBytes());
            out = new PrintWriter(System.out);

            Thread t = new Thread(null, null, "~", Runtime.getRuntime().maxMemory()) {
                @Override
                public void run() {
                    long s = System.currentTimeMillis();
                    solve();
                    out.flush();
                    if (!INPUT.isEmpty()) tr(System.currentTimeMillis() - s + "ms");
                }
            };
            t.start();
            t.join();

//		long s = System.currentTimeMillis();
//		solve();
//		out.flush();
//		tr(System.currentTimeMillis()-s+"ms");
        }

        public static void main(String[] args) throws Exception {
            new D6().run();
        }

        private byte[] inbuf = new byte[1024];
        private int lenbuf = 0, ptrbuf = 0;

        private int readByte() {
            if (lenbuf == -1) throw new InputMismatchException();
            if (ptrbuf >= lenbuf) {
                ptrbuf = 0;
                try {
                    lenbuf = is.read(inbuf);
                } catch (IOException e) {
                    throw new InputMismatchException();
                }
                if (lenbuf <= 0) return -1;
            }
            return inbuf[ptrbuf++];
        }

        private boolean isSpaceChar(int c) {
            return !(c >= 33 && c <= 126);
        }

        private int skip() {
            int b;
            while ((b = readByte()) != -1 && isSpaceChar(b)) ;
            return b;
        }

        private double nd() {
            return Double.parseDouble(ns());
        }

        private char nc() {
            return (char) skip();
        }

        private String ns() {
            int b = skip();
            StringBuilder sb = new StringBuilder();
            while (!(isSpaceChar(b))) { // when nextLine, (isSpaceChar(b) && b != ' ')
                sb.appendCodePoint(b);
                b = readByte();
            }
            return sb.toString();
        }

        private char[] ns(int n) {
            char[] buf = new char[n];
            int b = skip(), p = 0;
            while (p < n && !(isSpaceChar(b))) {
                buf[p++] = (char) b;
                b = readByte();
            }
            return n == p ? buf : Arrays.copyOf(buf, p);
        }

        private char[][] nm(int n, int m) {
            char[][] map = new char[n][];
            for (int i = 0; i < n; i++) map[i] = ns(m);
            return map;
        }

        private int[] na(int n) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = ni();
            return a;
        }

        private int ni() {
            int num = 0, b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private long nl() {
            long num = 0;
            int b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private boolean oj = System.getProperty("ONLINE_JUDGE") != null;

        private void tr(Object... o) {
            if (!oj) System.out.println(Arrays.deepToString(o));
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            int n = nextInt(1, 50);
            QueueInput input = new QueueInput();
            input.add(n);
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %s", nextInt(1, i - 1), RandomUtils.getRandomString(random, 'a', 'v', 1).charAt(0)));
            }
            return input.end();
        }
    }
}
