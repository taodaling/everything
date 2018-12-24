package com.daltao.script.parser.impl;

import com.daltao.script.ast.ASTContext;
import com.daltao.script.ast.ASTList;
import com.daltao.script.ast.ASTNode;

import java.util.List;
import java.util.function.Function;

public class ClassDefNode implements ASTNode {
    private IdentifierNode className;
    private ASTNode block;
    private ASTNode superClass;

    public ClassDefNode(ASTList list) {
        //"class" identifier ["extends" identifier] block
        className = (IdentifierNode) list.childAt(1);

        if (list.listChildAt(2) != null) {
            superClass = list.listChildAt(2).childAt(1);
        }

        block = list.childAt(3);
    }

    @Override
    public Object eval(ASTContext context) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setDefinedContext(context);
        if (superClass != null) {
            classInfo.setSuperClass((ClassInfo) superClass.eval(context));
        }
        className.setValue(context, classInfo);
        return classInfo;
    }

    public class ClassInfo implements Function<List<Object>, ClassObject> {
        private ASTContext definedContext;
        private ClassInfo superClass;
        private String constructName = "construct" + className.getName();

        public void init(ClassObject object) {
            if (superClass != null) {
                superClass.init(object);
            }
            block.eval(object);
        }

        @Override
        public ClassObject apply(List<Object> objects) {
            ClassObject object = new ClassObject();
            object.setDefinedContext(definedContext);

            init(object);

            //If constructor has been defined, invoke it
            DefNode.FunctionDef construct = (DefNode.FunctionDef) object.getProperty(constructName).getValue();
            if (construct != null) {
                construct.apply(objects);
            }
            return object;
        }

        public void setDefinedContext(ASTContext definedContext) {
            this.definedContext = definedContext;
        }

        @Override
        public String toString() {
            return "<" + definedContext.hashCode() + ">\n" + ClassDefNode.this.toString();
        }

        public void setSuperClass(ClassInfo superClass) {
            this.superClass = superClass;
        }
    }


    @Override
    public String toString() {
        return "class " + className + block;
    }
}
