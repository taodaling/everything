package com.daltao.oj.old.submit.codeforces;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by Administrator on 2017/8/18.
 */
public class RoadsInTheKingdom {
    public static void main(String[] args) throws Exception {
        AcmInputReader reader = new AcmInputReader(System.in);

        //Initialization
        int n = reader.nextInteger();
        GNode[] nodes = new GNode[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new GNode();
            nodes[i].id = i;
        }
        for (int i = 0; i < n; i++) {
            int nid1 = reader.nextInteger() - 1;
            int nid2 = reader.nextInteger() - 1;
            long length = reader.nextInteger();
            nodes[nid1].edges.put(nid2, length);
            nodes[nid2].edges.put(nid1, length);
        }

        //Find the loop with bfs
        GNode[] loop = null;
        long[] lengthToNextNode = null;
        long combinedDegree = 0;
        long degree[] = null;
        {
            //Find one node belong end loop
            GNode source = nodes[0];
            source.former = source;
            Deque<GNode> queue = new LinkedList<>();
            queue.addLast(source);
            GNode oneSide = null;
            boolean finish = false;
            while (!finish) {
                GNode cur = queue.pop();
                int filterId = cur.former.id;
                for (Map.Entry<Integer, Long> pair : cur.edges.entrySet()) {
                    Integer key = pair.getKey();
                    Long value = pair.getValue();
                    if (key == filterId) {
                        continue;
                    }
                    GNode otherSide = nodes[key];
                    //Wow, we find a node in loop!
                    if (otherSide.former != null) {
                        oneSide = cur;
                        GNode trace = null;
                        for (trace = otherSide; trace.former != trace; trace = trace.former) {
                            trace.degree += 1;
                        }
                        int rightNum = 0;
                        for (trace = cur; trace.former != trace; trace = trace.former) {
                            if (trace.degree == 1) {
                                break;
                            }
                            rightNum++;
                            trace.degree += 1;
                        }
                        GNode middle = trace;
                        int leftNum = 0;
                        for (trace = otherSide; trace != middle; trace = trace.former) {
                            leftNum += 1;
                        }
                        int loopLength = leftNum + rightNum + 1;
                        loop = new GNode[loopLength];
                        lengthToNextNode = new long[loopLength];
                        int wpos = 0;
                        for (trace = otherSide; trace != middle; trace = trace.former, wpos++) {
                            loop[wpos] = trace;
                            lengthToNextNode[wpos] = trace.distanceToFormer;
                        }
                        wpos = loopLength - 1;
                        lengthToNextNode[wpos] = value;
                        for (trace = cur; trace != middle; trace = trace.former, wpos--) {
                            loop[wpos] = trace;
                            lengthToNextNode[wpos - 1] = trace.distanceToFormer;
                        }
                        loop[wpos] = middle;
                        finish = true;
                        break;
                    }

                    otherSide.former = cur;
                    otherSide.distanceToFormer = value;
                    queue.addLast(otherSide);
                }
            }
            queue.clear();
            for (GNode node : loop) {
                node.inLoop = true;
                queue.add(node);
            }

            //Find the max length of subDirty graph
            for (GNode node : nodes) {
                node.former = null;
            }
            while (!queue.isEmpty()) {
                GNode cur = queue.pop();
                int filterId = -1;
                if (cur.former != null) {
                    filterId = cur.former.id;
                }
                for (Map.Entry<Integer, Long> pair : cur.edges.entrySet()) {
                    Integer key = pair.getKey();
                    Long value = pair.getValue();
                    if (key == filterId) {
                        continue;
                    }
                    GNode otherSide = nodes[key];
                    if (otherSide.inLoop) {
                        continue;
                    }
                    otherSide.former = cur;
                    otherSide.distanceToFormer = value;
                    queue.addLast(otherSide);
                }
            }
            for (GNode node : nodes) {
                if (node.inLoop) {
                    continue;
                }
                if (node.former.inLoop) {
                    node.former.updateSubGraphLength(node.distanceToFormer);
                    continue;
                }
                queue.addFirst(node);
                while (!queue.isEmpty()) {
                    GNode head = queue.peek();
                    if (head.former.former.inLoop) {
                        head.distanceToFormer = head.distanceToFormer + head.former.distanceToFormer;
                        head.former = head.former.former;
                        queue.pop();
                    } else {
                        queue.addFirst(head.former);
                    }
                }
                node.former.updateSubGraphLength(node.distanceToFormer);
            }

            //Find the degreeL and maximum combined degreeL
            for (GNode node : nodes) {
                if (!node.inLoop) {
                    continue;
                }
                combinedDegree = Math.max(node.subGraphLength0 + node.subGraphLength1, combinedDegree);
                node.degree = Math.max(node.subGraphLength0, node.subGraphLength1);
            }
        }

        //The length of loop
        long loopLength = 0;
        for (long edge : lengthToNextNode) {
            loopLength += edge;
        }

        //Ok, next step, iterate over the loop
        KeyHeap.Key[] lKeys = new KeyHeap.Key[loop.length];
        KeyHeap.Key[] rKeys = new KeyHeap.Key[loop.length];
        KeyHeap ldHeap = new KeyHeap(loop.length, new LdComparator());
        KeyHeap rdHeap = new KeyHeap(loop.length, new RdComparator());
        long ldFixValue = 0;
        long rdFixValue = 0;
        long minLongestWayInLoop = Long.MAX_VALUE;
        {
            //Remove (m, 1) at first
            long accumulation;
            long segmentLength = loopLength - lengthToNextNode[loop.length - 1];
            accumulation = lengthToNextNode[0];
            lKeys[0] = ldHeap.push(new Pair(loop[0].degree, 0));
            rKeys[0] = rdHeap.push(new Pair(loop[0].degree - segmentLength, 0));
            for (int i = 1, bound = loop.length; i < bound; i++) {
                lKeys[i] = ldHeap.push(new Pair(loop[i].degree - accumulation, i));
                rKeys[i] = rdHeap.push(new Pair(loop[i].degree - segmentLength + accumulation, i));
                accumulation += lengthToNextNode[i];
            }
            Pair<Long, Integer> lbest = (Pair<Long, Integer>) ldHeap.peek();
            Pair<Long, Integer> rbest = (Pair<Long, Integer>) rdHeap.peek();
            long best = 0;
            if (lbest.value == rbest.value) {
                Pair<Long, Integer> ldCandidate = (Pair<Long, Integer>) ldHeap.peekSecondOne();
                if (ldCandidate.value < rbest.value) {
                    best = Math.max(best, segmentLength + ldCandidate.key + rbest.key);
                }
                Pair<Long, Integer> rdCandidate = (Pair<Long, Integer>) ldHeap.peekSecondOne();
                if (rdCandidate.value > lbest.value) {
                    best = Math.max(best, segmentLength + lbest.key + rdCandidate.key);
                }
            } else {
                best = segmentLength + lbest.key + rbest.key;
            }
            best = best + ldFixValue + rdFixValue;
            minLongestWayInLoop = Math.min(minLongestWayInLoop, best);
        }

        //continue
        for (int i = 0, bound = loop.length; i < bound; i++) {
            //We move the i from the leftest end rightest
            GNode node = loop[i];

            ldFixValue += lengthToNextNode[i];
            long segmentLength = loopLength - lengthToNextNode[i];
            if (i > 0) {
                rdFixValue -= lengthToNextNode[i - 1];
            } else {
                rdFixValue -= lengthToNextNode[bound - 1];
            }
            long origLd = node.degree - segmentLength;
            long origRd = node.degree;
            lKeys[i].update(new Pair(origLd - ldFixValue, bound + i));
            rKeys[i].update(new Pair(origRd - rdFixValue, bound + i));

            //Find the longest way
            Pair<Long, Integer> lbest = (Pair<Long, Integer>) ldHeap.peek();
            Pair<Long, Integer> rbest = (Pair<Long, Integer>) rdHeap.peek();
            long best = 0;
            if (lbest.value == rbest.value) {
                Pair<Long, Integer> ldCandidate = (Pair<Long, Integer>) ldHeap.peekSecondOne();
                if (ldCandidate.value < rbest.value) {
                    best = Math.max(best, segmentLength + ldCandidate.key + rbest.key);
                }
                Pair<Long, Integer> rdCandidate = (Pair<Long, Integer>) ldHeap.peekSecondOne();
                if (rdCandidate.value > lbest.value) {
                    best = Math.max(best, segmentLength + lbest.key + rdCandidate.key);
                }
            } else {
                best = segmentLength + lbest.key + rbest.key;
            }
            best = best + ldFixValue + rdFixValue;
            minLongestWayInLoop = Math.min(minLongestWayInLoop, best);
        }

        long result = Math.max(minLongestWayInLoop, combinedDegree);
        System.out.println(result);


//        long[][] nearestDistance = new long[n][n];
//        nodes[12].edges.remove(33);
//        nodes[33].edges.remove(12);
//        long longest = 0;
//        for(int i = 0; i < n; i++) {
//            Pair<Integer, Long> p = getLonestWay(nodes, nodes[i], null);
//            if (p.value > longest)
//            {
//                longest = p.value;
//            }
//        }
//        System.out.print(longest);
    }

