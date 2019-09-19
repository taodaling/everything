package com.daltao.oj.topcoder;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AToughGameTest {
    AToughGame game;

    @BeforeEach
    public void before() {
        game = new AToughGame();
    }

    public void near(double a, double b) {
        Assert.assertTrue(Math.abs(a - b) < 1e-6);
    }

    @Test
    public void test1() {
        int[] prob = new int[]{1000, 1};
        int[] values = new int[]{3, 4};
        double ans = game.expectedGain(prob, values);
        near(ans, 3003.9999999999977D);
    }

    @Test
    public void test2() {
        int[] prob = new int[]{500,500,500,500,500};
        int[] values = new int[]{1,2,3,4,5};
        double ans = game.expectedGain(prob, values);
        near(ans, 16.626830517153095);
    }
}
