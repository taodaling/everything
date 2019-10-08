package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.RandomUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ARC060DTest {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(ARC060D.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setTestTime(10000)
                .build().call());
    }


    public static class Main {
        static class Scanner {
            BufferedReader br;
            StringTokenizer tk = new StringTokenizer("");

            public Scanner(InputStream is) {
                br = new BufferedReader(new InputStreamReader(is));
            }

            public int nextInt() throws IOException {
                if (tk.hasMoreTokens())
                    return Integer.parseInt(tk.nextToken());
                tk = new StringTokenizer(br.readLine());
                return nextInt();
            }

            public long nextLong() throws IOException {
                if (tk.hasMoreTokens())
                    return Long.parseLong(tk.nextToken());
                tk = new StringTokenizer(br.readLine());
                return nextLong();
            }

            public String next() throws IOException {
                if (tk.hasMoreTokens())
                    return (tk.nextToken());
                tk = new StringTokenizer(br.readLine());
                return next();
            }

            public String nextLine() throws IOException {
                tk = new StringTokenizer("");
                return br.readLine();
            }

            public double nextDouble() throws IOException {
                if (tk.hasMoreTokens())
                    return Double.parseDouble(tk.nextToken());
                tk = new StringTokenizer(br.readLine());
                return nextDouble();
            }

            public char nextChar() throws IOException {
                if (tk.hasMoreTokens())
                    return (tk.nextToken().charAt(0));
                tk = new StringTokenizer(br.readLine());
                return nextChar();
            }

            public int[] nextIntArray(int n) throws IOException {
                int a[] = new int[n];
                for (int i = 0; i < n; i++)
                    a[i] = nextInt();
                return a;
            }

            public long[] nextLongArray(int n) throws IOException {
                long a[] = new long[n];
                for (int i = 0; i < n; i++)
                    a[i] = nextLong();
                return a;
            }

            public int[] nextIntArrayOneBased(int n) throws IOException {
                int a[] = new int[n + 1];
                for (int i = 1; i <= n; i++)
                    a[i] = nextInt();
                return a;
            }

            public long[] nextLongArrayOneBased(int n) throws IOException {
                long a[] = new long[n + 1];
                for (int i = 1; i <= n; i++)
                    a[i] = nextLong();
                return a;
            }


        }

        public static void main(String args[]) throws IOException, InterruptedException {
            Thread t = new Thread(null, new Runnable() {
                public void run() {
                    try {
                        solve();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "1", 1 << 26);
            t.start();
            t.join();
        }

        static void solve() throws IOException {
            Scanner in = new Scanner(System.in);
            PrintWriter out = new PrintWriter(System.out);
            String s = in.next();
            if (s.length() == 1) {
                System.out.println("1\n1");
                return;
            }
            boolean vp[] = vp(s);
            if (vp[s.length() - 1]) {
                out.println(1);
                out.println(1);
            } else {
                int cnt = 0;
                for (int i = 1; i < s.length(); i++) {
                    if (s.charAt(i) == s.charAt(i - 1)) {
                        cnt++;
                    }
                }
                if (cnt == s.length() - 1) {
                    out.println(s.length());
                    out.println(1);
                } else {
                    String sr = reverse(s);
                    boolean vs[] = reverse(vp(sr));
                    cnt = 0;
                    for (int i = 1; i < s.length(); i++) {
                        if (vs[i] && vp[i - 1])
                            cnt++;
                    }
                    out.println(2);
                    out.println(cnt);
                }
            }
            out.close();

        }

        static String reverse(String s) {
            return new StringBuilder(s).reverse().toString();
        }

        static boolean[] reverse(boolean[] a) {
            int i = 0, j = a.length - 1;
            while (i < j) {
                a[i] ^= a[j];
                a[j] ^= a[i];
                a[i] ^= a[j];
                i++;
                j--;
            }
            return a;
        }

        static boolean[] vp(String s) {
            int n = s.length();
            int pf[] = new int[n];
            char sa[] = s.toCharArray();
            boolean vp[] = new boolean[n];
            Arrays.fill(vp, true);
            for (int i = 1; i < n; i++) {
                int j = pf[i - 1];
                while (j > 0 && sa[j] != sa[i])
                    j = pf[j - 1];

                if (sa[j] == sa[i])
                    j++;
                pf[i] = j;
                if (pf[i] > 0 && (i + 1) % (i + 1 - pf[i]) == 0)
                    vp[i] = false;
            }

            return vp;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            input.add(RandomUtils.getRandomString(random, 'a', 'c', 6));
            return input.end();
        }
    }
}
