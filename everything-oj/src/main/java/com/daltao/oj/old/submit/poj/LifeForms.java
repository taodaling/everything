package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/12/16.
 */
public class LifeForms {
    public static BlockReader input;
    int n;
    String[] texts;

    public static void main(String[] args) throws FileNotFoundException {
        System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\LifeForms.in"));

        int testCase = 0;
        input = new BlockReader(System.in);
        int n;
        while ((n = input.nextInteger()) != 0) {
            testCase++;
            if (testCase > 1) {
                System.out.println();
            }
            LifeForms solution = new LifeForms();
            solution.init(n);
            System.out.print(solution.solve());
        }
    }

    public void init(int n) {
        this.n = n;
        texts = new String[n];
        for (int i = 0; i < n; i++) {
            texts[i] = input.nextBlock();
        }
    }

    public String solve() {
        if (n == 1) {
            return texts[0] + "\n";
        }

        StringBuilder result = new StringBuilder();
        char[] combinedTextData;
        {
            StringBuilder combinedText = new StringBuilder();
            for (int i = 0; i < n; i++) {
                combinedText.append(texts[i]).append((char) (256 + i));
            }
            combinedTextData = combinedText.toString().toCharArray();
        }
        byte[] ids = new byte[combinedTextData.length];
        {
            int posRecorder = 0;
            for (int i = 0; i < n; i++) {
                int tmp = posRecorder;
                posRecorder += texts[i].length() + 1;
                Arrays.fill(ids, tmp, posRecorder, (byte) i);
            }
        }

        SuffixArray suffixArray = SuffixArray.getInstance(combinedTextData);
        int threshold = n / 2 + 1;

        //This step calculate all nodes' preRanks
        int[] matches = suffixArray.matches;
        int[] ranks = suffixArray.ranks;
        int[] orders = suffixArray.orders;
        int[] preRanks = new int[orders.length];
        {
            LinkedNode[] registries = new LinkedNode[n];
            int registriesCnt = 0;
            LinkedList queue = new LinkedList();
            for (int i = 1, bound = combinedTextData.length; i <= bound; i++) {
                int order = orders[i];
                int id = ids[order];

                if (registries[id] == null) {
                    registriesCnt++;
                    if (registriesCnt > threshold) {
                        //Remove the first item in queue
                        LinkedNode first = queue.peekFirst();
                        queue.remove(first);
                        registries[ids[orders[first.rank]]] = null;
                        registriesCnt--;
                    }
                } else {
                    queue.remove(registries[id]);
                }

                LinkedNode node = new LinkedNode();
                node.rank = i;
                queue.addLast(node);
                registries[id] = node;

                if (registriesCnt == threshold) {
                    preRanks[i] = queue.peekFirst().rank;
                } else {
                    preRanks[i] = -1;
                }
            }
        }

        //Then use the preRank to find the max match degreeL with endDp
        //leftLowerRanks[i] is the rank of the first lower element in matches; so is rightLowerRanks;
        int[] leftLowerRanks = new int[ranks.length];
        int[] rightLowerRanks = new int[ranks.length];
        {
            LinkedList stack = new LinkedList();
            int bound = matches.length;
            for (int i = 0; i < bound; i++) {
                while (!stack.isEmpty()) {
                    LinkedNode last = stack.peekLast();
                    if (matches[last.rank] > matches[i]) {
                        stack.remove(last);
                        rightLowerRanks[last.rank] = i;
                    } else {
                        break;
                    }
                }

                LinkedNode node = new LinkedNode();
                node.rank = i;
                stack.addLast(node);
            }
            while (!stack.isEmpty()) {
                LinkedNode last = stack.peekLast();
                stack.remove(last);
                rightLowerRanks[last.rank] = bound;
            }

            for (int i = bound - 1; i >= 0; i--) {
                while (!stack.isEmpty()) {
                    LinkedNode last = stack.peekLast();
                    if (matches[last.rank] > matches[i]) {
                        stack.remove(last);
                        leftLowerRanks[last.rank] = i;
                    } else {
                        break;
                    }
                }

                LinkedNode node = new LinkedNode();
                node.rank = i;
                stack.addLast(node);
            }
            while (!stack.isEmpty()) {
                LinkedNode last = stack.peekLast();
                stack.remove(last);
                leftLowerRanks[last.rank] = -1;
            }
        }

        //Calculate all match degreeL in each interval, and record the max one
        int maxMatchDegree = 0;
        int[] lowerRanks = new int[matches.length];
        {
            int lowerRank = matches.length - 1;
            for (int i = matches.length - 1; i >= 1; i--) {
                lowerRank = Math.min(lowerRank, i);
                int preRank = preRanks[i];
                while (leftLowerRanks[lowerRank] > preRank ||
                        rightLowerRanks[lowerRank] <= i) {
                    lowerRank--;
                }
                lowerRanks[i] = lowerRank;
                maxMatchDegree = Math.max(maxMatchDegree, matches[lowerRank]);
            }
        }

        if (maxMatchDegree == 0) {
            return "?\n";
        }

        int lastRightCover = -1;
        for (int i = 1, bound = matches.length; i < bound; i++) {
            int lowerRank = lowerRanks[i];
            if (lowerRank >= lastRightCover && matches[lowerRank] == maxMatchDegree) {
                result.append(combinedTextData, orders[lowerRank], maxMatchDegree).append('\n');
                lastRightCover = rightLowerRanks[lowerRanks[i]];
            }
        }

        return result.toString();
    }

