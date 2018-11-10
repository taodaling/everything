package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/6/13.
 */
public class RegularExpressionMatching {
    byte[][] matchStatuses;
    String text;
    int tlen;
    char[] patternBuf;
    int plen;
    int[] patternExtra;
    final int FLEXIBLE = 1 << 0;
    final int MATCH_ALL = 1 << 1;

    public boolean isMatch(String s, String p) {


        //Precalculate all needed information
        int starCount = 0;
        for (int i = 0, iBound = p.length(); i < iBound; i++) {
            if (p.charAt(i) == '*') {
                starCount++;
            }
        }
        text = s;
        int validPatternLength = p.length() - starCount;
        patternBuf = new char[validPatternLength];
        patternExtra = new int[validPatternLength];
        int wpos = 0;
        for (int i = 0, iBound = p.length(); i < iBound; i++, wpos++) {
            char ch = p.charAt(i);
            if (ch == '*') {
                wpos--;
                patternExtra[wpos] |= FLEXIBLE;
            } else if (ch == '.') {
                patternExtra[wpos] |= MATCH_ALL;
            } else {
                patternBuf[wpos] = ch;
            }
        }
        tlen = s.length() + 1;
        plen = validPatternLength + 1;
        matchStatuses = new byte[plen][tlen];
        matchStatuses[plen - 1][tlen - 1] = 1;
        for (int i = 0, iBound = tlen - 1; i < iBound; i++) {
            matchStatuses[validPatternLength][i] = -1;
        }
        for (int i = plen - 2; i >= 0; i--) {
            matchStatuses[i][tlen - 1] = (byte) (matchStatuses[i + 1][tlen - 1] == 1 && (patternExtra[i] & FLEXIBLE) == FLEXIBLE ? 1 : -1);
        }
        return match(0, 0);
    }

    public boolean match(int i, int j) {
        if (matchStatuses[i][j] != 0) {
            return matchStatuses[i][j] == 1;
        }
        boolean flag;
        if ((patternExtra[i] & FLEXIBLE) == 0) {
            flag =  ((patternExtra[i] & MATCH_ALL) == MATCH_ALL || patternBuf[i] == text.charAt(j)) && match(i + 1, j + 1);
        } else {
            boolean matchAllFlag = (patternExtra[i] & MATCH_ALL) == MATCH_ALL;
            flag = match(i + 1, j);
            if (!flag) {
                flag = (matchAllFlag || patternBuf[i] == text.charAt(j)) && match(i, j + 1);
            }
        }
        matchStatuses[i][j] = (byte)(flag ? 1 : -1);
        return flag;
    }
}
