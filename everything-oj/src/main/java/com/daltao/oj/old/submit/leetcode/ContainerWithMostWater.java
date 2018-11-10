package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2017/6/17.
 */
public class ContainerWithMostWater {
    public int maxArea(int[] height) {
        return maxArea(height, 0, height.length - 1);
    }

    private int maxArea(int[] height, int i, int j) {
        int sub;
        if (j <= i) {
            return 0;
        } else if (height[i] < height[j]) {
            sub = maxArea(height, i + 1, j);
        } else if (height[i] > height[j]) {
            sub = maxArea(height, i, j - 1);
        } else {
            sub = maxArea(height, i + 1, j - 1);
        }
        return Math.max((j - i) * Math.min(height[i], height[j]), sub);
    }
}
