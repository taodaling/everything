package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dalt on 2018/2/25.
 */
public class POJ1741 {
    public static final int INF = (int) 1e8;
    public static final int LIMIT = 10000;
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static List<Node> trace = new ArrayList(LIMIT);
    public static Node[] nodes = new Node[LIMIT + 1];
    public static Tree tree = new Tree();

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ1741.in"));
        }
        input = new BlockReader(System.in);

        while (true) {
            int n = input.nextInteger();
            int k = input.nextInteger();

            if (n == 0 && k == 0) {
                break;
            }

            Node.resetCache();
            Edge.resetCache();
            solve(n, k);
        }
    }

    public static void solve(int n, int k) {
        for (int i = 1; i <= n; i++) {
            nodes[i] = Node.allocate();
            nodes[i].id = i;
        }
        for (int i = 1; i < n; i++) {
            Node a = nodes[input.nextInteger()];
            Node b = nodes[input.nextInteger()];
            int len = input.nextInteger();

            buildEdge(a, b, len);
        }

        int sum = count(nodes[1], n, k);
        System.out.println(sum);
    }

    public static void buildEdge(Node a, Node b, int len) {
        Edge edge = Edge.allocate();
        edge.a = a;
        edge.b = b;
        edge.len = len;
        a.edgeList.add(edge);
        b.edgeList.add(edge);
    }

    public static int count(Node root, int size, int k) {

        if (size == 1) {
            return 0;
        }

        root = dfsForSummary(root, null, size);
        dfsForDepth(root, null, 0);

        root.initSplay();
        tree.setRoot(root);
        int sum = 0;
        for (Edge edge : root.edgeList) {
            Node dst = edge.getDst(root);
            dst.edgeList.remove(edge);

            trace.clear();
            dfsForRecord(dst, null, trace);
            for (Node node : trace) {
                sum += tree.lessThan(k - node.depth);
            }
            for (Node node : trace) {
                node.initSplay();
                tree.insert(node);
            }
        }

        for (Edge edge : root.edgeList) {
            Node dst = edge.getDst(root);
            sum += count(dst, dst.subTreeSize, k);
        }

        return sum;
    }

    public static void dfsForRecord(Node root, Node father, List<Node> trace) {
        trace.add(root);
        for (Edge edge : root.edgeList) {
            Node dst = edge.getDst(root);
            if (dst == father) {
                continue;
            }
            dfsForRecord(dst, root, trace);
        }
    }

    public static Node dfsForSummary(Node root, Node father, int size) {
        Node candidate = null;
        root.maxChildSize = 0;

        root.subTreeSize = 1;
        for (Edge edge : root.edgeList) {
            Node dst = edge.getDst(root);
            if (dst == father) {
                continue;
            }
            Node bestOfChild = dfsForSummary(dst, root, size);
            root.subTreeSize += dst.subTreeSize;
            if (candidate == null || bestOfChild.maxChildSize < candidate.maxChildSize) {
                candidate = bestOfChild;
            }
            root.maxChildSize = Math.max(root.maxChildSize, dst.subTreeSize);
        }
        root.maxChildSize = Math.max(size - root.subTreeSize, root.maxChildSize);
        if (candidate == null || root.maxChildSize < candidate.maxChildSize) {
            candidate = root;
        }
        return candidate;
    }

    public static void dfsForDepth(Node root, Node father, int depth) {
        root.depth = depth;
        root.subTreeSize = 1;
        for (Edge edge : root.edgeList) {
            Node dst = edge.getDst(root);
            if (dst == father) {
                continue;
            }
            dfsForDepth(dst, root, depth + edge.len);
            root.subTreeSize += dst.subTreeSize;
        }
    }

    public static class Tree {
        Node root;

        public static String dfs(Node node) {
            if (node == Node.NIL) {
                return "";
            }

            return dfs(node.left) + " " + node.depth + " " + dfs(node.right);
        }

        public void setRoot(Node root) {
            this.root = root;
        }

        public void insert(Node other) {
            Node trace = root;
            Node traceFather = root;
            while (trace != Node.NIL) {
                traceFather = trace;
                if (trace.depth >= other.depth) {
                    trace = trace.left;
                } else {
                    trace = trace.right;
                }
            }

            if (traceFather.depth >= other.depth) {
                traceFather.asLeft(other);
            } else {
                traceFather.asRight(other);
            }

            Node.splay(other);
            root = other;
        }

        public int lessThan(int v) {
            Node trace = root;
            Node traceFather = root;
            int sum = 0;
            while (trace != Node.NIL) {
                traceFather = trace;
                if (trace.depth > v) {
                    trace = trace.left;
                } else {
                    sum += trace.cnt - trace.right.cnt;
                    trace = trace.right;
                }
            }

            Node.splay(traceFather);
            root = traceFather;
            return sum;
        }

        @Override
        public String toString() {
            return dfs(root);
        }
    }

    public static class Node {
        public static Node[] nodeCache = new Node[LIMIT];
        public static int qlen = 0;
        public static Node NIL = new Node();
        List<Edge> edgeList = new ArrayList(2);
        int subTreeSize;
        int maxChildSize;
        int depth;
        int id;
        Node left;
        Node right;
        Node father;
        int cnt;

        private Node() {
        }

        public static void resetCache() {
            qlen = 0;
        }

        public static Node allocate() {
            if (nodeCache[qlen] == null) {
                nodeCache[qlen] = new Node();
            }
            nodeCache[qlen].init();
            return nodeCache[qlen++];
        }

        public void init() {
            edgeList.clear();
        }

        public static void zig(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node b = x.right;

            z.changeChild(y, x);
            y.asLeft(b);
            x.asRight(y);

            y.pushUp();
        }

        public static void zag(Node x) {
            Node y = x.father;
            Node z = y.father;
            Node b = x.left;

            z.changeChild(y, x);
            y.asRight(b);
            x.asLeft(y);

            y.pushUp();
        }

        public static void splay(Node x) {
            if (x == NIL) {
                return;
            }
            Node y, z;
            while ((y = x.father) != NIL) {
                if ((z = y.father) == NIL) {
                    if (x == y.left) {
                        zig(x);
                    } else {
                        zag(x);
                    }
                } else {
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

            x.pushUp();
        }

        public void changeChild(Node y, Node x) {
            if (left == y) {
                asLeft(x);
            } else {
                asRight(x);
            }
        }

        public void asLeft(Node x) {
            left = x;
            x.father = this;
        }

        public void asRight(Node x) {
            right = x;
            x.father = this;
        }

        public void pushUp() {
            cnt = left.cnt + right.cnt + 1;
        }

        public void initSplay() {
            left = right = father = NIL;
            cnt = 1;
        }

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class Edge {
        public static Edge[] edgeCache = new Edge[LIMIT];
        public static int qlen = 0;
        Node a;
        Node b;
        int len;

        private Edge() {
        }

        public static Edge allocate() {
            if (edgeCache[qlen] == null) {
                edgeCache[qlen] = new Edge();
            }
            return edgeCache[qlen++];
        }

        public static void resetCache() {
            qlen = 0;
        }

        public Node getDst(Node src) {
            return a == src ? b : a;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 1 << 13);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }
    }
}
