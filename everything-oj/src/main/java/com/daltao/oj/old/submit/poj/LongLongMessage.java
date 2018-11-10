package com.daltao.oj.old.submit.poj;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by dalt on 2017/12/13.
 */
public class LongLongMessage {
    static BlockReader input;
    String textOne;
    String textTwo;

    public static void main(String[] args) throws Exception {
     /*   System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\LongLongMessage.in"));
*/
        input = new BlockReader(System.in);
        while (input.hasMore()) {
            LongLongMessage goingHome = new LongLongMessage();
            goingHome.init();
            System.out.println(goingHome.solve());
        }
    }

    public void init() {
        textOne = input.nextBlock();
        textTwo = input.nextBlock();
    }

    public int solve() {
        char[] data = new StringBuilder(textOne.length() + textTwo.length() + 1)
                .append(textOne).append((char) ('z' + 1)).append(textTwo).append((char) ('z' + 2)).toString().toCharArray();

        SuffixArray array = SuffixArray.makeSuffixArray(data, 'a', 'z' + 3);

        //Suffix array
        int sepPost = textOne.length();
        int[] match = new int[data.length + 2];
        int maxMatchDegree = 0;
        int lastMatchDegree = 0;

        int p1 = 0;
        int p2 = 0;
        for (int i = 0, bound = data.length; i < bound; i++) {
            lastMatchDegree = Math.max(lastMatchDegree - 1, 0);
            int curRank = array.getRankByStartIndex(i);
            int preStartIndex = array.getStartIndexByRank(curRank - 1);

            for (int si = i, pi = preStartIndex, degreeBound = data.length - Math.max(si, pi);
                 lastMatchDegree < degreeBound && data[si + lastMatchDegree] == data[pi + lastMatchDegree]; lastMatchDegree++)
                ;
            match[curRank] = lastMatchDegree;
            if (i < sepPost && preStartIndex >= sepPost ||
                    i >= sepPost && preStartIndex < sepPost) {
                maxMatchDegree = Math.max(lastMatchDegree, maxMatchDegree);
                if (maxMatchDegree == lastMatchDegree) {
                    p1 = i;
                    p2 = preStartIndex;
                }
            }
        }


       System.out.println(new String(data, p1, maxMatchDegree));
       System.out.println(new String(data, p2, maxMatchDegree));
        return maxMatchDegree;
    }

    public static class SuffixArray {
        int[] rank;
        int[] revRank;
        char[] data;

        private SuffixArray(int[] rank, int[] revRank, char[] data) {
            this.revRank = revRank;
            this.rank = rank;
            this.data = data;
        }

        public static SuffixArray makeSuffixArray(char[] s, int rangeFrom, int rangeTo) {
            int n = s.length;
            int range = n + 1;
            Loop<int[]> rankLoop = new Loop(new int[3][n + 1]);

            int[] orderedSuffix = new int[n + 1];
            int[] firstRanks = rankLoop.get(0);
            for (int i = 0; i < n; i++) {
                orderedSuffix[i] = i;
                firstRanks[i] = s[i] - rangeFrom + 1;
            }
            orderedSuffix[n] = n;
            firstRanks[n] = 0;
            Loop<int[]> suffixLoop = new Loop(new int[][]{
                    orderedSuffix, new int[n + 1]
            });

            radixSort(suffixLoop.get(0), suffixLoop.get(1), rankLoop.get(0), rangeTo - rangeFrom + 1);
            assignRank(suffixLoop.turn(), rankLoop.get(0), rankLoop.get(0), rankLoop.turn());

            for (int i = 1; i < n; i <<= 1) {
                int until = range - i;
                System.arraycopy(rankLoop.get(0), i + 1, rankLoop.get(1), 1, range - i - 1);
                Arrays.fill(rankLoop.get(1), range - i + 1, range, 0);
                radixSort(suffixLoop.get(0), suffixLoop.turn(), rankLoop.get(1), range);
                radixSort(suffixLoop.get(0), suffixLoop.turn(), rankLoop.get(0), range);
                assignRank(suffixLoop.get(0), rankLoop.get(0), rankLoop.get(1), rankLoop.turn(2));
            }

            firstRanks = rankLoop.get(0);
            return new SuffixArray(firstRanks, suffixLoop.get(), s);
        }

        public static void assignRank(int[] seq, int[] firstKeys, int[] secondKeys, int[] rankOutput) {
            int cnt = 0;
            rankOutput[0] = 0;
            for (int i = 1, bound = seq.length; i < bound; i++) {
                if (firstKeys[seq[i - 1]] != firstKeys[seq[i]] ||
                        secondKeys[seq[i - 1]] != secondKeys[seq[i]]) {
                    cnt++;
                }
                rankOutput[seq[i]] = cnt;
            }
        }

        public static void radixSort(int[] oldSeq, int[] newSeq, int[] seqRanks, int range) {
            int[] counters = new int[range];
            for (int rank : seqRanks) {
                counters[rank]++;
            }
            int[] ranks = new int[range];
            ranks[0] = 0;
            for (int i = 1; i < range; i++) {
                ranks[i] = ranks[i - 1] + (counters[i] > 0 ? 1 : 0);
                counters[i] += counters[i - 1];
            }

            for (int i = oldSeq.length - 1; i >= 0; i--) {
                int newPos = --counters[seqRanks[oldSeq[i]]];
                newSeq[newPos] = oldSeq[i];
            }
        }

        public int getStartIndexByRank(int rank) {
            return revRank[rank];
        }

        public int getRankByStartIndex(int startIndex) {
            return rank[startIndex];
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 1, bound = revRank.length; i < bound; i++) {
                result.append(i).append(" : ").append(new String(data, revRank[i], data.length - revRank[i])).append(";\n ");
            }
            return result.toString();
        }

        public static class Loop<T> {
            T[] loops;
            int offset;

            public Loop(T[] initVal) {
                loops = initVal;
            }

            public T get(int index) {
                return loops[(offset + index) % loops.length];
            }

            public T get() {
                return get(0);
            }

            public T turn(int degree) {
                offset += degree;
                return get(0);
            }

            public T turn() {
                return turn(1);
            }
        }

        public static class Suffix {
            int suffixStartIndex;
            int rank;

            @Override
            public String toString() {
                return suffixStartIndex + ":" + rank;
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
