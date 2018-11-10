package com.daltao.oj.old.submit.codeforces;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/29.
 */
public class CF919F {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int orderAllocator = 1;

    static int[][] dp = new int[getStateId(0, 0, 0, 8) + 1][getStateId(0, 0, 0, 8) + 1];
    static boolean[][] visited = new boolean[dp.length][dp[0].length];
    static boolean[][] onbuilt = new boolean[dp.length][dp[0].length];

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\codeforces\\CF919F.in"));
        }
        input = new BlockReader(System.in);

        Arrays.fill(dp[0], 1);
        Arrays.fill(visited[0], true);

        int t = input.nextInteger();
        for (int i = 0; i < t; i++) {
            int f = input.nextInteger();
            int[] c1 = new int[5];
            int[] c2 = new int[5];


            for (int j = 0; j < 8; j++) {
                c1[input.nextInteger()]++;
            }
            for (int j = 0; j < 8; j++) {
                c2[input.nextInteger()]++;
            }

            int aliceWin;
            if (f == 0) {
                aliceWin = cache(getStateId(c1[1], c1[2], c1[3], c1[4]), getStateId(c2[1], c2[2], c2[3], c2[4]));
            } else {
                aliceWin = cache(getStateId(c2[1], c2[2], c2[3], c2[4]), getStateId(c1[1], c1[2], c1[3], c1[4]));
                if (aliceWin >= 0) {
                    aliceWin = aliceWin == 0 ? 1 : 0;
                }
            }

            System.out.println(aliceWin != -1 ? (aliceWin > 0 ? "Alice" : "Bob") : "Deal");
        }
    }

    public static int cache(int s1, int s2) {
        if (!visited[s1][s2]) {
            dp[s1][s2] = -1;
            visited[s1][s2] = true;
            onbuilt[s1][s2] = true;
            int[] data = new int[5];
            int[] data2 = new int[5];

            for (int i = 1, t1 = s1, t2 = s2; i <= 4; i++) {
                data[i] = t1 % 9;
                data2[i] = t2 % 9;
                t1 /= 9;
                t2 /= 9;
            }


            int deal = 0;
            for (int i = 1; i <= 4; i++) {
                if (data[i] == 0) {
                    continue;
                }
                for (int j = 1; j <= 4; j++) {
                    if (data2[j] == 0) {
                        continue;
                    }

                    data[i]--;
                    int newCard = (i + j) % 5;
                    data[newCard]++;
                    int newState = getStateId(data[1], data[2], data[3], data[4]);
                    if (onbuilt[s2][newState]) {
                        deal++;
                    }
                    if (cache(s2, newState) == 0) {
                        dp[s1][s2] = 1;
                        return 1;
                    }
                    data[newCard]--;
                    data[i]++;
                }


            }
            if (deal == 0) {
                dp[s1][s2] = 0;
            }
            onbuilt[s1][s2] = false;
        }


        return dp[s1][s2];
    }

    public static int getStateId(int n1, int n2, int n3, int n4) {
        return (n4 * 729) + (n3 * 81) + (n2 * 9) + n1;
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
