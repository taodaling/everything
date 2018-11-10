package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/19.
 */
public class RoadConstruction {
    public static BlockReader input;
    int n;
    int m;
    List<Node> nodeList;
    int leaf = 0;
    private int dfnAllocator = 1;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\RoadConstruction.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            RoadConstruction solution = new RoadConstruction();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
        n = input.nextInteger();
        m = input.nextInteger();

        nodeList = new ArrayList(n + 1);
        nodeList.add(null);
        for (int i = 0; i < n; i++) {
            nodeList.add(new Node());
        }
        for (int i = 0; i < m; i++) {
            Node node1 = nodeList.get(input.nextInteger());
            Node node2 = nodeList.get(input.nextInteger());
            node1.neighborhood.add(node2);
            node2.neighborhood.add(node1);
        }
    }

    public int tarjan(Node node, Node father) {
        node.dfn = dfnAllocator++;
        node.low = node.dfn;
        int low = node.low;

        int childrenNum = 0;
        for (Node neighbor : node.neighborhood) {
            if (neighbor.dfn != 0) {
                if (neighbor != father) {
                    low = Math.min(low, neighbor.low);
                }
                continue;
            }
            childrenNum += tarjan(neighbor, node);
            low = Math.min(low, neighbor.low);
        }

        node.low = low;

        if (father != null) {
            if (node.dfn == node.low && childrenNum == 0) {
                leaf++;
            }
        } else {
            if (childrenNum == 1) {
                leaf++;
            }
        }

        if (node.dfn == node.low) {
            return 1;
        } else {
            return childrenNum;
        }
    }

    public int solve() {
        tarjan(nodeList.get(1), null);
        return (leaf + 1) >> 1;
    }

    public static class Node {
        List<Node> neighborhood = new ArrayList();
        int low;
        int dfn;
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
