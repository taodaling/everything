package com.daltao.oj.old.submit.bzoj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/11.
 */
public class BZOJ3172 {

    final static char NIL = 'z' + 1;
    public static BlockReader input;
    char[][] data;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\bzoj\\BZOJ3172.in"));
        input = new BlockReader(System.in);
        BZOJ3172 solution = new BZOJ3172();
        System.out.print(solution.solve());
    }

    public String solve() {
        int n = input.nextInteger();
        data = new char[n][];
        char[] buf = new char[1000000];
        for (int i = 0; i < n; i++) {
            int len = input.nextBlock(buf, 0);
            data[i] = new char[len];
            System.arraycopy(buf, 0, data[i], 0, len);
        }

        List<ACNode> endList = new ArrayList(n);
        //Make actree
        ACNode root = new ACNode();
        {
            ACNode trace;
            for (char[] s : data) {
                trace = root;
                for (char c : s) {
                    trace = trace.ensure(c - 'a');
                }
                endList.add(trace);
            }
        }

        //Set the fail path
        LinkedList<ACNode> stack = new LinkedList();
        {
            LinkedList<ACNode> queue = new LinkedList();
            root.failPath = root;
            root.father = root;
            queue.addLast(root);
            stack.addLast(root);
            while (!queue.isEmpty()) {
                ACNode head = queue.removeFirst();
                ACNode trace = head.father.failPath;
                int index = head.index;
                for (; trace != root && trace.children[index] == null; trace = trace.failPath) ;
                if (head.father != root && trace.children[index] != null) {
                    head.failPath = trace.children[index];
                }
                for (ACNode child : head.children) {
                    if (child == null) {
                        continue;
                    }
                    child.failPath = root;
                    queue.addLast(child);
                    stack.addLast(child);
                }
            }
        }

        {
            ACNode trace = root;
            for (char[] s : data) {
                for (char c : s) {
                    int index = c - 'a';
                    for (; trace != root && trace.children[index] == null; trace = trace.failPath) ;
                    if (trace.children[index] != null) {
                        trace = trace.children[index];
                        trace.visitedTimes++;
                    }
                }
                trace = root;
            }
        }

        {
            //BFS and dp
            while (!stack.isEmpty()) {
                ACNode top = stack.removeLast();
                top.failPath.visitedTimes += top.visitedTimes;
            }
        }

        StringBuilder result = new StringBuilder();
        for (ACNode end : endList) {
            result.append(end.visitedTimes).append('\n');
        }
        return result.toString();
    }


    public static class ACNode {
        ACNode[] children = new ACNode[27];
        ACNode failPath;
        ACNode father;
        int index;
        int visitedTimes;

        public ACNode ensure(int i) {
            if (children[i] == null) {
                children[i] = new ACNode();
                children[i].father = this;
                children[i].index = i;
            }
            return children[i];
        }

        @Override
        public String toString() {
            if (father == this) {
                return "";
            }
            return father.toString() + ((char) ('a' + index));
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
