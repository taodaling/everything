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
public class CommonSubStrings2 {
    public static BlockReader input;
    String first;
    String second;
    int k;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\CommonSubstrings.in"));

        input = new BlockReader(System.in);
        int k;
        while ((k = input.nextInteger()) != 0) {
            CommonSubStrings2 solution = new CommonSubStrings2();
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
        int sepPos = first.length();

        SuffixArray suffixArray = SuffixArray.makeSuffixArray(data);

        int n = data.length;
        long result = 0;
        for (int i = 1; i < n; i++) {
            int iOrder = suffixArray.getOrderByRank(i);
            if (iOrder < sepPos) {
                int maxMatch = Integer.MAX_VALUE;
                for (int j = i + 1; j <= n && maxMatch >= k; j++) {
                    maxMatch = Math.min(maxMatch, suffixArray.getMatchDegree(j));
                    if (suffixArray.getOrderByRank(j) > sepPos && maxMatch >= k) {
                        result += maxMatch - k + 1;
                    }
                }
            } else {
                int maxMatch = Integer.MAX_VALUE;
                for (int j = i + 1; j <= n && maxMatch >= k; j++) {
                    maxMatch = Math.min(maxMatch, suffixArray.getMatchDegree(j));
                    if (suffixArray.getOrderByRank(j) < sepPos && maxMatch >= k) {
                        result += maxMatch - k + 1;
                    }
                }
            }
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