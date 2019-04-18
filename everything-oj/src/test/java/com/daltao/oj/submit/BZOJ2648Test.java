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

/**
 * BZOJ2648 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ËÄÔÂ 18, 2019</pre>
 */
public class BZOJ2648Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setActualSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\Documents\\oj-c\\online_judge\\BZOJ2648.exe")))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2648.class)))
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2648.class)))
                        .setInputFactory(new Generator())
                        .setTimeLimitForEachTestCase(10 * 1000)
                        .setTestTime(1000)
                        .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO(System.in, System.out);
            int n = io.readInt();
            int m = io.readInt();

            List<int[]> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(new int[]{io.readInt(), io.readInt()});
            }
            for (int i = 0; i < m; i++) {
                int t = io.readInt();
                int[] pos = new int[]{io.readInt(), io.readInt()};
                if (t == 1) {
                    list.add(pos);
                    continue;
                }
                int dist = 1000000000;
                for (int[] black : list) {
                    dist = Math.min(dist, distance(black, pos));
                }
                io.cache.append(dist).append('\n');
            }
            io.flush();
        }


        public static int distance(int[] a, int[] b) {
            return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(500000, 500000);
            int m = nextInt(500000, 500000);
            input.add(n).add(m);
            int limit = (int) 1e8;
            for (int i = 0; i < n; i++) {
                /*input.add(String.format("%d %d", nextInt(-limit, limit), nextInt(-limit, limit)));*/
                input.add(String.format("%d %d", 1, i));
            }
            for (int i = 0; i < m; i++) {
                input.add(String.format("%d %d %d", 2, nextInt(-limit, limit), nextInt(-limit, limit)));
                //input.add(String.format("%d %d %d", 1, nextInt(-limit, limit), nextInt(-limit, limit)));
            }
            return input.end();
        }
    }
} 
