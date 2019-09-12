package com.daltao.oj.submit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.TreeSet;

public class CFG102275D {
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
        long inf = (long) 1e18;

        public Task(FastIO io, Debug debug) {
            this.io = io;
            this.debug = debug;
        }

        @Override
        public void run() {
            int t = io.readInt();
            for (int i = 1; i <= t; i++) {
                io.cache.append("Case #").append(i).append(": ");
                solve();
                io.cache.append('\n');
            }
        }

        public void solve() {
            int n = io.readInt();

            //prehandle
            long p1 = io.readInt();
            long p2 = io.readInt();
            long ap = io.readInt();
            long bp = io.readInt();
            long cp = io.readInt();
            long dp = io.readInt();

            long h1 = io.readInt();
            long h2 = io.readInt();
            long ah = io.readInt();
            long bh = io.readInt();
            long ch = io.readInt();
            long dh = io.readInt();

            List<Stone> stoneList = new ArrayList<>(n);
            List<Clam> clamList = new ArrayList<>(n);

            int hardestClam = -1;
            int hardestStone = -1;
            if (io.readChar() == 'C') {
                clamList.add(newClam(p1, h1));
                hardestClam = Math.max(hardestClam, (int) h1);
            } else {
                stoneList.add(newStone(p1, h1));
                hardestStone = Math.max(hardestStone, (int) h1);
            }
            if (io.readChar() == 'C') {
                clamList.add(newClam(p2, h2));
                hardestClam = Math.max(hardestClam, (int) h2);
            } else {
                stoneList.add(newStone(p2, h2));
                hardestStone = Math.max(hardestStone, (int) h2);
            }


            for (int i = 3; i <= n; i++) {
                long p1Copy = p1;
                p1 = p2;
                p2 = (ap * p1Copy + bp * p1 + cp) % dp + 1;

                long h1Copy = h1;
                h1 = h2;
                h2 = (ah * h1Copy + bh * h1 + ch) % dh + 1;

                if (io.readChar() == 'C') {
                    clamList.add(newClam(p2, h2));
                    hardestClam = Math.max(hardestClam, (int) h2);
                } else {
                    stoneList.add(newStone(p2, h2));
                    hardestStone = Math.max(hardestStone, (int) h2);
                }
            }

            if (hardestStone <= hardestClam) {
                io.cache.append(-1);
                return;
            }

            if (clamList.isEmpty()) {
                io.cache.append(0);
                return;
            }

            stoneList.sort(Stone.sortByPos);
            clamList.sort(Clam.sortByPos);

            //filter
            List<Clam> filteredClamList = new ArrayList<>(n);
            Deque<Clam> clamDeque = new ArrayDeque<>(clamList);
            Deque<Stone> stoneDeque = new ArrayDeque<>(stoneList);

            while (!stoneDeque.isEmpty() && stoneDeque.peekLast().pos > clamDeque.peekLast().pos) {
                stoneDeque.removeLast();
            }
            int max = -1;
            while (!clamDeque.isEmpty()) {
                Clam clam = clamDeque.removeLast();
                while (!stoneDeque.isEmpty() && stoneDeque.peekLast().pos > clam.pos) {
                    max = Math.max(max, stoneDeque.removeLast().hard);
                }
                if (max > clam.hard) {
                    continue;
                }
                filteredClamList.add(clam);
            }

            //reverse
            for (int l = 0, r = filteredClamList.size() - 1; l < r; l++, r--) {
                Clam el = filteredClamList.get(l);
                Clam er = filteredClamList.get(r);
                filteredClamList.set(l, er);
                filteredClamList.set(r, el);
            }
            clamList = filteredClamList;


            //Use treeset to find l, r
            int clamNum = clamList.size();
            int stoneNum = stoneList.size();

            //find r
            TreeSet<Stone> floorSet = new TreeSet<>(Stone.sortByHard);
            clamDeque.clear();
            clamDeque.addAll(filteredClamList);
            stoneDeque.clear();
            stoneDeque.addAll(stoneList);

            while (!clamDeque.isEmpty() || !stoneDeque.isEmpty()) {
                if (stoneDeque.isEmpty() || (!clamDeque.isEmpty() && clamDeque.peekFirst().pos < stoneDeque.peekFirst().pos)) {
                    Clam clam = clamDeque.removeFirst();
                    while (!floorSet.isEmpty() && floorSet.first().hard <= clam.hard) {
                        floorSet.pollFirst().r = clamNum - clamDeque.size() - 2;
                    }
                } else {
                    floorSet.add(stoneDeque.removeFirst());
                }
            }
            while (!floorSet.isEmpty()) {
                floorSet.pollFirst().r = clamNum - 1;
            }

            //find l
            clamDeque.clear();
            clamDeque.addAll(filteredClamList);
            stoneDeque.clear();
            stoneDeque.addAll(stoneList);

            while (!clamDeque.isEmpty() || !stoneDeque.isEmpty()) {
                if (stoneDeque.isEmpty() || (!clamDeque.isEmpty() && clamDeque.peekLast().pos > stoneDeque.peekLast().pos)) {
                    Clam clam = clamDeque.removeLast();
                    while (!floorSet.isEmpty() && floorSet.first().hard <= clam.hard) {
                        floorSet.pollFirst().l = clamDeque.size() + 1;
                    }
                } else {
                    floorSet.add(stoneDeque.removeLast());
                }
            }
            while (!floorSet.isEmpty()) {
                floorSet.pollFirst().l = 0;
            }

            stoneDeque.clear();
            stoneDeque.addAll(stoneList);
            Segment tree = new Segment(0, clamNum);
            long[] f = new long[clamNum];
            for (int i = 0; i < clamNum; i++) {
                Clam clam = filteredClamList.get(i);
                while (!stoneDeque.isEmpty() && stoneDeque.peekFirst().pos <= clam.pos) {
                    Stone stone = stoneDeque.removeFirst();
                    if (stone.l > stone.r) {
                        continue;
                    }
                    long prev = 0;
                    if (stone.l > 0) {
                        prev = f[stone.l - 1];
                    }
                    tree.update(i, stone.r, 0, clamNum, prev - stone.pos);
                }
                f[i] = tree.query(i, i, 0, clamNum) + clam.pos;
            }

            long ans = inf;
            Clam lastClam = filteredClamList.get(clamNum - 1);
            for (Stone stone : stoneList) {
                if (stone.r < clamNum - 1 || stone.l > clamNum - 1) {
                    continue;
                }
                long sum = Math.abs(stone.pos - lastClam.pos);
                if (stone.l > 0) {
                    sum += f[stone.l - 1] * 2;
                }
                ans = Math.min(ans, sum);
            }

            io.cache.append(ans + lastClam.pos);
        }

