package com.daltao.oj.old.submit.poj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Administrator on 2018/1/13.
 */
public class POJ2104 {
    static final int UPDATE_LIMIT = 100000;
    static final int N_LIMIT = 100000;
    static final int RANGE = UPDATE_LIMIT + N_LIMIT;
    static final int[] initVals = new int[N_LIMIT];
    static final IntList allPossibleVals = new IntList(RANGE);
    static final int[][] request = new int[UPDATE_LIMIT][4];
    static final int[] currentVals = new int[N_LIMIT];
    static final StringBuilder BUILDER = new StringBuilder();
    public static BlockReader input;
    static ChairTree tree = new ChairTree(0, 100000, 10000);
    static RangeMap map = new RangeMap(RANGE);

    public static void main(String[] args) throws FileNotFoundException {
    //    System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2104.in"));
        throw new RuntimeException();
//        input = new BlockReader(System.in);
//        for(int i = 0, bound = input.nextInteger(); i < bound; i++){
//            POJ2104 solution = new POJ2104();
//            System.out.print(solution.solve());
//        }
    }

    public String solve() {
        int n = input.nextInteger();
        int q = input.nextInteger();
        allPossibleVals.clear();
        for (int i = 0; i < n; i++) {
            initVals[i] = input.nextInteger();
            allPossibleVals.add(initVals[i]);
        }

        for (int i = 0; i < q; i++) {
            //request[i][0] = input.nextInteger();
            request[i][1] = input.nextInteger();
            request[i][2] = input.nextInteger();
//            if (request[i][0] == 1) {
//                request[i][3] = allPossibleVals.size();
//                allPossibleVals.add(request[i][2]);
//            } else {
                request[i][3] = input.nextInteger();
//            }
        }

        map.init(allPossibleVals);
        tree.init(0, map.ranks[map.orders[allPossibleVals.size() - 1][0]]);

        for (int i = 0; i < n; i++) {
            currentVals[i] = map.ranks[i];
            tree.updateValue(currentVals[i], 1);
            //System.out.println(tree.toString());
        }

        BUILDER.setLength(0);
        for (int i = 0; i < q; i++) {
//            //Update
//            if (request[i][0] == 1) {
//                int history = request[i][1];
//                int index = history - 1;
//                int targetRank = map.ranks[request[i][3]];
//                tree.updateValue(history, currentVals[index], -1);
//                currentVals[index] = targetRank;
//                tree.updateValue(history, currentVals[index], 1);
//            } else {
                int l = request[i][1];
                int r = request[i][2];
                int k = request[i][3];
                int v = tree.findTheKthValue(l, r, k);
                BUILDER.append(allPossibleVals.get(map.orders[v][0])).append('\n');
//            }
        }

        return BUILDER.toString();
    }

    public static class RangeMap {
        int[][] orders;
        int[] ranks;

        public RangeMap(int cap) {
            ranks = new int[cap];
            orders = new int[cap][1];
        }

        public void init(final IntList data) {
            for (int i = 0, bound = data.size(); i < bound; i++) {
                orders[i][0] = i;
            }

            Arrays.sort(orders, 0, data.size(), new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return data.get(o1[0]) - data.get(o2[0]);
                }
            });

