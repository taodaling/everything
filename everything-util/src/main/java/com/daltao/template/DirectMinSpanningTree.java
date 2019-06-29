package com.daltao.template;

import java.util.*;

public class DirectMinSpanningTree {
    long inf = Long.MAX_VALUE;
    int now;
    Node top;

    public static class Edge {
        Node src;
        Node dst;
        long weight;
        long fixWeight;

        @Override
        public String toString() {
            return "(" + src + "," + dst + ")[" + weight + "]";
        }
    }

    public static class Node {
        int id = -1;
        List<Edge> outEdges = new ArrayList<>(2);
        List<Edge> inEdges = new ArrayList<>(2);
        LeftSideTree queue = LeftSideTree.NIL;
        Node parent;
        Edge out;
        Node outNode;
        int visited;


        Node circleP = this;
        int circleRank;

        Node proxy = this;

        Node findCircle() {
            return circleP.circleP == circleP ? circleP : (circleP = circleP.findCircle());
        }

        static void mergeCircle(Node a, Node b) {
            a = a.findCircle();
            b = b.findCircle();
            if (a == b) {
                return;
            }
            if (a.circleRank == b.circleRank) {
                a.circleRank++;
            }
            if (a.circleRank > b.circleRank) {
                b.circleP = a;
                a.queue = LeftSideTree.merge(a.queue, b.queue);
            } else {
                a.circleP = b;
                b.queue = LeftSideTree.merge(a.queue, b.queue);
            }
        }

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public List<Edge> dismantle(int rootId) {
        if (nodes.length == 1) {
            return Collections.emptyList();
        }

        now++;
        Node root = nodes[rootId];
        List<Edge> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    private void dfs(Node root, List<Edge> result) {
        if (root == top || root.visited == now) {
            return;
        }
        root.visited = now;
        Node trace = root;
        while (true) {
            Node bottom = trace.out.dst;
            Node next = trace.outNode;
            if (next == root) {
                break;
            }
            result.add(trace.out);
            next.visited = now;
            dfs(bottom, result);
            trace = next;
        }

        dfs(root.parent, result);
    }

    public void contract() {
        now++;
        Deque<LeftSideTree> deque = new ArrayDeque<>();
        for (Node node : nodes) {
            for (Edge edge : node.inEdges) {
                edge.fixWeight = edge.weight;
                deque.addLast(new LeftSideTree(edge));
            }
            node.queue = LeftSideTree.createFromDeque(deque);
        }

        int remain = nodes.length;
        Deque<Node> stack = new ArrayDeque<>();
        List<Node> waitList = new ArrayList<>();
        stack.addLast(nodes[0]);
        stack.peekFirst().visited = now;
        while (remain > 1) {
            Node tail = stack.peekLast().findCircle();
            Edge out = null;
            while (out == null) {
                Edge min = tail.queue.peek();
                tail.queue = LeftSideTree.pop(tail.queue);
                //self loop
                if (min.src.findCircle() == min.dst.findCircle()) {
                    continue;
                }
                out = min;
            }

            Node src = out.src.findCircle().proxy;
            //No loop
            if (src.visited != now) {
                src.visited = now;
                src.out = out;
                stack.addLast(src);
                continue;
            }
            //Find loop, merge them together
            Node proxy = new Node();
            proxy.visited = now;
            proxy.out = src.out;
            src.out = out;
            Node last = src;
            while (true) {
                Node trace = stack.removeLast().findCircle().proxy;
                trace.parent = proxy;
                last.outNode = trace;
                trace.findCircle().queue.modify(-last.out.fixWeight);
                Node.mergeCircle(proxy, trace);
                remain--;
                last = trace;
                if (trace == src) {
                    break;
                }
            }
            proxy.findCircle().proxy = proxy;
            stack.addLast(proxy);
            remain++;
        }
        top = stack.removeLast();
    }

    private Node[] nodes;

    public void addEdge(int s, int t, long weight) {
        Edge edge = new Edge();
        edge.src = nodes[s];
        edge.dst = nodes[t];
        edge.weight = weight;
        edge.src.outEdges.add(edge);
        edge.dst.inEdges.add(edge);
    }

    public DirectMinSpanningTree(int n) {
        nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
        for (int i = 0; i < n; i++) {
            addEdge(i, (i + 1) % n, inf);
        }
    }

    public static class LeftSideTree {
        public static final LeftSideTree NIL = new LeftSideTree(null);

        static {
            NIL.left = NIL;
            NIL.right = NIL;
            NIL.dist = -1;
        }

        LeftSideTree left = NIL;
        LeftSideTree right = NIL;
        int dist;
        DirectMinSpanningTree.Edge key;
        long mod;

        public void pushDown() {
            if (mod != 0) {
                left.modify(mod);
                right.modify(mod);
                mod = 0;
            }
        }

        public void modify(long k) {
            if (this == NIL) {
                return;
            }
            key.fixWeight += k;
            mod += k;
        }


        public LeftSideTree(DirectMinSpanningTree.Edge key) {
            this.key = key;
        }

        public static LeftSideTree createFromCollection(Collection<LeftSideTree> trees) {
            return createFromDeque(new ArrayDeque<>(trees));
        }

        public static LeftSideTree createFromDeque(Deque<LeftSideTree> deque) {
            while (deque.size() > 1) {
                deque.addLast(merge(deque.removeFirst(), deque.removeFirst()));
            }
            return deque.removeLast();
        }

        public static LeftSideTree merge(LeftSideTree a, LeftSideTree b) {
            if (a == NIL) {
                return b;
            } else if (b == NIL) {
                return a;
            }
            a.pushDown();
            b.pushDown();
            if (a.key.fixWeight > b.key.fixWeight) {
                LeftSideTree tmp = a;
                a = b;
                b = tmp;
            }
            a.right = merge(a.right, b);
            if (a.left.dist < a.right.dist) {
                LeftSideTree tmp = a.left;
                a.left = a.right;
                a.right = tmp;
            }
            a.dist = a.right.dist + 1;
            return a;
        }

        public boolean isEmpty() {
            return this == NIL;
        }

        public DirectMinSpanningTree.Edge peek() {
            return key;
        }

        public static LeftSideTree pop(LeftSideTree root) {
            root.pushDown();
            return merge(root.left, root.right);
        }

        private void toStringDfs(StringBuilder builder) {
            if (this == NIL) {
                return;
            }
            builder.append(key).append(' ');
            left.toStringDfs(builder);
            right.toStringDfs(builder);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            toStringDfs(builder);
            return builder.toString();
        }
    }
}