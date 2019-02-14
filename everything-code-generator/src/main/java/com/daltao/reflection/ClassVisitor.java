package com.daltao.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassVisitor {
    public void begin() throws Exception;

    public void visitField(Field field) throws Exception;

    public void visitMethod(Method method) throws Exception;

    public void visitSuperClass(Class superClass) throws Exception;

    public void visitInterface(Class cls) throws Exception;

    public void visitAnnotation(Annotation annotation) throws Exception;

    public void visitClass(Class cls) throws Exception;

    public void visitConstructor(Constructor constructor) throws Exception;

    public void end() throws Exception;

    public void visitPublicMethod(Method method) throws Exception;
}
