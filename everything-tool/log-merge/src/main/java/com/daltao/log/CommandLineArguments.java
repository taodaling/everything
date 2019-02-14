package com.daltao.log;

import java.util.*;

public class CommandLineArguments {
    private Map<String, String> optional = new HashMap<>();
    private List<String> required = new ArrayList<>();

    public boolean contain(String key) {
        return optional.containsKey(key);
    }

    public String get(String key, String defaultValue) {
        return optional.getOrDefault(key, defaultValue);
    }

    public List<String> getRequired() {
        return Collections.unmodifiableList(required);
    }

    public CommandLineArguments(String[] args) {
        this(Arrays.asList(args));
    }

    public CommandLineArguments(List<String> args) {
        for (String s : args) {
            parse(s);
        }
    }

    private void parse(String arg) {
        if (!arg.startsWith("-")) {
            required.add(arg);
        }
        int index = arg.indexOf('=');
        if (index == -1) {
            optional.put(arg, null);
        } else {
            optional.put(arg.substring(0, index), arg.substring(index + 1));
        }
    }
}
