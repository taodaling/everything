package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BZOJ4066Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setInputFactory(new Generator())
                        .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\BZOJ4066.exe")))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ4066.class)))
                        .setTestTime(3000)
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int ans = 0;
            int n = io.readInt();
            List<int[]> points = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                int c = io.readInt();
                if (c == 1) {
                    int x = ans ^ io.readInt();
                    int y = ans ^ io.readInt();
                    int a = ans ^ io.readInt();
                    points.add(new int[]{x, y, a});
                } else {
                    int minX = ans ^ io.readInt();
                    int minY = ans ^ io.readInt();
                    int maxX = ans ^ io.readInt();
                    int maxY = ans ^ io.readInt();
                    int sum = 0;
                    for (int[] pt : points) {
                        if (pt[0] >= minX && pt[0] <= maxX &&
                                pt[1] >= minY && pt[1] <= maxY) {
                            sum += pt[2];
                        }
                    }
                    //ans = sum;
                    io.cache.append(sum).append('\n');
                }

            }

            io.flush();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 50000);
            input.add(n);
            for (int i = 0; i < n; i++) {
                int c = nextInt(1, 2);
                if (c == 1) {
                    input.add(String.format("%d %d %d %d", c,
                            nextInt(1, 1000), nextInt(1, 1000), nextInt(1, 1000)));
                } else {
                    int x1 = nextInt(1, 1000);
                    int x2 = nextInt(1, 1000);
                    int y1 = nextInt(1, 1000);
                    int y2 = nextInt(1, 1000);
                    input.add(String.format("%d %d %d %d %d", c,
                            Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2)));
                }
            }
            return input.add(3).end();
        }
    }
}
