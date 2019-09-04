package com.daltao.template;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GravityModLagrangeInterpolationTest {
    GravityModLagrangeInterpolation interpolation;

    @Before
    public void before() {
        interpolation = new GravityModLagrangeInterpolation(new NumberTheory.Modular((int) 1e9 + 7), 5);
    }

    @Test
    public void test1() {
        interpolation.addPoint(1, 1);
        interpolation.addPoint(2, 2);
        Assert.assertEquals(2, interpolation.getYByInterpolation(2));
        Assert.assertEquals(1, interpolation.getYByInterpolation(1));
        Assert.assertEquals(3, interpolation.getYByInterpolation(3));
        Assert.assertEquals(0, interpolation.getYByInterpolation(0));
    }

    @Test
    public void test2() {
        interpolation.addPoint(1, 6);
        interpolation.addPoint(0, 3);
        interpolation.addPoint(-1, 2);

        Assert.assertEquals(11, interpolation.getYByInterpolation(2));
        Assert.assertEquals(18, interpolation.getYByInterpolation(3));

        GravityModLagrangeInterpolation.Polynomial polynomial = interpolation.preparePolynomial();
        Assert.assertEquals(11, polynomial.function(2));
        Assert.assertEquals(18, polynomial.function(3));
        Assert.assertEquals(2, polynomial.getRank());
        Assert.assertEquals(1, polynomial.getCoefficient(2));
        Assert.assertEquals(2, polynomial.getCoefficient(1));
        Assert.assertEquals(3, polynomial.getCoefficient(0));
    }
}
