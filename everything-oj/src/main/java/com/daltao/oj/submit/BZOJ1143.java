package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ1143 {
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
            solve();
        }

        public void solve() {
            int n = io.readInt();
            int m = io.readInt();

            boolean[][] path = new boolean[n + 1][n + 1];
            for (int i = 0; i < m; i++) {
                path[io.readInt()][io.readInt()] = true;
            }
            for (int k = 1; k <= n; k++) {
                for (int i = 1; i <= n; i++) {
                    for (int j = 1; j <= n; j++) {
                        path[i][j] = path[i][j] || (path[i][k] && path[k][j]);
                    }
                }
            }

            KMAlgo km = new KMAlgo(n, n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (path[i + 1][j + 1]) {
                        km.addEdge(i, j);
                    }
                }
            }

            int match = 0;
            for (int i = 0; i < n; i++) {
                if (km.matchLeft(i)) {
                    match++;
                }
            }

            int maxAntiChainLength = n - match;
            io.cache.append(maxAntiChainLength).append('\n');

            km.findMinVertexCover();
            for (int i = 0; i < n; i++) {
                if (!km.leftSides[i].inMinVertexCover &&
                        !km.rightSides[i].inMinVertexCover) {
                    io.cache.append(1);
                } else {
                    io.cache.append(0);
                }
            }

            io.cache.append('\n');
            for (int i = 0; i < n; i++) {
                KMAlgo local = new KMAlgo(n, n);
                int remain = 0;
                for (int j = 0; j < n; j++) {
                    if (j == i || path[j + 1][i + 1] || path[i + 1][j + 1]) {
                        continue;
                    }
                    remain++;
                    for (int k = 0; k < n; k++) {
                        if (k == i || path[k + 1][i + 1] || path[i + 1][k + 1]) {
                            continue;
                        }
                        if (path[j + 1][k + 1]) {
                            local.addEdge(j, k);
                        }
                    }
                }
                int localMatch = 0;
                for (int j = 0; j < n; j++) {
                    if (j == i || path[j + 1][i + 1] || path[i + 1][j + 1]) {
                        continue;
                    }
                    if (local.matchLeft(j)) {
                        localMatch++;
                    }
                }

                if (remain - localMatch != maxAntiChainLength - 1) {
                    io.cache.append(0);
                } else {
                    io.cache.append(1);
                }
            }
        }
    }

    public static class KMAlgo {
        public static class Node {
            List<Node> nodes = new ArrayList(2);
            int visited;
            Node partner;
            int id;
            boolean leftSide;
            boolean inMinVertexCover;

            @Override
            public String toString() {
                return "" + id;
            }
        }

        Node[] leftSides;
        Node[] rightSides;
        int version;

        public void findMinVertexCover() {
            prepare();
            for (Node r : rightSides) {
                if (r.partner == null) {
                    dfsRight(r);
                }
            }

            for (Node l : leftSides) {
                l.inMinVertexCover = l.visited == version;
            }
            for (Node r : rightSides) {
                r.inMinVertexCover = r.visited != version;
            }
        }

        private void dfsRight(Node node) {
            if (node.visited == version) {
                return;
            }
            node.visited = version;
            for (Node next : node.nodes) {
                dfsLeft(next);
            }
        }

        private void dfsLeft(Node node) {
            if (node.partner == null) {
                return;
            }
            if (node.visited == version) {
                return;
            }
            node.visited = version;
            dfsRight(node.partner);
        }

        public KMAlgo(int l, int r) {
            leftSides = new Node[l];
            for (int i = 0; i < l; i++) {
                leftSides[i] = new Node();
                leftSides[i].id = i;
                leftSides[i].leftSide = true;
            }
            rightSides = new Node[r];
            for (int i = 0; i < r; i++) {
                rightSides[i] = new Node();
                rightSides[i].id = i;
            }
        }

        public void addEdge(int lId, int rId) {
            leftSides[lId].nodes.add(rightSides[rId]);
            rightSides[rId].nodes.add(leftSides[lId]);
        }

        private void prepare() {
            version++;
        }

        /**
         * Determine can we find a partner for a left node to enhance the matching degree.
         */
        public boolean matchLeft(int id) {
            if (leftSides[id].partner != null) {
                return false;
            }
            prepare();
            return findPartner(leftSides[id]);
        }

        /**
         * Determine can we find a partner for a right node to enhance the matching degree.
         */
        public boolean matchRight(int id) {
            if (rightSides[id].partner != null) {
                return false;
            }
            prepare();
            return findPartner(rightSides[id]);
        }

        private boolean findPartner(Node src) {
            if (src.visited == version) {
                return false;
            }
            src.visited = version;
            for (Node node : src.nodes) {
                if (!tryRelease(node)) {
                    continue;
                }
                node.partner = src;
                src.partner = node;
                return true;
            }
            return false;
        }

        private boolean tryRelease(Node src) {
            if (src.visited == version) {
                return false;
            }
            src.visited = version;
            if (src.partner == null) {
                return true;
            }
            if (findPartner(src.partner)) {
                src.partner = null;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < leftSides.length; i++) {
                if (leftSides[i].partner == null) {
                    continue;
                }
                builder.append(leftSides[i].id).append(" - ").append(leftSides[i].partner.id).append("\n");
            }
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
