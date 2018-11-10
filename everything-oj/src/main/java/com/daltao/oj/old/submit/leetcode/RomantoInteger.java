package com.daltao.oj.old.submit.leetcode;


/**
 * Created by dalt on 2017/6/18.
 */
public class RomantoInteger {

    static int[] ranks = new int[256];

    static {
        char[] signatures = new char[]{'I', 'V', 'X', 'L', 'C', 'D', 'M'};
        int[] valueRepresented = new int[]{1, 5, 10, 50, 100, 500, 1000};
        for (int i = 0, bound = signatures.length; i < bound; i++) {
            ranks[signatures[i]] = valueRepresented[i];
        }
    }

    public int romanToInt(String s) {
        int curMax = 0;
        int lastDifferentValue = 0;
        int curSign = 1;
        int result = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            int v = ranks[c];
            if (v >= curMax) {
                curMax = v;
                lastDifferentValue = v;
                curSign = 1;
            } else if (v < lastDifferentValue) {
                lastDifferentValue = v;
                curSign *= -1;
            }
            result += curSign * v;
        }
        return result;
    }
}
