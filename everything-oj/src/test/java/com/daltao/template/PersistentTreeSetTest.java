package com.daltao.template;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.TreeSet;

public class PersistentTreeSetTest {
    PersistentTreeSet<Integer> set;

    @BeforeEach
    public void init() {
        set = new PersistentTreeSet<>((Comparator<Integer>) Comparator.naturalOrder());
        set.add(1);
        set.add(2);
        set.add(5);
        set.add(3);
        set.add(4);
    }

    @Test
    public void testAdd() {
        Assertions.assertEquals(set.floor(2), (Integer) 2);
        Assertions.assertEquals(set.floor(6), (Integer) 5);
        Assertions.assertEquals(set.ceiling(-1), (Integer) 1);
        Assertions.assertEquals(set.floor(3), (Integer) 3);
        Assertions.assertEquals(set.higher(3), (Integer) 4);
        Assertions.assertEquals(set.lower(4), (Integer) 3);
        Assertions.assertEquals(set.first(), (Integer) 1);
        Assertions.assertEquals(set.last(), (Integer) 5);
    }

    @Test
    public void testContain() {
        for (int i = 1; i <= 5; i++) {
            Assertions.assertTrue(set.contain(i));
        }
        Assertions.assertFalse(set.contain(0));
        Assertions.assertFalse(set.contain(8));
        Assertions.assertTrue(set.contain(3));
        Assertions.assertFalse(set.contain(6));
    }

    @Test
    public void testDelete() {
        set.delete(3);
        Assertions.assertEquals(set.floor(3), (Integer) 2);
        Assertions.assertFalse(set.contain(3));
    }

    @Test
    public void testClone() {
        PersistentTreeSet<Integer> cl = set.clone();

        cl.delete(3);
        cl.add(6);
        Assertions.assertTrue(set.contain(3));
        Assertions.assertFalse(set.contain(6));
        Assertions.assertFalse(cl.contain(3));
        Assertions.assertTrue(cl.contain(6));
    }

    @Test
    public void testSize() {
        Assertions.assertEquals(set.size(), 5);
    }

    @Test
    public void testAddIntoPersistentTreeSet() {
        addIntoPersistentTreeSet();
    }

    @Test
    public void testAddIntoTreeSet() {
        addIntoTreeSet();
    }

    public PersistentTreeSet<Integer> addIntoPersistentTreeSet() {
        int n = 1000000;
        PersistentTreeSet<Integer> set = new PersistentTreeSet<>((Comparator<Integer>) Comparator.naturalOrder());
        for (int i = 0; i < n; i++) {
            set.add(i);
        }
        return set;
    }

    public TreeSet<Integer> addIntoTreeSet() {
        int n = 1000000;
        TreeSet<Integer> set = new TreeSet<>((Comparator<Integer>) Comparator.naturalOrder());
        for (int i = 0; i < n; i++) {
            set.add(i);
        }
        return set;
    }

    @Test
    public void deleteFromPersistentTreeSet() {
        PersistentTreeSet<Integer> set1 = addIntoPersistentTreeSet();
        TreeSet<Integer> set2 = addIntoTreeSet();

        while (set1.size() > 0) {
            set1.pollFirst();
        }
    }

    @Test
    public void deleteFromTreeSet() {
        PersistentTreeSet<Integer> set1 = addIntoPersistentTreeSet();
        TreeSet<Integer> set2 = addIntoTreeSet();

        while (set2.size() > 0) {
            set2.pollFirst();
        }
    }
}
