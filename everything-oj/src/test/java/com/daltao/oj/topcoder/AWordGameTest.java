package com.daltao.oj.topcoder;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class AWordGameTest {
    AWordGame game = new AWordGame();

    @Test
    public void test(){
        String[] words = new String[]{"pascal program programmer task tree", "treacherous treachery tread trace"};
        String ans = game.outcome(words);
        Assert.assertEquals("treacherous", ans);
    }


    @Test
    public void test2(){
        String[] words = new String[]{"academic","base","board","cola","code","cute","hack"};
        String ans = game.outcome(words);
        Assert.assertEquals("code", ans);
    }
}