        public Clam newClam(long pos, long hard) {
            Clam item = new Clam();
            item.pos = (int) pos;
            item.hard = (int) hard;
            return item;
        }

        public Stone newStone(long pos, long hard) {
            Stone item = new Stone();
            item.pos = (int) pos;
            item.hard = (int) hard;
            return item;
        }

    }


    private static class Segment implements Cloneable {
        private Segment left;
        private Segment right;
        private static long inf = (long) 1e18;
        long min = inf;
        long dirty = inf;

        public void setMin(long m) {
            this.min = Math.min(this.min, m);
            dirty = Math.min(m, dirty);
        }

        public void pushUp() {
        }

        public void pushDown() {
            if (min != inf) {
                left.setMin(dirty);
                right.setMin(dirty);
                dirty = inf;
            }
        }

        public Segment(int l, int r) {
            if (l < r) {
                int m = (l + r) >> 1;
                left = new Segment(l, m);
                right = new Segment(m + 1, r);
                pushUp();
            } else {

            }
        }

        private boolean covered(int ll, int rr, int l, int r) {
            return ll <= l && rr >= r;
        }

        private boolean noIntersection(int ll, int rr, int l, int r) {
            return ll > r || rr < l;
        }

        public void update(int ll, int rr, int l, int r, long v) {
            if (noIntersection(ll, rr, l, r)) {
                return;
            }
            if (covered(ll, rr, l, r)) {
                setMin(v);
                return;
            }
            pushDown();
            int m = (l + r) >> 1;
            left.update(ll, rr, l, m, v);
            right.update(ll, rr, m + 1, r, v);
            pushUp();
        }

        public long query(int ll, int rr, int l, int r) {
            if (noIntersection(ll, rr, l, r)) {
                return inf;
            }
            if (covered(ll, rr, l, r)) {
                return min;
            }
            pushDown();
            int m = (l + r) >> 1;
            return Math.min(left.query(ll, rr, l, m),
                    right.query(ll, rr, m + 1, r));
        }
    }


    public static class Clam {
        int pos;
        int hard;

        static Comparator<Clam> sortByPos = (a, b) -> a.pos - b.pos;

        @Override
        public String toString() {
            return String.format("(%d, %d)", pos, hard);
        }
    }

    public static class Stone {
        int l;
        int r;

        int pos;
        int hard;

        static Comparator<Stone> sortByPos = (a, b) -> a.pos - b.pos;
        static Comparator<Stone> sortByHard = (a, b) -> a.hard == b.hard
                ? (a.pos - b.pos) : (a.hard - b.hard);

        @Override
        public String toString() {
            return String.format("(%d, %d)", pos, hard);
        }
    }


    /**
     * Mod operations
     */
    public static class Modular {
        int m;

        public Modular(int m) {
            this.m = m;
        }

        public int valueOf(int x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return x;
        }

        public int valueOf(long x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return (int) x;
        }

        public int mul(int x, int y) {
            return valueOf((long) x * y);
        }

        public int mul(long x, long y) {
            x = valueOf(x);
            y = valueOf(y);
            return valueOf(x * y);
        }

        public int plus(int x, int y) {
            return valueOf(x + y);
        }

        public int plus(long x, long y) {
            x = valueOf(x);
            y = valueOf(y);
            return valueOf(x + y);
        }

        @Override
        public String toString() {
            return "mod " + m;
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
