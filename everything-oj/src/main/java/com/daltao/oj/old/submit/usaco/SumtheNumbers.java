package com.daltao.oj.old.submit.usaco;

import java.io.*;

/**
 * Created by dalt on 2017/12/8.
 */
public class SumtheNumbers {
    static {
        try {
            System.setIn(new FileInputStream("D:/test/usaco/SumtheNumbers.in"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            System.setOut(new PrintStream(new FileOutputStream("D:/test/usaco/SumtheNumbers.out")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        BlockReader input = new BlockReader(System.in);
        System.out.println(BlockParser.parseInt(input.nextBlock()) + BlockParser.parseInt(input.nextBlock()));
    }

    public static class BlockParser {
        public static int parseInt(char[] data, int offset, int length) {
            int result = 0;
            int bound = offset + length;
            boolean rev = false;
            if (!Character.isDigit(data[offset])) {
                rev = data[offset++] == '-';
            }
            for (int i = offset; i < bound; i++) {
                result = (result << 3) + (result << 1) + data[i] - '0';
            }
            return rev ? -result : result;
        }

        public static int parseInt(String s) {
            return parseInt(s.toCharArray(), 0, s.length());
        }
    }

    public static class BlockReader {
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        static final int EOF = -1;

        public void skipBlank() throws IOException {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        StringBuilder builder = new StringBuilder();

        public String nextBlock() throws IOException {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public int nextBlock(char[] data, int offset) throws IOException {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() throws IOException {
            skipBlank();
            return next != EOF;
        }

        public BlockReader(InputStream is) {
            this(is, 1024);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            try {
                next = nextByte();
            } catch (IOException e) {
            }
        }

        public int nextByte() throws IOException {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                dSize = is.read(dBuf);
            }
            return dBuf[dPos++];
        }
    }
}
