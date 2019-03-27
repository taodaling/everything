package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dalt on 2017/12/12.
 */
public class MinimumCost {
    static final int INF = (int) 1e8;
    static BlockReader input;
    int n;
    int m;
    int k;
    int[][] nShopKeeper;
    int[][] mSupplier;
    int[][][] matrix;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\MinimumCost.in"));

        input = new BlockReader(System.in);
        int n, m, k;
        while ((n = input.nextInteger()) != 0 &&
                (m = input.nextInteger()) != 0 &&
                (k = input.nextInteger()) != 0) {
            MinimumCost wall = new MinimumCost();
            wall.init(n, m, k);
            System.out.println(wall.solve());
        }
    }

    public static void buildBridge(Node src, Node dst, int capacity, int fee) {
        PositiveEdge edge = new PositiveEdge(src, dst, capacity, fee);
        src.out.add(edge);
        dst.out.add(new NegativeEdge(edge));
    }

    public void init(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;

        nShopKeeper = new int[k][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                nShopKeeper[j][i] = input.nextInteger();
            }
        }

        mSupplier = new int[k][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                mSupplier[j][i] = input.nextInteger();
            }
        }

        matrix = new int[k][n][m];
        for (int i = 0; i < k; i++) {
            //Read the profit matrix
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < m; c++) {
                    matrix[i][r][c] = input.nextInteger();
                }
            }
        }
    }

    public int solve() {
        //The requirement could be satisfied only when the supplier could supply more goods
        for (int i = 0; i < k; i++) {
            int totalRequire = 0;
            for (int j = 0; j < n; j++) {
                totalRequire += nShopKeeper[i][j];
            }

            int totalSupply = 0;
            for (int j = 0; j < m; j++) {
                totalSupply += mSupplier[i][j];
            }

            if (totalRequire > totalSupply) {
                return -1;
            }
        }

        int expense = 0;
        for (int i = 0; i < k; i++) {
            int[][] edgeMat = matrix[i];
            int[] supplier = mSupplier[i];
            int[] shopKeeper = nShopKeeper[i];

            //Build datagraph
            Node sink = new Node();
            Node dest = new Node();
            List<Node> shopKeepersNodeList = new ArrayList(n);
            for (int j = 0; j < n; j++) {
                Node newNode = new Node();
                buildBridge(newNode, dest, shopKeeper[j], 0);
                shopKeepersNodeList.add(newNode);
            }
            List<Node> supplierNodeList = new ArrayList(m);
            for (int j = 0; j < m; j++) {
                Node newNode = new Node();
                buildBridge(sink, newNode, supplier[j], 0);
                supplierNodeList.add(newNode);
            }

            for (int r = 0; r < n; r++) {
                Node shopKeeperNode = shopKeepersNodeList.get(r);
                for (int c = 0; c < m; c++) {
                    buildBridge(supplierNodeList.get(c), shopKeeperNode, INF, edgeMat[r][c]);
                }
            }

            //Find the shortest from sink to dest, and setLength the flow on the road
            List<Node> combine = new ArrayList();
            combine.addAll(supplierNodeList);
            combine.addAll(shopKeepersNodeList);
            combine.add(sink);
            combine.add(dest);
            while (true) {
                for (Node node : combine) {
                    node.distance = INF;
                    node.last = null;
                }

                sink.distance = 0;
                spfa(sink);
                if (dest.last == null) {
                    break;
                }

                int maxAllowedAugmentFlow = INF;
                for (Node trace = dest; trace != sink; trace = trace.last.src()) {
                    Edge last = trace.last;
                    maxAllowedAugmentFlow = Math.min(last.capacity() - last.flow(), maxAllowedAugmentFlow);
                }

                for (Node trace = dest; trace != sink; trace = trace.last.src()) {
                    trace.last.sendFlow(maxAllowedAugmentFlow);
                }

                expense += maxAllowedAugmentFlow * dest.distance;
            }
        }

        return expense;
    }

    public void spfa(Node sink) {
        LinkedList<Node> queue = new LinkedList();
        queue.addLast(sink);

        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            head.inQueue = false;

            for (Edge edge : head.out) {
                if (edge.capacity() == edge.flow()) {
                    continue;
                }
                Node dst = edge.dst();
                int newDistance = head.distance + edge.getFee();
                if (newDistance < dst.distance) {
                    dst.distance = newDistance;
                    dst.last = edge;
                    if (!dst.inQueue) {
                        dst.inQueue = true;
                        queue.addLast(dst);
                    }
                }
            }
        }
    }

    public static interface Edge {
        int flow();

        int capacity();

        void sendFlow(int amount);

        Node src();

        Node dst();

        int getFee();
    }

    public static class Node {
        Edge last;
        int distance;
        boolean inQueue;
        List<Edge> out = new ArrayList();
    }

    public static class PositiveEdge implements Edge {
        int flow;
        int capacity;
        int fee;
        Node src;
        Node dst;

        public PositiveEdge(Node src, Node dst, int capacity, int fee) {
            this.src = src;
            this.dst = dst;
            this.capacity = capacity;
            this.fee = fee;
        }

        public int flow() {
            return flow;
        }

        public int capacity() {
            return capacity;
        }

        public void sendFlow(int amount) {
            flow += amount;
        }

        public Node src() {
            return src;
        }

        public Node dst() {
            return dst;
        }

        public int getFee() {
            return fee;
        }
    }

    public static class NegativeEdge implements Edge {
        Edge edge;

        public NegativeEdge(Edge edge) {
            this.edge = edge;
        }

        public int flow() {
            return 0;
        }

        public int capacity() {
            return edge.flow();
        }

        public void sendFlow(int amount) {
            edge.sendFlow(-amount);
        }

        public Node src() {
            return edge.dst();
        }

        public Node dst() {
            return edge.src();
        }

        public int getFee() {
            return -edge.getFee();
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }
}