/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc;

interface Foo {
    double op(double x);
}

class NewtonApproximation {
    static void solve(Foo func, Foo dFunc, int maxIterations, double initialX) {
        double x = initialX;
        int left = maxIterations;
        for (; left > 0; left--) {
            final double y = func.op(x);
            if (Math.abs(y) < 1E-7) {
                System.out.println("the solution is: " + x);
                break;
            }
            x = x + y / (-1.0 * dFunc.op(x));
        }
        if (left == 0) {
            System.out.printf("No solution found in %d iterations with initialX = %.2f%n", maxIterations, initialX);
        }
    }
    public static void main(String[] args) {
        System.out.println("To solve cos(x) = x");
        NewtonApproximation.solve((x) -> Math.cos(x) - x, (x) -> -Math.sin(x) - 1, 200, 1.0);

        System.out.println("To solve e^(-x) = x");
        NewtonApproximation.solve((x) -> Math.exp(-x) - x, (x) -> -Math.exp(-x) - 1, 200, 1.0);

        System.out.println("To solve x^2 - 2 = 0");
        NewtonApproximation.solve((x) -> x * x - 2, (x) -> 2 * x, 200, 0.0);
        NewtonApproximation.solve((x) -> x * x - 2, (x) -> 2 * x, 200, 1.0);
    }
}
