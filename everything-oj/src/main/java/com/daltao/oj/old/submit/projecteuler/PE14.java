package com.daltao.oj.old.submit.projecteuler;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by dalt on 2018/4/2.
 */
public class PE14 {
    public static final int MOD = (int) (1e9 + 7);
    public static PE13.BlockReader input;
    public static PrintStream output;

    public static void main(String[] args) throws FileNotFoundException {

        new Thread(null, new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                solve();

                output.flush();
            }
        }, "", 1 << 27).start();

    }

    public static void init() throws FileNotFoundException {
        input = new PE13.BlockReader(new ByteArrayInputStream((
                ""
        ).getBytes(Charset.forName("ascii"))));
        output = System.out;
    }

    public static void solve() {
        Deque<Node> deque = new ArrayDeque<>();
        for (int i = 2; i < 1000000; i++) {
            Node newNode = new Node();
            newNode.current = newNode.start = i;
            deque.addLast(newNode);
        }

        while (deque.size() > 1) {
            Node head = deque.removeFirst();
            head.current = next(head.current);
            if (head.current != 1) {
                deque.addLast(head);
            }
        }

        output.println(deque.removeFirst().start);
    }

    public static long next(long i) {
        return (i & 1L) == 0L ? (i >> 1) : (i + i + i + 1);
    }

    public static class Node {
        long start;
        long current;
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

        public long nextLong() {
            skipBlank();
            long ret = 0;
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
