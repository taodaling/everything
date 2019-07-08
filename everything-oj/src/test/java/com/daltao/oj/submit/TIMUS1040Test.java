package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.template.MathUtils;
import com.daltao.test.Checker;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import com.daltao.utils.CollectionUtils;
import com.google.common.base.Charsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TIMUS1040Test {

    @Test
    public void test() {
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                        .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(TIMUS1040.class)))
                        .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(TIMUS1040.class)))
                        .setInputFactory(new Generator())
                        .setCheckerFactory(SpecialJudge::new)
                        .build().call()
        );
    }

    public static class SpecialJudge implements Checker {
        public int read(Input input) {
            return Integer.parseInt(input.read().toString());
        }

        @Override
        public boolean check(Input expected, Input actual, Input input) {
            StringBuilder builder = new StringBuilder();
            while (input.available()) {
                builder.append(input.read()).append(' ');
            }
            if (!actual.read().toString().equals("YES")) {
                return false;
            }
            FastIO io = new FastIO(new ByteArrayInputStream(builder.toString().getBytes(Charsets.US_ASCII)), System.out);
            int n = io.readInt();
            int m = io.readInt();
            int[][] edges = new int[n + 1][n + 1];
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < m; i++) {
                int a = io.readInt();
                int b = io.readInt();
                int w = read(actual);
                if (w <= 0 || w > m || set.contains(w)) {
                    return false;
                }
                set.add(w);
                edges[a][b] = edges[b][a] = w;
            }

            MathUtils.Gcd gcd = new MathUtils.Gcd();
            for (int i = 1; i <= n; i++) {
                int g = 0;
                int nonZeroCnt = 0;
                for (int j = 1; j <= n; j++) {
                    if (edges[i][j] > 0) {
                        nonZeroCnt++;
                    }
                    g = gcd.gcd(g, edges[i][j]);
                }
                if (g != 1 && nonZeroCnt > 1) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(2, 5);
            int m = nextInt(1, Math.min(5, n * (n - 1) / 2));
            input.add(n).add(m);
            List<int[]> edges = new ArrayList<>();
            for(int i = 1; i <= n; i++)
            {
                for(int j = i + 1; j <= n; j++){
                    edges.add(new int[]{i, j});
                }
            }
            edges = CollectionUtils.shuffle(edges);
            for (int i = 0; i < m; i++) {
                input.add(String.format("%d %d", edges.get(i)[0], edges.get(i)[1]));
            }

            return input.end();
        }
    }
}