    public static class LinkedNode {
        LinkedNode former;
        LinkedNode later;
        int rank;
    }

    public static class LinkedList {
        LinkedNode dummy = new LinkedNode();

        {
            dummy.former = dummy.later = dummy;
        }

        public static void addAfter(LinkedNode former, LinkedNode later) {
            later.former = former;
            later.later = former.later;
            later.later.former = later;
            former.later = later;
        }

        public static void removeLater(LinkedNode node) {
            node.later = node.later.later;
            node.later.former = node;
        }

        public void addLast(LinkedNode node) {
            addAfter(dummy.former, node);
        }

        public LinkedNode peekFirst() {
            return dummy.later;
        }

        public boolean isEmpty() {
            return dummy.later == dummy;
        }

        public void remove(LinkedNode node) {
            removeLater(node.former);
        }

        public LinkedNode peekLast() {
            return dummy.former;
        }
    }

    public static class SuffixArray {
        public int[] ranks;
        public int[] matches;
        public int[] orders;

        private SuffixArray() {
        }

        public static SuffixArray getInstance(char[] data) {
            int n = data.length + 1;
            Loop<int[]> ordersLoop = new Loop(new int[2][n]);
            Loop<int[]> ranksLoop = new Loop(new int[3][n]);

            Integer[] intWrapper = new Integer[n];
            final int[] originalRanks = ranksLoop.get(0);
            for (int i = 0, bound = n - 1; i < bound; i++) {
                intWrapper[i] = i;
                originalRanks[i] = data[i];
            }
            intWrapper[n - 1] = n - 1;
            originalRanks[n - 1] = 0;
            Arrays.sort(intWrapper, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return originalRanks[o1] - originalRanks[o2];
                }
            });
            int[] originalOrders = ordersLoop.get(0);
            for (int i = 0; i < n; i++) {
                originalOrders[i] = intWrapper[i];
            }
            resetRanks(originalOrders, originalRanks, originalRanks, ranksLoop.turn(1));

            for (int i = 1; i < n; i <<= 1) {
                int[] key2 = ranksLoop.get(1);
                System.arraycopy(ranksLoop.get(0), i, key2, 0, n - i - 1);
                Arrays.fill(key2, n - i - 1, n, 0);
                radixSort(ordersLoop.get(0), ordersLoop.turn(1), key2, ranksLoop.get(2));
                radixSort(ordersLoop.get(0), ordersLoop.turn(1), ranksLoop.get(0), ranksLoop.get(2));
                resetRanks(ordersLoop.get(0), ranksLoop.get(0), ranksLoop.get(1), ranksLoop.turn(2));
            }

            int[] ranks = ranksLoop.get(0);
            int[] orders = ordersLoop.get(0);
            int[] matches = ranksLoop.get(1);
            matches[0] = 0;
            int lastMatch = 0;
            for (int i = 0, bound = n - 1; i < bound; i++) {
                lastMatch = Math.max(0, lastMatch - 1);
                int curRank = ranks[i];
                int lastRank = curRank - 1;
                int lastOrder = orders[lastRank];
                //lastOrder + lastMatch < n - 1 and i + lastMatch < n -1, so lastMatch < n - 1 - max(lastOrder, i)
                for (int until = n - 1 - Math.max(lastOrder, i); lastMatch < until && data[lastMatch + i] == data[lastMatch + lastOrder]; lastMatch++)
                    ;
                matches[curRank] = lastMatch;
            }

            SuffixArray suffixArray = new SuffixArray();
            suffixArray.matches = matches;
            suffixArray.orders = orders;
            suffixArray.ranks = ranks;
            return suffixArray;
        }

        public static void resetRanks(int[] orders, int[] key1, int[] key2, int[] outputRanks) {
            int cnt = 0;
            outputRanks[orders[0]] = 0;
            for (int i = 1, bound = orders.length; i < bound; i++) {
                if (key1[orders[i]] != key1[orders[i - 1]] || key2[orders[i]] != key2[orders[i - 1]]) {
                    cnt++;
                }
                outputRanks[orders[i]] = cnt;
            }
        }

        public static void radixSort(int[] originalOrders, int[] outputOrders, int[] ranks, int[] memo) {
            int[] cnts = memo;
            Arrays.fill(cnts, 0);
            for (int rank : ranks) {
                cnts[rank]++;
            }
            for (int i = 1, bound = cnts.length; i < bound; i++) {
                cnts[i] += cnts[i - 1];
            }
            for (int i = originalOrders.length - 1; i >= 0; i--) {
                outputOrders[--cnts[ranks[originalOrders[i]]]] = originalOrders[i];
            }
        }
    }

    public static class Loop<T> {
        T[] data;
        int head;

        public Loop(T... data) {
            this.data = data;
        }

        public T get(int offset) {
            return data[(head + offset) % data.length];
        }

        public T turn(int offset) {
            head += offset;
            return get(0);
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
