package com.daltao.oj.old.submit.nod51;

import java.io.*;

public class NOD1000 {
    public static void main(String[] args) throws FileNotFoundException {
        ASC2IO io = null;
        if (System.getProperty("ONLINE_JUDGE") == null) {
            io = new ASC2IO(new FileInputStream("E:\\DATABASE\\TESTCASE\\nod51\\FONOD1028.in"), System.out);
        } else {
            io = new ASC2IO(System.in, System.out);
        }

        io.writeInt(io.readInt() + io.readInt());
    }

    public static class ASC2IO {
        private static int BUF_SIZE = 1 << 13;

        private byte[] r_buf = new byte[BUF_SIZE];
        private int r_cur;
        private int r_total;
        private int r_next;
        private final InputStream in;

        private byte[] w_buf = new byte[BUF_SIZE];
        private int w_cur;
        private final OutputStream out;

        public ASC2IO(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        private void skipBlank() {
            while (r_next <= 32) {
                r_next = read();
            }
        }

        public int readString(char[] data, int offset, int limit) {
            skipBlank();

            int originalLimit = limit;
            while (limit > 0 && r_next > 32) {
                data[offset++] = (char) r_next;
                limit--;
                r_next = read();
            }

            return originalLimit - limit;
        }

        public String readString(StringBuilder builder) {
            skipBlank();

            while (r_next > 32) {
                builder.append((char) r_next);
            }

            return builder.toString();
        }

        public String readString() {
            return readString(new StringBuilder(16));
        }

        public long readUnsignedLong() {
            skipBlank();

            long num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public long readLong() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign * readUnsignedLong();
        }

        public int readUnsignedInt() {
            skipBlank();

            int num = 0;
            while (r_next >= '0' && r_next <= '9') {
                num = num * 10 + r_next - '0';
                r_next = read();
            }
            return num;
        }

        public int readInt() {
            skipBlank();

            int sign = 1;
            while (r_next == '+' || r_next == '-') {
                if (r_next == '-') {
                    sign *= -1;
                }
                r_next = read();
            }

            return sign * readUnsignedInt();
        }

        public int read() {
            while (r_total <= r_cur) {
                try {
                    r_total = in.read(r_buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                r_cur = 0;
                if (r_total == -1) {
                    return -1;
                }
            }
            return r_buf[r_cur++];
        }

        public void write(char c) {
            if (w_cur == BUF_SIZE) {
                flush();
            }
            w_buf[w_cur++] = (byte) c;
        }

        public void writeInt(int n) {
            if (n >= 10) {
                writeInt(n / 10);
            }
            write((char) (n % 10 + '0'));
        }

        public void writeString(String s) {
            for (int i = 0, until = s.length(); i < until; i++) {
                write(s.charAt(i));
            }
        }

        public void writeCharArray(char[] data, int offset, int cnt) {
            for (int i = offset, until = offset + cnt; i < until; i++) {
                write(data[i]);
            }
        }

        public void flush() {
            try {
                out.write(w_buf, 0, w_cur);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
