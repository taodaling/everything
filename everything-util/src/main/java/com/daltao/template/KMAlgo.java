package com.daltao.template;

import java.util.ArrayList;
import java.util.List;

public class KMAlgo {
    public static class Node {
        List<Node> nodes = new ArrayList<>();
        int visited;
        Node partner;
        int id;

        @Override
        public String toString() {
            return "" + id;
        }
    }

    Node[] leftSides;
    Node[] rightSides;
    int version;

    public KMAlgo(int l, int r) {
        leftSides = new Node[l + 1];
        for (int i = 1; i <= l; i++) {
            leftSides[i] = new Node();
            leftSides[i].id = i;
        }
        rightSides = new Node[r + 1];
        for (int i = 1; i <= r; i++) {
            rightSides[i] = new Node();
            rightSides[i].id = i;
        }
    }

    public void addEdge(int lId, int rId) {
        leftSides[lId].nodes.add(rightSides[rId]);
        rightSides[rId].nodes.add(leftSides[lId]);
    }

    private void init() {
        version++;
    }

    /**
     * Determine can we find a partner for a left node to enhance the matching degree.
     */
    public boolean matchLeft(int id) {
        if (leftSides[id].partner != null) {
            return false;
        }
        init();
        return findPartner(leftSides[id]);
    }

    /**
     * Determine can we find a partner for a right node to enhance the matching degree.
     */
    public boolean matchRight(int id) {
        if (rightSides[id].partner != null) {
            return false;
        }
        init();
        return findPartner(rightSides[id]);
    }

    private boolean findPartner(Node src) {
        if (src.visited != version) {
            return false;
        }
        src.visited = version;
        for (Node node : src.nodes) {
            if (!tryRelease(node)) {
                continue;
            }
            node.partner = src;
            src.partner = node;
            return true;
        }
        return false;
    }

    private boolean tryRelease(Node src) {
        if (src.visited != version) {
            return false;
        }
        src.visited = version;
        if (src.partner == null) {
            return true;
        }
        if (findPartner(src.partner)) {
            src.partner = null;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < leftSides.length; i++) {
            if (leftSides[i].partner == null) {
                continue;
            }
            builder.append(leftSides[i].id).append(" - ").append(leftSides[i].partner.id).append(" || ");
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 4);
        }
        return builder.toString();
    }
}