package com.daltao.oj.old.submit.codeforces;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;

/**
 * Built using CHelper plug-in
 * Actual solution is at the top
 */
public class ECR44C_A {
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
            int n = in.nextInt(), k = in.nextInt(), l = in.nextInt();
            int a[] = in.parseInt1D(n * k);
            Integer aa[] = ArrayUtils.toBoxedArray(a);
            Arrays.sort(aa);
            long sum = 0;
            int maxPosIndex = 0;
            int min = aa[0];
            for (int i = 0; i < n * k; i++) {
                if (aa[i] - min <= l) {
                    maxPosIndex = i;
                } else {
                    break;
                }
            }

            if (aa[n - 1] - aa[0] > l) {
                out.println(0);
            } else {
                int extraIndex = maxPosIndex + 1;
                int ci = maxPosIndex;
                ArrayList<Integer> values[] = new ArrayList[n];
                int vi = 0;
                while (ci >= 0) {
                    int v = aa[ci--];
                    values[vi] = new ArrayList<>();
                    values[vi].add(v);
                    while (extraIndex < k * n && values[vi].size() < k) {
                        values[vi].add(aa[extraIndex++]);
                    }
                    while (ci >= 0 && values[vi].size() < k) {
                        values[vi].add(aa[ci--]);
                    }
                    sum += Collections.min(values[vi]);
                    vi++;
                }

                out.println(sum);
            }
        }

    }

    static class ArrayUtils {
        public static Integer[] toBoxedArray(int a[]) {
            Integer[] result = new Integer[a.length];
            for (int i = 0; i < a.length; i++) {
                result[i] = a[i];
            }
            return result;
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

        public void close() {
            writer.close();
        }

        public void println(long i) {
            writer.println(i);
        }

        public void println(int i) {
            writer.println(i);
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

        public int nextInt() {
            return readInt();
        }

        public int[] parseInt1D(int n) {
            int r[] = new int[n];
            for (int i = 0; i < n; i++) {
                r[i] = nextInt();
            }
            return r;
        }

        public interface SpaceCharFilter {
            public boolean isSpaceChar(int ch);

        }

    }
}