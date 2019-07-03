package com.daltao.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ModifiableMoOnTree {
    private static class Node {
        List<Node> next = new ArrayList<>(2);
        int id;
        int close;
        int open;
        int dfn;
    }

    Node[] nodes;

    public ModifiableMoOnTree(int n) {
        nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
            nodes[i].id = i;
        }
    }

    public void addEdge(int a, int b) {
        nodes[a].next.add(nodes[b]);
        nodes[b].next.add(nodes[a]);
    }


    private boolean preHandled = false;

    private void preHandle() {
        if (preHandled) {
            return;
        }
        preHandled = true;
        eulerTrace = new ArrayList<>(nodes.length * 2);
        dfs(nodes[1], null);

    }

    List<Node> eulerTrace;

    private void dfs(Node root, Node father) {
        root.open = eulerTrace.size();
        eulerTrace.add(root);
        for (Node node : root.next) {
            if (node == father) {
                continue;
            }
            dfs(node, root);
        }
        root.close = eulerTrace.size();
        eulerTrace.add(root);
    }

    public void solve(Query[] queries, Modification[] modifications, Assistant assistant, int now) {
        final int blockSize = Math.max((int) Math.pow(nodes.length, 2.0 / 3), 1);
        for (Query q : queries) {
            Node u = nodes[q.u];
            Node v = nodes[q.v];
            if (u.open > v.open) {
                Node tmp = u;
                u = v;
                v = tmp;
            }
            if (u.close <= v.open) {
                q.l = u.close;
                q.r = v.open;
            } else {
                q.l = v.close;
                q.r = u.open;
            }
        }
        Arrays.sort(queries, new Comparator<Query>() {
            @Override
            public int compare(Query a, Query b) {
                int r = a.l / blockSize - b.l / blockSize;
                if (r == 0) {
                    r = a.version / blockSize - b.version / blockSize;
                }
                return r;
            }
        });
    }

    public static class Query {
        int l;
        int r;
        int version;
        int u;
        int v;
        int answer;
    }

    public static class Modification {
        int x;
    }

    public static interface Assistant {
        void apply(int v);

        void revoke(int v);

        void add(int i);

        void remove(int i);

        int query();
    }
}
