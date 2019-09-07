package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ2049 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getSecurityManager() == null;
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
            int m = io.readInt();
            EulerTourTree ett = new EulerTourTree(n);
            char[] cmd = new char[15];
            for (int i = 0; i < m; i++) {
                debug.debug("i", i);
                io.readString(cmd, 0);
                int u = io.readInt() - 1;
                int v = io.readInt() - 1;
                if (cmd[0] == 'C') {
                    ett.link(u, v);
                } else if (cmd[0] == 'D') {
                    ett.cut(u, v);
                } else {
                    int a = ett.rootOf(u);
                    int b = ett.rootOf(v);
                    io.cache.append(a == b
                            ? "Yes" : "No").append('\n');
                }
            }
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(1 << 13);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
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

    public static class EulerTourTree {
        Splay[] nodes;
        Deque<Splay> deque;
        Map<Long, Edge> map;

        public EulerTourTree(int n) {
            deque = new ArrayDeque(3 * n);
            map = new HashMap(n);
            nodes = new Splay[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = alloc(i);
            }
        }

        private Splay alloc(int id) {
            Splay splay = deque.isEmpty() ? new Splay() : deque.removeFirst();
            splay.id = id;
            return splay;
        }

        private void destroy(Splay s) {
            deque.addLast(s);
        }

        public int rootOf(int i) {
            return rootOf(nodes[i]).id;
        }

        public void setRoot(int i) {
            if (rootOf(i) == i) {
                return;
            }

            Splay.splay(nodes[i]);
            Splay l = Splay.splitLeft(nodes[i]);
            if (l == Splay.NIL) {
                return;
            }
            Splay a = Splay.selectMinAsRoot(l);
            Splay b = Splay.selectMaxAsRoot(nodes[i]);

            Splay.splitLeft(b);
            destroy(b);
            nodes[a.id] = a;

            Splay newNode = alloc(i);
            Splay.splay(nodes[i]);
            Splay.splay(l);
            Splay.merge(nodes[i], Splay.merge(l, newNode));
        }

        private long idOfEdge(int i, int j) {
            if (i > j) {
                int tmp = i;
                i = j;
                j = tmp;
            }
            return (((long) i) << 32) | j;
        }

        public void link(int i, int j) {
            setRoot(i);
            setRoot(j);

            Edge e = new Edge();

            long id = idOfEdge(i, j);
            e.a = alloc(-1);
            e.b = alloc(-1);
            map.put(id, e);

            Splay.splay(nodes[i]);
            Splay.splay(nodes[j]);
            Splay.merge(nodes[i], e.a);
            Splay.merge(nodes[j], e.b);
            Splay.splay(nodes[i]);
            Splay.splay(nodes[j]);
            Splay.merge(nodes[i], nodes[j]);

            Splay newNode = alloc(i);
            Splay.splay(nodes[i]);
            Splay.merge(nodes[i], newNode);
        }

        private Splay rootOf(Splay x) {
            Splay.splay(x);
            return Splay.selectMinAsRoot(x);
        }

        public void cut(int i, int j) {
            long id = idOfEdge(i, j);
            Edge e = map.remove(id);

            Splay.splay(e.a);
            Splay al = Splay.splitLeft(e.a);
            Splay ar = Splay.splitRight(e.a);


            Splay l, r;
            if (rootOf(ar) == rootOf(e.b)) {
                Splay.splay(e.b);
                Splay bl = Splay.splitLeft(e.b);
                Splay br = Splay.splitRight(e.b);

                l = al;
                r = br;
            } else {
                Splay.splay(e.b);
                Splay bl = Splay.splitLeft(e.b);
                Splay br = Splay.splitRight(e.b);

                l = bl;
                r = ar;
            }

            Splay.splay(l);
            Splay.splay(r);
            l = Splay.selectMaxAsRoot(l);
            r = Splay.selectMinAsRoot(r);

            Splay rSnapshot = r;
            r = Splay.splitRight(r);
            destroy(rSnapshot);
            nodes[l.id] = l;

            Splay.merge(l, r);
            destroy(e.a);
            destroy(e.b);
        }

        private static class Edge {
            Splay a;
            Splay b;
        }

        /**
         * Created by dalt on 2018/5/20.
         */
        public static class Splay implements Cloneable {
            public static final Splay NIL = new Splay();

            static {
                NIL.left = NIL;
                NIL.right = NIL;
                NIL.father = NIL;
                NIL.size = 0;
                NIL.id = -2;
            }

            Splay left = NIL;
            Splay right = NIL;
            Splay father = NIL;
            int size = 1;
            int id;

            public static void splay(Splay x) {
                if (x == NIL) {
                    return;
                }
                Splay y, z;
                while ((y = x.father) != NIL) {
                    if ((z = y.father) == NIL) {
                        y.pushDown();
                        x.pushDown();
                        if (x == y.left) {
                            zig(x);
                        } else {
                            zag(x);
                        }
                    } else {
                        z.pushDown();
                        y.pushDown();
                        x.pushDown();
                        if (x == y.left) {
                            if (y == z.left) {
                                zig(y);
                                zig(x);
                            } else {
                                zig(x);
                                zag(x);
                            }
                        } else {
                            if (y == z.left) {
                                zag(x);
                                zig(x);
                            } else {
                                zag(y);
                                zag(x);
                            }
                        }
                    }
                }

                x.pushDown();
                x.pushUp();
            }

            public static void zig(Splay x) {
                Splay y = x.father;
                Splay z = y.father;
                Splay b = x.right;

                y.setLeft(b);
                x.setRight(y);
                z.changeChild(y, x);

                y.pushUp();
            }

            public static void zag(Splay x) {
                Splay y = x.father;
                Splay z = y.father;
                Splay b = x.left;

                y.setRight(b);
                x.setLeft(y);
                z.changeChild(y, x);

                y.pushUp();
            }

            public void setLeft(Splay x) {
                left = x;
                x.father = this;
            }

            public void setRight(Splay x) {
                right = x;
                x.father = this;
            }

            public void changeChild(Splay y, Splay x) {
                if (left == y) {
                    setLeft(x);
                } else {
                    setRight(x);
                }
            }

            public void pushUp() {
                if (this == NIL) {
                    return;
                }
                size = left.size + right.size + 1;
            }

            public void pushDown() {
            }

            public static void toString(Splay root, StringBuilder builder) {
                if (root == NIL) {
                    return;
                }
                root.pushDown();
                toString(root.left, builder);
                builder.append(root.id).append(',');
                toString(root.right, builder);
            }

            public Splay clone() {
                try {
                    return (Splay) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }

            public static Splay cloneTree(Splay splay) {
                if (splay == NIL) {
                    return NIL;
                }
                splay = splay.clone();
                splay.left = cloneTree(splay.left);
                splay.right = cloneTree(splay.right);
                return splay;
            }

            /**
             * Make the node with the minimum key as the root of tree
             */
            public static Splay selectMinAsRoot(Splay root) {
                if (root == NIL) {
                    return root;
                }
                root.pushDown();
                while (root.left != NIL) {
                    root = root.left;
                    root.pushDown();
                }
                splay(root);
                return root;
            }

            /**
             * Make the node with the maximum key as the root of tree
             */
            public static Splay selectMaxAsRoot(Splay root) {
                if (root == NIL) {
                    return root;
                }
                root.pushDown();
                while (root.right != NIL) {
                    root = root.right;
                    root.pushDown();
                }
                splay(root);
                return root;
            }

            /**
             * delete root of tree, then merge remain nodes into a new tree, and return the new root
             */
            public static Splay deleteRoot(Splay root) {
                root.pushDown();
                Splay left = splitLeft(root);
                Splay right = splitRight(root);
                return merge(left, right);
            }

            /**
             * detach the left subtree from root and return the root of left subtree
             */
            public static Splay splitLeft(Splay root) {
                root.pushDown();
                Splay left = root.left;
                left.father = NIL;
                root.setLeft(NIL);
                root.pushUp();
                return left;
            }

            /**
             * detach the right subtree from root and return the root of right subtree
             */
            public static Splay splitRight(Splay root) {
                root.pushDown();
                Splay right = root.right;
                right.father = NIL;
                root.setRight(NIL);
                root.pushUp();
                return right;
            }


            public static Splay merge(Splay a, Splay b) {
                if (a == NIL) {
                    return b;
                }
                if (b == NIL) {
                    return a;
                }
                a = selectMaxAsRoot(a);
                a.setRight(b);
                a.pushUp();
                return a;
            }

            public static Splay selectKthAsRoot(Splay root, int k) {
                if (root == NIL) {
                    return NIL;
                }
                Splay trace = root;
                Splay father = NIL;
                while (trace != NIL) {
                    father = trace;
                    trace.pushDown();
                    if (trace.left.size >= k) {
                        trace = trace.left;
                    } else {
                        k -= trace.left.size + 1;
                        if (k == 0) {
                            break;
                        } else {
                            trace = trace.right;
                        }
                    }
                }
                splay(father);
                return father;
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder().append(id).append(":");
                toString(cloneTree(this), builder);
                return builder.toString();
            }
        }
    }

}
