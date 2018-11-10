package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by Administrator on 2017/12/16.
 */
public class KnightsoftheRoundTable {
    public static BlockReader input;
    int n;
    int m;
    boolean[][] removeEdge;
    boolean[] inStacks;
    int[] ids;
    int[] lowers;
    int[] colors;
    boolean[] remainFlags;
    int idAllocator = 1;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\KnightsoftheRoundTable.in"));

        input = new BlockReader(System.in);
        int n, m;
        while (true) {
            n = input.nextInteger();
            m = input.nextInteger();
            if (n == 0 && m == 0) {
                break;
            }
            KnightsoftheRoundTable solution = new KnightsoftheRoundTable();
            solution.init(n, m);
            System.out.println(solution.solve());
        }
    }

    public void init(int n, int m) {
        this.n = n;
        this.m = m;

        removeEdge = new boolean[n][n];
        ids = new int[n];
        lowers = new int[n];
        inStacks = new boolean[n];
        colors = new int[n];
        remainFlags = new boolean[n];
        for (int i = 0; i < m; i++) {
            int from = input.nextInteger() - 1;
            int to = input.nextInteger() - 1;
            removeEdge[from][to] = true;
            removeEdge[to][from] = true;
        }

        for (int i = 0; i < n; i++) {
            removeEdge[i][i] = true;
        }

//        for (int i = 0; i < n; i++) {
//            for (int j = i + 1; j < n; j++) {
//                if (!removeEdge[i][j]) {
//                    System.out.println((i + 1) + "-" + (j + 1));
//                }
//            }
//        }
    }

    public int tarjan(int node, int father, LinkedList<Integer> stack) {
        //Initialized once
        if (ids[node] != 0) {
            return lowers[node];
        }

        ids[node] = idAllocator++;
        lowers[node] = ids[node];
        stack.addLast(node);
        inStacks[node] = true;

        int minLower = lowers[node];
        for (int i = 0; i < n; i++) {
            if (i == father || removeEdge[node][i]) {
                continue;
            }

            if (ids[i] != 0) {
                if (inStacks[i]) {
                    minLower = Math.min(lowers[i], minLower);
                }
                continue;
            }

            int iLower = tarjan(i, node, stack);

            minLower = Math.min(iLower, minLower);

            //If node is a cut point
            if (iLower == ids[node]) {
                colors[node] = 1;
                boolean isOdd = !dyeing(i, node);

                while (true) {
                    int last = stack.removeLast();
                    inStacks[last] = false;
                    remainFlags[last] = remainFlags[last] || isOdd;
                    if (last == i) {
                        break;
                    }
                }

                remainFlags[node] = remainFlags[node] || isOdd;
                colors[node] = 0;
            } else if (iLower > ids[node]) {
                while (true) {
                    int last = stack.removeLast();
                    inStacks[last] = false;
                    if (last == i) {
                        break;
                    }
                }
            }
        }

        lowers[node] = minLower;
        return minLower;
    }

    /**
     * set the color for each node in subtree with indicated root
     */
    public boolean dyeing(int root, int father) {
        if (!inStacks[root]) {
            return true;
        }
        if (colors[root] != 0) {
            return colors[root] != colors[father];
        }

        colors[root] = colors[father] == 1 ? 2 : 1;
        for (int i = 0; i < n; i++) {
            if (removeEdge[root][i] || i == father) {
                continue;
            }
            if (!dyeing(i, root)) {
                return false;
            }
        }
        return true;
    }

    public int solve() {
        int cnt = 0;
        LinkedList<Integer> stack = new LinkedList();
        for (int i = 0; i < n; i++) {
            tarjan(i, -1, stack);
            while (stack.isEmpty()) {
                int last = stack.removeLast();
                inStacks[last] = false;
            }

            if (remainFlags[i]) {
                cnt++;
//                System.out.println(i + 1);
            }
        }
        return n - cnt;
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
