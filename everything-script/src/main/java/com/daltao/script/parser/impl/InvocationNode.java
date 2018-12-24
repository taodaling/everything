package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InvocationNode implements ASTNode {
    private ASTNode function;
    private List<ASTNode> args;

    public InvocationNode(ASTNode function, ASTList bracket) {
        init(function, bracket);
    }

    private void init(ASTNode function, ASTList bracket) {
        this.function = function;
        ParamNode params = (ParamNode) bracket.childAt(1);
        args = new ArrayList<>(params.getParamList());
    }

    @Override
    public Object eval(ASTContext context) {
        Function<List<Object>, Object> functionDef = (Function<List<Object>, Object>) function.eval(context);
        List<Object> params = args.stream().map(x -> x.eval(context)).collect(Collectors.toList());
        return functionDef.apply(params);
    }

    @Override
    public String toString() {
        return new StringBuilder().append(function).append("(")
                .append(StringUtils.concatenate(",", args.toArray()))
                .append(")").toString();
    }
}
