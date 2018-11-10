package com.daltao.oj.template;

import java.util.Comparator;

/**
 * Created by dalt on 2018/5/20.
 */
public class St<T> {
    //st[i][j] means the min value between [i, i + 2^j),
    //so st[i][j] equals to min(st[i][j - 1], st[i + 2^(j - 1)][j - 1])
    Object[][] st;
    Comparator<T> comparator;

    int[] floorLogTable;

    public St(Object[] data, int length, Comparator<T> comparator) {
        int m = floorLog2(length);
        st = new Object[length][m + 1];
        this.comparator = comparator;
        for (int i = 0; i < length; i++) {
            st[i][0] = data[i];
        }
        for (int i = 0; i < m; i++) {
            int interval = 1 << i;
            for (int j = 0; j < length; j++) {
                if (j + interval < length) {
                    st[j][i + 1] = min((T) st[j][i], (T) st[j + interval][i]);
                } else {
                    st[j][i + 1] = st[j][i];
                }
            }
        }

        floorLogTable = new int[length + 1];
        int log = 1;
        for (int i = 0; i <= length; i++) {
            if ((1 << log) <= i) {
                log++;
            }
            floorLogTable[i] = log - 1;
        }
    }

    public static int floorLog2(int x) {
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    private T min(T a, T b) {
        return comparator.compare(a, b) <= 0 ? a : b;
    }

    public static int ceilLog2(int x) {
        return 32 - Integer.numberOfLeadingZeros(x - 1);
    }

    /**
     * query the min value in [left,right]
     */
    public T query(int left, int right) {
        int queryLen = right - left + 1;
        int bit = floorLogTable[queryLen];
        //x + 2^bit == right + 1
        //So x should be right + 1 - 2^bit - left=queryLen - 2^bit
        return min((T) st[left][bit], (T) st[right + 1 - (1 << bit)][bit]);
    }
}