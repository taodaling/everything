package com.daltao.oj.old.submit.leetcode;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Created by dalt on 2018/4/7.
 */
public class LC327_2 {
    public int countRangeSum(int[] nums, int lower, int upper) {
        int n = nums.length;
        long s = 0;
        int cnt = 0;
        int[] val = new int[1];
        TreeSet<long[]> treeSet = new TreeSet<>(new Comparator<long[]>() {
            @Override
            public int compare(long[] o1, long[] o2) {
                int cmp = Long.compare(o1[0], o2[0]);
                if (cmp == 0) {
                    cmp = Long.compare(o1[1], o2[1]);
                }
                return cmp;
            }
        });
        treeSet.add(new long[]{0, -1});
        for (int i = 0; i < n; i++) {
            s += nums[i];
            //lower <= s - a <= upper
            //that's meaning s-upper<=a<=s-lower
            cnt += treeSet.headSet(new long[]{s - lower, i}, true)
                    .tailSet(new long[]{s - upper, -1}, true).size();
            treeSet.add(new long[]{s, i});
        }
        return cnt;
    }
}
