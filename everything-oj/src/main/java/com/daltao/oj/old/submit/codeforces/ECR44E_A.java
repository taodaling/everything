package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.util.Arrays;
import java.util.StringTokenizer;


public class ECR44E_A {

    static StringTokenizer st;
    static BufferedReader br;
    static PrintWriter pw;

    public static void main(String[] args) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        int n = nextInt();
        int k = nextInt();
        int d = nextInt();
        Integer[] a = new Integer[n + 1];
        a[0] = (int) (-1e9 - 9);
        for (int i = 1; i <= n; i++) {
            a[i] = nextInt();
        }
        Arrays.sort(a);
        int[] sum = new int[n + 1];
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        sum[0] = 1;
        int left = 0;
        for (int i = 1; i <= n; i++) {
            sum[i] = sum[i - 1];
            while (a[i] - a[left] > d)
                left++;
            if (left - 1 <= i - k) {
                int cnt = sum[i - k];
                if (left - 2 >= 0)
                    cnt -= sum[left - 2];
                if (cnt > 0)
                    dp[i] = true;
            }
            if (dp[i])
                sum[i]++;
        }
        if (dp[n])
            System.out.println("YES");
        else
            System.out.println("NO");
        pw.close();
    }

    private static int nextInt() throws IOException {
        return Integer.parseInt(next());
    }

    private static long nextLong() throws IOException {
        return Long.parseLong(next());
    }

    private static double nextDouble() throws IOException {
        return Double.parseDouble(next());
    }

    private static String next() throws IOException {
        while (st == null || !st.hasMoreTokens())
            st = new StringTokenizer(br.readLine());
        return st.nextToken();
    }
}