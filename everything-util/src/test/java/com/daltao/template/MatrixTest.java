package com.daltao.template;

import org.junit.Assert;
import org.junit.Test;

public class MatrixTest {
    public boolean near(double a, double b){
        return Math.abs(a - b) < 1e-8;
    }

    @Test
    public void testDeterminant1(){
        Matrix mat = new Matrix(2, 2);
        mat.asStandard();
        Assert.assertTrue(near(1, Matrix.determinant(mat)));
    }

    @Test
    public void testDeterminant2(){
        Matrix mat = new Matrix(2, 2);
        mat.set(0, 0, 1);
        mat.set(1,1,1);
        mat.set(0, 1, -1);
        mat.set(1, 0, -1);
        Assert.assertTrue(near(0, Matrix.determinant(mat)));
    }

    @Test
    public void testDeterminant3(){
        Matrix mat = new Matrix(2, 2);
        mat.set(0, 0, 1);
        mat.set(1,1,4);
        mat.set(0, 1, 2);
        mat.set(1, 0, 2);
        Assert.assertTrue(near(0, Matrix.determinant(mat)));
    }
}
