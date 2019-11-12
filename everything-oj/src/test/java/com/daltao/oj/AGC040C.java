package com.daltao.oj;

import com.daltao.oj.tool.MainMethod2Runnable;
import com.daltao.oj.tool.Runnable2OJSolution;
import com.daltao.template.FastInput;
import com.daltao.test.Checker;
import com.daltao.test.Input;
import com.daltao.test.Input2InputStream;
import com.daltao.test.QueueInput;
import com.daltao.test.RandomFactory;
import com.daltao.test.TestCaseExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.util.InputMismatchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Closeable;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;

public class AGC040C {

    @Test
    public void test() {
        Assert.assertTrue(new TestCaseExecutor.Builder()
                .setInputFactory(new Generator())
                .setCheckerFactory(() -> new C())
                .setExpectedSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Main.class)))
                .setActualSolution(() -> new Runnable2OJSolution(new MainMethod2Runnable(Task.class)))
                .setTestTime(10000)
                .build().call());
    }

    public static class C implements Checker {
        @Override
        public boolean check(Input expected, Input actual, Input input) {
            FastInput ein = new FastInput(new Input2InputStream(expected));
            FastInput ain = new FastInput(new Input2InputStream(actual));
            FastInput in = new FastInput(new Input2InputStream(input));

            boolean hasSolution = ain.readString().equals("YES");
            if (hasSolution != ein.readString().equals("YES")) {
                return false;
            }

            if (!hasSolution) {
                return true;
            }

            int n = in.readInt();
            int m = in.readInt();
            int a = in.readInt();
            int b = in.readInt();

            char[][] mat = new char[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    mat[i][j] = ain.readChar();
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (mat[i][j] == '<') {
                        if (j == m - 1 || mat[i][j + 1] != '>') {
                            return false;
                        }
                        a--;
                    } else if (mat[i][j] == '>') {
                        if (j == 0 || mat[i][j - 1] != '<') {
                            return false;
                        }
                    } else if (mat[i][j] == '^') {
                        if (i == n - 1 || mat[i + 1][j] != 'v') {
                            return false;
                        }
                        b--;
                    } else if (mat[i][j] == 'v') {
                        if (i == 0 || mat[i - 1][j] != '^') {
                            return false;
                        }
                    } else if (mat[i][j] != '.') {
                        return false;
                    }
                }
            }

            return a == 0 && b == 0;
        }
    }




    /**
     * Built using CHelper plug-in
     * Actual solution is at the top
     *
     * @author Egor Kulikov (egor@egork.net)
     */
    public static class Main {
        public static void main(String[] args) {
            InputStream inputStream = System.in;
            OutputStream outputStream = System.out;
            InputReader in = new InputReader(inputStream);
            OutputWriter out = new OutputWriter(outputStream);
            TaskC solver = new TaskC();
            solver.solve(1, in, out);
            out.close();
        }

        static class TaskC {
            public void solve(int testNumber, InputReader in, OutputWriter out) {
                int n = in.readInt();
                int m = in.readInt();
                int a = in.readInt();
                int b = in.readInt();
                if (2 * (a + b) > n * m) {
                    out.printLine("NO");
                    return;
                }
                char[][] answer = new char[n][m];
                ArrayUtils.fill(answer, '.');
                if (n % 2 != 0) {
                    for (int i = 0; i + 1 < m; i += 2) {
                        if (a != 0) {
                            answer[n - 1][i] = '<';
                            answer[n - 1][i + 1] = '>';
                            a--;
                        }
                    }
                }
                if (m % 2 != 0) {
                    for (int i = n % 2; i < n; i += 2) {
                        if (b != 0) {
                            answer[i][m - 1] = '^';
                            answer[i + 1][m - 1] = 'v';
                            b--;
                        }
                    }
                }
                for (int i = n - n % 2 - 2; i >= 0; i -= 2) {
                    for (int j = 0; j + 1 < m; j += 2) {
                        if (a >= 2) {
                            answer[i][j] = '<';
                            answer[i][j + 1] = '>';
                            answer[i + 1][j] = '<';
                            answer[i + 1][j + 1] = '>';
                            a -= 2;
                        } else if (b >= 2) {
                            answer[i][j] = '^';
                            answer[i][j + 1] = '^';
                            answer[i + 1][j] = 'v';
                            answer[i + 1][j + 1] = 'v';
                            b -= 2;
                        } else if (a == 1 && b == 1 && i == 0 && j == m - 3 && n % 2 == 1) {
                            answer[i][j] = '^';
                            answer[i + 1][j] = 'v';
                            answer[i][j + 1] = '<';
                            answer[i][j + 2] = '>';
                            a--;
                            b--;
                        } else if (a == 1) {
                            answer[i][j] = '<';
                            answer[i][j + 1] = '>';
                            a--;
                        } else if (b == 1) {
                            answer[i][j] = '^';
                            answer[i + 1][j] = 'v';
                            b--;
                        }
                    }
                }
                if (a != 0 || b != 0) {
                    out.printLine("NO");
                    return;
                }
                out.printLine("YES");
                for (char[] row : answer) {
                    out.printLine(row);
                }
            }

        }

        static class OutputWriter {
            private final PrintWriter writer;

            public OutputWriter(OutputStream outputStream) {
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
            }

            public OutputWriter(Writer writer) {
                this.writer = new PrintWriter(writer);
            }

            public void print(Object... objects) {
                for (int i = 0; i < objects.length; i++) {
                    if (i != 0) {
                        writer.print(' ');
                    }
                    writer.print(objects[i]);
                }
            }

            public void printLine(Object... objects) {
                print(objects);
                writer.println();
            }

            public void printLine(char[] array) {
                writer.println(array);
            }

            public void close() {
                writer.close();
            }

        }

        static class ArrayUtils {
            public static void fill(char[][] array, char value) {
                for (char[] row : array) {
                    Arrays.fill(row, value);
                }
            }

        }

        static class InputReader {
            private InputStream stream;
            private byte[] buf = new byte[1024];
            private int curChar;
            private int numChars;
            private InputReader.SpaceCharFilter filter;

            public InputReader(InputStream stream) {
                this.stream = stream;
            }

            public int read() {
                if (numChars == -1) {
                    throw new InputMismatchException();
                }
                if (curChar >= numChars) {
                    curChar = 0;
                    try {
                        numChars = stream.read(buf);
                    } catch (IOException e) {
                        throw new InputMismatchException();
                    }
                    if (numChars <= 0) {
                        return -1;
                    }
                }
                return buf[curChar++];
            }

            public int readInt() {
                int c = read();
                while (isSpaceChar(c)) {
                    c = read();
                }
                int sgn = 1;
                if (c == '-') {
                    sgn = -1;
                    c = read();
                }
                int res = 0;
                do {
                    if (c < '0' || c > '9') {
                        throw new InputMismatchException();
                    }
                    res *= 10;
                    res += c - '0';
                    c = read();
                } while (!isSpaceChar(c));
                return res * sgn;
            }

            public boolean isSpaceChar(int c) {
                if (filter != null) {
                    return filter.isSpaceChar(c);
                }
                return isWhitespace(c);
            }

            public static boolean isWhitespace(int c) {
                return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
            }

            public interface SpaceCharFilter {
                public boolean isSpaceChar(int ch);

            }

        }
    }






    /**
     * Built using CHelper plug-in Actual solution is at the top
     */
    public static class Task {
        public static void main(String[] args) throws Exception {
            Thread thread = new Thread(null, new TaskAdapter(), "", 1 << 27);
            thread.start();
            thread.join();
        }

        static class TaskAdapter implements Runnable {
            @Override
            public void run() {
                InputStream inputStream = System.in;
                OutputStream outputStream = System.out;
                FastInput in = new FastInput(inputStream);
                FastOutput out = new FastOutput(outputStream);
                TaskC solver = new TaskC();
                solver.solve(1, in, out);
                out.close();
            }
        }
        static class TaskC {
            public void solve(int testNumber, FastInput in, FastOutput out) {
                int n = in.readInt();
                int m = in.readInt();
                int a = in.readInt();
                int b = in.readInt();

                if (n == 3 && m == 3 && a == 2 && b == 2) {
                    out.println("YES\n^<>\nv.^\n<>v");
                    return;
                }

                char[][] mat = new char[n][m];
                if (n % 2 == 1) {
                    for (int i = 0; i < m - 1; i++) {
                        if (a == 0 || mat[0][i] != 0 || mat[0][i + 1] != 0) {
                            continue;
                        }
                        mat[0][i] = '<';
                        mat[0][i + 1] = '>';
                        a--;
                    }
                }
                if (m % 2 == 1) {
                    for (int i = 0; i < n - 1; i++) {
                        if (b == 0 || mat[i][m - 1] != 0 || mat[i + 1][m - 1] != 0) {
                            continue;
                        }
                        mat[i][m - 1] = '^';
                        mat[i + 1][m - 1] = 'v';
                        b--;
                    }
                }
                for (int i = 0; i < n - 1; i++) {
                    for (int j = 0; j < m - 1; j++) {
                        if (mat[i][j] + mat[i + 1][j] + mat[i][j + 1] + mat[i + 1][j + 1] != 0) {
                            continue;
                        }
                        if (a > 0) {
                            mat[i][j] = '<';
                            mat[i][j + 1] = '>';
                            a--;
                            if (a > 0) {
                                mat[i + 1][j] = '<';
                                mat[i + 1][j + 1] = '>';
                                a--;
                            }
                        } else if (b > 0) {
                            mat[i][j] = '^';
                            mat[i + 1][j] = 'v';
                            b--;
                            if (b > 0) {
                                mat[i][j + 1] = '^';
                                mat[i + 1][j + 1] = 'v';
                                b--;
                            }
                        }
                    }
                }

                if (b > 0 || a > 0) {
                    out.println("NO");
                    return;
                }
                yes(out, mat);
            }

            public void yes(FastOutput out, char[][] mat) {
                out.println("YES");
                for (char[] r : mat) {
                    for (char c : r) {
                        if (c == 0) {
                            out.append('.');
                        } else {
                            out.append(c);
                        }
                    }
                    out.println();
                }
            }

        }
        static class FastInput {
            private final InputStream is;
            private byte[] buf = new byte[1 << 13];
            private int bufLen;
            private int bufOffset;
            private int next;

            public FastInput(InputStream is) {
                this.is = is;
            }

            private int read() {
                while (bufLen == bufOffset) {
                    bufOffset = 0;
                    try {
                        bufLen = is.read(buf);
                    } catch (IOException e) {
                        bufLen = -1;
                    }
                    if (bufLen == -1) {
                        return -1;
                    }
                }
                return buf[bufOffset++];
            }

            public void skipBlank() {
                while (next >= 0 && next <= 32) {
                    next = read();
                }
            }

            public int readInt() {
                int sign = 1;

                skipBlank();
                if (next == '+' || next == '-') {
                    sign = next == '+' ? 1 : -1;
                    next = read();
                }

                int val = 0;
                if (sign == 1) {
                    while (next >= '0' && next <= '9') {
                        val = val * 10 + next - '0';
                        next = read();
                    }
                } else {
                    while (next >= '0' && next <= '9') {
                        val = val * 10 - next + '0';
                        next = read();
                    }
                }

                return val;
            }

        }
        static class FastOutput implements AutoCloseable, Closeable {
            private StringBuilder cache = new StringBuilder(10 << 20);
            private final Writer os;

            public FastOutput(Writer os) {
                this.os = os;
            }

            public FastOutput(OutputStream os) {
                this(new OutputStreamWriter(os));
            }

            public FastOutput append(char c) {
                cache.append(c);
                return this;
            }

            public FastOutput println(String c) {
                cache.append(c).append('\n');
                return this;
            }

            public FastOutput println() {
                cache.append('\n');
                return this;
            }

            public FastOutput flush() {
                try {
                    os.append(cache);
                    os.flush();
                    cache.setLength(0);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return this;
            }

            public void close() {
                flush();
                try {
                    os.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public String toString() {
                return cache.toString();
            }

        }
    }



    public static class Generator extends RandomFactory {
        @Override
        public Input newInstance() {
            QueueInput in = new QueueInput();
            int n = nextInt(1, 10);
            int m = nextInt(1, 10);
            int area = n * m;
            int a = nextInt(0, area / 2);
            int b = nextInt(0, area / 2);
            in.add(String.format("%d %d %d %d", n, m, a, b));
            return in.end();
        //return new QueueInput().add("6 4 7 2").end();
        }
    }
}
