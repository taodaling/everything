package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dalt on 2017/12/15.
 */
public class CommonSubstrings {
    public static BlockReader input;
    String first;
    String second;
    int k;
    int seperateIndex;
    int[] heights;
    int[] orders;
    Loop<int[]> bufLoop;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\CommonSubstrings.in"));

        input = new BlockReader(System.in);
        int k;
        while ((k = input.nextInteger()) != 0) {
            CommonSubstrings solution = new CommonSubstrings();
            solution.init(k);
            System.out.println(solution.solve());
        }
    }

    public void init(int k) {
        this.k = k;
        first = input.nextBlock();
        second = input.nextBlock();
    }

    public long solve() {
        char[] data = (first + '$' + second).toCharArray();
        seperateIndex = first.length();

        SuffixArray suffixArray = SuffixArray.makeSuffixArray(data);
        heights = suffixArray.matchDegrees.clone();
        for (int i = 1, bound = heights.length; i < bound; i++) {
            heights[i] = Math.max(0, heights[i] - k + 1);
        }
        orders = suffixArray.orders.clone();
        bufLoop = new Loop(new int[heights.length + 1],
                new int[heights.length + 1], new int[heights.length + 1]);
        long result = kMatch(1, heights.length);
        return result;
    }

    public long kMatch(int from, int to) {
        if (from + 1 == to) {
            return 0;
        }

        int half = (from + to) >> 1;
        long result = kMatch(from, half) + kMatch(half, to); //Recursive part
        int[] lHeights = bufLoop.get(0);
        int[] rHeights = bufLoop.get(1);
        int[] belongToFirstCnts = bufLoop.get(2);
        lHeights[half] = Integer.MAX_VALUE;
        for (int i = half - 1; i >= from; i--) {
            lHeights[i] = Math.min(heights[i + 1], lHeights[i + 1]);
        }
        rHeights[half] = Integer.MAX_VALUE;
        belongToFirstCnts[half] = orders[half] < seperateIndex ? 1 : 0;
        for (int i = half + 1; i < to; i++) {
            rHeights[i] = Math.min(rHeights[i - 1], heights[i]);
            belongToFirstCnts[i] = belongToFirstCnts[i - 1] + (orders[i] < seperateIndex ? 1 : 0);
        }

        int lindex = from;
        int rindex = to - 1;
        long preFix = 0;
        long posFix = 0;
        while (lindex < half) {
            while (lHeights[lindex] > rHeights[rindex]) {
                if (orders[rindex] < seperateIndex) {
                    preFix += rHeights[rindex];
                } else {
                    posFix += rHeights[rindex];
                }
                rindex--;
            }
            if (orders[lindex] < seperateIndex) {
                result += posFix + (long)lHeights[lindex] * (rindex + 1 - half - belongToFirstCnts[rindex]);
            } else {
                result += preFix + (long)lHeights[lindex] * belongToFirstCnts[rindex];
            }

            lindex++;
        }

        return result;
    }

    public static class Loop<T> {
        T[] data;
        int offset;

        public Loop(T... initVal) {
            this.data = initVal;
        }

        public T get(int i) {
            return data[(offset + i) % data.length];
        }

        public T turn(int i) {
            offset += i;
            return get(0);
        }
    }

    public static class SuffixArray {
        int[] ranks;
        int[] orders;
        int[] matchDegrees;

        private SuffixArray() {
        }

        public static SuffixArray makeSuffixArray(char[] data) {

            int n = data.length + 1;
            Loop<int[]> orderLoop = new Loop(new int[n], new int[n]);
            Loop<int[]> rankLoop = new Loop(new int[n], new int[n], new int[n]);
            {
                Integer[] orderWrapper = new Integer[n];
                int[] orders = orderLoop.get(0);
                final int[] ranks = rankLoop.get(0);
                for (int i = 0, bound = n - 1; i < bound; i++) {
                    orderWrapper[i] = i;
                    ranks[i] = data[i];
                }
                orderWrapper[n - 1] = n - 1;
                ranks[n - 1] = 0;
                Arrays.sort(orderWrapper, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return ranks[o1] - ranks[o2];
                    }
                });
                for (int i = 0; i < n; i++) {
                    orders[i] = orderWrapper[i];
                }
                resetRank(rankLoop.get(0), rankLoop.get(0), orderLoop.get(0), rankLoop.turn(1));
            }
            for (int i = 1; i < n; i <<= 1) {
                int[] secondKeys = rankLoop.get(1);
                System.arraycopy(rankLoop.get(0), i, secondKeys, 0, n - i - 1); //j + i < n - 1 => j < n - 1 - i
                Arrays.fill(secondKeys, n - i - 1, n, 0); // j + i >= n - 1 => j >= n - 1 - i
                radixSort(orderLoop.get(0), orderLoop.turn(1), secondKeys, rankLoop.get(2), n);
                radixSort(orderLoop.get(0), orderLoop.turn(1), rankLoop.get(0), rankLoop.get(2), n);
                resetRank(rankLoop.get(0), rankLoop.get(1), orderLoop.get(0), rankLoop.turn(2));
            }
            SuffixArray sa = new SuffixArray();
            int[] orders = orderLoop.get(0);
            int[] ranks = rankLoop.get(0);
            sa.orders = orders;
            sa.ranks = ranks;

            int[] matchDegrees = new int[n];
            int lastMatchDegree = 0;
            for (int i = 0, iBound = n - 1; i < iBound; i++) {
                int rank = ranks[i];
                int lastRank = rank - 1;
                int lastOrder = orders[lastRank];

                lastMatchDegree = Math.max(0, lastMatchDegree - 1);
                for (int bound = n - 1 - Math.max(i, lastOrder); lastMatchDegree < bound &&
                        data[lastMatchDegree + i] == data[lastMatchDegree + lastOrder]; lastMatchDegree++)
                    ;

                matchDegrees[rank] = lastMatchDegree;
            }

            sa.matchDegrees = matchDegrees;
            return sa;
        }

        public static void resetRank(int[] key1, int[] key2, int[] orders, int[] output) {
            int cnt = 0;
            output[0] = 0;
            for (int i = 1, bound = orders.length; i < bound; i++) {
                if (key1[orders[i]] != key1[orders[i - 1]] || key2[orders[i]] != key2[orders[i - 1]]) {
                    cnt++;
                }
                output[orders[i]] = cnt;
            }
        }

        public static void radixSort(int[] orders, int[] output, int[] ranks, int[] unused, int range) {
            int[] cnts = null;
            if (unused != null && unused.length > range) {
                cnts = unused;
            } else {
                cnts = new int[range];
            }

            for (int rank : ranks) {
                cnts[rank]++;
            }
            for (int i = 1; i < range; i++) {
                cnts[i] += cnts[i - 1];
            }
            for (int i = orders.length - 1; i >= 0; i--) {
                output[--cnts[ranks[orders[i]]]] = orders[i];
            }
        }

        public int getMatchDegree(int rank) {
            return matchDegrees[rank];
        }

        public int getOrderByRank(int rank) {
            return orders[rank];
        }

        public int getRankByOrder(int order) {
            return ranks[order];
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
