package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/6/4.
 */
public class MedianOfTwoSortedArrays2 {
    public static void main(String[] args) {
        int[] nums1 = new int[]{1};
        int[] nums2 = new int[]{2, 3, 4};
        System.out.println(new MedianOfTwoSortedArrays2().findMedianSortedArrays(nums1, nums2));
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            int[] temp = nums1;
            nums1 = nums2;
            nums2 = temp;
        }

        int len1 = nums1.length;
        int len2 = nums2.length;
        int totalLength = len1 + len2;
        int halfLength = totalLength >> 1;
        int offset = totalLength & 1;
        int lbound = 0;
        int rbound = nums1.length;
        int precalc = (totalLength + offset) >> 1;
        while (lbound <= rbound) {
            int i = (lbound + rbound) >> 1;
            int j = precalc - i;
            if (i > 0 && nums1[i - 1] > nums2[j]) {
                rbound = i;
            } else if (i < len1 && nums2[j - 1] > nums1[i]) {
                lbound = i + 1;
            } else {
                int smallmid = i <= 0 ? nums2[j - 1] : j <= 0 ? nums1[i - 1] : Math.max(nums1[i - 1], nums2[j - 1]);
                if (offset == 0) {
                    int largermid = i >= len1 ? nums2[j] : j >= len2 ? nums1[i] : Math.min(nums1[i], nums2[j]);
                    return (smallmid + largermid) / 2.0;
                }
                return smallmid;
            }
        }
        return -1;
    }

}
