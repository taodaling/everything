package com.daltao.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BitsetTest {
    Bitset bs;

    @BeforeEach
    public void before() {
        bs = new Bitset(20);
        for (int i = 0; i < 20; i += 2) {
            bs.set(i, true);
        }
    }

    @Test
    public void test() {
        Assertions.assertTrue(bs.leftShiftView(4).get(0));
        Assertions.assertFalse(bs.leftShiftView(1).get(0));
        Assertions.assertTrue(bs.leftShiftView(3).get(1));
        Assertions.assertFalse(bs.rightShiftView(2).get(3));
    }

    @Test
    public void test2() {
        Assertions.assertTrue(Bitset.intersect(bs, bs));
        Assertions.assertTrue(Bitset.intersect(bs.leftShiftView(2), bs.leftShiftView(18)));
        Assertions.assertTrue(Bitset.intersect(bs.leftShiftView(1), bs.leftShiftView(3)));
        Assertions.assertFalse(Bitset.intersect(bs.leftShiftView(1), bs.leftShiftView(4)));
        Assertions.assertFalse(Bitset.intersect(bs.leftShiftView(4), bs.rightShiftView(18)));
    }
}
