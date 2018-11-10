package com.daltao.oj.old.submit.bzoj;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by F on 2017/8/19.
 */
public class Knight{
    public static void main(String[] args) throws Exception {
        //AcmInputReader input = new AcmInputReader(new FileInputStream("D:/input.txt"));
        AcmInputReader input = new AcmInputReader(System.in);
        //Input
        int n = input.nextInteger();
        Node[] knights = new Node[n];
        for (int i = 0; i < n; i++) {
            knights[i] = new Node(i);
        }
        for (int i = 0; i < n; i++) {
            int power = input.nextInteger();
            int hatedId = input.nextInteger() - 1;
            knights[i].setWeight(power);
            knights[i].getEdges().add(knights[hatedId]);
            knights[hatedId].getEdges().add(knights[i]);
        }

        List<ConnectedGraph> graphs = new LinkedList();
        //寻找连通图
        {
            int statusTag = 1;
            Node[] formers = new Node[n];
            for (int i = 0; i < n; i++) {
                if (knights[i].status == statusTag) {
                    continue;
                }
                Queue<Node> queue = new LinkedList();
                queue.add(knights[i]);
                ConnectedGraph graph = new ConnectedGraph();
                while (!queue.isEmpty()) {
                    Node front = queue.poll();
                    Node former = formers[front.id];
                    if (front.status == statusTag) {
                        graph.loopNode2 = front;
                        graph.loopNode1 = former;
                        graph.isLoop = true;
                        continue;
                    }
                    front.status = statusTag;
                    graph.nodes.add(front);
                    for (Node node : front.getEdges()) {
                        if (node == former || node == front) {
                            continue;
                        }
                        formers[node.id] = front;
                        queue.add(node);
                    }
                }
                graphs.add(graph);
            }
        }

        long result = 0;
        long[][] cache = new long[n][2];
        for (ConnectedGraph graph : graphs) {
            result += solutionForGraph(cache, graph);
        }

        System.out.println(result);
        //input.close();
    }

    private static long solutionForGraph(long[][] cache, ConnectedGraph graph) {
        if (graph.isLoop) {
            //切开环
            graph.loopNode1.getEdges().remove(graph.loopNode2);
            graph.loopNode2.getEdges().remove(graph.loopNode1);
            long best = Math.max(ifContain(cache, graph.loopNode1, null, graph.loopNode2),
                    ifNotContain(cache, graph.loopNode1, null, graph.loopNode2));

            //清除cache
            cache = new long[cache.length][cache[0].length];
            return Math.max(best,
                    Math.max(ifContain(cache, graph.loopNode2, null, graph.loopNode1),
                            ifNotContain(cache, graph.loopNode2, null, graph.loopNode1))
            );
        } else {
            return Math.max(ifContain(cache, graph.nodes.get(0), null, null),
                    ifNotContain(cache, graph.nodes.get(0), null, null));
        }
    }

    private static long ifContain(long[][] cache, Node node, Node parent, Node filter) {
        if (cache[node.id][1] > 0) {
            return cache[node.id][1];
        }
        long best = 0;
        for (Node next : node.getEdges()) {
            if (next == parent) {
                continue;
            }
            best = Math.max(ifNotContain(cache, next, node, filter), best);
        }
        cache[node.id][1] = best + node.getWeight();
        return cache[node.id][1];
    }

    private static long ifNotContain(long[][] cache, Node node, Node parent, Node filter) {
        if (cache[node.id][0] > 0) {
            return cache[node.id][0];
        }
        long best = 0;
        for (Node next : node.getEdges()) {
            if (next == parent) {
                continue;
            }
            best = Math.max(ifNotContain(cache, next, node, filter), best);
            if(next != filter) {
                best = Math.max(ifContain(cache, next, node, filter), best);
            }
        }
        cache[node.id][0] = best;
        return cache[node.id][0];
    }

    private static class ConnectedGraph {
        public List<Node> nodes = new LinkedList();
        public Node loopNode1;
        public Node loopNode2;
        public boolean isLoop;
        public Node src;
        public static int total = 0;
        public final int id = total++;

    }

    private static class Node {
        private List<Node> edges = new LinkedList();
        private int weight;
        private int status;
        private final int id;

        public Node(int id) {
            this.id = id;
        }

        public List<Node> getEdges() {
            return edges;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }


        public String toString() {
            return "" + id + " : " + weight;
        }
    }

    /**
     * @author dalt
     * @see AutoCloseable
     * @since java1.7
     */
    private static class AcmInputReader implements Closeable {
        private PushbackInputStream in;

        /**
         * 创建读取器
         *
         * @param input 输入流
         */
        public AcmInputReader(InputStream input) {
            in = new PushbackInputStream(new BufferedInputStream(input));
        }


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
         * 如果后续k个字节均为b，则跳过k个字节。这里{@literal k<=times}
         *
         * @param b     被跳过的字节值
         * @param times 跳过次数，-1表示无穷
         * @throws IOException if 输入流读取错误
         */
        public void skipByte(int b, int times) throws IOException {
            int c;
            while ((c = nextByte()) == b && times != 0) {
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
}