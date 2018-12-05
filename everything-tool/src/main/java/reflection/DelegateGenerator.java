package reflection;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;

public class DelegateGenerator extends AbstractClassVisitor{
    public static void main(String[] args) throws Exception {
        DelegateGenerator visitor = new DelegateGenerator();
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
        body.append(MessageFormat.format("delegate().{0}(", method.getName()));
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
    public void visitInterface(Class cls) throws Exception {
        WrapperVisitor visitor = new WrapperVisitor();
        new ClassHostImpl(cls).accept(visitor);
        body.append(visitor.body);
    }

    @Override
    public void visitClass(Class cls) {
        head.append(MessageFormat.format("public abstract class {0} {2} {1} '{'\n" + "  protected abstract {1} delegate();\n",
                "Forwarding" + cls.getSimpleName(), cls.getSimpleName(), cls.isInterface() ? "implements" : "extend"));
        tail.append("}");
    }

    @Override
    public String toString() {
        return head.toString() + body + tail;
    }
}
