package com.daltao.template;

public class SegmentCandidate implements Cloneable {
    private SegmentCandidate left;
    private SegmentCandidate right;

    public void pushUp() {
    }

    public void pushDown() {
    }

    public SegmentCandidate(int l, int r) {
        if (l < r) {
            int m = (l + r) >> 1;
            left = new SegmentCandidate(l, m);
            right = new SegmentCandidate(m + 1, r);
            pushUp();
        } else {

        }
    }

    private boolean covered(int ll, int rr, int l, int r) {
        return ll <= l && rr >= r;
    }

    private boolean noIntersection(int ll, int rr, int l, int r) {
        return ll > r || rr < l;
    }

    public void update(int ll, int rr, int l, int r) {
        if (noIntersection(ll, rr, l, r)) {
            return;
        }
        if (covered(ll, rr, l, r)) {
            return;
        }
        pushDown();
        int m = (l + r) >> 1;
        left.update(ll, rr, l, m);
        right.update(ll, rr, m + 1, r);
        pushUp();
    }

    public void query(int ll, int rr, int l, int r) {
        if (noIntersection(ll, rr, l, r)) {
            return;
        }
        if (covered(ll, rr, l, r)) {
            return;
        }
        pushDown();
        int m = (l + r) >> 1;
        left.query(ll, rr, l, m);
        right.query(ll, rr, m + 1, r);
    }
}
