package com.daltao.oj.old.submit.codeforces;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/6/30.
 */
public class MEXQueries {
    public static void main(String[] args) throws Exception {
        AcmInputReader reader = new AcmInputReader(System.in);
        int num = reader.nextInteger();

        if (num == 0) {
            return;
        }

        int[] cmds = new int[num];
        long[] leftBounds = new long[num];
        long[] rightBounds = new long[num];

        //read all input into memory
        for (int i = 0; i < num; i++) {
            cmds[i] = reader.nextInteger();
            leftBounds[i] = reader.nextLong();
            rightBounds[i] = reader.nextLong();
        }

        //Generate g
        long[] g = new long[num * 4 + 2];
        System.arraycopy(leftBounds, 0, g, 0, num);
        System.arraycopy(rightBounds, 0, g, num, num);
        for (int i = 0, base1 = num * 2, base2 = num * 3; i < num; i++) {

            g[base1 + i] = Math.max(rightBounds[i] - 1, 1);
            g[base2 + i] = rightBounds[i] + 1;
        }
        g[num * 4] = 1;
        g[num * 4 + 1] = Long.MAX_VALUE;
        Arrays.sort(g);
        //Remove all repeated elements
        int gLen = 0;
        for (int i = 1, bound = g.length; i < bound; i++) {
            if (g[i] != g[gLen]) {
                g[++gLen] = g[i];
            }
        }
        gLen++;

        //Build section tree
        SectionTree tree = new SectionTree(0, gLen);

        //Handle all cmds and output the MEX
        for (int i = 0; i < num; i++) {
            int cmd = cmds[i];
            int leftBound = Arrays.binarySearch(g, 0, gLen, leftBounds[i]);
            int rightBound = Arrays.binarySearch(g, 0, gLen, rightBounds[i] + 1) - 1;

            tree.update(leftBound, rightBound, cmd);

            //Event all information
//            for (int j = 0; j < gLen - 1; j++) {
//                if (tree.queryValue(j, j).maxValue == 1) {
//                    System.out.print(String.format("[%d, %d], ", g[j], g[j + 1] - 1));
//                }
//            }
//            System.out.println();

            System.out.println(g[tree.queryMEX()]);
        }
    }

    /**
     * @author dalt
     * @see AutoCloseable
     * @since java1.7
     */
    private static class AcmInputReader implements AutoCloseable {
        private PushbackInputStream in;

        @Override
        public void close() throws IOException {
            in.close();
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
    }

    private static /**
     * Created by F on 2017/6/29.
     */
    class SectionTree {
        /**
         * 根结点
         */
        private SectionTreeNode root;

        /**
         * 代表最大值和最小值的值对
         */
        public static class ValuePair {
            public static final ValuePair EMPTY_VALUE_PAIR = new ValuePair(Integer.MAX_VALUE, Integer.MIN_VALUE);
            public final int minValue;
            public final int maxValue;

            public ValuePair(int minValue, int maxValue) {
                this.minValue = minValue;
                this.maxValue = maxValue;
            }

            @Override
            public String toString() {
                return String.format("max:%d, min:%d", maxValue, minValue);
            }

            public static ValuePair combine(ValuePair a, ValuePair b) {
                return new ValuePair(Math.min(a.minValue, b.minValue),
                        Math.max(a.maxValue, b.maxValue));
            }
        }

        /**
         * 区间类型
         */
        private static class Section {
            /**
             * 两个区间的关系，分别为被包含，相交和无覆盖
             */
            public static enum SectionRelation {
                INCLUDED, INTERSECT, NONE
            }

            private int leftBound, rightBound;

            public int getLeftBound() {
                return leftBound;
            }

            public int getRightBound() {
                return rightBound;
            }

            public Section(int leftBound, int rightBound) {
                if (rightBound < leftBound) {
                    throw new IllegalArgumentException(String.format("A section should obey leftBound<=rightBound, but current state is %d > %d", leftBound, rightBound));
                }
                this.leftBound = leftBound;
                this.rightBound = rightBound;
            }

