package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;

//package round543;

public class CF1120D {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(D.class)))
                .setTestTime(10000)
                .build().call());
    }



    /**
     * Built using CHelper plug-in Actual solution is at the top
     */
    public static class Main {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "", 1 << 27);
            thread.start();
            thread.join();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                FastOutput out = new FastOutput(outputStream);
                TaskD solver = new TaskD();
                solver.solve(1, in, out);
                out.close();
            }
        }
        static class TaskD {
            List<Node> dpSolutions = new ArrayList<>(1000000);

            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                Node[] nodes = new Node[n + 1];
                for (int i = 1; i <= n; i++) {
                    nodes[i] = new Node();
                    nodes[i].price = in.readInt();
                    nodes[i].id = i;
                }

                for (int i = 1; i < n; i++) {
                    Node a = nodes[in.readInt()];
                    Node b = nodes[in.readInt()];
                    a.next.add(b);
                    b.next.add(a);
                }

                dfsForDp(nodes[1], null);
                out.println(nodes[1].dp);
                dpSolutions.add(nodes[1].max);
                for (Node node : dpSolutions) {
                    node.find().selected = true;
                    for (Node c : node.candidate) {
                        c.find().selected = true;
                    }
                }

                List<Integer> ans = new ArrayList<>(n);
                for (int i = 1; i <= n; i++) {
                    if (nodes[i].find().selected) {
                        ans.add(i);
                    }
                }

                out.println(ans.size());
                for (Integer node : ans) {
                    out.append(node).append(' ');
                }
            }

            public void dfsForDp(Node root, Node father) {
                root.next.remove(father);
                if (root.next.isEmpty()) {
                    root.dp = root.price;
                    root.max = root;
                    return;
                }
                Node max = null;
                int cnt = 0;
                for (Node node : root.next) {
                    dfsForDp(node, root);
                    root.dp += node.dp;
                    if (max == null) {
                        max = node.max;
                        cnt = 1;
                    } else if (node.max.price == max.price) {
                        Node.merge(node.max, max);
                        dpSolutions.add(node.max);
                        cnt++;
                    } else if (node.max.price > max.price) {
                        dpSolutions.add(max);
                        max = node.max;
                        cnt = 1;
                    } else {
                        dpSolutions.add(node.max);
                    }
                }

                if (root.price == max.price) {
                    max.candidate.add(root);
                }
                if (root.price < max.price) {
                    if (cnt == 1) {
                        max.candidate.clear();
                    } else {
                        dpSolutions.add(max);
                    }

                    root.dp -= max.price;
                    root.dp += root.price;
                    max = root;
                }
                root.max = max;
            }

        }
        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(10 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput append(char c) {
                cache.append(c);
                return this;
            }

            public FastOutput append(Object c) {
                cache.append(c);
                return this;
            }

            public FastOutput println(int c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput println(long c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput flush() {
                try {
                    os.append(cache);
                    os.flush();
                    cache.setLength(0);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return this;
            }

            public void close() {
                flush();
                try {
                    os.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public String toString() {
                return cache.toString();
            }

        }
        static class FastInput {
            private final InputStream is;
            private byte[] buf = new byte[1 << 13];
            private int bufLen;
            private int bufOffset;
            private int next;

            public FastInput(InputStream is) {
                this.is = is;
            }

            private int read() {
                while (bufLen == bufOffset) {
                    bufOffset = 0;
                    try {
                        bufLen = is.read(buf);
                    } catch (IOException e) {
                        bufLen = -1;
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

        }
        static class Node {
            List<Node> next = new ArrayList<>();
            long dp;
            long price;
            Node p = this;
            int rank;
            int id;
            boolean selected;
            Node max;
            List<Node> candidate = new ArrayList<>();

            Node find() {
                return p == p.p ? p : (p = p.find());
            }

            static void merge(Node a, Node b) {
                a = a.find();
                b = b.find();
                if (a == b) {
                    return;
                }
                if (a.rank == b.rank) {
                    a.rank++;
                }
                if (a.rank > b.rank) {
                    b.p = a;
                } else {
                    a.p = b;
                }
            }

            public String toString() {
                return "" + id;
            }

        }
    }




    public static class D {
        InputStream is;
        PrintWriter out;
        String INPUT = "";

        void solve() {
            int n = ni();
            int[] a = na(n);
            int[] from = new int[n - 1];
            int[] to = new int[n - 1];
            for (int i = 0; i < n - 1; i++) {
                from[i] = ni() - 1;
                to[i] = ni() - 1;
            }
            int[][] g = packU(n, from, to);
            int[][] pars = parents3(g, 0);
            int[] par = pars[0], ord = pars[1], dep = pars[2];

            long[] dp = new long[n];
            long[] ep = new long[n];
            long[] chas = new long[n];
            for (int i = n - 1; i >= 0; i--) {
                int cur = ord[i];
                if (g[cur].length == 1 && i > 0) {
                    dp[cur] = a[cur];
                    ep[cur] = 0;
                } else {
                    long cha = 0;
                    for (int e : g[cur]) {
                        if (par[cur] == e) continue;

                        dp[cur] += dp[e];
                        if (dp[e] - ep[e] > cha) {
                            cha = dp[e] - ep[e];
                            chas[cur] = cha;
                        }
                    }
                    ep[cur] = dp[cur] - cha;
                    if (ep[cur] + a[cur] < dp[cur]) {
                        dp[cur] = ep[cur] + a[cur];
                    }
                }
            }
            tr(dp);
            tr(ep);

            out.print(dp[0] + " ");
            int[] de = new int[n];
            de[0] = 1;
            List<Integer> ret = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int cur = ord[i];
                if (de[cur] << ~0 < 0) {
                    if (g[cur].length == 1 && i > 0) {
                        ret.add(cur + 1);
                        continue;
                    }
                    long s = 0;
                    for (int e : g[cur]) {
                        if (par[cur] == e) continue;
                        s += dp[e];
                    }
                    if (s <= ep[cur] + a[cur]) {
                        for (int e : g[cur]) {
                            if (par[cur] == e) continue;
                            de[e] |= 1;
                        }
                    }
                    if (s >= ep[cur] + a[cur]) {
                        ret.add(cur + 1);
                        int hit = 0;
                        for (int e : g[cur]) {
                            if (par[cur] == e) continue;
                            if (dp[e] - ep[e] == chas[cur]) {
                                hit++;
                                de[e] |= 2;
                            } else {
                                de[e] |= 1;
                            }
                        }
                        if (hit > 1) {
                            for (int e : g[cur]) {
                                if (par[cur] == e) continue;
                                de[e] |= 1;
                            }
                        }
                    }
                } else {
                    int hit = 0;
                    for (int e : g[cur]) {
                        if (par[cur] == e) continue;
                        if (dp[e] - ep[e] == chas[cur]) {
                            hit++;
                            de[e] |= 2;
                        } else {
                            de[e] |= 1;
                        }
                    }
                    if (hit > 1) {
                        for (int e : g[cur]) {
                            if (par[cur] == e) continue;
                            de[e] |= 1;
                        }
                    }
                }
            }

            out.println(ret.size());
            Collections.sort(ret);
            for (int v : ret) {
                out.print(v + " ");
            }
        }

        public static int[][] parentToChildren(int[] par) {
            int n = par.length;
            int[] ct = new int[n];
            for (int i = 0; i < n; i++) {
                if (par[i] >= 0) {
                    ct[par[i]]++;
                }
            }
            int[][] g = new int[n][];
            for (int i = 0; i < n; i++) {
                g[i] = new int[ct[i]];
            }
            for (int i = 0; i < n; i++) {
                if (par[i] >= 0) {
                    g[par[i]][--ct[par[i]]] = i;
                }
            }

            return g;
        }

        public static int[][] parents3(int[][] g, int root) {
            int n = g.length;
            int[] par = new int[n];
            Arrays.fill(par, -1);

            int[] depth = new int[n];
            depth[0] = 0;

            int[] q = new int[n];
            q[0] = root;
            for (int p = 0, r = 1; p < r; p++) {
                int cur = q[p];
                for (int nex : g[cur]) {
                    if (par[cur] != nex) {
                        q[r++] = nex;
                        par[nex] = cur;
                        depth[nex] = depth[cur] + 1;
                    }
                }
            }
            return new int[][]{par, q, depth};
        }

        static int[][] packU(int n, int[] from, int[] to) {
            int[][] g = new int[n][];
            int[] p = new int[n];
            for (int f : from)
                p[f]++;
            for (int t : to)
                p[t]++;
            for (int i = 0; i < n; i++)
                g[i] = new int[p[i]];
            for (int i = 0; i < from.length; i++) {
                g[from[i]][--p[from[i]]] = to[i];
                g[to[i]][--p[to[i]]] = from[i];
            }
            return g;
        }


        void run() throws Exception {
            is = oj ? System.in : new ByteArrayInputStream(INPUT.getBytes());
            out = new PrintWriter(System.out);

            long s = System.currentTimeMillis();
            solve();
            out.flush();
            tr(System.currentTimeMillis() - s + "ms");
        }

        public static void main(String[] args) throws Exception {
            new D().run();
        }

        private byte[] inbuf = new byte[1024];
        public int lenbuf = 0, ptrbuf = 0;

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

    static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(2, 10);
            in.add(n);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                builder.append(nextInt(0, 10)).append(' ');
            }
            in.add(builder.toString());
            for (int i = 2; i <= n; i++) {
                in.add(String.format("%d %d", nextInt(1, i - 1), i));
            }
            return in.end();
        }
    }
}
