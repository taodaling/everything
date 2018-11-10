package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dalt on 2017/12/22.
 */
public class Mobilephones {
    static BlockReader input;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\test\\poj\\Mobilephones.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            Mobilephones solution = new Mobilephones();
            System.out.print(solution.solve());
        }
    }

    public String solve() {
        input.nextInteger();
        int size = input.nextInteger();
        MITx2 tree = new MITx2(size, size);

        StringBuilder result = new StringBuilder();
        while (true) {
            int cmd = input.nextInteger();
            switch (cmd) {
                case 1: {
                    int x = input.nextInteger() + 1;
                    int y = input.nextInteger() + 1;
                    int n = input.nextInteger();
                    tree.update(x, y, n);
                    break;
                }
                case 2: {
                    int l = input.nextInteger() + 1;
                    int b = input.nextInteger() + 1;
                    int r = input.nextInteger() + 1;
                    int t = input.nextInteger() + 1;
//                    System.out.println(tree.query(r, t));
//                    System.out.println(tree.query(l, b));
//                    System.out.println(tree.query(l, t));
//                    System.out.println(tree.query(r, b));
                    int val = tree.query(r, t) + tree.query(l - 1, b - 1) - tree.query(l - 1, t) - tree.query(r, b - 1);
                    result.append(val).append('\n');
                    break;
                }
                case 3: {
                    return result.toString();
                }
            }
        }
    }

    public static class MITx2 {
        int[][] data;

        public MITx2(int rowNum, int colNum) {
            data = new int[rowNum + 1][colNum + 1];
        }

        public void update(int x, int y, int val) {
            int xc = x;
            int bound = data.length;
            while (y < bound) {
                x = xc;
                while (x < bound) {
                    data[y][x] += val;
                    x += lowbit(x);
                }
                y += lowbit(y);
            }
        }

        public int query(int x, int y) {
            if (x <= 0 || y <= 0) {
                return 0;
            }

            int sum = 0;
            int xc = x;
            while (y > 0) {
                x = xc;
                while (x > 0) {
                    sum += data[y][x];
                    x -= lowbit(x);
                }
                y -= lowbit(y);
            }
            return sum;
        }

        public int lowbit(int x) {
            return x & -x;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 1024);
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
