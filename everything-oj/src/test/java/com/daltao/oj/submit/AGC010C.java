package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.UncheckedIOException;
import java.util.List;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
public class AGC010C {

    @Test
    public void test(){
        Assert.assertTrue(new TestCaseExecutor.Builder()
        .setTestTime(1000)
        .setInputFactory(new Generator())
        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
        .build().call());
    }

    /**
     * Built using CHelper plug-in Actual solution is at the top
     *
     * @author daltao
     */
    public static class Task {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "daltao", 1 << 27);
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
                Cleaning solver = new Cleaning();
                solver.solve(1, in, out);
                out.close();
            }
        }
        static class Cleaning {
            boolean valid = true;

            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                Node[] nodes = new Node[n + 1];
                for (int i = 1; i <= n; i++) {
                    nodes[i] = new Node();
                    nodes[i].id = i;
                    nodes[i].stone = in.readInt();
                }

                for (int i = 1; i < n; i++) {
                    Node a = nodes[in.readInt()];
                    Node b = nodes[in.readInt()];
                    a.next.add(b);
                    b.next.add(a);
                }

                if (n == 2) {
                    out.println(nodes[1].stone == nodes[2].stone ? "YES" : "NO");
                    return;
                }

                for (int i = 1; i <= n; i++) {
                    if (nodes[i].next.size() >= 2) {
                        SequenceUtils.swap(nodes, 1, i);
                        break;
                    }
                }
                dfs(nodes[1], null);
                out.println(valid && nodes[1].stone == 0 ? "YES" : "NO");
            }

            public void dfs(Node root, Node father) {
                root.next.remove(father);
                if (root.next.isEmpty()) {
                    return;
                }
                long max = 0;
                for (Node node : root.next) {
                    dfs(node, root);
                    root.sum += node.stone;
                    max = Math.max(node.stone, max);
                }
                long k = root.sum - root.stone;
                if (root.sum - max < k) {
                    valid = false;
                }
                root.stone = root.sum - 2 * k;
                if (root.stone < 0) {
                    valid = false;
                }
            }

        }
        static class Node {
            List<Node> next = new ArrayList<>();
            long stone;
            long sum;
            int id;

            public String toString() {
                return "" + id;
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
        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(10 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput println(String c) {
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

        }
        static class SequenceUtils {
            public static <T> void swap(T[] data, int i, int j) {
                T tmp = data[i];
                data[i] = data[j];
                data[j] = tmp;
            }

        }
    }



    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     */
    public static class Main {
        public static void main(String[] args) {
            InputStream inputStream = System.in;
            OutputStream outputStream = System.out;
            InputReader in = new InputReader(inputStream);
            PrintWriter out = new PrintWriter(outputStream);
            TaskC solver = new TaskC();
            solver.solve(1, in, out);
            out.close();
        }

        static class TaskC {
            public void solve(int testNumber, InputReader in, PrintWriter out) {
                int n = in.nextInt();
                TaskC.Vertex[] vs = new TaskC.Vertex[n];
                for (int i = 0; i < n; ++i) vs[i] = new TaskC.Vertex(in.nextInt());
                for (int i = 0; i < n - 1; ++i) {
                    TaskC.Vertex a = vs[in.nextInt() - 1];
                    TaskC.Vertex b = vs[in.nextInt() - 1];
                    a.adj.add(b);
                    b.adj.add(a);
                }
                if (n == 2) {
                    if (vs[0].stones == vs[1].stones) {
                        out.println("YES");
                    } else {
                        out.println("NO");
                    }
                    return;
                }
                TaskC.Vertex root = null;
                for (TaskC.Vertex v : vs)
                    if (v.adj.size() > 1) {
                        root = v;
                        break;
                    }
                long got = root.dfs(null);
                if (got == 0) out.println("YES");
                else out.println("NO");
            }

            static class Vertex {
                long stones;
                List<TaskC.Vertex> adj = new ArrayList<>();

                public Vertex(int stones) {
                    this.stones = stones;
                }

                public long dfs(TaskC.Vertex parent) {
                    if (adj.size() == 1) return stones;
                    long sum = 0;
                    long max = 0;
                    for (TaskC.Vertex v : adj) {
                        if (v == parent) continue;
                        long got = v.dfs(this);
                        if (got < 0) return got;
                        sum += got;
                        max = Math.max(max, got);
                    }
                    if (stones > sum) {
                        return -1;
                    }
                    if (stones * 2 < sum) {
                        return -1;
                    }
                    long connect = sum - stones;
                    if (sum - max < connect) {
                        return -1;
                    }
                    return sum - 2 * connect;
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



    public static class Generator extends RandomFactory{
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(2, 6);
            in.add(n);
            StringBuilder builder = new StringBuilder();
            for(int i = 1; i <= n; i++){
                builder.append(nextInt(1, 5)).append(' ');
            }
            in.add(builder.toString());
            for(int i = 2; i <= n; i++){
                in.add(String.format("%d %d", i, nextInt(1, i - 1)));
            }

            return in.end();
        }
    }
}
