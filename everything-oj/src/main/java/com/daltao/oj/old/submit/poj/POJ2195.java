package com.daltao.oj.old.submit.poj;

import java.io.*;
import java.util.*;

public class POJ2195 {
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
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\poj\\POJ2195.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
        int n, m;

        while (true) {
            n = input.nextInteger();
            m = input.nextInteger();

            if (n == 0 && m == 0) {
                break;
            }

            singleSolve(n, m);
        }
    }

    public static void singleSolve(int n, int m) {
        char[][] map = new char[n][m];

        for (int i = 0; i < n; i++) {
            input.nextBlock(map[i], 0);
        }

        MinFeeMaxFlow flow = new MinFeeMaxFlow(n * m + 2);
        int sourceId = n * m + 1;
        int sinkId = n * m + 2;
        flow.setSource(sourceId);
        flow.setSink(sinkId);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int id = getId(m, i, j);
                if (i > 0) {
                    flow.buildChannel(id, getId(m, i - 1, j), INF, 1, 0);
                }
                if (i + 1 < n) {
                    flow.buildChannel(id, getId(m, i + 1, j), INF, 1, 0);
                }
                if (j > 0) {
                    flow.buildChannel(id, getId(m, i, j - 1), INF, 1, 0);
                }
                if (j + 1 < m) {
                    flow.buildChannel(id, getId(m, i, j + 1), INF, 1, 0);
                }

                if (map[i][j] == 'H') {
                    flow.buildChannel(id, sinkId, 1, 0, 0);
                } else if (map[i][j] == 'm') {
                    flow.buildChannel(sourceId, id, 1, 0, 0);
                }
            }
        }

        int[] res = new int[2];
        flow.send(INF, res);

        output.println(res[1]);
    }

    public static int getId(int c, int i, int j) {
        return i * c + j + 1;
    }

    public static class MinFeeMaxFlow {
        Node[] nodes;
        Deque<Node> deque;
        Node source;
        Node sink;
        int nodeNum;
        final static int INF = (int) 1e8;

        public MinFeeMaxFlow(int nodeNum) {
            this.nodeNum = nodeNum;
            nodes = new Node[nodeNum + 1];
            for (int i = 1; i <= nodeNum; i++) {
                nodes[i] = new Node(i);
            }
            deque = new ArrayDeque(nodeNum);
        }

        public void setSource(int id) {
            source = nodes[id];
        }

        public void setSink(int id) {
            sink = nodes[id];
        }

        public DirectFeeChannel buildChannel(int src, int dst, int cap, int fee, int id) {
            return Node.buildChannel(nodes[src], nodes[dst], cap, fee, id);
        }

        /**
         * reuslt[0] store how much flow could be sent and result[1] represents the fee
         */
        public void send(int flow, int[] result) {
            int totalFee = 0;
            int totalFlow = 0;

            while (flow > 0) {
                spfa();

                if (sink.distance == INF) {
                    break;
                }


                int feeSum = 0;
                int minFlow = flow;

                Node trace = sink;
                while (trace != source) {
                    FeeChannel last = trace.last;
                    feeSum += last.getFee();
                    minFlow = Math.min(minFlow, last.getCapacity() - last.getFlow());
                    trace = last.getSrc();
                }

                flow -= minFlow;

                trace = sink;
                while (trace != source) {
                    FeeChannel last = trace.last;
                    last.sendFlow(minFlow);
                    trace = last.getSrc();
                }

                totalFee += feeSum;
                totalFlow += minFlow;
            }

            result[0] = totalFlow;
            result[1] = totalFee;
        }

        private void spfa() {
            for (int i = 1; i <= nodeNum; i++) {
                nodes[i].distance = INF;
                nodes[i].inque = false;
                nodes[i].last = null;
            }

            deque.addLast(source);
            source.distance = 0;
            source.inque = true;

            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                head.inque = false;
                for (FeeChannel channel : head.channelList) {
                    if (channel.getFlow() == channel.getCapacity()) {
                        continue;
                    }
                    Node dst = channel.getDst();
                    int newDist = head.distance + channel.getFee();
                    if (dst.distance <= newDist) {
                        continue;
                    }
                    dst.distance = newDist;
                    dst.last = channel;
                    if (dst.inque) {
                        continue;
                    }
                    deque.addLast(dst);
                    dst.inque = true;
                }
            }
        }

        public static interface FeeChannel {
            public Node getSrc();

            public Node getDst();

            public int getCapacity();

            public int getFlow();

            public void sendFlow(int volume);

            public FeeChannel getInverse();

            public int getFee();
        }

        public static class DirectFeeChannel implements FeeChannel {
            final Node src;
            final Node dst;
            final int id;
            int capacity;
            int flow;
            FeeChannel inverse;
            final int fee;

            @Override
            public int getFee() {
                return fee;
            }

            public DirectFeeChannel(Node src, Node dst, int capacity, int fee, int id) {
                this.src = src;
                this.dst = dst;
                this.capacity = capacity;
                this.id = id;
                this.fee = fee;
                inverse = new InverseFeeChannelWrapper(this);
            }

            @Override
            public String toString() {
                return String.format("%s--%s/%s-->%s", getSrc(), getFlow(), getCapacity(), getDst());
            }

            @Override
            public Node getSrc() {
                return src;
            }

            @Override
            public FeeChannel getInverse() {
                return inverse;
            }

            public void setCapacity(int expand) {
                capacity = expand;
            }

            @Override
            public Node getDst() {
                return dst;
            }

            @Override
            public int getCapacity() {
                return capacity;
            }

            @Override
            public int getFlow() {
                return flow;
            }

            @Override
            public void sendFlow(int volume) {
                flow += volume;
            }


        }

        public static class InverseFeeChannelWrapper implements FeeChannel {
            final FeeChannel inner;

            public InverseFeeChannelWrapper(FeeChannel inner) {
                this.inner = inner;
            }

            @Override
            public int getFee() {
                return -inner.getFee();
            }

            @Override
            public FeeChannel getInverse() {
                return inner;
            }


            @Override
            public Node getSrc() {
                return inner.getDst();
            }

            @Override
            public Node getDst() {
                return inner.getSrc();
            }

            @Override
            public int getCapacity() {
                return inner.getFlow();
            }

            @Override
            public int getFlow() {
                return 0;
            }

            @Override
            public void sendFlow(int volume) {
                inner.sendFlow(-volume);
            }


            @Override
            public String toString() {
                return String.format("%s--%s/%s-->%s", getSrc(), getFlow(), getCapacity(), getDst());
            }
        }

        public static class Node {
            final int id;
            int distance;
            boolean inque;
            FeeChannel last;
            List<FeeChannel> channelList = new ArrayList(1);

            public Node(int id) {
                this.id = id;
            }

            public static DirectFeeChannel buildChannel(Node src, Node dst, int cap, int fee, int id) {
                DirectFeeChannel channel = new DirectFeeChannel(src, dst, cap, fee, id);
                src.channelList.add(channel);
                dst.channelList.add(channel.getInverse());
                return channel;
            }

            @Override
            public String toString() {
                return "" + id;
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