            /**
             * 计算this和other两个区间的关系
             *
             * @param other 另外一个区间
             * @return this和other的关系
             */
            public SectionRelation relationWith(Section other) {
                if (rightBound < other.leftBound || leftBound > other.rightBound) {
                    return SectionRelation.NONE;
                }
                if (rightBound <= other.rightBound && leftBound >= other.leftBound) {
                    return SectionRelation.INCLUDED;
                }
                return SectionRelation.INTERSECT;
            }

            /**
             * 提取左半区间
             *
             * @return 左半区间
             */
            public Section leftHalfSection() {
                return new Section(leftBound, (leftBound + rightBound) / 2);
            }

            /**
             * 提取右半区间
             *
             * @return 右半区间
             */
            public Section rightHalfSection() {
                return new Section((leftBound + rightBound) / 2 + 1, rightBound);
            }

            @Override
            public String toString() {
                return String.format("[%d, %d]", leftBound, rightBound);
            }

            /**
             * 获取区间中覆盖的整数数目
             *
             * @return 区间中覆盖的整数数目
             */
            public int size() {
                return rightBound - leftBound + 1;
            }

            /**
             * 快速判断当前区间是否只覆盖了一个整数
             *
             * @return true表示只覆盖了一个整数，否则返回false
             */
            public boolean containSingleElement() {
                return rightBound == leftBound;
            }
        }

        /**
         * 区间树结点
         */
        private static class SectionTreeNode {
            public SectionTreeNode leftChild, rightChild;
            private Section section;
            private int cachedMinValue, cachedMaxValue;
            private int dirtyMark;

            /**
             * 获取结点所代表的区间
             *
             * @return
             */
            public Section getSection() {
                return section;
            }

            /**
             * 设置肮脏标志
             *
             * @param dirtyMark 肮脏标志（修改值）
             */
            public void setDirtyMark(int dirtyMark) {
                this.dirtyMark += dirtyMark;
            }

            /**
             * 构造代表section区间的区间树结点
             *
             * @param section 一个区间
             */
            public SectionTreeNode(Section section) {
                this.section = section;
            }

            /**
             * 获取结点所代表的区间中的最小值
             *
             * @return 结点所代表的区间中的最小值
             */
            public int getMinValue() {
                switch (dirtyMark) {
                    case 0:
                        return cachedMinValue;
                    case 1:
                        return 1;
                    case 2:
                        return 0;
                    default:
                        return (cachedMaxValue + 1) % 2;
                }
            }

            /**
             * 获取结点所代表的区间中的最大值
             *
             * @return 结点所代表的区间中的最大值
             */
            public int getMaxValue() {
                switch (dirtyMark) {
                    case 0:
                        return cachedMaxValue;
                    case 1:
                        return 1;
                    case 2:
                        return 0;
                    default:
                        return (cachedMinValue + 1) % 2;
                }
            }

            /**
             * 利用子结点中保存的信息更新当前结点内部缓存的最小值和最大值
             */
            public void updateCachedValue() {
                cachedMinValue = Math.min(leftChild.getMinValue(), rightChild.getMinValue());
                cachedMaxValue = Math.max(leftChild.getMaxValue(), rightChild.getMaxValue());
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(String.format("{lrBound:%s,minMaxValue:[%d,%d],dirtyMark:%d(",
                        section.toString(), cachedMinValue, cachedMaxValue, dirtyMark));
                if (leftChild != null) {
                    sb.append(leftChild.toString());
                }
                sb.append(", ");
                if (rightChild != null) {
                    sb.append(rightChild.toString());
                }
                sb.append(")}");
                return sb.toString();
            }
        }

        /**
         * 利用数组data构建一株区间树
         *
         * @param data 数组
         */
        public SectionTree(int[] data) {
            this(data, 0, data.length);
        }

        /**
         * 利用数组data构建一株区间树
         *
         * @param data 数组
         */
        public SectionTree(int[] data, int from, int to) {
            if (from >= to) {
                throw new IllegalArgumentException("You can't build a section tree with empty array");
            }
            this.root = buildTreeNode(data, new Section(from, to - 1));
        }

        @Override
        public String toString() {
            return root.toString();
        }

