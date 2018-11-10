package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by dalt on 2018/1/30.
 */
public class BZOJ3611 implements Runnable{
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    static final int INF = 100000000;
    public static BlockReader input;
    static int idAllocator = 0;

    @Override
    public void run() {
        if (!IS_OJ) {
            try {
                System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3611.in"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        input = new BlockReader(System.in);

        StringBuilder result = new StringBuilder();
        int n = input.nextInteger();
        Node[] nodes = new Node[n + 1];
        for (int i = 0; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].index = i;
        }
        buildEdge(nodes[0], nodes[1]);
        for (int i = 1; i < n; i++) {
            buildEdge(nodes[input.nextInteger()], nodes[input.nextInteger()]);
        }

        dfs(nodes[1], nodes[0]);

        int q = input.nextInteger();

        Node[] choose = new Node[n];
        Deque<Node> queue = new ArrayDeque(n);

        for (int i = 1; i <= q; i++) {
            int k = input.nextInteger();
            for (int j = 0; j < k; j++) {
                choose[j] = nodes[input.nextInteger()];
            }

            Arrays.sort(choose, 0, k);

            //Build fake tree
            queue.clear();
            queue.addLast(nodes[0]);
            nodes[0].init(i);
            for (int j = 0; j < k; j++) {
                Node node = choose[j];
                Node lca = lca(node, queue.getLast());

                node.init(i);
                lca.init(i);

                while (lca.depth < queue.getLast().depth) {
                    Node last = queue.removeLast();
                    if (lca.depth >= queue.getLast().depth) {
                        last.father = lca;
                    } else {
                        last.father = queue.getLast();
                    }
                    last.father.children.add(last);
                }
                if (lca != queue.getLast()) {
                    queue.addLast(lca);
                }
                queue.addLast(node);
            }

            while (queue.size() > 2) {
                Node last = queue.removeLast();
                last.father = queue.getLast();
                last.father.children.add(last);
            }

            for (int j = 0; j < k; j++) {
                choose[j].isPick = true;
            }

            Node root = queue.getLast();
            solve(root);
            result.append(root.sum).append(' ').append(root.min).append(' ').append(root.max).append('\n');
            System.out.print(result);
            result.setLength(0);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        new Thread(null, new BZOJ3611(), "cf", 1 << 27).start();


    }

    public static void solve(Node root) {
        if (root.isPick) {
            root.childNum = 1;
            root.deepest = root.depth;
            root.highest = root.depth;
        }

        for (Node child : root.children) {
            solve(child);
            child.weight += (long) child.childNum * (child.depth - root.depth);
            root.sum += root.childNum * child.weight + root.weight * child.childNum + child.sum;
            root.weight += child.weight;
            root.childNum += child.childNum;
            root.min = Math.min(root.min, child.min);
            root.min = Math.min(child.highest + root.highest - root.depth * 2, root.min);
            root.highest = Math.min(root.highest, child.highest);
            root.max = Math.max(child.deepest + root.deepest - root.depth * 2, root.max);
            root.max = Math.max(child.max, root.max);
            root.deepest = Math.max(child.deepest, root.deepest);
        }

    }

    public static void buildEdge(Node a, Node b) {
        a.children.add(b);
        b.children.add(a);
    }

    public static Node lca(Node a, Node b) {
        if (a.depth > b.depth) {
            a = ancestorOf(a, a.depth - b.depth);
        } else {
            b = ancestorOf(b, b.depth - a.depth);
        }

        for (int i = 19; i >= 0 && a != b; i--) {
            if (a.ancestors[i] != b.ancestors[i]) {
                a = a.ancestors[i];
                b = b.ancestors[i];
            }
        }
        return a == b ? a : a.ancestors[0];
    }

    public static Node ancestorOf(Node a, int h) {
        for (int i = 0; h != 0; i++) {
            int bit = 1 << i;
            if ((bit & h) != 0) {
                h -= bit;
                a = a.ancestors[i];
            }
        }
        return a;
    }

    public static void dfs(Node node, Node father) {
        node.depth = father.depth + 1;
        node.id = ++idAllocator;
        node.ancestors[0] = father;
        for (int i = 0; node.ancestors[i] != null; i++) {
            node.ancestors[i + 1] = node.ancestors[i].ancestors[i];
        }

        node.children.remove(father);
        for (Node child : node.children) {
            dfs(child, node);
        }
    }

    public static class Node implements Comparable<Node> {
        List<Node> children = new ArrayList(2);
        Node father;
        int depth;
        int version;
        Node[] ancestors = new Node[20];
        long sum;
        int childNum;
        int max;
        int min;
        long weight;
        int deepest;
        int highest;
        boolean isPick;
        int id;
        int index;

        @Override
        public String toString() {
            return "" + index;
        }

        public void init(int version) {
            if (this.version >= version) {
                return;
            }

            this.version = version;
            father = null;
            children.clear();
            sum = 0;
            childNum = 0;
            max = 0;
            min = INF;
            isPick = false;
            deepest = -INF;
            weight = 0;
            highest = INF;
        }

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
