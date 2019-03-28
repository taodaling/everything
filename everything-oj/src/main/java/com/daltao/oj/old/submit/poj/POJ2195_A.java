package com.daltao.oj.old.submit.poj;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dalt on 2017/12/13.
 */
public class POJ2195_A{
    static final int INF = (int) 1e8;
    static BlockReader input;

    public static void main(String[] args) throws Exception {
        //System.setIn(new FileInputStream("D:\\test\\poj\\GoingHome.in"));

        input = new BlockReader(System.in);
        int n, m;
        while ((n = input.nextInteger()) != 0 &&
                (m = input.nextInteger()) != 0) {
            POJ2195_A goingHome = new POJ2195_A();
            goingHome.init(n, m);
            System.out.println(goingHome.solve());
        }
    }

    public static void buildBridge(Node src, Node dst, int capacity, int fee) {
        PositiveEdge edge = new PositiveEdge(src, dst, capacity, fee);
        src.out.add(edge);
        dst.out.add(new NegativeEdge(edge));
    }

    public int distance(Node a, Node b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    char[][] map;
    int n;
    int m;

    public void init(int n, int m) {
        this.n = n;
        this.m = m;
        map = new char[n][m];
        for (int i = 0; i < n; i++) {
            input.nextBlock(map[i], 0);
        }
    }

    public int solve() {

        int expense = 0;

        //Build datagraph
        Node sink = new Node();
        Node dest = new Node();
        List<Node> manNodeList = new ArrayList();
        List<Node> houseNodeList = new ArrayList();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < m; c++) {
                switch (map[r][c]) {
                    case 'H': {
                        Node node = new Node();
                        node.row = r;
                        node.col = c;
                        houseNodeList.add(node);
                        buildBridge(node, dest, 1, 0);
                        break;
                    }
                    case 'm': {
                        Node node = new Node();
                        node.row = r;
                        node.col = c;
                        manNodeList.add(node);
                        buildBridge(sink, node, 1, 0);
                        break;
                    }
                }
            }
        }


        for (Node house : houseNodeList) {
            for (Node man : manNodeList) {
                buildBridge(man, house, INF, distance(house, man));
            }
        }

        //Find the shortest from sink to dest, and setLength the flow on the road
        List<Node> combine = new ArrayList();
        combine.addAll(houseNodeList);
        combine.addAll(manNodeList);
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
        int row;
        int col;

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
            this(is, 1024);
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