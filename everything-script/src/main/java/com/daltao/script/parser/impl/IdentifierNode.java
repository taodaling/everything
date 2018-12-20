package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTLeaf;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.error.InterpretException;

public class IdentifierNode implements ASTNode {
    private String name;

    public IdentifierNode(ASTList list) {
        name = ((ASTLeaf) (list.childAt(0))).getToken()
                .getText();
    }

    @Override
    public Object eval(ASTContext context) {
        if (!context.definedProperty(name)) {
            throw new InterpretException("Undefined variable " + name, null);
        }
        return context.getProperty(name);
    }

    public void setValue(ASTContext context, Object value) {
        context.putProperty(name, value);
    }

    @Override
    public String toString() {
        return name;
    }
}
