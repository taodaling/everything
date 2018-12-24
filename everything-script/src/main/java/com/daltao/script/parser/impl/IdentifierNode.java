package com.daltao.script.parser.impl;

import com.daltao.script.ast.*;
import com.daltao.script.error.InterpretException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

public class IdentifierNode implements SetValueAble{
    private String name;
    private ValueReference valueReference;

    public String getName() {
        return name;
    }


    public IdentifierNode(ASTList list) {
        name = ((ASTLeaf) (list.childAt(0))).getToken()
                .getText();
    }

    private ValueReference getRef(ASTContext context) {
        if (valueReference != null && valueReference.getContext() != context) {
            //expired
            valueReference = null;
        }
        if (valueReference == null) {
            valueReference = context.getProperty(name);
        }
        return valueReference;
    }

    @Override
    public Object eval(ASTContext context) {
        return getRef(context).getValue();
    }

    public void setValue(ASTContext context, Object value) {
        getRef(context).setValue(value);
    }

    @Override
    public String toString() {
        return name;
    }
}
