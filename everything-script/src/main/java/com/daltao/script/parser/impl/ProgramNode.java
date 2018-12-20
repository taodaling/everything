package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

public class ProgramNode implements ASTNode {
    private ASTNode statement;

    public ProgramNode(ASTList list) {
        if (list.listChildAt(0) != null) {
            statement = list.listChildAt(0).childAt(0);
        }
    }

    @Override
    public String toString() {
        return statement == null ? ";" : statement.toString();
    }

    @Override
    public Object eval(ASTContext context) {
        if (statement != null) {
            return statement.eval(context);
        }
        return null;
    }
}
