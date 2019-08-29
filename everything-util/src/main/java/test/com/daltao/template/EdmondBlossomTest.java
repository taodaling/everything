package test.com.daltao.template;

import com.daltao.template.EdmondBlossom;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

/**
 * EdmondBlossom Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>八月 29, 2019</pre>
 */
public class EdmondBlossomTest {

    @Test
    public void test1(){
        EdmondBlossom eb = new EdmondBlossom(1);
        Assert.assertEquals(0, eb.maxMatch());
    }

    @Test
    public void test2(){
        EdmondBlossom eb = new EdmondBlossom(2);
        Assert.assertEquals(0, eb.maxMatch());
    }

    @Test
    public void test3(){
        EdmondBlossom eb = new EdmondBlossom(2);
        eb.addEdge(0, 1);
        Assert.assertEquals(1, eb.maxMatch());
    }

    @Test
    public void test4(){
        EdmondBlossom eb = new EdmondBlossom(2);
        eb.addEdge(0, 1);
        eb.addEdge(0, 1);
        Assert.assertEquals(1, eb.maxMatch());
    }

    @Test
    public void test6(){
        EdmondBlossom eb = new EdmondBlossom(5);
        eb.addEdge(0, 1);
        eb.addEdge(0, 2);
        eb.addEdge(1, 3);
        eb.addEdge(2, 4);
        eb.addEdge(3, 4);
        Assert.assertEquals(2, eb.maxMatch());
    }

    @Test
    public void test7(){
        EdmondBlossom eb = new EdmondBlossom(6);
        eb.addEdge(0, 1);
        eb.addEdge(0, 2);
        eb.addEdge(1, 3);
        eb.addEdge(2, 4);
        eb.addEdge(3, 4);
        eb.addEdge(2, 5);
        Assert.assertEquals(3, eb.maxMatch());
    }

    @Test
    public void test8(){
        EdmondBlossom eb = new EdmondBlossom(9);
        eb.addEdge(0, 1);
        eb.addEdge(0, 8);
        eb.addEdge(8, 2);
        eb.addEdge(2, 3);
        eb.addEdge(3, 4);
        eb.addEdge(3, 6);
        eb.addEdge(4, 5);
        eb.addEdge(6, 7);
        Assert.assertEquals(4, eb.maxMatch());
    }

    @Test
    public void test9(){
        EdmondBlossom eb = new EdmondBlossom(6);
        eb.addEdge(1, 3);
        eb.addEdge(5, 2);
        eb.addEdge(0, 4);
        eb.addEdge(3, 4);
        eb.addEdge(3, 5);
        eb.addEdge(4, 5);
        Assert.assertEquals(3, eb.maxMatch());
    }
} 
