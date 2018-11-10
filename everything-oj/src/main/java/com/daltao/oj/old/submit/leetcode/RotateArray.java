package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2018/6/1.
 */
public class RotateArray {
    public static void main(String[] args) {
        RotateArray solution = new RotateArray();
        solution.rotate(new int[]{1, 2, 3, 4, 5, 6, 7}, 3);
    }

    public static int gcd(int a, int b) {
        return a > b ? gcd0(a, b) : gcd0(b, a);
    }

    public static int gcd0(int a, int b) {
        return b == 0 ? a : gcd0(b, a % b);
    }

    public void rotate(int[] nums, int k) {
        if (nums.length == 0) {
            return;
        }

        int n = nums.length;
        k %= n;

        int cap = gcd(k, n);

        for (int i = 0; i < cap; i++) {
            int val = nums[i];
            for (int j = (i + k) % n; j != i; j = (j + k) % n) {
                int tmp = nums[j];
                nums[j] = val;
                val = tmp;
            }
            nums[i] = val;
        }
    }
}
