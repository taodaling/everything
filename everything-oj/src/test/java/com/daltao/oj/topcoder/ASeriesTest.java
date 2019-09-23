package com.daltao.oj.topcoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

;

public class ASeriesTest {
    ASeries aSeries;

    @Before
    public void before() {
        aSeries = new ASeries();
    }

    @Test
    public void test1() {
        Assert.assertEquals(5, aSeries.longest(new int[]{3, 8, 4, 5, 6, 2, 2}));
        Assert.assertEquals(3, aSeries.longest(new int[]{-1, -5, 1, 3}));
    }
}
