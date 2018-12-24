package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTLeaf;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

import java.util.Objects;

public class BinaryOp implements ASTNode {
    private ASTNode leftOperand;
    private ASTNode rightOperand;
    private String operator;

    private ASTNode delegate;

    public BinaryOp(ASTList list) {
        leftOperand = list.childAt(0);
        operator = ((ASTLeaf) list.childAt(1)).getToken().getText();
        rightOperand = list.childAt(2);

        switch (operator) {
            case "+":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                + (Integer) (rightOperand.eval(context));
                    }
                };
                break;
            case "-":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                - (Integer) (rightOperand.eval(context));
                    }
                };
                break;
            case "*":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                * (Integer) (rightOperand.eval(context));
                    }
                };
                break;
            case "/":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                / (Integer) (rightOperand.eval(context));
                    }
                };
                break;
            case "%":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                % (Integer) (rightOperand.eval(context));
                    }
                };
                break;
            case "<":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                < (Integer) (rightOperand.eval(context)) ? 1 : 0;
                    }
                };
                break;
            case ">":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return (Integer) (leftOperand.eval(context))
                                > (Integer) (rightOperand.eval(context)) ? 1 : 0;
                    }
                };
                break;
            case "is":
                delegate = new ASTNode() {
                    @Override
                    public Object eval(ASTContext context) {
                        return Objects.equals(leftOperand.eval(context), rightOperand.eval(context)) ? 1 : 0;
                    }
                };
                break;
            case "=":
                delegate = new ASTNode() {
                    SetValueAble left = (SetValueAble) leftOperand;

                    @Override
                    public Object eval(ASTContext context) {
                        left.setValue(context, rightOperand.eval(context));
                        return left.eval(context);
                    }
                };
                break;
        }
    }

    @Override
    public Object eval(ASTContext context) {
        return delegate.eval(context);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", leftOperand, operator, rightOperand);
    }
}
