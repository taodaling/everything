package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
public class AppleTree {
    static BlockReader input;
    int[] ids;
    int[] subTreeIds;
    List<Integer>[] edgeLists;
    int n;
    int idAllocator = 1;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\AppleTree.in"));

        input = new BlockReader(System.in);
        while (input.hasMore()) {
            AppleTree solution = new AppleTree();
            solution.init();
            System.out.print(solution.solve());
        }
    }

    public void init() {
        n = input.nextInteger();
        ids = new int[n + 1];
        subTreeIds = new int[n + 1];
        edgeLists = new List[n + 1];
        for (int i = 1; i <= n; i++) {
            edgeLists[i] = new ArrayList();
        }
        for (int i = 1; i < n; i++) {
            int u = input.nextInteger();
            int v = input.nextInteger();

            edgeLists[u].add(v);
            edgeLists[v].add(u);
        }
    }

    public String solve() {
        dfs(1, 0);
        BIT bit = new BIT(n);
        for (int i = 1; i <= n; i++) {
            bit.update(i, 1);
        }
        int m = input.nextInteger();
        StringBuilder builder = new StringBuilder();
        char[] cmd = new char[1];
        while (m-- > 0) {
            input.nextBlock(cmd, 0);
            switch (cmd[0]) {
                case 'C': {
                    int no = input.nextInteger();
                    int id = ids[no];
                    //Is there an apple on fork no
                    int appleNum = bit.query(id) - bit.query(id - 1);
                    if (appleNum > 0) {
                        appleNum = -appleNum;
                    } else {
                        appleNum = 1;
                    }
                    bit.update(id, appleNum);
                    break;
                }
                case 'Q': {
                    int no = input.nextInteger();
                    int id = ids[no];
                    int appleNum = bit.query(subTreeIds[no]) - bit.query(id - 1);
                    builder.append(appleNum).append('\n');
                    break;
                }
            }
        }
        return builder.toString();
    }

    public int dfs(int node, int father) {
        ids[node] = idAllocator++;
        subTreeIds[node] = ids[node];

        for (Integer neighbor : edgeLists[node]) {
            if (neighbor == father) {
                continue;
            }
            subTreeIds[node] = Math.max(subTreeIds[node], dfs(neighbor, node));
        }
        return subTreeIds[node];
    }

    public static class BIT {
        int[] data;


        public BIT(int cap) {
            data = new int[cap + 1];
        }

        public void update(int i, int val) {
            for (int j = i, bound = data.length; j < bound; j += lowbit(j)) {
                data[j] += val;
            }
        }

        public int query(int i) {
            int sum = 0;
            for (int j = i; j > 0; j -= lowbit(j)) {
                sum += data[j];
            }
            return sum;
        }

        public int lowbit(int x) {
            return x & -x;
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = 1; i < data.length; i++) {
                s += query(i) - query(i - 1);
                s += ",";
            }
            return s.substring(0, s.length() - 1);
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
