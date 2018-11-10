package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dalt on 2017/12/21.
 */
public class Mayorsposters {
    static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\test\\poj\\Mayorsposters.in"));

        input = new BlockReader(System.in);
        int testCaseNum = input.nextInteger();
        while (testCaseNum-- > 0) {
            Mayorsposters solution = new Mayorsposters();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    int n;
    int[][] intervals;

    public void init() {
        n = input.nextInteger();
        intervals = new int[n][2];
        for (int i = 0; i < n; i++) {
            intervals[i][0] = input.nextInteger();
            intervals[i][1] = input.nextInteger();
        }
    }

    public int solve() {
        //Compress data at first
        int[] data = new int[n * 4];
        for (int i = 0; i < n; i++) {
            data[(i << 2) | 0] = intervals[i][0];
            data[(i << 2) | 1] = intervals[i][0] - 1;
            data[(i << 2) | 2] = intervals[i][1];
            data[(i << 2) | 3] = intervals[i][1] + 1;
        }
        RankMap map = new RankMap(data);
        SegmentTree tree = new SegmentTree(map.ranks[map.orders[0]], map.ranks[map.orders[map.orders.length - 1]]);
        for (int i = 0; i < n; i++) {
            int leftRank = map.ranks[(i << 2)];
            int rightRank = map.ranks[(i << 2) | 2];

            tree.update(leftRank, rightRank, i);
        }
        int[] view = tree.getView();
        int[] cnts = new int[n];
        for (int v : view) {
            if (v != SegmentTree.NO_VALUE) {
                cnts[v]++;
            }
        }

        int positiveCnt = 0;
        for (int i = 0; i < n; i++) {
            if (cnts[i] != 0) {
                positiveCnt++;
            }
        }

        return positiveCnt;
    }

    public class RankMap {
        int[] orders;
        int[] ranks;
        int n;

        public RankMap(final int[] values) {
            n = values.length;
            ranks = new int[n];
            orders = new int[n];
            {
                Integer[] wrappers = new Integer[n];
                for (int i = 0; i < n; i++) {
                    wrappers[i] = i;
                }
                Arrays.sort(wrappers, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return values[o1] - values[o2];
                    }
                });
                for (int i = 0; i < n; i++) {
                    orders[i] = wrappers[i];
                }
            }
            {
                int cnt = 0;
                ranks[0] = cnt;
                for (int i = 1; i < n; i++) {
                    if (values[orders[i - 1]] != values[orders[i]]) {
                        cnt++;
                    }
                    ranks[orders[i]] = cnt;
                }
            }
        }
    }

    public static class SegmentTree {
        int leftBound;
        int rightBound;
        int actuallyRightBound;
        int[] data;
        static final int NO_VALUE = -1;

        public SegmentTree(int leftBound, int rightBound) {
            this.leftBound = leftBound;
            this.rightBound = rightBound;

            int properSize = 1;
            int diff = rightBound - leftBound + 1;
            while (diff != 0) {
                properSize <<= 1;
                diff >>= 1;
            }
            actuallyRightBound = leftBound + properSize - 1;

            data = new int[properSize * 2];
            Arrays.fill(data, NO_VALUE);
        }

        public void update(int from, int to, int val) {
            if (to < leftBound || from > rightBound) {
                return;
            }
            updateRec(leftBound, actuallyRightBound, 1, from, to, val);
        }

        private void updateRec(int left, int right, int index, int from, int to, int val) {
            if (left >= from && right <= to) {
                data[index] = val;
                return;
            }
            int center = (left + right) >> 1;
            if (data[index] != NO_VALUE) {
                data[leftChild(index)] = data[rightChild(index)] = data[index];
                data[index] = NO_VALUE;
            }
            if (center >= from) {
                updateRec(left, center, leftChild(index), from, to, val);
            }
            if (center < to) {
                updateRec(center + 1, right, rightChild(index), from, to, val);
            }
        }

        public int[] getView() {
            flush();
            int[] copy = new int[rightBound - leftBound + 1];
            System.arraycopy(data, data.length >> 1, copy, 0, copy.length);
            return copy;
        }

        public void flush() {
            for (int i = 1, bound = data.length >> 1; i < bound; i++) {
                if (data[i] == NO_VALUE) {
                    continue;
                }
                data[leftChild(i)] = data[rightChild(i)] = data[i];
                data[i] = NO_VALUE;
            }
        }


        public int leftChild(int i) {
            return i << 1;
        }

        public int rightChild(int i) {
            return (i << 1) | 1;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 1024);
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