    public static Pair<Integer, Long> getLonestWay(GNode[] all, GNode nodes, GNode filter) {
        long maxLength = 0;
        int id = nodes.id;
        for (Map.Entry<Integer, Long> entry : nodes.edges.entrySet()) {
            if(filter != null && entry.getKey() == filter.id)
            {
                continue;
            }
            Pair<Integer, Long> p = getLonestWay(all, all[entry.getKey()], nodes);
            if (p.value + entry.getValue() > maxLength) {
                maxLength = p.value + entry.getValue();
                id = p.key;
                nodes.former = all[entry.getKey()];
                nodes.distanceToFormer = entry.getValue();
            }
        }
        return new Pair<>(id, maxLength);
    }

    private static class LdComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            int result = ((Comparable) (((Pair) b).key)).compareTo(((Pair) a).key);
            if (result == 0) {
                result = ((Comparable) (((Pair) a).value)).compareTo(((Pair) b).value);
            }
            return result;
        }
    }

    private static class RdComparator implements Comparator {
        @Override
        public int compare(Object a, Object b) {
            int result = ((Comparable) (((Pair) b).key)).compareTo(((Pair) a).key);
            if (result == 0) {
                result = ((Comparable) (((Pair) b).value)).compareTo(((Pair) a).value);
            }
            return result;
        }
    }

    private static class GNode {
        int id;
        boolean inLoop;
        Map<Integer, Long> edges = new TreeMap<>();
        GNode former;
        long distanceToFormer;
        long subGraphLength0;
        long subGraphLength1;
        long degree;

        void updateSubGraphLength(long newOne) {
            if (subGraphLength0 <= subGraphLength1) {
                if (subGraphLength0 <= newOne) {
                    subGraphLength0 = newOne;
                }
            } else if (subGraphLength1 <= newOne) {
                subGraphLength1 = newOne;
            }
        }

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static class Pair<K, V> {
        public final K key;
        public final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + " : " + value;
        }
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

    /**
     * 这个类代表了一个定长最小堆结构。
     * <br>
     * Created by dalt on 2017/7/29.
     */
    public static class KeyHeap {
        private Comparator<Object> comparator;
        private Key[] keys;
        private int remain;

        /**
         * 利用data创建堆的初始数据，其中里面数据的偏序关系由comparator计算得到。
         * 而keyOutput用于返还data中每个数据对应的键。可以直接利用键修改甚至移除键代表的数据。
         *
         * @param data       初始数据
         * @param keyOutput  返回的键
         * @param comparator 比较器
         */
        public KeyHeap(Object[] data, Key[] keyOutput, Comparator<Object> comparator) {
            remain = data.length;
            this.comparator = comparator;

            if (data == null) {
                throw new IllegalArgumentException();
            }
            if (comparator == null) {
                throw new IllegalArgumentException();
            }
            keys = new Key[data.length];
            for (int i = 0, bound = data.length; i < bound; i++) {
                keys[i] = new Key(data[i], i);
            }
            if (keyOutput != null) {
                System.arraycopy(keys, 0, keyOutput, 0, Math.min(keys.length, keyOutput.length));
            }
            Arrays.sort(keys, comparator);
            for (int i = 0, bound = data.length; i < bound; i++) {
                keys[i].index = i;
            }
        }

        /**
         * 创建一个容量为capacity的空堆，堆内存储的数据的比较规则由comparator指定。
         *
         * @param capacity   最大容量
         * @param comparator 数据比较器
         */
        public KeyHeap(int capacity, Comparator<Object> comparator) {
            if (capacity < 0) {
                throw new IllegalArgumentException();
            }
            if (comparator == null) {
                throw new IllegalArgumentException();
            }
            keys = new Key[capacity];
            this.comparator = comparator;
        }

        /**
         * 获取堆中最小的数据对象。
         *
         * @return 堆中关键字最小的数据对象
         */
        public Object peek() {
            if (remain <= 0) {
                throw new IllegalStateException();
            }
            return keys[0].value;
        }

        public Object peekSecondOne() {
            if (remain <= 2) {
                throw new IllegalStateException();
            }
            Object v1 = keys[1].getValue();
            Object v2 = keys[2].getValue();
            return comparator.compare(v1, v2) <= 0 ? v1 : v2;
        }

        /**
         * 返回堆中元素数目
         *
         * @return 堆的大小
         */
        public int size() {
            return remain;
        }

        /**
         * 返回并移除堆中最小的数据对象
         *
         * @return 堆中最小的数据对象
         */
        public Object pop() {
            if (remain <= 0) {
                throw new IllegalStateException();
            }
            Key result = keys[0];
            remain--;
            inject(keys[remain], 0);
            keys[remain] = null;
            downward(0);
            return result.value;
        }

        /**
         * 插入一个新的对象
         *
         * @param value 对象
         * @return 一个新的对象
         */
        public Key push(Object value) {
            if (remain == keys.length) {
                throw new IllegalStateException();
            }
            Key newKey = new Key(value, remain);
            keys[remain] = newKey;
            remain++;
            upward(newKey.index);
            return newKey;
        }

        /**
         * 清空堆
         */
        public void clear() {
            for (int i = 0; i < remain; i++) {
                keys[i].removed = true;
                keys[i] = null;
            }
            remain = 0;
        }

        private void downward(int target) {
            if (target < 0 || target >= remain) {
                return;
            }
            Key targetKey = keys[target];
            while (true) {
                int lindex = target << 1;
                int rindex = (target << 1) | 1;
                if (lindex >= remain) {
                    break;
                }
                if (rindex >= remain) {
                    rindex = lindex;
                }
                Key left = keys[lindex];
                Key right = keys[rindex];
                if (comparator.compare(left.value, right.value) < 0) {
                    if (comparator.compare(left.value, targetKey.value) < 0) {
                        inject(left, target);
                        target = lindex;
                    } else {
                        break;
                    }
                } else {
                    if (comparator.compare(right.value, targetKey.value) < 0) {
                        inject(right, target);
                        target = rindex;
                    } else {
                        break;
                    }
                }
            }
            inject(targetKey, target);
        }

        private void inject(Key key, int i) {
            keys[i] = key;
            key.index = i;
        }

        private void upward(int target) {
            if (target < 0 || target >= remain) {
                return;
            }
            Key targetKey = keys[target];
            while (target > 0) {
                int pindex = target >> 1;
                Key parent = keys[pindex];
                if (comparator.compare(targetKey.value, parent.value) < 0) {
                    inject(parent, target);
                    target = pindex;
                } else {
                    break;
                }
            }
            inject(targetKey, target);
        }

        /**
         * 判断堆是否为空
         *
         * @return true表示堆为空，false表示不空
         */
        public boolean isEmpty() {
            return remain == 0;
        }

        private void remove(int index) {
            keys[index].removed = true;
            keys[index] = keys[--remain];
            keys[remain] = null;
            downward(index);
        }

        /**
         * 钥匙的抽象类，一个插入堆中的对象都对应一个钥匙，可以利用钥匙找到对象，以及可以修改它或是从堆中移除它。
         */
        public class Key {
            boolean removed = false;
            private int index;
            private Object value;

            private Key(Object value, int index) {
                this.value = value;
                this.index = index;
            }

            /**
             * 获取钥匙对应的值
             *
             * @return 钥匙对应的值
             */
            public Object getValue() {
                return value;
            }

            /**
             * 从堆中移除钥匙代表的数据对象
             */
            public void remove() {
                if (removed) {
                    throw new IllegalStateException();
                }
                KeyHeap.this.remove(index);
            }

            /**
             * 尝试用value更新钥匙内部存储的数据对象
             *
             * @param value 新值
             */
            public void update(Object value) {
                if (removed) {
                    throw new IllegalStateException();
                }
                int cmp = comparator.compare(value, this.value);
                this.value = value;
                if (cmp < 0) {
                    upward(index);
                } else if (cmp > 0) {
                    downward(index);
                }
            }
        }
    }
}
