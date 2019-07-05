package com.daltao.template;

public class SimpsonRule {
    private final double eps;

    public SimpsonRule(double eps) {
        this.eps = eps;
    }

    public static interface Function {
        double y(double x);
    }


    public double integral(double l, double r, Function function) {
        if (r - l < eps) {
            return (r - l) / 6 * (function.y(l) + 4 * function.y((l + r) / 2) + function.y(r));
        }
        double m = (l + r) / 2;
        return integral(l, m, function) + integral(m, r, function);
    }
}