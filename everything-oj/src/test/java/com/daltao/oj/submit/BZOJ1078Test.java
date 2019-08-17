package com.daltao.oj.submit;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Process2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BZOJ1078Test {
    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_BZOJ1078_oj.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1078.class)))
                .setInputFactory(new Generator())
                .build().call());
    }

    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput input = new QueueInput();
            int n = nextInt(1, 30);
            input.add(n);
            Node root = new Node();
            for (int i = 1; i <= n; i++) {
                Node target = new Node();
                target.val = i;
                input.add(insert(root, target));
            }
            return input.end();
        }

        public int insert(Node root, Node target) {
            if (root.l == null || nextInt(0, 1) == 0) {
                if (root.l == null) {
                    root.l = target;
                    return root.val;
                } else {
                    return insert(root.l, target);
                }
            } else {
                if (root.r == null) {
                    root.r = target;
                    return 100 + root.val;
                } else {
                    return insert(root.r, target);
                }
            }
        }

        public static class Node {
            Node l;
            Node r;
            int val;
        }
    }
}
