package com.daltao.oj.old.submit.bzoj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by Administrator on 2018/2/7.
 */
public class BZOJ4802 {
    public static BlockReader input;
    public static Random random = new Random();

    public static void main(String[] args) throws FileNotFoundException {
        input = new BlockReader(System.in);

        solve(Long.parseLong(input.nextBlock()));
    }

    public static void solve(long n) {
        System.out.println(phi(n));

    }

    public static long phi(long n) {
        if (n == 1) {
            return 1;
        }
        if (millarRabin(n)) {
            return n - 1;
        }

        while (true) {
            long a = Math.abs(random.nextInt());
            long x1 = 2;
            long x2 = gen(2, a, n);
            while (x1 != x2) {
                long diff = Math.abs(x1 - x2);
                long gcd = gcd0(n, diff);
                if (gcd > 1) {
                    long fac1 = gcd;
                    long fac2 = n / gcd;
                    long com = gcd(fac1, fac2);
                    if (com == fac1) {
                        return phi(fac2) * fac1;
                    }
                    if (com == fac2) {
                        return phi(fac1) * fac2;
                    }
                    fac1 *= com;
                    fac2 /= com;
                    //now gcd(fac1, fac2) == 1
                    return phi(fac1) * phi(fac2);
                }
                x1 = gen(x1, a, n);
                x2 = gen(gen(x2, a, n), a, n);
            }
        }

    }

    public static long gen(long x, long a, long p) {
        return ((mul(x, x, p) + a) % p);
    }

    public static long gcd(long a, long b) {
        return a >= b ? gcd0(a, b) : gcd0(b, a);
    }

    public static long gcd0(long a, long b) {
        return b == 0 ? a : gcd0(b, a % b);
    }

    public static boolean millarRabin(long n) {
        if (n == 2 || n == 3 || n == 5 || n == 7) {
            return true;
        }
        return millarRabin(n, 2) && millarRabin(n, 3) && millarRabin(n, 5) && millarRabin(n, 7);
    }

    //Test whether n is a prime by m
    public static boolean millarRabin(long n, long m) {
        long e = n - 1;
        m %= n;
        if (pow(m, e, n) != 1) {
            return false;
        }
        while ((e & -e) > 1) {
            e >>= 1;
            long p = pow(m, e, n);
            if (p == 1) {
                continue;
            } else if (p == n -
                    1) {
                break;
            } else {
                return false;
            }
        }
        return true;
    }

    public static long mul(long x, long y, long p) {
        if (y == 0) {
            return 0;
        }
        long s = mul(x, y >> 1, p) << 1;
        if ((y & 1) != 0) {
            s += x;
        }
        return s % p;
    }

    public static long pow(long x, long n, long p) {
        int i = 60;
        for (; (n & (1L << i)) == 0; i--) ;
        long r = 1;
        for (; i >= 0; i--) {
            r = mul(r, r, p);
            if ((n & (1L << i)) != 0) {
                r = mul(r, x, p);
            }
        }

        return r;
    }

    public static class BlockReader {
        static final int EOF = -1;
        InputStream is;
        byte[] dBuf;
        int dPos, dSize, next;
        StringBuilder builder = new StringBuilder();

        public BlockReader(InputStream is) {
            this(is, 8192);
        }

        public BlockReader(InputStream is, int bufSize) {
            this.is = is;
            dBuf = new byte[bufSize];
            next = nextByte();
        }

        public void skipBlank() {
            while (Character.isWhitespace(next)) {
                next = nextByte();
            }
        }

        public String nextBlock() {
            builder.setLength(0);
            skipBlank();
            while (next != EOF && !Character.isWhitespace(next)) {
                builder.append((char) next);
                next = nextByte();
            }
            return builder.toString();
        }

        public int nextInteger() {
            skipBlank();
            int ret = 0;
            boolean rev = false;
            if (next == '+' || next == '-') {
                rev = next == '-';
                next = nextByte();
            }
            while (next >= '0' && next <= '9') {
                ret = (ret << 3) + (ret << 1) + next - '0';
                next = nextByte();
            }
            return rev ? -ret : ret;
        }

        public int nextBlock(char[] data, int offset) {
            skipBlank();
            int index = offset;
            int bound = data.length;
            while (next != EOF && index < bound && !Character.isWhitespace(next)) {
                data[index++] = (char) next;
                next = nextByte();
            }
            return index - offset;
        }

        public boolean hasMore() {
            skipBlank();
            return next != EOF;
        }

        public int nextByte() {
            while (dPos >= dSize) {
                if (dSize == -1) {
                    return EOF;
                }
                dPos = 0;
                try {
                    dSize = is.read(dBuf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return dBuf[dPos++];
        }
    }
}
