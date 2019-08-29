package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ4405 {
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
            int t = io.readInt();
            while (t-- > 0)
                solve();
        }

        int n;
        int m;

        public int idOfBall(int i) {
            return i - 1;
        }

        public int idOfSlot(int i, int j) {
            return n + (i - 1) * 3 + j - 1;
        }

        public int inverseBallId(int i) {
            return i + 1;
        }

        public int inverseSlotId(int i) {
            return (i - n) / 3 + 1;
        }

        public void solve() {
            n = io.readInt();
            m = io.readInt();
            int e = io.readInt();

            EdmondBlossom eb = new EdmondBlossom(n + 3 * m);
            for (int i = 1; i <= m; i++) {
                eb.addEdge(idOfSlot(i, 1), idOfSlot(i, 2));
                eb.addEdge(idOfSlot(i, 2), idOfSlot(i, 3));
                eb.addEdge(idOfSlot(i, 3), idOfSlot(i, 1));
            }

            for (int i = 1; i <= e; i++) {
                int v = io.readInt();
                int u = io.readInt();
                eb.addEdge(idOfBall(v), idOfSlot(u, 1));
                eb.addEdge(idOfBall(v), idOfSlot(u, 2));
                eb.addEdge(idOfBall(v), idOfSlot(u, 3));
            }

            io.cache.append(eb.maxMatch() - n).append('\n');
            for (int i = 1; i <= n; i++) {
                io.cache.append(inverseSlotId(eb.nodes[idOfBall(i)].mate.id)).append(' ');
            }
            io.cache.append('\n');
        }
    }


    public static class EdmondBlossom {
        private static class Node {
            List<Node> next = new ArrayList(2);
            Node pre;
            boolean inTree;
            boolean inQueue;
            Node mate;

            Node p;
            int rank;
            Node tip;
            int depth;
            int id;

            @Override
            public String toString() {
                if (mate == null) {
                    return "" + id;
                }
                return "" + id + "(" + mate.id + ")";
            }

            Node find() {
                return p.p == p ? p : (p = p.find());
            }

            static Node min(Node a, Node b) {
                return a.depth <= b.depth ? a : b;
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
                    a.tip = min(a.tip, b.tip);
                } else {
                    a.p = b;
                    b.tip = min(a.tip, b.tip);
                }
            }
        }

        Node[] nodes;
        int n;
        Deque<Node> deque;

        public EdmondBlossom(int n) {
            this.n = n;
            deque = new ArrayDeque(n);
            nodes = new Node[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
        }

        public void addEdge(int aId, int bId) {
            nodes[aId].next.add(nodes[bId]);
            nodes[bId].next.add(nodes[aId]);
//            if (nodes[aId].mate == null && nodes[bId].mate == null) {
//                nodes[aId].mate = nodes[bId];
//                nodes[bId].mate = nodes[aId];
//            }
        }

        public int maxMatch() {
            for (Node node : nodes) {
                if (node.mate == null) {
                    match(node);
                }
            }
            int match = 0;
            for (Node node : nodes) {
                if (node.mate != null) {
                    match++;
                }
            }
            return match / 2;
        }

        private void expand(Node tail) {
            while (tail != null) {
                Node next = tail.pre.pre;
                tail.mate = tail.pre;
                tail.pre.mate = tail;
                tail = next;
            }
        }


        private boolean match(Node since) {
            for (Node node : nodes) {
                node.inTree = false;
                node.pre = null;
                node.p = node;
                node.rank = 0;
                node.tip = node;
                node.depth = 0;
                node.inQueue = false;
            }
            deque.clear();
            since.inTree = true;
            since.inQueue = true;
            deque.add(since);

            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                head.inQueue = false;
                Node tip = head.find().tip;
                boolean even = tip.depth % 2 == 0;
                for (Node next : head.next) {
                    if ((next == head.mate) == even) {
                        continue;
                    }

                    if (!next.inTree) {

                        if (next.mate == null) {
                            next.pre = head;
                            expand(next);
                            return true;
                        }

                        next.pre = head;
                        next.depth = tip.depth + 1;
                        next.inTree = true;
                        next.inQueue = true;
                        deque.addLast(next);
                        continue;
                    }
                    //even circle
                    if ((tip.depth - next.depth) % 2 != 0) {
                        continue;
                    }
                    //odd circle
                    else {
                        //shrink
                        blossom(head, next);
                    }
                }
            }

            return false;
        }

        private void blossom(Node a, Node b) {
            a = a.find().tip;
            b = b.find().tip;
            while (a != b) {
                if (a.depth < b.depth) {
                    Node tmp = a;
                    a = b;
                    b = tmp;
                }
                //find odd node
                if (!a.inQueue && a.depth % 2 == 1) {
                    a.inQueue = true;
                    deque.addFirst(a);
                }
                Node.merge(a, a.pre);
                a = a.find().tip;
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
}
