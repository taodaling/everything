package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class POJ1548 {
    public static void main(String[] args) throws Exception {
        boolean local = System.getProperty("ONLINE_JUDGE") == null;
        boolean async = false;

        Charset charset = Charset.forName("ascii");

        FastIO io = local ? new FastIO(new FileInputStream("D:\\DATABASE\\TESTCASE\\Code.in"), System.out, charset) : new FastIO(System.in, System.out, charset);
        Task task = new Task(io, new Debug(local));

        if (async) {
            Thread t = new Thread(null, task, "dalt", 1 << 27);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
            t.join();
        } else {
            task.run();
        }

        if (local) {
            io.cache.append("\n\n--memory -- \n" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");
        }

        io.flush();
    }

    public static class Task implements Runnable {
        final FastIO io;
        final Debug debug;
        int inf = (int) 1e8;
        int mod = (int) 1e9 + 7;

        public int mod(int val) {
            val %= mod;
            if (val < 0) {
                val += mod;
            }
            return val;
        }

        public int mod(long val) {
            val %= mod;
            if (val < 0) {
                val += mod;
            }
            return (int) val;
        }

        int bitAt(int x, int i) {
            return (x >> i) & 1;
        }

        int bitAt(long x, int i) {
            return (int) ((x >> i) & 1);
        }

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int maxX;
        int maxY;
        public int idOfCellInput(int i, int j) {
            return (i - 1) * maxY + j;
        }

        public int idOfCellOutput(int i, int j) {
            return idOfCellInput(i, j) + maxX * maxY;
        }

        int[][] edges = new int[24 * 24 + 1][2];

        public void solve() {
            while (true) {

                int n = 0;
                maxX = 0;
                maxY = 0;
                while (true) {
                    int x = io.readInt();
                    int y = io.readInt();
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                    edges[n][0] = x;
                    edges[n][1] = y;
                    if (x == 0 && y == 0) {
                        break;
                    }
                    if (x < 0 && y < 0) {
                        return;
                    }
                    n++;
                }
                MinCostMaxFlow mcmf = new MinCostMaxFlow(maxX * maxY * 2);
                for (int i = 0; i < n; i++) {
                    int x = edges[i][0];
                    int y = edges[i][1];
                    mcmf.buildChannel(idOfCellInput(x, y), idOfCellOutput(x, y), 1, -1, 0);
                }
                for (int i = 1; i <= maxX; i++) {
                    for (int j = 1; j <= maxY; j++) {
                        mcmf.buildChannel(idOfCellInput(i, j), idOfCellOutput(i, j), inf, 0, 0);
                        if (i < maxX) {
                            mcmf.buildChannel(idOfCellOutput(i, j), idOfCellInput(i + 1, j), inf, 0, 0);
                        }
                        if (j < maxY) {
                            mcmf.buildChannel(idOfCellOutput(i, j), idOfCellInput(i, j + 1), inf, 0, 0);
                        }
                    }
                }

                mcmf.setSource(idOfCellInput(1, 1));
                mcmf.setSink(idOfCellOutput(maxX, maxY));

                int flow = 0;
                int[] result = new int[2];
                while (true) {
                    mcmf.send(1, result);
                    if (result[1] < 0) {
                        flow++;
                    } else {
                        break;
                    }
                }

                io.cache.append(flow).append('\n');
            }
        }
    }

    public static class MinCostMaxFlow {
        Node[] nodes;
        Deque<Node> deque;
        Node source;
        Node sink;
        int nodeNum;
        final static int INF = (int) 1e8;

        public MinCostMaxFlow(int nodeNum) {
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


                int feeSum = sink.distance;
                int minFlow = flow;

                Node trace = sink;
                while (trace != source) {
                    FeeChannel last = trace.last;
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


    public static class FastIO {
        public final StringBuilder cache = new StringBuilder();
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 8);
        private byte[] buf = new byte[1 << 13];
        private int bufLen;
        private int bufOffset;
        private int next;

        public FastIO(InputStream is, OutputStream os, Charset charset) {
            this.is = is;
            this.os = os;
            this.charset = charset;
        }

        public FastIO(InputStream is, OutputStream os) {
            this(is, os, Charset.forName("ascii"));
        }

        private int read() {
            while (bufLen == bufOffset) {
                bufOffset = 0;
                try {
                    bufLen = is.read(buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (bufLen == -1) {
                    return -1;
                }
            }
            return buf[bufOffset++];
        }

        public void skipBlank() {
            while (next >= 0 && next <= 32) {
                next = read();
            }
        }

        public int readInt() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            int val = 0;
            if (sign == 1) {
                while (next >= '0' && next <= '9') {
                    val = val * 10 + next - '0';
                    next = read();
                }
            } else {
                while (next >= '0' && next <= '9') {
                    val = val * 10 - next + '0';
                    next = read();
                }
            }

            return val;
        }

        public long readLong() {
            int sign = 1;

            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+' ? 1 : -1;
                next = read();
            }

            long val = 0;
            if (sign == 1) {
                while (next >= '0' && next <= '9') {
                    val = val * 10 + next - '0';
                    next = read();
                }
            } else {
                while (next >= '0' && next <= '9') {
                    val = val * 10 - next + '0';
                    next = read();
                }
            }

            return val;
        }

        public double readDouble() {
            boolean sign = true;
            skipBlank();
            if (next == '+' || next == '-') {
                sign = next == '+';
                next = read();
            }

            long val = 0;
            while (next >= '0' && next <= '9') {
                val = val * 10 + next - '0';
                next = read();
            }
            if (next != '.') {
                return sign ? val : -val;
            }
            next = read();
            long radix = 1;
            long point = 0;
            while (next >= '0' && next <= '9') {
                point = point * 10 + next - '0';
                radix = radix * 10;
                next = read();
            }
            double result = val + (double) point / radix;
            return sign ? result : -result;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (next > 32) {
                builder.append((char) next);
                next = read();
            }

            return builder.toString();
        }

        public String readString() {
            defaultStringBuf.setLength(0);
            return readString(defaultStringBuf);
        }

        public int readLine(char[] data, int offset) {
            int originalOffset = offset;
            while (next != -1 && next != '\n') {
                data[offset++] = (char) next;
                next = read();
            }
            return offset - originalOffset;
        }

        public int readString(char[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (char) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public int readString(byte[] data, int offset) {
            skipBlank();

            int originalOffset = offset;
            while (next > 32) {
                data[offset++] = (byte) next;
                next = read();
            }

            return offset - originalOffset;
        }

        public char readChar() {
            skipBlank();
            char c = (char) next;
            next = read();
            return c;
        }

        public void flush() {
            try {
                os.write(cache.toString().getBytes(charset));
                os.flush();
                cache.setLength(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean hasMore() {
            skipBlank();
            return next != -1;
        }
    }

    public static class Debug {
        private boolean allowDebug;

        public Debug(boolean allowDebug) {
            this.allowDebug = allowDebug;
        }

        public void assertTrue(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (!flag) {
                fail();
            }
        }

        public void fail() {
            throw new RuntimeException();
        }

        public void assertFalse(boolean flag) {
            if (!allowDebug) {
                return;
            }
            if (flag) {
                fail();
            }
        }

        private void outputName(String name) {
            System.out.print(name + " = ");
        }

        public void debug(String name, int x) {
            if (!allowDebug) {
                return;
            }

            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, long x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, double x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, int[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, long[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, double[] x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.toString(x));
        }

        public void debug(String name, Object x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println("" + x);
        }

        public void debug(String name, Object... x) {
            if (!allowDebug) {
                return;
            }
            outputName(name);
            System.out.println(Arrays.deepToString(x));
        }
    }
}
