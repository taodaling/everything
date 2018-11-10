package com.daltao.oj.template;

/**
 * Created by dalt on 2018/5/20.
 */
public class Segment implements Cloneable {
    Segment left;
    Segment right;

    public static Segment build(int l, int r) {
        Segment segment = new Segment();
        if (l != r) {
            int m = (l + r) >> 1;
            segment.left = build(l, m);
            segment.right = build(m + 1, r);
            segment.pushUp();
        }
        return segment;
    }

    public static boolean checkOutOfRange(int ll, int rr, int l, int r)
    {
        return ll > r || rr < l;
    }
    public static boolean checkCoverage(int ll, int rr, int l, int r)
    {
        return ll <= l && rr >= r;
    }
    public static void update(int ll, int rr, int l, int r, Segment segment) {
        if (checkOutOfRange(ll, rr, l, r)) {
            return;
        }
        if (checkCoverage(ll, rr, l, r)) {
            return;
        }
        int m = (l + r) >> 1;

        segment.pushDown();
        update(ll, rr, l, m, segment.left);
        update(ll, rr, m + 1, r, segment.right);
        segment.pushUp();
    }

    public static Segment updatePersistently(int ll, int rr, int l, int r, Segment segment) {
        if (checkOutOfRange(ll, rr, l, r)) {
            return segment;
        }
        segment = segment.clone();
        if (checkCoverage(ll, rr, l, r)) {
            return segment;
        }

        int m = (l + r) >> 1;

        segment.pushDown();
        segment.left = updatePersistently(ll, rr, l, m, segment.left);
        segment.right = updatePersistently(ll, rr, m + 1, r, segment.right);
        segment.pushUp();
        return segment;
    }

    public static void query(int ll, int rr, int l, int r, Segment segment) {
        if (checkOutOfRange(ll, rr, l, r)) {
            return;
        }
        if (checkCoverage(ll, rr, l, r)) {
            return;
        }
        int m = (l + r) >> 1;

        segment.pushDown();
        query(ll, rr, l, m, segment.left);
        query(ll, rr, m + 1, r, segment.right);
    }

    public void pushDown() {
    }

    public void pushUp() {
    }

    @Override
    public Segment clone() {
        try {
            return (Segment) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
