package com.daltao.oj.tool;

import com.daltao.common.Factory;
import com.daltao.oj.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.utils.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Function;

public class OJMainSolution implements Function<Input, Input>, Factory<Function<Input, Input>> {
    private Class cls;

    public OJMainSolution(Class cls) {
        this.cls = cls;
    }

    @Override
    public OJMainSolution newInstance() {
        return new OJMainSolution(cls);
    }

    @Override
    public Input apply(Input input) {
        InputStream originInput = System.in;
        PrintStream originOutput = System.out;
        String oj = System.getProperty("ONLINE_JUDGE");
        try {
            StringBuilder stringBuilder = new StringBuilder();
            while (input.available()) {
                stringBuilder.append(input.read()).append("\n");
            }
            byte[] data = stringBuilder.toString().getBytes(Charset.forName("ascii"));
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            System.setIn(bais);
            System.setOut(new PrintStream(baos));
            System.setProperty("ONLINE_JUDGE", "1");

            cls.getMethod("main", String[].class).invoke(null, new Object[]{new String[0]});
            FastIO fastIO = new FastIO(new ByteArrayInputStream(baos.toByteArray()), null);
            QueueInput output = new QueueInput();
            while (fastIO.hasMore()) {
                output.add(fastIO.readString());
            }

            output.end();
            return output;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setIn(originInput);
            System.setOut(originOutput);
            System.setProperty("ONLINE_JUDGE", StringUtils.valueOf(oj));
        }
    }
}
