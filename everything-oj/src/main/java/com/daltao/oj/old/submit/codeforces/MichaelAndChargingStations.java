package com.daltao.oj.old.submit.codeforces;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/9/21.
 */
public class MichaelAndChargingStations {
    int totalDay; //The total day number
    int[] costs; //The cost for each day
    int[] sumUp; //sumUp[i] = cost[i] + cost[i + 1] + ... + cost[totalDay - 1]
    int[] remain; //remain[i] = remian[0] + remain[1] + ... + remain[i - 1]

    public static void main(String[] args) {
        MichaelAndChargingStations solution = new MichaelAndChargingStations();
        solution.init();
        int result = solution.solve();
        System.out.println(result);
    }

    public void init() {
        try {
            AcmInputReader input = new AcmInputReader(System.in);

            totalDay = input.nextInteger();
            costs = new int[totalDay];
            for (int i = 0; i < totalDay; i++) {
                costs[i] = input.nextInteger();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int solve() {
        remain = new int[totalDay];
        sumUp = new int[totalDay + 1];

        remain[0] = 0;
        for (int i = 0, bound = totalDay - 1; i < bound; i++) {
            remain[i + 1] = remain[i] + costs[i] / 10;
        }
        sumUp[totalDay] = 0;
        for (int i = totalDay - 1; i >= 0; i--) {
            sumUp[i] = sumUp[i + 1] + costs[i];
        }

        int maxConsume = 0;


        int h1, h2;

        //Try H1 < H2
        h1 = preIndex(1000, totalDay);
        h2 = totalDay;
        while (h1 >= 0) {
            int allowed = maxAllowedUse(h1, h2);
            if (allowed < 0) {
                break;
            }

            maxConsume = Math.max(maxConsume, allowed + sumUp[h2]);

            h2--;
            if (h2 == h1) {
                h1 = preIndex(1000, h1);
            }
        }

        //Try H2 < H1
        int h1before = preIndex(1000, totalDay);
        h2 = preIndex(2000, totalDay);
        h1 = totalDay;
        while (h2 >= 0) {
            if (h1before > h2) {
                h1 = h1before;
                h1before = preIndex(1000, h1before);
                continue;
            }

            int allowed;
            allowed = maxAllowedUseWithInterval(h2, h1);
            if (allowed < 0) {
                break;
            }
            maxConsume = Math.max(maxConsume, allowed + sumOf(h2 + 1, h1) + sumOf(h1 + 1, totalDay));

            allowed = maxAllowedUseWithInterval(h2, totalDay);
            if (allowed >= 0) {
                maxConsume = Math.max(maxConsume, allowed + sumOf(h2 + 1, totalDay));
            }

            h2 = preIndex(2000, h2);
        }

        return sumUp[0] - maxConsume;
    }

    /**
     * This function calculate a model that from day blockstart, we use enough bonus end feed the cost.
     * And the day index is the only day before blockStart that use bonus, so how many bonus day index can use?
     */
    int maxAllowedUse(int index, int blockStart) {
        if (blockStart >= totalDay) {
            return Math.min(remain[index], costs[index]);
        }
        return Math.min(Math.min(remain[blockStart] - (sumUp[blockStart] + costs[index] / 10), remain[index]), costs[index]);
    }

    /**
     * A simple function end sum up costs[from], cost[from + 1], ... , costs[end -1]
     */
    int sumOf(int from, int to) {
        if (from >= to) {
            return 0;
        }
        return sumUp[from] - sumUp[to];
    }

    /**
     * This function solve a problem, that all the day from index except day interval all use bouns, and all the day use bonus feed the cost other than day index.
     * So how many bonus day index can use?
     */
    int maxAllowedUseWithInterval(int index, int interval) {
        if (interval >= totalDay) {
            return Math.min(remain[index] - sumUp[index + 1], costs[index]);
        }
        return Math.min(Math.min(remain[index], remain[index] + costs[interval] / 10 - sumUp[interval + 1]) - sumOf(index + 1, interval), costs[index]);
    }

    int preIndex(int val, int cur) {
        int i;
        for (i = cur - 1; i >= 0 && costs[i] != val; i--) ;
        return i;
    }

    /**
     * @author dalt
     * @see AutoCloseable
     * @since java1.7
     */
    static class AcmInputReader implements AutoCloseable {
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
