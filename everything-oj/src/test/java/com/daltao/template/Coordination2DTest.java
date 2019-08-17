package com.daltao.template;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Coordination2DTest {
    Matrix point;
    Matrix target;

    @BeforeEach
    public void initPoint() {
        point = new Matrix(3, 1);
        point.set(2, 0, 1);
        target = new Matrix(3, 1);
        target.set(2, 0, 1);
    }

    public static void assertNear(double x, double y) {
        Assertions.assertTrue(Math.abs(x - y) < 1e-8);
    }

    @Test
    public void testOrigin() {
        Coordination2D.ofOrigin(1, 1).toCurrentCoordination(point, target);
        assertNear(target.get(0, 0), -1);
        assertNear(target.get(1, 0), -1);

        Coordination2D.ofOrigin(1, 1).toNormalCoordination(target, point);
        assertNear(point.get(0, 0), 0);
        assertNear(point.get(1, 0), 0);
    }

    @Test
    public void testXAxis() {
        point.set(0, 0, 1);
        point.set(1, 0, 1);
        Coordination2D.ofXAxis(0, 1).toCurrentCoordination(point, target);
        assertNear(target.get(0, 0), 1);
        assertNear(target.get(1, 0), -1);
        Coordination2D.ofXAxis(0, 1).toNormalCoordination(target, point);
        assertNear(point.get(0, 0), 1);
        assertNear(point.get(1, 0), 1);
    }

    @Test
    public void testMerge() {
        Coordination2D.merge(Coordination2D.ofXAxis(0, 1), Coordination2D.ofOrigin(0, 1))
                .toCurrentCoordination(point, target);
        assertNear(target.get(0, 0), -1);
        assertNear(target.get(1, 0), 0);
        Coordination2D.merge(Coordination2D.ofXAxis(0, 1), Coordination2D.ofOrigin(0, 1))
                .toNormalCoordination(target, point);
        assertNear(point.get(0, 0), 0);
        assertNear(point.get(1, 0), 0);
    }

}
