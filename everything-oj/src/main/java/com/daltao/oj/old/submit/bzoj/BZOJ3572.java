package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by Administrator on 2018/1/29.
 */
public class BZOJ3572 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    static {
        if (!IS_OJ) {
            try {
                System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3572.in"));
            } catch (FileNotFoundException e) {
            }
        } else {
            System.setOut(new PrintStream(System.out, false));
        }
        input = new BlockReader(System.in);
    }

    public static int idAllocator = 0;

    public static void main(String[] args) throws FileNotFoundException {
        int n = input.nextInteger();
        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
        }

        dfs(nodes[1], null, 0);

        int q = input.nextInteger();
        Node[] choose = new Node[n];
        Deque<Node> deque = new ArrayDeque<>(n * 2);
        List<Node> nodeList = new ArrayList<>(n * 2);
        for (int i = 1; i <= q; i++) {
            int m = input.nextInteger();
            for (int j = 0; j < m; j++) {
                choose[j] = nodes[input.nextInteger()];
            }
            Arrays.sort(choose, 0, m);
            deque.clear();
            nodeList.clear();
            deque.addLast(nodes[1]);
            for (int j = 0; j < m; j++) {
                Node node = choose[j];
                Node lca = lca(node, deque.getLast());
                while (lca.depth < deque.getLast().depth) {
                    Node last = deque.removeLast();
                    nodeList.add(last);
                    if (lca.depth < deque.getLast().depth) {
                        last.weakFather = deque.getLast();
                    } else {
                        last.weakFather = lca;
                    }
                }
                while (deque.size() > 1) {
                    Node last = deque.removeLast();
                    nodeList.add(last);
                    last.weakFather = deque.getLast();
                }

                nodeList.add(deque.removeLast());
            }

            for (Node node : nodeList) {
                node.belongTo = null;
                node.distance = 100000000;
                node.weakChildren.clear();
                if (node.weakFather != null) {
                    node.weakFather.weakChildren.add(node);
                }
            }

            for (int j = 0; j < m; j++) {
                choose[j].belongTo = choose[j];
                choose[j].distance = 0;
            }

            for (int j = nodeList.size() - 1; j >= 0; j--) {
                Node node = nodeList.get(j);
                int depth = 0;

            }
        }
    }

    public static int dfs(Node root, Node father, int depth) {
        root.id = ++idAllocator;
        root.ancestors[0] = father;
        for (int i = 0; root.ancestors[i] != null; i++) {
            root.ancestors[i + 1] = root.ancestors[i].ancestors[i];
        }
        root.depth = depth + 1;
        root.subTreeSize = 1;
        for (Node node : root.nodeList) {
            if (node == father) {
                continue;
            }
            root.subTreeSize += dfs(node, root, root.depth);
        }
        return root.subTreeSize;
    }

    public static Node lca(Node a, Node b) {
        if (a.depth < b.depth) {
            Node tmp = a;
            a = b;
            b = tmp;
        }

        for (int i = 0, differ = a.depth - b.depth; differ != 0; i++) {
            int bit = 1 << i;
            if ((differ & bit) != 0) {
                differ -= bit;
                a = a.ancestors[i];
            }
        }

        for (int i = 19; i >= 0 && a != b; i--) {
            if (a.ancestors[i] != b.ancestors[i]) {
                a = a.ancestors[i];
                b = b.ancestors[i];
            }
        }
        return a == b ? a : a.ancestors[0];
    }

    public static int distance(Node a, Node b) {
        int length = 0;
        for (int i = 19; a != b && i >= 0; i--) {
            if (a.ancestors[i] != b.ancestors[i]) {
                a = a.ancestors[i];
                length += 1 << i;
            }
        }
        if (a != b) {
            length++;
        }
        return length;
    }

    public static Node getAncestor(Node a, int height) {
        for (int i = 0; height != 0; i++) {
            int bit = 1 << i;
            if ((height & bit) != 0) {
                height -= bit;
                a = a.ancestors[i];
            }
        }
        return a;
    }

    public static class Node implements Comparable<Node> {
        List<Node> nodeList = new ArrayList<>(2);
        List<Node> weakChildren = new ArrayList<>();
        Node father;
        Node weakFather;
        Node[] ancestors = new Node[20];
        int depth;
        int subTreeSize;
        int id;
        Node belongTo;
        int distance;

        @Override
        public int compareTo(Node o) {
            return id - o.id;
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

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
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

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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
    }
}
