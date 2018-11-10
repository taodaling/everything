package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by Administrator on 2018/2/7.
 */
public class POJ1811 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static Random random = new Random();

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ1811.in"));
        }
        input = new BlockReader(System.in);

        for (int i = 0, t = input.nextInteger(); i < t; i++) {
            long n = Long.parseLong(input.nextBlock());
            solve(n);
        }

    }

    public static void solve(long n) {
        //If n is prime
        long minFactor = findMinPrimeFactor(n);
        if (minFactor == n) {
            System.out.println("Prime");
            return;
        }

        //Find the smallest prime factor
        System.out.println(minFactor);
    }

    public static long findMinPrimeFactor(long n) {
        if (millerRabin(n)) {
            return n;
        }

        long minPrime;
        while ((minPrime = findMinPrimeFactor2(n)) == -1) ;

        return minPrime;
    }

    public static long findMinPrimeFactor2(long n) {
        long seed = random.nextInt();
        long x1 = 2;
        long x2 = gen(2, seed, n);
        while (x1 != x2) {
            long diff = Math.abs(x2 - x1);
            long gcd = gcd(n, diff);
            if (gcd != 1) {
                return Math.min(findMinPrimeFactor(gcd), findMinPrimeFactor(n / gcd));
            }
            x1 = gen(x1, seed, n);
            x2 = gen(gen(x2, seed, n), seed, n);
        }

        return -1;
    }


    public static long gen(long x, long seed, long mod) {
        return ((mul(x, x, mod) + seed) % mod + mod) % mod;
    }

    public static long gcd(long a, long b) {
        return a >= b ? gcd0(a, b) : gcd0(b, a);
    }

    public static long gcd0(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public static boolean millerRabin(long n) {
        if (n == 2 || n == 5 || n == 7 || n == 3) {
            return true;
        }
        //Test for 2 5 7 11
        return millerRabin(n, 2) && millerRabin(n, 5) && millerRabin(n, 7) && millerRabin(n, 3);
    }

    //Judge whether m ^ (n - 1) = 1
    public static boolean millerRabin(long n, long m) {
        if (n == 1) {
            return false;
        }
        if (pow(m, n, n) != m) {
            return false;
        }
        long e = n - 1;
        while (e > 0 && (e & 1) == 0) {
            e >>= 1;
            long p = pow(m, e, n);
            if (p != 1 && p != n - 1) {
                return false;
            }
            if (p != 1) {
                break;
            }
        }

        return true;
    }

    public static long mul(long x, long y, long mod) {
        if (y <= 1) {
            return y == 0 ? 0 : x;
        }
        return ((y & 1) == 0 ? (mul(x, y >> 1, mod) << 1) : ((mul(x, y >> 1, mod) << 1) + x)) % mod;
    }

    public static long pow(long x, long n, long mod) {
        int i = 63;
        for (; i >= 0 && (n & (1L << i)) == 0; i--) ;
        long r = 1;
        for (; i >= 0; i--) {
            r = mul(r, r, mod);
            if ((n & (1L << i)) != 0) {
                r = r * x % mod;
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