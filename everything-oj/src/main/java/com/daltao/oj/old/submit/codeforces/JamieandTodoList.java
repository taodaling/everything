package com.daltao.oj.old.submit.codeforces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Administrator on 2018/1/20.
 */
public class JamieandTodoList {
    public static final int Q_LIMIT = 100000;
    public static final int BT_SIZE = 5000000;
    public static final int ST_SIZE = 500000;
    public static BlockReader input;
    public static int[] bt_val = new int[BT_SIZE];
    public static int[][] bt_children = new int[BT_SIZE][2];
    public static int bt_cnt = 1;
    public static int[] st_val = new int[ST_SIZE];
    public static int[][] st_children = new int[ST_SIZE][26];
    public static int st_cnt = 1;

    public static int getStClone(int template) {
        int id = getStNode();
        st_val[id] = st_val[template];
        System.arraycopy(st_children[template], 0, st_children[id], 0, 26);
        return id;
    }

    public static int getBtClone(int template) {
        int id = getBtNode();
        bt_val[id] = bt_val[template];
        bt_children[id][0] = bt_children[template][0];
        bt_children[id][1] = bt_children[template][1];
        return id;
    }

    public static void main(String[] args) throws FileNotFoundException {


//        while (input.hasMore()) {
        solve();
//        }
    }

    public static void solve() {
        int q = input.nextInteger();

        int[] btHistories = new int[q + 1];
        int[] stHistories = new int[q + 1];
        btHistories[0] = getBtNode();
        stHistories[0] = getStNode();

        int[] prevVal = new int[1];
        char[] cmd = new char[64];

        for (int i = 1; i <= q; i++) {
            input.nextBlock(cmd, 0);
            btHistories[i] = btHistories[i - 1];
            stHistories[i] = stHistories[i - 1];
            if (cmd[0] == 's') {
                int len = input.nextBlock(cmd, 0);
                int val = input.nextInteger();
                stHistories[i] = StringTrie.add(stHistories[i], cmd, len, val, prevVal);
                int p = prevVal[0];
                //exists
                if (p != val) {
                    if (p > 0) {
                        btHistories[i] = BinaryTrie.update(btHistories[i], p, -1);
                    }
                    btHistories[i] = BinaryTrie.update(btHistories[i], val, 1);
                }
            } else if (cmd[0] == 'q') {
                int len = input.nextBlock(cmd, 0);
                int p = StringTrie.query(stHistories[i], cmd, len);//priorityTrie.query(cmd, 0, len);
                int output = -1;
                if (p > 0) {
                    output = BinaryTrie.query(btHistories[i], p);
                }
                System.out.println(output);
                //System.out.flush();
            } else if (cmd[0] == 'r') {
                int len = input.nextBlock(cmd, 0);
                stHistories[i] = StringTrie.add(stHistories[i], cmd, len, -1, prevVal);
                int p = prevVal[0];
                if (p > 0) {
                    btHistories[i] = BinaryTrie.update(btHistories[i], p, -1);
                }
            } else {
                int day = input.nextInteger();
                stHistories[i] = stHistories[i - day - 1];
                btHistories[i] = btHistories[i - day - 1];
            }
        }
    }

    public static int getBtNode() {
        return bt_cnt++;
    }

    public static int getStNode() {
        return st_cnt++;
    }


    public static class StringTrie {
        public static int add(int root, char[] data, int to, int v, int[] oldVal) {
            root = getStClone(root);
            int trace = root;
            for (int i = 0; i < to; i++) {
                int index = data[i] - 'a';
                if (st_children[trace][index] == 0) {
                    st_children[trace][index] = getStNode();
                } else {
                    st_children[trace][index] = getStClone(st_children[trace][index]);
                }
                trace = st_children[trace][index];
            }

            oldVal[0] = st_val[trace];
            st_val[trace] = v;
            return root;
        }

        public static int query(int root, char[] data, int to) {
            int trace = root;
            for (int i = 0; i < to; i++) {
                int index = data[i] - 'a';
                if (st_children[trace][index] == 0) {
                    return -1;
                }
                trace = st_children[trace][index];
            }
            return st_val[trace];
        }
    }


    public static class BinaryTrie {
        public static int update(int root, int n, int v) {
            root = getBtClone(root);
            int trace = root;
            for (int i = 29; i >= 0; i--) {
                int bit = (n & (1 << i)) >> i;
                bt_val[trace] += v;
                if (bt_children[trace][bit] == 0) {
                    bt_children[trace][bit] = getBtNode();
                } else {
                    bt_children[trace][bit] = getBtClone(bt_children[trace][bit]);
                }
                trace = bt_children[trace][bit];
            }

            bt_val[trace] += v;
            return root;
        }

        public static int query(int root, int n) {
            int trace = root;
            int prefixSum = 0;
            for (int i = 29; i >= 0; i--) {
                int bit = (n & (1 << i)) >> i;
                if (bit == 1 && bt_children[trace][0] != 0) {
                    prefixSum += bt_val[bt_children[trace][0]];
                }
                trace = bt_children[trace][bit];
            }
            return prefixSum;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 1 << 13);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (next <= 32) {
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
            while (next >= 'a' && next <= 'z') {
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
