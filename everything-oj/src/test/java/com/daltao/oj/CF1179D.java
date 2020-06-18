package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.IOException;
import java.util.TreeSet;
import java.util.ArrayList;
import java.io.UncheckedIOException;
import java.util.List;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.util.Collections;
import java.io.InputStream;

public class CF1179D {
    @Test
    public void test() {
        Assert.assertTrue(
                new TestCaseExecutor.Builder()
                        .setTestTime(10000)
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                        .setInputFactory(new Generator())
                        .build().call()
        );
    }

    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     */
    public static class Main {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "", 1 << 27);
            thread.start();
            thread.join();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                FastOutput out = new FastOutput(outputStream);
                TaskD solver = new TaskD();
                solver.solve(1, in, out);
                out.close();
            }
        }

        static class TaskD {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                Node[] nodes = new Node[n + 1];
                for (int i = 1; i <= n; i++) {
                    nodes[i] = new Node();
                }
                for (int i = 1; i < n; i++) {
                    Node a = nodes[in.readInt()];
                    Node b = nodes[in.readInt()];
                    a.next.add(b);
                    b.next.add(a);
                }
                dfs(nodes[1], null);
                long ans = 0;
                for (int i = 1; i <= n; i++) {
                    ans = Math.max(ans, nodes[i].dp[0]);
                    ans = Math.max(ans, nodes[i].dp[1]);
                }
                long origin = (long) n * (n - 1) / 2;
                out.println(ans + origin);
            }

            public void dfs(Node root, Node p) {
                root.next.remove(p);
                root.size = 1;
                for (Node node : root.next) {
                    dfs(node, root);
                    root.size += node.size;
                }
                LongConvexHullTrick cht = new LongConvexHullTrick();
                long[] dp = root.dp;
                for (Node node : root.next) {
                    dp[0] = Math.max(dp[0], node.dp[0] + (long) (root.size - node.size) * node.size);
                    if (!cht.isEmpty()) {
                        dp[1] = Math.max(dp[1], node.dp[0] + (long) (root.size - node.size) * node.size + cht.query(node.size));
                    }
                    cht.insert(-node.size, node.dp[0] + (long) (root.size - node.size) * node.size);
                }
            }

        }

        static class LongConvexHullTrick implements Iterable<LongConvexHullTrick.Line> {
            private TreeSet<LongConvexHullTrick.Line> setOrderByA = new TreeSet(LongConvexHullTrick.Line.orderByA);
            private TreeSet<LongConvexHullTrick.Line> setOrderByLx = new TreeSet(LongConvexHullTrick.Line.orderByLx);
            private LongConvexHullTrick.Line queryLine = new LongConvexHullTrick.Line(0, 0);

            public boolean isEmpty() {
                return setOrderByA.isEmpty();
            }

            public long query(long x) {
                queryLine.lx = x;
                LongConvexHullTrick.Line line = setOrderByLx.floor(queryLine);
                return line.y(x);
            }

            public LongConvexHullTrick.Line insert(long a, long b) {
                LongConvexHullTrick.Line newLine = new LongConvexHullTrick.Line(a, b);
                boolean add = true;
                while (add) {
                    LongConvexHullTrick.Line prev = setOrderByA.floor(newLine);
                    if (prev == null) {
                        newLine.lx = Long.MIN_VALUE;
                        break;
                    }
                    if (prev.a == newLine.a) {
                        if (prev.b >= newLine.b) {
                            add = false;
                            break;
                        } else {
                            setOrderByA.remove(prev);
                            setOrderByLx.remove(prev);
                        }
                    } else {
                        double lx = LongConvexHullTrick.Line.intersectAt(prev, newLine);
                        if (lx <= prev.lx) {
                            setOrderByA.remove(prev);
                            setOrderByLx.remove(prev);
                        } else if (lx > prev.rx) {
                            add = false;
                            break;
                        } else {
                            prev.rx = (long) Math.floor(lx);
                            newLine.lx = (long) Math.ceil(lx);
                            break;
                        }
                    }
                }

                while (add) {
                    LongConvexHullTrick.Line next = setOrderByA.ceiling(newLine);
                    if (next == null) {
                        newLine.rx = Long.MAX_VALUE;
                        break;
                    }
                    double rx = LongConvexHullTrick.Line.intersectAt(newLine, next);
                    if (rx >= next.rx) {
                        setOrderByA.remove(next);
                        setOrderByLx.remove(next);
                    } else if (rx < next.lx || (newLine.lx >= rx)) {
                        LongConvexHullTrick.Line lastLine = setOrderByA.floor(newLine);
                        if (lastLine != null) {
                            lastLine.rx = next.lx;
                        }
                        add = false;
                        break;
                    } else {
                        next.lx = (long) Math.floor(rx);
                        newLine.rx = (long) Math.ceil(rx);
                        break;
                    }
                }

                if (add) {
                    setOrderByA.add(newLine);
                    setOrderByLx.add(newLine);
                }

                return newLine;
            }

            public Iterator<LongConvexHullTrick.Line> iterator() {
                return setOrderByA.iterator();
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                for (LongConvexHullTrick.Line line : this) {
                    builder.append(line).append('\n');
                }
                return builder.toString();
            }

            public static class Line {
                long a;
                long b;
                long lx;
                long rx;
                static Comparator<LongConvexHullTrick.Line> orderByA = new Comparator<LongConvexHullTrick.Line>() {

                    public int compare(LongConvexHullTrick.Line o1, LongConvexHullTrick.Line o2) {
                        return Double.compare(o1.a, o2.a);
                    }
                };
                static Comparator<LongConvexHullTrick.Line> orderByLx = new Comparator<LongConvexHullTrick.Line>() {

                    public int compare(LongConvexHullTrick.Line o1, LongConvexHullTrick.Line o2) {
                        return Long.compare(o1.lx, o2.lx);
                    }
                };

                public Line(long a, long b) {
                    this.a = a;
                    this.b = b;
                }

                public long y(long x) {
                    return a * x + b;
                }

                public static double intersectAt(LongConvexHullTrick.Line a, LongConvexHullTrick.Line b) {
                    return (double) (b.b - a.b) / (a.a - b.a);
                }

                public int hashCode() {
                    return (int) (Double.doubleToLongBits(a) * 31 + Double.doubleToLongBits(b));
                }

                public boolean equals(Object obj) {
                    LongConvexHullTrick.Line line = (LongConvexHullTrick.Line) obj;
                    return a == line.a && b == line.b;
                }

                public String toString() {
                    return a + "x+" + b;
                }

            }

        }

        static class FastInput {
            private final InputStream is;
            private byte[] buf = new byte[1 << 13];
            private int bufLen;
            private int bufOffset;
            private int next;

            public FastInput(InputStream is) {
                this.is = is;
            }

            private int read() {
                while (bufLen == bufOffset) {
                    bufOffset = 0;
                    try {
                        bufLen = is.read(buf);
                    } catch (IOException e) {
                        bufLen = -1;
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

        }

        static class Node {
            List<Node> next = new ArrayList<>();
            long[] dp = new long[2];
            int size;

        }

        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(10 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput println(long c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput flush() {
                try {
                    os.append(cache);
                    os.flush();
                    cache.setLength(0);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return this;
            }

            public void close() {
                flush();
                try {
                    os.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public String toString() {
                return cache.toString();
            }

        }
    }


    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     */
    public static class Task {
        public static void main(String[] args) {
            InputStream inputStream = System.in;
            OutputStream outputStream = System.out;
            InputReader in = new InputReader(inputStream);
            PrintWriter out = new PrintWriter(outputStream);
            TaskD solver = new TaskD();
            solver.solve(1, in, out);
            out.close();
        }

        static class TaskD {
            static int n;
            static List<TaskD.Vertex> pairs = new ArrayList<>();

            public void solve(int testNumber, InputReader in, PrintWriter out) {
                n = in.nextInt();
                TaskD.Vertex[] vs = new TaskD.Vertex[n];
                for (int i = 0; i < n; ++i) vs[i] = new TaskD.Vertex();
                for (int i = 0; i < n - 1; ++i) {
                    TaskD.Vertex a = vs[in.nextInt() - 1];
                    TaskD.Vertex b = vs[in.nextInt() - 1];
                    a.adj.add(b);
                    b.adj.add(a);
                }
                TaskD.Vertex root = vs[0];
                List<TaskD.Vertex> stack = new ArrayList<>();
                stack.add(root);
                while (!stack.isEmpty()) {
                    TaskD.Vertex cur = stack.get(stack.size() - 1);
                    if (cur.state == 0) {
                        cur.state = 1;
                        for (TaskD.Vertex v : cur.adj)
                            if (v != cur.parent) {
                                v.parent = cur;
                                stack.add(v);
                            }
                    } else {
                        cur.state = 2;
                        cur.process();
                        stack.remove(stack.size() - 1);
                    }
                }

                if ((n * (long) n - root.bestAnswer) % 2 != 0) throw new RuntimeException();
                out.println((n * (long) n - root.bestAnswer) / 2 + n * (long) (n - 1) / 2);
            }

            static class Vertex implements Comparable<TaskD.Vertex> {
                List<TaskD.Vertex> adj = new ArrayList<>();
                long subtreeSize;
                long bestPathDown;
                long bestAnswer = -1;
                TaskD.Vertex parent;
                int state = 0;

                public void process() {
                    subtreeSize = 1;
                    for (TaskD.Vertex v : adj)
                        if (v != parent) {
                            subtreeSize += v.subtreeSize;
                        }
                    Collections.sort(adj);
                    bestPathDown = subtreeSize * subtreeSize;
                    bestAnswer = n * (long) n;
                    long prevSubtreeSize = -1;
                    for (TaskD.Vertex v : adj)
                        if (v != parent) {
                            bestPathDown = Math.min(bestPathDown, (subtreeSize - v.subtreeSize) * (subtreeSize - v.subtreeSize) + v.bestPathDown);
                            bestAnswer = Math.min(bestAnswer, v.bestPathDown + (n - v.subtreeSize) * (n - v.subtreeSize));
                            bestAnswer = Math.min(bestAnswer, v.bestAnswer);
                            for (TaskD.Vertex prev : pairs) {
                                long curAnswer = v.bestPathDown + (n - v.subtreeSize - prev.subtreeSize) * (n - v.subtreeSize - prev.subtreeSize);
                                if (curAnswer >= bestAnswer) break;
                                curAnswer += prev.bestPathDown;
                                if (curAnswer < bestAnswer) {
                                    bestAnswer = curAnswer;
                                }
                            }
                            if (v.subtreeSize != prevSubtreeSize) {
                                pairs.add(v);
                                prevSubtreeSize = v.subtreeSize;
                            }
                        }
                    pairs.clear();
                }

                public int compareTo(TaskD.Vertex o) {
                    int z = Long.compare(o.subtreeSize, subtreeSize);
                    if (z == 0)
                        z = Long.compare(bestPathDown, o.bestPathDown);
                    return z;
                }

            }

        }

        static class InputReader {
            public BufferedReader reader;
            public StringTokenizer tokenizer;

            public InputReader(InputStream stream) {
                reader = new BufferedReader(new InputStreamReader(stream), 32768);
                tokenizer = null;
            }

            public String next() {
                while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                    try {
                        tokenizer = new StringTokenizer(reader.readLine());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return tokenizer.nextToken();
            }

            public int nextInt() {
                return Integer.parseInt(next());
            }

        }
    }


    private static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(1, 7);
            in.add(n);
            for (int i = 2; i <= n; i++) {
                in.add(String.format("%d %d", nextInt(1, i - 1), i));
            }
            return in.end();
        }
    }
}
