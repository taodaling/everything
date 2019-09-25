package com.daltao.oj.topcoder;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class AbsSequenceTest {
    AbsSequence absSequence = new AbsSequence();

    @Test
    public void test() {
        String a = "21";
        String b = "12";
        String[] idx = new String[]{"0", "1", "2", "3", "4"};
        String[] ans = absSequence.getElements(a, b, idx);
        Assert.assertArrayEquals(new String[]{"21", "12", "9", "3", "6"}, ans);
    }

    @Test
    public void test2() {
        String a = "823";
        String b = "470";
        String[] idx = new String[]{"3", "1", "31", "0", "8", "29", "57", "75", "8", "77"};
        String[] ans = absSequence.getElements(a, b, idx);
        Assert.assertArrayEquals(new String[]{"117", "470", "2", "823", "115", "87", "49", "25", "115", "23"}, ans);
    }

    @Test
    public void test3() {
        String a = "710370";
        String b = "177300";
        String[] idx = new String[]{"5", "95", "164721", "418", "3387", "710", "0", "1197", "19507", "5848"};
        String[] ans = absSequence.getElements(a, b, idx);
        Assert.assertArrayEquals(new String[]{"178470", "108270", "90", "0", "90", "90", "710370", "90", "0", "0"}, ans);
    }
}
