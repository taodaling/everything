package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.InputStream;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.InputMismatchException;

public class AGC007FTest {
    @Test
    public void test(){
        Assert.assertTrue(
                new TestCaseExecutor.Builder()
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setInputFactory(new Generator()).setTestTime(10000)
                .build().call()
        );
    }




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
                TaskF solver = new TaskF();
                solver.solve(1, in, out);
                out.close();
            }
        }
        static class TaskF {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                char[] s = new char[n + 1];
                char[] t = new char[n + 1];
                in.readString(s, 1);
                in.readString(t, 1);

                if (Arrays.equals(s, t)) {
                    out.println(0);
                    return;
                }

                int scan = n + 1;
                Segment seg = new Segment(0, n);
                for (int i = n; i >= 1; i--) {
                    if (scan > i) {
                        // scan = i + 1
                        scan = i;
                    }
                    if (s[scan] == t[i]) {
                        continue;
                    }
                    while (scan > 0 && s[scan] != t[i]) {
                        scan--;
                    }
                    if (s[scan] != t[i]) {
                        out.println(-1);
                        return;
                    }
                    seg.update(scan - 1, i, 0, n);
                }


                out.println(seg.query(0, n, 0, n));
            }

        }
        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(1 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput println(int c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput flush() {
                try {
                    os.append(cache);
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

            public int readString(char[] data, int offset) {
                skipBlank();

                int originalOffset = offset;
                while (next > 32) {
                    data[offset++] = (char) next;
                    next = read();
                }

                return offset - originalOffset;
            }

        }
        static class Segment implements Cloneable {
            private Segment left;
            private Segment right;
            private int max;
            private int plus;

            public void plus(int m) {
                plus += m;
                max += m;
            }

            public void pushUp() {
                max = Math.max(left.max, right.max);
            }

            public void pushDown() {
                if (plus != 0) {
                    left.plus(plus);
                    right.plus(plus);
                    plus = 0;
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

            public void update(int ll, int rr, int l, int r) {
                if (noIntersection(ll, rr, l, r)) {
                    return;
                }
                if (covered(ll, rr, l, r)) {
                    plus(1);
                    return;
                }
                pushDown();
                int m = (l + r) >> 1;
                left.update(ll, rr, l, m);
                right.update(ll, rr, m + 1, r);
                pushUp();
            }

            public int query(int ll, int rr, int l, int r) {
                if (noIntersection(ll, rr, l, r)) {
                    return 0;
                }
                if (covered(ll, rr, l, r)) {
                    return max;
                }
                pushDown();
                int m = (l + r) >> 1;
                return Math.max(left.query(ll, rr, l, m), right.query(ll, rr, m + 1, r));
            }

        }
    }






    public static class Main {
        static InputStream is;
        static PrintWriter out;
        static String INPUT = "";

        static void solve()
        {
            int n = ni();
            char[] s = ns(n);
            char[] t = ns(n);
            if(Arrays.equals(s, t)){
                out.println(0);
                return;
            }
            int low = 0, high = 1000005;
            while(high-low>1){
                int h = high+low>>1;
                if(ok(h, s, t)){
                    high = h;
                }else{
                    low = h;
                }
            }
            if(high > 1000001){
                out.println(-1);
            }else{
                out.println(high);
            }
        }

        static boolean ok(int h, char[] s, char[] t)
        {
            int p = 0;
            int[] cs = new int[s.length+3];
            int qh = 0;
            int qt = 0;
            int[] rs = new int[s.length+3];
            int de = 0;
            for(int i = 0;i < t.length;i++){
                if(i > 0 && t[i] == t[i-1]){
                    if(i-2 < 0 || t[i-2] != t[i]){
                        rs[qh-1] = h-1+de;
                        cs[qh-1]--;
                        rs[qh] = h+de;
                        cs[qh++] = i+1-de;
                    }else{
                        cs[qh-1]++;
                    }
                }else{
                    while(true){
                        while(p < s.length && t[i] != s[p])p++;
                        if(p > i || s[p] != t[i])return false;
                        int lastr = -99999;
                        while(qt < qh && cs[qt]+de <= p){
                            lastr = rs[qt]-de;
                            qt++;
                        }
                        if(lastr != -99999)qt--;
                        if(lastr != -99999 && rs[qt]-de == 0){
                            p++;
                            continue;
                        }
                        de++;
                        rs[qh] = h+de;
                        cs[qh++] = i+1-de;
                        break;
                    }
                }
//			if(h == 1){
//				tr(rs);
//				tr(cs);
//			}
            }
            return true;
        }

        public static int sumFenwick(int[] ft, int i)
        {
            int sum = 0;
            for(i++;i > 0;i -= i&-i)sum += ft[i];
            return sum;
        }

        public static void addFenwick(int[] ft, int i, int v)
        {
            if(v == 0 || i < 0)return;
            int n = ft.length;
            for(i++;i < n;i += i&-i)ft[i] += v;
        }


        public static void main(String[] args) throws Exception
        {
            long S = System.currentTimeMillis();
            is = INPUT.isEmpty() ? System.in : new ByteArrayInputStream(INPUT.getBytes());
            out = new PrintWriter(System.out);

            solve();
            out.flush();
            long G = System.currentTimeMillis();
            tr(G-S+"ms");
        }

        private static boolean eof()
        {
            if(lenbuf == -1)return true;
            int lptr = ptrbuf;
            while(lptr < lenbuf)if(!isSpaceChar(inbuf[lptr++]))return false;

            try {
                is.mark(1000);
                while(true){
                    int b = is.read();
                    if(b == -1){
                        is.reset();
                        return true;
                    }else if(!isSpaceChar(b)){
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

        private static int readByte()
        {
            if(lenbuf == -1)throw new InputMismatchException();
            if(ptrbuf >= lenbuf){
                ptrbuf = 0;
                try { lenbuf = is.read(inbuf); } catch (IOException e) { throw new InputMismatchException(); }
                if(lenbuf <= 0)return -1;
            }
            return inbuf[ptrbuf++];
        }

        private static boolean isSpaceChar(int c) { return !(c >= 33 && c <= 126); }
        //	private static boolean isSpaceChar(int c) { return !(c >= 32 && c <= 126); }
        private static int skip() { int b; while((b = readByte()) != -1 && isSpaceChar(b)); return b; }

        private static double nd() { return Double.parseDouble(ns()); }
        private static char nc() { return (char)skip(); }

        private static String ns()
        {
            int b = skip();
            StringBuilder sb = new StringBuilder();
            while(!(isSpaceChar(b))){
                sb.appendCodePoint(b);
                b = readByte();
            }
            return sb.toString();
        }

        private static char[] ns(int n)
        {
            char[] buf = new char[n];
            int b = skip(), p = 0;
            while(p < n && !(isSpaceChar(b))){
                buf[p++] = (char)b;
                b = readByte();
            }
            return n == p ? buf : Arrays.copyOf(buf, p);
        }

        private static char[][] nm(int n, int m)
        {
            char[][] map = new char[n][];
            for(int i = 0;i < n;i++)map[i] = ns(m);
            return map;
        }

        private static int[] na(int n)
        {
            int[] a = new int[n];
            for(int i = 0;i < n;i++)a[i] = ni();
            return a;
        }

        private static int ni()
        {
            int num = 0, b;
            boolean minus = false;
            while((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-'));
            if(b == '-'){
                minus = true;
                b = readByte();
            }

            while(true){
                if(b >= '0' && b <= '9'){
                    num = num * 10 + (b - '0');
                }else{
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private static long nl()
        {
            long num = 0;
            int b;
            boolean minus = false;
            while((b = readByte()) != -1 && !((b >= '0' && b <= '9') || b == '-'));
            if(b == '-'){
                minus = true;
                b = readByte();
            }

            while(true){
                if(b >= '0' && b <= '9'){
                    num = num * 10 + (b - '0');
                }else{
                    return minus ? -num : num;
                }
                b = readByte();
            }
        }

        private static void tr(Object... o) { if(INPUT.length() != 0)System.out.println(Arrays.deepToString(o)); }
    }

    private static class Generator extends RandomFactory{
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(1, 10);
            String s = RandomUtils.getRandomString(random, 'a', 'd', n);
            String t = RandomUtils.getRandomString(random, 'a', 'd', n);
            return in.add(n).add(s).add(t).end();
        }
    }
}
