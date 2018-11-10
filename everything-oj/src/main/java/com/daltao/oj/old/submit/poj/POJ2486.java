package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */
public class POJ2486 {
    static final int INF = (int) 1e8;
    static BlockReader input;
    int n, k;
    List<Node> nodeList;
    int[][] endDp;
    int[][] midDp;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2486.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            POJ2486 solution = new POJ2486();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
        n = input.nextInteger();
        k = input.nextInteger();
        nodeList = new ArrayList(n + 1);
        nodeList.add(null);
        for (int i = 1; i <= n; i++) {
            Node node = new Node();
            node.id = i;
            node.amount = input.nextInteger();
            nodeList.add(node);
        }

        for (int i = 1; i < n; i++) {
            Node n1 = nodeList.get(input.nextInteger());
            Node n2 = nodeList.get(input.nextInteger());
            n1.children.add(n2);
            n2.children.add(n1);
        }

        endDp = new int[n + 1][];
        midDp = new int[n + 1][];
    }

    public void dfs(Node root) {
        for (Node child : root.children) {
            child.children.remove(root);
            dfs(child);

            int[] localMidDp = midDp[child.id];
            int[] localEndDp = endDp[child.id];
            System.arraycopy(localEndDp, 0, localEndDp, 1, k);
            System.arraycopy(localMidDp, 0, localMidDp, 2, k - 1);
            localMidDp[0] = localEndDp[0] = localMidDp[1] = 0;
        }

        if (root.children.isEmpty()) {
            midDp[root.id] = new int[k + 1];
            endDp[root.id] = new int[k + 1];
        } else {
            int[][] data = merge(root.children, 0, root.children.size());
            midDp[root.id] = data[0];
            endDp[root.id] = data[1];
        }
        for (int i = 0; i <= k; i++) {
            midDp[root.id][i] += root.amount;
            endDp[root.id][i] += root.amount;
        }
    }

    public int[][] merge(List<Node> children, int from, int to) {
        if (to - from == 1) {
            return new int[][]{midDp[children.get(from).id], endDp[children.get(from).id]};
        }
        int mpos = (from + to) / 2;
        int[] lmid;
        int[] lend;
        int[] rmid;
        int[] rend;
        {
            int[][] res = merge(children, from, mpos);
            lmid = res[0];
            lend = res[1];
        }
        {
            int[][] res = merge(children, mpos, to);
            rmid = res[0];
            rend = res[1];
        }

        int[] mid = new int[k + 1];
        int[] end = new int[k + 1];
        for (int i = 0; i <= k; i++) {
            mid[i] = 0;
            end[i] = 0;
            for (int j = 0; j <= i; j++) {
                mid[i] = Math.max(mid[i], lmid[j] + rmid[i - j]);
                end[i] = Math.max(Math.max(end[i], lmid[j] + rend[i - j]), lend[j] + rmid[i - j]);
            }
        }
        return new int[][]{mid, end};
    }

    public int solve() {
        if (k == 0) {
            return nodeList.get(1).amount;
        }

        dfs(nodeList.get(1));
        return endDp[1][k];
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

    public static class Node {
        int id;
        int amount;
        List<Node> children = new ArrayList();

        @Override
        public String toString() {
            return id + ":" + amount;
        }
    }
}
