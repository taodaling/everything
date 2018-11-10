package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/6/4.
 */
public class MedianOfTwoSortedArrays {
    public static void main(String[] args) {
        int[] nums1 = new int[]{1,2};
        int[] nums2 = new int[]{3,4};
        System.out.println(new MedianOfTwoSortedArrays().findMedianSortedArrays(nums1, nums2));
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int totalLength = nums1.length + nums2.length - 1;
        if (totalLength % 2 == 0) {
            return findMedianSortedArrays(nums1, nums2, totalLength / 2);
        } else {
            return (findMedianSortedArrays(nums1, nums2, totalLength / 2) + findMedianSortedArrays(nums1, nums2, totalLength / 2 + 1)) / 2;
        }
    }

    static class SubList {
        final int[] arr;
        final int offset;
        final int length;

        public SubList(int[] arr, int from, int to) {
            this.arr = arr;
            this.offset = from;
            this.length = to - from;
        }

        public int length() {
            return length;
        }

        @Override
        public String toString() {
            String s = "";
            for (int i = offset, bound = offset + length; i < bound; i++) {
                s += arr[i] + ", ";
            }
            return "[" + s.substring(0, s.length() - 2) + "]";
        }

        public int elementAt(int i) {
            return arr[i + offset];
        }

        public SubList sub(int from, int to) {
            return new SubList(arr, from + offset, offset + to);
        }

    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2, int order) {
        return findMedianSortedArrays(new SubList(nums1, 0, nums1.length),
                new SubList(nums2, 0, nums2.length),
                order);
    }

    public double findMedianSortedArrays(SubList nums1, SubList nums2, int order) {
        SubList larger = nums1.length() > nums2.length() ? nums1 : nums2;
        SubList smaller = nums1.length() > nums2.length() ? nums2 : nums1;
        if (smaller.length() == 0) {
            return larger.elementAt(order);
        }
        if (smaller.length() == 1) {
            int lbound = 0;
            int singleValue = smaller.elementAt(0);
            int rbound = larger.length() - 1;
            while (lbound != rbound) {
                int half = (lbound + rbound + 1) / 2;
                int halfValue = larger.elementAt(half);
                if (halfValue >= singleValue) {
                    rbound = half - 1;
                } else {
                    lbound = half;
                }
            }
            if(lbound == 0 && larger.elementAt(lbound) > singleValue)
            {
                return order == 0 ? singleValue : larger.elementAt(order - 1);
            }
            return order <= lbound ? larger.elementAt(order) : order == lbound + 1 ? singleValue : larger.elementAt(order - 1);
        }
        int lmid = larger.length() / 2;
        int smid = smaller.length() / 2;
        if (larger.elementAt(lmid) >= smaller.elementAt(smid)) {
            if (order <= lmid + smid) {
                return findMedianSortedArrays(smaller,
                        larger.sub(0, lmid), order);
            } else {
                return findMedianSortedArrays(smaller.sub(smid, smaller.length()),
                        larger, order - smid);
            }
        } else {
            if (order <= lmid + smid) {
                return findMedianSortedArrays(smaller.sub(0, smid),
                        larger, order);
            } else {
                return findMedianSortedArrays(smaller,
                        larger.sub(lmid, larger.length()), order - lmid);
            }
        }
    }
}
