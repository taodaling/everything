package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dalt on 2017/6/19.
 */
public class ThreeSumClosest {
    public static void main(String[] args) {
        System.out.println(new ThreeSum().threeSum(new int[]{}));
    }

    public int threeSumClosest(int[] nums, int target) {
        if (nums.length < 3) {
            return 0;
        }
        Arrays.sort(nums);
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();
        int best = nums[0] + nums[1] + nums[2];
        int offset = Math.abs(best - target);
        for (int i = 0, bound = n - 2; i < bound; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int l = i + 1;
            int r = n - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                int dist = Math.abs(sum - target);
                if (dist < offset) {
                    best = sum;
                    offset = dist;
                }
                if (sum < target) {
                    l++;
                } else if (sum > target) {
                    r--;
                } else {
                    return target;
                }
            }
        }
        return best;
    }
}
