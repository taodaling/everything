package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class BZOJ1038 {
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
        double eps = 1e-6;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            solve();
        }

        public void solve() {
            int n = io.readInt();
            int[][] pts = new int[2][n];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < n; j++) {
                    pts[i][j] = io.readInt();
                }
            }

            ConvexHullTrick cht = new ConvexHullTrick();
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double a = (double) (pts[1][j] - pts[1][i]) / (pts[0][j] - pts[0][i]);
                    double b = pts[1][i] - a * pts[0][i];
                    cht.insert(a, b);
                }
            }

            double l = pts[0][0];
            double r = pts[0][n - 1];
            while (r - l > eps) {
                double m1 = l + (r - l) / 3;
                double m2 = r - (r - l) / 3;
                if (cht.query(m1) <= cht.query(m2)) {
                    r = m2;
                } else {
                    l = m1;
                }
            }

            io.cache.append(String.format("%.3f", cht.query(l)));
        }
    }

    public static class ConvexHullTrick implements Cloneable {
        static final double INF = 1e50;

        public static class Line {
            // y = ax + b
            double a;
            double b;
            double lx;
            double rx;

            static Comparator<Line> orderByA = new Comparator<Line>() {
                @Override
                public int compare(Line o1, Line o2) {
                    return Double.compare(o1.a, o2.a);
                }
            };
            static Comparator<Line> orderByLx = new Comparator<Line>() {
                @Override
                public int compare(Line o1, Line o2) {
                    return Double.compare(o1.lx, o2.lx);
                }
            };

            public Line(double a, double b) {
                this.a = a;
                this.b = b;
            }

            public double y(double x) {
                return a * x + b;
            }

            //a1x+b1=a2x+b2=>(a1-a2)x=b2-b1=>x=(b2-b1)/(a1-a2)
            public static double intersectAt(Line a, Line b) {
                return (b.b - a.b) / (a.a - b.a);
            }

            @Override
            public int hashCode() {
                return (int) (Double.doubleToLongBits(a) * 31 + Double.doubleToLongBits(b));
            }

            @Override
            public boolean equals(Object obj) {
                Line line = (Line) obj;
                return a == line.a && b == line.b;
            }

            @Override
            public String toString() {
                return a + "x+" + b;
            }
        }

        private PersistentTreeSet<Line> setOrderByA = new PersistentTreeSet(Line.orderByA);
        private PersistentTreeSet<Line> setOrderByLx = new PersistentTreeSet(Line.orderByLx);

        private Line queryLine = new Line(0, 0);

        public double query(double x) {
            queryLine.lx = x;
            Line line = setOrderByLx.floor(queryLine);
            return line.y(x);
        }

        public Line insert(double a, double b) {
            Line newLine = new Line(a, b);
            boolean add = true;
            while (add) {
                Line prev = setOrderByA.floor(newLine);
                if (prev == null) {
                    newLine.lx = -INF;
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
                    double lx = Line.intersectAt(prev, newLine);
                    if (lx <= prev.lx) {
                        setOrderByA.remove(prev);
                        setOrderByLx.remove(prev);
                    } else if (lx > prev.rx) {
                        add = false;
                        break;
                    } else {
                        prev.rx = lx;
                        newLine.lx = lx;
                        break;
                    }
                }
            }

            while (add) {
                Line next = setOrderByA.ceiling(newLine);
                if (next == null) {
                    newLine.rx = INF;
                    break;
                }
                double rx = Line.intersectAt(newLine, next);
                if (rx >= next.rx) {
                    setOrderByA.remove(next);
                    setOrderByLx.remove(next);
                } else if (rx < next.lx || (newLine.lx >= rx)) {
                    Line lastLine = setOrderByA.floor(newLine);
                    if (lastLine != null) {
                        lastLine.rx = next.lx;
                    }
                    add = false;
                    break;
                } else {
                    next.lx = rx;
                    newLine.rx = rx;
                    break;
                }
            }

            if (add) {
                setOrderByA.add(newLine);
                setOrderByLx.add(newLine);
            }

            return newLine;
        }

        @Override
        protected ConvexHullTrick clone() {
            try {
                return (ConvexHullTrick) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class PersistentTreeSet<K> implements Cloneable {
        private Treap<K> treap = Treap.NIL;
        private Comparator<K> keyComparator;

        public PersistentTreeSet(Comparator<K> keyComparator) {
            this.keyComparator = keyComparator;
        }

        public int size() {
            return treap.size;
        }

        public K lower(K k) {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByKeyPreferRight(op, k, keyComparator);
            return Treap.findMax(pair[0]).key;
        }

        public K floor(K k) {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByKeyPreferLeft(op, k, keyComparator);
            return Treap.findMax(pair[0]).key;
        }

        public K ceiling(K k) {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByKeyPreferRight(op, k, keyComparator);
            return Treap.findMin(pair[1]).key;
        }

        public K higher(K k) {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByKeyPreferLeft(op, k, keyComparator);
            return Treap.findMin(pair[1]).key;
        }

        public K pollFirst() {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByRank(op, 1);
            treap = pair[1];
            return pair[0].key;
        }

        public K pollLast() {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByRank(op, op.size - 1);
            treap = pair[0];
            return pair[1].key;
        }


        public boolean contain(K key) {
            K last = floor(key);
            return last != null && keyComparator.compare(last, key) == 0;
        }

        public Comparator<? super K> comparator() {
            return keyComparator;
        }

        public K first() {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByRank(op, 1);
            return pair[0].key;
        }

        public K last() {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByRank(op, op.size - 1);
            return pair[1].key;
        }

        public void add(K key) {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByKeyPreferLeft(op, key, keyComparator);

            Treap<K> t = new Treap<>();
            t.key = key;
            treap = Treap.merge(pair[0], t);
            treap = Treap.merge(treap, pair[1]);
        }

        public void remove(K key) {
            Treap<K> op = treap.clone();
            Treap<K>[] pair = Treap.splitByKeyPreferLeft(op, key, keyComparator);
            if (pair[0] == Treap.NIL) {
                return;
            }
            pair[0] = Treap.splitByKeyPreferRight(pair[0], key, keyComparator)[0];
            treap = Treap.merge(pair[0], pair[1]);
        }

        private static class Treap<T> implements Cloneable {
            private static Random random = new Random();

            private static Treap NIL = new Treap();

            static {
                NIL.left = NIL.right = NIL;
                NIL.size = 0;
            }

            Treap<T> left = NIL;
            Treap<T> right = NIL;
            int size = 1;
            T key;
            int w = random.nextInt();

            @Override
            public Treap<T> clone() {
                if (this == NIL) {
                    return this;
                }
                try {
                    return (Treap<T>) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }

            public void pushDown() {
                left = left.clone();
                right = right.clone();
            }

            public void pushUp() {
                size = left.size + right.size + 1;
            }

            public static <T> Treap<T>[] splitByRank(Treap<T> root, int rank) {
                if (root == NIL) {
                    return new Treap[]{NIL, NIL};
                }
                root.pushDown();
                Treap<T>[] result;
                if (root.left.size >= rank) {
                    result = splitByRank(root.left, rank);
                    root.left = result[1];
                    result[1] = root;
                } else {
                    result = splitByRank(root.right, rank - (root.size - root.right.size));
                    root.right = result[0];
                    result[0] = root;
                }
                root.pushUp();
                return result;
            }

            public static <T> Treap<T> merge(Treap<T> a, Treap<T> b) {
                if (a == NIL) {
                    return b;
                }
                if (b == NIL) {
                    return a;
                }
                if (a.w <= b.w) {
                    a.pushDown();
                    a.right = merge(a.right, b);
                    a.pushUp();
                    return a;
                } else {
                    b.pushDown();
                    b.left = merge(a, b.left);
                    b.pushUp();
                    return b;
                }
            }

            public static <T> void toString(Treap<T> root, StringBuilder builder) {
                if (root == NIL) {
                    return;
                }
                root.pushDown();
                toString(root.left, builder);
                builder.append(root.key).append(',');
                toString(root.right, builder);
            }

            public static <T> Treap<T> clone(Treap<T> root) {
                if (root == NIL) {
                    return NIL;
                }
                Treap<T> clone = root.clone();
                clone.left = clone(root.left);
                clone.right = clone(root.right);
                return clone;
            }

            public static <T> Treap<T> findMax(Treap<T> root) {
                return root.right == NIL ? root : findMax(root.right);
            }

            public static <T> Treap<T> findMin(Treap<T> root) {
                return root.left == NIL ? root : findMin(root.left);
            }

            @Override
            public String toString() {
                StringBuilder builder = new StringBuilder().append(key).append(":");
                toString(clone(this), builder);
                return builder.toString();
            }

            public static <T> Treap<T>[] splitByKeyPreferLeft(Treap<T> root, T key, Comparator<T> keyComparator) {
                if (root == NIL) {
                    return new Treap[]{NIL, NIL};
                }
                root.pushDown();
                Treap<T>[] result;
                if (keyComparator.compare(root.key, key) > 0) {
                    result = splitByKeyPreferLeft(root.left, key, keyComparator);
                    root.left = result[1];
                    result[1] = root;
                } else {
                    result = splitByKeyPreferLeft(root.right, key, keyComparator);
                    root.right = result[0];
                    result[0] = root;
                }
                root.pushUp();
                return result;
            }

            public static <T> Treap<T>[] splitByKeyPreferRight(Treap<T> root, T key, Comparator<T> keyComparator) {
                if (root == NIL) {
                    return new Treap[]{NIL, NIL};
                }
                root.pushDown();
                Treap<T>[] result;
                if (keyComparator.compare(root.key, key) >= 0) {
                    result = splitByKeyPreferRight(root.left, key, keyComparator);
                    root.left = result[1];
                    result[1] = root;
                } else {
                    result = splitByKeyPreferRight(root.right, key, keyComparator);
                    root.right = result[0];
                    result[0] = root;
                }
                root.pushUp();
                return result;
            }
        }

        @Override
        public PersistentTreeSet<K> clone() {
            try {
                return (PersistentTreeSet<K>) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FastIO {
        public final StringBuilder cache = new StringBuilder(1 << 13);
        private final InputStream is;
        private final OutputStream os;
        private final Charset charset;
        private StringBuilder defaultStringBuf = new StringBuilder(1 << 13);
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
