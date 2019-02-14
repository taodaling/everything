package com.daltao.template;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

/**
 * RankArray Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>十一月 23, 2018</pre>
 */
public class RankArrayTest {
    @Test
    public void test() {
        RankArray rankArray = new RankArray(Comparator.naturalOrder());
        rankArray.add(2);
        Assertions.assertEquals(1, rankArray.size());
        Assertions.assertEquals(2, rankArray.elementWithRank(0));
    }
} 
