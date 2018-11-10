package com.daltao.oj.old.submit.projecteuler;

/**
 * Created by dalt on 2018/4/2.
 */
public class PE10 {
    public static void main(String[] args) {
        final int N = (int) 2e6;
        boolean[] isComposite = new boolean[N + 1];
        int[] primes = new int[N + 1];
        int primesLen = 0;
        isComposite[1] = true;
        long sum = 0;
        for (int i = 2; i <= N; i++) {
            if (!isComposite[i]) {
                sum += i;
                primes[primesLen++] = i;
            }
            for (int j = 0; primes[j] * i <= N; j++) {
                int ij = primes[j] * i;
                isComposite[ij] = true;
                if (i % primes[j] == 0) {
                    break;
                }
            }
        }

        System.out.println(sum);
    }
}
