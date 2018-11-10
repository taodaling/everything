package com.daltao.oj.old.submit.bzoj;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by dalt on 2018/5/20.
 */
public class BZOJ2286_2 {
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
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        int n = input.nextInteger();
        LCTNode[] nodes = new LCTNode[n + 1];
        int[][] edges = new int[n][3];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new LCTNode();
            nodes[i].id = i;
        }
        for (int i = 1; i < n; i++) {
            edges[i][0] = input.nextInteger();
            edges[i][1] = input.nextInteger();
            edges[i][2] = input.nextInteger();
            LCTNode a = nodes[edges[i][0]];
            LCTNode b = nodes[edges[i][1]];
            a.children.add(b);
            b.children.add(a);
        }

        Comparator<LCTNode> orderCmp = new Comparator<LCTNode>() {
            @Override
            public int compare(LCTNode o1, LCTNode o2) {
                return o1.order - o2.order;
            }
        };
        Deque<LCTNode> queue = new ArrayDeque<LCTNode>(n);
        queue.add(nodes[1]);
        nodes[1].depth = 0;
        int order = 0;
        while (!queue.isEmpty()) {
            LCTNode head = queue.removeLast();
            head.order = order++;
            for (LCTNode child : head.children) {
                if (child.depth != INF) {
                    continue;
                }
                child.depth = head.depth + 1;
                queue.addLast(child);
            }
        }

        for (int i = 1; i < n; i++) {
            LCTNode edge = new LCTNode();
            edge.weight = edges[i][2];
            LCTNode.join(nodes[edges[i][0]], edge);
            LCTNode.join(edge, nodes[edges[i][1]]);
        }

        ObjectList<LCTNode> objectList = new ObjectList<LCTNode>(new LCTNode[n]);
        int m = input.nextInteger();
        for (int i = 1; i <= m; i++) {
            int k = input.nextInteger();
            objectList.clear();
            for (int j = 1; j <= k; j++) {
                LCTNode node = nodes[input.nextInteger()];
                if (node.version != i) {
                    node.init(i);
                    node.containResource = true;
                    objectList.add(node);
                }
            }
            objectList.sort(orderCmp);
            for (int j = 1, until = objectList.size(); j < until; j++) {
                LCTNode a = objectList.get(j - 1);
                LCTNode b = objectList.get(j);

                LCTNode lca = LCTNode.lca(a, b);
                if (lca.version != i) {
                    lca.init(i);
                    objectList.add(lca);
                }
            }
            if (nodes[1].version != i) {
                nodes[1].init(i);
                objectList.add(nodes[1]);
            }
            objectList.sort(orderCmp);

            queue.clear();
            queue.addLast(objectList.get(0));
            for (int j = 1, until = objectList.size(); j < until; j++) {
                LCTNode node = objectList.get(j);
                for (LCTNode last = queue.peekLast(); LCTNode.lca(last, node) != last; queue.pollLast(), last = queue.peekLast())
                    ;
                node.virtualFather = queue.peekLast();
                queue.addLast(node);
            }

            for (int j = objectList.size() - 1; j >= 1; j--) {
                LCTNode node = objectList.get(j);
                LCTNode.findRoute(node, node.virtualFather);
                LCTNode.splay(node);
                if (node.containResource) {
                    node.dp = node.minWeight;
                } else {
                    node.dp = Math.min(node.dp, node.minWeight);
                }
                node.virtualFather.dp += node.dp;
            }

            output.println(nodes[1].dp);
        }
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }

    public static class LCTNode {
        public static final LCTNode NIL = new LCTNode();

        static {
            NIL.left = NIL;
            NIL.right = NIL;
            NIL.father = NIL;
            NIL.treeFather = NIL;
        }

        LCTNode left = NIL;
        LCTNode right = NIL;
        LCTNode father = NIL;
        LCTNode treeFather = NIL;
        boolean reverse;
        int depth = INF;
        LCTNode minDepthNode = this;
        int weight = INF;
        int minWeight = INF;
        int order;
        List<LCTNode> children = new ArrayList(1);
        int version;
        int id;
        LCTNode virtualFather;

        boolean containResource;
        int dp;

        public static void access(LCTNode x) {
            LCTNode last = NIL;
            while (x != NIL) {
                splay(x);
                x.right.father = NIL;
                x.right.treeFather = x;
                x.setRight(last);
                x.pushUp();

                last = x;
                x = x.treeFather;
            }
        }

        public static void makeRoot(LCTNode x) {
            access(x);
            splay(x);
            x.reverse();
        }

        public static void cut(LCTNode y, LCTNode x) {
            makeRoot(y);
            access(x);
            splay(y);
            y.right.treeFather = NIL;
            y.right.father = NIL;
            y.setRight(NIL);
            y.pushUp();
        }

        public static void join(LCTNode y, LCTNode x) {
            makeRoot(x);
            x.treeFather = y;
        }

        public static void findRoute(LCTNode x, LCTNode y) {
            makeRoot(y);
            access(x);
        }

        public static LCTNode lca(LCTNode x, LCTNode y) {
            findRoute(x, y);
            splay(x);
            return x.minDepthNode;
        }

        public static void splay(LCTNode x) {
            if (x == NIL) {
                return;
            }
            LCTNode y, z;
            while ((y = x.father) != NIL) {
                if ((z = y.father) == NIL) {
                    y.pushDown();
                    x.pushDown();
                    if (x == y.left) {
                        zig(x);
                    } else {
                        zag(x);
                    }
                } else {
                    z.pushDown();
                    y.pushDown();
                    x.pushDown();
                    if (x == y.left) {
                        if (y == z.left) {
                            zig(y);
                            zig(x);
                        } else {
                            zig(x);
                            zag(x);
                        }
                    } else {
                        if (y == z.left) {
                            zag(x);
                            zig(x);
                        } else {
                            zag(y);
                            zag(x);
                        }
                    }
                }
            }

            x.pushDown();
            x.pushUp();
        }

        public static void zig(LCTNode x) {
            LCTNode y = x.father;
            LCTNode z = y.father;
            LCTNode b = x.right;

            y.setLeft(b);
            x.setRight(y);
            z.changeChild(y, x);

            y.pushUp();
        }

        public static void zag(LCTNode x) {
            LCTNode y = x.father;
            LCTNode z = y.father;
            LCTNode b = x.left;

            y.setRight(b);
            x.setLeft(y);
            z.changeChild(y, x);

            y.pushUp();
        }

        public static LCTNode findRoot(LCTNode x) {
            x.pushDown();
            while (x.left != NIL) {
                x = x.left;
                x.pushDown();
            }
            splay(x);
            return x;
        }

        @Override
        public String toString() {
            return "" + id;
        }

        public void init(int version) {
            dp = 0;
            this.version = version;
            containResource = false;
        }

        public void pushDown() {
            if (reverse) {
                reverse = false;

                LCTNode tmpNode = left;
                left = right;
                right = tmpNode;

                left.reverse();
                right.reverse();
            }

            left.treeFather = treeFather;
            right.treeFather = treeFather;
        }

        public void reverse() {
            reverse = !reverse;
        }

        public void setLeft(LCTNode x) {
            left = x;
            x.father = this;
        }

        public void setRight(LCTNode x) {
            right = x;
            x.father = this;
        }

        public void changeChild(LCTNode y, LCTNode x) {
            if (left == y) {
                setLeft(x);
            } else {
                setRight(x);
            }
        }

        public void pushUp() {
            minDepthNode = this;
            if (minDepthNode.depth > left.minDepthNode.depth) {
                minDepthNode = left.minDepthNode;
            }
            if (minDepthNode.depth > right.minDepthNode.depth) {
                minDepthNode = right.minDepthNode;
            }

            minWeight = Math.min(Math.min(weight, left.minWeight), right.minWeight);
        }
    }

    public static class ObjectList<T> implements Iterable<T> {
        T[] data;
        int size;

        public ObjectList(T[] data) {
            this.data = data;
        }

        public T get(int i) {
            return data[i];
        }

        public void add(T x) {
            data[size++] = x;
        }

        public void clear() {
            size = 0;
        }

        public int size() {
            return size;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                T[] data = ObjectList.this.data;
                int size = ObjectList.this.size;
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < size;
                }

                @Override
                public T next() {
                    return data[index++];
                }

                @Override
                public void remove() {
                    throw new RuntimeException();
                }
            };
        }

        public void sort(Comparator<T> comparator) {
            Sortable.randomizedQuickSort(data, comparator, 0, size);
        }

        public void sort() {
            Arrays.sort(data, 0, size);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < size; i++) {
                builder.append(data[i]).append(',');
            }
            builder.setCharAt(builder.length() - 1, ']');
            return builder.toString();
        }
    }

    public static class Debug {
        boolean debug = System.getProperty("ONLINE_JUDGE") == null;
        Deque<ModuleRecorder> stack = new ArrayDeque<ModuleRecorder>();
        Map<String, Module> fragmentMap = new HashMap<String, Module>();

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


    /**
     * Created by dalt on 2018/5/20.
     */
    public static class St<T> {
        //st[i][j] means the min value between [i, i + 2^j),
        //so st[i][j] equals to min(st[i][j - 1], st[i + 2^(j - 1)][j - 1])
        Object[][] st;
        Comparator<T> comparator;

        private St() {
        }

        public St(Object[] data, int length, Comparator<T> comparator) {
            int m = floorLog2(length);
            st = new Object[length][m + 1];
            this.comparator = comparator;
            for (int i = 0; i < length; i++) {
                st[i][0] = data[i];
            }
            for (int i = 0; i < m; i++) {
                int interval = 1 << i;
                for (int j = 0; j < length; j++) {
                    if (j + interval < length) {
                        st[j][i + 1] = min((T) st[j][i], (T) st[j + interval][i]);
                    } else {
                        st[j][i + 1] = st[j][i];
                    }
                }
            }
        }

        public static int floorLog2(int x) {
            return 31 - Integer.numberOfLeadingZeros(x);
        }

        private T min(T a, T b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }

        public static int ceilLog2(int x) {
            return 32 - Integer.numberOfLeadingZeros(x - 1);
        }

        /**
         * query the min value in [left,right]
         */
        public T query(int left, int right) {
            int queryLen = right - left + 1;
            int bit = floorLog2(queryLen);
            //x + 2^bit == right + 1
            //So x should be right + 1 - 2^bit - left=queryLen - 2^bit
            return min((T) st[left][bit], (T) st[right + 1 - (1 << bit)][bit]);
        }
    }


    /**
     * Created by dalt on 2018/5/20.
     */
    public static class Sortable {
        private static final int THRESHOLD = 4;

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
    }

}
