package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.util.*;

public class CF986A {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static BlockReader input;
    public static PrintStream output;
    public static Debug debug;

    public static void main(String[] args) throws FileNotFoundException {

        init();

        solve();

        destroy();
    }

    public static void init() throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF986A.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        int n = input.nextInteger();
        int m = input.nextInteger();
        int k = input.nextInteger();
        int s = input.nextInteger();

        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node(i);
            int goods = input.nextInteger();
            nodes[i].dp[goods] = 0;
        }

        for (int i = 0; i < m; i++) {
            Node a = nodes[input.nextInteger()];
            Node b = nodes[input.nextInteger()];
            a.children.add(b);
            b.children.add(a);
        }

        Deque<Node> deque = new ArrayDeque<>(n);
        for (int i = 1; i <= k; i++) {
            for (int j = 1; j <= n; j++) {
                if (nodes[j].dp[i] != INF) {
                    deque.addLast(nodes[j]);
                }
            }
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                for (Node child : head.children) {
                    if (child.dp[i] != INF) {
                        continue;
                    }
                    child.dp[i] = head.dp[i] + 1;
                    deque.addLast(child);
                }
            }
        }

        for (int i = 1; i <= n; i++) {
            Node node = nodes[i];
            Randomized.randomizedArray(node.dp, 1, k);
            Sortable.theKthElement(node.dp, 1, k + 1, s);

            int sum = 0;
            for (int j = 1; j <= s; j++) {
                sum += node.dp[j];
            }
            output.print(sum);
            output.print(' ');
        }
    }

    public static class Node {
        List<Node> children = new ArrayList(1);
        int[] dp = new int[101];
        int id;

        @Override
        public String toString() {
            return "" + id;
        }

        public Node(int id) {
            this.id = id;
            Arrays.fill(dp, INF);
        }
    }

    /**
     * Created by dalt on 2018/6/1.
     */
    public static class Randomized {
        static Random random = new Random();

        public static double nextDouble(double min, double max) {
            return random.nextDouble() * (max - min) + min;
        }

        public static void randomizedArray(int[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                int tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(long[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                long tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(double[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                double tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static void randomizedArray(float[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                float tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static <T> void randomizedArray(T[] data, int from, int to) {
            to--;
            for (int i = from; i <= to; i++) {
                int s = nextInt(i, to);
                T tmp = data[i];
                data[i] = data[s];
                data[s] = tmp;
            }
        }

        public static int nextInt(int l, int r) {
            return random.nextInt(r - l + 1) + l;
        }
    }

    public static class Sortable {
        private static final int THRESHOLD = 4;

        public static int theKthElement(int[] data, int f, int t, int k) {
            int l = f;
            int r = t;
            int rule = data[l];
            for (int i = f + 1; i < r; ) {
                if (data[i] > rule) {
                    swap(data, i, r - 1);
                    r--;
                } else if (data[i] < rule) {
                    swap(data, i, l);
                    l++;
                    i++;
                } else {
                    i++;
                }
            }

            if (k <= l - f) {
                return theKthElement(data, f, l, k);
            } else if (k > r - f) {
                return theKthElement(data, r, t, k - (r - f));
            }
            return rule;
        }

        public static void swap(int[] data, int i, int j) {
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> void randomizedQuickSort(T[] data, Comparator<T> cmp, int f, int t) {
            Random random = new Random();
            int len = t - f;
            for (int i = len - 1; i >= 0; i--) {
                int rand = random.nextInt(i + 1);
                swap(data, f + rand, f + i);
            }
            quickSort(data, cmp, f, t);
        }

        public static <T> void quickSort(T[] data, Comparator<T> cmp, int f, int t) {
            if (t - f < 2) {
                //insertSort(data, cmp, f, t);
                return;
            }
            T rule = data[f];
            int l = f;
            int r = t;
            for (int i = f + 1; i < r; ) {
                int cmpRes = cmp.compare(data[i], rule);
                if (cmpRes < 0) {
                    swap(data, l++, i);
                } else if (cmpRes > 0) {
                    swap(data, --r, i);
                } else {
                    i++;
                }
            }
            quickSort(data, cmp, f, l);
            quickSort(data, cmp, r, t);
        }

        public static <T> void mergeSort(T[] data, Comparator<T> cmp, int f, int t, T[] buf) {
            if (t - f < THRESHOLD) {
                insertSort(data, cmp, f, t);
                return;
            }

            int m = (f + t) >> 1;
            mergeSort(data, cmp, f, m, buf);
            mergeSort(data, cmp, m, t, buf);

            int lIndex = f;
            int rIndex = m;
            int bIndex = 0;
            while (lIndex < m && rIndex < t) {
                int cmpRes = cmp.compare(data[lIndex], data[rIndex]);
                if (cmpRes <= 0) {
                    buf[bIndex++] = data[lIndex++];
                } else {
                    buf[bIndex++] = data[rIndex++];
                }
            }
            while (lIndex < m) {
                buf[bIndex++] = data[lIndex++];
            }
            while (rIndex < m) {
                buf[bIndex++] = data[rIndex++];
            }

            System.arraycopy(buf, 0, data, f, bIndex);
        }

        public static <T> void selectSort(T[] data, Comparator<T> cmp, int f, int t) {
            for (int i = f; i < t; i++) {
                int minIndex = i;
                for (int j = i + 1; j < t; j++) {
                    if (cmp.compare(data[minIndex], data[j]) > 0) {
                        minIndex = j;
                    }
                }
                swap(data, minIndex, i);
            }
        }

        public static <T> void swap(T[] data, int i, int j) {
            T tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }

        public static <T> void insertSort(T[] data, Comparator<T> cmp, int f, int t) {
            for (int i = f + 1; i < t; i++) {
                T v = data[i];
                int j = i - 1;
                while (j >= f && cmp.compare(v, data[j]) < 0) {
                    data[j + 1] = data[j];
                    j--;
                }
                data[j + 1] = v;
            }
        }

        public static void radixSort(int[] data, int from, int to, int bit, int[] buf, int bufFrom, int[] cnt) {
            if (cnt == null || cnt.length < (1 << bit)) {
                cnt = new int[1 << bit];
            }

            int sortTime = (31 + bit - 1) / bit;
            for (int i = 0; i < sortTime; i++) {
                radixSort0(data, from, to, cnt, bit * i, bit * (i + 1), buf, bufFrom);
                System.arraycopy(buf, bufFrom, data, from, to - from);
            }
        }

        private static void radixSort0(int[] data, int from, int to, int[] cnt, int bitFrom, int bitTo, int[] output, int outputFrom) {
            Arrays.fill(cnt, 0);

            int mask = (1 << (bitTo - bitFrom)) - 1;
            for (int i = from; i < to; i++) {
                cnt[getInt(data[i], bitFrom, mask)]++;
            }

            for (int i = 1, until = cnt.length; i < until; i++) {
                cnt[i] += cnt[i - 1];
            }

            for (int i = to - 1; i >= from; i--) {
                int bitval = getInt(data[i], bitFrom, mask);
                output[outputFrom + (--cnt[bitval])] = data[i];
            }
        }

        private static int getInt(int val, int shiftRight, int mask) {
            return (val >> shiftRight) & mask;
        }
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }


    public static class Debug {
        boolean debug = System.getProperty("ONLINE_JUDGE") == null;
        Deque<ModuleRecorder> stack = new ArrayDeque<>();
        Map<String, Module> fragmentMap = new HashMap<>();

        public void enter(String module) {
            if (debug) {
                stack.push(new ModuleRecorder(getModule(module)));
            }
        }

        public Module getModule(String moduleName) {
            Module module = fragmentMap.get(moduleName);
            if (module == null) {
                module = new Module(moduleName);
                fragmentMap.put(moduleName, module);
            }
            return module;
        }

        public void exit() {
            if (debug) {
                ModuleRecorder fragment = stack.pop();
                fragment.exit();
            }
        }

        public void statistic() {
            if (!debug) {
                return;
            }

            if (stack.size() > 0) {
                throw new RuntimeException("Exist unexited tag");
            }
            System.out.println("\n------------------------------------------");

            System.out.println("memory used " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");

            System.out.println("\n------------------------------------------");
            for (Module module : fragmentMap.values()) {
                System.out.println(String.format("Module %s : enter %d : cost %d", module.moduleName, module.enterTime, module.totaltime));
            }

            System.out.println("------------------------------------------");
        }

        public static class ModuleRecorder {
            Module fragment;
            long time;

            public ModuleRecorder(Module fragment) {
                this.fragment = fragment;
                time = System.currentTimeMillis();
            }

            public void exit() {
                fragment.totaltime += System.currentTimeMillis() - time;
                fragment.enterTime++;
            }
        }

        public static class Module {
            String moduleName;
            long totaltime;
            long enterTime;

            public Module(String moduleName) {
                this.moduleName = moduleName;
            }
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