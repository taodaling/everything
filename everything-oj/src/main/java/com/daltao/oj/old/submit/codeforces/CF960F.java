package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalt on 2018/5/2.
 */
public class CF960F {
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
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF960F.in"));
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

        SpNode[] roots = new SpNode[n + 1];

        for (int i = 1; i <= n; i++) {
            roots[i] = SpNode.NIL;
        }

        SpNode[] max = new SpNode[1];
        for (int i = 1; i <= m; i++) {
            int a = input.nextInteger();
            int b = input.nextInteger();
            int w = input.nextInteger();
            roots[a] = SpNode.foundMaxValueNodeLessThan(roots[a], w, max);
            roots[b] = SpNode.add(roots[b], w, max[0].value + 1, a);
        }

        int maxLength = 0;
        for (int i = 1; i <= n; i++) {
            maxLength = Math.max(maxLength, roots[i].maxNode.value);
        }

        output.println(maxLength);
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }

    public static class SpNode {
        final static SpNode NIL = new SpNode();

        static {
            NIL.father = NIL;
            NIL.left = NIL;
            NIL.right = NIL;
            NIL.maxNode = NIL;
        }

        SpNode left = NIL;
        SpNode right = NIL;
        SpNode father = NIL;
        int key; //The max of path
        int value; //The length of path
        int extra; //The last node in path
        SpNode maxNode = this; //The node with max value in subtree

        public static void splay(SpNode x) {
            if (x == NIL) {
                return;
            }

            SpNode y, z;
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

        public static void zig(SpNode x) {
            SpNode y = x.father;
            SpNode z = y.father;
            SpNode b = x.right;

            z.replace(y, x);
            x.setRight(y);
            y.setLeft(b);

            y.pushUp();
        }

        public static void zag(SpNode x) {
            SpNode y = x.father;
            SpNode z = y.father;
            SpNode b = x.left;

            z.replace(y, x);
            x.setLeft(y);
            y.setRight(b);

            y.pushUp();
        }

        public static SpNode add(SpNode root, int key, int value, int extra) {
            SpNode newNode = new SpNode();
            newNode.key = key;
            newNode.value = value;
            newNode.extra = extra;

            if (root == NIL) {
                newNode.pushUp();
                return newNode;
            }

            SpNode father = NIL;
            SpNode trace = root;
            while (trace != NIL) {
                father = trace;
                trace.pushDown();
                if (trace.key < key) {
                    trace = trace.right;
                } else {
                    trace = trace.left;
                }
            }


            if (father.key < key) {
                father.setRight(newNode);
            } else {
                father.setLeft(newNode);
            }

            splay(newNode);
            return newNode;
        }

        public static SpNode foundMaxValueNodeLessThan(SpNode root, int key, SpNode[] result) {
            SpNode boundary = add(root, key, 0, 0);

            result[0] = boundary.left.maxNode;

            return remove(boundary);
        }

        public static SpNode remove(SpNode node) {
            splay(node);
            SpNode left = node.left;
            SpNode right = node.right;

            node.left = NIL;
            node.right = NIL;
            node.pushUp();

            left.father = NIL;
            right.father = NIL;
            if (left == NIL) {
                return right;
            }
            if (right == NIL) {
                return left;
            }

            SpNode max = left;
            max.pushDown();
            while (max.right != NIL) {
                max = max.right;
                max.pushDown();
            }

            splay(max);
            max.setRight(right);
            max.pushUp();

            return max;
        }

        @Override
        public String toString() {
            return this == NIL ? "NIL" : String.format("(%d, %d, %d)", key, value, extra);
        }

        public void pushDown() {

        }

        public void setRight(SpNode node) {
            this.right = node;
            node.father = this;
        }

        public void setLeft(SpNode node) {
            this.left = node;
            node.father = this;
        }

        public void pushUp() {
            maxNode = this;
            if (left.maxNode.value > maxNode.value) {
                maxNode = left.maxNode;
            }
            if (right.maxNode.value > maxNode.value) {
                maxNode = right.maxNode;
            }
        }

        public void replace(SpNode node, SpNode newNode) {
            if (left == node) {
                setLeft(newNode);
            } else {
                setRight(newNode);
            }
        }
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
