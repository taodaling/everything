package com.daltao.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class ISAP {
    Node[] nodes;
    int[] distanceCnt;
    Node source;
    Node target;
    int nodeNum;
    boolean bfsFlag;

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

    public int sendFlow(int flow) {
        int flowSnapshot = flow;
        while (flow > 0 && source.distance < nodeNum) {
            flow -= send(source, flow);
        }
        return flowSnapshot - flow;
    }

    public int send(Node node, int flowRemain) {
        if (node == target) {
            return flowRemain;
        }

        int flowSnapshot = flowRemain;
        int nextDistance = node.distance - 1;
        for (Channel channel : node.channelList) {
            int channelRemain = channel.getCapacity() - channel.getFlow();
            Node dst = channel.getDst();
            if (channelRemain == 0 || dst.distance != nextDistance) {
                continue;
            }
            int actuallySend = send(channel.getDst(), Math.min(flowRemain, channelRemain));
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

    public DirectChannel buildChannel(int a, int b, int flow, int id) {
        return Node.buildChannel(nodes[a], nodes[b], flow, id);
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

        public int getCapacity();

        public int getFlow();

        public void sendFlow(int volume);

        public Channel getInverse();
    }

    public static class DirectChannel implements Channel {
        final Node src;
        final Node dst;
        final int id;
        int capacity;
        int flow;
        Channel inverse;

        public DirectChannel(Node src, Node dst, int capacity, int id) {
            this.src = src;
            this.dst = dst;
            this.capacity = capacity;
            this.id = id;
            inverse = new InverseChannelWrapper(this);
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
        public int getCapacity() {
            return channel.getFlow();
        }

        @Override
        public int getFlow() {
            return 0;
        }

        @Override
        public void sendFlow(int volume) {
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
        List<Channel> channelList = new ArrayList<>(1);

        public static DirectChannel buildChannel(Node src, Node dst, int capacity, int id) {
            DirectChannel channel = new DirectChannel(src, dst, capacity, id);
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