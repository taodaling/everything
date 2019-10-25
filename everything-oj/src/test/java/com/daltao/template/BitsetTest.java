package com.daltao.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

public class BitsetTest {
    Bitset bs;
    BitSet bitSet;

    int limit = 10000000;
    @BeforeEach
    public void before() {
        bs = new Bitset(limit);
        bitSet = new BitSet(limit);
        for(int i = 0; i < limit; i++){
            bs.set(i, i % 2 == 0);
            bitSet.set(i, i % 2 == 0);
        }
    }

    @Test
    public void bsSetSpeed(){
        for(int i = 0; i < limit; i++){
            bs.set(i, true);
        }
    }

    @Test
    public void bitSetSetSpeed(){
        for(int i = 0; i < limit; i++){
            bitSet.set(i, true);
        }
    }

    @Test
    public void bsGetSpeed(){
        for(int i = 0; i < limit; i++){
            bs.get(i);
        }
    }

    @Test
    public void bitGetSetSpeed(){
        for(int i = 0; i < limit; i++){
            bitSet.get(i);
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
