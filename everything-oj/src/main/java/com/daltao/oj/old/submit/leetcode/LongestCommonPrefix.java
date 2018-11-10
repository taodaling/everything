package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/6/18.
 */
public class LongestCommonPrefix {
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            throw new IllegalArgumentException();
        }
        String shortestStr = strs[0];
        for (String s : strs) {
            if (s.length() < shortestStr.length()) {
                shortestStr = s;
            }
        }

        int cpl = shortestStr.length();
        for (String s : strs) {
            int i = 0;
            for (; i < cpl && s.charAt(i) == shortestStr.charAt(i); i++);
            cpl = Math.min(i, cpl);
        }
        return shortestStr.substring(0, cpl);
    }
}
