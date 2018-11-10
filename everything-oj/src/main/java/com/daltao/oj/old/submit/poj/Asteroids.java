package com.daltao.oj.old.submit.poj;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */
public class Asteroids {
    private static final int INF = 100000000;
    private static AcmInputReader input = new AcmInputReader(System.in);
    private Node sink;
    private Node target;
    private int[] distCnt;
    private int size;

    public static void main(String[] args) throws IOException {
        while (!input.isMeetBlankAndEOF()) {
            Asteroids solution = new Asteroids();
            solution.init();
            System.out.println(solution.solve());
        }
    }

    public void buildSingleEdge(Node src, Node dst) {
        PositiveEdge pEdgeV = new PositiveEdge(1, src, dst);
        NegativeEdge nEdgeV = new NegativeEdge(pEdgeV);
        src.edgeList.add(pEdgeV);
        dst.edgeList.add(nEdgeV);
    }

    public void init() throws IOException {
        sink = new Node();
        target = new Node();
        size = input.nextInteger();


        Node[] vertical = new Node[size];
        Node[] horizontal = new Node[size];
        for (int i = 0; i < size; i++) {
            vertical[i] = new Node();
            horizontal[i] = new Node();

            buildSingleEdge(sink, vertical[i]);
            buildSingleEdge(horizontal[i], target);
        }

        int asteroidNum = input.nextInteger();
        for (int i = 0; i < asteroidNum; i++) {
            int row = input.nextInteger() - 1;
            int col = input.nextInteger() - 1;

            buildSingleEdge(vertical[row], horizontal[col]);
        }

        size += 2;
        distCnt = new int[2 * size];
    }

    void bfs() {
        LinkedList<Node> queue = new LinkedList();
        queue.addLast(target);
        target.distance = 0;
        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            distCnt[head.distance]++;
            for (Edge edge : head.edgeList) {
                if (edge.getClass() == PositiveEdge.class) {
                    continue;
                }
                Node dst = edge.dst();
                if (dst.distance == -1) {
                    dst.distance = head.distance + 1;
                    queue.addLast(dst);
                }
            }
        }
    }

    public int sendFlow(Node cur, int flowLimit) {
        if (cur == target) {
            return flowLimit;
        }

        int dist = cur.distance;
        int totalSend = 0;
        for (Edge edge : cur.edgeList) {
            if (!(edge.remainCapacity() > 0 && edge.dst().distance + 1 == dist)) {
                continue;
            }
            int actually = sendFlow(edge.dst(), Math.min(flowLimit - totalSend, edge.remainCapacity()));
            edge.addFlow(actually);
            totalSend += actually;
            flowLimit -= actually;
            if (0 == flowLimit) {
                break;
            }
        }

        if (totalSend == 0) {
            //Fail to send any flow
            if (--distCnt[cur.distance] == 0) {
                sink.distance = size;
            }

            distCnt[++cur.distance]++;
        }

        return totalSend;
    }

    public int solve() {
        bfs();

        int sum = 0;
        while (sink.distance < size) {
            sum += sendFlow(sink, INF);
        }
        return sum;
    }

    private static interface Edge {
        int remainCapacity();

        int flow();

        void addFlow(int amount);

        Node dst();

        Node src();
    }

    private static class Node {
        int distance = -1;
        List<Edge> edgeList = new ArrayList<Edge>();
    }

    private static class PositiveEdge implements Edge {
        int cap;
        int flow;
        Node dst;
        Node src;

        public PositiveEdge(int cap, Node src, Node dst) {
            this.cap = cap;
            this.src = src;
            this.dst = dst;
        }

        public int remainCapacity() {
            return cap - flow;
        }

        public int flow() {
            return flow;
        }

        public void addFlow(int amount) {
            flow += amount;
        }

        
        public Node dst() {
            return dst;
        }

        
        public Node src() {
            return src;
        }

        
        public String toString() {
            return src() + "-" + flow() + ":" + cap + "-" + dst();
        }
    }

    private static class NegativeEdge implements Edge {
        Edge edge;

        public NegativeEdge(Edge edge) {
            this.edge = edge;
        }

        public int remainCapacity() {
            return edge.flow();
        }

        public int flow() {
            return 0;
        }

        public void addFlow(int amout) {
            edge.addFlow(-amout);
        }

        
        public Node dst() {
            return edge.src();
        }

        
        public Node src() {
            return edge.dst();
        }

        
        public String toString() {
            return src() + "-" + remainCapacity() + "-" + dst();
        }
    }

    private static class AcmInputReader implements Closeable {
        private PushbackInputStream in;

        /**
         * 创建读取器
         *
         * @param input 输入流
         */
        public AcmInputReader(InputStream input) {
            in = new PushbackInputStream(input);
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

        public void skipUntilByte(int val) throws IOException {
            int next = nextByte();
            while (next != val && AsciiMarksLazyHolder.asciiMarks[next] != AsciiMarksLazyHolder.EOF) {
                next = nextByte();
            }
            in.unread(next);
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
