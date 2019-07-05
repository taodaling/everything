package com.daltao.oj.submit;


import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1452Test {

    @Test
    public void test(){
        Assertions.assertTrue(
                new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Solution.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1452.class)))
                .setTestTime(1000)
                .build().call()
        );
    }

    public static class Solution {
        public static void main(String[] args) {
            FastIO io = new FastIO();
            int n = io.readInt();
            int m = io.readInt();
            int[][] mat = new int[n + 1][m + 1];
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= m; j++) {
                    mat[i][j] = io.readInt();
                }
            }
            int q = io.readInt();
            for (int i = 0; i < q; i++) {
                int t = io.readInt();
                if (t == 1) {
                    int x = io.readInt();
                    int y = io.readInt();
                    int c = io.readInt();
                    mat[x][y] = c;
                }
                else{
                    int x1 = io.readInt();
                    int x2 = io.readInt();
                    int y1 = io.readInt();
                    int y2 = io.readInt();
                    int c = io.readInt();
                    int cnt = 0;
                    for(int x = x1; x <= x2; x++){
                        for(int y = y1; y <= y2; y++){
                            if(mat[x][y] == c){
                                cnt++;
                            }
                        }
                    }
                    io.cache.append(cnt).append('\n');
                }
            }
            io.flush();
        }
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 3);
            int m = nextInt(1, 3);
            input.add(n).add(m);
            for (int i = 0; i < n; i++) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < m; j++) {
                    builder.append(nextInt(1, n))
                            .append(' ');
                }
                input.add(builder.toString());
            }
            int q = nextInt(10, 20);
            input.add(q);
            for (int i = 0; i < q; i++) {
                int t = nextInt(1, 2);
                if (t == 1) {
                    input.add(String.format("%d %d %d %d", 1, nextInt(1, n), nextInt(1, m), nextInt(1, n)));
                } else {
                    int x1 = nextInt(1, n);
                    int x2 = nextInt(1, n);
                    int y1 = nextInt(1, m);
                    int y2 = nextInt(1, m);
                    int c = nextInt(1, n);
                    input.add(String.format("%d %d %d %d %d %d",
                            2, Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2), c));
                }
            }
            return input.end();
        }
    }
}
