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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BZOJ1023Test {

    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setExpectedSolution(() -> new Runnable2OJSolution(new Process2Runnable("C:\\Users\\daltao\\VSCodeProject\\oj-c\\target\\_BZOJ1023.exe")))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ1023.class)))
                .setInputFactory(new Generator())
                .build().call());
    }

    public static class Generator extends RandomFactory {


        @Override
        public Input newInstance() {
            List<int[]> edges = new ArrayList<>();
            QueueInput input = new QueueInput();
            int id = 0;

            int n = nextInt(1, 300);
            Node[] nodes = new Node[n + 1];
            for (int i = 1; i <= n; i++) {
                nodes[i] = new Node();
                nodes[i].idFrom = id + 1;
                nodes[i].idTo = id + nextInt(3, 60);
                if (nextInt(0, 1) == 1) {
                    nodes[i].idTo = nodes[i].idFrom;
                }
                id = nodes[i].idTo;
            }

            for (int i = 2; i <= n; i++) {
                int f = nextInt(1, i - 1);
                nodes[i].nodes.add(nodes[f]);
                nodes[f].nodes.add(nodes[i]);
            }

            dfs(nodes[1], null, edges);

            input.add(id);
            input.add(edges.size());
            for (int[] es : edges) {
                input.add(es.length);
                input.add(Arrays.stream(es).mapToObj(Integer::new).map(String::valueOf).collect(Collectors.joining(" ")));
            }
            return input.end();
        }

        public void dfs(Node root, Node father, List<int[]> edges) {
            if (root.idFrom != root.idTo) {
                int[] ids = new int[root.idTo - root.idFrom + 2];
                for (int i = root.idFrom; i <= root.idTo; i++) {
                    ids[i - root.idFrom] = i;
                }
                ids[ids.length - 1] = root.idFrom;
                edges.add(ids);
            }
            for (Node node : root.nodes) {
                if (node == father) {
                    continue;
                }
                edges.add(new int[]{nextInt(root.idFrom, root.idTo), nextInt(node.idFrom, node.idTo)});
                dfs(node, root, edges);
            }
        }
    }

    private static class Node {
        List<Node> nodes = new ArrayList<>();
        int idFrom;
        int idTo;
    }
}
