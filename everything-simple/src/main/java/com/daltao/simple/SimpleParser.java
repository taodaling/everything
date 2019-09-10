package com.daltao.simple;

import com.google.common.collect.ImmutableMap;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleParser {
    public static void main(String[] args) {
        System.out.println(
                new SimpleParser().parse("I'm $!{name} and 我的年龄是$!{age}")
        );

        System.out.println(
                new SimpleParser().format("I'm $!{name} and 我的年龄是$!{age}",
                        ImmutableMap.of("name", "铁开诚", "age", "18"))
        );
    }

    private static final Pattern PATTERN = Pattern.compile("\\$!\\{(.+?)\\}");

    public List<Var> parse(String s) {
        Matcher matcher = PATTERN.matcher(s);
        List<Var> ans = new ArrayList<>();
        while (matcher.find()) {
            Var var = new Var();
            var.setContent(matcher.group(1));
            var.setFrom(matcher.start());
            var.setTo(matcher.end());
            ans.add(var);
        }
        return ans;
    }

    public String format(String template, Map<String, String> context) {
        List<Var> vars = parse(template);
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (Var var : vars) {
            if (var.getFrom() > last) {
                builder.append(template, last, var.getFrom());
            }
            builder.append(context.getOrDefault(var.getContent(), ""));
            last = var.getTo();
        }
        return builder.toString();
    }

    @Data
    public static class Var {
        private int from;
        private int to;
        private String content;
    }
}
