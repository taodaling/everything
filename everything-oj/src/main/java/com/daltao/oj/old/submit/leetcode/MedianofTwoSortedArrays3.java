package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;

public class MedianofTwoSortedArrays3 {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if ((nums1.length + nums2.length) % 2 == 0) {
            int half = (nums1.length + nums2.length) / 2;
            return (findMedianSortedArrays(new List(nums1, 0, nums1.length), new List(nums2, 0, nums2.length), half + 1) +
                    findMedianSortedArrays(new List(nums1, 0, nums1.length), new List(nums2, 0, nums2.length), half)) / 2;
        }
        return findMedianSortedArrays(new List(nums1, 0, nums1.length), new List(nums2, 0, nums2.length), (nums1.length + nums2.length + 1) / 2);
    }

    public double findMedianSortedArrays(List a, List b, int k) {
        if (a.size() > k) {
            a = a.subList(0, k);
        }
        if (b.size() > k) {
            b = b.subList(0, k);
        }

        if (a.size() == 0) {
            return b.get(k - 1);
        }
        if (b.size() == 0) {
            return a.get(k - 1);
        }
        if (k == 1) {
            return Math.min(a.get(0), b.get(0));
        }

        if (k == a.size() + b.size()) {
            return Math.max(a.get(a.size() - 1), b.get(b.size() - 1));
        }

        List longer;
        List shorter;
        if (a.size() < b.size()) {
            longer = b;
            shorter = a;
        } else {
            longer = a;
            shorter = b;
        }

        if (shorter.size() == 1) {
            if (longer.get(k - 1) > shorter.get(0)) {
                return Math.max(longer.get(k - 2), shorter.get(0));
            } else {
                return longer.get(k - 1);
            }
        }


        int halfS = shorter.size() >> 1;
        int halfL = k - halfS - 2;
        if (halfL < 0) {
            halfS += halfL;
            halfL = 0;
        }
        if (longer.get(halfL) <= shorter.get(halfS)) {
            return findMedianSortedArrays(longer.subList(halfL + 1, longer.size()), shorter.subList(0, halfS + 1), k - halfL - 1);
        } else {
            return findMedianSortedArrays(shorter.subList(halfS + 1, shorter.size()), longer.subList(0, halfL + 1), k - halfS - 1);
        }
    }

    public static class List {
        final int[] data;
        final int from;
        final int to;

        public List(int[] data, int from, int to) {
            this.data = data;
            this.from = from;
            this.to = to;
        }

        public int size() {
            return to - from;
        }

        public int get(int i) {
            return data[from + i];
        }

        public List subList(int from, int to) {
            return new List(data, this.from + from, this.from + to);
        }

        @Override
        public String toString() {
            return Arrays.toString(Arrays.copyOfRange(data, from, to));
        }
    }
}
