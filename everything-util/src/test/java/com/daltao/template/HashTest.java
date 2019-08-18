package com.daltao.template;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
* Hash Tester. 
* 
* @author <Authors name> 
* @since <pre>八月 18, 2019</pre> 
* @version 1.0 
*/ 
public class HashTest { 
    int[] data = new int[]{1, 1, 3, 4, 1, 3, 4, 5};
    Hash hash;

    @Before
    public void before(){
        hash = new Hash(100, 31);
        hash.populate(data, data.length);
    }

    @Test
    public void test(){
        Assert.assertEquals(hash.partial(0, 0), hash.partial(1, 1));
        Assert.assertEquals(hash.partial(1, 2), hash.partial(4, 5));
        Assert.assertEquals(hash.partial(1, 3), hash.partial(4, 6));
        Assert.assertEquals(hash.partial(2, 3), hash.partial(5, 6));
        Assert.assertNotEquals(hash.partial(0, 3), hash.partial(4, 7));
    }

} 
