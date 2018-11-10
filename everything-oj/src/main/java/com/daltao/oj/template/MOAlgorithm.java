package com.daltao.oj.template;

import java.util.Arrays;
import java.util.Comparator;

public class MOAlgorithm<T> {
    public static interface Queries<T> {
        void setAnswer(Interval<T> interval);

        int getLeft();

        int getRight();
    }

    public static interface Interval<T> {
        void llMove(T add);

        void rrMove(T add);

        void lrMove(T remove);

        void rlMove(T remove);

        void clear();
    }

    public void setData(T[] data) {
        this.data = data;
    }

    public void setQueries(Queries<T>[] queries) {
        this.queries = queries.clone();
    }

    public void setInterval(Interval<T> interval) {
        this.interval = interval;
    }

    public void solve() {
        int n = data.length;
        int m = queries.length;

        if (n == 0 || m == 0) {
            return;
        }

        int k = Math.max(1, Mathematics.intRound(n / Math.sqrt(m)));

        Arrays.sort(queries, new Comparator<Queries<T>>() {
            @Override
            public int compare(Queries<T> o1, Queries<T> o2) {
                int c = o1.getLeft() / k - o2.getLeft() / k;
                if (c == 0) {
                    c = o1.getRight() - o2.getRight();
                }
                return c;
            }
        });

        interval.clear();
        int left = queries[0].getLeft();
        int right = left - 1;
        for (int i = 0; i < m; i++) {
            Queries<T> q = queries[i];
            int l = q.getLeft();
            int r = q.getRight();

            while (left > l) {
                interval.llMove(data[--left]);
            }

            while (right < r) {
                interval.rrMove(data[++right]);
            }

            while (left < l) {
                interval.lrMove(data[left++]);
            }

            while (right > r) {
                interval.rlMove(data[right--]);
            }

            q.setAnswer(interval);
        }

        return;
    }

    protected T[] data;
    protected Queries<T>[] queries;
    protected Interval<T> interval;
}
