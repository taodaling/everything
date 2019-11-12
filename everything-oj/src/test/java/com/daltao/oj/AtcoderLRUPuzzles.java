package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.InputMismatchException;

public class AtcoderLRUPuzzles {

    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                .setInputFactory(new Generator())
                .setTestTime(10000)
                .build().call());
    }


    /**
     * Built using CHelper plug-in Actual solution is at the top
     */
    public static class Task {
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
                TaskE solver = new TaskE();
                solver.solve(1, in, out);
                out.close();
            }
        }
        static class TaskE {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                int m = in.readInt();
                int n = in.readInt();
                int q = in.readInt();
                int[] a = new int[q + 1];
                for (int i = 1; i <= q; i++) {
                    a[i] = in.readInt();
                }
                Order[] order = new Order[n + 1];
                for (int i = 1; i <= n; i++) {
                    order[i] = new Order();
                    order[i].val = i;
                    order[i].last = -i;
                }
                IntList[] list = new IntList[n + 1];
                for (int i = 1; i <= n; i++) {
                    list[i] = new IntList();
                }
                for (int i = 1; i <= q; i++) {
                    list[a[i]].add(i);
                    order[a[i]].last = i;
                }

                Arrays.sort(order, 1, n + 1, (x, y) -> -Integer.compare(x.last, y.last));

                int incUntil = n;
                while (incUntil > 1 && order[incUntil - 1].val < order[incUntil].val) {
                    incUntil--;
                }

                for (int i = 1; i <= n; i++) {
                    SequenceUtils.reverse(list[i].getData(), 0, list[i].size());
                }

                IntList tmp = new IntList(n);
                for (int i = 1; i < incUntil; i++) {
                    int x = order[i].val;
                    int y = order[i + 1].val;
                    if (list[x].size() < m) {
                        no(out);
                        return;
                    }

                    if (i + 1 == incUntil) {
                        break;
                    }

                    tmp.clear();
                    for (int j = 0, k = 0; j < m && k < list[y].size(); j++) {
                        int index = list[x].get(j);
                        while (k < list[y].size() && list[y].get(k) > index) {
                            k++;
                        }
                        if (k == list[y].size()) {
                            no(out);
                            return;
                        }
                        tmp.add(list[y].get(k));
                        k++;
                    }

                    list[y].clear();
                    list[y].addAll(tmp);
                }

                yes(out);
            }

            public void no(FastOutput out) {
                out.println("No");
            }

            public void yes(FastOutput out) {
                out.println("Yes");
            }

        }
        static class IntList {
            private int size;
            private int cap;
            private int[] data;
            private static final int[] EMPTY = new int[0];

            public int[] getData() {
                return data;
            }

            public IntList(int cap) {
                this.cap = cap;
                if (cap == 0) {
                    data = EMPTY;
                } else {
                    data = new int[cap];
                }
            }

            public IntList(IntList list) {
                this.size = list.size;
                this.cap = list.cap;
                this.data = Arrays.copyOf(list.data, size);
            }

            public IntList() {
                this(0);
            }

            public void ensureSpace(int need) {
                int req = size + need;
                if (req > cap) {
                    while (cap < req) {
                        cap = Math.max(cap + 10, 2 * cap);
                    }
                    data = Arrays.copyOf(data, cap);
                }
            }

            private void checkRange(int i) {
                if (i < 0 || i >= size) {
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            public int get(int i) {
                checkRange(i);
                return data[i];
            }

            public void add(int x) {
                ensureSpace(1);
                data[size++] = x;
            }

            public void addAll(int[] x, int offset, int len) {
                ensureSpace(len);
                System.arraycopy(x, offset, data, size, len);
                size += len;
            }

            public void addAll(IntList list) {
                addAll(list.data, 0, list.size);
            }

            public int size() {
                return size;
            }

            public int[] toArray() {
                return Arrays.copyOf(data, size);
            }

            public void clear() {
                size = 0;
            }

            public String toString() {
                return Arrays.toString(toArray());
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof IntList)) {
                    return false;
                }
                IntList other = (IntList) obj;
                return SequenceUtils.equal(data, other.data, 0, size - 1, 0, other.size - 1);
            }

        }
        static class Order {
            int val;
            int last;

        }
        static class SequenceUtils {
            public static void swap(int[] data, int i, int j) {
                int tmp = data[i];
                data[i] = data[j];
                data[j] = tmp;
            }

            public static void reverse(int[] data, int f, int t) {
                int l = f, r = t - 1;
                while (l < r) {
                    swap(data, l, r);
                    l++;
                    r--;
                }
            }

            public static boolean equal(int[] a, int[] b, int al, int ar, int bl, int br) {
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

            public String toString() {
                return cache.toString();
            }

        }
    }




    public static class Main {
        static InputStream is;
        static PrintWriter out;
        static String INPUT = "";

        static void solve() {
            int n = ni(), m = ni();
            int Q = ni();
            int[] a = na(Q);
            for (int i = 0; i < Q; i++) a[i]--;

            boolean[] used = new boolean[m];
            int[] last = new int[m];
            int p = 0;
            for (int i = Q - 1; i >= 0; i--) {
                if (!used[a[i]]) {
                    used[a[i]] = true;
                    last[p++] = a[i];
                }
            }
            for (int i = 0; i < m; i++) {
                if (!used[i]) {
                    last[p++] = i;
                }
            }
            int[] ilast = new int[m];
            for (int i = 0; i < m; i++) ilast[last[i]] = i;

            int[] ps = new int[m];
            for (int i = Q - 1; i >= 0; i--) {
                int id = ilast[a[i]];
                if (ps[id] < n && (id == 0 || ps[id - 1] >= ps[id] + 1)) {
                    ps[id]++;
                } else if (ps[id] < n && (id > 0 && ps[id - 1] == 0)) {
                    out.println("No");
                    return;
                }
            }
            int nat = m - 1;
            for (int i = m - 2; i >= 0; i--) {
                if (last[i] < last[i + 1]) {
                    nat--;
                } else {
                    break;
                }
            }
            for (int i = 0; i < nat; i++) {
                if (ps[i] != n) {
                    out.println("No");
                    return;
                }
            }
            out.println("Yes");
        }

        public static void main(String[] args) throws Exception {
            long S = System.currentTimeMillis();
            is = INPUT.isEmpty() ? System.in : new ByteArrayInputStream(INPUT.getBytes());
            out = new PrintWriter(System.out);

            solve();
            out.flush();
            long G = System.currentTimeMillis();
            tr(G - S + "ms");
        }

        private static boolean eof() {
            if (lenbuf == -1) return true;
            int lptr = ptrbuf;
            while (lptr < lenbuf) if (!isSpaceChar(inbuf[lptr++])) return false;

            try {
                is.mark(1000);
                while (true) {
                    int b = is.read();
                    if (b == -1) {
                        is.reset();
                        return true;
                    } else if (!isSpaceChar(b)) {
                        is.reset();
                        return false;
                    }
                }
            } catch (IOException e) {
                return true;
            }
        }

        private static byte[] inbuf = new byte[1024];
        static int lenbuf = 0, ptrbuf = 0;

        private static int readByte() {
            if (lenbuf == -1) throw new InputMismatchException();
            if (ptrbuf >= lenbuf) {
                ptrbuf = 0;
                try {
                    lenbuf = is.read(inbuf);
                } catch (IOException e) {
                    throw new InputMismatchException();
                }
                if (lenbuf <= 0) return -1;
            }
            return inbuf[ptrbuf++];
        }

        private static boolean isSpaceChar(int c) {
            return !(c >= 33 && c <= 126);
        }

        //	private static boolean isSpaceChar(int c) { return !(c >= 32 && c <= 126); }
        private static int skip() {
            int b;
            while ((b = readByte()) != -1 && isSpaceChar(b)) ;
            return b;
        }

        private static double nd() {
            return Double.parseDouble(ns());
        }

        private static char nc() {
            return (char) skip();
        }

        private static String ns() {
            int b = skip();
            StringBuilder sb = new StringBuilder();
            while (!(isSpaceChar(b))) {
                sb.appendCodePoint(b);
                b = readByte();
            }
            return sb.toString();
        }

        private static char[] ns(int n) {
            char[] buf = new char[n];
            int b = skip(), p = 0;
            while (p < n && !(isSpaceChar(b))) {
                buf[p++] = (char) b;
                b = readByte();
            }
            return n == p ? buf : Arrays.copyOf(buf, p);
        }

        private static char[][] nm(int n, int m) {
            char[][] map = new char[n][];
            for (int i = 0; i < n; i++) map[i] = ns(m);
            return map;
        }

        private static int[] na(int n) {
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = ni();
            return a;
        }

        private static int ni() {
            int num = 0, b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private static long nl() {
            long num = 0;
            int b;
            boolean minus = false;
            while ((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-')) ;
            if (b == '-') {
                minus = true;
                b = readByte();
            }

            while (true) {
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                } else {
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private static void tr(Object... o) {
            if (INPUT.length() != 0) System.out.println(Arrays.deepToString(o));
        }
    }


    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(1, 3);

            in.add(n).add(m);
            int q = nextInt(1, n * m * 2);
            in.add(q);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < q; i++) {
                builder.append(nextInt(1, m)).append(' ');
            }
            in.add(builder.toString());
            return in.end();
        }
    }
}
