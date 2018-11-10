package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dalt on 2017/12/22.
 */
public class Stars {
    static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\test\\poj\\Stars.in"));

        input = new BlockReader(System.in);
        StringBuilder builder = new StringBuilder();
        while (input.hasMore()) {
            Stars solution = new Stars();
            solution.init();
            builder.setLength(0);
            for (int cnt : solution.solve()) {
                builder.append(cnt).append('\n');
            }
            System.out.print(builder.toString());
        }
    }

    int n;
    int[][] locations;

    public void init() {
        n = input.nextInteger();
        locations = new int[n][2];
        for (int i = 0; i < n; i++) {
            locations[i][0] = input.nextInteger();
            locations[i][1] = input.nextInteger();
        }
    }

    public int[] solve() {
        int[] xAxises = new int[n];
        for (int i = 0; i < n; i++) {
            xAxises[i] = locations[i][0];
        }
        RankMap map = new RankMap(xAxises);
        BIT bit = new BIT(map.maxRank() + 1);
        int[] counters = new int[n];
        for (int i = 0; i < n; i++) {
            counters[bit.query(map.ranks[i] + 1)]++;
            bit.update(1, map.ranks[i] + 1);
        }

        return counters;
    }

    public class RankMap {
        int[] orders;
        int[] ranks;
        int n;

        public RankMap(final int[] values) {
            n = values.length;
            ranks = new int[n];
            orders = new int[n];
            {
                Integer[] wrappers = new Integer[n];
                for (int i = 0; i < n; i++) {
                    wrappers[i] = i;
                }
                Arrays.sort(wrappers, new Comparator<Integer>() {
                    public int compare(Integer o1, Integer o2) {
                        return values[o1] - values[o2];
                    }
                });
                for (int i = 0; i < n; i++) {
                    orders[i] = wrappers[i];
                }
            }
            {
                int cnt = 0;
                ranks[0] = cnt;
                for (int i = 1; i < n; i++) {
                    if (values[orders[i - 1]] != values[orders[i]]) {
                        cnt++;
                    }
                    ranks[orders[i]] = cnt;
                }
            }
        }

        public int minRank() {
            return 0;
        }

        public int maxRank() {
            return ranks[orders[n - 1]];
        }
    }

    public static class BIT {
        int[] data;

        public BIT(int end) {
            data = new int[end + 1];
        }

        public void update(int v, int index) {
            while (index < data.length) {
                data[index] += v;
                index += lowbit(index);
            }
        }

        public int query(int index) {
            int sum = 0;
            while (index > 0) {
                sum += data[index];
                index -= lowbit(index);
            }
            return sum;
        }

        public int lowbit(int i) {
            return i & -i;
        }
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
