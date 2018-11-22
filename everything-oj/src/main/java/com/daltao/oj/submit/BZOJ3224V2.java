package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Random;

public class BZOJ3224V2 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io);

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

        public Task(FastIO io) {
            this.io = io;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();

            SplayNode root = SplayNode.NIL;
            for (int i = 0; i < n; i++) {
                int cmd = io.readInt();
                int x = io.readInt();
                switch (cmd) {
                    case 1: {
                        //insert
                        root = SplayNode.asRoot(root, x);
                        if (root.key != x) {
                            SplayNode node = new SplayNode();
                            node.key = x;
                            if (root.key < x) {
                                node.setRight(root.right);
                                node.pushUp();
                                root.setRight(node);
                                root.pushUp();
                            } else {
                                root.setLeft(node);
                                root.pushUp();
                            }
                            SplayNode.splay(node);
                            root = node;
                        }

                        root.cnt++;
                        root.pushUp();
                    }
                    break;
                    case 2: {
                        //delete
                        root = SplayNode.asRoot(root, x);
                        if (root.key == x) {
                            root.cnt--;
                            root.pushUp();
                            if (root.cnt == 0) {
                                if (root.left == SplayNode.NIL || root.right == SplayNode.NIL) {
                                    if (root.left == SplayNode.NIL) {
                                        root = root.right;
                                    } else {
                                        root = root.left;
                                    }
                                    root.father = SplayNode.NIL;
                                } else {
                                    root.left.father = SplayNode.NIL;
                                    SplayNode left = SplayNode.asRoot(root.left, x);
                                    left.setRight(root.right);
                                    left.pushUp();
                                    root = left;
                                }
                            }
                        }
                    }
                    break;
                    case 3: {
                        //query rank
                        root = SplayNode.asRoot(root, x - 1);
                        io.cache.append(root.size - root.right.size + 1).append('\n');
                        break;
                    }
                    case 4: {
                        //query k-th
                        int k = x;
                        SplayNode trace = root;
                        while (true) {
                            if (trace.left.size >= k) {
                                trace = trace.left;
                            } else {
                                k -= trace.size - trace.right.size;
                                if (k <= 0) {
                                    break;
                                } else {
                                    trace = trace.right;
                                }
                            }
                        }
                        SplayNode.splay(trace);
                        root = trace;
                        io.cache.append(trace.key).append('\n');
                    }
                    break;
                    case 5: {
                        root = SplayNode.asRoot(root, x - 1);
                        io.cache.append(root.key).append('\n');
                        break;
                    }
                    case 6: {
                        root = SplayNode.asRoot(root, x);
                        root.right.father = SplayNode.NIL;
                        root.setRight(SplayNode.asRoot(root.right, x));
                        root.pushUp();

                        io.cache.append(root.right.key).append('\n');
                        break;
                    }
                }
            }
        }
    }

    public static class FastIO {
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        public final StringBuilder cache = new StringBuilder();

        private byte[] buf = new byte[1 << 13];
        private int bufLen;
        private int bufOffset;
        private int next;

        public FastIO(InputStream is, OutputStream os, Charset charset) {
            this.is = is;
            this.os = os;
            this.charset = charset;
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
            long num = readLong();
            if (next != '.') {
                return num;
            }

            next = read();
            long divisor = 1;
            long later = 0;
            while (next >= '0' && next <= '9') {
                divisor = divisor * 10;
                later = later * 10 + next - '0';
                next = read();
            }

            if (num >= 0) {
                return num + (later / (double) divisor);
            } else {
                return num - (later / (double) divisor);
            }
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

        public void flush() {
            try {
                os.write(cache.toString().getBytes(charset));
                os.flush();
                cache.setLength(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean hasMore() {
            skipBlank();
            return next != -1;
        }
    }

    public static class Memory {
        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> int min(T[] data, int from, int to, Comparator<T> cmp) {
            int m = from;
            for (int i = from + 1; i < to; i++) {
                if (cmp.compare(data[m], data[i]) > 0) {
                    m = i;
                }
            }
            return m;
        }

        public static <T> void move(T[] data, int from, int to, int step) {
            int len = to - from;
            step = len - (step % len + len) % len;
            Object[] buf = new Object[len];
            for (int i = 0; i < len; i++) {
                buf[i] = data[(i + step) % len + from];
            }
            System.arraycopy(buf, 0, data, from, len);
        }

        public static <T> void reverse(T[] data, int f, int t) {
            int l = f, r = t - 1;
            while (l < r) {
                swap(data, l, r);
                l++;
                r--;
            }
        }

        public static void copy(Object[] src, Object[] dst, int srcf, int dstf, int len) {
            if (len < 8) {
                for (int i = 0; i < len; i++) {
                    dst[dstf + i] = src[srcf + i];
                }
            } else {
                System.arraycopy(src, srcf, dst, dstf, len);
            }
        }
    }

    public static class SplayNode {
        public static final SplayNode NIL = new SplayNode();

        static {
            NIL.left = NIL;
            NIL.right = NIL;
            NIL.father = NIL;
        }

        SplayNode left = NIL;
        SplayNode right = NIL;
        SplayNode father = NIL;

        int cnt;
        int size;
        int key;

        /**
         * Make the largest key node as the root of this tree and return new root.
         * If no node less than or equal to key, then the smallest key node will be root.
         */
        public static SplayNode asRoot(SplayNode node, int key) {
            if (node == NIL) {
                return NIL;
            }

            SplayNode parent = NIL;
            SplayNode trace = node;
            while (trace != NIL && trace.key != key) {
                parent = trace;
                if (trace.key > key) {
                    trace = trace.left;
                } else {
                    trace = trace.right;
                }
            }

            if (trace != NIL) {
                splay(trace);
                return trace;
            }
            splay(parent);
            return parent;
        }

        public static void splay(SplayNode x) {
            if (x == NIL) {
                return;
            }
            SplayNode y, z;
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

        public static void zig(SplayNode x) {
            SplayNode y = x.father;
            SplayNode z = y.father;
            SplayNode b = x.right;

            y.setLeft(b);
            x.setRight(y);
            z.changeChild(y, x);

            y.pushUp();
        }

        public static void zag(SplayNode x) {
            SplayNode y = x.father;
            SplayNode z = y.father;
            SplayNode b = x.left;

            y.setRight(b);
            x.setLeft(y);
            z.changeChild(y, x);

            y.pushUp();
        }

        public void setLeft(SplayNode x) {
            left = x;
            x.father = this;
        }

        public void setRight(SplayNode x) {
            right = x;
            x.father = this;
        }

        public void changeChild(SplayNode y, SplayNode x) {
            if (left == y) {
                setLeft(x);
            } else {
                setRight(x);
            }
        }

        public void pushUp() {
            size = cnt + left.size + right.size;
        }

        public void pushDown() {
        }
    }
}
