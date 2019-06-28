package com.daltao.template;


import java.util.ArrayList;
import java.util.List;

public class MinTreeGraph {
    public static class Edge {
        Node src;
        Node dst;
        int weight;
    }

    public static class INode {
        public
    }

    public static class Node {
        int id;
        List<Edge> edges = new ArrayList<>(2);
    }



    public static class VirtualNode {
        Node inner;
        List<>
    }

    private Node[] nodes;
    private boolean hasSolution;
    private long minWeightSum;

    public MinTreeGraph(int n) {
        nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node();
        }
    }

    public void solve(int root) {

    }
}
