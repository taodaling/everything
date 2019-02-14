package com.daltao.oj.submit;

import com.daltao.template.RankArray;
import com.daltao.oj.tool.OJMainSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class BZOJ3224Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setActualSolution(new OJMainSolution(BZOJ3224V2.class))
                .setExpectedSolution(new OJMainSolution(BZOJ3224.class))
                .setInputFactory(new InputGenerator())
                .setTestTime(10000)
                .setTimeLimitForEachTestCase(1000)
                .build().call());
    }

    private static class InputGenerator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = 80;
            int valRange = 100;

            RankArray array = new RankArray(Comparator.naturalOrder());
            input.add(n);

            for (int i = 0; i < n; i++) {
                int cmd = nextInt(1, 6);

                if ((cmd == 2 || cmd == 3 || cmd == 4) && array.size() == 0) {
                    i--;
                    continue;
                }
                if ((cmd == 5 || cmd == 6) && array.distinctSize() <= 1) {
                    i--;
                    continue;
                }

                switch (cmd) {
                case 1: {
                    //insert
                    int val = nextInt(1, valRange);
                    array.add(val);
                    input.add(cmd + " " + val);
                }
                break;
                case 2: {
                    //delete
                    int rank = nextInt(0, array.size() - 1);
                    Object element = array.elementWithRank(rank);
                    array.remove(element);
                    input.add(cmd + " " + element);
                }
                break;
                case 3: {
                    // rank
                    int rank = nextInt(0, array.size() - 1);
                    Object element = array.elementWithRank(rank);
                    input.add(cmd + " " + element);
                }
                break;
                case 4: {
                    // k-th
                    int rank = nextInt(0, array.size() - 1);
                    input.add(cmd + " " + (rank + 1));
                }
                break;
                case 5: {
                    // pre
                    int rank = nextInt(array.countOf(array.elementWithRank(0)), array.size() - 1);
                    Object element = array.elementWithRank(rank);
                    input.add(cmd + " " + element);
                }
                break;
                case 6: {
                    // post
                    int rank = nextInt(0, array.size() - array.countOf(array.elementWithRank(array.size() - 1)));
                    Object element = array.elementWithRank(rank);
                    input.add(cmd + " " + element);
                }
                break;
                }
            }

            input.end();

            return input;
        }
    }
}
