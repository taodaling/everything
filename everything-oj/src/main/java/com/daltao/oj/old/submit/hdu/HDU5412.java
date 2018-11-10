package com.daltao.oj.old.submit.hdu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by Administrator on 2018/1/13.
 */
public class HDU5412 {
    static final int N_LIMIT = 100000;
    static final int SNODE_CACHE = 2000001;
    public static BlockReader input;
    static int[] snodes_father = new int[SNODE_CACHE];
    static int[] snodes_left = new int[SNODE_CACHE];
    static int[] snodes_right = new int[SNODE_CACHE];
    static int[] snodes_size = new int[SNODE_CACHE];
    static int[] snodes_k = new int[SNODE_CACHE];
    static int snodes_cnt = 0;
    static int[] bit = new int[N_LIMIT + 1];
    static int lastRemoved = -1;
    static int[] currentVals = new int[N_LIMIT + 1];
    static StringBuilder result = new StringBuilder();

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\hdu\\HDU5412.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            HDU5412 solution = new HDU5412();
            solution.init();
            solution.solve();
        }
//        if (result.length() > 0) {
//            result.setLength(result.length() - 1);
//        }
    }

    public static void snode_init(int id) {
        snodes_father[id] = snodes_left[id] = snodes_right[id] = 0;
        snodes_size[id] = 1;
    }

    public static int snode_alloc(int k) {
        int id;
        if (lastRemoved >= 0) {
            id = lastRemoved;
            lastRemoved = -1;
        } else {
            id = snodes_cnt++;
        }

        snodes_k[id] = k;
        snode_init(id);
        return id;
    }

    public static void snode_free(int id) {
        lastRemoved = id;
    }

    public static void snode_update(int id) {
        snodes_size[id] = 1 + snodes_size[snodes_left[id]] + snodes_size[snodes_right[id]];
    }

    public static void snode_zig(int x) {
        int y = snodes_father[x];
        int z = snodes_father[y];
        int b = snodes_right[x];

        snode_asRight(x, y);
        snode_asLeft(y, b);
        snode_change(z, y, x);

        snode_update(y);
    }

    public static void snode_zag(int x) {
        int y = snodes_father[x];
        int z = snodes_father[y];
        int b = snodes_left[x];

        snode_asLeft(x, y);
        snode_asRight(y, b);
        snode_change(z, y, x);

        snode_update(y);
    }

    public static void snode_asLeft(int id, int x) {
        snodes_left[id] = x;
        snodes_father[x] = id;
    }

    public static void snode_asRight(int id, int x) {
        snodes_right[id] = x;
        snodes_father[x] = id;
    }

    public static void snode_change(int id, int y, int x) {
        if (snodes_left[id] == y) {
            snode_asLeft(id, x);
        } else {
            snode_asRight(id, x);
        }
    }

    public static void snode_splay(int x) {
        if (x == 0) {
            return;
        }
        int y, z;
        while ((y = snodes_father[x]) != 0) {
            if ((z = snodes_father[y]) == 0) {
                if (x == snodes_left[y]) {
                    snode_zig(x);
                } else {
                    snode_zag(x);
                }
            } else {
                if (x == snodes_left[y]) {
                    if (y == snodes_left[z]) {
                        snode_zig(y);
                        snode_zig(x);
                    } else {
                        snode_zig(x);
                        snode_zag(x);
                    }
                } else {
                    if (y == snodes_left[z]) {
                        snode_zag(x);
                        snode_zig(x);
                    } else {
                        snode_zag(y);
                        snode_zag(x);
                    }
                }
            }
        }

        snode_update(x);
    }

    public static int snode_deleteRoot(int root) {
        snodes_father[snodes_left[root]] = 0;
        snodes_father[snodes_right[root]] = 0;

        int x = 0;
        if (snodes_left[root] != 0) {
            for (x = snodes_left[root]; snodes_right[x] != 0; x = snodes_right[x]) ;
            snode_splay(x);
            snode_asRight(x, snodes_right[root]);
            snode_update(x);
        } else if (snodes_right[root] != 0) {
            for (x = snodes_right[root]; snodes_left[x] != 0; x = snodes_left[x]) ;
            snode_splay(x);
            snode_asLeft(x, snodes_left[root]);
            snode_update(x);
        }
        snode_free(root);
        return x;
    }

    public static int snode_delete(int root, int k) {
        int trace = root;
        while (snodes_k[trace] != k) {
            if (snodes_k[trace] > k) {
                trace = snodes_left[trace];
            } else {
                trace = snodes_right[trace];
            }
        }
        snode_splay(trace);
        return snode_deleteRoot(trace);
    }

    public static int snode_insert(int root, int k) {
        int trace = root;
        int father = 0;
        while (trace != 0) {
            father = trace;
            if (snodes_k[trace] >= k) {
                trace = snodes_left[trace];
            } else {
                trace = snodes_right[trace];
            }
        }

        int id = snode_alloc(k);
        if (snodes_k[father] >= k) {
            snode_asLeft(father, id);
        } else {
            snode_asRight(father, id);
        }
        snode_splay(id);
        return id;
    }

    public static void bit_inc(int i, int k) {
        for (; i <= N_LIMIT; i += i & -i) {
            bit[i] = snode_insert(bit[i], k);
        }
    }

    public static void bit_dec_inc(int i, int k1, int k2) {
        for (; i <= N_LIMIT; i += i & -i) {
            bit[i] = snode_insert(bit[i], k2);
            bit[i] = snode_delete(bit[i], k1);
        }
    }

    public static int bit_findLessThan(int hF, int hT, int k) {
        return bit_findLessThan(hT, k) - bit_findLessThan(hF - 1, k);
    }

    public static int bit_findLessThan(int h, int k) {
        int sum = 0;
        for (; h > 0; h -= h & -h) {
            bit[h] = snode_insert(bit[h], k);
            sum += snodes_size[snodes_left[bit[h]]];
            bit[h] = snode_deleteRoot(bit[h]);
        }
        return sum;
    }

    public static void bit_init(int cap) {
        Arrays.fill(bit, 1, cap, 0);
    }


    public void init() {
        snodes_cnt = 1;
        lastRemoved = -1;
        bit_init(bit.length);
    }

    public void solve() {
        int n = input.nextInteger();

        for (int i = 1; i <= n; i++) {
            int v = input.nextInteger();
            bit_inc(i, v);
            currentVals[i] = v;
        }
        int q = input.nextInteger();
        for (int i = 0; i < q; i++) {
            int type = input.nextInteger();
            if (type == 1) {
                int l = input.nextInteger();
                int v = input.nextInteger();

                bit_dec_inc(l, currentVals[l], v);
                currentVals[l] = v;
            } else {
                int l = input.nextInteger();
                int r = input.nextInteger();
                int k = input.nextInteger();
                int minVal = 0;
                int maxVal = 1000000001;
                while (minVal != maxVal) {
                    int c = (minVal + maxVal) >> 1;
                    int less = bit_findLessThan(l, r, c);
                    if (less >= k) {
                        maxVal = c;
                    } else {
                        minVal = c + 1;
                    }
                }
                System.out.println(maxVal - 1);
                //result.append(maxVal - 1).append('\n');
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
