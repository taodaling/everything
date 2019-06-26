package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * UOJ179 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 26, 2019</pre>
 */
public class UOJ179Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\taoda\\Documents\\oj-c\\UOJ179_oj.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(UOJ179.class)))
                .setCheckerFactory(SpecialChecker::new)
                .build().call());
    }


    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 30);
            int m = nextInt(1, 30);
            input.add(n).add(m).add(0);
            for (int i = 0; i < n; i++) {
                input.add(nextInt(-100, 100));
            }
            for (int i = 0; i < m; i++) {
                for (int j = 0; j <= n; j++) {
                    input.add(nextInt(-100, 100));
                }
            }

            return input.end();
        }
    }

    public static class SpecialChecker2 implements Checker {

        Pattern isNum = Pattern.compile("-?\\d+(\\.\\d*)?");


        public BigDecimal rd(Input input) {
            return new BigDecimal(input.read().toString());
        }

        public int ri(Input input) {
            return Integer.parseInt(input.read().toString());
        }

        @Override
        public boolean check(Input expected, Input actual, Input input) {
            BigDecimal ans = rd(expected);
            int n = ri(input);
            int m = ri(input);
            ri(input);
            BigDecimal[] assignments = new BigDecimal[n];
            for (int i = 0; i < n; i++) {
                assignments[i] = rd(expected);
            }

            BigDecimal sum = BigDecimal.ZERO;
            for (int i = 0; i < n; i++) {
                BigDecimal c = rd(input);
                sum = sum.add(c.multiply(assignments[i]));
            }

            if (sum.subtract(ans).abs().compareTo(new BigDecimal(1e-6)) > 0) {
                return false;
            }

            for (int i = 0; i < m; i++) {
                BigDecimal v = BigDecimal.ZERO;
                for (int j = 0; j < n; j++) {
                    BigDecimal c = rd(input);
                    v = v.add(c.multiply(assignments[j]));
                }
                v = v.subtract(rd(input));
                if (v.compareTo(BigDecimal.valueOf(1e-12)) > 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public static class SpecialChecker implements Checker {

        Pattern isNum = Pattern.compile("-?\\d+(\\.\\d*)?");

        @Override
        public boolean check(Input expected, Input actual, Input input) {
            String a = expected.read().toString();
            String b = actual.read().toString();
            if (isNum.matcher(a).matches() && isNum.matcher(b).matches()) {
                return Math.abs(Double.parseDouble(a) - Double.parseDouble(b)) < 1e-6;
            }
            return a.equals(b);
        }
    }
} 
