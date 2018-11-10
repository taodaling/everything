package com.daltao.oj.old.submit.poj;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dalt on 2017/12/4.
 */
public class ACMComputerFactory {
    static AcmInputReader input = new AcmInputReader(System.in);

    public static void main(String[] args) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (!input.isMeetBlankAndEOF()) {
            ACMComputerFactory solution = new ACMComputerFactory();
            solution.init();
            int totalFlow = solution.solve();
            int containFlowEdgeNum = 0;
            for (Edge edge : solution.edgeList) {
                if (edge.flow() > 0) {
                    containFlowEdgeNum++;
                }
            }
            builder.append(totalFlow)
                    .append(' ')
                    .append(containFlowEdgeNum)
                    .append('\n');
            for (Edge edge : solution.edgeList) {
                if (edge.flow() == 0) {
                    continue;
                }
                builder.append(edge.src().id)
                        .append(' ')
                        .append(edge.dst().id)
                        .append(' ')
                        .append(edge.flow())
                        .append('\n');
            }
            System.out.print(builder.toString());
            builder.setLength(0);
        }
    }

    private static final int INF = 100000000;
    int[] levelCnt;
    int graphNodeNum;
    int partNum;
    int machineNum;
    Node sink;
    Node target;
    private List<Edge> edgeList = new ArrayList();

    public boolean match(byte[] output, byte[] input) {
        for (int i = 0; i < partNum; i++) {
            if (input[i] != 2 && input[i] != output[i]) {
                return false;
            }
        }
        return true;
    }

    public void init() throws IOException {
        partNum = input.nextInteger();
        machineNum = input.nextInteger();
        sink = new Node();
        target = new Node();
        graphNodeNum = machineNum * 2 + 2;

        Node[][] nodes = new Node[machineNum][2];
        int inNodeIndex = 0;
        int outNodeIndex = 1;
        for (int i = 0; i < machineNum; i++) {
            nodes[i][inNodeIndex] = new Node();
            nodes[i][outNodeIndex] = new Node();
            nodes[i][inNodeIndex].id = i + 1;
            nodes[i][outNodeIndex].id = i + 1;
        }


        byte[][] inputRequire = new byte[machineNum][partNum];
        byte[][] outputSample = new byte[machineNum][partNum];
        byte[] sinkOutput = new byte[partNum];
        byte[] targetIn = new byte[partNum];
        Arrays.fill(targetIn, (byte) 1);
        for (int i = 0; i < machineNum; i++) {
            int performance = input.nextInteger();
            buildEdge(nodes[i][inNodeIndex], nodes[i][outNodeIndex], performance);

            for (int j = 0; j < partNum; j++) {
                inputRequire[i][j] = (byte) input.nextInteger();
            }
            for (int j = 0; j < partNum; j++) {
                outputSample[i][j] = (byte) input.nextInteger();
            }
        }

        for (int i = 0; i < machineNum; i++) {
            if (match(sinkOutput, inputRequire[i])) {
                buildEdge(sink, nodes[i][inNodeIndex], INF);
            }
            if (match(outputSample[i], targetIn)) {
                buildEdge(nodes[i][outNodeIndex], target, INF);
            }

            for (int j = 0; j < machineNum; j++) {
                if (j == i) {
                    continue;
                }
                if (match(outputSample[i], inputRequire[j])) {
                    edgeList.add(buildEdge(nodes[i][outNodeIndex], nodes[j][inNodeIndex], INF));
                }
            }
        }

        levelCnt = new int[graphNodeNum + 2];
    }

    public void bfs() {
        LinkedList<Node> queue = new LinkedList();
        target.dist = 0;
        queue.addLast(target);
        while (!queue.isEmpty()) {
            Node head = queue.removeFirst();
            levelCnt[head.dist]++;
            for (Edge edge : head.edgeList) {
                Node dst = edge.dst();
                if (edge.getClass() != NegEdge.class || dst.dist != -1) {
                    continue;
                }
                dst.dist = head.dist + 1;
                queue.addLast(dst);
            }
        }
    }

    public int sendFlow(Node node, int flowLimit) {
        if (node == target) {
            return flowLimit;
        }
        int remainFlowLimit = flowLimit;
        for (Edge edge : node.edgeList) {
            Node dst = edge.dst();
            if (edge.remainCap() > 0 && dst.dist + 1 == node.dist) {
                int actuallySend = sendFlow(dst, Math.min(remainFlowLimit, Math.min(remainFlowLimit, edge.remainCap())));
                remainFlowLimit -= actuallySend;
                edge.sendFlow(actuallySend);
                if (remainFlowLimit == 0) {
                    break;
                }
            }
        }

        if (remainFlowLimit == flowLimit) {
            if (--levelCnt[node.dist] == 0) {
                sink.dist = graphNodeNum;
            }
            levelCnt[++node.dist]++;
        }
        return flowLimit - remainFlowLimit;
    }

    public int solve() {
        int sum = 0;
        bfs();
        if (sink.dist == -1) {
            sink.dist = graphNodeNum;
        }
        while (sink.dist < graphNodeNum) {
            sum += sendFlow(sink, INF);
        }
        return sum;
    }

    private Edge buildEdge(Node src, Node dst, int cap) {
        Edge posEdge = new PosEdge(src, dst, cap);
        src.edgeList.add(posEdge);
        dst.edgeList.add(new NegEdge(posEdge));
        return posEdge;
    }

    private static class Node {
        int id;
        List<Edge> edgeList = new ArrayList();
        int dist = -1;

        public String toString() {
            return "" + id;
        }
    }

    private static interface Edge {
        public int remainCap();

        public int flow();

        public void sendFlow(int flow);

        public Node dst();

        public Node src();
    }

    private static class PosEdge implements Edge {
        int flow;
        int cap;
        Node dst;
        Node src;

        public PosEdge(Node src, Node dst, int cap) {
            this.src = src;
            this.dst = dst;
            this.cap = cap;
        }


        public int remainCap() {
            return cap - flow;
        }


        public int flow() {
            return flow;
        }


        public void sendFlow(int flow) {
            this.flow += flow;
        }


        public Node dst() {
            return dst;
        }


        public Node src() {
            return src;
        }

        public String toString() {
            return src() + "-" + dst();
        }
    }

    public static class NegEdge implements Edge {
        Edge edge;

        public NegEdge(Edge edge) {
            this.edge = edge;
        }


        public int remainCap() {
            return edge.flow();
        }


        public int flow() {
            return 0;
        }


        public void sendFlow(int flow) {
            edge.sendFlow(-flow);
        }


        public Node dst() {
            return edge.src();
        }


        public Node src() {
            return edge.dst();
        }

        public String toString() {
            return src() + "-" + dst();
        }
    }

    private static class AcmInputReader {
        private PushbackInputStream in;

        /**
         * 创建读取器
         *
         * @param input 输入流
         */
        public AcmInputReader(InputStream input) {
            in = new PushbackInputStream(new BufferedInputStream(input));
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
            while (times > 0) {
                skipBlank();
                if ((c = nextByte()) == b) {
                    times--;
                } else {
                    in.unread(c);
                    break;
                }
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
            int c;
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
