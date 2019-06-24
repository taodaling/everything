package com.daltao.oj.tool;

import com.daltao.template.FastIO;
import com.daltao.test.Checker;
import com.daltao.test.Input;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class InteractiveTask implements Runnable {
    private final Runnable prog;

    protected InteractiveTask(Runnable prog) {
        this.prog = prog;
    }

    @Override
    public final void run() {
        InputStream sysin = System.in;
        PrintStream sysout = System.out;

        try {
            Recorder recorder = new Recorder();
            BindingOutputStream interactUse = new BindingOutputStream(recorder, "counter");
            BindingOutputStream progUse = new BindingOutputStream(recorder, "user");
            System.setIn(interactUse.getAnotherSide());
            System.setOut(new PrintStream(progUse));
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.submit(prog);
            service.shutdown();
            boolean r = interact(new FastIO(progUse.getAnotherSide(), interactUse), new FastIO(sysin, new ByteArrayOutputStream()));
            recorder.record("result", r ? '1' : '0');
            sysout.print(recorder.toString());
            service.shutdownNow();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setIn(sysin);
            System.setOut(sysout);
        }
    }

    public static Checker newChecker() {
        return new InnerChecker();
    }

    private static class InnerChecker implements Checker {

        @Override
        public boolean check(Input expected, Input actual, Input input) {
            Object last = null;
            while (actual.available()) {
                last = actual.read();
            }
            return Objects.equals(last, "1");
        }
    }

    protected abstract boolean interact(FastIO progIO, FastIO sysin) throws Exception;
}
