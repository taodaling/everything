package com.daltao.oj.old.submit.leetcode;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/9/15.
 */
public class FindTheClosestPalindrome {
    static String LONG_MAX_STRING = Long.toString(Long.MAX_VALUE);
    static Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String a, String b) {
            if (a.length() != b.length()) {
                return a.length() - b.length();
            }
            return a.compareTo(b);
        }
    };

    public String nearestPalindromic(String n) {
        char[] data = n.toCharArray();
        if (data[0] == '0') {
            int blankUtil = 0;
            while (data[blankUtil] == '0') {
                blankUtil++;
            }
            n = n.substring(blankUtil);
            data = n.toCharArray();
        }

        long value = Long.parseLong(n);
        if (value < 10) {
            return Long.toString(value - 1);
        }

        int size = n.length();
        //Same length result
        char[] near = new char[n.length()];
        int l, r;
        for (l = 0, r = n.length() - 1; l <= r; l++, r--) {
            near[l] = data[l];
            near[r] = near[l];
        }
        char[] possibleUpper = near;
        char[] possibleLower = near;
        int cmp = comparator.compare(String.valueOf(near), n);
        if (cmp >= 0) {
            possibleLower = near.clone();
            int lc = l - 1;
            int rc = r + 1;
            while (lc >= 0) {
                if (possibleLower[lc] == '0') {
                    possibleLower[lc] = possibleLower[rc] = '9';
                    lc--;
                    rc++;
                } else {
                    possibleLower[lc] = possibleLower[rc] = (char) (possibleLower[rc] - 1);
                    break;
                }
            }

            if (lc < 0 || (lc == 0 && possibleLower[0] == '0')) {
                possibleLower = new char[size - 1];
                for (int i = size - 2; i >= 0; i--) {
                    possibleLower[i] = '9';
                }
            }
        }
        if (cmp <= 0) {
            possibleUpper = near.clone();
            int lc = l - 1;
            int rc = r + 1;
            while (lc >= 0) {
                if (possibleUpper[lc] == '9') {
                    possibleUpper[lc] = possibleUpper[rc] = '0';
                    lc--;
                    rc++;
                } else {
                    possibleUpper[lc] = possibleUpper[rc] = (char) (possibleUpper[rc] + 1);
                    break;
                }
            }
            if (lc < 0) {
                possibleUpper = new char[size + 1];
                for (int i = size - 1; i > 0; i--) {
                    possibleUpper[i] = '0';
                }
                possibleUpper[0] = possibleUpper[size] = '1';
            }
        }

        //Check to avoid exceed the range of long
        String sLower = String.valueOf(possibleLower);
        String sUpper = String.valueOf(possibleUpper);
        if (comparator.compare(sUpper, LONG_MAX_STRING) > 0) {
            sUpper = "0";
        }
        long vLower = Long.parseLong(sLower);
        long vUpper = Long.parseLong(sUpper);
        int absOffset = Long.compare(Math.abs(value - vLower), Math.abs(vUpper - value));
        if (absOffset <= 0) {
            return sLower;
        } else {
            return sUpper;
        }
    }
}
