package com.daltao.oj.topcoder;

public class Abacus {
    public String[] add(String[] original, int val) {
        String[] ans = original.clone();
        int v = valueOf(original) + val;
        for (int i = ans.length - 1; i >= 0; i--) {
            ans[i] = toString(v % 10);
            v /= 10;
        }
        return ans;
    }

    int valueOf(String[] x) {
        int ans = 0;
        for (String s : x) {
            ans = ans * 10 + parse(s);
        }
        return ans;
    }

    public String toString(int digit) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 9 - digit; i++) {
            builder.append('o');
        }
        builder.append("---");
        for (int i = 0; i < digit; i++) {
            builder.append('o');
        }
        return builder.toString();
    }

    private int parse(String s) {
        return s.length() - (s.indexOf("---") + 3);
    }
}
