package com.daltao.oj.old.submit.hdu;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class HDU1402V2 {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static ASC2IO io;
    public static Debug debug;

    public static void main(String[] args) throws FileNotFoundException {
        init();

        int time = 0;
        while (io.hasMore()) {
            if (time > 0) {
                io.write('\n');
            }
            solve();
            time++;
        }

        destroy();
    }

    public static void init() throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            io = new ASC2IO(new FileInputStream("E:\\DATABASE\\TESTCASE\\nod51\\FONOD1028.in"), System.out);
        } else {
            io = new ASC2IO(System.in, System.out);
        }

        debug = new Debug();
        debug.enter("main");
    }

    static char[] data = new char[50000];
    public static void solve() {
        int len1 = io.readString(data, 0, 50000);
        HugeUnsignedInt a = new HugeUnsignedInt(data, 0, len1);

        int len2 = io.readString(data, 0, 50000);
        HugeUnsignedInt b = new HugeUnsignedInt(data, 0, len2);

        HugeUnsignedInt c = HugeUnsignedInt.mul(a, b);

        io.writeInt(c.digits[c.length - 1]);
        for (int i = c.length - 2; i >= 0; i--) {
            io.writeInt(c.digits[i] / 10);
            io.writeInt(c.digits[i] % 10);
        }

    }

    public static class HugeUnsignedInt {
        static final int DIGIT_NUM = 2;
        static final int RADIX = (int) Math.round(Math.pow(10, DIGIT_NUM));
        static final int POLYNOMIAL_MUL_THRESHOLD = 0;
        int[] digits;
        int length;

        private HugeUnsignedInt(int[] digits) {
            this.digits = digits;
            length = digits.length - 1;
            for (; length >= 1 && digits[length] == 0; length--) ;
            length++;
        }

        public HugeUnsignedInt(char[] data, int from, int to) {
            while (from < to && data[from] == '0') {
                from++;
            }

            if (from == to) {
                digits = new int[1];
                return;
            }

            int charNum = to - from;
            length = (charNum + DIGIT_NUM - 1) / DIGIT_NUM;
            digits = new int[length];

            int largestNum = 0;
            for (int i = 0, until = charNum - (length - 1) * DIGIT_NUM; i < until; i++) {
                largestNum = largestNum * 10 + data[from++] - '0';
            }

            digits[length - 1] = largestNum;
            for (int i = length - 2; i >= 0; i--) {
                int val = 0;
                for (int j = 0; j < DIGIT_NUM; j++) {
                    val = val * 10 + data[from++] - '0';
                }
                digits[i] = val;
            }

        }

        public static HugeUnsignedInt mul(HugeUnsignedInt a, HugeUnsignedInt b) {
            if (a.length < POLYNOMIAL_MUL_THRESHOLD || b.length < POLYNOMIAL_MUL_THRESHOLD) {
                return straightForwardMul(a.digits, b.digits);
            }

            Polynomial.MutablePolynomial p1 = Polynomial.polynomialCache.remove();
            Polynomial.MutablePolynomial p2 = Polynomial.polynomialCache.remove();

            int alen = a.length;
            int blen = b.length;

            p1.extend(alen);
            for (int i = 0; i < alen; i++) {
                p1.data[i] = new Polynomial.Complex(a.digits[i], 0);
            }

            p2.extend(blen);
            for (int i = 0; i < blen; i++) {
                p2.data[i] = new Polynomial.Complex(b.digits[i], 0);
            }

            Polynomial.polynomialCache.add(p1);
            Polynomial.polynomialCache.add(p2);

            Polynomial c = Polynomial.mul(new Polynomial(p1), new Polynomial(p2));

            int clen = alen + blen;
            int[] digits = new int[clen];
            int remainder = 0;
            for (int i = 0, until = c.length(); i < until; i++) {
                remainder = remainder + (int) (c.data[i].getReal() + 0.5);
                digits[i] = remainder % RADIX;
                remainder /= RADIX;
            }
            for (int i = c.length(); i < clen; i++) {
                digits[i] = remainder % RADIX;
                remainder /= RADIX;
            }

            return new HugeUnsignedInt(digits);
        }

        private static HugeUnsignedInt straightForwardMul(int[] a, int[] b) {
            int alen = a.length;
            int blen = b.length;
            int clen = a.length + b.length;

            int[] c = new int[clen];

            int remainder = 0;
            for (int i = 0; i < clen; i++) {
                //0<=i-j<blen
                //j<=i&&j>i-blen
                for (int j = Math.max(0, i - blen + 1), until = Math.min(i + 1, alen); j < until; j++) {
                    remainder += a[j] * b[i - j];
                }
                c[i] = remainder % RADIX;
                remainder /= RADIX;
            }

            return new HugeUnsignedInt(c);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(digits[length - 1]);
            for (int i = length - 2; i >= 0; i--) {
                int v = digits[i];
                int div = RADIX / 10;
                for (int j = 0; j < DIGIT_NUM; j++) {
                    builder.append(v / div % 10);
                    div /= 10;
                }
            }
            return builder.toString();
        }
    }

    public static class Polynomial {
        private static Queue<MutablePolynomial> polynomialCache = new ArrayDeque<>(4);
        private static Complex[] wn1Cache = new Complex[64];
        private static final int FFT_THRESHOLD = 0;

        static {
            polynomialCache.add(new MutablePolynomial());
            polynomialCache.add(new MutablePolynomial());
            polynomialCache.add(new MutablePolynomial());
            polynomialCache.add(new MutablePolynomial());
        }

        private Complex[] data;

        public Polynomial(MutablePolynomial polynomial) {
            int length = polynomial.length - 1;
            for (; length >= 1 && polynomial.data[length].isZero(); length--) ;
            length++;
            data = Arrays.copyOfRange(polynomial.data, 0, length);
        }

        public int length() {
            return data.length;
        }

        private static Complex getWn1(int i) {
            if (wn1Cache[i] == null) {
                double angle = Math.PI * 2 / (1 << i);
                wn1Cache[i] = new Complex(Math.cos(angle), Math.sin(angle));
            }
            return wn1Cache[i];
        }

        public static class Complex {
            public static final Complex ONE = new Complex(1, 0);
            public static final Complex ZERO = new Complex(0, 0);
            private double real;
            private double img;

            public Complex() {
            }

            public Complex(double real) {
                this(real, 0);
            }

            public Complex(double real, double img) {
                this.real = real;
                this.img = img;
            }

            public static Complex add(Complex a, Complex b, Complex c) {
                c.real = a.real + b.real;
                c.img = a.img + b.img;
                return c;
            }

            public static Complex sub(Complex a, Complex b, Complex c) {
                c.real = a.real - b.real;
                c.img = a.img - b.img;
                return c;
            }

            public static Complex mul(Complex a, Complex b, Complex c) {
                double r = a.real * b.real - a.img * b.img;
                double i = a.real * b.img + a.img * b.real;
                c.real = r;
                c.img = i;
                return c;
            }

            public static Complex div(Complex a, double b, Complex c) {
                c.real = a.real / b;
                c.img = c.img / b;
                return c;
            }

            public static Complex div(Complex a, Complex b) {
                double divisor = b.real * b.real + b.img * b.img;
                Complex r = new Complex(b.real / divisor, -b.img / divisor);
                return mul(a, b, b);
            }

            public Complex add(Complex b) {
                return add(this, b, new Complex());
            }

            public Complex sub(Complex b) {
                return sub(this, b, new Complex());
            }

            public Complex mul(Complex b) {
                return mul(this, b, new Complex());
            }

            public Complex div(double b) {
                return div(this, b, new Complex());
            }

            public Complex div(Complex b) {
                return div(this, b);
            }

            public double getReal() {
                return real;
            }

            public boolean isZero() {
                return Math.abs(real) + Math.abs(img) <= 1e-1;
            }

            public double getImg() {
                return img;
            }

            @Override
            public String toString() {
                if (img == 0) {
                    return "" + real;
                }
                if (real == 0) {
                    return "" + img + "i";
                }
                if (img < 0) {
                    return real + img + "i";
                }
                return real + "+" + img + "i";
            }
        }

        public static Polynomial mul(Polynomial a, Polynomial b) {
            if (a.data.length >= FFT_THRESHOLD && b.data.length >= FFT_THRESHOLD) {
                return FFT(a, b);
            } else {
                return straightForwardMul(a, b);
            }
        }

        @Override
        public String toString() {
            return Arrays.toString(data);
        }

        private static Polynomial straightForwardMul(Polynomial a, Polynomial b) {
            MutablePolynomial c = polynomialCache.remove();
            int len = a.length() + b.length() - 1;
            c.extend(len);

            int alen = a.length();
            int blen = b.length();
            for (int i = 0; i < alen; i++) {
                for (int j = 0; j < blen; j++) {
                    int k = i + j;
                    c.data[k] = c.data[k].add(a.data[i].mul(b.data[j]));
                }
            }

            polynomialCache.add(c);
            return new Polynomial(c);
        }

        private static Polynomial FFT(Polynomial a, Polynomial b) {
            MutablePolynomial ma = polynomialCache.remove();
            MutablePolynomial mb = polynomialCache.remove();
            ma.copyOf(a);
            mb.copyOf(b);

            int n = a.length() + b.length() - 1;
            n = 1 << 32 - Integer.numberOfLeadingZeros(n - 1);

            ma.extend(n);
            mb.extend(n);

            MutablePolynomial res = IDFT(dmul(DFT(ma), DFT(mb)));
            polynomialCache.add(res);

            return new Polynomial(res);
        }

        private static MutablePolynomial dmul(MutablePolynomial a, MutablePolynomial b) {
            int n = a.length;

            MutablePolynomial c = polynomialCache.remove();
            c.setLength(n);

            for (int i = 0; i < n; i++) {
                c.data[i] = a.data[i].mul(b.data[i]);
            }

            polynomialCache.add(a);
            polynomialCache.add(b);
            return c;
        }

        private static MutablePolynomial IDFT(MutablePolynomial p) {
            int n = p.length;
            MutablePolynomial dftY = DFT(p);
            MutablePolynomial c = polynomialCache.remove();
            c.setLength(n);

            c.data[0] = dftY.data[0].div(n);
            for (int i = 1; i < n; i++) {
                c.data[i] = dftY.data[n - i].div(n);
            }

            polynomialCache.add(dftY);

            return c;
        }

        private static MutablePolynomial DFT(MutablePolynomial p) {
            int n = p.length;
            int log2n = 31 - Integer.numberOfLeadingZeros(n);

            MutablePolynomial last = p;
            for (int d = log2n - 1; d >= 0; d--) {
                MutablePolynomial next = polynomialCache.remove();
                next.setLength(n);

                int curStep = 1 << d;
                int size = n / curStep;
                int half = size >> 1;
                int halfStep = half * curStep;
                Complex wn1 = getWn1(log2n - d);
                Complex w = Complex.ONE;
                for (int i = 0; i < size; i += 2) {
                    for (int j = 0; j < curStep; j++) {
                        int even = i * curStep + j;
                        int odd = even + curStep;
                        int k = (even + j) >> 1;

                        Complex tmp = w.mul(last.data[odd]);
                        next.data[k] = last.data[even].add(tmp);
                        next.data[k + halfStep] = last.data[even].sub(tmp);
                    }
                    w = w.mul(wn1);
                }

                polynomialCache.add(last);
                last = next;
            }

            return last;
        }

        private static class MutablePolynomial {
            private Complex[] data;
            private int length;

            public MutablePolynomial() {
                data = new Complex[1];
            }

            public void extend(int size) {
                ensureCapacity(size);
                if (length < size) {
                    Arrays.fill(data, length, size, Complex.ZERO);
                }
                length = size;
            }

            public void setLength(int size) {
                ensureCapacity(size);
                length = size;
            }

            private void ensureCapacity(int size) {
                if (data.length < size) {
                    int n = data.length;
                    while (n < size) {
                        n <<= 1;
                    }
                    Complex[] tmp = new Complex[n];
                    System.arraycopy(data, 0, tmp, 0, length);
                    data = tmp;
                }
            }

            public void copyOf(Polynomial polynomial) {
                ensureCapacity(polynomial.data.length);
                length = polynomial.data.length;
                System.arraycopy(polynomial.data, 0, data, 0, length);
            }

            @Override
            public String toString() {
                return Arrays.toString(Arrays.copyOfRange(data, 0, length));
            }
        }
    }

    public static void destroy() {
        io.flush();
        debug.exit();
        debug.statistic();
    }

    public static class Debug {
        boolean debug = System.getProperty("ONLINE_JUDGE") == null;
        Deque<ModuleRecorder> stack = new ArrayDeque<>();
        Map<String, Module> fragmentMap = new HashMap<>();

        public void enter(String module) {
            if (debug) {
                stack.push(new ModuleRecorder(getModule(module)));
            }
        }

        public Module getModule(String moduleName) {
            Module module = fragmentMap.get(moduleName);
            if (module == null) {
                module = new Module(moduleName);
                fragmentMap.put(moduleName, module);
            }
            return module;
        }

        public void exit() {
            if (debug) {
                ModuleRecorder fragment = stack.pop();
                fragment.exit();
            }
        }

        public void statistic() {
            if (!debug) {
                return;
            }

            if (stack.size() > 0) {
                throw new RuntimeException("Exist unexited tag");
            }
            System.out.println("\n------------------------------------------");

            System.out.println("memory used " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20) + "M");

            System.out.println("\n------------------------------------------");
            for (Module module : fragmentMap.values()) {
                System.out.println(String.format("Module %s : enter %d : cost %d", module.moduleName, module.enterTime, module.totaltime));
            }

            System.out.println("------------------------------------------");
        }

        public static class ModuleRecorder {
            Module fragment;
            long time;

            public ModuleRecorder(Module fragment) {
                this.fragment = fragment;
                time = System.currentTimeMillis();
            }

            public void exit() {
                fragment.totaltime += System.currentTimeMillis() - time;
                fragment.enterTime++;
            }
        }

        public static class Module {
            String moduleName;
            long totaltime;
            long enterTime;

            public Module(String moduleName) {
                this.moduleName = moduleName;
            }
        }
    }

    public static class ASC2IO {
        private static final int BUF_SIZE = 1 << 13;
        private static final int EOF = -1;
        private byte[] r_buf = new byte[BUF_SIZE];
        private int r_cur;
        private int r_total;
        private int r_next;
        private final InputStream in;

        StringBuilder w_buf = new StringBuilder();
        private final OutputStream out;

        public boolean hasMore() {
            skipBlank();
            return r_next != -1;
        }

        public ASC2IO(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        private void skipBlank() {
            while (r_next <= 32 && r_next != EOF) {
                r_next = read();
            }
        }

        public int readString(char[] data, int offset, int limit) {
            skipBlank();

            int originalLimit = limit;
            while (limit > 0 && r_next > 32) {
                data[offset++] = (char) r_next;
                limit--;
                r_next = read();
            }

            return originalLimit - limit;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (r_next > 32) {
                builder.append((char) r_next);
            }

            return builder.toString();
        }

        public String readString() {
            return readString(new StringBuilder(16));
        }

        public long readUnsignedLong() {
            skipBlank();

            long num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public long readLong() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign * readUnsignedLong();
        }

        public int readUnsignedInt() {
            skipBlank();

            int num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public int readInt() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign * readUnsignedInt();
        }

        public int read() {
            while (r_total <= r_cur) {
                try {
                    r_total = in.read(r_buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                r_cur = 0;
                if (r_total == -1) {
                    return EOF;
                }
            }
            return r_buf[r_cur++];
        }

        public void write(char c) {
            w_buf.append(c);
        }

        public void writeInt(int n) {
            w_buf.append(n);
        }

        public void writeString(String s) {
            w_buf.append(s);
        }

        public void writeCharArray(char[] data, int offset, int cnt) {
            for (int i = offset, until = offset + cnt; i < until; i++) {
                write(data[i]);
            }
        }

        public void flush() {
            try {
                out.write(w_buf.toString().getBytes(Charset.forName("ascii")));
                w_buf.setLength(0);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}