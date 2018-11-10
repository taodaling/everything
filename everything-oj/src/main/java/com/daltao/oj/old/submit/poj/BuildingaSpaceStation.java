package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/12/9.
 */
public class BuildingaSpaceStation {
    private static final int INF = (int) 1e8;
    private static BlockReader input;

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\BuildingaSpaceStation.in"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    int cellNum;
    List<Node> nodeList = new ArrayList();
    List<Edge> edgeList = new ArrayList();

    public static void main(String[] args) {

        input = new BlockReader(System.in);
        int cells;
        while ((cells = input.nextInteger()) != 0) {
            BuildingaSpaceStation buildingaSpaceStation = new BuildingaSpaceStation();
            buildingaSpaceStation.init(cells);
            System.out.println(String.format("%.3f", buildingaSpaceStation.solve()));
        }
    }

    public void init(int cellNum) {
        this.cellNum = cellNum;
        double[][] points = new double[cellNum][4];
        for (int i = 0; i < cellNum; i++) {
            Node node = new Node();
            nodeList.add(node);

            points[i][0] = Double.parseDouble(input.nextBlock());
            points[i][1] = Double.parseDouble(input.nextBlock());
            points[i][2] = Double.parseDouble(input.nextBlock());
            points[i][3] = Double.parseDouble(input.nextBlock());

            for (int j = i - 1; j >= 0; j--) {
                double xoff = points[i][0] - points[j][0];
                double yoff = points[i][1] - points[j][1];
                double zoff = points[i][2] - points[j][2];
                double centerDistance = Math.sqrt(xoff * xoff + yoff * yoff + zoff * zoff);
                double distance = Math.max(0, centerDistance - points[i][3] - points[j][3]);

                Edge edge = new Edge();
                edge.src = node;
                edge.dst = nodeList.get(j);
                edge.length = distance;
                edgeList.add(edge);
            }
        }
    }

    public double solve() {
        Collections.sort(edgeList, new Comparator<Edge>() {
            public int compare(Edge o1, Edge o2) {
                return o1.length < o2.length ? -1 : o1.length > o2.length ? 1 : 0;
            }
        });

        double sum = 0;
        for (Edge edge : edgeList) {
            if (edge.src.getRepr() != edge.dst.getRepr()) {
                sum += edge.length;
                Node.union(edge.src, edge.dst);
            }
        }
        return sum;
    }

    public static class Node {
        Node p = this;
        int rank;

        public static void union(Node a, Node b) {
            a = a.getRepr();
            b = b.getRepr();
            if (a == b) {
                return;
            }
            if (a.rank == b.rank) {
                a.rank++;
            }
            if (a.rank > b.rank) {
                b.p = a;
            } else {
                a.p = b;
            }
        }

        public Node getRepr() {
            if (p.p != p) {
                p = p.getRepr();
            }
            return p;
        }
    }

    public static class Edge {
        Node src;
        Node dst;
        double length;
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
