package com.daltao.oj.submit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LtCd92 {
    public static void main(String[] args) {
        System.out.println(new Solution().primePalindrome(96));
    }

    static class Solution {
        static int[] bases = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

        public int primePalindrome(int N) {
            int d = 0;
            while (N / bases[d] >= 10) {
                d++;
            }

            for (; ; d++) {
                List<Integer> nums = findPrime(d + 1);
                for (Integer num : nums) {
                    if (num < N) {
                        continue;
                    }

                    BigInteger val = BigInteger.valueOf(num.intValue());
                    if (val.isProbablePrime(20)) {
                        return num;
                    }
                }
            }
        }

        public List<Integer> findPrime(int n) {
            List<Integer> set = new ArrayList<>();
            if (n == 1) {
                build(n, 0, 0, set);
            } else {
                int base = bases[0] + bases[n - 1];
                for (int i = 1; i < 10; i++) {
                    build(n, 1, i * base, set);
                }
            }

            return set;
        }

        public static void build(int n, int i, int val, List<Integer> set) {
            int other = n - 1 - i;

            if (i > other) {
                set.add(val);
                return;
            }

            int base;
            if (i == other) {
                base = bases[i];
            } else {
                base = (bases[i] + bases[other]);
            }

            for (int j = 0; j < 10; j++) {
                build(n, i + 1, val + j * base, set);
            }
        }
    }
}
