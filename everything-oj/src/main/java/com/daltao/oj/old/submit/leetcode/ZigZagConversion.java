package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/6/9.
 */
public class ZigZagConversion {
    public String convert(String s, int numRows) {
        int slen = s.length();
        if (numRows == 1) {
            return s;
        }
        StringBuilder sb = new StringBuilder(slen);
        int totalstep = numRows + numRows - 2;
        //The first line
        for (int i = 0, bound = slen; i < bound; i += totalstep) {
            sb.append(s.charAt(i));
        }
        //The following line
        for (int i = 1, iBound = numRows - 1; i < iBound; i++) {
            int step = totalstep - i * 2;
            for (int j = i, jBound = slen; j < jBound; j += totalstep) {
                sb.append(s.charAt(j));
                if (j + step < jBound) {
                    sb.append(s.charAt(j + step));
                }
            }
        }

        //The last line
        for (int i = numRows - 1, iBound = slen; i < iBound; i += totalstep) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
}
