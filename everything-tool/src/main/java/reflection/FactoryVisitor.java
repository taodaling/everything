package reflection;



import com.daltao.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;

public class FactoryVisitor implements ClassVisitor {

    public static void main(String[] args) throws Exception {
        FactoryVisitor visitor = new FactoryVisitor(false);
        new ClassHostImpl(Object.class).accept(visitor);
        System.out.println(visitor);
    }

    private StringBuilder head = new StringBuilder();
    private StringBuilder body = new StringBuilder();
    private StringBuilder newInstanceHead = new StringBuilder();
    private StringBuilder newInstance = new StringBuilder();
    private StringBuilder newInstanceTail = new StringBuilder();
    private StringBuilder tail = new StringBuilder();
    private boolean builderSwitch;
    private String setterMethodSuffix = "";
    private String setterMethodReturnType = "void";
    private String className = "";

    public FactoryVisitor(boolean builderSwitch) {
        this.builderSwitch = builderSwitch;

        if (builderSwitch) {
            setterMethodSuffix = "    return this;\n";
        }
    }

    @Override
    public void begin() throws Exception {
        head.setLength(0);
        body.setLength(0);
        tail.setLength(0);
        newInstance.setLength(0);
        newInstanceHead.setLength(0);
        newInstanceTail.setLength(0);
    }

    @Override
    public void visitField(Field field) throws Exception {

    }

    @Override
    public void visitMethod(Method method) throws Exception {
        int mod = method.getModifiers();
        if (Modifier.isStatic(mod) || !Modifier.isPublic(mod)) {
            return;
        }
        if (!method.getName().startsWith("set")) {
            return;
        }
        if (method.getReturnType() != Void.class && method.getReturnType() != void.class) {
            return;
        }
        if (method.getParameterCount() == 0) {
            return;
        }

        String field = ReflectionUtils.getSetterAttributeName(method.getName());
        if (method.getParameterCount() == 1) {
            String type = method.getParameterTypes()[0].getSimpleName();
            body.append(MessageFormat.format(
                    "  private {0} {1};\n" +
                            "  public {3} {2}({0} {1})'{'\n" +
                            "    this.{1}={1};\n" +
                            "{4}'" +
                            "  }'\n",
                    type, field, method.getName(), setterMethodReturnType, setterMethodSuffix
            ));
            newInstance.append(MessageFormat.format("    instance.{0}({1});\n", method.getName(), field));
        } else {
            body.append(MessageFormat.format(
                    "  private Object[] {0};\n" +
                            "  public {2} {1}(",
                    field, method.getName(), setterMethodReturnType
            ));
            newInstance.append(MessageFormat.format(
                    "    instance.{0}(", method.getName()
            ));

            int order = 0;
            StringBuilder methodBody = new StringBuilder();
            for (Parameter parameter : method.getParameters()) {
                body.append(MessageFormat.format("{0} {1},", parameter.getType().getSimpleName(), parameter.getName()));
                methodBody.append(MessageFormat.format(
                        "    this.{0}[{1}] = {2};\n", field, order, parameter.getName()
                ));
                newInstance.append(MessageFormat.format("({0}){1}[{2}],",
                        parameter.getType().getSimpleName(), field, order));

                order++;
            }
            body.setLength(body.length() - 1);
            newInstance.setLength(newInstance.length() - 1);
            body.append(MessageFormat.format(")'{'\n" +
                    "{0}{1}" +
                    "'  }'\n", methodBody, setterMethodSuffix));
            newInstance.append(");\n");
        }
    }

    @Override
    public void visitSuperClass(Class superClass) throws Exception {
        FactoryVisitor visitor = new FactoryVisitor(builderSwitch);
        new ClassHostImpl(superClass).accept(visitor);
        body.append(visitor.body);
        newInstance.append(visitor.newInstance);
    }

    @Override
    public void visitInterface(Class cls) throws Exception {

    }

    @Override
    public void visitAnnotation(Annotation annotation) throws Exception {

    }

    @Override
    public void visitClass(Class cls) throws Exception {
        className = cls.getSimpleName() + (builderSwitch ? "Builder" : "Factory");

        head.append(MessageFormat.format(
                "public class {0}'{'\n", className));
        tail.append("}");

        newInstanceHead.append(MessageFormat.format("  public {0} {1}()'{'\n" +
                "    {0} instance = new {0}();\n", cls.getSimpleName(), builderSwitch ? "build" : "newInstance"));
        newInstanceTail.append("    return instance;\n" +
                "  }\n");

        if (builderSwitch) {
            setterMethodReturnType = className;
        }
    }

    @Override
    public void visitConstructor(Constructor constructor) throws Exception {

    }

    @Override
    public void end() throws Exception {

    }

    @Override
    public String toString() {
        return head.toString() + body + newInstanceHead + newInstance + newInstanceTail + tail;
    }
}
