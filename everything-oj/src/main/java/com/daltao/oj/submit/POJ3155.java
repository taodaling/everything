package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POJ3155 {
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


        int n;
        int m;

        int idOfNode(int i) {
            return i;
        }

        int idOfEdge(int i) {
            return n + i;
        }

        int idOfSrc() {
            return n + m + 1;
        }

        int idOfDst() {
            return n + m + 2;
        }

        ISAP isap;
        int[][] edges;

        public void solve() {
            n = io.readInt();
            m = io.readInt();


            edges = new int[m + 1][2];
            for (int i = 1; i <= m; i++) {
                edges[i][0] = io.readInt();
                edges[i][1] = io.readInt();
            }

            if (m == 0) {
                io.cache.append(1).append('\n').append(1).append('\n');
                return;
            }

            isap = new ISAP(n + m + 2);
            isap.setSource(idOfSrc());
            isap.setTarget(idOfDst());
            double prec = 1.0 / n / n;
            double l = 0;
            double r = m;

            while (r - l >= prec) {
                double m = (r + l) / 2;
                double hm = h(m);
                if (hm > 0) {
                    l = m;
                } else {
                    r = m;
                }
            }

            h(l - 1e-5);
            List<ISAP.Node> s = isap.getComponentS(deque);
            Collections.sort(s, new Comparator<ISAP.Node>() {
                @Override
                public int compare(ISAP.Node o1, ISAP.Node o2) {
                    return o1.id - o2.id;
                }
            });

            int count = 0;
            for (ISAP.Node node : s) {
                if (node.id > n) {
                    break;
                }
                count++;
            }

            io.cache.append(count).append('\n');
            for (ISAP.Node node : s) {
                if (node.id > n) {
                    break;
                }
                io.cache.append(node.id).append('\n');
            }
        }


        Deque<ISAP.Node> deque = new ArrayDeque();

        public double h(double c) {
            for (int i = 1; i <= n; i++) {
                isap.getChannel(idOfNode(i), idOfDst()).modify(c, 0);
            }
            for (int i = 1; i <= m; i++) {
                isap.getChannel(idOfSrc(), idOfEdge(i)).modify(1, 0);
                for (int j = 0; j < 2; j++) {
                    isap.getChannel(idOfEdge(i), idOfNode(edges[i][j])).modify(1e18, 0);
                }
            }

            isap.bfs(deque);
            double maxFlow = isap.sendFlow(m);
            //debug.debug("c", c);
            //debug.debug("isap", isap);
            debug.debug("c", c);
            debug.debug("h(c)", m - maxFlow);
            return m - maxFlow;
        }
    }

    public static class ISAP {
        Node[] nodes;
        int[] distanceCnt;
        Node source;
        Node target;
        int nodeNum;
        Map<Long, DirectChannel> channelMap = new HashMap();

        public List<Node> getComponentS(Deque<Node> deque) {
            List<Node> result = new ArrayList();
            for (int i = 1; i <= nodeNum; i++) {
                nodes[i].visited = false;
            }
            deque.addLast(source);
            source.visited = true;
            while (!deque.isEmpty()) {
                Node head = deque.removeFirst();
                result.add(head);
                for (Channel channel : head.channelList) {
                    if (channel.getFlow() == channel.getCapacity()) {
                        continue;
                    }
                    Node node = channel.getDst();
                    if (node.visited) {
                        continue;
                    }
                    node.visited = true;
                    deque.addLast(node);
                }
            }
            return result;
        }

        public Collection<DirectChannel> getChannels() {
            return channelMap.values();
        }

        public DirectChannel getChannel(int src, int dst) {
            Long id = (((long) src) << 32) | dst;
            DirectChannel channel = channelMap.get(id);
            if (channel == null) {
                channel = new DirectChannel(nodes[src], nodes[dst], 0, id.hashCode());
                nodes[src].channelList.add(channel);
                nodes[dst].channelList.add(channel.getInverse());
                channelMap.put(id, channel);
            }
            return channel;
        }


        public ISAP(int nodeNum) {
            this.nodeNum = nodeNum;
            nodes = new Node[nodeNum + 1];
            distanceCnt = new int[nodeNum + 2];
            for (int i = 1; i <= nodeNum; i++) {
                Node node = new Node();
                node.id = i;
                nodes[i] = node;
            }
        }

        public double sendFlow(double flow) {
            double flowSnapshot = flow;
            while (flow > 0 && source.distance < nodeNum) {
                flow -= send(source, flow);
            }
            return flowSnapshot - flow;
        }

        public double send(Node node, double flowRemain) {
            if (node == target) {
                return flowRemain;
            }

            double flowSnapshot = flowRemain;
            int nextDistance = node.distance - 1;
            for (Channel channel : node.channelList) {
                double channelRemain = channel.getCapacity() - channel.getFlow();
                Node dst = channel.getDst();
                if (channelRemain == 0 || dst.distance != nextDistance) {
                    continue;
                }
                double actuallySend = send(channel.getDst(), Math.min(flowRemain, channelRemain));
                channel.sendFlow(actuallySend);
                flowRemain -= actuallySend;
                if (flowRemain == 0) {
                    break;
                }
            }

            if (flowSnapshot == flowRemain) {
                if (--distanceCnt[node.distance] == 0) {
                    distanceCnt[source.distance]--;
                    source.distance = nodeNum;
                    distanceCnt[source.distance]++;
                    if (node != source) {
                        distanceCnt[++node.distance]++;
                    }
                } else {
                    distanceCnt[++node.distance]++;
                }
            }

            return flowSnapshot - flowRemain;
        }

        public void setSource(int id) {
            source = nodes[id];
        }

        public void setTarget(int id) {
            target = nodes[id];
        }

        public void bfs(Deque<Node> queue) {
            Arrays.fill(distanceCnt, 0);
            queue.clear();

            for (int i = 1; i <= nodeNum; i++) {
                nodes[i].distance = nodeNum;
            }

            target.distance = 0;
            queue.addLast(target);

            while (!queue.isEmpty()) {
                Node head = queue.removeFirst();
                distanceCnt[head.distance]++;
                for (Channel channel : head.channelList) {
                    Channel inverse = channel.getInverse();
                    if (inverse.getCapacity() == inverse.getFlow()) {
                        continue;
                    }
                    Node dst = channel.getDst();
                    if (dst.distance != nodeNum) {
                        continue;
                    }
                    dst.distance = head.distance + 1;
                    queue.addLast(dst);
                }
            }
        }

        public static interface Channel {
            public Node getSrc();

            public Node getDst();

            public double getCapacity();

            public double getFlow();

            public void sendFlow(double volume);

            public Channel getInverse();
        }

        public static class DirectChannel implements Channel {
            final Node src;
            final Node dst;
            final int id;
            double capacity;
            double flow;
            Channel inverse;

            public DirectChannel(Node src, Node dst, int capacity, int id) {
                this.src = src;
                this.dst = dst;
                this.capacity = capacity;
                this.id = id;
                inverse = new InverseChannelWrapper(this);
            }

            public void modify(double cap, double flow) {
                this.flow = flow;
                this.capacity = cap;
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
            public Channel getInverse() {
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


        }

        public static class InverseChannelWrapper implements Channel {
            final Channel channel;

            public InverseChannelWrapper(Channel channel) {
                this.channel = channel;
            }

            @Override
            public Channel getInverse() {
                return channel;
            }


            @Override
            public Node getSrc() {
                return channel.getDst();
            }

            @Override
            public Node getDst() {
                return channel.getSrc();
            }

            @Override
            public double getCapacity() {
                return channel.getFlow();
            }

            @Override
            public double getFlow() {
                return 0;
            }

            @Override
            public void sendFlow(double volume) {
                channel.sendFlow(-volume);
            }


            @Override
            public String toString() {
                return String.format("%s--%s/%s-->%s", getSrc(), getFlow(), getCapacity(), getDst());
            }
        }

        public static class Node {
            int id;
            int distance;
            boolean visited;
            List<Channel> channelList = new ArrayList(1);

            @Override
            public String toString() {
                return "" + id;
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (DirectChannel channel : getChannels()) {
                builder.append(channel).append('\n');
            }
            return builder.toString();
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
