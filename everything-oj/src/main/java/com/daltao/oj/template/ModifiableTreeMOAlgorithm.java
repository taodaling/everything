package com.daltao.oj.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ModifiableTreeMOAlgorithm<T> {
    public static class Node<T> {
        List<Node> children = new ArrayList(1);
        int dfn1;
        int dfn2;
        int order;
        int id;
        T value;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    public static interface OutsideQuery {
        public int getU();

        public int getV();

        public void setAnswer(Interval interval);

        public int getVersion();
    }

    public static interface Interval<T> {
        void add(T val);

        void remove(T val);
    }

    public static interface Modification<T> {
        int index();

        T invoke();

        T revoke();
    }

    public static class InnerQuery<T> {
        OutsideQuery outsideQuery;
        int extra = -1;
        int left;
        int right;

        public void setAnswer(Interval interval) {
            outsideQuery.setAnswer(interval);
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }

        public int getExtra() {
            return extra;
        }

        public int getVersion() {
            return outsideQuery.getVersion();
        }
    }

    Node[] nodes;
    int nodeNum;
    St<Node<T>> st;

    public ModifiableTreeMOAlgorithm(int nodeNum) {
        this.nodeNum = nodeNum;
        nodes = new Node[nodeNum + 1];
        for (int i = 1; i <= nodeNum; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
        in = new boolean[nodeNum + 1];
    }

    public void buildEdge(int u, int v) {
        nodes[u].children.add(nodes[v]);
        nodes[v].children.add(nodes[u]);
    }

    public void setNodeValue(int i, T val) {
        nodes[i].value = val;
    }

    Object[] values;
    int[] sequence;

    private static <T> void flat(Node<T> root, Node<T> father, List<T> list, List<Node<T>> trace) {
        root.dfn1 = list.size();
        list.add(root.value);
        root.order = trace.size();
        trace.add(root);
        for (Node child : root.children) {
            if (child == father) {
                continue;
            }
            flat(child, root, list, trace);
            trace.add(root);
        }
        root.dfn2 = list.size();
        list.add(root.value);
    }

    public void buildFinish() {
        List<T> list = new ArrayList(nodeNum * 2);
        List<Node<T>> trace = new ArrayList(nodeNum * 2);
        flat(nodes[1], null, list, trace);
        values = list.toArray();
        sequence = new int[nodeNum * 2];
        for (int i = 1; i <= nodeNum; i++) {
            sequence[nodes[i].dfn1] = sequence[nodes[i].dfn2] = nodes[i].id;
        }
        st = new St(trace.toArray(), trace.size(), new Comparator<Node<T>>() {
            @Override
            public int compare(Node<T> o1, Node<T> o2) {
                return o1.order - o2.order;
            }
        });
    }

    boolean[] in;
    Interval<T> interval;

    public void solve(OutsideQuery[] outsideQueries, Interval<T> interval, Modification<T>[] modifications) {
        int n = values.length;
        int q = outsideQueries.length;
        int m = modifications.length;
        if (nodeNum == 0 || q == 0) {
            return;
        }
        if (m == 0) {
            m = 1;
        }
        this.interval = interval;

        InnerQuery<T>[] innerQueries = new InnerQuery[m];

        for (int i = 0, until = values.length; i < until; i++) {
            nodes[sequence[i]].value = values[i];
        }

        for (int i = 0; i < m; i++) {
            innerQueries[i] = new InnerQuery();
            innerQueries[i].outsideQuery = outsideQueries[i];

            Node u = nodes[outsideQueries[i].getU()];
            Node v = nodes[outsideQueries[i].getV()];
            if (u.dfn1 > v.dfn1) {
                Node tmp = u;
                u = v;
                v = tmp;
            }
            //u is lca(u,v)
            if (u.dfn2 >= v.dfn1) {
                innerQueries[i].left = v.dfn2;
                innerQueries[i].right = u.dfn2;
            } else {
                innerQueries[i].left = u.dfn2;
                innerQueries[i].right = v.dfn1;
                innerQueries[i].extra = st.query(u.order, v.order).dfn1;
            }
        }


        final int k = Math.max(1, Mathematics.intRound(Math.pow(1.0d / q * n * n * m, 1.0 / 3)));

        Arrays.sort(innerQueries, new Comparator<InnerQuery<T>>() {
            @Override
            public int compare(InnerQuery<T> o1, InnerQuery<T> o2) {
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

        int left = innerQueries[0].getLeft();
        int right = left - 1;
        Arrays.fill(in, false);
        int version = 0;
        Object[] valuesClone = values.clone();

        for (int i = 0; i < m; i++) {
            InnerQuery<T> query = innerQueries[i];
            int l = query.getLeft();
            int r = query.getRight();
            int v = query.getVersion();

            while (version < v) {
                Modification<T> modification = modifications[version++];
                int index = modification.index();
                Node node = nodes[index];
                values[node.dfn1] = modification.invoke();
                values[node.dfn2] = modification.invoke();
                if (in[node.id]) {
                    interval.remove((T) modification.revoke());
                    interval.add((T) modification.invoke());
                }
            }
            while (version > v) {
                Modification<T> modification = modifications[--version];
                int index = modification.index();
                Node node = nodes[index];
                values[node.dfn1] = modification.revoke();
                values[node.dfn2] = modification.revoke();
                if (in[node.id]) {
                    interval.remove((T) modification.invoke());
                    interval.add((T) modification.revoke());
                }
            }

            while (left > l) {
                add(--left);
            }

            while (right < r) {
                add(++right);
            }

            while (left < l) {
                remove(left++);
            }

            while (right > r) {
                remove(right--);
            }

            if (query.extra != -1) {
                interval.add((T) values[query.extra]);
                query.setAnswer(interval);
                interval.remove((T) values[query.extra]);
            } else {
                query.setAnswer(interval);
            }
        }

        values = valuesClone;
        return;
    }

    public void add(int i) {
        int j = sequence[i];
        if (in[j]) {
            interval.remove((T) values[i]);
        } else {
            interval.add((T) values[i]);
        }
        in[j] = !in[j];
    }

    public void remove(int i) {
        add(i);
    }
}