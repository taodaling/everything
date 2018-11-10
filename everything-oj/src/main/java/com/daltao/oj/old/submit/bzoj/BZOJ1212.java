package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by dalt on 2018/1/12.
 */
public class BZOJ1212 {

    public static BlockReader input;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.setIn(new FileInputStream("D:\\test\\bzoj\\BZOJ1212.in"));

        input = new BlockReader(System.in);

        BZOJ1212 solution = new BZOJ1212();
        //   solution.before();
        System.out.print(solution.solve());
    }

    public String solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        int limit = (1 << 20) + 1;
        char[] buf = new char[limit];
        ACNode root = new ACNode();
        root.fail = root.father = root;

        //Make prefix tree
        for (int i = 0; i < n; i++) {
            int len = input.nextBlock(buf, 0);
            ACNode trace = root;
            for (int j = 0; j < len; j++) {
                trace = trace.ensure(buf[j] - 'a');
            }
            trace.isEnd = true;
        }

        //Make fail
        LinkedList<ACNode> queue = new LinkedList();
        queue.addLast(root);
        while (!queue.isEmpty()) {
            ACNode head = queue.removeFirst();
            int index = head.index;
            ACNode trace;
            for (trace = head.father.fail; trace != root && trace.children[index] == null; trace = trace.fail) ;
            if (head.father != root && trace.children[index] != null) {
                trace = trace.children[index];
            }
            head.fail = trace;

            for (ACNode child : head.children) {
                if (child == null) {
                    continue;
                }
                queue.addLast(child);
            }
        }

        ACNode[] dp = new ACNode[limit];
        boolean[] match = new boolean[limit];
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < m; i++) {
            int len = input.nextBlock(buf, 1);
            ACNode trace = root;

            //Consume buf
            for (int j = 1; j <= len; j++) {
                char c = buf[j];
                int index = c - 'a';
                for (; trace != root && trace.children[index] == null; trace = trace.fail) ;
                if (trace.children[index] != null) {
                    trace = trace.children[index];
                }
                dp[j] = trace;
            }

            //dp
            match[0] = true;
            int maxMatch = 0;
            for (int j = 1; j <= len; j++) {
                match[j] = false;
                trace = dp[j];
                while (match[j] == false && trace != root) {
                    if (trace.isEnd) {
                        match[j] = match[j - trace.depth];
                    }
                    trace = trace.fail;
                }
                if (match[j]) {
                    maxMatch = j;
                }
            }

            result.append(maxMatch).append('\n');
        }

        return result.toString();
    }

    public static class ACNode {
        ACNode[] children = new ACNode[26];
        ACNode father;
        ACNode fail;
        int index;
        int depth;
        boolean isEnd;

        public ACNode ensure(int index) {
            if (children[index] == null) {
                ACNode node = new ACNode();
                node.father = this;
                node.index = index;
                node.depth = depth + 1;
                children[index] = node;
            }
            return children[index];
        }

        @Override
        public String toString() {
            return depth == 0 ? "" : father.toString() + (char)('a' + index);
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
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }
    }
}
