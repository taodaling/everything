package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by dalt on 2017/12/11.
 */
public class Intervals {
    static BlockReader reader;
    int[][] intervals;
    int[] intersectTimes;
    int n;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Intervals.in"));

        reader = new BlockReader(System.in);
        while (reader.hasMore()) {
            Intervals intervals = new Intervals();
            intervals.init();
            System.out.println(intervals.solve());
        }
    }

    public void init() {
        n = reader.nextInteger();
        intervals = new int[n][2];
        intersectTimes = new int[n];
        for (int i = 0; i < n; i++) {
            intervals[i][0] = reader.nextInteger();
            intervals[i][1] = reader.nextInteger();
            intersectTimes[i] = reader.nextInteger();
        }
    }

    public int solve() {
        if (n == 0) {
            return 0;
        }

        //为闭区间之间建立联系
        TreeMap<Integer, Node> treeMap = new TreeMap();
        List<Edge> edgeList = new ArrayList();
        for (int i = 0; i < n; i++) {
            Node node = getNode(treeMap, intervals[i][1]);
            Node next = getNode(treeMap, intervals[i][0] - 1);

            Edge edge = new Edge();
            edge.src = node;
            edge.dst = next;
            edge.length = -intersectTimes[i];
            edgeList.add(edge);

            node.out.add(edge);
        }

        //保证后面的点都比前面的点大，即v在u之前则有v-u<=0
        Node[] orderedData = treeMap.values().toArray(new Node[treeMap.size()]);
        Node[] revOrderedData = new Node[orderedData.length];
        for (int i = 0, bound = orderedData.length; i < bound; i++) {
            revOrderedData[i] = orderedData[bound - i - 1];
        }

        for (int i = 1, bound = revOrderedData.length; i < bound; i++) {
            Node last = revOrderedData[i - 1];
            Node next = revOrderedData[i];
            Edge edge1 = new Edge();
            edge1.src = last;
            edge1.dst = next;
            edge1.length = 0;
            last.out.add(edge1);
            edgeList.add(edge1);


            Edge edge2 = new Edge();
            edge2.src = next;
            edge2.dst = last;
            edge2.length = last.id - next.id;
            next.out.add(edge2);
            edgeList.add(edge2);
        }

        revOrderedData[0].distance = 0;
        spfa(Arrays.asList(revOrderedData), edgeList);

        return -revOrderedData[revOrderedData.length - 1].distance;
    }

    public void spfa(List<Node> nodeList, List<Edge> edgeList) {
        LinkedList<Node> fixedNodeList = new LinkedList();
        fixedNodeList.addLast(nodeList.get(0));
        nodeList.get(0).status = true;
        while (!fixedNodeList.isEmpty()) {
            Node head = fixedNodeList.removeFirst();
            head.status = false;
            for (Edge edge : head.out) {
                Node dst = edge.dst;
                int newDistance = edge.src.distance + edge.length;
                if (newDistance < dst.distance) {
                    dst.distance = newDistance;
                    if (dst.status == false) {
                        dst.status = true;
                        fixedNodeList.addLast(dst);
                    }
                }
            }
        }
    }

    public Node getNode(Map<Integer, Node> map, Integer key) {
        Node v = map.get(key);
        if (v == null) {
            v = new Node();
            v.id = key;
            map.put(key, v);
        }
        return v;
    }

    public static class Node {
        int distance = 10000;
        List<Edge> out = new ArrayList();
        int id;
        boolean status;

        public String toString() {
            return "" + distance;
        }
    }

    public static class Edge {
        int length;
        Node src;
        Node dst;
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