package com.daltao.oj.submit;

import java.util.Arrays;

public class LTSolution {

    public static void main(String[] args) {
        System.out.println(new Solution().superpalindromesInRange("1",
                "2"));
    }



    static class Solution {
        public static long[] cache = new long[100000];
        public static int cacheLen = 0;
        public static int[] buf = new int[100];
        public static long[] retval = new long[2];
        public static long[] radix = new long[18];
        static {
            loadCache();
        }

        public int superpalindromesInRange(String L, String R) {

            return notMoreThan(Long.parseLong(R)) - notMoreThan(Long.parseLong(L) - 1);
        }

        public static int notMoreThan(long v) {
            int num = 0;
            for (int i = 0; i < cacheLen; i++) {
                if (cache[i] <= v) {
                    num++;
                } else {
                    break;
                }
            }
            return num;
        }


        public static void loadCache() {
            radix[1] = 1;
            for (int i = 2; i < radix.length; i++) {
                radix[i] = radix[i - 1] * 10;
            }

            cache[cacheLen++] = 0;
            long limit = (long) 1e9;
            for (int i = 1; ; i++) {
                long[] val = asPalindrom(i);
                if (val[0] > limit && val[1] > limit) {
                    break;
                }

                if (val[0] <= limit) {
                    long sq = val[0] * val[0];
                    if (isPalindrom(sq)) {
                        cache[cacheLen++] = sq;
                    }
                }
                if (val[1] <= limit) {
                    long sq = val[1] * val[1];
                    if (isPalindrom(sq)) {
                        cache[cacheLen++] = sq;
                    }
                }
            }

            Arrays.sort(cache, 0, cacheLen);
        }

        public static boolean isPalindrom(long n) {
            int len = toIntArray(n);
            int l = 1;
            int r = len;
            while (l < r) {
                if (buf[l] != buf[r]) {
                    return false;
                }
                l++;
                r--;
            }
            return true;
        }

        public static int toIntArray(long n) {
            int i = 1;
            for (; n > 0; i++, n /= 10) {
                buf[i] = (int) (n % 10);
            }
            return i - 1;
        }

        public static long[] asPalindrom(int n) {
            int i = toIntArray(n);

            int m = n;

            long even = m * radix[i + 1];
            long odd = m * radix[i];
            for (int j = 1; j <= i; j++) {
                even += radix[j] * buf[i - j + 1];
                if (i != j) {
                    odd += radix[j] * buf[i - j + 1];
                }
            }

            retval[0] = odd;
            retval[1] = even;
            return retval;
        }
    }

}
