package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class NewObjectNode implements ASTNode {
    private SetValueAble className;
    private List<ASTNode> args;

    public NewObjectNode(ASTList list) {
        //new : "new" identifier "(" params ")"
        className = (SetValueAble) list.childAt(1);
        ParamNode paramNode = (ParamNode) list.childAt(3);
        args = paramNode.getParamList();
    }

    @Override
    public Object eval(ASTContext context) {
        ClassDefNode.ClassInfo classInfo = (ClassDefNode.ClassInfo) className.eval(context);
        List<Object> actualArgs = args.stream().map(x -> x.eval(context)).collect(Collectors.toList());
        return classInfo.apply(actualArgs);
    }

    @Override
    public String toString() {
        return "new " + className + "(" +
                StringUtils.concatenate(",", args.toArray())
                + ")";
    }
}
