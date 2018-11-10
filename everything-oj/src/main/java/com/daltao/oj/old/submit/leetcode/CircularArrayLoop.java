package com.daltao.oj.old.submit.leetcode;

/**
 * Created by dalt on 2018/5/29.
 */
public class CircularArrayLoop {
    public static void main(String[] args) {
        System.out.println(new CircularArrayLoop(
        ).circularArrayLoop(new int[]{2, -1, 1, 2, 2}));
        System.out.println(new CircularArrayLoop(
        ).circularArrayLoop(new int[]{2, -1, 1, 2, 2}));
        System.out.println(new CircularArrayLoop(
        ).circularArrayLoop(new int[]{-1, 2}));
    }

    public boolean circularArrayLoop(int[] nums) {
        int n = nums.length;
        for (long i = 0; i < n; i++) {
            if (nums[(int) i] < 0) {
                continue;
            }
            int next = mod(i + nums[(int) i], n);
            if (next <= i) {
                continue;
            }
            while (true) {
                if (nums[next] < 0) {
                    break;
                }
                int next2 = mod(next + nums[next], n);
                if (next2 == next) {
                    nums[next] = mod(i - next, n);
                    break;
                }
                if (next2 < i) {
                    break;
                }
                if (next2 == i) {
                    return true;
                }
                nums[next] = mod(i - next, n);
                next = next2;
            }
        }
        for (long i = 0; i < n; i++) {
            if (nums[(int) i] > 0) {
                continue;
            }
            int next = mod(i + nums[(int) i], n);
            if (next <= i) {
                continue;
            }
            while (true) {
                if (nums[next] > 0) {
                    break;
                }
                int next2 = mod(next + nums[next], n);
                if (next2 == next) {
                    nums[next] = mod(i - next, n) - n;
                    break;
                }
                if (next2 < i) {
                    break;
                }
                if (next2 == i) {
                    return true;
                }
                nums[next] = mod(i - next, n) - n;
                next = next2;
            }
        }
        return false;
    }

    public static int mod(long n, int mod) {
        return (int) (((n % mod) + mod) % mod);
    }
}
