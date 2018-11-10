package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Contest482D {
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
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\Contest482D.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        BNode[] roots = new BNode[100001];
        boolean[] exists = new boolean[100001];
        for (int i = 1; i <= 100000; i++) {
            roots[i] = new BNode();
        }

        int query = input.nextInteger();
        for (int i = 1; i <= query; i++) {
            int t = input.nextInteger();
            if (t == 1) {
                int a = input.nextInteger();
                if (exists[a]) {
                    continue;
                }
                exists[a] = true;

                for (int j = 1; j * j <= a; j++) {
                    if (a % j == 0) {
                        BNode.add(roots[j], a);
                        if (j * j != a) {
                            BNode.add(roots[a / j], a);
                        }
                    }
                }
            } else {
                int x = input.nextInteger();
                int k = input.nextInteger();
                int s = input.nextInteger();

                if (x % k != 0) {
                    output.println("-1");
                    continue;
                }

                int m = s - x;
                int max = BNode.max(roots[k], x, m, 16, 0);

                output.println(max);
            }
        }
    }

    public static class BNode {
        BNode[] nodes = new BNode[2];

        public static void add(BNode root, int n) {
            BNode trace = root;
            for (int i = 16; i >= 0; i--) {
                trace = trace.ensureExists((n & (1 << i)) == 0 ? 0 : 1);
            }
        }

        public BNode ensureExists(int i) {
            if (nodes[i] == null) {
                nodes[i] = new BNode();
            }
            return nodes[i];
        }

        public static int max(BNode root, int x, int m, int bit, int cur) {
            if (root == null || cur > m) {
                return -1;
            }
            if (bit == -1) {
                return cur;
            }
            if (((1 << bit) & x) == 0) {
                int v = max(root.nodes[1], x, m, bit - 1, cur + (1 << bit));
                if (v == -1) {
                    v = max(root.nodes[0], x, m, bit - 1, cur);
                }
                return v;
            } else {
                int v = max(root.nodes[0], x, m, bit - 1, cur);
                if (v == -1) {
                    v = max(root.nodes[1], x, m, bit - 1, cur + (1 << bit));
                }
                return v;
            }
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
