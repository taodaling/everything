package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dalt on 2018/4/2.
 */
public class CF914E {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static BlockReader input;
    public static PrintStream output;
    public static Debug debug;
    public static int[] registries = new int[1 << 20];
    public static int[] registries2 = new int[1 << 20];

    public static void main(String[] args) throws FileNotFoundException {
        Thread t = new Thread(null, new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                solve();

                destroy();
            }
        }, "cf", 1 << 26);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

    }

    public static void init() throws FileNotFoundException {
  /*      try (FileOutputStream fos = new FileOutputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF914E.in")) {
            StringBuilder builder = new StringBuilder();
            builder.append("200000\n");
            Random random = new Random();
            for (int i = 2; i <= 200000; i++) {
                builder.append(i).append(' ').append(random.nextInt(i - 1) + 1).append('\n');
            }
            for (int i = 1; i <= 200000; i++) {
                builder.append('a');
            }
            fos.write(builder.toString().getBytes(Charset.forName("ascii")));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        if (System.getProperty("ONLINE_JUDGE") == null) {
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF914E.in"));
            //output = System.out;
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {

        debug.enter("input");
        int n = input.nextInteger();
        Node[] nodes = new Node[n + 1];
        for (int i = 1; i <= n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }

        int[][] edges = new int[2][n + 1];
        int[] edgeCnt = new int[n + 1];
        for (int i = 1; i < n; i++) {
            edges[0][i] = input.nextInteger();
            edges[1][i] = input.nextInteger();
            edgeCnt[edges[0][i]]++;
            edgeCnt[edges[1][i]]++;
        }
        for (int i = 1; i <= n; i++) {
            nodes[i].children = new Node[edgeCnt[i]];
        }
        for (int i = 1; i < n; i++) {
            Node a = nodes[edges[0][i]];
            Node b = nodes[edges[1][i]];
            a.children[a.childrenSize++] = b;
            b.children[b.childrenSize++] = a;
        }

        char[] data = new char[n + 1];
        input.nextBlock(data, 1);
        for (int i = 1; i <= n; i++) {
            nodes[i].tag = 1 << (data[i] - 'a');
        }
        debug.exit();

        debug.enter("dp");
        dpOnTree(nodes[1]);
        debug.exit();

        debug.enter("output");
        StringBuilder result = new StringBuilder(1000000);
        for (int i = 1; i <= n; i++) {
            result.append(nodes[i].sum).append(' ');
            // output.println(nodes[i].sum);
        }
        output.println(result);
        debug.exit();
    }

    static Node[] bufListData = new Node[200000];
    static Node[] bufListData2 = new Node[200000];
    static int bufListDataSize = 0;
    static int bufListData2Size = 0;

    static int currentVersion = -1;
    static int currentVersion2 = -1;

    public static void dpOnTree(Node root) {
        if (root.childrenSize == 0) {
            return;
        }

        currentVersion++;

        bufListDataSize = 0;
        debug.enter("dfs");
        dfs(root, null);
        debug.exit();
        int total = root.size;
        int half = total >> 1;


        //Find the core
        Node core = null;
        for (int i = 0, until = bufListDataSize; i < until; i++) {
            Node node = bufListData[i];
            boolean satisfy = true;

            if (node.father != null) {
                if (node.father.size - node.size > half) {
                    satisfy = false;
                }
            }

            if (!satisfy) {
                continue;
            }

            for (int j = 0, juntil = node.childrenSize; j < juntil; j++) {
                Node child = node.children[j];
                if (child == node.father) {
                    continue;
                }
                if (child.size > half) {
                    satisfy = false;
                    break;
                }
            }

            if (!satisfy) {
                continue;
            }

            core = node;
            break;
        }

        registries[core.tag]++;
        bufListData2Size = 0;
        //DP on all child of core
        for (int j = 0, juntil = core.childrenSize; j < juntil; j++) {
            Node child = core.children[j];
            bufListDataSize = 0;
            debug.enter("dfs2");
            dfs2(child, core, 0, child);
            debug.exit();

            for (int i = 0, until = bufListDataSize; i < until; i++) {
                Node node = bufListData[i];
                bufListData2[bufListData2Size++] = node;
                node.localsum = 0;
                registries[node.pathTag ^ core.tag]++;
            }
        }

        debug.enter("main loop");
        Node lastAncestor = null;
        core.ancestor = null;
        core.pathTag = 0;
        core.localsum = 0;
        bufListData2[bufListData2Size++] = core;
        for (int i = 0, until = bufListData2Size; i < until; i++) {
            Node node = bufListData2[i];
            if (node.ancestor != lastAncestor) {
                for (int j = i - 1; j >= 0; j--) {
                    Node prevNode = bufListData2[j];
                    if (prevNode.ancestor != lastAncestor) {
                        break;
                    }
                    int localSum = count(prevNode);
                    prevNode.localsum += localSum;
                    prevNode.sum += prevNode.localsum;
                    prevNode.father.localsum += prevNode.localsum;
                }
                for (int j = i - 1; j >= 0; j--) {
                    Node prevNode = bufListData2[j];
                    if (prevNode.ancestor != lastAncestor) {
                        break;
                    }
                    registries2[node.pathTag ^ core.tag] = 0;
                }
                lastAncestor = node.ancestor;
            }

            registries2[node.pathTag ^ core.tag]++;
        }
        registries2[core.tag] = 0;
        for (int i = 0, until = bufListData2Size; i < until; i++) {
            Node node = bufListData2[i];
            registries[node.pathTag ^ core.tag] = 0;
        }
        debug.exit();
        //
        int mod = count(core);

        core.sum += mod + ((core.localsum - mod) >> 1);
        for (int i = 0, iuntil = core.childrenSize; i < iuntil; i++) {
            Node child = core.children[i];
            int j = 0;
            for (int juntil = child.childrenSize; j < juntil && child.children[j] != core; j++) ;
            child.children[j] = child.children[--child.childrenSize];
            dpOnTree(child);
        }
    }

    public static int count(Node prevNode) {
        int pathTag = prevNode.pathTag;
        int localSum = 0;
        localSum += registries[pathTag] - registries2[pathTag];
        for (int k = 0; k < 20; k++) {
            int tag = pathTag ^ (1 << k);
            localSum += registries[tag] - registries2[tag];
        }
        return localSum;
    }


    //Set the size of node
    public static void dfs(Node node, Node father) {
        bufListData[bufListDataSize++] = node;
        node.father = father;

        int size = 1;
        for (int i = 0, iuntil = node.childrenSize; i < iuntil; i++) {
            Node child = node.children[i];
            if (child == father) {
                continue;
            }
            dfs(child, node);
            size += child.size;
        }
        node.size = size;
    }

    public static void dfs2(Node node, Node father, int pathTag, Node ancestor) {
        bufListData[bufListDataSize++] = node;
        node.pathTag = pathTag ^ node.tag;
        node.ancestor = ancestor;
        node.father = father;
        for (int i = 0, iuntil = node.childrenSize; i < iuntil; i++) {
            Node child = node.children[i];
            if (child == father) {
                continue;
            }
            dfs2(child, node, node.pathTag, ancestor);
        }
    }

    public static class Node {
        Node[] children;
        int childrenSize;
        int tag;
        int size;
        Node father;
        int pathTag;
        int id;
        Node ancestor;
        long localsum;
        long sum = 1;

        @Override
        public String toString() {
            return "" + id;
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
