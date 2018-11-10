package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Contest87D {
    static final int POS = 0;
    static final int STATUS = 1;
    static final int DISTANCE = 2;

    public int shortestPathLength(int[][] graph) {
        int n = graph.length;

        if (n <= 1) {
            return 0;
        }

        int[][] dp = new int[n][1 << n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dp[i], (int) 1e8);
        }

        int m = (int) 1e8;
        int mask = (1 << n) - 1;
        Deque<int[]> deque = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            deque.add(new int[]{i, 1 << i, 0});
            dp[i][1 << i] = 0;
        }

        while (!deque.isEmpty()) {
            int[] head = deque.removeFirst();

            for (int next : graph[head[POS]]) {
                int status = head[STATUS] | (1 << next);
                int distance = head[DISTANCE] + 1;
                if (dp[next][status] <= distance) {
                    continue;
                }

                dp[next][status] = distance;
                deque.add(new int[]{next, status, distance});
            }
        }

        for (int i = 0; i < n; i++) {
            m = Math.min(m, dp[i][mask]);
        }

        return m;
    }
}
