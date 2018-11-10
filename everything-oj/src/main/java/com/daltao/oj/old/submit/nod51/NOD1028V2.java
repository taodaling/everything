package com.daltao.oj.old.submit.nod51;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class NOD1028V2 {
    static final int INF = (int) 1e8;
    static final int MOD = (int) 1e9 + 7;
    public static ASC2IO io;
    public static Debug debug;

    public static void main(String[] args) throws FileNotFoundException {

        init();

        solve();

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

    public static void solve() {
        char[] data = new char[(int) 1e5];

        HugeUnsignedInt a = new HugeUnsignedInt(data, 0, io.readString(data, 0, data.length));
        HugeUnsignedInt b = new HugeUnsignedInt(data, 0, io.readString(data, 0, data.length));
        HugeUnsignedInt c = HugeUnsignedInt.fftMultiply(a, b);

        c.toString(io.w_buf);
    }

    public static class HugeUnsignedInt {
        static final int CHAR_LENGTH = 2;
        static final int RADIX = (int) Math.round(Math.pow(10, CHAR_LENGTH));
        static final int FFT_THRESHOLD = 50;
        int[] digits;
        int length;

        public HugeUnsignedInt(char[] data, int from, int to) {
            for (; from < to && data[from] == '0'; from++) ;
            from = Math.min(to - 1, from);

            int digitNum = (to - from + CHAR_LENGTH - 1) / CHAR_LENGTH;
            digits = new int[digitNum];
            length = digitNum;

            for (int i = 0, until = to - from - CHAR_LENGTH * (digitNum - 1); i < until; i++) {
                digits[digitNum - 1] = digits[digitNum - 1] * 10 + data[from++] - '0';
            }

            for (int i = digitNum - 2; i >= 0; i--) {
                for (int j = 0; j < CHAR_LENGTH; j++) {
                    digits[i] = digits[i] * 10 + data[from++] - '0';
                }
            }
        }

        public HugeUnsignedInt(int[] digits) {
            this.digits = digits;
            length = digits.length - 1;
            for (; length > 0 && digits[length] == 0; length--) ;
            length++;
        }

        public static HugeUnsignedInt straightForwardMultiply(HugeUnsignedInt a, HugeUnsignedInt b) {
            int length = a.length + b.length;
            int[] digits = new int[length];

            int remainder = 0;
            int alen = a.length;
            int blen = b.length;
            for (int i = 0; i < length; i++) {
                //j>=0, j < alen
                //i-j>=0, i-j<blen
                //j<min(alen,i+1), j>=max(0,i-blen+1)
                int val = remainder;
                for (int j = Math.max(0, i - blen + 1), until = Math.min(alen, i + 1); j < until; j++) {
                    int k = i - j;
                    val += a.digits[j] * b.digits[k];
                }
                digits[i] = val % RADIX;
                remainder %= RADIX;
            }

            return new HugeUnsignedInt(digits);
        }

        public static HugeUnsignedInt mul(HugeUnsignedInt a, HugeUnsignedInt b) {
            if (a.length < FFT_THRESHOLD || b.length < FFT_THRESHOLD) {
                return straightForwardMultiply(a, b);
            } else {
                return fftMultiply(a, b);
            }
        }

        public static HugeUnsignedInt fftMultiply(HugeUnsignedInt a, HugeUnsignedInt b) {
            FFT.AutoArray aArray = FFT.cache.remove();
            FFT.AutoArray bArray = FFT.cache.remove();

            int aLen = a.length;
            int bLen = b.length;

            int log = 32 - Integer.numberOfLeadingZeros(aLen + bLen - 2);
            int cLen = 1 << log;

            aArray.setLength(cLen);
            bArray.setLength(cLen);

            for (int i = 0; i < aLen; i++) {
                aArray.data[i] = new FFT.Complex(a.digits[i], 0);
            }
            Arrays.fill(aArray.data, aLen, cLen, FFT.Complex.ZERO);

            for (int i = 0; i < bLen; i++) {
                bArray.data[i] = new FFT.Complex(b.digits[i], 0);
            }
            Arrays.fill(bArray.data, bLen, cLen, FFT.Complex.ZERO);

            FFT.DFT(aArray.data, log);
            FFT.DFT(bArray.data, log);
            FFT.dotMul(aArray.data, bArray.data, aArray.data, log);
            FFT.IDFT(aArray.data, log);

            int[] digits = new int[aLen + bLen];
            int remainder = 0;
            for (int i = 0, until = aLen + bLen - 1; i < until; i++) {
                remainder += (int) (aArray.data[i].real + 0.5);
                digits[i] = remainder % RADIX;
                remainder /= RADIX;
            }
            digits[digits.length - 1] = remainder;

            FFT.cache.add(aArray);
            FFT.cache.add(bArray);

            return new HugeUnsignedInt(digits);
        }

        public void toString(StringBuilder builder) {
            builder.ensureCapacity(builder.length() + CHAR_LENGTH * length);
            builder.append(digits[length - 1]);
            int basicBase = RADIX / 10;
            for (int i = length - 2; i >= 0; i--) {
                int base = basicBase;
                for (int j = 0; j < CHAR_LENGTH; j++) {
                    builder.append((char) (digits[i] / base % 10 + '0'));
                    base /= 10;
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            toString(builder);
            return builder.toString();
        }
    }

    public static class FFT {
        static final int MAX_LEN = (int) 3e5 + 1;
        static final int REV[] = new int[MAX_LEN];
        static int REV_LOG = -1;
        static Queue<AutoArray> cache = new ArrayDeque<>();

        static {
            for (int i = 0; i < 2; i++) {
                cache.add(new AutoArray());
            }
        }

        public static class AutoArray implements RandomAccess {
            public Complex[] data;
            public int length;

            public AutoArray() {
                data = new Complex[0];
            }

            public void ensureCapacity(int cap) {
                if (data.length >= cap) {
                    return;
                }
                int proper = 1 << 32 - Integer.numberOfLeadingZeros(cap - 1);
                Complex[] tmp = data;
                data = new Complex[proper];
                System.arraycopy(tmp, 0, data, 0, tmp.length);
            }

            public int getLength() {
                return length;
            }

            public void setLength(int newLength) {
                ensureCapacity(newLength);
                length = newLength;
            }

            public void expand(int newLength, Complex def) {
                ensureCapacity(newLength);
                while (length < newLength) {
                    data[length++] = def;
                }
                length = newLength;
            }
        }

        static final Complex[] wCache = new Complex[64];

        private static class Complex {
            private final double real;
            private final double img;
            public static final Complex ZERO = new Complex(0, 0);
            public static final Complex ONE = new Complex(1, 0);

            public Complex(double real, double img) {
                this.real = real;
                this.img = img;
            }

            public static Complex add(Complex a, Complex b) {
                return new Complex(a.real + b.real, a.img + b.img);
            }

            public Complex add(Complex other) {
                return add(this, other);
            }

            public static Complex sub(Complex a, Complex b) {
                return new Complex(a.real - b.real, a.img - b.img);
            }

            public Complex sub(Complex other) {
                return sub(this, other);
            }

            public static Complex mul(Complex a, Complex b) {
                return new Complex(a.real * b.real - a.img * b.img, a.real * b.img + a.img * b.real);
            }

            public Complex mul(Complex other) {
                return mul(this, other);
            }

            public static Complex div(Complex a, double n) {
                return new Complex(a.real / n, a.img / n);
            }

            public Complex div(double n) {
                return div(this, n);
            }

            @Override
            public String toString() {
                return img == 0 ? ("" + real) : real == 0 ? ("" + img + "i") : "" + real + "+" + img + "i";
            }
        }

        private static void bitRevProcess(int log) {
            if (REV_LOG == log) {
                return;
            }
            REV_LOG = log;
            int n = 1 << log;
            int t = log - 1;
            for (int i = 1; i < n; i++) {
                REV[i] = (REV[i >> 1] >> 1) | ((i & 1) << t);
            }
        }

        private static Complex getW(int log) {
            if (wCache[log] == null) {
                double angle = Math.PI * 2 / (1 << log);
                wCache[log] = new Complex(Math.cos(angle), Math.sin(angle));
            }
            return wCache[log];
        }

        public static void dotMul(Complex[] a, Complex[] b, Complex[] c, int log) {
            int n = 1 << log;
            for (int i = 0; i < n; i++) {
                c[i] = a[i].mul(b[i]);
            }
        }

        public static void IDFT(Complex[] p, int log) {
            int n = 1 << log;
            DFT(p, log);
            p[0] = p[0].div(n);
            //Loop until n - i < i -> 2i > n -> i > n/2
            for (int i = 1, until = n / 2; i <= until; i++) {
                int j = n - i;
                Complex a = p[i];
                Complex b = p[j];
                p[i] = b.div(n);
                p[j] = a.div(n);
            }
        }

        public static void DFT(Complex[] p, int log) {
            int n = 1 << log;
            bitRevProcess(log);
            for (int i = 0; i < n; i++) {
                if (i >= REV[i]) {
                    continue;
                }
                Complex tmp = p[i];
                p[i] = p[REV[i]];
                p[REV[i]] = tmp;
            }

            for (int d = 0; d < log; d++) {
                int h = 1 << d;
                int h2 = h + h;
                Complex w1 = getW(d + 1);
                for (int i = 0; i < n; i += h2) {
                    Complex w = Complex.ONE;
                    for (int j = 0; j < h; j++) {
                        int a = i + j;
                        int b = a + h;
                        Complex t = w.mul(p[b]);
                        Complex e = p[a];
                        p[a] = e.add(t);
                        p[b] = e.sub(t);
                        w = w.mul(w1);
                    }
                }
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
        private static int BUF_SIZE = 1 << 13;

        private byte[] r_buf = new byte[BUF_SIZE];
        private int r_cur;
        private int r_total;
        private int r_next;
        private final InputStream in;

        StringBuilder w_buf = new StringBuilder();
        private final OutputStream out;

        public ASC2IO(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        private void skipBlank() {
            while (r_next <= 32) {
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
                    return -1;
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

        public void reverse() {
            w_buf.reverse();
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