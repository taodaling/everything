package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class BlockNode implements ASTNode {
    private List<ASTNode> statements = new ArrayList<>();

    public BlockNode(ASTList list) {
        //block : "{" { statement } "}"
        for (ASTNode child : list.listChildAt(1)) {
            //statement
            statements.add(child);
        }
    }

    @Override
    public Object eval(ASTContext context) {
        Object result = null;
        for (ASTNode statement : statements) {
            result = statement.eval(context);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");
        for (ASTNode statement : statements) {
            builder.append(statement).append(";\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
