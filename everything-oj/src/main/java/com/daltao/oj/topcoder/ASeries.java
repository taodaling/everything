package com.daltao.oj.topcoder;

import java.util.Arrays;

public class ASeries {
    public int longest(int[] values) {
        Arrays.sort(values);
        int n = values.length;
        int ans = 1;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                ans = Math.max(ans, best(values, values[j] - values[i]));
            }
        }
        return ans;
    }

    public int best(int[] values, int k) {
        int max = 0;
        int n = values.length;
        for (int i = 0; i < n; i++) {
            int now = values[i];
            int len = 1;
            for (int j = i + 1; j < n; j++) {
                if (values[j] == now + k) {
                    len++;
                    now = values[j];
                }
            }
            max = Math.max(max, len);
        }
        return max;
    }
}
