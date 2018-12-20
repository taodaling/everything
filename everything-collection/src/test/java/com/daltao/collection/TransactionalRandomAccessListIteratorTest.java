package com.daltao.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/** 
* TransactionalRandomAccessListIterator Tester. 
* 
* @author <Authors name> 
* @since <pre>ʮ���� 18, 2018</pre> 
* @version 1.0 
*/ 
public class TransactionalRandomAccessListIteratorTest {
    private TransactionalRandomAccessListIterator<Integer> before1() {
        return new TransactionalRandomAccessListIterator<>(Arrays.asList(1, 2, 3, 4, 5));
    }

    /**
     * No transaction
     */
    @Test
    public void test1() {
        TransactionalRandomAccessListIterator<Integer> iterator = before1();

        Assertions.assertEquals((Integer) 1, iterator.next());
        Assertions.assertEquals((Integer) 2, iterator.next());
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        Assertions.assertEquals((Integer) 5, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    /**
     * One transaction with rollback
     */
    @Test
    public void test2() {
        TransactionalRandomAccessListIterator<Integer> iterator = before1();

        Assertions.assertEquals((Integer) 1, iterator.next());
        Object sp2 = iterator.savePoint();
        Assertions.assertEquals((Integer) 2, iterator.next());
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        Assertions.assertEquals((Integer) 5, iterator.next());
        iterator.rollback(sp2);
        Assertions.assertEquals((Integer) 2, iterator.next());
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        Assertions.assertEquals((Integer) 5, iterator.next());
    }

    /**
     * Transaction with rollback and commit
     */
    @Test
    public void test3() {
        TransactionalRandomAccessListIterator<Integer> iterator = before1();

        Assertions.assertEquals((Integer) 1, iterator.next());
        Object sp2 = iterator.savePoint();
        Assertions.assertEquals((Integer) 2, iterator.next());
        Assertions.assertEquals((Integer) 3, iterator.next());
        iterator.commit(sp2);
        Assertions.assertEquals((Integer) 4, iterator.next());
        Object sp5 = iterator.savePoint();
        Assertions.assertEquals((Integer) 5, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        iterator.rollback(sp5);
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals((Integer) 5, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    /**
     * Embed transaction, rollback inner but commit the outer
     */
    @Test
    public void test4() {
        TransactionalRandomAccessListIterator<Integer> iterator = before1();

        Object sp1 = iterator.savePoint();
        Assertions.assertEquals((Integer) 1, iterator.next());
        Assertions.assertEquals((Integer) 2, iterator.next());
        Object sp3 = iterator.savePoint();
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        iterator.rollback(sp3);
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        iterator.commit(sp1);

        Assertions.assertEquals((Integer) 5, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    /**
     * Embed transaction, rollback outer but commit the inner
     */
    @Test
    public void test5() {
        TransactionalRandomAccessListIterator<Integer> iterator = before1();

        Object sp1 = iterator.savePoint();
        Assertions.assertEquals((Integer) 1, iterator.next());
        Assertions.assertEquals((Integer) 2, iterator.next());
        Object sp3 = iterator.savePoint();
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        iterator.commit(sp3);
        Assertions.assertEquals((Integer) 5, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        iterator.rollback(sp1);

        Assertions.assertEquals((Integer) 1, iterator.next());
        Assertions.assertEquals((Integer) 2, iterator.next());
        Assertions.assertEquals((Integer) 3, iterator.next());
        Assertions.assertEquals((Integer) 4, iterator.next());
        Assertions.assertEquals((Integer) 5, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

} 
