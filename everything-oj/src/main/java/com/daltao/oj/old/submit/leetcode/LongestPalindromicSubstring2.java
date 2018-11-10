package com.daltao.oj.old.submit.leetcode;


/**
 * Created by dalt on 2017/6/7.
 */
public class LongestPalindromicSubstring2 {
    public String longestPalindrome(String s) {
        if (s.length() <= 1) {
            return s;
        }
        char[] buf = new char[s.length() * 2 + 1];
        for (int i = 0, bound = buf.length; i < bound; i += 2) {
            buf[i] = ' ';
        }
        for (int i = 1, bound = buf.length; i < bound; i += 2) {
            buf[i] = s.charAt(i >> 1);
        }
        int blen = buf.length;
        int blenMinusOne = blen - 1;

        int[] width = new int[blen];
        int mxWidth = 0;
        int mxCenter = 0;
        int mxRightBound = mxCenter + mxWidth; //The right bound include
        width[0] = 0;

        int bestCenter = 0;

        for (int i = 1; i < blen; i++) {
            int leastwidth = 0;
            //If locate at the circle organized by current rightest palindromic substring
            if (i < mxRightBound) {
                int j = 2 * mxCenter - i;
                int remain = mxRightBound - i;
                if (width[j] > remain) {
                    width[i] = remain;
                    continue;
                } else if (width[j] < remain) {
                    width[i] = width[j];
                    continue;
                } else {
                    leastwidth = remain;
                }
            }

            int rbound = i + leastwidth;
            int lbound = i - leastwidth;
            while (rbound < blenMinusOne && lbound > 0 && buf[rbound + 1] == buf[lbound - 1]) {
                rbound++;
                lbound--;
            }
            width[i] = rbound - i;

            //Change the rightest palindromic substring if needed
            mxCenter = i;
            mxWidth = width[i];
            mxRightBound = i + mxWidth;

            if (mxWidth > width[bestCenter]) {
                bestCenter = i;
            }
        }
        int longestlength = width[bestCenter];
        int startPoint = (bestCenter - longestlength + 1) / 2;
        return s.substring(startPoint, startPoint + longestlength);
    }
}
