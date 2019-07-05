package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BZOJ2661 {
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
        Gcd gcd = new Gcd();
        Random random = new Random();

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        int a;
        int b;


        int idOfNode(int i) {
            return i - a + 1;
        }

        int idOfSrc() {
            return idOfNode(b) + 1;
        }

        int idOfDst() {
            return idOfSrc() + 1;
        }

        public boolean biparty(Node root, int color) {
            if (root.color != -1) {
                return root.color == color;
            }
            root.color = color;
            for (Node node : root.next) {
                if (!biparty(node, color ^ 1)) {
                    return false;
                }
            }
            return true;
        }

        public void solve() {
            a = io.readInt();
            b = io.readInt();

            Node[] nodes = new Node[b + 1];
            for (int i = a; i <= b; i++) {
                nodes[i] = new Node();
                nodes[i].id = i;
            }

            int multiplier = 10000;
            ZkwMinCostMaxFlow km = new ZkwMinCostMaxFlow((b - a + 1) * 2 + 2);
            for (int i = a; i <= b; i++) {
                for (int j = i + 1; j <= b; j++) {
                    if (gcd.gcd(i, j) == 1 && isPower2(i, j)) {
                        nodes[i].next.add(nodes[j]);
                        nodes[j].next.add(nodes[i]);
                    }
                }
            }

            for (int i = a; i <= b; i++) {
                if (nodes[i].color == -1) {
                    debug.assertTrue(biparty(nodes[i], 0));
                }
                if (nodes[i].color == 0) {
                    km.getChannel(idOfSrc(), idOfNode(i), -i).modify(1, 0);
                    for (Node node : nodes[i].next) {
                        km.getChannel(idOfNode(i), idOfNode(node.id), 0).modify(1, 0);
                    }
                } else {
                    km.getChannel(idOfNode(i), idOfDst(), -i).modify(1, 0);
                }
            }

            km.setSource(idOfSrc());
            km.setTarget(idOfDst());
            km.send(inf);

            int totalFlow = (int)(km.flow + 0.5);
            int totalFee = (int) (-km.fee + 0.5);
            io.cache.append(totalFlow).append(' ')
                    .append(totalFee);
        }

        private boolean isPower2(int a, int b) {
            if (a > b) {
                int t = a;
                a = b;
                b = t;
            }
            double x = Math.sqrt(b * b - a * a);
            int y = (int) (x + 0.5);
            return y * y + a * a == b * b;
        }
    }

    public static class Node {
        List<Node> next = new ArrayList(2);
        int id;
        int color = -1;
    }


    public static class ZkwMinCostMaxFlow {
        Node[] nodes;
        Deque<Node> deque;
        Node source;
        Node target;
        int nodeNum;
        double fee = 0;
        double flow = 0;
        final static double INF = 1e14;

        static class ID {
            int src;
            int dst;
            double fee;

            ID(int src, int dst, double fee) {
                this.src = src;
                this.dst = dst;
                this.fee = fee;
            }

            @Override
            public int hashCode() {
                return (int) ((src * 31L + dst) * 31 + Double.doubleToLongBits(fee));
            }

            @Override
            public boolean equals(Object obj) {
                ID other = (ID) obj;
                return src == other.src &&
                        dst == other.dst &&
                        fee == other.fee;
            }
        }

        Map<ID, DirectFeeChannel> channelMap = new HashMap();

        public ZkwMinCostMaxFlow(int nodeNum) {
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

        ID id = new ID(0, 0, 0);

        public DirectFeeChannel getChannel(int src, int dst, double fee) {
            id.src = src;
            id.dst = dst;
            id.fee = fee;
            DirectFeeChannel channel = channelMap.get(id);
            if (channel == null) {
                channel = addChannel(src, dst, fee);
                channelMap.put(new ID(src, dst, fee), channel);
            }
            return channel;
        }

        private DirectFeeChannel addChannel(int src, int dst, double fee) {
            DirectFeeChannel dfc = new DirectFeeChannel(nodes[src], nodes[dst], fee);
            nodes[src].channelList.add(dfc);
            nodes[dst].channelList.add(dfc.inverse());
            return dfc;
        }

        private double send(Node root, double flow) {
            if (root == target) {
                return flow;
            }
            if (root.inque) {
                return 0;
            }
            root.inque = true;
            double totalSent = 0;
            for (FeeChannel channel : root.channelList) {
                Node node = channel.getDst();
                if (channel.getCapacity() == channel.getFlow()
                        || node.distance + channel.getFee() != root.distance) {
                    continue;
                }
                double f = send(node, Math.min(flow, channel.getCapacity() - channel.getFlow()));
                if (f == 0) {
                    continue;
                }
                flow -= f;
                channel.sendFlow(f);
                fee += channel.getFee() * f;
                totalSent += f;
            }
            return totalSent;
        }

        /**
         * reuslt[0] store how much flow could be sent and result[1] represents the fee
         */
        public void send(double flow) {
            while (flow > 0) {
                spfa();
                if (source.distance == INF) {
                    break;
                }
                while (true) {
                    for (int i = 1; i <= nodeNum; i++) {
                        nodes[i].inque = false;
                    }
                    double f = send(source, flow);
                    if (f == 0) {
                        break;
                    }
                    flow -= f;
                    this.flow += f;
                }
            }
        }

        private void spfa() {
            for (int i = 1; i <= nodeNum; i++) {
                nodes[i].distance = INF;
                nodes[i].inque = false;
            }

            deque.addLast(target);
            target.distance = 0;
            target.inque = true;
            double sumOfDistance = 0;

            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                if (head.distance * (deque.size() + 1) > sumOfDistance) {
                    deque.addLast(head);
                    continue;
                }
                sumOfDistance -= head.distance;
                head.inque = false;
                for (FeeChannel channel : head.channelList) {
                    channel = channel.inverse();
                    if (channel.getFlow() == channel.getCapacity()) {
                        continue;
                    }
                    Node dst = channel.getSrc();
                    double oldDist = dst.distance;
                    double newDist = head.distance + channel.getFee();
                    if (oldDist <= newDist) {
                        continue;
                    }
                    dst.distance = newDist;
                    if (dst.inque) {
                        sumOfDistance -= oldDist;
                        sumOfDistance += newDist;
                        continue;
                    }
                    if (!deque.isEmpty() && deque.peekFirst().distance < dst.distance) {
                        deque.addFirst(dst);
                    } else {
                        deque.addLast(dst);
                    }
                    dst.inque = true;
                    sumOfDistance += newDist;
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
            List<FeeChannel> channelList = new ArrayList(2);

            public Node(int id) {
                this.id = id;
            }

            @Override
            public String toString() {
                return "" + id;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (DirectFeeChannel channel : channelMap.values()) {
                if (channel.getFlow() > 0) {
                    builder.append(channel).append('\n');
                }
            }
            for (DirectFeeChannel channel : channelMap.values()) {
                if (channel.getFlow() == 0) {
                    builder.append(channel).append('\n');
                }
            }
            return builder.toString();
        }
    }


    public static class Gcd {
        public long gcd(long a, long b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private long gcd0(long a, long b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        public int gcd(int a, int b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private int gcd0(int a, int b) {
            return b == 0 ? a : gcd0(b, a % b);
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

        public void flush() throws IOException {
            os.write(cache.toString().getBytes(charset));
            os.flush();
            cache.setLength(0);
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
