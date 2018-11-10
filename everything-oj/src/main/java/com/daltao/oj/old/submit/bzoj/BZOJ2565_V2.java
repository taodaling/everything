package com.daltao.oj.old.submit.bzoj;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalt on 2018/6/1.
 */
public class BZOJ2565_V2 {
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
        int[] dpL = new int[len + 1];
        int[] dpR = new int[len + 1];
        char[] revData = new char[len];
        for (int i = len - 1; i >= 0; i--) {
            revData[i] = data[len - i - 1];
        }

        Hash normalHash = new Hash(data, 31);
        Hash revHash = new Hash(revData, 31);


        for (int i = len - 1; i >= 0; i--) {
            int since = Math.min(i + 1 + dpL[i + 1], len - 1);
            while (normalHash.hash(i, since) != revHash.hash(len - since - 1, len - i - 1)) {
                since--;
            }
            dpL[i] = since - i + 1;
        }

        dpR[0] = 1;
        for (int i = 1; i < len; i++) {
            int since = Math.max(0, i - 1 - dpR[i - 1]);
            while (normalHash.hash(since, i) != revHash.hash(len - i - 1, len - since - 1)) {
                since++;
            }
            dpR[i] = i - since + 1;
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

    public static class Hash {
        private static int MOD = (int) (1e9 + 7);
        private int[] inverse;
        private int[] hash;
        private char[] data;
        private int n;
        private int x;

        public Hash(char[] data, int x) {
            n = data.length;
            this.data = data;
            inverse = new int[n];
            this.x = x;
            inverse[0] = 1;
            long inv = pow(x, MOD - 2);
            for (int i = 1; i < n; i++) {
                this.inverse[i] = (int) (this.inverse[i - 1] * inv % MOD);
            }

            hash = new int[n];
            hash[n - 1] = data[n - 1];
            long baseInc = 1;
            for (int i = n - 2; i >= 0; i--) {
                baseInc = baseInc * x % MOD;
                hash[i] = (int) ((hash[i + 1] + data[i] * baseInc) % MOD);
            }
        }

        public static long pow(int x, int n) {
            int bit = 31 - Integer.numberOfLeadingZeros(n);
            long product = 1;
            for (; bit >= 0; bit--) {
                product = product * product % MOD;
                if (((1 << bit) & n) != 0) {
                    product = product * x % MOD;
                }
            }
            return product;
        }

        public int hash(int l, int r) {
            long hash = this.hash[l];
            if (r < n - 1) {
                hash = hash - this.hash[r + 1];
                if (hash < 0) {
                    hash += MOD;
                }
                hash = hash * inverse[n - 1 - r] % MOD;
            }
            return (int) hash;
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
