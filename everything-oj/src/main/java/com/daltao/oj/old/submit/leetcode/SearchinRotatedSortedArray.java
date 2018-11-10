package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/11/2.
 */
public class SearchinRotatedSortedArray {
    public int search(int[] nums, int target) {
        if (nums.length == 0) {
            return -1;
        }

        OrderedList list = new OrderedList(nums, findLeastElementIndex(nums, 0, nums.length - 1));
        int left = 0;
        int right = nums.length;

        while (left < right) {
            int half = (left + right) / 2;
            int halfVal = list.get(half);
            if (target == halfVal) {
                return list.reverse(half);
            } else if (target > halfVal) {
                left = half + 1;
            } else {
                right = half;
            }
        }
        return -1;
    }

    public int findLeastElementIndex(int[] nums, int left, int right) {
        if (nums[left] <= nums[right]) {
            return left;
        }
        if (right - left == 1) {
            return right;
        }
        int half = (left + right) / 2;
        return nums[left] <= nums[half] ? findLeastElementIndex(nums, half, right) : findLeastElementIndex(nums, left, half);
    }

    public static class OrderedList {
        int leastElementOffset;
        int[] data;

        public OrderedList(int[] data, int leastElementOffset) {
            this.data = data;
            this.leastElementOffset = leastElementOffset;
        }

        public int size() {
            return data.length;
        }

        public int get(int i) {
            return data[(leastElementOffset + i) % data.length];
        }

        public int reverse(int i) {
            return (leastElementOffset + i) % data.length;
        }
    }
}
