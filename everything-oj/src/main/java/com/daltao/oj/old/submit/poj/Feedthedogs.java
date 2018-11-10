package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/12/28.
 */
public class Feedthedogs {
    static final int MAX_N = 100000;
    static final int MAX_M = 50000;
    static final int INF = (int) 1e8;
    static StringBuilder builder = new StringBuilder();
    static BlockReader input;
    static int[] data = new int[MAX_N];
    static ChairTree tree = new ChairTree();

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Feedthedogs.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            solve();
        }
        System.out.print(builder.toString());
    }

    public static void init() {
    }

    public static void solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        for (int i = 0; i < n; i++) {
            data[i] = input.nextInteger();

        }
        OrderedMap.reset(data, n);
        tree.reset(n);
        for (int i = 0; i < n; i++) {
            tree.update(OrderedMap.ranks[i]);
        }

        for (int i = 0; i < m; i++) {
            int v1 = input.nextInteger() - 1;
            int v2 = input.nextInteger();
            int rank = input.nextInteger();

            int index = tree.theKthIndex(v1, v2, rank);
            builder.append(data[OrderedMap.orders[index]]).append('\n');
        }
    }

    public static class OrderedMap {
        static int[] orders = new int[MAX_N];
        static int[] ranks = new int[MAX_N];
        static Integer[] ordersWrapperData = new Integer[MAX_N];
        static Integer[] ordersWrapper = new Integer[MAX_N];
        static int size;

        static {
            for (int i = 0; i < MAX_N; i++) {
                ordersWrapperData[i] = i;
            }
        }

        static void reset(final int[] data, int size) {
            OrderedMap.size = size;
            System.arraycopy(ordersWrapperData, 0, ordersWrapper, 0, size);
            Arrays.sort(ordersWrapper, 0, size, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return data[o1] - data[o2];
                }
            });
            for (int i = 0; i < size; i++) {
                orders[i] = ordersWrapper[i].intValue();
            }
            ranks[orders[0]] = 0;
            int cnt = 0;
            for (int i = 1; i < size; i++) {
                if (data[orders[i]] != data[orders[i - 1]]) {
                    cnt = i;
                }
                ranks[orders[i]] = cnt;
            }
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

    public static class ChairTree {
        int[] sums = new int[20 * MAX_N];
        int[] leftSons = new int[20 * MAX_N];
        int[] rightSons = new int[20 * MAX_N];
        int[] history = new int[MAX_N + 1];
        int historySize = 1;
        int idAllocator = 0;
        int idAllocatorRestore = 0;
        int size;

        {
            buildTree(0, MAX_N);
            idAllocatorRestore = idAllocator;
        }

        void pushHistory(int root) {
            history[historySize++] = root;
        }

        int peekHistory() {
            return history[historySize - 1];
        }

        private int alloc() {
            return idAllocator++;
        }

        private int buildTree(int left, int right) {
            int id = alloc();
            if (left != right) {
                int center = (left + right) >> 1;
                leftSons[id] = buildTree(left, center);
                rightSons[id] = buildTree(center + 1, right);
            }
            return id;
        }

        public void reset(int size) {
            idAllocator = idAllocatorRestore;
            historySize = 1;

            int right = MAX_N;
            int id = 0;
            int size2 = size << 1;
            while (right >= size2) {
                right >>= 1;
                id = leftSons[id];
            }
            this.size = right;
            history[0] = id;
        }

        public void copy(int from, int to) {
            leftSons[to] = leftSons[from];
            rightSons[to] = rightSons[from];
            sums[to] = sums[from];
        }

        public void update(int index) {
            pushHistory(update(index, 0, size, peekHistory()));
        }

        public int theKthIndex(int v1, int v2, int k) {
            return theKthIndex(history[v1], history[v2], k, 0, size);
        }

        public int theKthIndex(int v1, int v2, int k, int left, int right) {
            if (left == right) {
                return left;
            }
            int center = (left + right) >> 1;
            int ls1 = sums[leftSons[v1]];
            int ls2 = sums[leftSons[v2]];
            if (ls2 - ls1 >= k) {
                return theKthIndex(leftSons[v1], leftSons[v2], k, left, center);
            } else {
                return theKthIndex(rightSons[v1], rightSons[v2], k - ls2 + ls1, center + 1, right);
            }
        }

        private String asString(int version, int size) {
            String s = "";
            for (int j = 1; j <= size; j++) {
                s += theKthIndex(version - 1, version, j) + ",";
            }
            return s;
        }

        private int update(int index, int left, int right, int id) {
            int cloneId = alloc();
            copy(id, cloneId);
            sums[cloneId]++;
            if (left == right) {
                return cloneId;
            }
            int center = (left + right) >> 1;
            if (center >= index) {
                leftSons[cloneId] = update(index, left, center, leftSons[cloneId]);
            } else {
                rightSons[cloneId] = update(index, center + 1, right, rightSons[cloneId]);
            }
            return cloneId;
        }

        public int sumOf(int version, int from, int to) {
            return sumOf(history[version], 0, MAX_N, from, to);
        }

        public int sumOf(int id, int left, int right, int from, int to) {
            if (left > to || right < from) {
                return 0;
            }
            if (left >= from && right <= to) {
                return sums[id];
            }
            return sumOf(leftSons[id], left, (left + right) / 2, from, to)
                    + sumOf(rightSons[id], (left + right) / 2 + 1, right, from, to);
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = 0; i < historySize; i++) {
                for (int j = 0; j <= size; j++) {
                    s += sumOf(i, j, j) + ",";
                }
                s += "\n";
            }
            return s;
        }
    }
}
