package com.daltao.template;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by dalt on 2018/5/20.
 */
public class Sortable {
    private static final int THRESHOLD = 4;

    public static <T> T theKthSmallestElement(T[] data, Comparator<T> cmp, int f, int t, int k) {
        Memory.swap(data, f, Randomized.nextInt(f, t - 1));
        int l = f;
        int r = t;
        int m = l + 1;
        while (m < r) {
            int c = cmp.compare(data[m], data[l]);
            if (c == 0) {
                m++;
            } else if (c < 0) {
                Memory.swap(data, l, m);
                l++;
                m++;
            } else {
                Memory.swap(data, m, --r);
            }
        }
        if (l - f >= k) {
            return theKthSmallestElement(data, cmp, f, l, k);
        } else if (m - f >= k) {
            return data[l];
        }
        return theKthSmallestElement(data, cmp, m, t, k - (m - f));
    }
}
