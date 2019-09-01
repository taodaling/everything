package test.com.daltao.template;

import com.daltao.template.Memory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Memory Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>°ËÔÂ 30, 2019</pre>
 */
public class MemoryTest {

    @Test
    public void testRotate1() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Memory.rotate(list, 0, 2, 4);
        Assert.assertEquals(list, Arrays.asList(3, 4, 5, 1, 2));
    }
    @Test
    public void testRotate2() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Memory.rotate(list, 0, 1, 4);
        Assert.assertEquals(list, Arrays.asList(2, 3, 4, 5, 1));
    }
    @Test
    public void testRotate3() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Memory.rotate(list, 0, 3, 4);
        Assert.assertEquals(list, Arrays.asList(4, 5, 1, 2, 3));
    }
    @Test
    public void testRotate4() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Memory.rotate(list, 0, 4, 4);
        Assert.assertEquals(list, Arrays.asList(5, 1, 2, 3, 4));
    }
    @Test
    public void testRotate5() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Memory.rotate(list, 0, 0, 4);
        Assert.assertEquals(list, Arrays.asList(1, 2, 3, 4, 5));
    }
} 
