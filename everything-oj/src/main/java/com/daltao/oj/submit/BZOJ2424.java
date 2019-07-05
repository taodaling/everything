package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ2424 {
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

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int n;

        int idOfMonth(int i) {
            return i + 1;
        }

        int idOfSrc() {
            return n + 1;
        }

        int idOfDst() {
            return idOfSrc() + 1;
        }

        public void solve() {
            n = io.readInt();
            int m = io.readInt();
            int s = io.readInt();
            MinCostMaxFlow mcmf = new MinCostMaxFlow(idOfDst());
            int[] requirements = new int[n];
            int[] prices = new int[n];
            for (int i = 0; i < n; i++) {
                requirements[i] = io.readInt();
            }
            for (int i = 0; i < n; i++) {
                prices[i] = io.readInt();
            }
            for (int i = 0; i < n; i++) {
                mcmf.getChannel(idOfSrc(), idOfMonth(i), prices[i]).reset(inf, 0);
                mcmf.getChannel(idOfMonth(i), idOfDst(), 0).reset(requirements[i], 0);
                if (i > 0) {
                    mcmf.getChannel(idOfMonth(i - 1), idOfMonth(i), m).reset(s, 0);
                }
            }
            mcmf.setSource(idOfSrc());
            mcmf.setTarget(idOfDst());
            long ans = (long)(mcmf.send(inf)[1] + 0.5);
            io.cache.append(ans);
        }
    }


    public static class MinCostMaxFlow {
        Node[] nodes;
        Deque<Node> deque;
        Node source;
        Node target;
        int nodeNum;
        final static double INF = 1e50;
        Map<Long, Map<Double, DirectFeeChannel>> channelMap = new HashMap();

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

        public void setTarget(int id) {
            target = nodes[id];
        }

        public DirectFeeChannel getChannel(int src, int dst, double fee) {
            long id = (((long) src) << 32) | dst;
            Map<Double, DirectFeeChannel> map = channelMap.get(id);
            if (map == null) {
                map = new HashMap(1);
                channelMap.put(id, map);
            }
            DirectFeeChannel channel = map.get(fee);
            if (channel == null) {
                channel = addChannel(src, dst, fee);
                map.put(fee, channel);
            }
            return channel;
        }

        private DirectFeeChannel addChannel(int src, int dst, double fee) {
            DirectFeeChannel dfc = new DirectFeeChannel(nodes[src], nodes[dst], fee);
            nodes[src].channelList.add(dfc);
            nodes[dst].channelList.add(dfc.inverse());
            return dfc;
        }

        /**
         * reuslt[0] store how much flow could be sent and result[1] represents the fee
         */
        public double[] send(double flow) {
            double totalFee = 0;
            double totalFlow = 0;

            while (flow > 0) {
                spfa();

                if (target.distance == INF) {
                    break;
                }


                double feeSum = target.distance;
                double minFlow = flow;

                Node trace = target;
                while (trace != source) {
                    FeeChannel last = trace.last;
                    minFlow = Math.min(minFlow, last.getCapacity() - last.getFlow());
                    trace = last.getSrc();
                }

                flow -= minFlow;

                trace = target;
                while (trace != source) {
                    FeeChannel last = trace.last;
                    last.sendFlow(minFlow);
                    trace = last.getSrc();
                }

                totalFee += feeSum * minFlow;
                totalFlow += minFlow;
            }

            return new double[]{totalFlow, totalFee};
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
                    double newDist = head.distance + channel.getFee();
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

            public double getCapacity();

            public double getFlow();

            public void sendFlow(double volume);

            public FeeChannel inverse();

            public double getFee();
        }

        public static class DirectFeeChannel implements FeeChannel {
            final Node src;
            final Node dst;
            double capacity;
            double flow;
            FeeChannel inverse;
            final double fee;

            @Override
            public double getFee() {
                return fee;
            }

            public DirectFeeChannel(Node src, Node dst, double fee) {
                this.src = src;
                this.dst = dst;
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
            public FeeChannel inverse() {
                return inverse;
            }

            @Override
            public Node getDst() {
                return dst;
            }

            @Override
            public double getCapacity() {
                return capacity;
            }

            @Override
            public double getFlow() {
                return flow;
            }

            @Override
            public void sendFlow(double volume) {
                flow += volume;
            }

            public void reset(double cap, double flow) {
                this.capacity = cap;
                this.flow = flow;
            }

            public void modify(double cap, double flow) {
                this.capacity += cap;
                this.flow += flow;
            }
        }

        public static class InverseFeeChannelWrapper implements FeeChannel {
            final FeeChannel inner;

            public InverseFeeChannelWrapper(FeeChannel inner) {
                this.inner = inner;
            }

            @Override
            public double getFee() {
                return -inner.getFee();
            }

            @Override
            public FeeChannel inverse() {
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
            public double getCapacity() {
                return inner.getFlow();
            }

            @Override
            public double getFlow() {
                return 0;
            }

            @Override
            public void sendFlow(double volume) {
                inner.sendFlow(-volume);
            }


            @Override
            public String toString() {
                return String.format("%s--%s/%s-->%s", getSrc(), getFlow(), getCapacity(), getDst());
            }
        }

        public static class Node {
            final int id;
            double distance;
            boolean inque;
            FeeChannel last;
            List<FeeChannel> channelList = new ArrayList(1);

            public Node(int id) {
                this.id = id;
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
