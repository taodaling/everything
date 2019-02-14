package com.daltao.oj.tool;


import com.daltao.template.FastIO;
import com.daltao.utils.IOUtils;
import com.daltao.common.NILRunnable;
import com.daltao.utils.Precondition;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Created by dalt on 2018/3/13.
 */
public class PseudoOJ2 implements Runnable, AutoCloseable {
    private long msCostOfStandard;
    private long msCostOfTest;
    private Writer errorInfoStream = new OutputStreamWriter(System.out, Charset.forName("ascii"));
    private String ojName = "dalt-oj";
    private Runnable beTestedProgram = NILRunnable.getInstance();
    private Runnable standardProgram = NILRunnable.getInstance();
    private Runnable inputSupplier = NILRunnable.getInstance();
    private int testTime = 100;
    private Checker checker = new EqualChecker();

    public void setErrorInfoStream(File file) {
        try {
            setErrorInfoStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
        }
    }

    public void setErrorInfoStream(OutputStream errorInfoStream) {
        this.errorInfoStream = new OutputStreamWriter(errorInfoStream, Charset.forName("ascii"));
    }

    public void setErrorInfoStream(String filename) {
        try {
            setErrorInfoStream(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
           throw new RuntimeException(e);
        }
    }

    public void setBeTestedProgram(Runnable beTestedProgram) {
        this.beTestedProgram = beTestedProgram;
    }

    public void setStandardProgram(Runnable standardProgram) {
        this.standardProgram = standardProgram;
    }

    public void setInputSupplier(Runnable inputSupplier) {
        this.inputSupplier = inputSupplier;
    }

    public void setTestTime(int testTime) {
        this.testTime = testTime;
    }

    @Override
    public void run() {
        System.out.println("Start testing on " + ojName + "!\n**********************************\n");
        msCostOfStandard = 0;
        msCostOfTest = 0;
        try {
            for (int i = 1; i <= testTime; i++) {
                testOnce();

                System.out.println("Passed test #" + i);
            }
        } catch (IOException e) {
           throw new RuntimeException(e);
        }

        System.out.println("\n\n**********************************\nTested program has passed all the tests!!!\n");
        System.out.println("Test on standard program spent " + msCostOfStandard + "ms");
        System.out.println("Test on tested program spent " + msCostOfTest + "ms");
    }

    public void testOnce() throws IOException {
        InputStream stdin = System.in;
        PrintStream stdout = System.out;
        System.setProperty("ONLINE_JUDGE", "1");
        ByteArrayOutputStream supplierOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        try {


            System.setOut(new PrintStream(supplierOutputStream));
            inputSupplier.run();
            byte[] inputBytes = supplierOutputStream.toByteArray();


            long begin, end;


            //test on standard

            System.setIn(new ByteArrayInputStream(inputBytes));
            System.setOut(new PrintStream(standardOutput));
            begin = System.currentTimeMillis();
            standardProgram.run();
            end = System.currentTimeMillis();
            msCostOfStandard += end - begin;

            System.setIn(new ByteArrayInputStream(inputBytes));
            System.setOut(new PrintStream(testOutput));
            begin = System.currentTimeMillis();
            beTestedProgram.run();
            end = System.currentTimeMillis();
            msCostOfTest += end - begin;

            checker.check(new FastIO(new ByteArrayInputStream(standardOutput.toByteArray()), System.out),
                    new FastIO(new ByteArrayInputStream(testOutput.toByteArray()), System.out));
        } catch (Throwable t) {
            String msg = String.format("Input:\n%s\n\nAnswer:\n%s\n\nOutput:\n%s\n\n",
                    supplierOutputStream.toString("ascii"),
                    standardOutput.toString("ascii"),
                    testOutput.toString("ascii"));
            errorInfoStream.write(msg);
            errorInfoStream.flush();
            throw new RuntimeException(t);
        } finally {
            System.setIn(stdin);
            System.setOut(stdout);
        }
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    @Override
    public void close() throws Exception {
        errorInfoStream.close();
    }

    public static interface Checker {
        public void check(FastIO std, FastIO test);
    }

    public static class MainRunner implements Runnable {
        Class cls;

        public MainRunner(Class cls) {
            this.cls = cls;
        }

        @Override
        public void run() {
            try {
                Method method = cls.getMethod("main", String[].class);
                method.invoke(null, new Object[]{new String[0]});
            } catch (Throwable e) {
               throw new RuntimeException(e);
            }
        }
    }

    public static class EqualChecker implements Checker {
        @Override
        public void check(FastIO std, FastIO test) {
            while (std.hasMore() && test.hasMore()) {
                Precondition.equal(std.readString(), test.readString());
            }
            Precondition.equal(std.hasMore(), test.hasMore());
        }
    }

    public static abstract class RandomInputRunner implements Runnable {
        private Random random = new Random();

        protected int nextInt(int l, int r) {
            return random.nextInt(r - l + 1) + l;
        }

        protected long nextLong(long l, long r) {
            return (long) (random.nextDouble() * (r - l + 1) + l);
        }
    }

    public static class ExeRunner implements Runnable{
        final String inFile;
        final String outFile;
        final String exeFile;

        public ExeRunner(String inFile, String outFile, String exeFile) {
            this.inFile = inFile;
            this.outFile = outFile;
            this.exeFile = exeFile;
        }

        @Override
        public void run() {
            try(OutputStream os = new FileOutputStream(inFile)) {
                IOUtils.copy(System.in, os);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }

            try {
                Runtime.getRuntime().exec(String.format("%s >%s <%s", exeFile, outFile, inFile)).waitFor();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }

            try(InputStream is = new FileInputStream(outFile)) {
                IOUtils.copy(is, System.out);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
        }
    }
}