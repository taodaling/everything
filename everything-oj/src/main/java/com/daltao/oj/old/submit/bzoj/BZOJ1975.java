package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Administrator on 2018/1/17.
 */
public class BZOJ1975 {
    public static final double INF = 1e15;

    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ1975.in"));
        input = new BlockReader(System.in);
        BZOJ1975 solution = new BZOJ1975();
        System.out.println(solution.solve());
    }

    public int solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        double remain = Double.parseDouble(input.nextBlock());

        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
        }
        for (int i = 0; i < m; i++) {
            Node src = nodes[input.nextInteger()];
            Node target = nodes[input.nextInteger()];
            double cost = Double.parseDouble(input.nextBlock());
            Edge edge = new Edge();
            edge.src = src;
            edge.target = target;
            edge.cost = cost;
            src.outList.add(edge);
            target.inList.add(edge);
        }

        //spfa
        for (int i = 1; i <= n; i++) {
            nodes[i].distance = INF;
            nodes[i].inque = false;
        }
        LinkedList<Node> queue = new LinkedList();
        queue.addLast(nodes[n]);
        nodes[n].inque = true;
        nodes[n].distance = 0;
        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            head.inque = false;
            for (Edge edge : head.inList) {
                double newDist = edge.cost + head.distance;
                if (newDist < edge.src.distance) {
                    edge.src.distance = newDist;
                    if (!edge.src.inque) {
                        queue.addLast(edge.src);
                        edge.src.inque = true;
                    }
                }
            }
        }

        //A *
        Node src = nodes[1];
        Node sink = nodes[n];
        PriorityQueue<Trace> tracePriorityQueue = new PriorityQueue();
        tracePriorityQueue.add(new Trace(src, 0));
        int k = 0;
        while (!tracePriorityQueue.isEmpty()) {
            Trace min = tracePriorityQueue.poll();
            if (min.pos == sink) {
                if (remain < min.evaluation) {
                    break;
                }
                k++;
                remain -= min.evaluation;
            }

            for (Edge edge : min.pos.outList) {
                Trace trace = new Trace(edge.target, min.g + edge.cost);
                tracePriorityQueue.add(trace);
            }
        }

        return k;
    }

    public static class Trace implements Comparable<Trace> {
        Node pos;
        double g;
        double evaluation;

        public Trace(Node pos, double g) {
            this.pos = pos;
            this.g = g;
            this.evaluation = pos.distance + g;
        }

        @Override
        public int compareTo(Trace o) {
            return evaluation < o.evaluation ? -1 : evaluation > o.evaluation ? 1 : 0;
        }
    }

    public static class Node {
        List<Edge> outList = new ArrayList();
        List<Edge> inList = new ArrayList();
        double distance;
        boolean inque;
    }

    public static class Edge {
        Node target;
        Node src;
        double cost;
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
