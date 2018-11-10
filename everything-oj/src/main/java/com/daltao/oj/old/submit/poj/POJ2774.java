package com.daltao.oj.old.submit.poj;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalt on 2018/6/1.
 */
public class POJ2774 {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static BlockReader input;
    public static PrintStream output;
    public static Debug debug;
    static char[] a = new char[100000];
    static char[] b = new char[100000];

    public static void main(String[] args) throws FileNotFoundException {
        init();

        solve();

        destroy();
    }

    public static void init() throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\poj\\POJ2774.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        while (input.hasMore()) {
            solveOnce();
        }
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }

    public static void solveOnce() {
        int alen = input.nextBlock(a, 0);
        int blen = input.nextBlock(b, 0);

        SuffixAutomaton automaton = new SuffixAutomaton();
        for (int i = 0; i < alen; i++) {
            automaton.build(a[i]);
        }

        int max = 0;
        automaton.beginMatch();
        for (int i = 0; i < blen; i++) {
            automaton.match(b[i]);
            max = Math.max(max, automaton.matchLength);
        }

        output.println(max);
    }

    public static class SuffixAutomaton {
        static final int MIN_CHARACTER = 'a';
        static final int MAX_CHARACTER = 'z';
        static final int RANGE_SIZE = MAX_CHARACTER - MIN_CHARACTER + 1;
        Node root;
        Node buildLast;
        Node matchLast;
        int matchLength;

        public SuffixAutomaton() {
            buildLast = root = new Node();
            root.fail = null;
        }

        public void beginMatch() {
            matchLast = root;
            matchLength = 0;
        }

        public void match(char c) {
            int index = c - MIN_CHARACTER;
            if (matchLast.next[index] != null) {
                matchLast = matchLast.next[index];
                matchLength = matchLength + 1;
                return;
            }
            while (matchLast != null && matchLast.next[index] == null) {
                matchLast = matchLast.fail;
            }
            if (matchLast == null) {
                matchLast = root;
                matchLength = 0;
            } else {
                matchLength = matchLast.maxlen + 1;
                matchLast = matchLast.next[index];
            }
        }

        public void build(char c) {
            int index = c - MIN_CHARACTER;
            Node now = new Node();
            now.maxlen = buildLast.maxlen + 1;

            Node p = visit(index, buildLast, null, now);
            if (p == null) {
                now.fail = root;
            } else {
                Node q = p.next[index];
                if (q.maxlen == p.maxlen + 1) {
                    now.fail = q;
                } else {
                    Node clone = q.clone();
                    clone.maxlen = p.maxlen + 1;

                    now.fail = q.fail = clone;
                    visit(index, p, q, clone);
                }
            }

            buildLast = now;
        }

        public Node visit(int index, Node trace, Node target, Node replacement) {
            while (trace != null && trace.next[index] == target) {
                trace.next[index] = replacement;
                trace = trace.fail;
            }
            return trace;
        }

        public static class Node implements Cloneable {
            Node[] next = new Node[RANGE_SIZE];
            Node fail;
            int maxlen;

            @Override
            public Node clone() {
                try {
                    Node res = (Node) super.clone();
                    res.next = res.next.clone();
                    return res;
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static class Debug {
        boolean debug = System.getProperty("ONLINE_JUDGE") == null;
        Deque<ModuleRecorder> stack = new ArrayDeque();
        Map<String, Module> fragmentMap = new HashMap();

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
