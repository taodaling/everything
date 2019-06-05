package com.daltao.template;

public class ArrayIndex {
    int[] dimensions;

    public ArrayIndex(int... dimensions) {
        this.dimensions = dimensions;
    }

    public int indexOf(int a, int b) {
        return a * dimensions[1] + b;
    }

    public int indexOf(int a, int b, int c) {
        return indexOf(a, b) * dimensions[2] + c;
    }

    public int indexOf(int a, int b, int c, int d) {
        return indexOf(a, b, c) * dimensions[3] + d;
    }

    public int indexOf(int a, int b, int c, int d, int e) {
        return indexOf(a, b, c, d) * dimensions[4] + e;
    }
}