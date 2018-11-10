package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/23.
 */
public class BalancedLineup {
    static BlockReader input;
    int[] heights;
    int cowNum;
    int questionNum;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\BalancedLineup.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            BalancedLineup solution = new BalancedLineup();
            solution.init();
            System.out.print(solution.solve());
        }
    }

    public void init() {
        cowNum = input.nextInteger();
        questionNum = input.nextInteger();
        heights = new int[cowNum + 1];
        for (int i = 1; i <= cowNum; i++) {
            heights[i] = input.nextInteger();
        }
    }

    public String solve() {
        StringBuilder builder = new StringBuilder();
        IntervalTree tree = new IntervalTree(heights);
        for (int i = 0; i < questionNum; i++) {
            int c1 = input.nextInteger();
            int c2 = input.nextInteger();
            builder.append(tree.getDiffer(c1, c2)).append('\n');
        }
        return builder.toString();
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

    public static class IntervalTree {
        int[] max;
        int[] min;
        int leftBound;
        int rightBound;

        public IntervalTree(int[] heights) {
            int size = heights.length;
            while (size != (size & -size)) {
                size -= size & -size;
            }
            size <<= 1;
            leftBound = 0;
            rightBound = size - 1;
            max = new int[size * 2];
            min = new int[size * 2];
            Arrays.fill(min, 100000000);
            System.arraycopy(heights, 0, max, size, heights.length);
            System.arraycopy(heights, 0, min, size, heights.length);
            for (int i = size + heights.length - 1; i > 1; i--) {
                int father = i >> 1;
                max[father] = Math.max(max[father], max[i]);
                min[father] = Math.min(min[father], min[i]);
            }
        }

        public int getDiffer(int begin, int end) {
            if (end < leftBound || begin > rightBound) {
                return -1;
            }
            int[] minAndMax = new int[] {
                Integer.MAX_VALUE, Integer.MIN_VALUE
            } ;
            getRange(1, leftBound, rightBound, begin, end, minAndMax);
            return minAndMax[1] - minAndMax[0];
        }

        private void getRange(int index, int left, int right, int begin, int end, int[] result) {
            if (left >= begin && right <= end) {
                result[0] = Math.min(result[0], min[index]);
                result[1] = Math.max(result[1], max[index]);
                return;
            }

            int center = (left + right) >> 1;
            if (center >= begin) {
                getRange(getLeftChild(index), left, center, begin, end, result);
            }
            if (center < end) {
                getRange(getRightChild(index), center + 1, right, begin, end, result);
            }
        }

        private int getLeftChild(int i) {
            return i << 1;
        }

        private int getRightChild(int i) {
            return (i << 1) | 1;
        }

    }
}
