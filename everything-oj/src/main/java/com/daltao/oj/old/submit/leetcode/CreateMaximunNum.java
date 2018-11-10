package com.daltao.oj.old.submit.leetcode;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

/**
 * Created by dalt on 2018/6/1.
 */
public class CreateMaximunNum {
    public int[] maxNumber(int[] a, int[] b, int k) {
        int n = a.length;
        int m = b.length;

        MinQueue<Integer> minQueue = new MinQueue<Integer>(Math.max(n, m), new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        int[][] dpA = new int[k + 1][k];
        int[][] dpB = new int[k + 1][k];
        for (int i = 0; i <= k; i++) {
            minQueue.clear();

            if (n < i) {
                dpA[i] = null;
                continue;
            }

            int begin = n - i;
            for (int j = 0; j < begin; j++) {
                minQueue.enqueue(a[j]);
            }

            for (int j = begin; j < n; j++) {
                minQueue.enqueue(a[j]);
                int max = minQueue.query();
                while (minQueue.deque() != max) {
                }

                dpA[i][j - begin] = max;
            }
        }
        for (int i = 0; i <= k; i++) {
            minQueue.clear();

            if (m < i) {
                dpB[i] = null;
                continue;
            }

            int begin = m - i;
            for (int j = 0; j < begin; j++) {
                minQueue.enqueue(b[j]);
            }

            for (int j = begin; j < m; j++) {
                minQueue.enqueue(b[j]);
                int max = minQueue.query();
                while (minQueue.deque() != max) {
                }

                dpB[i][j - begin] = max;
            }
        }

        int[] max = new int[k];
        for (int i = 0; i <= k; i++) {
            int j = k - i;
            if (dpA[i] == null || dpB[j] == null) {
                continue;
            }
            max = max(max, new ConcatIterator(dpA[i], dpB[j], i, j).toArray(), k);
        }


        return max;
    }

    public static int[] max(int[] a, int[] b, int k) {
        for (int i = 0; i < k; i++) {
            if (a[i] < b[i]) {
                return b;
            } else if (a[i] > b[i]) {
                return a;
            }
        }

        return a;
    }

    public static class ConcatIterator {
        int[] a;
        int[] b;
        int alen;
        int blen;
        int ai;
        int bi;

        public ConcatIterator(int[] a, int[] b, int alen, int blen) {
            this.a = a;
            this.b = b;
            this.alen = alen;
            this.blen = blen;
        }

        public void rewind() {
            ai = bi = 0;
        }

        public int[] toArray() {
            int[] data = new int[alen + blen];
            for (int i = 0, until = data.length; i < until; i++) {
                data[i] = next();
            }
            return data;
        }

        public int next() {
            int aStep = ai;
            int bStep = bi;
            for (; aStep < alen && bStep < blen; aStep++, bStep++) {
                if (a[aStep] < b[bStep]) {
                    return b[bi++];
                } else if (a[aStep] > b[bStep]) {
                    return a[ai++];
                }
            }
            if (aStep < alen) {
                return a[ai++];
            } else {
                return b[bi++];
            }
        }
    }

    public static class MinQueue<T> {
        Deque<T> data;
        Deque<T> increasing;
        Comparator<T> comparator;

        public MinQueue(int cap, Comparator<T> comparator) {
            data = new ArrayDeque<>(cap);
            increasing = new ArrayDeque<>(cap);
            this.comparator = comparator;
        }

        public void enqueue(T x) {
            while (!increasing.isEmpty() && comparator.compare(x, increasing.peekLast()) < 0) {
                increasing.removeLast();
            }
            increasing.addLast(x);
            data.addLast(x);
        }

        public T deque() {
            T head = data.removeFirst();
            if (increasing.peekFirst() == head) {
                increasing.removeFirst();
            }
            return head;
        }

        public void clear() {
            data.clear();
            increasing.clear();
        }

        public T query() {
            return increasing.peekFirst();
        }
    }
}
