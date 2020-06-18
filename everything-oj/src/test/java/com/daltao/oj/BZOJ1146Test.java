package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.io.UncheckedIOException;
import java.util.List;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.InputStream;

public class BZOJ1146Test {

    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Standard.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setTestTime(1000)
                .build().call());
    }

    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     */
    public static class Main {
        public static void main(String[] args) throws Exception {
            new TaskAdapter().run();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                FastOutput out = new FastOutput(outputStream);
                BZOJ1146 solver = new BZOJ1146();
                solver.solve(1, in, out);
                out.close();
            }
        }

        static class BZOJ1146 {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                int q = in.readInt();

                int[] data = new int[n];
                for (int i = 0; i < n; i++) {
                    data[i] = in.readInt();
                }

                IntegerList allTimes = new IntegerList(n + q);
                allTimes.addAll(data);

                MultiWayIntegerStack edges = new MultiWayIntegerStack(n, n * 2);
                for (int i = 1; i < n; i++) {
                    int a = in.readInt() - 1;
                    int b = in.readInt() - 1;
                    edges.addLast(a, b);
                    edges.addLast(b, a);
                }

                List<Modify> modifyList = new ArrayList<>(q);
                List<Query> queryList = new ArrayList<>(q);
                for (int i = 0; i < q; i++) {
                    int k = in.readInt();
                    int a = in.readInt();
                    int b = in.readInt();
                    if (k == 0) {
                        Modify modify = new Modify();
                        modify.x = a - 1;
                        modify.val = b;
                        modifyList.add(modify);
                        allTimes.add(modify.val);
                    } else {
                        Query query = new Query();
                        query.u = a - 1;
                        query.v = b - 1;
                        query.version = modifyList.size();
                        query.k = k;
                        queryList.add(query);
                    }
                }

                IntegerDiscreteMap dm = new IntegerDiscreteMap(allTimes.getData(), 0, allTimes.size());
                for (int i = 0; i < n; i++) {
                    data[i] = dm.rankOf(data[i]);
                }
                for (Modify modify : modifyList) {
                    modify.val = dm.rankOf(modify.val);
                }

                MoOnTreeBeta mo = new MoOnTreeBeta(edges);
                mo.handle(data, modifyList.toArray(new Modify[0]),
                        queryList.toArray(new Query[0]), new Handler(dm.maxRank()));

                for (Query query : queryList) {
                    if (query.ans == -1) {
                        out.println("invalid request!");
                        continue;
                    }
                    out.println(dm.iThElement(query.ans));
                }
            }

        }

        static class Modify implements MoOnTreeBeta.IntModify {
            int x;
            int val;

            public <Q extends MoOnTreeBeta.VersionQuery> void apply(int[] data, MoOnTreeBeta.IntHandler<Q> handler, boolean[] exists) {
                if (exists[x]) {
                    handler.remove(x, data[x]);
                }
                int oldVal = data[x];
                data[x] = val;
                val = oldVal;
                if (exists[x]) {
                    handler.add(x, data[x]);
                }
            }

            public <Q extends MoOnTreeBeta.VersionQuery> void revoke(int[] data, MoOnTreeBeta.IntHandler<Q> handler, boolean[] exists) {
                apply(data, handler, exists);
            }

        }

        static class IntegerDiscreteMap {
            int[] val;
            int f;
            int t;

            public IntegerDiscreteMap(int[] val, int f, int t) {
                Randomized.randomizedArray(val, f, t);
                Arrays.sort(val, f, t);
                int wpos = f + 1;
                for (int i = f + 1; i < t; i++) {
                    if (val[i] == val[i - 1]) {
                        continue;
                    }
                    val[wpos++] = val[i];
                }
                this.val = val;
                this.f = f;
                this.t = wpos;
            }

            public int rankOf(int x) {
                return Arrays.binarySearch(val, f, t, x) - f;
            }

            public int iThElement(int i) {
                return val[f + i];
            }

            public int maxRank() {
                return t - f - 1;
            }

            public String toString() {
                return Arrays.toString(Arrays.copyOfRange(val, f, t));
            }

        }

        static class Randomized {
            static Random random = new Random();

            public static void randomizedArray(int[] data, int from, int to) {
                to--;
                for (int i = from; i <= to; i++) {
                    int s = nextInt(i, to);
                    int tmp = data[i];
                    data[i] = data[s];
                    data[s] = tmp;
                }
            }

            public static int nextInt(int l, int r) {
                return random.nextInt(r - l + 1) + l;
            }

        }

        static class MultiWayIntegerStack {
            private int[] values;
            private int[] next;
            private int[] heads;
            private int alloc;
            private int stackNum;

            public IntegerIterator iterator(final int queue) {
                return new IntegerIterator() {
                    int ele = heads[queue];


                    public boolean hasNext() {
                        return ele != 0;
                    }


                    public int next() {
                        int ans = values[ele];
                        ele = next[ele];
                        return ans;
                    }
                };
            }

            private void doubleCapacity() {
                int newSize = Math.max(next.length + 10, next.length * 2);
                next = Arrays.copyOf(next, newSize);
                values = Arrays.copyOf(values, newSize);
            }

            public void alloc() {
                alloc++;
                if (alloc >= next.length) {
                    doubleCapacity();
                }
                next[alloc] = 0;
            }

            public int stackNumber() {
                return stackNum;
            }

            public MultiWayIntegerStack(int qNum, int totalCapacity) {
                values = new int[totalCapacity + 1];
                next = new int[totalCapacity + 1];
                heads = new int[qNum];
                stackNum = qNum;
            }

            public void addLast(int qId, int x) {
                alloc();
                values[alloc] = x;
                next[alloc] = heads[qId];
                heads[qId] = alloc;
            }

            public String toString() {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < stackNum; i++) {
                    builder.append(i).append(": ");
                    for (IntegerIterator iterator = iterator(i); iterator.hasNext(); ) {
                        builder.append(iterator.next()).append(",");
                    }
                    if (builder.charAt(builder.length() - 1) == ',') {
                        builder.setLength(builder.length() - 1);
                    }
                    builder.append('\n');
                }
                return builder.toString();
            }

        }

        static class CachedLog2 {
            private static final int BITS = 16;
            private static final int LIMIT = 1 << BITS;
            private static final byte[] CACHE = new byte[LIMIT];

            static {
                int b = 0;
                for (int i = 0; i < LIMIT; i++) {
                    while ((1 << (b + 1)) <= i) {
                        b++;
                    }
                    CACHE[i] = (byte) b;
                }
            }

            public static int floorLog(int x) {
                return x < LIMIT ? CACHE[x] : (BITS + CACHE[x >>> BITS]);
            }

        }

        static class Query implements MoOnTreeBeta.VersionQuery {
            int u;
            int v;
            int version;
            int ans;
            int k;

            public int getU() {
                return u;
            }

            public int getV() {
                return v;
            }

            public int getVersion() {
                return version;
            }

        }

        static class SequenceUtils {
            public static boolean equal(int[] a, int al, int ar, int[] b, int bl, int br) {
                if ((ar - al) != (br - bl)) {
                    return false;
                }
                for (int i = al, j = bl; i <= ar; i++, j++) {
                    if (a[i] != b[j]) {
                        return false;
                    }
                }
                return true;
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

        static class Handler implements MoOnTreeBeta.IntHandler<Query> {
            int[] cnts;
            int[] summary;
            int blockSize;

            public Handler(int n) {
                cnts = new int[n + 1];
                blockSize = (int) Math.ceil(Math.sqrt(n + 1));
                summary = new int[n / blockSize + 1];
            }

            public void add(int node, int x) {
                cnts[x]++;
                summary[x / blockSize]++;
            }

            public void remove(int node, int x) {
                cnts[x]--;
                summary[x / blockSize]--;
            }

            public void answer(Query query) {
                query.ans = -1;
                int k = query.k;
                for (int i = summary.length - 1; i >= 0; i--) {
                    if (k > summary[i]) {
                        k -= summary[i];
                        continue;
                    }

                    for (int j = Math.min((i + 1) * blockSize, cnts.length) - 1; ; j--) {
                        if (k > cnts[j]) {
                            k -= cnts[j];
                            continue;
                        }
                        query.ans = j;
                        return;
                    }
                }
            }

        }

        static interface IntegerIterator {
            boolean hasNext();

            int next();

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

            public FastOutput println(int c) {
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

        static class MoOnTreeBeta {
            private MultiWayIntegerStack edges;
            private boolean[] odd;
            private int[] eulerTour;
            private int eulerTourTail = 0;
            private int[] begin;
            private int[] end;
            private LcaOnTree lcaOnTree;

            public MoOnTreeBeta(MultiWayIntegerStack edges) {
                this.edges = edges;
                odd = new boolean[edges.stackNumber()];
                eulerTour = new int[edges.stackNumber() * 2];
                begin = new int[edges.stackNumber()];
                end = new int[edges.stackNumber()];
                dfs(0, -1);
                lcaOnTree = new LcaOnTree(edges, 0);
            }

            private void dfs(int root, int p) {
                begin[root] = eulerTourTail;
                eulerTour[eulerTourTail++] = root;

                for (IntegerIterator iterator = edges.iterator(root); iterator.hasNext(); ) {
                    int node = iterator.next();
                    if (node == p) {
                        continue;
                    }
                    dfs(node, root);
                }

                end[root] = eulerTourTail;
                eulerTour[eulerTourTail++] = root;
            }

            private <Q extends MoOnTreeBeta.Query> void invert(int node, int x, MoOnTreeBeta.IntHandler<Q> handler) {
                odd[node] = !odd[node];
                if (odd[node]) {
                    handler.add(node, x);
                } else {
                    handler.remove(node, x);
                }
            }

            public <Q extends MoOnTreeBeta.VersionQuery> void handle(int[] data, MoOnTreeBeta.IntModify[] modifies, Q[] queries, MoOnTreeBeta.IntHandler<Q> handler) {
                handle(data, modifies, queries, handler, (int) Math.ceil(Math.pow(eulerTour.length, 2.0 / 3)));
            }

            public <Q extends MoOnTreeBeta.VersionQuery> void handle(int[] data, MoOnTreeBeta.IntModify[] modifies, Q[] queries, MoOnTreeBeta.IntHandler<Q> handler,
                                                                     int blockSize) {
                if (data.length == 0 || queries.length == 0) {
                    return;
                }

                MoOnTreeBeta.QueryWrapper<Q>[] wrappers = new MoOnTreeBeta.QueryWrapper[queries.length];
                for (int i = 0; i < queries.length; i++) {
                    Q q = queries[i];
                    wrappers[i] = new MoOnTreeBeta.QueryWrapper<Q>();
                    wrappers[i].q = q;
                    int u = q.getU();
                    int v = q.getV();
                    int ul = begin[u];
                    int ur = end[u];
                    int vl = begin[v];
                    int vr = end[v];

                    if (ur > vr) {
                        int tmp = ul;
                        ul = vl;
                        vl = tmp;

                        tmp = ur;
                        ur = vr;
                        vr = tmp;
                    }

                    if (ur < vl) {
                        wrappers[i].l = ur;
                        wrappers[i].r = vl;
                        wrappers[i].extra = end[lcaOnTree.lca(u, v)];
                    } else {
                        wrappers[i].l = ur;
                        wrappers[i].r = vr - 1;
                        wrappers[i].extra = vr;
                    }
                }

                Arrays.fill(odd, false);
                Arrays.sort(wrappers, (a, b) -> {
                    int ans = a.l / blockSize - b.l / blockSize;
                    if (ans == 0) {
                        ans = a.q.getVersion() / blockSize - b.q.getVersion() / blockSize;
                    }
                    if (ans == 0) {
                        ans = a.r - b.r;
                    }
                    return ans;
                });

                int l = wrappers[0].l;
                int r = l - 1;
                int v = 0;
                for (MoOnTreeBeta.QueryWrapper<Q> wrapper : wrappers) {
                    int ll = wrapper.l;
                    int rr = wrapper.r;
                    int vv = wrapper.q.getVersion();

                    while (v < vv) {
                        modifies[v].apply(data, handler, odd);
                        v++;
                    }
                    while (v > vv) {
                        v--;
                        modifies[v].revoke(data, handler, odd);
                    }
                    while (l > ll) {
                        l--;
                        invert(eulerTour[l], data[eulerTour[l]], handler);
                    }
                    while (r < rr) {
                        r++;
                        invert(eulerTour[r], data[eulerTour[r]], handler);
                    }
                    while (l < ll) {
                        invert(eulerTour[l], data[eulerTour[l]], handler);
                        l++;
                    }
                    while (r > rr) {
                        invert(eulerTour[r], data[eulerTour[r]], handler);
                        r--;
                    }
                    invert(eulerTour[wrapper.extra], data[eulerTour[wrapper.extra]], handler);
                    handler.answer(wrapper.q);
                    invert(eulerTour[wrapper.extra], data[eulerTour[wrapper.extra]], handler);
                }
            }

            private static class QueryWrapper<Q extends MoOnTreeBeta.Query> {
                int l;
                int r;
                int extra;
                Q q;

            }

            public interface Query {
                int getU();

                int getV();

            }

            public interface IntHandler<Q extends MoOnTreeBeta.Query> {
                void add(int node, int x);

                void remove(int node, int x);

                void answer(Q q);

            }

            public interface VersionQuery extends MoOnTreeBeta.Query {
                int getVersion();

            }

            public interface IntModify {
                <Q extends MoOnTreeBeta.VersionQuery> void apply(int[] data, MoOnTreeBeta.IntHandler<Q> handler, boolean[] exists);

                <Q extends MoOnTreeBeta.VersionQuery> void revoke(int[] data, MoOnTreeBeta.IntHandler<Q> handler, boolean[] exists);

            }

        }

        static class IntegerList implements Cloneable {
            private int size;
            private int cap;
            private int[] data;
            private static final int[] EMPTY = new int[0];

            public int[] getData() {
                return data;
            }

            public IntegerList(int cap) {
                this.cap = cap;
                if (cap == 0) {
                    data = EMPTY;
                } else {
                    data = new int[cap];
                }
            }

            public IntegerList(IntegerList list) {
                this.size = list.size;
                this.cap = list.cap;
                this.data = Arrays.copyOf(list.data, size);
            }

            public IntegerList() {
                this(0);
            }

            public void ensureSpace(int req) {
                if (req > cap) {
                    while (cap < req) {
                        cap = Math.max(cap + 10, 2 * cap);
                    }
                    data = Arrays.copyOf(data, cap);
                }
            }

            public void add(int x) {
                ensureSpace(size + 1);
                data[size++] = x;
            }

            public void addAll(int[] x) {
                addAll(x, 0, x.length);
            }

            public void addAll(int[] x, int offset, int len) {
                ensureSpace(size + len);
                System.arraycopy(x, offset, data, size, len);
                size += len;
            }

            public void addAll(IntegerList list) {
                addAll(list.data, 0, list.size);
            }

            public int size() {
                return size;
            }

            public int[] toArray() {
                return Arrays.copyOf(data, size);
            }

            public String toString() {
                return Arrays.toString(toArray());
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof IntegerList)) {
                    return false;
                }
                IntegerList other = (IntegerList) obj;
                return SequenceUtils.equal(data, 0, size - 1, other.data, 0, other.size - 1);
            }

            public int hashCode() {
                int h = 1;
                for (int i = 0; i < size; i++) {
                    h = h * 31 + Integer.hashCode(data[i]);
                }
                return h;
            }

            public IntegerList clone() {
                IntegerList ans = new IntegerList();
                ans.addAll(this);
                return ans;
            }

        }

        static class LcaOnTree {
            int[] parent;
            int[] preOrder;
            int[] i;
            int[] head;
            int[] a;
            int time;

            void dfs1(MultiWayIntegerStack tree, int u, int p) {
                parent[u] = p;
                i[u] = preOrder[u] = time++;
                for (IntegerIterator iterator = tree.iterator(u); iterator.hasNext(); ) {
                    int v = iterator.next();
                    if (v == p) continue;
                    dfs1(tree, v, u);
                    if (Integer.lowestOneBit(i[u]) < Integer.lowestOneBit(i[v])) {
                        i[u] = i[v];
                    }
                }
                head[i[u]] = u;
            }

            void dfs2(MultiWayIntegerStack tree, int u, int p, int up) {
                a[u] = up | Integer.lowestOneBit(i[u]);
                for (IntegerIterator iterator = tree.iterator(u); iterator.hasNext(); ) {
                    int v = iterator.next();
                    if (v == p) continue;
                    dfs2(tree, v, u, a[u]);
                }
            }

            public LcaOnTree(MultiWayIntegerStack tree, int root) {
                int n = tree.stackNumber();
                preOrder = new int[n];
                i = new int[n];
                head = new int[n];
                a = new int[n];
                parent = new int[n];

                dfs1(tree, root, -1);
                dfs2(tree, root, -1, 0);
            }

            private int enterIntoStrip(int x, int hz) {
                if (Integer.lowestOneBit(i[x]) == hz)
                    return x;
                int hw = 1 << CachedLog2.floorLog(a[x] & (hz - 1));
                return parent[head[i[x] & -hw | hw]];
            }

            public int lca(int x, int y) {
                int hb = i[x] == i[y] ? Integer.lowestOneBit(i[x]) : (1 << CachedLog2.floorLog(i[x] ^ i[y]));
                int hz = Integer.lowestOneBit(a[x] & a[y] & -hb);
                int ex = enterIntoStrip(x, hz);
                int ey = enterIntoStrip(y, hz);
                return preOrder[ex] < preOrder[ey] ? ex : ey;
            }

        }
    }


    public static class Standard {
        public static void main(String[] args) throws Exception {
            boolean local = false;
            boolean async = true;

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

            int[] delay;


            public void solve() {
                int n = io.readInt();
                int q = io.readInt();
                List<ModifiableMoOnTree.Query> queries = new ArrayList(q);
                List<ModifiableMoOnTree.Modification> modifications = new ArrayList(q);
                int[] allValues = new int[n + q];
                int allValuesTail = 0;
                delay = new int[n];
                for (int i = 0; i < n; i++) {
                    delay[i] = io.readInt();
                    allValues[allValuesTail++] = delay[i];
                }
                ModifiableMoOnTree mo = new ModifiableMoOnTree(n);
                for (int i = 1; i < n; i++) {
                    int a = io.readInt() - 1;
                    int b = io.readInt() - 1;
                    mo.addEdge(a, b);
                }
                for (int i = 0; i < q; i++) {
                    int k = io.readInt();
                    int a = io.readInt();
                    int b = io.readInt();
                    if (k == 0) {
                        ModifiableMoOnTree.Modification mod = new ModifiableMoOnTree.Modification();
                        mod.x = a - 1;
                        mod.from = delay[mod.x];
                        mod.to = delay[mod.x] = b;
                        modifications.add(mod);
                        allValues[allValuesTail++] = b;
                    } else {
                        ModifiableMoOnTree.Query query = new ModifiableMoOnTree.Query();
                        query.u = a - 1;
                        query.v = b - 1;
                        query.k = k;
                        query.version = modifications.size();
                        queries.add(query);
                    }
                }

                DiscreteMap map = new DiscreteMap(allValues, 0, allValuesTail);
                for (int i = 0; i < n; i++) {
                    delay[i] = map.rankOf(delay[i]);
                }
                for (ModifiableMoOnTree.Modification modification : modifications) {
                    modification.from = map.rankOf(modification.from);
                    modification.to = map.rankOf(modification.to);
                }


                mo.solve(queries.toArray(new ModifiableMoOnTree.Query[0]),
                        modifications.toArray(new ModifiableMoOnTree.Modification[0]),
                        new AssistantImpl(), modifications.size());


                for (ModifiableMoOnTree.Query query : queries) {
                    if (query.answer == -1) {
                        io.cache.append("invalid request!");
                    } else {
                        io.cache.append(map.iThElement(query.answer));
                    }
                    io.cache.append('\n');
                }
            }

            public class AssistantImpl implements ModifiableMoOnTree.Assistant {
                int block = 500;
                int[] cnts = new int[250000];
                int[] cnts2 = new int[500];
                int total = 0;

                @Override
                public void apply(ModifiableMoOnTree.Modification m) {
                    delay[m.x] = m.to;
                }

                @Override
                public void revoke(ModifiableMoOnTree.Modification m) {
                    delay[m.x] = m.from;
                }

                @Override
                public void add(int i) {
                    cnts[delay[i]]++;
                    cnts2[delay[i] / block]++;
                    total++;
                }

                @Override
                public void remove(int i) {
                    cnts[delay[i]]--;
                    cnts2[delay[i] / block]--;
                    total--;
                }

                @Override
                public void query(ModifiableMoOnTree.Query q) {
                    int k = q.k;
                    if (total < k) {
                        q.answer = -1;
                        return;
                    }
                    k = total + 1 - k;
                    int whichBlock = 0;

                    for (; whichBlock < 500 && cnts2[whichBlock] < k; whichBlock++) {
                        k -= cnts2[whichBlock];
                    }

                    int whichValue = whichBlock * block;
                    for (; cnts[whichValue] < k; whichValue++) {
                        k -= cnts[whichValue];
                    }
                    q.answer = whichValue;
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

        /**
         * Created by dalt on 2018/6/1.
         */
        public static class Randomized {
            static Random random = new Random();

            public static double nextDouble(double min, double max) {
                return random.nextDouble() * (max - min) + min;
            }

            public static void randomizedArray(int[] data, int from, int to) {
                to--;
                for (int i = from; i <= to; i++) {
                    int s = nextInt(i, to);
                    int tmp = data[i];
                    data[i] = data[s];
                    data[s] = tmp;
                }
            }

            public static void randomizedArray(long[] data, int from, int to) {
                to--;
                for (int i = from; i <= to; i++) {
                    int s = nextInt(i, to);
                    long tmp = data[i];
                    data[i] = data[s];
                    data[s] = tmp;
                }
            }

            public static void randomizedArray(double[] data, int from, int to) {
                to--;
                for (int i = from; i <= to; i++) {
                    int s = nextInt(i, to);
                    double tmp = data[i];
                    data[i] = data[s];
                    data[s] = tmp;
                }
            }

            public static void randomizedArray(float[] data, int from, int to) {
                to--;
                for (int i = from; i <= to; i++) {
                    int s = nextInt(i, to);
                    float tmp = data[i];
                    data[i] = data[s];
                    data[s] = tmp;
                }
            }

            public static <T> void randomizedArray(T[] data, int from, int to) {
                to--;
                for (int i = from; i <= to; i++) {
                    int s = nextInt(i, to);
                    T tmp = data[i];
                    data[i] = data[s];
                    data[s] = tmp;
                }
            }

            public static int nextInt(int l, int r) {
                return random.nextInt(r - l + 1) + l;
            }
        }

        public static class DiscreteMap {
            int[] val;
            int f;
            int t;

            public DiscreteMap(int[] val, int f, int t) {
                Randomized.randomizedArray(val, f, t);
                Arrays.sort(val, f, t);
                int wpos = f + 1;
                for (int i = f + 1; i < t; i++) {
                    if (val[i] == val[i - 1]) {
                        continue;
                    }
                    val[wpos++] = val[i];
                }
                this.val = val;
                this.f = f;
                this.t = wpos;
            }

            /**
             * Return 0, 1, so on
             */
            public int rankOf(int x) {
                return Arrays.binarySearch(val, f, t, x) - f;
            }

            /**
             * Get the i-th smallest element
             */
            public int iThElement(int i) {
                return val[f + i];
            }

            public int minRank() {
                return 0;
            }

            public int maxRank() {
                return t - f - 1;
            }
        }


        /**
         * Created by dalt on 2018/5/20.
         */
        public static class St<T> {
            //st[i][j] means the min value between [i, i + 2^j),
            //so st[i][j] equals to min(st[i][j - 1], st[i + 2^(j - 1)][j - 1])
            Object[][] st;
            Comparator<T> comparator;

            int[] floorLogTable;

            public St(Object[] data, int length, Comparator<T> comparator) {
                int m = floorLog2(length);
                st = new Object[m + 1][length];
                this.comparator = comparator;
                for (int i = 0; i < length; i++) {
                    st[0][i] = data[i];
                }
                for (int i = 0; i < m; i++) {
                    int interval = 1 << i;
                    for (int j = 0; j < length; j++) {
                        if (j + interval < length) {
                            st[i + 1][j] = min((T) st[i][j], (T) st[i][j + interval]);
                        } else {
                            st[i + 1][j] = st[i][j];
                        }
                    }
                }

                floorLogTable = new int[length + 1];
                int log = 1;
                for (int i = 0; i <= length; i++) {
                    if ((1 << log) <= i) {
                        log++;
                    }
                    floorLogTable[i] = log - 1;
                }
            }

            public static int floorLog2(int x) {
                return 31 - Integer.numberOfLeadingZeros(x);
            }

            private T min(T a, T b) {
                return comparator.compare(a, b) <= 0 ? a : b;
            }

            public static int ceilLog2(int x) {
                return 32 - Integer.numberOfLeadingZeros(x - 1);
            }

            /**
             * query the min value in [left,right]
             */
            public T query(int left, int right) {
                int queryLen = right - left + 1;
                int bit = floorLogTable[queryLen];
                //x + 2^bit == right + 1
                //So x should be right + 1 - 2^bit - left=queryLen - 2^bit
                return min((T) st[bit][left], (T) st[bit][right + 1 - (1 << bit)]);
            }
        }


        public static class ModifiableMoOnTree {
            private static class Node {
                List<Node> next = new ArrayList(2);
                int id;
                int close;
                int open;
                boolean added;
                int dfn;

                @Override
                public String toString() {
                    return "" + id;
                }
            }

            Node[] nodes;

            public ModifiableMoOnTree(int n) {
                nodes = new Node[n];
                for (int i = 0; i < n; i++) {
                    nodes[i] = new Node();
                    nodes[i].id = i;
                }
            }

            public void addEdge(int a, int b) {
                nodes[a].next.add(nodes[b]);
                nodes[b].next.add(nodes[a]);
            }

            private boolean preHandled = false;

            private void preHandle() {
                if (preHandled) {
                    return;
                }
                preHandled = true;
                eulerTrace = new Node[nodes.length * 2];
                lcaTrace = new Node[nodes.length * 2 - 1];
                dfs(nodes[0], null);
                st = new St(lcaTrace, lcaTraceTail, new Comparator<Node>() {
                    @Override
                    public int compare(Node a, Node b) {
                        return a.dfn - b.dfn;
                    }
                });
            }

            Node[] eulerTrace;
            int eulerTraceTail = 0;
            Node[] lcaTrace;
            int lcaTraceTail = 0;
            St<Node> st;


            private void dfs(Node root, Node father) {
                root.open = eulerTraceTail;
                eulerTrace[eulerTraceTail++] = root;
                root.dfn = lcaTraceTail;
                lcaTrace[lcaTraceTail++] = root;
                for (Node node : root.next) {
                    if (node == father) {
                        continue;
                    }
                    dfs(node, root);
                    lcaTrace[lcaTraceTail++] = root;
                }
                root.close = eulerTraceTail;
                eulerTrace[eulerTraceTail++] = root;
            }

            public void solve(Query[] queries, Modification[] modifications, Assistant assistant, int now) {
                preHandle();

                final int blockSize = Math.max((int) Math.pow(nodes.length, 2.0 / 3), 1);

                for (Query q : queries) {
                    Node u = nodes[q.u];
                    Node v = nodes[q.v];
                    if (u.open > v.open) {
                        Node tmp = u;
                        u = v;
                        v = tmp;
                    }
                    if (u.close <= v.open) {
                        q.l = u.close;
                        q.r = v.open;
                    } else {
                        q.l = v.close;
                        q.r = u.close - 1;
                    }
                    q.lca = st.query(Math.min(u.dfn, v.dfn), Math.max(u.dfn, v.dfn)).id;
                }

                Arrays.sort(queries, new Comparator<Query>() {
                    @Override
                    public int compare(Query a, Query b) {
                        int r = a.l / blockSize - b.l / blockSize;
                        if (r == 0) {
                            r = a.version / blockSize - b.version / blockSize;
                        }
                        if (r == 0) {
                            r = a.r - b.r;
                        }
                        return r;
                    }
                });

                int l = 0;
                int r = -1;
                for (Node node : nodes) {
                    node.added = false;
                }


                for (Query q : queries) {
                    while (now < q.version) {
                        Modification m = modifications[now];
                        Node x = nodes[m.x];
                        if (x.added) {
                            assistant.remove(x.id);
                        }
                        assistant.apply(m);
                        if (x.added) {
                            assistant.add(x.id);
                        }
                        now++;
                    }
                    while (now > q.version) {
                        now--;
                        Modification m = modifications[now];
                        Node x = nodes[m.x];
                        if (x.added) {
                            assistant.remove(x.id);
                        }
                        assistant.revoke(m);
                        if (x.added) {
                            assistant.add(x.id);
                        }
                    }
                    while (r < q.r) {
                        r++;
                        Node x = eulerTrace[r];
                        if (x.added) {
                            assistant.remove(x.id);
                        } else {
                            assistant.add(x.id);
                        }
                        x.added = !x.added;
                    }
                    while (l > q.l) {
                        l--;
                        Node x = eulerTrace[l];
                        if (x.added) {
                            assistant.remove(x.id);
                        } else {
                            assistant.add(x.id);
                        }
                        x.added = !x.added;
                    }
                    while (r > q.r) {
                        Node x = eulerTrace[r];
                        if (x.added) {
                            assistant.remove(x.id);
                        } else {
                            assistant.add(x.id);
                        }
                        x.added = !x.added;
                        r--;
                    }
                    while (l < q.l) {
                        Node x = eulerTrace[l];
                        if (x.added) {
                            assistant.remove(x.id);
                        } else {
                            assistant.add(x.id);
                        }
                        x.added = !x.added;
                        l++;
                    }

                    Node lca = nodes[q.lca];
                    if (lca.added) {
                        assistant.remove(q.lca);
                    } else {
                        assistant.add(q.lca);
                    }
                    lca.added = !lca.added;
                    assistant.query(q);
                    if (lca.added) {
                        assistant.remove(q.lca);
                    } else {
                        assistant.add(q.lca);
                    }
                    lca.added = !lca.added;
                }

            }

            public static class Query {
                int l;
                int r;
                int version;
                int u;
                int v;
                int answer;
                int k;
                int lca;

                @Override
                public String toString() {
                    return "(" + u + "," + v + ")[" + version + "]";
                }
            }

            public static class Modification {
                int x;
                int from;
                int to;

                @Override
                public String toString() {
                    return x + "[" + from + "->" + to + "]";
                }
            }

            public interface Assistant {
                void apply(Modification m);

                void revoke(Modification m);

                void add(int i);

                void remove(int i);

                void query(Query q);
            }
        }
    }

    static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 1000);
            int m = nextInt(1, 1000);
            input.add(n).add(m);
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                builder.append(nextInt(1, 1000))
                        .append(' ');
            }
            input.add(builder.toString());
            for (int i = 2; i <= n; i++) {
                input.add(String.format("%d %d", nextInt(1, i - 1), i));
            }
            for (int i = 0; i < m; i++) {
                int k = nextInt(0, 1) == 0 ? 0 : nextInt(1, n);
                int a;
                int b;
                if (k == 0) {
                    a = nextInt(1, n);
                    b = nextInt(1, 1000);
                } else {
                    a = nextInt(1, n);
                    b = nextInt(1, n);
                }
                input.add(String.format("%d %d %d", k, a, b));
            }
            return input.end();
        }
    }
}
