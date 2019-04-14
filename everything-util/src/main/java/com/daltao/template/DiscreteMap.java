package com.daltao.template;

import java.util.Arrays;

public class DiscreteMap {
    int[] val;
    int f;
    int t;

    public DiscreteMap(int[] val, int f, int t) {
        Randomized.randomizedArray(val, f, t);
        Arrays.sort(val, f, t);
        int wpos = f + 1;
        for (int i = f + 1; i < t; i++) {
            if (val[i] == val[i - 1]) {
                continue;
            }
            val[wpos++] = val[i];
        }
        this.val = val;
        this.f = f;
        this.t = wpos;
    }

    public int rankOf(int x) {
        return Arrays.binarySearch(val, f, t, x);
    }
}
