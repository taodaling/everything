package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTNode;

public class DotNode implements ASTNode, SetValueAble {
    private ASTNode leftOperand;
    private SetValueAble rightOperand;

    public DotNode(ASTNode leftOperand, SetValueAble rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public Object eval(ASTContext context) {
        ClassObject classObject = (ClassObject) leftOperand.eval(context);
        return rightOperand.eval(classObject);
    }

    @Override
    public void setValue(ASTContext context, Object value) {
        ClassObject classObject = (ClassObject) leftOperand.eval(context);
        rightOperand.setValue(classObject, value);
    }

    @Override
    public String toString() {
        return leftOperand + "." + rightOperand;
    }
}
