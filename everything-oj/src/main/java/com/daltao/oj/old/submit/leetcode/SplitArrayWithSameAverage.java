package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by dalt on 2018/5/20.
 */
public class SplitArrayWithSameAverage {
    public static Random random;

    public boolean splitArraySameAverage(int[] A) {
        random = new Random(19950823);

        int sum = 0;
        int n = A.length;

        if (n <= 1) {
            return false;
        }

        if (n == 2) {
            return A[0] == A[1];
        }

        for (int i = 0; i < n; i++) {
            A[i] *= n;
            sum += A[i];
        }

        int avg = sum / n;
        int max = 0;
        for (int i = 0; i < n; i++) {
            A[i] -= avg;
            max = Math.max(max, Math.abs(A[i]));
        }


        if (max == 0) {
            return true;
        }


        boolean[] exists = new boolean[n];
        for (int i = 0; i < 50; i++) {
            if (sa(A, n, max, exists)) {
                return true;
            }
        }

        return false;
    }

    public static boolean sa(int[] A, int n, int max, boolean[] exists) {
        Arrays.fill(exists, false);
        int sum = 0;
        int cnt = 0;

        //get at least one
        int rand = random.nextInt(n);
        exists[rand] = true;
        sum += A[rand];
        cnt += 1;
        double now = evaluate(sum, max);

        double T = 1e3;
        double k = 1e-1;
        double r = 0.97;
        double threshold = 1e-1;
        while (T > threshold && sum != 0) {
            rand = random.nextInt(n);
            boolean changed = false;

            if (exists[rand]) {
                if (cnt > 1) {
                    cnt--;
                    sum -= A[rand];
                    exists[rand] = false;
                    changed = true;
                }
            } else {
                if (cnt < n - 1) {
                    cnt++;
                    sum += A[rand];
                    exists[rand] = true;
                    changed = false;
                }
            }

            if (!changed) {
                continue;
            }


            double newState = evaluate(sum, max);
            if (newState >= now ||
                    random.nextDouble() < Math.exp((newState - now) / (k * T))) {
                now = newState;
            } else {
                //Undo
                if (exists[rand]) {
                    exists[rand] = false;
                    sum -= A[rand];
                    cnt -= 1;
                } else {
                    exists[rand] = true;
                    sum += A[rand];
                    cnt += 1;
                }
            }


            T *= r;
        }

        return sum == 0;
    }


    public static double evaluate(int sum, int max) {
        return -Math.abs(sum) / (double) max;
    }
}
