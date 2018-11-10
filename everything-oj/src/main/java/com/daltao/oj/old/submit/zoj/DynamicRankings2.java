package com.daltao.oj.old.submit.zoj;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/1/1.
 */
public class DynamicRankings2 {
    public static BlockReader input;
    HistoryTree tree = new HistoryTree(0, 60000, 60000 * 20, 50000);
    int[] values = new int[60000];
    int[] rankMap = new int[60000];
    OrderedMap orderedMap = new OrderedMap(60000);
    int[][] request = new int[10000][4];
    StringBuilder builder = new StringBuilder();

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\zoj\\DynamicRankings.in"));
        input = new BlockReader(System.in);
        DynamicRankings2 solution = new DynamicRankings2();
        for (int i = 1, bound = input.nextInteger(); i <= bound; i++) {
            solution.reInit();
            System.out.print(solution.solve());
        }
    }

    public void reInit() {
    }

    public String solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        int valuesCnt = n;

        for (int i = 0; i < n; i++) {
            values[i] = input.nextInteger();
        }

        char[] cmd = new char[1];
        for (int i = 0; i < m; i++) {
            input.nextBlock(cmd, 0);
            switch (cmd[0]) {
                case 'Q': {
                    request[i][0] = 'Q';
                    request[i][1] = input.nextInteger();
                    request[i][2] = input.nextInteger();
                    request[i][3] = input.nextInteger();
                    break;
                }
                case 'C': {
                    request[i][0] = 'C';
                    request[i][1] = input.nextInteger();
                    request[i][2] = input.nextInteger();
                    request[i][3] = valuesCnt;
                    values[valuesCnt++] = request[i][2];
                    break;
                }
            }
        }


        orderedMap.reInit(values, valuesCnt);
        tree.reInit(orderedMap.minRank(), orderedMap.maxRank());

        builder.setLength(0);
        for (int i = 0; i < n; i++) {
            tree.insert(orderedMap.ranks[i], 1);
            rankMap[i] = orderedMap.ranks[i];
        }
        for (int i = 0; i < m; i++) {
            switch (request[i][0]) {
                case 'C': {
                    int id = request[i][3];
                    int x = request[i][1];
                    int newVal = request[i][2];
                    int valIndex = request[i][3];
                    tree.update(x, rankMap[x - 1], -1);
                    rankMap[x - 1] = orderedMap.ranks[valIndex];
                    tree.update(x, rankMap[x - 1], 1);
                    break;
                }
                case 'Q': {
                    int result = tree.theKth(request[i][1] - 1, request[i][2], request[i][3]);
                    builder.append(values[orderedMap.orders[result]]).append('\n');
                    break;
                }
            }
        }

        return builder.toString();
    }

    public static class OrderedMap {
        int[] orders;
        Integer[] intWrapperCache;
        Integer[] intWrappers;
        int[] ranks;
        int n;

        public OrderedMap(int cacheSize) {
            orders = new int[cacheSize];
            ranks = new int[cacheSize];
            intWrapperCache = new Integer[cacheSize];
            intWrappers = new Integer[cacheSize];
            for (int i = 0; i < cacheSize; i++) {
                intWrapperCache[i] = i;
            }
        }

        public void reInit(final int[] values, int n) {
            this.n = n;
            System.arraycopy(intWrapperCache, 0, intWrappers, 0, n);
            Arrays.sort(intWrappers, 0, n, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return values[o1] - values[o2];
                }
            });
            for (int i = 0; i < n; i++) {
                orders[i] = intWrappers[i].intValue();
            }
            int cnt = 0;
            ranks[orders[0]] = 0;
            for (int i = 1; i < n; i++) {
                if (values[orders[i]] != values[orders[i - 1]]) {
                    cnt = i;
                }
                ranks[orders[i]] = cnt;
            }
        }

        public int minRank() {
            return 0;
        }

        public int maxRank() {
            return ranks[orders[n - 1]];
        }
    }

    public static class HistoryTree {
        List<Interval> historyList;
        Interval[] pool;
        int poolCnt;
        Interval[] bit;
        Interval empty;

        int leftBound;
        int rightBound;
        List<Interval> v1BitIntervalList = new ArrayList<>();
        List<Interval> v2BitIntervalList = new ArrayList<>();
        int viewFrom;
        int viewTo;

        public HistoryTree(int leftBound, int rightBound, int poolSize, int maxVersion) {
            this.leftBound = leftBound;
            this.rightBound = rightBound;
            empty = buildTree(leftBound, rightBound);
            historyList = new ArrayList<>();
            bit = new Interval[maxVersion + 1];
            pool = new Interval[poolSize];
            for (int i = 0; i < poolSize; i++) {
                pool[i] = new Interval();
            }
        }

        public static Interval buildTree(int left, int right) {
            Interval interval = new Interval();
            if (left != right) {
                int center = (left + right) >> 1;
                interval.left = buildTree(left, center);
                interval.right = buildTree(center + 1, right);
            }
            return interval;
        }

        public Interval getInterval() {
            return pool[poolCnt++];
        }

        public void insert(int x, int val) {
            historyList.add(update(x, val, leftBound, rightBound, historyList.get(historyList.size() - 1)));
        }

        public void update(int version, int x, int val) {
            for (int i = version, bound = bit.length; i < bound; i += lowbit(i)) {
                bit[i] = update(x, val, leftBound, rightBound, bit[i]);
            }
        }

        public int theKth(int v1, int v2, int k) {
            v1BitIntervalList.clear();
            v2BitIntervalList.clear();
            for (int i = v1; i > 0; i -= lowbit(i)) {
                v1BitIntervalList.add(bit[i]);
            }
            for (int i = v2; i > 0; i -= lowbit(i)) {
                v2BitIntervalList.add(bit[i]);
            }

            Interval v1Interval = historyList.get(v1);
            Interval v2Interval = historyList.get(v2);

            int left = leftBound, right = rightBound;
            while (left != right) {
                int center = (left + right) >> 1;
                int sum = v2Interval.left.sum - v1Interval.left.sum;
                for (Interval interval : v1BitIntervalList) {
                    sum -= interval.left.sum;
                }
                for (Interval interval : v2BitIntervalList) {
                    sum += interval.left.sum;
                }
                if (sum >= k) {
                    v1Interval = v1Interval.left;
                    v2Interval = v2Interval.left;
                    turnLeft(v1BitIntervalList);
                    turnLeft(v2BitIntervalList);
                    right = center;
                } else {
                    k -= sum;
                    v1Interval = v1Interval.right;
                    v2Interval = v2Interval.right;
                    turnRight(v2BitIntervalList);
                    turnRight(v1BitIntervalList);
                    left = center + 1;
                }
            }
            return left;
        }

        private void turnLeft(List<Interval> intervals) {
            for (int i = 0, bound = intervals.size(); i < bound; i++) {
                intervals.set(i, intervals.get(i).left);
            }
        }

        private void turnRight(List<Interval> intervals) {
            for (int i = 0, bound = intervals.size(); i < bound; i++) {
                intervals.set(i, intervals.get(i).right);
            }
        }

        private Interval update(int x, int val, int left, int right, Interval interval) {
            Interval clone = getInterval();
            clone.sum = interval.sum + val;
            if (left == right) {
                clone.left = interval.left;
                clone.right = interval.right;
                return clone;
            }
            int center = (left + right) >> 1;
            if (center >= x) {
                clone.left = update(x, val, left, center, interval.left);
                clone.right = interval.right;
            } else {
                clone.left = interval.left;
                clone.right = update(x, val, center + 1, right, interval.right);
            }
            return clone;
        }


        public void reInit(int viewFrom, int viewTo) {
            historyList.clear();
            historyList.add(empty);
            Arrays.fill(bit, empty);
            poolCnt = 0;

            this.viewFrom = viewFrom;
            this.viewTo = viewTo;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < historyList.size(); i++) {
                for (int j = 1; j <= i; j++) {
                    builder.append(theKth(0, i, j)).append(',');
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        private int lowbit(int x) {
            return x & -x;
        }

        private static class Interval {
            int sum;
            Interval left;
            Interval right;

            @Override
            public String toString() {
                return String.format("%d", sum);
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
}

