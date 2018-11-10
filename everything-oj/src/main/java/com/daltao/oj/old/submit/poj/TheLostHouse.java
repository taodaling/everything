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
 * Created by dalt on 2017/12/27.
 */
public class TheLostHouse {
    static BlockReader input;
    static final int INF = 100000000;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\test\\poj\\TheLostHouse.in"));

        input = new BlockReader(System.in);
        int n;
        while ((n = input.nextInteger()) != 0) {
            TheLostHouse solution = new TheLostHouse();
            solution.init(n);
            System.out.println(String.format("%.4f", solution.solve()));
        }
    }

    int n;
    int leafNum;
    Node[] nodes;

    public void init(int n) {
        this.n = n;
        nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }

        input.nextInteger();
        char[] hasWorm = new char[1];
        input.nextBlock(hasWorm, 0);
        for (int i = 2; i <= n; i++) {
            int father = input.nextInteger();
            input.nextBlock(hasWorm, 0);
            nodes[father].children.add(nodes[i]);
            nodes[i].hasWorm = hasWorm[0] == 'Y';
        }
    }

    public double solve() {
        dfs(nodes[1]);
        double sum = nodes[1].houseIn;
        return sum / leafNum;
    }

    public int dfs(Node root) {
        int childNum = root.children.size();
        if (childNum == 0) {
            leafNum++;
            root.houseIn = 0;
            root.houseNotIn = 0;
        }

        int successSum = 0;
        int failSum = 0;
        int minFail = INF;
        for (Node child : root.children) {
            dfs(child);

            successSum += child.houseIn;
            failSum += child.houseNotIn;
            minFail = Math.min(minFail, child.houseNotIn);
        }

        Collections.sort(root.children, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.houseNotIn - o2.houseNotIn;
            }
        });
        root.houseIn = successSum + failSum - minFail + (childNum - 1) + childNum;
        root.houseNotIn = root.hasWorm ? 0 : (failSum + 2 * childNum);
        return 0;
    }

    public static class Node {
        List<Node> children = new ArrayList<>();
        boolean hasWorm;
        int houseIn;
        int houseNotIn;
        int id;
        int leafNum;
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 4096);
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