        /**
         * 更新[leftBound, rightBound]中所有的整数，令其增加val
         *
         * @param leftBound  区间左边界
         * @param rightBound 区间右边界
         * @param val        修正值
         */
        public void update(int leftBound, int rightBound, int val) {
            update(root, new Section(leftBound, rightBound), val);
        }

        /**
         * 查询[leftBound, rightBound]中的最大值和最小值
         *
         * @param leftBound  区间左边界
         * @param rightBound 区间右边界
         * @return 保存了最大值和最小值的ValuePair对象
         */
        public ValuePair queryValue(int leftBound, int rightBound) {
            return queryValue(root, new Section(leftBound, rightBound));
        }

        public static ValuePair queryValue(SectionTreeNode node, Section section) {
            switch (node.getSection().relationWith(section)) {
                case INCLUDED:
                    return new ValuePair(node.getMinValue(), node.getMaxValue());
                case INTERSECT:
                    ValuePair vp = ValuePair.combine(queryValue(node.leftChild, section),
                            queryValue(node.rightChild, section));
                    int min = vp.minValue;
                    int max = vp.maxValue;
                    switch (node.dirtyMark) {
                        case 0:
                            return vp;
                        case 1:
                            return new ValuePair(1, 1);
                        case 2:
                            return new ValuePair(0, 0);
                        case 3:
                            return new ValuePair((max + 1) % 2, (min + 1) % 2);
                    }
            }
            return ValuePair.EMPTY_VALUE_PAIR;
        }

        public static void update(SectionTreeNode node, Section section, int val) {
            switch (node.getSection().relationWith(section)) {
                case INCLUDED:
                    if (val != 3) {
                        node.dirtyMark = val;
                    } else {
                        node.dirtyMark = 3 - node.dirtyMark;
                    }
                    return;
                case INTERSECT:
                    if (node.dirtyMark != 0) {
                        update(node.leftChild, node.section, node.dirtyMark);
                        update(node.rightChild, node.section, node.dirtyMark);
                        node.dirtyMark = 0;
                    }
                    update(node.leftChild, section, val);
                    update(node.rightChild, section, val);
                    node.updateCachedValue();
                    return;
                case NONE:
                    return;
            }
        }

        private static SectionTreeNode buildTreeNode(int[] data, Section section) {
            SectionTreeNode current = new SectionTreeNode(section);
            if (section.containSingleElement()) {
                current.cachedMinValue = current.cachedMaxValue = data[section.getLeftBound()];
                return current;
            }
            current.leftChild = buildTreeNode(data, section.leftHalfSection());
            current.rightChild = buildTreeNode(data, section.rightHalfSection());
            current.updateCachedValue();
            return current;
        }

        public SectionTree(int from, int to) {
            if (to <= from) {
                throw new IllegalArgumentException("You can't build a section tree with empty array");
            }
            this.root = buildZeroizeTreeNode(new Section(from, to - 1));
        }

        private static SectionTreeNode buildZeroizeTreeNode(Section section) {
            SectionTreeNode current = new SectionTreeNode(section);
            if (section.containSingleElement()) {
                return current;
            }
            current.leftChild = buildZeroizeTreeNode(section.leftHalfSection());
            current.rightChild = buildZeroizeTreeNode(section.rightHalfSection());
            return current;
        }

        public int queryMEX() {
            return queryMEX(root, 0);
        }

        private static int queryMEX(SectionTreeNode node, int reverse) {
            if (node.getSection().containSingleElement()) {
                return node.getSection().leftBound;
            }
            reverse = reverse ^ (node.dirtyMark == 3 ? 1 : 0);
            if (reverse == 0) {
                if (node.dirtyMark == 2) {
                    return node.section.leftBound;
                }
                if (node.leftChild.getMinValue() == 0) {
                    return queryMEX(node.leftChild, reverse);
                }
                return queryMEX(node.rightChild, reverse);
            } else {
                if (node.dirtyMark == 1) {
                    return node.section.leftBound;
                }
                if (node.leftChild.getMaxValue() == 1) {
                    return queryMEX(node.leftChild, reverse);
                }
                return queryMEX(node.rightChild, reverse);
            }
        }
    }
}


