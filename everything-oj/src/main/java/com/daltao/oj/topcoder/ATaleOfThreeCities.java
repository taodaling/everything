package com.daltao.oj.topcoder;

import java.util.Arrays;

public class ATaleOfThreeCities {
    public double connect(int[] ax, int[] ay, int[] bx, int[] by, int[] cx, int[] cy) {
        int[] d = new int[]{
                dist(ax, ay, bx, by),
                dist(ax, ay, cx, cy),
                dist(bx, by, cx, cy)
        };

        Arrays.sort(d);
        return Math.sqrt(d[0]) + Math.sqrt(d[1]);
    }

    public int dist(int[] ax, int[] ay, int[] bx, int[] by) {
        int n = ax.length;
        int m = bx.length;
        int dist = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int dx = ax[i] - bx[j];
                int dy = ay[i] - by[j];
                dist = Math.min(dist, dx * dx + dy * dy);
            }
        }
        return dist;
    }
}
