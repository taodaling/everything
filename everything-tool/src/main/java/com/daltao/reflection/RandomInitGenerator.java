package com.daltao.reflection;


import java.lang.reflect.Method;
import java.text.MessageFormat;

public class RandomInitGenerator extends AbstractClassVisitor {

    public static void main(String[] args) throws Exception {
        RandomInitGenerator randomInitGenerator = new RandomInitGenerator();
        new ClassHostImpl(Object.class).accept(randomInitGenerator);
        System.out.println(randomInitGenerator);
    }

    private StringBuilder builder = new StringBuilder();

    @Override
    public void visitMethod(Method method) throws Exception {
        if (!ReflectionUtils.isSetter(method)) {
            return;
        }
        Class type = method.getParameterTypes()[0];

        String value;
        if (String.class.equals(type)) {
            value = "\"12345678\"";
        } else if (Byte.class.equals(type) || byte.class.equals(type)) {
            value = "(byte)1";
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            value = "true";
        } else if (Character.class.equals(type) || char.class.equals(type)) {
            value = "(char)1";
        } else if (Short.class.equals(type) || short.class.equals(type)) {
            value = "(short)1";
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            value = "1";
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            value = "1F";
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            value = "1D";
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            value = "1L";
        } else {
            value = "null";
        }

        builder.append(MessageFormat.format("model.{0}({1});\n", method.getName(), value));
    }

    @Override
    public void visitClass(Class cls) throws Exception {
        builder.append(MessageFormat.format("{0} model = new {0}();\n", cls.getSimpleName()));
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
