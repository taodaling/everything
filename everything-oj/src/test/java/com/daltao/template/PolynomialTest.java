package com.daltao.template;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PolynomialTest {
    public void assertNear(double a, double b) {
        Assertions.assertTrue(Math.abs(a - b) <= 1e-8);
    }

    @Test
    public void test1() {
        Polynomial p = Polynomial.newBuilder()
                .addPoint(0, 1).build();
        assertNear(p.coefficientOf(0), 1);
        assertNear(p.coefficientOf(1), 0);
        assertNear(p.y(100), 1);
    }

    @Test
    public void test2() {
        Polynomial p = Polynomial.newBuilder()
                .addPoint(0, 0).addPoint(1, 1).build();
        assertNear(p.coefficientOf(0), 0);
        assertNear(p.coefficientOf(1), 1);
        assertNear(p.coefficientOf(2), 0);
        assertNear(p.y(100), 100);
    }

    @Test
    public void test3() {
        Polynomial p = Polynomial.newBuilder()
                .addPoint(0, 0).addPoint(1, 1)
                .addPoint(2, 3).build();
        assertNear(p.y(1), 1);
        assertNear(p.y(2), 3);
        assertNear(p.y(3), 6);
        assertNear(p.y(100), 5050);
    }

    @Test
    public void test4() {
        Polynomial p = Polynomial.newBuilder()
                .addPoint(0, 0).addPoint(1, 1)
                .addPoint(2, 5).addPoint(3, 14).build();
        System.out.println(p);
    }

    @Test
    public void test5() {
        Polynomial p = Polynomial.newBuilder()
                .addPoint(0, 0).addPoint(1, 0)
                .addPoint(2, 2).addPoint(3, 8).build();
        assertNear(p.y(1), 0);
        assertNear(p.y(4), 20);
        assertNear(p.y(5), 40);
        assertNear(p.y(6), 70);
        System.out.println(p);
    }
}
