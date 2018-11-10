package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.TreeMap;

public class Contest87C {
    public boolean isNStraightHand(int[] hand, int W) {
        if (hand.length % W != 0) {
            return false;
        }

        Deque<Integer> deque = new ArrayDeque<>(W - 1);
        int sum = 0;
        for (int i = 0; i < W; i++) {
            deque.addLast(0);
        }

        Map<Integer, Integer> map = new TreeMap<>();
        for (int i = 0, n = hand.length; i < n; i++) {
            int cnt = map.getOrDefault(hand[i], 0);
            map.put(hand[i], cnt + 1);
        }

        int last = -1;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int head = deque.removeFirst();

            sum -= head;
            if (sum != 0 && last + 1 != entry.getKey()) {
                return false;
            }
            last = entry.getKey();

            if (sum > entry.getValue()) {
                return false;
            }


            deque.addLast(entry.getValue() - sum);
            sum = entry.getValue();
        }

        deque.removeFirst();
        while(!deque.isEmpty())
        {
            if(deque.removeFirst() != 0)
            {
                return false;
            }
        }

        return true;
    }
}
