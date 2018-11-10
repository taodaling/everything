package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class CF986D {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static BlockReader input;
    public static PrintStream output;
    public static Debug debug;

    public static void main(String[] args) throws FileNotFoundException {

        try(Writer writer = new OutputStreamWriter(new FileOutputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF986D.in"))){
            StringBuilder builder = new StringBuilder();
            builder.append(100000);
            builder.append(' ');
            builder.append(100000);
            builder.append('\n');
            for(int i = 0; i < 100000; i++)
            {
                builder.append(0).append(' ');
            }
            builder.append(1).append(' ');
            builder.append('\n');
            for(int i = 0; i < 100000; i++)
            {
                builder.append(0).append(' ');
            }
            builder.append(1).append(' ');
            writer.write(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        init();

        solve();

        destroy();
    }

    public static void init() throws FileNotFoundException {
        if (System.getProperty("ONLINE_JUDGE") == null) {
            input = new BlockReader(new FileInputStream("E:\\DATABASE\\TESTCASE\\codeforces\\CF986D.in"));
            output = System.out;
        } else {
            input = new BlockReader(System.in);
            output = new PrintStream(new BufferedOutputStream(System.out), false);
        }

        debug = new Debug();
        debug.enter("main");
    }

    public static void solve() {
    }

    public static int getTail(BigInteger v, int bit) {
        int sum = 0;

        for (int i = bit; i >= 0; i--) {
            sum <<= 1;
            if (v.testBit(i)) {
                sum += 1;
            }
        }
        return sum;
    }

    public static void destroy() {
        output.flush();
        debug.exit();
        debug.statistic();
    }

    public static class Polynomial {
        private Complex[] data;
        private int length;

        private Polynomial(Complex[] data) {
            this.data = data;
            for (length = data.length - 1; length >= 0 && (data[length] == Complex.ZERO || data[length].equals(Complex.ZERO)); length--)
                ;
            length++;
        }

        public static class PolynomialBuilder {
            Complex[] data;

            public PolynomialBuilder(int size) {
                size = properLength(size);
                data = new Complex[size];
                Arrays.fill(data, Complex.ZERO);
            }

            public Polynomial toPolynomial() {
                return new Polynomial(data);
            }

            public void set(int i, Complex v) {
                data[i] = v;
            }
        }

        public int length() {
            return length;
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

            public void setReal(double real) {
                this.real = real;
            }

            public double getImg() {
                return img;
            }

            public void setImg(double img) {
                this.img = img;
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

        public static int compareTo(Polynomial a, Polynomial b, Comparator<Complex> comparator) {
            if (a.length != b.length) {
                return a.length - b.length;
            }

            for (int i = a.length - 1; i >= 0; i--) {
                int cmp = comparator.compare(a.data[i], b.data[i]);
                if (cmp != 0) {
                    return cmp;
                }
            }

            return 0;
        }

        public static Polynomial multiply(Polynomial a, Polynomial b) {
            return FFT(a, b);
        }

        private static int properLength(int n) {
            return 1 << (32 - Integer.numberOfLeadingZeros(n - 1));
        }

        private Polynomial extend(int n) {
            if (data.length >= n) {
                return this;
            }
            Complex[] buf = new Complex[n];
            System.arraycopy(data, 0, buf, 0, data.length);
            Arrays.fill(buf, data.length, n, Complex.ZERO);
            this.data = buf;
            return this;
        }

        private static Polynomial FFT(Polynomial a, Polynomial b) {
            int n = properLength(a.length() + b.length());
            a = a.extend(n);
            b = b.extend(n);

            double angle = 2 * Math.PI / n;
            Complex wn1 = new Complex(Math.cos(angle), Math.sin(angle));

            return IDFT(dotMul(DFT(a, wn1), DFT(b, wn1)), wn1);
        }

        public static Polynomial IDFT(Polynomial y, Complex wn1) {
            int n = y.length();
            PolynomialBuilder c = new PolynomialBuilder(n);

            Polynomial dftY = DFT(y, wn1);

            c.set(0, dftY.data[0].div(n));
            for (int i = 1; i < n; i++) {
                c.data[i] = dftY.data[n - i].div(n);
            }

            return c.toPolynomial();
        }

        public static Polynomial dotMul(Polynomial a, Polynomial b) {
            int min = Math.min(a.length(), b.length());
            PolynomialBuilder builder = new PolynomialBuilder(min);
            for (int i = 0; i < min; i++) {
                builder.set(i, a.data[i].mul(b.data[i]));
            }
            return builder.toPolynomial();
        }

        private static Polynomial DFT(Polynomial f, Complex wn1) {
            int n = f.length();
            int half = n >> 1;

            if (n == 1) {
                return f;
            }

            PolynomialBuilder evenBuilder = new PolynomialBuilder(half);
            PolynomialBuilder oddBuilder = new PolynomialBuilder(half);
            for (int i = 0; i < half; i++) {
                int i2 = i << 1;
                evenBuilder.set(i, f.data[i2]);
                oddBuilder.set(i, f.data[i2 + 1]);
            }

            Complex wn2 = wn1.mul(wn1);
            Polynomial even = DFT(evenBuilder.toPolynomial(), wn2);
            Polynomial odd = DFT(oddBuilder.toPolynomial(), wn2);

            Complex w = Complex.ONE;
            PolynomialBuilder y = new PolynomialBuilder(n);
            for (int i = 0; i < half; i++) {
                y.set(i, even.data[i].add(w.mul(odd.data[i])));
                y.set(i + half, even.data[i].sub(w.mul(odd.data[i])));
                w = w.mul(wn1);
            }

            return y.toPolynomial();
        }
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

        public long nextLong() {
            skipBlank();
            long ret = 0;
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