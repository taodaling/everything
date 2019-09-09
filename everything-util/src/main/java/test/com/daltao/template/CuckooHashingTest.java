package test.com.daltao.template;

import com.daltao.template.CuckooHashing;
import com.daltao.utils.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CuckooHashingTest {
    Map<Integer, Integer> map;

    @Before
    public void before() {
        map = new CuckooHashing<>(1);
    }

    public Map<String, String> populate(Map<String, String> map) {
        Random random = new Random(0);
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            String s = RandomUtils.getRandomString(random, 'a', 'z', 10);
            map.put(s, s);
        }
        return map;
    }

    @Test
    public void addCock() {
        populate(new CuckooHashing<>(1));
    }

    @Test
    public void addHash() {
        populate(new HashMap<>(1));
    }

    @Test
    public void getCock() {
        Map<String, String> map = populate(new CuckooHashing<>(1));
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100000; j++) {
                map.containsKey(j);
            }
        }
    }

    @Test
    public void getHash() {
        Map<String, String> map = populate(new HashMap<>(1));
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 1000000; j++) {
                map.containsKey(j);
            }
        }
    }
}
