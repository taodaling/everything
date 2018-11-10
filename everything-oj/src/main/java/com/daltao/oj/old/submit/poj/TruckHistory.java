package com.daltao.oj.old.submit.poj;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/30.
 */
public class TruckHistory {
    private static final int INF = 100000000;
    private static AcmInputReader input = new AcmInputReader(System.in);
    int n;
    byte[][] edgeWeights;

    public static void main(String[] args) throws IOException {
        StringBuilder result = new StringBuilder();
        while (!input.isMeetBlankAndEOF()) {
            int n = input.nextInteger();
            if (n == 0) {
                break;
            }
            TruckHistory solution = new TruckHistory();
            solution.init(n);
            result.append("The highest possible quality is 1/")
                    .append(solution.solve())
                    .append(".\n");
        }
        System.out.print(result);
    }

    public void init(int n) throws IOException {
        this.n = n;
        edgeWeights = new byte[n][n];
        char[][] versions = new char[n][];
        for (int i = 0; i < n; i++) {
            char[] nextLine = input.nextWord().toCharArray();
            versions[i] = nextLine;
            for (int j = 0; j < i; j++) {
                char[] former = versions[j];
                int cnt = 0;
                for (int k = 0; k < 7; k++) {
                    cnt += nextLine[k] == former[k] ? 0 : 1;
                }
                edgeWeights[i][j] = edgeWeights[j][i] = (byte) cnt;
            }
        }
    }

    public int solve() {
        Integer[] dist = new Integer[n];
        Integer[] num = new Integer[n];
        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            num[i] = i;
        }
        dist[0] = 0;
        KeyHeap.Key<Integer, Integer>[] keys = new KeyHeap.Key[n];
        KeyHeap<Integer, Integer> heap = new KeyHeap<Integer, Integer>(dist, num, keys, new Comparator<Integer>() {

            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        int sum = 0;
        while (!heap.isEmpty()) {
            Map.Entry<Integer, Integer> top = heap.pop();
            sum += top.getKey();
            int nodeNo = top.getValue();
            for (int i = 0; i < n; i++) {
                if (edgeWeights[nodeNo][i] < keys[i].getKey()) {
                    keys[i].update((int) edgeWeights[nodeNo][i]);
                }
            }
        }

        return sum;
    }

    /**
     * 这个类代表了一个定长最小堆结构。
     * <br>
     * Created by dalt on 2017/7/29.
     */
    private static class KeyHeap<K, V> {
        private Comparator<K> comparator;
        private Key<K, V>[] keys;
        private int remain;

        /**
         * 利用data创建堆的初始数据，其中里面数据的偏序关系由comparator计算得到。
         * 而keyOutput用于返还data中每个数据对应的键。可以直接利用键修改甚至移除键代表的数据。
         *
         * @param comparator 比较器
         */
        public KeyHeap(K[] keys, V[] values, Key<K, V>[] output, final Comparator<K> comparator) {
            remain = keys.length;
            this.comparator = comparator;

            if (comparator == null) {
                throw new NullPointerException();
            }
            this.keys = (Key[]) Array.newInstance(Key.class, remain);
            for (int i = 0; i < remain; i++) {
                this.keys[i] = new Key(keys[i], values[i], i, this);
            }
            System.arraycopy(this.keys, 0, output, 0, Math.min(output.length, this.keys.length));
            Arrays.sort(this.keys);
            for (int i = 0; i < remain; i++) {
                this.keys[i].index = i;
            }
        }


        /**
         * 创建一个容量为capacity的空堆，堆内存储的数据的比较规则由comparator指定。
         *
         * @param capacity   最大容量
         * @param comparator 数据比较器
         */
        public KeyHeap(int capacity, Comparator<K> comparator) {
            if (capacity < 0) {
                throw new IllegalArgumentException();
            }
            if (comparator == null) {
                throw new NullPointerException();
            }
            keys = new Key[capacity];
            this.comparator = comparator;
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
        public Map.Entry<K, V> pop() {
            Key result = keys[0];
            remain--;
            inject(keys[remain], 0);
            keys[remain] = null;
            smoothDownward(0);
            return result;
        }

        /**
         * 获取堆中最小的数据对象。
         *
         * @return 堆中关键字最小的数据对象
         */
        public Key<K, V> peek() {
            return keys[0];
        }

        /**
         * 插入一个新的对象
         *
         * @param value 对象
         * @return 一个新的对象
         */
        public Key push(K key, V value) {
            Key newKey = new Key(key, value, remain, this);
            keys[remain] = newKey;
            remain++;
            smoothUpward(newKey.index);
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

        private void smoothDownward(int target) {
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
                if (left.compareTo(right) < 0) {
                    if (left.compareTo(targetKey) < 0) {
                        inject(left, target);
                        target = lindex;
                    } else {
                        break;
                    }
                } else {
                    if (right.compareTo(targetKey) < 0) {
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

        private void smoothUpward(int target) {
            if (target < 0 || target >= remain) {
                return;
            }
            Key targetKey = keys[target];
            while (target > 0) {
                int pindex = target >> 1;
                Key parent = keys[pindex];
                if (targetKey.compareTo(parent) < 0) {
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
            smoothDownward(index);
        }

        /**
         * 钥匙的抽象类，一个插入堆中的对象都对应一个钥匙，可以利用钥匙找到对象，以及可以修改它或是从堆中移除它。
         */
        public static class Key<K, V> implements Map.Entry<K, V>, Comparable<Key<K, V>> {
            boolean removed = false;
            private int index;
            private K key;
            private V value;
            private KeyHeap<K, V> heap;

            private Key(K key, V value, int index, KeyHeap<K, V> heap) {
                this.key = key;
                this.value = value;
                this.index = index;
                this.heap = heap;
            }


            public K getKey() {
                return key;
            }


            public V setValue(V value) {
                V tmp = value;
                this.value = value;
                return tmp;
            }

            /**
             * 获取钥匙对应的值
             *
             * @return 钥匙对应的值
             */
            public V getValue() {
                return value;
            }

            /**
             * 从堆中移除钥匙代表的数据对象
             */
            public void remove() {
                if (removed) {
                    throw new IllegalStateException();
                }
                heap.remove(index);
            }

            /**
             * 尝试用value更新钥匙内部存储的数据对象
             *
             * @param key 新值
             */
            public void update(K key) {
                if (removed) {
                    return;
                }
                int cmp = heap.comparator.compare(key, this.key);
                this.key = key;
                if (cmp < 0) {
                    heap.smoothUpward(index);
                } else if (cmp > 0) {
                    heap.smoothDownward(index);
                }
            }


            public int compareTo(Key<K, V> o) {
                return heap.comparator.compare(key, o.key);
            }
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
