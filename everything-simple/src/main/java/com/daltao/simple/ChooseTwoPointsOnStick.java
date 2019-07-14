package com.daltao.simple;

import java.util.Arrays;
import java.util.Random;

public class ChooseTwoPointsOnStick {
    public static void main(String[] args) {
        Random random = new Random();
        int n = 100000000;
        int cnt = 0;
        for (int i = 0; i < n; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x > y) {
                double t = x;
                x = y;
                y = t;
            }
            double[] arr = new double[]{
                    x, 1 - y, y - x
            };
            Arrays.sort(arr);
            if (arr[2] < arr[0] + arr[1]) {
                cnt++;
            }
        }

        System.out.println((double) cnt / n);
    }
}
