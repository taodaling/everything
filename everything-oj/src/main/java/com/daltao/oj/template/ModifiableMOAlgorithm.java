package com.daltao.oj.template;

import java.util.Arrays;
import java.util.Comparator;

public class ModifiableMOAlgorithm<T> {
    public static interface VersionQueries<T> {
        void setAnswer(Interval<T> interval);

        int getLeft();

        int getRight();

        int getVersion();
    }

    public static interface Modification<T> {
        public int index();

        public T invoke();

        public T revoke();
    }

    public static interface Interval<T> {
        void add(T add);

        void remove(T remove);

        void clear();
    }

    public void setData(T[] data) {
        this.data = data;
    }

    public void setQueries(VersionQueries<T>[] queries) {
        this.queries = queries.clone();
    }

    public void setInterval(Interval<T> interval) {
        this.interval = interval;
    }

    public void solve() {
        T[] clone = data.clone();

        int n = data.length;
        int q = queries.length;
        int m = modifications.length;

        if (n == 0 || q == 0) {
            return;
        }
        if (m == 0) {
            m = 1;
        }

        int k = Math.max(1, Mathematics.intRound(Math.pow(1.0d / q * n * n * m, 1.0 / 3)));

        Arrays.sort(queries, new Comparator<VersionQueries<T>>() {
            @Override
            public int compare(VersionQueries<T> o1, VersionQueries<T> o2) {
                int c = o1.getLeft() / k - o2.getLeft() / k;
                if (c == 0) {
                    c = o1.getVersion() / k - o2.getVersion() / k;
                }
                if (c == 0) {
                    c = o1.getRight() - o2.getRight();
                }
                return c;
            }
        });

        interval.clear();
        int left = queries[0].getLeft();
        int right = left - 1;
        int version = 0;
        for (int i = 0; i < q; i++) {
            VersionQueries<T> query = queries[i];
            int l = query.getLeft();
            int r = query.getRight();
            int v = query.getVersion();

            while (left > l) {
                interval.add(data[--left]);
            }

            while (right < r) {
                interval.add(data[++right]);
            }

            while (left < l) {
                interval.remove(data[left++]);
            }

            while (right > r) {
                interval.remove(data[right--]);
            }

            while (version < v) {
                Modification<T> modification = modifications[version++];
                int index = modification.index();
                data[index] = modification.invoke();
                if (index >= l && index <= r) {
                    interval.remove(modification.revoke());
                    interval.add(data[index]);
                }
            }

            while (version > v) {
                Modification<T> modification = modifications[--version];
                int index = modification.index();
                data[index] = modification.revoke();
                if (index >= l && index <= r) {
                    interval.remove(modification.invoke());
                    interval.add(data[index]);
                }
            }



            query.setAnswer(interval);
        }
    }

    public void setModifications(Modification<T>[] modifications) {
        this.modifications = modifications;
    }

    T[] data;
    VersionQueries<T>[] queries;
    Interval<T> interval;
    Modification<T>[] modifications;
}
