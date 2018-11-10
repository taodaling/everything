package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/12/25.
 */
public class Roads {
    static final int INF = 100000000;
    static BlockReader input;

    int money;
    int cityNum;
    Node[] nodes;
    List<Edge>[] edgeList;
    Heap<Node> heap = new Heap();

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Roads.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            Roads solution = new Roads();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public int index(int pos, int cost) {
        return cost * cityNum + pos;
    }

    public int solve() {
        nodes[index(0, 0)].length = 0;
        heap.init(nodes, new Comparator<Node>() {
            public int compare(Node o1, Node o2) {
                return o1.length - o2.length;
            }
        });

        for (int i = 0, bound = nodes.length; i < bound; i++) {
            Node nearest = heap.popMin();
            for (Edge edge : edgeList[nearest.pos]) {
                int cost = nearest.cost + edge.cost;
                if (cost > money) {
                    continue;
                }
                int index = index(edge.to, cost);
                int newLen = nearest.length + edge.length;
                if (nodes[index].length > newLen) {
                    nodes[index].length = newLen;
                    heap.dec(index);
                }
            }
        }

        //Find the min length
        int result = INF;
        for (int i = 0, pos = cityNum - 1; i <= money; i++) {
            result = Math.min(result, nodes[index(pos, i)].length);
        }
        return result == INF ? -1 : result;
    }

    public void init() {
        money = input.nextInteger();
        cityNum = input.nextInteger();
        int r = input.nextInteger();
        edgeList = new List[cityNum];
        for (int i = 0; i < cityNum; i++) {
            edgeList[i] = new ArrayList();
        }
        for (int i = 0; i < r; i++) {
            Edge edge = new Edge();
            edge.from = input.nextInteger() - 1;
            edge.to = input.nextInteger() - 1;
            edge.length = input.nextInteger();
            edge.cost = input.nextInteger();
            edgeList[edge.from].add(edge);
        }

        nodes = new Node[index(cityNum - 1, money) + 1];
        for (int i = 0; i < cityNum; i++) {
            for (int j = 0; j <= money; j++) {
                Node node = new Node();
                node.pos = i;
                node.cost = j;
                node.length = INF;
                nodes[index(i, j)] = node;
            }
        }
    }

    public static class Heap<T> {
        Comparator<T> cmp;
        T[] data;
        int[] orders = new int[0];
        int[] ranks = new int[0];
        int remain;

        public int size() {
            return remain;
        }

        public void init(T[] data, Comparator<T> cmp) {
            this.data = data;
            this.cmp = cmp;
            remain = data.length;
            if (orders.length < remain + 1) {
                orders = new int[remain + 1];
            }
            if (ranks.length < remain) {
                ranks = new int[remain];
            }
            for (int i = 0; i < remain; i++) {
                orders[i + 1] = i;
                ranks[i] = i + 1;
            }

            for (int i = 2; i <= remain; i++) {
                dec0(i);
            }
        }

        public void inc(int i) {
            inc0(ranks[i]);
        }

        private void inc0(int i) {
            int r;
            while ((r = right(i)) <= remain) {
                int l = left(i);
                int minChild = cmp.compare(elementAt(l), elementAt(r)) <= 0 ? l : r;
                if (cmp.compare(elementAt(minChild), elementAt(i)) >= 0) {
                    break;
                }
                swap(minChild, i);
                i = minChild;
            }
            if (r == remain + 1) {
                if (cmp.compare(elementAt(remain), elementAt(i)) < 0) {
                    swap(remain, i);
                }
            }
        }

        public T popMin() {
            T min = elementAt(1);
            swap(1, remain);
            remain--;
            inc0(1);
            return min;
        }

        public void dec(int i) {
            dec0(ranks[i]);
        }

        private void dec0(int i) {
            int f;
            while ((f = father(i)) >= 1 && cmp.compare(elementAt(i), elementAt(f)) < 0) {
                swap(i, f);
                i = f;
            }
        }

        private T elementAt(int i) {
            return data[orders[i]];
        }

        private void swap(int i, int j) {
            int t = orders[i];
            orders[i] = orders[j];
            orders[j] = t;

            ranks[orders[i]] = i;
            ranks[orders[j]] = j;
        }

        private int left(int i) {
            return i << 1;
        }

        private int right(int i) {
            return (i << 1) | 1;
        }

        private int father(int i) {
            return i >> 1;
        }
    }

    public static class Edge {
        int from;
        int to;
        int length;
        int cost;
    }

    public static class Node {
        int pos;
        int cost;
        int length;
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
                } catch (Throwable e) {
                    dSize = -1;
                }
            }
            return dBuf[dPos++];
        }
    }
}
