package com.daltao.oj.old.submit.leetcode;

import java.util.BitSet;

/**
 * Created by Administrator on 2017/6/7.
 */
public class LongestPalindromicSubstring {
    int slen;
    BitSet visited;
    BitSet isPalindromic;
    String s;

    public String longestPalindrome(String s) {
        this.s = s;
        slen = s.length();
        visited = new BitSet(slen * slen);
        isPalindromic = new BitSet(slen * slen);

        int longest = s.length() == 0 ? 0 : 1;
        int longestfrom = 0;
        for (int i = 0; i < slen && slen - i > longest; i++) {
            for (int j = slen - 1; j > i; j--) {
                if (rec(i, j) && j - i + 1 > longest) {
                    longest = j - i + 1;
                    longestfrom = i;
                }
            }
        }

        return s.substring(longestfrom, longestfrom + longest);
    }

    public int getIndex(int from, int to) {
        return from * slen + to;
    }

    public boolean rec(int from, int to) {
        int index = getIndex(from, to);
        if (visited.get(index)) {
            return isPalindromic.get(index);
        }
        visited.set(index, true);
        if (to - from <= 0) {
            isPalindromic.set(index, true);
            return true;
        }
        boolean res = s.charAt(from) == s.charAt(to) && rec(from + 1, to - 1);
        isPalindromic.set(index, res);
        return res;
    }
}
