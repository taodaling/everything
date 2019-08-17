package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.template.GeometryUtils;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BZOJ1067Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1067.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_BZOJ1064_oj.exe")))
                .build().call());
    }

    public static class Solution {
        public static void main(String[] args) {
            String data = "\n" +
                    "\n" +
                    "14\n" +
                    "0.317 0.098\n" +
                    "0.580 0.079\n" +
                    "0.369 0.200\n" +
                    "0.091 0.090\n" +
                    "0.203 0.356\n" +
                    "0.537 0.594\n" +
                    "0.893 0.695\n" +
                    "0.192 0.007\n" +
                    "0.193 0.217\n" +
                    "0.713 0.467\n" +
                    "0.306 0.492\n" +
                    "0.899 0.404\n" +
                    "0.200 0.098\n" +
                    "0.167 0.095\n";
            FastIO io = new FastIO(new ByteArrayInputStream(data.getBytes()), System.out);


            int n = io.readInt();
            List<GeometryUtils.Point2D> pts = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                pts.add(new GeometryUtils.Point2D(io.readDouble(), io.readDouble()));
            }
            double max = 0;

            List<GeometryUtils.Point2D> maxList = null;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    for (int k = j + 1; k < n; k++) {
                        for (int t = k + 1; t < n; t++) {
                            List<GeometryUtils.Point2D> list = Arrays.asList(
                                    pts.get(i), pts.get(j), pts.get(k), pts.get(t)
                            );
                            double local = new GeometryUtils.GrahamScan(new GeometryUtils.PointPolygon(list)).getConvex().area();
                            if (max < local) {
                                max = local;
                                maxList = list;
                            }
                        }
                    }
                }
            }
            io.cache.append(max).append('\n').append(maxList);

            io.flush();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(100, 100);
            input.add(n);
            for (int i = 0; i < n; i++) {
                input.add(String.format("%.3f %.3f", nextDouble(), nextDouble()));
            }
            return input.end();
        }
    }
}
