package com.daltao.oj.old.submit.leetcode;

/**
 * Created by Administrator on 2017/6/22.
 */
public class NextPermutation {
    public void swap(int[] nums, int i, int j)
    {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }
    public void reverse(int[] nums, int from, int to)
    {
        for(int i = from, j = to - 1; i < j; i++, j--)
        {
            swap(nums, i, j);
        }
    }
    public void nextPermutation(int[] nums) {
        for(int i = nums.length - 1; i > 0; i--)
        {
            if(nums[i] > nums[i - 1])
            {
                int j;
                for(j = nums.length - 1; j > i; j--)
                {
                    if(nums[j] > nums[i - 1])
                    {
                        break;
                    }
                }
                int t = nums[j];
                while(j > i)
                {
                    nums[j] = nums[j - 1];
                    j--;
                }
                reverse(nums, i + 1, nums.length);
                nums[i] = t;
                swap(nums, i - 1, i);
                return;
            }
        }
        reverse(nums, 0, nums.length);
    }
}
