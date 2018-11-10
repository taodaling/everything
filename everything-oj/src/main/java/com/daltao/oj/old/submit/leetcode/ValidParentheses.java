package com.daltao.oj.old.submit.leetcode;

import java.util.Stack;

/**
 * Created by Administrator on 2017/6/20.
 */
public class ValidParentheses {
    public boolean isValid(String s) {
        Stack<Character> stk = new Stack<>();
        for (int i = 0, bound = s.length(); i < bound; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '(':
                case '{':
                case '[':
                    stk.push(c);
                    break;
                case ')':
                    if (stk.empty() || stk.pop() != '(') {
                        return false;
                    }
                    break;
                case '}':
                    if (stk.empty() || stk.pop() != '{') {
                        return false;
                    }
                    break;
                case ']':
                    if (stk.empty() || stk.pop() != '[') {
                        return false;
                    }
                    break;
            }
        }
        return stk.empty();
    }
}
