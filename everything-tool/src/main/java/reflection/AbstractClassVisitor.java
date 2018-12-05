package reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class AbstractClassVisitor implements ClassVisitor {
    @Override
    public void begin() throws Exception {

    }

    @Override
    public void visitField(Field field) throws Exception {

    }

    @Override
    public void visitMethod(Method method) throws Exception {

    }

    @Override
    public void visitSuperClass(Class superClass) throws Exception {

    }

    @Override
    public void visitInterface(Class cls) throws Exception {

    }

    @Override
    public void visitAnnotation(Annotation annotation) throws Exception {

    }

    @Override
    public void visitClass(Class cls) throws Exception {

    }

    @Override
    public void visitConstructor(Constructor constructor) throws Exception {

    }

    @Override
    public void end() throws Exception {

    }
}
