package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Created by Administrator on 2017/12/21.
 */
public class StarsinYourWindow {
    static BlockReader input;
    int n;
    int h;
    int w;
    int[][] stars;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\StarsinYourWindow.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            StarsinYourWindow solution = new StarsinYourWindow();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
        n = input.nextInteger();
        w = input.nextInteger();
        h = input.nextInteger();
        stars = new int[n][3];

        int v230 = 1 << 30;
        for (int i = 0; i < n; i++) {
            stars[i][0] = input.nextInteger() - v230;
            stars[i][1] = input.nextInteger() - v230;
            stars[i][2] = input.nextInteger() ;
        }
    }

    public int solve() {
        int[] heights = new int[n * 2];
        for (int i = 0; i < n; i++) {
            heights[(i << 1)] = stars[i][1];
            heights[(i << 1) | 1] = stars[i][1] + h - 1;
        }

        RankMap map = new RankMap(heights);
        SegmentTree tree = new SegmentTree(map.minRank(), map.maxRank());
        int[] xOrders = new int[n];
        {
            Integer[] wrapper = new Integer[n];
            for (int i = 0; i < n; i++) {
                wrapper[i] = i;
            }
            Arrays.sort(wrapper, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return stars[o1][0] < stars[o2][0] ? -1 : stars[o1][0] > stars[o2][0] ? 1 : 0;
                }
            });
            for (int i = 0; i < n; i++) {
                xOrders[i] = wrapper[i];
            }
        }

        int lastOne = 0;
        int max = 0;
        for (int i = 0; i < n; i++) {
            int translatedI = xOrders[i];
            int[] star = stars[translatedI];
            while (stars[xOrders[lastOne]][0] + w <= star[0]) {
                int lastIndex = xOrders[lastOne];
                tree.update(map.ranks[lastIndex << 1], map.ranks[(lastIndex << 1) | 1], -stars[lastIndex][2]);
                lastOne++;
            }
            tree.update(map.ranks[translatedI << 1], map.ranks[(translatedI << 1) | 1], star[2]);

            max = Math.max(max, tree.getMax());
        }

        return max;
    }

    public static class SegmentTree {
        int leftBound;
        int rightBound;
        int actuallyRightBound;
        int[] data;
        int[] cache;

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
            cache = new int[properSize * 2];
            Arrays.fill(data, 0);
        }

        public void update(int from, int to, int val) {
            if (to < leftBound || from > rightBound) {
                return;
            }
            updateRec(leftBound, actuallyRightBound, 1, from, to, val);
        }

        private void updateRec(int left, int right, int index, int from, int to, int val) {
            if (left >= from && right <= to) {
                data[index] += val;
                return;
            }
            int center = (left + right) >> 1;
            if (center >= from) {
                updateRec(left, center, leftChild(index), from, to, val);
            }
            if (center < to) {
                updateRec(center + 1, right, rightChild(index), from, to, val);
            }
            cache[index] = Math.max(getMax(leftChild(index)), getMax(rightChild(index)));
        }

        public int getMax() {
            return getMax(1);
        }

        private int getMax(int i) {
            return cache[i] + data[i];
        }

        private int leftChild(int i) {
            return i << 1;
        }

        private int rightChild(int i) {
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

    public static class RankMap {
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
                        return values[o1] < values[o2] ? -1 : values[o1] > values[o2] ? 1 : 0;
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

        public int minRank() {
            return 0;
        }

        public int maxRank() {
            return ranks[orders[n - 1]];
        }
    }
}
