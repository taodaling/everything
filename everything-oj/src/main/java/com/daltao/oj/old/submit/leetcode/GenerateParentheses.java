package com.daltao.oj.old.submit.leetcode;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */
public class GenerateParentheses {
    List<String> list;
    char[] buf;
    int countLimit;

    public List<String> generateParenthesis(int n) {
        list = new LinkedList<>();
        int leftParanthesisCount = 0;
        int rightParanthesisCount = 0;
        countLimit = n;
        buf = new char[countLimit * 2];
        rec(0, 0);
        return list;
    }

    public void rec(int leftParanthesisCount, int rightParanthesisCount) {
        int depth = leftParanthesisCount + rightParanthesisCount;
        if (depth == buf.length) {
            list.add(String.valueOf(buf));
            return;
        }
        if (leftParanthesisCount == countLimit) {
            for (int i = depth, bound = buf.length; i < bound; i++) {
                buf[i] = ')';
            }
            list.add(String.valueOf(buf));
            return;
        } else {
            buf[depth] = '(';
            rec(leftParanthesisCount + 1, rightParanthesisCount);
        }
        if (rightParanthesisCount < leftParanthesisCount) {
            buf[depth] = ')';
            rec(leftParanthesisCount, rightParanthesisCount + 1);
        }
    }
}
