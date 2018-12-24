package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;
import com.daltao.script.ast.impl.NestedContext;
import com.daltao.utils.Precondition;
import com.daltao.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefNode implements ASTNode {
    private SetValueAble func;
    private List<String> parameters;
    private ASTNode block;

    public DefNode(ASTList list) {
        //def : "def" [identifier] "("  params ")" block
        if (list.childAt(1) != null) {
            func = (SetValueAble) list.childAt(1);
        }

        parameters = new ArrayList<>();
        ParamNode params = (ParamNode) list.childAt(3);
        parameters = params.getParamList().stream().map(node -> ((IdentifierNode) node).getName())
                .collect(Collectors.toList());

        block = list.childAt(5);
    }

    @Override
    public Object eval(ASTContext context) {
        //创建函数
        FunctionDef functionDef = new FunctionDef(context);
        if (func != null) {
            func.setValue(context, functionDef);
        }
        return functionDef;
    }

    public class FunctionDef implements Function<List<Object>, Object> {
        private ASTContext definedContext;

        public FunctionDef(ASTContext definedContext) {
            this.definedContext = definedContext;
        }

        public Object apply(List<Object> args) {
            Precondition.equal(args.size(), parameters.size());

            NestedContext nestedContext = new NestedContext();
            nestedContext.setDefinedContext(definedContext);
            for (int i = 0, until = args.size(); i < until; i++) {
                nestedContext.newLocalContextVariable(parameters.get(i), args.get(i));
            }

            return block.eval(nestedContext);
        }

        @Override
        public String toString() {
            return "<" + definedContext.hashCode() + ">\n" + DefNode.this.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("def ");
        if (func != null) {
            builder.append(func);
        }
        builder.append("(");
        builder.append(StringUtils.concatenate(",", parameters.toArray()));
        builder.append(")");
        builder.append(block)
                .append("\n");
        return builder.toString();
    }
}
