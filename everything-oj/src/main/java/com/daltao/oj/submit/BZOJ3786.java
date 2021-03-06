package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ3786 {
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
            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
            for (int i = 2; i <= n; i++) {
                Node fa = nodes[io.readInt()];
                fa.nodes.add(nodes[i]);
                nodes[i].fa = fa;
            }
            for (int i = 1; i <= n; i++) {
                nodes[i].w = io.readInt();
            }

            dfs(nodes[1], null);

            int m = io.readInt();
            for (int i = 0; i < m; i++) {
                char c = io.readChar();
                if (c == 'Q') {
                    int d = io.readInt();
                    Splay.splay(nodes[d].head);
                    io.cache.append(nodes[d].head.sum -
                            nodes[d].head.right.sum).append('\n');
                } else if (c == 'C') {
                    int x = io.readInt();
                    int y = io.readInt();
                    detach(nodes[x]);
                    attach(nodes[x], nodes[y]);
                    nodes[x].fa = nodes[y];
                } else {
                    int p = io.readInt();
                    int q = io.readInt();
                    if(p != 1) {
                        detach(nodes[p]);
                    }
                    Splay.splay(nodes[p].head);
                    nodes[p].head.modWeight(q);
                    if(p != 1) {
                        attach(nodes[p], nodes[p].fa);
                    }
                }
            }
        }

        public Splay detach(Node x) {
            Splay.splay(x.head);
            Splay l = Splay.splitLeft(x.head);
            Splay.splay(x.tail);
            Splay r = Splay.splitRight(x.tail);
            return Splay.merge(l, r);
        }

        public void attach(Node x, Node y) {
            Splay.splay(y.tail);
            Splay l = Splay.splitLeft(y.tail);
            Splay.splay(x.head);
            Splay.merge(l, x.head);
            Splay.splay(l);
            Splay.merge(l, y.tail);
        }

        Splay splay = Splay.NIL;

        public void dfs(Node root, Node fa) {
            root.head = new Splay();
            root.head.sign = 1;
            root.head.weight = root.w;
            root.head.pushUp();
            root.head.key = root.id;
            root.tail = new Splay();
            root.tail.sign = -1;
            root.tail.weight = -root.w;
            root.tail.pushUp();
            root.tail.key = -root.id;

            Splay.splay(splay);
            splay = Splay.merge(splay, root.head);

            for (Node node : root.nodes) {
                if (node == fa) {
                    continue;
                }
                dfs(node, root);
            }

            Splay.splay(splay);
            Splay.merge(splay, root.tail);
        }
    }

    public static class Node {
        List<Node> nodes = new ArrayList(2);
        Splay head;
        Splay tail;
        Node fa;
        int w;
        int id;
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
        }

        Splay left = NIL;
        Splay right = NIL;
        Splay father = NIL;
        int size = 1;
        int key;

        int sign;
        long sum;
        long weight;
        long modWeight;

        public void modWeight(long mod) {
            weight += sign * mod;
            modWeight += mod;
            sum += size * mod;
        }

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
            size = left.size + right.size + sign;
            sum = left.sum + right.sum + weight;
        }

        public void pushDown() {
            if (this == NIL) {
                return;
            }
            if (modWeight != 0) {
                left.modWeight(modWeight);
                right.modWeight(modWeight);
                modWeight = 0;
            }
        }

        public static int toArray(Splay root, int[] data, int offset) {
            if (root == NIL) {
                return offset;
            }
            offset = toArray(root.left, data, offset);
            data[offset++] = root.key;
            offset = toArray(root.right, data, offset);
            return offset;
        }

        public static void toString(Splay root, StringBuilder builder) {
            if (root == NIL) {
                return;
            }
            root.pushDown();
            toString(root.left, builder);
            builder.append(root.key).append(',');
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

        public static Splay add(Splay root, Splay node) {
            if (root == NIL) {
                return node;
            }
            Splay p = root;
            while (root != NIL) {
                p = root;
                root.pushDown();
                if (root.key < node.key) {
                    root = root.right;
                } else {
                    root = root.left;
                }
            }

            if (p.key < node.key) {
                p.setRight(node);
            } else {
                p.setLeft(node);
            }
            p.pushUp();
            splay(node);
            return node;
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

        public static Splay selectKeyAsRoot(Splay root, int k) {
            if (root == NIL) {
                return NIL;
            }
            Splay trace = root;
            Splay father = NIL;
            Splay find = NIL;
            while (trace != NIL) {
                father = trace;
                trace.pushDown();
                if (trace.key > k) {
                    trace = trace.left;
                } else {
                    if (trace.key == k) {
                        find = trace;
                        trace = trace.left;
                    } else {
                        trace = trace.right;
                    }
                }
            }

            splay(father);
            if (find != NIL) {
                splay(find);
                return find;
            }
            return father;
        }

        public static Splay bruteForceMerge(Splay a, Splay b) {
            if (a == NIL) {
                return b;
            } else if (b == NIL) {
                return a;
            }
            if (a.size < b.size) {
                Splay tmp = a;
                a = b;
                b = tmp;
            }

            a = selectMaxAsRoot(a);
            int k = a.key;
            while (b != NIL) {
                b = selectMinAsRoot(b);
                if (b.key >= k) {
                    break;
                }
                Splay kickedOut = b;
                b = deleteRoot(b);
                a = add(a, kickedOut);
            }
            return merge(a, b);
        }

        public static Splay[] split(Splay root, int key) {
            if (root == NIL) {
                return new Splay[]{NIL, NIL};
            }
            Splay p = root;
            while (root != NIL) {
                p = root;
                root.pushDown();
                if (root.key > key) {
                    root = root.left;
                } else {
                    root = root.right;
                }
            }

            splay(p);
            if (p.key <= key) {
                return new Splay[]{p, splitRight(p)};
            } else {
                return new Splay[]{splitLeft(p), p};
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder().append(key).append(":");
            toString(cloneTree(this), builder);
            return builder.toString();
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
}
