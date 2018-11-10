package reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassHostImpl implements ClassHost {
    final Class cls;

    public ClassHostImpl(Class cls) {
        this.cls = cls;
    }

    @Override
    public void accept(ClassVisitor visitor) throws Exception {
        visitor.begin();

        //Visit annotation at first
        for (Annotation annotation : cls.getAnnotations()) {
            visitor.visitAnnotation(annotation);
        }
        //Then visit class
        visitor.visitClass(cls);
        //Then visit field
        for (Field field : cls.getDeclaredFields()) {
            visitor.visitField(field);
        }

        for (Method method : cls.getMethods()) {
            visitor.visitPublicMethod(method);
        }

        //Then visit method
        for (Method method : cls.getDeclaredMethods()) {
            visitor.visitMethod(method);
        }

        //Then visit constructor
        for (Constructor constructor : cls.getConstructors()) {
            visitor.visitConstructor(constructor);
        }

        //Then visit superClass
        if (cls.getSuperclass() != null) {
            visitor.visitSuperClass(cls.getSuperclass());
        }
        //Then visit interfaces
        for (Class face : cls.getInterfaces()) {
            visitor.visitInterface(face);
        }

        visitor.end();
    }
}
