package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/6/18.
 */
public class IntegertoRoman {
    public String intToRoman(int num) {
        StringBuilder s = new StringBuilder();
        char[] signatures = new char[]{'I', 'V', 'X', 'L', 'C', 'D', 'M', ' ', ' '};
        int[] valueRepresented = new int[]{1, 5, 10, 50, 100, 500, 1000, 5000, 10000};
        for (int i = signatures.length - 1; i >= 2; i -= 2) {
            int tenPos = i;
            int fivePos = i - 1;
            int onePos = i - 2;
            int oneValue = valueRepresented[onePos];
            int value = (num / oneValue) % 10;
            if (value <= 3) {
                for (int j = 0; j < value; j++) {
                    s.append(signatures[onePos]);
                }
            } else if (value <= 8) {
                for (int j = 5 - 1; j >= value; j--) {
                    s.append(signatures[onePos]);
                }
                s.append(signatures[fivePos]);
                for (int j = 5; j < value; j++) {
                    s.append(signatures[onePos]);
                }
            } else {
                if (value == 9) {
                    s.append(signatures[onePos]);
                }
                s.append(signatures[tenPos]);
            }
        }
        return s.toString();
    }
}