            int rankCnt = 0;
            ranks[orders[0][0]] = 0;
            for (int i = 1, bound = data.size(); i < bound; i++) {
                if (data.get(orders[i][0]) != data.get(orders[i - 1][0])) {
                    rankCnt = i;
                }
                ranks[orders[i][0]] = rankCnt;
            }
        }
    }

    public static class ChairTree {
        int[] lefts;
        int[] rights;
        int[] sums;
        int idRecorder;
        int idRecorderRollBack;
        int leftBound;
        int rightBound;
        IntList historyList;
        int[] bit;
        IntList vFIds = new IntList(32);
        IntList vTIds = new IntList(32);
        int actuallyL;
        int actuallyR;

        public ChairTree(int leftBound, int rightBound, int cap) {
            int size = rightBound - leftBound + 1;

            lefts = new int[cap];
            rights = new int[cap];
            sums = new int[cap];
            historyList = new IntList(size + 1);
            bit = new int[size + 1];
            this.leftBound = leftBound;
            this.rightBound = rightBound;
            buildTree(leftBound, rightBound);
            idRecorderRollBack = idRecorder;
        }

        public void updateValue(int x, int v) {
            int id = updateValue(x, v, leftBound, rightBound, historyList.get(historyList.size() - 1));
            historyList.add(id);
        }

        public int updateValue(int x, int v, int l, int r, int id) {
            int cloneId = getClone(id);
            sums[cloneId] += v;
            if (l != r) {
                int c = (l + r) >> 1;
                if (x <= c) {
                    lefts[cloneId] = updateValue(x, v, l, c, lefts[cloneId]);
                } else {
                    rights[cloneId] = updateValue(x, v, c + 1, r, rights[cloneId]);
                }
            }
            return cloneId;
        }

        public void updateValue(int h, int x, int v) {
            for (int i = h, bound = bit.length; i < bound; i += i & -i) {
                bit[i] = updateValue(x, v, leftBound, rightBound, bit[i]);
            }
        }

        public int findTheKthValue(int vF, int vT, int k) {
            vF--;
            vFIds.clear();
            for (int i = vF; i > 0; i -= i & -i) {
                vFIds.add(bit[i]);
            }
            vTIds.clear();
            for (int i = vT; i > 0; i -= i & -i) {
                vTIds.add(bit[i]);
            }

            int l = leftBound;
            int r = rightBound;
            int vFId = historyList.get(vF);
            int vTId = historyList.get(vT);
            while (l != r) {
                int sumOfLeft = sums[lefts[vTId]] - sums[lefts[vFId]];
                        //+ sumOfLeft(vTIds) - sumOfLeft(vFIds);
                int c = (l + r) >> 1;
                if (sumOfLeft >= k) {
                    r = c;
                    vFId = lefts[vFId];
                    vTId = lefts[vTId];
//                    switchLeft(vFIds);
//                    switchLeft(vTIds);
                } else {
                    k -= sumOfLeft;
                    l = c + 1;
                    vFId = rights[vFId];
                    vTId = rights[vTId];
//                    switchRight(vFIds);
//                    switchRight(vTIds);
                }
            }

            return l;
        }

        public void switchLeft(IntList list) {
            for (int i = 0, bound = list.size(); i < bound; i++) {
                list.set(i, lefts[list.get(i)]);
            }
        }

        public void switchRight(IntList list) {
            for (int i = 0, bound = list.size(); i < bound; i++) {
                list.set(i, rights[list.get(i)]);
            }
        }

        public int sumOfLeft(IntList list) {
            int sum = 0;
            for (int i = 0, bound = list.size(); i < bound; i++) {
                sum += sums[lefts[list.get(i)]];
            }
            return sum;
        }

        public int buildTree(int l, int r) {
            int id = getId();
            if (l < r) {
                int c = (l + r) >> 1;
                lefts[id] = buildTree(l, c);
                rights[id] = buildTree(c + 1, r);
            }
            return id;
        }

        public int getClone(int id) {
            int cloneId = getId();
            lefts[cloneId] = lefts[id];
            rights[cloneId] = rights[id];
            sums[cloneId] = sums[id];
            return cloneId;
        }

        public int getId() {
            return idRecorder++;
        }

        public void init(int l, int r) {
            idRecorder = idRecorderRollBack;
            historyList.clear();
            historyList.add(0);
            //Arrays.fill(bit, 0);
            actuallyL = l;
            actuallyR = r;
        }

        public String toString() {
            String s = "";
            for (int i = 1; i < historyList.size(); i++) {
                s += "V " + i + "-" + findTheKthValue(i, i, 1);
                s += "\n";
            }
            return s;
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

    public static class IntList {
        int[] data;
        int top;

        public IntList(int cap) {
            data = new int[cap];
        }

        public void add(int v) {
            data[top++] = v;
        }

        public void set(int i, int v) {
            data[i] = v;
        }

        public int get(int i) {
            return data[i];
        }

        public int size() {
            return top;
        }

        public void clear() {
            top = 0;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < top; i++) {
                s.append(data[i]).append(',');
            }
            if (s.length() > 0) {
                s.setLength(s.length() - 1);
            }
            return s.toString();
        }
    }
}
