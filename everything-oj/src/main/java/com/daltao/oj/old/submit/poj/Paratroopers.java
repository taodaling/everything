package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dalt on 2017/12/21.
 */
public class Paratroopers {
    static BlockReader input;
    static Double INF = 1e15;

    int rowNum;
    int colNum;
    int locationNum;
    double[] rowPrices;
    double[] colPrices;
    int[][] locations;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\test\\poj\\Paratroopers.in"));

        input = new BlockReader(System.in);
        int testCaseNum = input.nextInteger();
        while (testCaseNum-- > 0) {
            Paratroopers solution = new Paratroopers();
            solution.init();
            System.out.println(String.format("%.4f", solution.solve()));
        }
    }

    public void init() {
        rowNum = input.nextInteger();
        colNum = input.nextInteger();
        locationNum = input.nextInteger();
        locations = new int[locationNum][2];
        rowPrices = new double[rowNum];
        colPrices = new double[colNum];

        for (int i = 0; i < rowNum; i++) {
            rowPrices[i] = Math.log(Double.parseDouble(input.nextBlock()));
        }
        for (int i = 0; i < colNum; i++) {
            colPrices[i] = Math.log(Double.parseDouble(input.nextBlock()));
        }

        for (int i = 0; i < locationNum; i++) {
            locations[i][0] = input.nextInteger() - 1;
            locations[i][1] = input.nextInteger() - 1;
        }
    }

    public double solve() {
        //Build datagraph
        Node sink = new Node();
        Node target = new Node();
        List<Node> rowNodeList = new ArrayList(rowNum);
        for (int i = 0; i < rowNum; i++) {
            Node node = new Node();
            rowNodeList.add(node);
            Node.buildEdge(sink, node, rowPrices[i]);
        }
        List<Node> colNodeList = new ArrayList(colNum);
        for (int i = 0; i < colNum; i++) {
            Node node = new Node();
            colNodeList.add(node);
            Node.buildEdge(node, target, colPrices[i]);
        }
        for (int[] location : locations) {
            Node.buildEdge(rowNodeList.get(location[0]),
                    colNodeList.get(location[1]), INF);
        }

        //ISAP
        List<Node> nodeList = new ArrayList(colNum + rowNum + 2);
        nodeList.add(sink);
        nodeList.add(target);
        nodeList.addAll(rowNodeList);
        nodeList.addAll(colNodeList);
        return Math.pow(Math.E, isap(nodeList, sink, target));
    }

    int[] cnts;
    Node sink;
    Node target;
    int distanceLimit;

    public double isap(List<Node> nodeList, Node sink, Node target) {
        distanceLimit = nodeList.size();
        cnts = new int[distanceLimit + 2];
        this.sink = sink;
        this.target = target;
        bfs(nodeList, target);
        if (sink.distance == -1) {
            return 0;
        }

        double sum = 0;
        while (sink.distance < distanceLimit) {
            sum += trySendFlow(sink, INF);
        }

        return sum;
    }

    public double trySendFlow(Node node, double flowLimit) {
        double sum = 0;
        if (node == target) {
            return flowLimit;
        }
        for (Edge edge : node.edgeList) {
            if (edge.capacity() == edge.flow() || edge.dst().distance != node.distance - 1) {
                continue;
            }

            double actuallySend = trySendFlow(edge.dst(), Math.min(flowLimit - sum, edge.capacity() - edge.flow()));
            sum += actuallySend;
            edge.sendFlow(actuallySend);

            if (sum == flowLimit) {
                break;
            }
        }

        //If not flow has been send out
        if (sum == 0) {
            //Fix the distance of node
            if (--cnts[node.distance] == 0) {
                cnts[sink.distance]--;
                cnts[distanceLimit]++;
                sink.distance = distanceLimit;
            }
            cnts[++node.distance]++;
        }

        return sum;
    }

    public void bfs(List<Node> nodeList, Node beginNode) {
        for (Node node : nodeList) {
            node.distance = -1;
        }
        beginNode.distance = 0;
        LinkedList<Node> queue = new LinkedList();
        queue.addLast(beginNode);
        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            cnts[head.distance]++;
            for (Edge edge : head.edgeList) {
                if (edge.getClass() != NegEdge.class) {
                    continue;
                }
                Node dst = edge.dst();
                if (dst.distance == -1) {
                    dst.distance = head.distance + 1;
                    queue.addLast(dst);
                }
            }
        }
    }


    public static class Node {
        int distance;

        List<Edge> edgeList = new ArrayList();

        public static void buildEdge(Node src, Node dst, double cap) {
            Edge posEdge = new PosEdge(src, dst, cap);
            src.edgeList.add(posEdge);
            dst.edgeList.add(new NegEdge(posEdge));
        }
    }

    public static interface Edge {
        public double flow();

        public double capacity();

        public void sendFlow(double f);

        public Node dst();

        public Node src();
    }

    public static class PosEdge implements Edge {
        double flow;
        double capacity;
        Node dst;
        Node src;

        public PosEdge(Node src, Node dst, double cap) {
            this.src = src;
            this.dst = dst;
            this.capacity = cap;
        }


        public double flow() {
            return flow;
        }


        public double capacity() {
            return capacity;
        }


        public void sendFlow(double f) {
            flow += f;
        }


        public Node dst() {
            return dst;
        }


        public Node src() {
            return src;
        }
    }

    public static class NegEdge implements Edge {
        Edge rev;

        public NegEdge(Edge edge) {
            rev = edge;
        }


        public double flow() {
            return 0;
        }


        public double capacity() {
            return rev.flow();
        }


        public void sendFlow(double f) {
            rev.sendFlow(-f);
        }


        public Node dst() {
            return rev.src();
        }


        public Node src() {
            return rev.dst();
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
