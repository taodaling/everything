package com.daltao.oj.old.submit.bzoj;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalt on 2018/6/1.
 */
public class BZOJ2565_V3 {
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
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\bzoj\\BZOJ2565.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        char[] data = input.nextBlock().toCharArray();
        int len = data.length;
        int[] dpL = new int[len];
        int[] dpR = new int[len];

        PalindromeAutomaton automaton = new PalindromeAutomaton(len);
        for (int i = 0; i < len; i++) {
            automaton.build(data[i]);
            dpR[i] = automaton.buildLast.len;
        }

        PalindromeAutomaton invautomaton = new PalindromeAutomaton(len);
        for (int i = len - 1; i >= 0; i--) {
            invautomaton.build(data[i]);
            dpL[i] = invautomaton.buildLast.len;
        }

        int max = 0;
        for (int i = 0, until = len - 1; i < until; i++) {
            max = Math.max(max, dpR[i] + dpL[i + 1]);
        }

        output.println(max);
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }

    public static class PalindromeAutomaton {
        static final int MIN_CHARACTER = 'a';
        static final int MAX_CHARACTER = 'z';
        static final int RANGE_SIZE = MAX_CHARACTER - MIN_CHARACTER + 1;

        Node odd;
        Node even;

        char[] data;
        int size;
        Node buildLast;

        public PalindromeAutomaton(int cap) {
            data = new char[cap];
            size = 0;

            odd = new Node();
            odd.len = -1;

            even = new Node();
            even.fail = odd;
            even.len = 0;

            buildLast = odd;
        }

        public void build(char c) {
            data[size++] = c;

            int index = c - MIN_CHARACTER;

            Node trace = buildLast;
            while (size - 2 - trace.len < 0) {
                trace = trace.fail;
            }

            while (data[size - trace.len - 2] != c) {
                trace = trace.fail;
            }

            if (trace.next[index] != null) {
                buildLast = trace.next[index];
                return;
            }

            Node now = new Node();
            now.len = trace.len + 2;
            trace.next[index] = now;

            if (now.len == 1) {
                now.fail = even;
            } else {
                trace = trace.fail;
                while (data[size - trace.len - 2] != c) {
                    trace = trace.fail;
                }
                now.fail = trace.next[index];
            }

            buildLast = now;
        }

        public static class Node {
            Node[] next = new Node[RANGE_SIZE];
            Node fail;
            int len;
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
