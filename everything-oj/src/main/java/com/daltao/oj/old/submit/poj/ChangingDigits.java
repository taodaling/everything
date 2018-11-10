package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/25.
 */
public class ChangingDigits {
    static final int INF = 100;
    static BlockReader input;
    static int[][][] records = new int[101][10000][4];
    int[] digits;
    int digitNum;
    int k;

    public static void main(String[] args) throws Exception {

        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\ChangingDigits.in"));

            input = new BlockReader(System.in);
            while (input.hasMore()) {
                ChangingDigits solution = new ChangingDigits();
                solution.init();
                System.out.println(solution.solve());
            }
        } catch (ArrayIndexOutOfBoundsException t) {
            return;
        }
    }

    public String solve() {
        for (int i = 0; i < digitNum; i++) {
            for (int j = 0; j < k; j++) {
                int[] record = records[i][j];
                record[0] = 100;
                record[1] = 9;
                record[2] = 9;
                record[3] = 100;
            }
        }

        Loop<int[]> rankLoop = new Loop(new int[3][k]);
        Loop<int[]> orderLoop = new Loop(new int[3][k]);
        int[] memo = new int[Math.max(k, 10)];

        for (int j = 1; j < 10; j++) {
            int remainder = j % k;
            int modifiedTime = j == digits[0] ? 0 : 1;
            int fatherValue = 0;
            int fixedValue = j;
            int[] targetRecorder = records[0][remainder];

            if (estimate(targetRecorder[0], targetRecorder[1], targetRecorder[2])
                    > estimate(modifiedTime, fatherValue, fixedValue)) {
                targetRecorder[0] = modifiedTime;
                targetRecorder[1] = fatherValue;
                targetRecorder[2] = fixedValue;
                targetRecorder[3] = -1;
            }
        }

        for (int i = 0, bound = digitNum - 1; i < bound; i++) {
            int[] curRank = rankLoop.turn(1);
            //Fix rank
            {
                int[] orders = orderLoop.get(0);
                for (int j = 0; j < k; j++) {
                    orders[j] = j;
                }
                int[] main = rankLoop.turn(1);
                int[] minor = rankLoop.turn(1);
                for (int j = 0; j < k; j++) {
                    main[j] = records[i][j][1];
                    minor[j] = records[i][j][2];
                }
                radixSort(minor, orderLoop.get(0), orderLoop.turn(1), memo);
                radixSort(main, orderLoop.get(0), orderLoop.turn(1), memo);
                resetRank(main, minor, orderLoop.get(0), curRank);
            }

            //modify status of i + 1
            for (int j = 0; j < k; j++) {
                int k10 = j * 10;
                for (int t = 0; t < 10; t++) {
                    int remainder = (k10 + t) % k;
                    int modifiedTime = records[i][j][0] + (t == digits[i + 1] ? 0 : 1);
                    int fatherValue = curRank[j];
                    int fixedValue = t;
                    int[] targetRecorder = records[i + 1][remainder];
                    if (estimate(targetRecorder[0], targetRecorder[1], targetRecorder[2])
                            > estimate(modifiedTime, fatherValue, fixedValue)) {
                        targetRecorder[0] = modifiedTime;
                        targetRecorder[1] = fatherValue;
                        targetRecorder[2] = fixedValue;
                        targetRecorder[3] = j;
                    }
                }
            }
        }


        //The best solution should ends with records[digitNum - 1][0]
        char[] result = new char[digitNum];
        int remainder = 0;
        for (int i = digitNum - 1; i >= 0; i--) {
            int[] targetRecord = records[i][remainder];
            result[i] = (char) (targetRecord[2] + '0');
            remainder = targetRecord[3];
        }

        return String.valueOf(result);
    }

    public long estimate(long fixTime, long fatherRank, long fixedValue) {
        return (fixTime << 32) | (fatherRank << 16) | (fixedValue);
    }

    public void resetRank(int[] key1, int[] key2, int[] orders, int[] retRanks) {
        retRanks[orders[0]] = 0;
        int cnt = 0;
        for (int i = 1, bound = orders.length; i < bound; i++) {
            if (key1[orders[i]] != key1[orders[i - 1]] || key2[orders[i]] != key2[orders[i - 1]]) {
                cnt++;
            }
            retRanks[orders[i]] = cnt;
        }
    }

    public void radixSort(int[] ranks, int[] orders, int[] retOrders, int[] memo) {
        Arrays.fill(memo, 0);
        for (int rank : ranks) {
            memo[rank]++;
        }
        for (int i = 1, bound = memo.length; i < bound; i++) {
            memo[i] += memo[i - 1];
        }
        for (int i = orders.length - 1; i >= 0; i--) {
            retOrders[--memo[ranks[orders[i]]]] = orders[i];
        }
    }

    public void init() {
        char[] data = input.nextBlock().toCharArray();
        digitNum = data.length;
        digits = new int[digitNum];
        for (int i = 0, bound = data.length; i < bound; i++) {
            digits[i] = data[i] - '0';
        }
        k = input.nextInteger();
    }

    public static class Loop<T> {
        T[] data;
        int offset;

        public Loop(T... data) {
            this.data = data;
        }

        public T get(int i) {
            return data[(offset + i) % data.length];
        }

        public T turn(int i) {
            offset += i;
            return get(0);
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
                } catch (Throwable e) {
                    dSize = -1;
                }
            }
            return dBuf[dPos++];
        }
    }
}
