package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by dalt on 2017/6/19.
 */
public class FourSum {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        if (nums.length < 3) {
            return Collections.emptyList();
        }
        Arrays.sort(nums);
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0, iBound = n - 3; i < iBound; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            for (int j = i + 1, jBound = n - 2, jinit = i + 1; j < jBound; j++) {
                if (j > jinit && nums[j] == nums[j - 1]) {
                    continue;
                }
                int l = j + 1;
                int r = n - 1;
                int relatedTarget = target - nums[i] - nums[j];
                while (l < r) {
                    int sum = nums[l] + nums[r];
                    if (sum < relatedTarget) {
                        l++;
                    } else if (sum > relatedTarget) {
                        r--;
                    } else {
                        result.add(Arrays.asList(nums[i], nums[j], nums[l], nums[r]));
                        for (l = l + 1; l < r && nums[l] == nums[l - 1]; l++) ;
                        for (r = r - 1; l < r && nums[r] == nums[r + 1]; r--) ;
                    }
                }
            }
        }
        return result;
    }
}
