package com.daltao.oj.old.submit.hdu;

import java.io.*;

/**
 * Created by Administrator on 2018/3/10.
 */
public class HDU1402 {
    public static BlockReader input;
    public static PrintStream output;

    public static int CACHE_LIMIT = (1 << 18) * 3;
    public static int POLYNOMIAL_LIMIT = 1 << 18;


    static ObjectList a = new ObjectList(CACHE_LIMIT);
    static ObjectList b = new ObjectList(CACHE_LIMIT);
    static ObjectList y = new ObjectList(CACHE_LIMIT);

    static double[] reals = new double[100 + CACHE_LIMIT + CACHE_LIMIT + CACHE_LIMIT];
    static double[] images = new double[100 + CACHE_LIMIT + CACHE_LIMIT + CACHE_LIMIT];
    static char[] aStr = new char[100000];
    static char[] bStr = new char[100000];
    static int[] cData = new int[POLYNOMIAL_LIMIT];

    static {
        reals[1] = 1;

        int queue = 100;
        for (int i = 0; i < CACHE_LIMIT; i++) {
            a.set(i, queue++);
            b.set(i, queue++);
            y.set(i, queue++);
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            input = new BlockReader(new FileInputStream("D:\\DataBase\\TESTCASE\\nod51\\NOD1028.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        solve();

        output.flush();
    }

    public static void push() {
        ObjectList.push();
    }

    public static void pop() {
        ObjectList.pop();
    }

    public static void solve() {
        push();

        a.free();
        b.free();
        y.free();


        int aLen = input.nextBlock(aStr, 0);
        int bLen = input.nextBlock(bStr, 0);
        for (int i = 0; i < aLen; i++) {
            int complex = a.get(i);
            reals[complex] = aStr[aLen - 1 - i] - '0';
            images[complex] = 0;
        }
        a.extend(aLen);
        for (int i = 0; i < bLen; i++) {
            int complex = b.get(i);
            reals[complex] = bStr[bLen - 1 - i] - '0';
            images[complex] = 0;
        }
        b.extend(bLen);

        ObjectList cPol = fft(a, b, y);
        int n = cPol.size();
        for (int i = 0; i < n; i++) {
            cData[i] = castDouble(reals[cPol.get(n - i - 1)]);
        }
        long overflow = 0;
        for (int i = n - 1; i >= 1; i--) {
            cData[i] += overflow;
            overflow = cData[i] / 10;
            cData[i] %= 10;
        }
        cData[0] += overflow;

        int head = 0;
        for (int bound = n - 1; head < bound && cData[head] == 0; head++) ;
        if (head == 0) {
            output.print(cData[0]);
            head++;
        }

        for (int i = head; i < n; i++) {
            output.print((char) (cData[i] + '0'));
        }
        output.println();
        pop();

    }

    public static int castDouble(double v) {
        int r = (int) v;
        double off = v - r;
        if (off > 0.5D) {
            r++;
        }
        return r;
    }

    public static ObjectList fft(ObjectList a, ObjectList b, ObjectList y) {
        //calculate the n that n is 2^k and a.size() <= n/2 and b.size <= n/2
        int alen = a.size() << 1;
        int blen = b.size() << 1;
        int n = 1;
        while (n < alen || n < blen) {
            n <<= 1;
        }

        for (int i = a.size(); i < n; i++) {
            Complex.copyTo(0, a.get(i));
        }
        a.extend(n);
        for (int i = b.size(); i < n; i++) {
            Complex.copyTo(0, b.get(i));
        }
        b.extend(n);

        double u = 2 * Math.PI / n;
        int w = 2;
        reals[w] = Math.cos(u);
        images[w] = Math.sin(u);

        ObjectList yB = dft(b, y.allocate(n), w, 3);
        b.extend(0);
        b.free();
        ObjectList yA = dft(a, b.allocate(n), w, 3);
        ObjectList yAB = a.subList(0, n);
        for (int i = 0; i < n; i++) {
            Complex.mul(yA.get(i), yB.get(i), yAB.get(i));
        }
        y.free();
        a.free();
        b.extend(0);
        b.free();


        return idft(b, yAB, w);
    }

    public static ObjectList idft(ObjectList polynomial, ObjectList y, int x1) {
        int n = y.size();
        ObjectList coefficients = dft(y, polynomial.allocate(n), x1, 3);
        Complex.div(coefficients.get(0), n, y.get(0));
        for (int i = 1; i < n; i++) {
            Complex.div(coefficients.get(n - i), n, y.get(i));
        }
        y.extend(n);
        return y;
    }

    public static ObjectList dft(ObjectList polynomial, ObjectList y, int x1, int depth) {
        int n = polynomial.size();
        if (n == 1) {
            Complex.copyTo(polynomial.get(0), y.get(0));
            return y;
        }

        push();
        int halfN = n >> 1;
        ObjectList odd = polynomial.allocate(halfN);
        ObjectList even = polynomial.allocate(halfN);
        for (int i = 0; i < halfN; i++) {
            int i2 = i << 1;
            Complex.copyTo(polynomial.get(i2), even.get(i));
            Complex.copyTo(polynomial.get(i2 + 1), odd.get(i));
        }


        Complex.mul(x1, x1, depth);

        ObjectList evenRes = dft(even, y.allocate(halfN), depth, depth + 1);
        ObjectList oddRes = dft(odd, y.allocate(halfN), depth, depth + 1);


        //f(w(n,i))=even(w(n,i)^2)+w(n,i)*odd(w(n,i)^2)=even(w(n/2,i))+w(n,i)*odd(w(n/2,i))
        int x = depth;
        Complex.copyTo(1, x);

        for (int i = 0; i < halfN; i++) {
            Complex.add(evenRes.get(i), Complex.mul(oddRes.get(i), x, y.get(i)), y.get(i));
            Complex.sub(evenRes.get(i), Complex.mul(oddRes.get(i), x, y.get(i + halfN)), y.get(i + halfN));
            Complex.mul(x1, x, x);
        }

        pop();
        return y;
    }


    public static class ObjectList {
        public static final ObjectList[] POOL = new ObjectList[10000];
        public static int head;
        static int[] position = new int[10000];
        static int top = -1;

        static {
            for (int i = 0, bound = POOL.length; i < bound; i++) {
                POOL[i] = new ObjectList();
            }
        }

        private int[] data;
        private int from;
        private int size;
        private int to;

        public ObjectList(int cap) {
            data = new int[cap];
        }

        private ObjectList() {
        }

        private ObjectList(int[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
            this.size = to - from;
        }

        public static ObjectList getInstance(int[] data, int from, int to) {
            ObjectList list = POOL[head++];
            list.data = data;
            list.from = from;
            list.to = to;
            list.size = to - from;
            return list;
        }

        public static void push() {
            position[++top] = head;
        }

        public static void pop() {
            head = position[top--];
        }

        public void extend(int size) {
            this.size = size;
            this.to = Math.max(size + from, to);
        }

        public void free() {
            this.to = this.size;
        }

        public ObjectList subList(int from, int to) {
            return getInstance(data, this.from + from, this.from + to);
        }

        public ObjectList allocate(int size) {
            return getInstance(data, this.to, this.to += size);
        }

        public int size() {
            return size;
        }

        public int get(int i) {
            return data[from + i];
        }

        public void set(int i, int v) {
            data[from + i] = v;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            for (int i = 0, bound = size; i < bound; i++) {
                builder.append(String.format("(%f, %f)", reals[get(i)], images[get(i)])).append(',');
            }
            builder.setCharAt(builder.length() - 1, ']');
            return builder.toString();
        }
    }

    public static class Complex {
        public static final int ONE = 1;
        public static final int ZERO = 0;


        public static void copyTo(int from, int to) {
            reals[to] = reals[from];
            images[to] = images[from];
        }

        public static int add(int a, int b, int result) {
            double r = reals[a] + reals[b];
            double i = images[a] + images[b];

            reals[result] = r;
            images[result] = i;
            return result;
        }


        public static int sub(int a, int b, int result) {
            double r = reals[a] - reals[b];
            double i = images[a] - images[b];

            reals[result] = r;
            images[result] = i;
            return result;
        }

        public static int mul(int a, int b, int result) {

            double r = reals[a] * reals[b] - images[a] * images[b];
            double i = reals[a] * images[b] + images[a] * reals[b];

            reals[result] = r;
            images[result] = i;
            return result;
        }

        public static int div(int a, double x, int result) {
            double r = reals[a] / x;
            double i = images[a] / x;

            reals[result] = r;
            images[result] = i;
            return result;
        }
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (Exception e) {
                }
            }
            return dBuf[dPos++];
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }
    }
}
