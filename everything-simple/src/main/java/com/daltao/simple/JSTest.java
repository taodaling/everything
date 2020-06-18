package com.daltao.simple;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public class JSTest {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager scriptEngineManager =
                new ScriptEngineManager();
        ScriptEngine nashorn =
                scriptEngineManager.getEngineByName("nashorn");
        SimpleBindings bindings = new SimpleBindings();
        bindings.put("context", new JSTest(1));
        Object ans = nashorn.eval("context.runMillion(100000000000);if(context.getId() != null){2;}else{1;}", bindings);
        System.out.println(ans);
    }

    private int id;

    public JSTest(int id) {
        this.id = id;
    }

    public void runMillion(long time) {
        long ans = 0;
        while (time-- > 0) {
            ans++;
            ans--;
        }
    }

    public int getId() {
        return id;
    }
}
