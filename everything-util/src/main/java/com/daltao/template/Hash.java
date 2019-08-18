package com.daltao.template;

public class Hash {
    private static final NumberTheory.Modular MOD = new NumberTheory.Modular((int) (1e9 + 7));
    private int[] inverse;
    private int[] hash;
    private int n;
    private int x;
    private int invX;

    public static interface ToHash<T> {
        int hash(T obj);
    }

    public Hash(int size, int x) {
        inverse = new int[size];
        hash = new int[size];
        this.x = x;
        this.invX = new NumberTheory.Power(MOD).inverse(x);
        inverse[0] = 1;
        for (int i = 1; i < size; i++) {
            this.inverse[i] = MOD.mul(this.inverse[i - 1], invX);
        }
    }

    public <T> void populate(T[] data, int n, ToHash<T> toHash) {
        this.n = n;
        hash[0] = toHash.hash(data[0]);
        int xn = 1;
        for (int i = 1; i < n; i++) {
            xn = MOD.mul(xn, x);
            hash[i] = MOD.plus(hash[i - 1], MOD.mul(toHash.hash(data[i]), xn));
        }
    }

    public void populate(Object[] data, int n) {
        this.n = n;
        hash[0] = data[0].hashCode();
        int xn = 1;
        for (int i = 1; i < n; i++) {
            xn = MOD.mul(xn, x);
            hash[i] = MOD.plus(hash[i - 1], MOD.mul(data[i].hashCode(), xn));
        }
    }

    public void populate(int[] data, int n) {
        this.n = n;
        hash[0] = data[0];
        int xn = 1;
        for (int i = 1; i < n; i++) {
            xn = MOD.mul(xn, x);
            hash[i] = MOD.plus(hash[i - 1], MOD.mul(data[i], xn));
        }
    }

    public void populate(char[] data, int n) {
        this.n = n;
        hash[0] = data[0];
        int xn = 1;
        for (int i = 1; i < n; i++) {
            xn = MOD.mul(xn, x);
            hash[i] = MOD.plus(hash[i - 1], MOD.mul(data[i], xn));
        }
    }

    public int partial(int l, int r) {
        int h = hash[r];
        if (l > 0) {
            h = MOD.plus(h, -hash[l - 1]);
            h = MOD.mul(h, inverse[l]);
        }
        return h;
    }
}