package com.daltao.oj.old.submit.codeforces;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by Administrator on 2017/11/26.
 */
public class RalphandMushrooms implements Runnable {
    static long[] kCache = new long[20000];
    private static boolean DEBUG = false;

    static {
        for (int i = 0; i < 20000; i++) {
            long longi = i;
            kCache[i] = (longi + 2) * (longi + 1) * longi / 6;
        }
    }

    List<Node> nodeList = new ArrayList<>();
    int n;
    int m;
    int s;
    Deque<Node> deque;
    int idAllocator;

    public static void main(String[] args) throws IOException {
        RalphandMushrooms solution = new RalphandMushrooms();
        solution.run();
        //new Thread(null, solution, "cf", 1 << 27).start();
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
        }
        System.out.println(solve());
    }

    public void init() throws IOException {
        //  long btime = System.currentTimeMillis();

        JoltyScanner reader = new JoltyScanner(System.in);

        n = reader.nextInteger();
        m = reader.nextInteger();

        for (int i = 0; i < n; i++) {
            Node node = new Node();
            node.id = i;
            nodeList.add(node);
        }

        for (int i = 0; i < m; i++) {
            int x = reader.nextInteger() - 1;
            int y = reader.nextInteger() - 1;
            int w = reader.nextInteger();

            Node nodex = nodeList.get(x);
            Node nodey = nodeList.get(y);

            Edge edge = new Edge();
            edge.mushroomNum = w;
            edge.src = nodex;
            edge.dst = nodey;

            nodex.post.add(edge);
            nodey.pre.add(edge);
        }

        s = reader.nextInteger() - 1;

//        long etime = System.currentTimeMillis();
//        if (DEBUG)
//            System.out.println("before:" + (etime - btime));
    }


    public void tarjan(Node cur) {
        int id = cur.id;
        cur.low = cur.allocatedId = ++idAllocator;
        cur.inStack = true;
        deque.push(cur);

        for (Edge edge : cur.post) {
            Node dst = edge.dst;
            if (dst.allocatedId == 0) {
                tarjan(dst);
                cur.low = Math.min(cur.low, dst.low);
            } else if(dst.inStack){
                cur.low = Math.min(cur.low, dst.low);
            }
        }

        if (cur.allocatedId == cur.low) {
            combine(cur, deque);
        }
    }

    public void combine(Node cur, Deque<Node> deque) {
        //long btime = System.currentTimeMillis();
        while (true) {
            Node top = deque.pop();
            top.inStack = false;

            if (top == cur) {
                break;
            }
            nodeList.set(top.id, cur);
            for (Edge post : top.post) {
                post.src = cur;
            }
            cur.post.addAll(top.post);
            for (Edge pre : top.pre) {
                pre.dst = cur;
            }
            cur.pre.addAll(top.pre);
        }
        // long etime = System.currentTimeMillis();
//        if (DEBUG)
//            System.out.println("combine:" + (etime - btime));
    }

    public long solve() {
        //   long btime = System.currentTimeMillis();
        idAllocator = 0;
        deque = new ArrayDeque<>();
        tarjan(nodeList.get(s));

        // System.out.println("dfs:" + dfsTime);
//        long etime = System.currentTimeMillis();
//        if (DEBUG)
//            System.out.println("tarjan:" + (etime - btime));

        //  btime = System.currentTimeMillis();
        long result = dp(nodeList.get(s), new long[n], new boolean[n]);
//        etime = System.currentTimeMillis();
//        if (DEBUG)
//            System.out.println("dp:" + (etime - btime));
        return result;
    }

    public long dp(Node node, long[] cacheValue, boolean[] visited) {
        if (visited[node.id]) {
            return cacheValue[node.id];
        }

        visited[node.id] = true;

        long internal = 0;
        long external = 0;

        for (Edge edge : node.post) {
            if (edge.dst == node) {
                internal += valueOf(edge);
            } else {
                external = Math.max(external, dp(edge.dst, cacheValue, visited) + edge.mushroomNum);
            }
        }

        cacheValue[node.id] = external + internal;
        return cacheValue[node.id];
    }

    public long valueOf(Edge edge) {
        int n = edge.mushroomNum;
        int n2 = n << 1;
        int left = 1;
        int right = 20000;
        while (left < right) {
            int half = (left + right) >> 1;
            int half2 = (1 + half) * half;
            if (half2 < n2) {
                left = half + 1;
            } else if (half2 > n2) {
                right = half;
            } else {
                left = half + 1;
                break;
            }
        }

        /*int sqrt = (int) Math.sqrt(n2);
        if ((sqrt + 1) * sqrt > n2) {
            sqrt -= 1;
        }

        long k = sqrt;
*/
        int k = left - 1;

        return (k + 1L) * n - kCache[k];
    }

    private static class Node {
        public int id;
        public boolean inStack;
        public int allocatedId;
        public int low;
        public List<Edge> post = new ArrayList<>();
        public List<Edge> pre = new ArrayList<>();
    }

    private static class Edge {
        Node src;
        Node dst;
        int mushroomNum;
    }

    /**
     * @author dalt
     * @see AutoCloseable
     * @since java1.7
     */
    private static class AcmInputReader implements AutoCloseable {
        private PushbackInputStream in;

        /**
         * 创建读取器
         *
         * @param input 输入流
         */
        public AcmInputReader(InputStream input) {
            in = new PushbackInputStream(new BufferedInputStream(input));
        }

        @Override
        public void close() throws IOException {
            in.close();
        }

        private int nextByte() throws IOException {
            return in.read() & 0xff;
        }

        /**
         * 如果下一个字节为b，则跳过该字节
         *
         * @param b 被跳过的字节值
         * @throws IOException if 输入流读取错误
         */
        public void skipByte(int b) throws IOException {
            int c;
            if ((c = nextByte()) != b) {
                in.unread(c);
            }
        }

        /**
         * 如果后续k个字节均为b，则跳过k个字节。这里{@literal k<times}
         *
         * @param b     被跳过的字节值
         * @param times 跳过次数，-1表示无穷
         * @throws IOException if 输入流读取错误
         */
        public void skipByte(int b, int times) throws IOException {
            int c;
            while ((c = nextByte()) == b && times > 0) {
                times--;
            }
            if (c != b) {
                in.unread(c);
            }
        }

        /**
         * 类似于{@link #skipByte(int, int)}, 但是会跳过中间出现的空白字符。
         *
         * @param b     被跳过的字节值
         * @param times 跳过次数，-1表示无穷
         * @throws IOException if 输入流读取错误
         */
        public void skipBlankAndByte(int b, int times) throws IOException {
            int c;
            skipBlank();
            while ((c = nextByte()) == b && times > 0) {
                times--;
                skipBlank();
            }
            if (c != b) {
                in.unread(c);
            }
        }

        /**
         * 读取下一块不含空白字符的字符块
         *
         * @return 下一块不含空白字符的字符块
         * @throws IOException if 输入流读取错误
         */
        public String nextBlock() throws IOException {
            skipBlank();
            StringBuilder sb = new StringBuilder();
            int c = nextByte();
            while (AsciiMarksLazyHolder.asciiMarks[c = nextByte()] != AsciiMarksLazyHolder.BLANK_MARK) {
                sb.append((char) c);
            }
            in.unread(c);
            return sb.toString();
        }

        /**
         * 跳过输入流中后续空白字符
         *
         * @throws IOException if 输入流读取错误
         */
        private void skipBlank() throws IOException {
            int c;
            while ((c = nextByte()) <= 32) ;
            in.unread(c);
        }

        /**
         * 读取下一个整数（可正可负），这里没有对溢出做判断
         *
         * @return 下一个整数值
         * @throws IOException if 输入流读取错误
         */
        public int nextInteger() throws IOException {
            skipBlank();
            int value = 0;
            boolean positive = true;
            int c = nextByte();
            if (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.SIGN_MARK) {
                positive = c == '+';
            } else {
                value = '0' - c;
            }
            c = nextByte();
            while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                value = (value << 3) + (value << 1) + '0' - c;
                c = nextByte();
            }

            in.unread(c);
            return positive ? -value : value;
        }

        /**
         * 判断是否到了文件结尾
         *
         * @return true如果到了文件结尾，否则false
         * @throws IOException if 输入流读取错误
         */
        public boolean isMeetEOF() throws IOException {
            int c = nextByte();
            if (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.EOF) {
                return true;
            }
            in.unread(c);
            return false;
        }

        /**
         * 判断是否在跳过空白字符后抵达文件结尾
         *
         * @return true如果到了文件结尾，否则false
         * @throws IOException if 输入流读取错误
         */
        public boolean isMeetBlankAndEOF() throws IOException {
            skipBlank();
            int c = nextByte();
            if (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.EOF) {
                return true;
            }
            in.unread(c);
            return false;
        }

        /**
         * 获取下一个用英文字母组成的单词
         *
         * @return 下一个用英文字母组成的单词
         */
        public String nextWord() throws IOException {
            StringBuilder sb = new StringBuilder(16);
            skipBlank();
            int c;
            while ((AsciiMarksLazyHolder.asciiMarks[(c = nextByte())] & AsciiMarksLazyHolder.LETTER_MARK) != 0) {
                sb.append((char) c);
            }
            in.unread(c);
            return sb.toString();
        }

        /**
         * 读取下一个长整数（可正可负），这里没有对溢出做判断
         *
         * @return 下一个长整数值
         * @throws IOException if 输入流读取错误
         */
        public long nextLong() throws IOException {
            skipBlank();
            long value = 0;
            boolean positive = true;
            int c = nextByte();
            if (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.SIGN_MARK) {
                positive = c == '+';
            } else {
                value = '0' - c;
            }
            c = nextByte();
            while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                value = (value << 3) + (value << 1) + '0' - c;
                c = nextByte();
            }
            in.unread(c);
            return positive ? -value : value;
        }

        /**
         * 读取下一个浮点数（可正可负），浮点数是近似值
         *
         * @return 下一个浮点数值
         * @throws IOException if 输入流读取错误
         */
        public float nextFloat() throws IOException {
            return (float) nextDouble();
        }

        /**
         * 读取下一个浮点数（可正可负），浮点数是近似值
         *
         * @return 下一个浮点数值
         * @throws IOException if 输入流读取错误
         */
        public double nextDouble() throws IOException {
            skipBlank();
            double value = 0;
            boolean positive = true;
            int c = nextByte();
            if (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.SIGN_MARK) {
                positive = c == '+';
            } else {
                value = c - '0';
            }
            c = nextByte();
            while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                value = value * 10.0 + c - '0';
                c = nextByte();
            }

            if (c == '.') {
                double littlePart = 0;
                double base = 1;
                c = nextByte();
                while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                    littlePart = littlePart * 10.0 + c - '0';
                    base *= 10.0;
                    c = nextByte();
                }
                value += littlePart / base;
            }
            in.unread(c);
            return positive ? value : -value;
        }

        /**
         * 读取下一个高精度数值
         *
         * @return 下一个高精度数值
         * @throws IOException if 输入流读取错误
         */
        public BigDecimal nextDecimal() throws IOException {
            skipBlank();
            StringBuilder sb = new StringBuilder();
            sb.append((char) nextByte());
            int c = nextByte();
            while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                sb.append((char) c);
                c = nextByte();
            }
            if (c == '.') {
                sb.append('.');
                c = nextByte();
                while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                    sb.append((char) c);
                    c = nextByte();
                }
            }
            in.unread(c);
            return new BigDecimal(sb.toString());
        }

        /**
         * 读取下一个大整数数值
         *
         * @return 下一个大整数数值
         * @throws IOException if 输入流读取错误
         */
        public BigInteger nextBigInteger() throws IOException {
            skipBlank();
            StringBuilder sb = new StringBuilder();
            sb.append((char) nextByte());
            int c = nextByte();
            while (AsciiMarksLazyHolder.asciiMarks[c] == AsciiMarksLazyHolder.NUMERAL_MARK) {
                sb.append((char) c);
                c = nextByte();
            }
            in.unread(c);
            return new BigInteger(sb.toString());
        }

        private static class AsciiMarksLazyHolder {
            public static final byte BLANK_MARK = 1;
            public static final byte SIGN_MARK = 1 << 1;
            public static final byte NUMERAL_MARK = 1 << 2;
            public static final byte UPPERCASE_LETTER_MARK = 1 << 3;
            public static final byte LOWERCASE_LETTER_MARK = 1 << 4;
            public static final byte LETTER_MARK = UPPERCASE_LETTER_MARK | LOWERCASE_LETTER_MARK;
            public static final byte EOF = 1 << 5;
            public static byte[] asciiMarks = new byte[256];

            static {
                for (int i = 0; i <= 32; i++) {
                    asciiMarks[i] = BLANK_MARK;
                }
                asciiMarks['+'] = SIGN_MARK;
                asciiMarks['-'] = SIGN_MARK;
                for (int i = '0'; i <= '9'; i++) {
                    asciiMarks[i] = NUMERAL_MARK;
                }
                for (int i = 'a'; i <= 'z'; i++) {
                    asciiMarks[i] = LOWERCASE_LETTER_MARK;
                }
                for (int i = 'A'; i <= 'Z'; i++) {
                    asciiMarks[i] = UPPERCASE_LETTER_MARK;
                }
                asciiMarks[0xff] = EOF;
            }
        }
    }

    private static class JoltyScanner {
        public static final int BUFFER_SIZE = 1 << 16;
        public static final char NULL_CHAR = (char) -1;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bufferIdx = 0, size = 0;
        char c = NULL_CHAR;
        BufferedInputStream in;

        public JoltyScanner(InputStream in) {
            this.in = new BufferedInputStream(in, BUFFER_SIZE);
            c = nextChar();
        }

        public int nextInteger() {
            while (c < '0' || c > '9')
                c = nextChar();
            int res = 0;
            while (c >= '0' && c <= '9') {
                res = (res << 3) + (res << 1) + c - '0';
                c = nextChar();
            }
            return res;
        }

        public char nextChar() {
            while (bufferIdx == size) {
                try {
                    size = in.read(buffer);
                } catch (Exception e) {
                    return NULL_CHAR;
                }
                bufferIdx = 0;
            }
            return (char) buffer[bufferIdx++];
        }
    }
}