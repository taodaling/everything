package com.daltao.reflection;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;

public class WrapperVisitor extends AbstractClassVisitor {
    public static void main(String[] args) throws Exception {
        WrapperVisitor visitor = new WrapperVisitor();
        new ClassHostImpl(Object.class).accept(visitor);
        System.out.println(visitor);
    }

    StringBuilder body = new StringBuilder();
    StringBuilder head = new StringBuilder();
    StringBuilder tail = new StringBuilder();

    @Override
    public void begin() {
        head.setLength(0);
        body.setLength(0);
        tail.setLength(0);
    }

    @Override
    public void visitField(Field field) {

    }

    @Override
    public void visitMethod(Method method) {
        int modifier = method.getModifiers();
        if (Modifier.isPrivate(modifier) || Modifier.isStatic(modifier)) {
            return;
        }
        String accessLevel = Modifier.isProtected(modifier) ? "protected" : "public";
        body.append(MessageFormat.format("  {0} {1} {2}(", accessLevel, method.getGenericReturnType().getTypeName(), method.getName()));

        int argCount = method.getParameterCount();
        for (Parameter arg : method.getParameters()) {
            body.append(MessageFormat.format("{0} {1},", arg.getParameterizedType().getTypeName(), arg.getName()));
        }
        if (argCount > 0) {
            body.setLength(body.length() - 1);
        }
        body.append("){");
        if (method.getReturnType() != Void.class && method.getReturnType() != void.class) {
            body.append("return ");
        }
        body.append(MessageFormat.format("inner.{0}(", method.getName()));
        for (Parameter arg : method.getParameters()) {
            body.append(MessageFormat.format("{0},", arg.getName()));
        }
        if (argCount > 0) {
            body.setLength(body.length() - 1);
        }
        body.append(");");
        body.append("}\n");
    }

    @Override
    public void visitSuperClass(Class superClass) {

    }

    @Override
    public void visitInterface(Class cls) throws Exception {
        WrapperVisitor visitor = new WrapperVisitor();
        new ClassHostImpl(cls).accept(visitor);
        body.append(visitor.body);
    }

    @Override
    public void visitAnnotation(Annotation annotation) {
    }

    @Override
    public void visitClass(Class cls) {
        head.append(MessageFormat.format("public class {0} {2} {1}  '{'\n" + "  private final {1} inner;\n" + "  protected {0}({1} inner)'{this.inner = inner;}'\n",
                cls.getSimpleName() + "Wrapper", cls.getSimpleName(), cls.isInterface() ? "implements" : "extends"));
        tail.append("}");
    }

    @Override
    public void visitConstructor(Constructor constructor) throws Exception {

    }

    @Override
    public void end() {

    }

    @Override
    public String toString() {
        return head.toString() + body + tail;
    }
}
