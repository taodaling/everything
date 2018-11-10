package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dalt on 2018/1/17.
 */
public class BZOJ2038 {
    public static BlockReader input;
    int[] data;
    int[] color;
    int[] choose;
    int sum = 0;
    int[][] reqs;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //  System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ2038.in"));

        input = new BlockReader(System.in);

        BZOJ2038 solution = new BZOJ2038();
        //   solution.before();
        System.out.print(solution.solve());
    }

    public static int gcd(int a, int b) {
        return a >= b ? gcd0(a, b) : gcd0(b, a);
    }

    public static int gcd0(int a, int b) {
        return b == 0 ? a : gcd0(b, a % b);
    }

    public String solve() {
        StringBuilder result = new StringBuilder();

        int n = input.nextInteger();
        int m = input.nextInteger();


        data = new int[n + 1];
        color = new int[n + 1];
        choose = new int[n + 1];
        reqs = new int[m][4];

        for (int i = 1; i <= n; i++) {
            data[i] = input.nextInteger();
        }

        int blockSize = (int) Math.sqrt(n);
        int blockNum = (n + blockSize - 1) / blockSize;
        for (int i = 0; i < m; i++) {
            reqs[i][0] = input.nextInteger();
            reqs[i][1] = input.nextInteger();
            reqs[i][2] = reqs[i][0] / blockSize;
        }

        int[][] orderedReqs = reqs.clone();
        Arrays.sort(orderedReqs, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[2] != o2[2] ? o1[2] - o2[2] : o1[1] - o2[1];
            }
        });

        int left = 1;
        int right = 1;
        inc(data[1]);
        for (int i = 0; i < m; i++) {
            int[] req = orderedReqs[i];
            while (req[1] > right) {
                right++;
                inc(data[right]);
            }
            while (req[0] < left) {
                left--;
                inc(data[left]);
            }
            while (req[1] < right) {
                dec(data[right]);
                right--;
            }
            while (req[0] > left) {
                dec(data[left]);
                left++;
            }

            req[3] = sum;
        }

        for (int i = 0; i < m; i++) {


            int[] req = reqs[i];

            if (req[3] == 0) {
                result.append("0/1\n");
            } else {
                int intervalLen = req[1] - req[0] + 1;
                int totalPossibles = (int)((long) (intervalLen - 1) * intervalLen / 2);
                int gcd = gcd0(totalPossibles, req[3]);
                result.append(req[3] / gcd).append('/').append(totalPossibles / gcd).append('\n');
            }
        }

        return result.toString();
    }

    public void inc(int i) {
        if (color[i] < 1) {
        } else if (color[i] == 1) {
            choose[i] = 1;
            sum += 1;
        } else {
            int newChoose = (int) ((long) choose[i] * (color[i] + 1) / (color[i] - 1));
            sum += newChoose - choose[i];
            choose[i] = newChoose;
        }

        color[i] += 1;
    }

    public void dec(int i) {
        if (color[i] < 2) {
        } else if (color[i] == 2) {
            choose[i] = 0;
            sum -= 1;
        } else {
            int newChoose = (int) ((long) choose[i] * (color[i] - 2) / color[i]);
            sum += newChoose - choose[i];
            choose[i] = newChoose;
        }

        color[i] -= 1;
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 4096);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
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

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
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
    }
}
