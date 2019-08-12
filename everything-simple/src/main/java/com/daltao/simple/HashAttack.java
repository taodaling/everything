package com.daltao.simple;

import java.util.HashSet;
import java.util.Set;

public class HashAttack {
    public static void main(String[] args) {
        int n = 1000000;
        Set<Item> set = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            set.add(new Item(i));
        }
    }

    public static class Item implements Comparable<Item> {
        final int val;

        public Item(int val) {
            this.val = val;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return val == ((Item) obj).val;
        }

        @Override
        public int compareTo(Item o) {
            return val - o.val;
        }
    }
}
