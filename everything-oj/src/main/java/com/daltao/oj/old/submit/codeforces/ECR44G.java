package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalt on 2018/6/1.
 */
public class ECR44G {
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
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\ECR44G.in"));
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
        int a = input.nextInteger();
        int b = input.nextInteger();
        int c = input.nextInteger();

        Segment root = Segment.build(0, n);

        final int LESS_THAN = 0;
        final int GREATER_THAN = 1;
        int[][] compatible = new int[n][2];

        long[] choose1 = new long[n + 1];
        long[] choose2 = new long[n + 1];
        for (int i = 0; i <= n; i++) {
            choose1[i] = i;
            choose2[i] = (long) i * (i - 1) / 2;
        }

        for (int i = 0; i < n; i++) {
            compatible[i][0] = i;
            compatible[i][1] = n - i - 1;
        }

        long sum = 0;
        long[][] mod = new long[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };
        for (int i = 0; i < m; i++) {
            int u = input.nextInteger();
            int v = input.nextInteger();

            if (u > v) {
                int tmp = u;
                u = v;
                v = tmp;
            }

            compatible[u][GREATER_THAN]--;
            compatible[v][LESS_THAN]--;

            Segment.update(0, u - 1, mod[0], 0, n, root);
            Segment.update(u + 1, v - 1, mod[1], 0, n, root);
            Segment.update(v + 1, n, mod[2], 0, n, root);
        }

        for (int i = 0; i < n; i++) {
            long[] val = Segment.query(i, 0, n, root);

            //As min
            sum += (choose2[compatible[i][GREATER_THAN]] - val[0]) * a * i;
            //As middle
            sum += (choose1[compatible[i][LESS_THAN]] - val[1]) * choose1[compatible[i][GREATER_THAN]] * b * i;
            //As max
            sum += (choose2[compatible[i][LESS_THAN]] - val[2]) * c * i;
        }

        BigDecimal decimal = new BigDecimal(sum);
        if (decimal.signum() < 0) {
            decimal.add(new BigDecimal(2).pow(64));
        }
        output.println(decimal.toPlainString());
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

    public static class Segment {
        Segment left;
        Segment right;
        long[] conflict = new long[3]; //As min, as mid, as max
        long[] modify = new long[3];
        boolean dirty;

        public static Segment build(int l, int r) {
            Segment segment = new Segment();
            if (l != r) {
                int m = (l + r) >> 1;
                segment.left = build(l, m);
                segment.right = build(m + 1, r);
                segment.pushUp();
            }
            return segment;
        }

        public static void update(int f, int t, long[] mod, int l, int r, Segment segment) {
            if (f > r || t < l) {
                return;
            }
            if (f <= l && r <= t) {
                segment.mod(mod);
                return;
            }
            int m = (l + r) >> 1;

            segment.pushDown();
            update(f, t, mod, l, m, segment.left);
            update(f, t, mod, m + 1, r, segment.right);
            segment.pushUp();
        }

        public static long[] query(int x, int l, int r, Segment segment) {
            if (x > r || x < l) {
                return null;
            }
            if (x <= l && r <= x) {
                return segment.conflict;
            }
            int m = (l + r) >> 1;

            segment.pushDown();

            long[] v = query(x, l, m, segment.left);
            if (v == null) {
                v = query(x, m + 1, r, segment.right);
            }
            return v;
        }

        public void mod(long[] mod) {
            for (int i = 0; i < 3; i++) {
                modify[i] += mod[i];
                conflict[i] += mod[i];
            }
            dirty = true;
        }

        public void pushDown() {
            if (dirty) {
                dirty = false;
                left.mod(modify);
                right.mod(modify);
                for (int i = 0; i < 3; i++) {
                    modify[i] = 0;
                }
            }
        }

        public void pushUp() {
        }
    }

}
