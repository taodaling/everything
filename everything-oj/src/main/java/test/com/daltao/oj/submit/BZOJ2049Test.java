package test.com.daltao.oj.submit;

import com.daltao.oj.old.submit.bzoj.BZOJ2049V2;
import com.daltao.oj.submit.BZOJ2049;
import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * BZOJ2049 Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>九月 7, 2019</pre>
 */
public class BZOJ2049Test {
    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(BZOJ2049.class)))
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(com.daltao.oj.old.submit.bzoj.BZOJ2049V2.class)))
                .setTestTime(10000)
                .build().call());
    }

    public static class Generator extends RandomFactory {


        public boolean connect(int u, int v, Set<int[]> set) {
            Set<Integer> visited = new HashSet<>();
            dfs(u, set, visited);
            return visited.contains(v);
        }

        public void dfs(int i, Set<int[]> set, Set<Integer> visited) {
            if (visited.contains(i)) {
                return;
            }
            visited.add(i);
            for (int[] e : set) {
                if (e[0] == i || e[1] == i) {
                    dfs(e[0], set, visited);
                    dfs(e[1], set, visited);
                }
            }
        }

        @Override
        public Input newInstance() {
            Set<int[]> set = new TreeSet<>((a, b) -> a[0] == b[0] ?
                    a[1] - b[1] : a[0] - b[0]);
            set.clear();
            QueueInput input = new QueueInput();
            int n = nextInt(2, 50);
            int m = nextInt(1, 50);
            input.add(n).add(m);

            for (int i = 0; i < m; i++) {
                int u = nextInt(1, n);
                int v = nextInt(1, n);
                if (u == v) {
                    i--;
                    continue;
                }
                if (u > v) {
                    int tmp = u;
                    u = v;
                    v = tmp;
                }

                switch (nextInt(1, 3)) {
                    case 1:
                        input.add("Query").add(u).add(v);
                        break;
                    case 2:
                        if (connect(u, v, set)) {
                            i--;
                            break;
                        }
                        set.add(new int[]{u, v});
                        input.add("Connect").add(u).add(v);
                        break;
                    case 3:
                        if (!set.contains(new int[]{u, v})) {
                            i--;
                            break;
                        }
                        set.remove(new int[]{u, v});
                        input.add("Destroy").add(u).add(v);
                        break;
                }
            }

            return input.end();
        }
    }
} 
