package com.daltao.template;

import java.util.ArrayList;
import java.util.List;

public class FlowerTree {
    int hd;
    int tl;

    Node[] nodes;

    private static class Node {
        List<Node> next = new ArrayList<>(2);
        int pre;
        int mate;
        int link;
        int vis;
        Node fa = this;
        int que;
        int rank;

        Node find() {
            return fa.fa == fa ? fa : (fa = fa.find());
        }

        public static Node merge(Node a, Node b){

        }
    }

    public void addEdge(int xId, int yId) {
        nodes[xId].next.add(nodes[yId]);
        nodes[yId].next.add(nodes[xId]);
    }
}
