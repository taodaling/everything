package com.daltao.oj.old.submit.leetcode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

public class Contest89C {
    public static class ExamRoom {
        TreeSet<int[]> set = new TreeSet<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] != o2[0] ? (o2[0] - o1[0]) : (o1[1] - o2[1]);
            }
        });
        TreeSet<Integer> pos = new TreeSet<>();
        HashMap<Integer, int[]> last = new HashMap<>();
        HashMap<Integer, int[]> next = new HashMap<>();
        int n;

        public ExamRoom(int N) {
            n = N;
            zero();
        }

        public void zero() {
            set.clear();
            set.add(new int[]{n, 0});
        }

        public int seat() {
            int[] s = set.first();
            set.remove(s);

            int  x = s[1];
            Integer former = pos.lower(x);
            Integer later = pos.ceiling(x);

            if (former == null) {
                int[] seat = new int[]{0, x};
                set.add(seat);
                last.put(x, seat);
            } else  {
                int centre = (former + x) / 2;
                int[] seat = new int[]{centre - former, centre};
                set.add(seat);
                last.put(x, seat);
                next.put(former, seat);
            }

            if (later == null) {
                int[] seat = new int[]{n - 1 - x, n - 1};
                set.add(seat);
                next.put(x, seat);
            } else {
                int centre = (later + x) / 2;
                int[] seat = new int[]{centre - x, centre};
                set.add(seat);
                last.put(later, seat);
                next.put(x, seat);
            }

            pos.add(x);

            return s[1];
        }

        public void leave(int p) {
            pos.remove(p);

            Integer former = pos.lower(p);
            Integer later = pos.ceiling(p);

            if (former != null && later != null) {
                int centre = (former + later) / 2;
                int[] seat = new int[]{centre - former, centre};
                set.add(seat);
                last.put(later, seat);
                next.put(former, seat);
            } else if (former != null) {
                int centre = n - 1;
                int[] seat = new int[]{centre - former, centre};
                set.add(seat);
                next.put(former, seat);
            } else if (later != null) {
                int centre = 0;
                int[] seat = new int[]{later - centre, centre};
                set.add(seat);
                last.put(later, seat);
            } else {
                zero();
            }

            set.remove(last.get(p));
            set.remove(next.get(p));
        }
    }

}
