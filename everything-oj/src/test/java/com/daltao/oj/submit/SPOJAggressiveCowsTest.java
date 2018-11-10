package com.daltao.oj.submit;


import com.daltao.oj.template.FastIO;
import com.daltao.oj.tool.OJMainSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SPOJAggressiveCowsTest {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setTestTime(-1)
                .setExpectedSolution(new OJMainSolution(AGGRCOW.class))
                .setActualSolution(new OJMainSolution(SPOJAggressiveCows.class))
                .setInputFactory(new InputGenerator())
                .build().call());
    }

    public static class AGGRCOW {
        private static FastIO in;
        private static PrintWriter out;

        public static void main(String[] args) throws IOException {
            in = new FastIO(System.in, System.out);
            out = new PrintWriter(System.out, true);
            int T = in.readInt();
            int stalls[] = null;
            while (T-- > 0) {
                int N = in.readInt();
                int cows = in.readInt();
                stalls = new int[N];
                for (int i = 0; i < N; i++) {
                    stalls[i] = in.readInt();
                }
//      if(cows == 2){
//        System.out.println(stalls[stalls.length - 1] - stalls[0]);
//        continue;
//      }
                out.println(getMostWork(stalls, cows));
            }
        }

        public static long getMostWork(int stalls[], int cows) {
            Arrays.sort(stalls);
            int n = stalls.length;
            int lo = 0, hi = stalls[n - 1] - stalls[0], mid;
            while (lo < hi) {
                mid = (lo + hi) >> 1;
                if (lo == mid) break;
                if (find(stalls, cows, mid)) lo = mid;
                else hi = mid - 1;
            }
            if (find(stalls, cows, hi)) lo = hi;
            return lo;

        }

        private static boolean find(int[] stalls, long cows, long mid) {
            long toBeassigned = cows;
            toBeassigned--;
            long currentDistance = 0l;
            //first cow is assigned to first stall
            for (int i = 1; i < stalls.length; i++) {
                long distance = stalls[i] - stalls[i - 1];
                if (distance + currentDistance < mid) {
                    currentDistance += distance;
                } else {
                    toBeassigned--;
                    if (toBeassigned == 0) {
                        return true;
                    }
                    currentDistance = 0;
                }
            }
            return false;
        }
    }

    public static class InputGenerator extends RandomFactory {

        @Override
        public Input newInstance() {
            QueueInput output = new QueueInput();
            output.add("1");
            int n = nextInt(2, 1000);
            int c = nextInt(2, n);
            Set<Integer> set = new HashSet();

            output.add(n);
            output.add(c);
            for (int i = 0; i < n; i++) {
                int v = nextInt(0, 1000000000);
                while (set.contains(v)) {
                    v = nextInt(0, 1000000000);
                }
                output.add(v);
            }

            output.end();
            return output;
        }
    }
} 
