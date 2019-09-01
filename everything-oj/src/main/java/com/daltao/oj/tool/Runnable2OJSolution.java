package com.daltao.oj.tool;

import com.daltao.template.FastIO;
import com.daltao.test.Input;
import com.daltao.test.QueueInput;
import com.daltao.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.function.Function;

public class Runnable2OJSolution implements Function<Input, Input> {
    private Runnable task;

    public Runnable2OJSolution(Runnable task) {
        this.task = task;
    }

    @Override
    public Input apply(Input input) {
        InputStream originInput = System.in;
        PrintStream originOutput = System.out;
        String oj = System.getProperty("ONLINE_JUDGE");
        System.setSecurityManager(new OJSecurityManager());
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

            task.run();

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
            System.setSecurityManager(null);
        }
    }
}
