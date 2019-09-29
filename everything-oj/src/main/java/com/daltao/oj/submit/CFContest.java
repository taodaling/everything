package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;


public class CFContest {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = true;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io, new Debug(local));

        if (async) {
            Thread t = new Thread(null, task, "skypool", 1 << 27);
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
        int inf = (int) 1e9;
        long lInf = (long) 1e18;
        double dInf = 1e50;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int n;

        public void solve() {
            n = io.readInt();
            int nodeNum = (1 << n) - 1;
            int m = (1 << n) - 3;
            Node[] nodes = new Node[nodeNum + 1];
            for (int i = 1; i <= nodeNum; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }
            for (int i = 1; i <= m; i++) {
                Node a = nodes[io.readInt()];
                Node b = nodes[io.readInt()];
                a.next.add(b);
                b.next.add(a);
            }

            for (int i = 1; i <= nodeNum; i++) {
                if (nodes[i].next.size() != 0) {
                    Node tmp = nodes[i];
                    nodes[i] = nodes[1];
                    nodes[1] = tmp;
                }
            }

            dfsForDiameter(nodes[1], null);
            Deque<Node> deque = new ArrayDeque<>(nodeNum);
            traceDiameter(nodes[1].farthest, null, 0, nodes[1].diameter, deque);

            for (int i = 0; i < nodes[1].diameter / 2; i++) {
                deque.removeFirst();
            }


            handleNode(deque.removeFirst());
            if (nodes[1].diameter % 2 == 1) {
                handleNode(deque.removeFirst());
            }

            ans.sort(Comparator.naturalOrder());
            io.cache.append(ans.size()).append('\n');
            for (Integer id : ans) {
                io.cache.append(id).append(' ');
            }
        }

        public void handleNode(Node root) {
            root = clone(root, null);
            dfsForHeight(root);
            if (tryRepaire(root) && check(root, n)) {
                ans.add(repaire.id);
            }
        }

        List<Integer> ans = new ArrayList<>();

        public boolean check(Node root, int h) {
            if (h == 1) {
                return root.next.size() == 0;
            }
            if (root.next.size() != 2) {
                return false;
            }
            for (Node node : root.next) {
                if (!check(node, h - 1)) {
                    return false;
                }
            }
            return true;
        }

        Node repaire;

        public boolean tryRepaire(Node root) {
            if (root.next.size() == 1) {
                root.repaire = true;
                root.next.add(new Node());
                repaire = root;
                return true;
            }
            if (root.next.size() == 3) {
                root.repaire = true;
                root.next.sort((a, b) -> a.height - b.height);
                Node node = new Node();
                node.next.addAll(root.next.subList(0, 2));
                root.next.removeAll(node.next);
                root.next.add(node);
                repaire = root;
                return true;
            }
            for (Node node : root.next) {
                if (tryRepaire(node)) {
                    return true;
                }
            }
            return false;
        }

        public void dfsForHeight(Node root) {
            root.height = 1;
            for (Node node : root.next) {
                dfsForHeight(node);
                root.height = Math.max(root.height, node.height + 1);
            }
        }

        public Node clone(Node root, Node father) {
            Node clone = new Node();
            clone.id = root.id;
            for (Node node : root.next) {
                if (node == father) {
                    continue;
                }
                Node nodeClone = clone(node, root);
                clone.next.add(nodeClone);
            }
            return clone;
        }

        public boolean traceDiameter(Node root, Node father, int distance, int targetDiameter, Deque<Node> deque) {
            deque.addLast(root);
            if (distance == targetDiameter) {
                return true;
            }
            for (Node node : root.next) {
                if (node == father) {
                    continue;
                }
                if (traceDiameter(node, root, distance + 1, targetDiameter, deque)) {
                    return true;
                }
            }
            deque.removeLast();
            return false;
        }

        public void dfsForDiameter(Node root, Node father) {
            root.farthest = root;
            root.diameter = 0;
            root.depth = father == null ? 0 : father.depth + 1;
            root.depthest = root;
            for (Node node : root.next) {
                if (node == father) {
                    continue;
                }
                dfsForDiameter(node, root);
                if (root.diameter < node.diameter) {
                    root.diameter = node.diameter;
                    root.farthest = node.farthest;
                }
                if (root.diameter < root.depthest.depth + node.depthest.depth - 2 * root.depth) {
                    root.diameter = root.depthest.depth + node.depthest.depth - 2 * root.depth;
                    root.farthest = root.depthest;
                }
                if (root.depthest.depth < node.depthest.depth) {
                    root.depthest = node.depthest;
                }
            }
        }
    }

    public static class Node {
        List<Node> next = new ArrayList<>(2);
        Node farthest;
        Node depthest;
        int depth;
        int diameter;
        int id;
        int height;
        boolean repaire;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(20 << 20);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        private byte[] buf = new byte[1 << 20];
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