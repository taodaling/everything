package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/12/26.
 */
public class PaintingABoard {
    static final int INF = (int) 1e8;
    static BlockReader input;
    int[][] rectangles;
    int rectNum;

    public static void main(String[] args) throws Exception {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\PaintingABoard.in"));

        input = new BlockReader(System.in);
        for (int i = 0, bound = input.nextInteger(); i < bound; i++) {
            PaintingABoard solution = new PaintingABoard();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void init() {
        rectNum = input.nextInteger();
        rectangles = new int[rectNum][5];
        for (int i = 0; i < rectNum; i++) {
            for (int j = 0; j < 5; j++) {
                rectangles[i][j] = input.nextInteger();
            }
        }
    }


    public int solve() {
        Arrays.sort(rectangles, new Comparator<int[]>() {
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        });

        int[] dp = new int[1 << rectNum];
        int[] rely = new int[rectNum];
        for (int i = 0; i < rectNum; i++) {
            for (int j = 0; j < rectNum; j++) {
                //rect[j].rb.y < rect[i].lt.y && rect[j].lt.x < rect[i].rb.x && rect[j].rb.x > rect[i].lt.x
                if (rectangles[j][0] < rectangles[i][0] &&
                        rectangles[j][1] < rectangles[i][3] &&
                        rectangles[j][3] > rectangles[i][1]) {
                    rely[i] |= (1 << j);
                }
            }
        }

        Arrays.fill(dp, INF);
        dp[0] = 0;
        for (int i = 0, bound = dp.length; i < bound; i++) {
            if (dp[i] == INF) {
                continue;
            }
            //Iterate over colors
            for (int c = 1; c <= 20; c++) {
                int newState = i;
                for (int j = 0; j < rectangles.length; j++) {
                    if (rectangles[j][4] != c || (rely[j] & newState) != rely[j]) {
                        continue;
                    }
                    newState |= 1 << j;
                }
                dp[newState] = Math.min(dp[newState], dp[i] + 1);
            }
        }

//        for (int i = 0; i < endDp.length; i++) {
//            if (endDp[i] != INF) {
//                System.out.println(Integer.toBinaryString(i) + ": " + endDp[i]);
//            }
//        }

        return dp[dp.length - 1];
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
