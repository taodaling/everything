package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/12/9.
 */
public class Code {
    private static final int INF = (int) 1e8;
    static long[][][] memory = new long[10][26][26];
    static long[][][] valueOfMem = new long[10][26][26];
    private static BlockReader input;

    static {
        try {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\Code.in"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        for (long[][] pieces : memory) {
            for (long[] piece : pieces) {
                Arrays.fill(piece, -1L);
            }
        }
        for (long[][] pieces : valueOfMem) {
            for (long[] piece : pieces) {
                Arrays.fill(piece, -1L);
            }
        }
    }

    char[] word;

    public static void main(String[] args) {

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            Code code = new Code();
            code.init();
            System.out.println(code.solve());
        }
    }

    public void init() {
        word = input.nextBlock().toCharArray();
    }

    public long maxValueOf(int c, int index, int lastC) {
        int cIndex = c - 'a';
        int lastCIndex = lastC - 'a' + 1;
        if (memory[index][lastCIndex][cIndex] == -1) {
            if (index == 0) {
                memory[index][lastCIndex][cIndex] = c - lastC;
            } else {
                memory[index][lastCIndex][cIndex] = valueOf(c, index, lastC) + maxValueOf('z' - index + 1, index - 1, c);
            }
        }

       // System.out.println("maxValueOf(" + (char)c + "," + index + "," + (char)lastC + ")=" +  memory[index][lastCIndex][cIndex]);

        return memory[index][lastCIndex][cIndex];
    }

    public long valueOf(int c, int index, int lastC) {
        int cIndex = c - 'a';
        int lastCIndex = lastC - 'a' + 1;
        if (valueOfMem[index][lastCIndex][cIndex] == -1) {
            if (index == 0) {
                valueOfMem[index][lastCIndex][cIndex] = c - lastC;
            } else {
                if (c == lastC + 1) {
                    if (lastC < 'a') { //First is 'a'
                        valueOfMem[index][lastCIndex][cIndex] = maxValueOf('z' - index + 1, index - 1, lastC);
                    } else {
                        valueOfMem[index][lastCIndex][cIndex] = 0;
                    }
                } else {
                    valueOfMem[index][lastCIndex][cIndex] = maxValueOf(c - 1, index, lastC);
                }
            }
        }

        //System.out.println("valueOf(" + (char)c + "," + index + "," + (char)lastC + ")=" +  valueOfMem[index][lastCIndex][cIndex]);

        return valueOfMem[index][lastCIndex][cIndex];
    }

    public long solve() {
        long ret = 0;
        char last = 'a' - 1;
        for (char c : word) {

        }


        for (int i = 0, bound = word.length; i < bound; i++) {
            char c = word[i];
            if (!Character.isLowerCase(c) || c <= last) {
                return 0;
            }
            ret += valueOf(c, bound - i - 1, last);
            last = c;
        }

        return ret;
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
