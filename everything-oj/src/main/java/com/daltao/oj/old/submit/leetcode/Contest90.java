package com.daltao.oj.old.submit.leetcode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

public class Contest90 {
    public static void main(String[] args) {
        System.out.println(new Solution().mincostToHireWorkers(new int[]{37,32,14,14,23,31,82,96,81,96,22,17,68,3,88,59,54,23,22,77,61,16,46,22,94,50,29,46,7,33,22,99,31,99,75,67,95,54,31,48,44,96,99,20,51,54,18,85,25,84},
new int[]{453,236,199,359,107,45,150,433,32,192,433,94,113,200,293,31,48,27,15,32,295,97,199,427,90,215,390,412,475,131,122,398,479,142,103,243,86,309,498,210,173,363,449,135,353,397,105,165,165,62},
        20));
    }

    static class Solution {
        double PREC = 1e-5;

        public double mincostToHireWorkers(int[] quality, int[] wage, int K) {
            int n = quality.length;
            Worker[] workers = new Worker[n];
            for (int i = 0; i < n; i++) {
                workers[i] = new Worker();
                workers[i].q = quality[i];
                workers[i].w = wage[i];
                workers[i].r = (double) workers[i].w / workers[i].q;
                workers[i].id = i;
            }

            Comparator<Worker> cmp = new Comparator<Worker>() {
                public int compare(Worker a, Worker b) {
                    if (Math.abs(a.r - b.r) < 1e-8) {
                        return Integer.compare(a.q, b.q);
                    } else {
                        return Double.compare(a.r, b.r);
                    }
                }
            };
            Arrays.sort(workers, cmp);

            double min = 1e20;
            int sum = 0;
            TreeSet<Worker> set = new TreeSet(new Comparator<Worker>() {
                @Override
                public int compare(Worker o1, Worker o2) {
                    return o1.q == o2.q ? (o1.id - o2.id) : (o1.q - o2.q);
                }
            });
            for (int i = 0, until = K - 1; i < until; i++) {
                set.add(workers[i]);
                sum += workers[i].q;
            }
            for (int i = K - 1; i < n; i++) {
                min = Math.min(min, (workers[i].q + sum) * workers[i].r);
                if (K > 1) {
                    Worker last = set.last();
                    if (workers[i].q < last.q) {
                        set.remove(last);
                        set.add(workers[i]);
                        sum += workers[i].q - last.q;
                    }
                }
            }

            return min;
        }

        public static class Worker {
            int q;
            int w;
            double r;
            int id;
        }
    }
}
