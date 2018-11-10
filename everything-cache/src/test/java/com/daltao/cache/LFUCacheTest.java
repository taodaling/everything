package com.daltao.cache;


import com.daltao.test.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

/**
 * LFUCache Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>ʮһ�� 9, 2018</pre>
 */
public class LFUCacheTest {
    private static final int SIZE = 10;
    private static final int ADD = 1;
    private static final int PURGE = 2;
    private static final int GET = 3;
    private static final int CONTAIN = 4;
    private static final int PURGE_ALL = 5;



    @Test
    public void test() {
        Assertions.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new InputFactory())
                .setActualSolution(() -> new Solution(new LFUCache(SIZE)))
                .setExpectedSolution(() -> new Solution(new EasiestLFUCache(SIZE)))
                .setTestTime(10000)
                .build().call());

    }
    @Test
    public void test1(){
        testCore1(new LFUCache(1));
        testCore1(new EasiestLFUCache(1));
    }

    public void testCore1(Cache cache){
        Assertions.assertFalse(cache.contain(1));
        Assertions.assertFalse(cache.contain(1));
        Assertions.assertEquals(null, cache.get(2));
    }

    @Test
    public void test2(){
        testCore2(new LFUCache(1));
        testCore2(new EasiestLFUCache(1));
    }

    public void testCore2(Cache cache){
        Assertions.assertFalse(cache.contain(2));
        Assertions.assertFalse(cache.contain(1));
        Assertions.assertEquals(null, cache.get(1));
    }

    @Test
    public void test3(){
        testCore3(new LFUCache(1));
        testCore3(new EasiestLFUCache(1));
    }

    public void testCore3(Cache cache){
        Assertions.assertEquals(null, cache.get(1));
        Assertions.assertEquals(false, cache.contain(2));
        cache.add(2, "q");
    }

    @Test
    public void test4(){
        testCore4(new LFUCache(1));
        testCore4(new EasiestLFUCache(1));
    }


    public void testCore4(Cache cache){
        cache.add(2, "e");
        cache.purgeAll();
        Assertions.assertEquals(false, cache.contain(1));
    }

    @Test
    public void test5(){
        testCore5(new LFUCache(1));
        testCore5(new EasiestLFUCache(1));
    }


    public void testCore5(Cache cache){
        cache.add(1, "b");
        cache.purgeAll();
        Assertions.assertEquals(false, cache.contain(1));
    }

    @Test
    public void test6(){
        testCore6(new LFUCache(1));
        testCore6(new EasiestLFUCache(1));
    }


    public void testCore6(Cache cache){
        cache.add(1, "z");
        Assertions.assertEquals(true, cache.contain(1));
        cache.purgeAll();
    }

    @Test
    public void test7(){
        testCore7(new LFUCache(2));
        testCore7(new EasiestLFUCache(2));
    }


    public void testCore7(Cache cache){
        cache.add(1, "z");
        Assertions.assertEquals(null, cache.get(2));
        cache.add(2, "g");
        Assertions.assertEquals(null, cache.get(3));
        cache.add(1, "b");
    }

    @Test
    public void test8(){
        testCore8(new LFUCache(2));
        testCore8(new EasiestLFUCache(2));
    }


    public void testCore8(Cache cache){
        cache.add(3, "q");
        cache.add(3, "e");
        cache.purgeAll();
        cache.add(3, "q");
        cache.add(3, "n");
    }

    @Test
    public void test9(){
        testCore9(new LFUCache(2));
        testCore9(new EasiestLFUCache(2));
    }


    public void testCore9(Cache cache){
        cache.add(1, "v");
        cache.add(3, "p");
        cache.add(2, "o");
        cache.add(4, "f");
        cache.purgeAll();
    }

    @Test
    public void test10(){
        testCore10(new LFUCache(2));
        testCore10(new EasiestLFUCache(2));
    }


    public void testCore10(Cache cache){
        cache.add(1, 1);
        cache.add(2, 2);
        Assertions.assertEquals(1, cache.get(1));
        cache.add(3, 3);
        Assertions.assertEquals(null, cache.get(2));
    }

    @Test
    public void test11(){
        testCore10(new LFUCache(2));
        testCore10(new EasiestLFUCache(2));
    }


    public void testCore11(Cache cache){
        cache.add(1, 1);
        cache.add(2, 2);
        Assertions.assertEquals(1, cache.get(1));
        cache.add(3, 3);
        Assertions.assertEquals(null, cache.get(2));
    }

    private static class InputFactory extends RandomFactory {
        @Override
        public Input<TestCase> newInstance() {
            QueueInput<TestCase> input = new QueueInput<>();

            int cmdNum = nextInt(100, 500);

            for (int i = 0; i < cmdNum; i++) {
                int cmd = nextInt(ADD, PURGE_ALL);
                switch (cmd) {
                    case ADD:
                        input.add(TestCase.newTestCase("add", nextInt(1, SIZE * 2), nextString(1)));
                        break;
                    case PURGE:
                        input.add(TestCase.newTestCase("purge", nextInt(1, SIZE * 2)));
                        break;
                    case GET:
                        input.add(TestCase.newTestCase("get", nextInt(1, SIZE * 2)));
                        break;
                    case CONTAIN:
                        input.add(TestCase.newTestCase("contain", nextInt(1, SIZE * 2)));
                        break;
                    case PURGE_ALL:
                        input.add(TestCase.newTestCase("purgeAll"));
                        break;
                }
            }

            input.end();
            return input;
        }
    }

    private static class Solution implements Function<Input, Input> {
        private Cache cache;

        private Solution(Cache cache) {
            this.cache = cache;
        }

        @Override
        public Input apply(Input input) {
            QueueInput output = new QueueInput<>();
            while (input.available()) {
                TestCase testCase = (TestCase) input.read();
                String command = (String) testCase.getType();
                Object[] args = testCase.getArgs();
                switch (command) {
                    case "add":
                        cache.add(args[0], args[1]);
                        break;
                    case "purge":
                        cache.purge(args[0]);
                        break;
                    case "get":
                        output.add(cache.get(args[0]));
                        break;
                    case "contain":
                        output.add(cache.contain(args[0]));
                        break;
                    case "purgeAll":
                        cache.purgeAll();
                        break;
                }
            }
            output.end();
            return output;
        }
    }
} 
