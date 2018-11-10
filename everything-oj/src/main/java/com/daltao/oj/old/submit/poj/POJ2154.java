package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/5.
 */
public class POJ2154 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int MAX_PRIME_NUMBER = 32000;
    public static int[] primes = new int[MAX_PRIME_NUMBER];
    public static int primes_len;

    static {
        boolean[] isComposite = new boolean[MAX_PRIME_NUMBER];
        isComposite[1] = false;
        for (int i = 2; i < MAX_PRIME_NUMBER; i++) {
            if (isComposite[i]) {
                continue;
            }
            primes[primes_len++] = i;
            for (int j = i + i; j < MAX_PRIME_NUMBER; j += i) {
                isComposite[j] = true;
            }
        }
    }

    public static int euler(int n) {
        int res = n;
        for (int i = 0; primes[i] * primes[i] <= n; i++) {
            int prime = primes[i];
            if (n % prime == 0) {
                res = res / prime * (prime - 1);
                do {
                    n /= prime;
                } while (n % prime == 0);
            }
        }

        //because the n can't divide any prime less than 32000, so the n is a prime
        if (n > 1) {
            res = res / n * (n - 1);
        }
        return res;
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2154.in"));
        }
        input = new BlockReader(System.in);

        StringBuilder result = new StringBuilder();
        for (int i = 0, t = input.nextInteger(); i < t; i++) {
            int n = input.nextInteger();
            int p = input.nextInteger();
            result.append(solve(n, p)).append('\n');
        }

        System.out.print(result);
    }

    public static int solve(int n, int p) {
        if (n == 0) {
            return 0;
        }

        //enumerate 1 to sqrt(n)
        //gcd(n, L)=i->gcd(n/i,L/i)=1, the possible is euler(n/i)
        //result = sigma(i)(euler(n/i) * 1/1*(n^i))
        int sum = 0;
        int bound = (int) Math.sqrt(n);
        for (int i = 1; i <= bound; i++) {
            if (n % i == 0) {
                sum = sum + euler(n / i) % p * pow(n, i - 1, p);
                sum = sum + euler(i) % p * pow(n, n / i - 1, p);
                sum %= p;
            }
        }
        if (bound * bound == n) {
            sum = sum - euler(bound) % p * pow(n, bound - 1, p);
            sum = ((sum % p) + p) % p;
        }
        return sum;
    }

    public static int pow(int a, int b, int mod) {
        if (b == 0) {
            return 1;
        }
        a %= mod;
        int i = 30;
        while ((b & (1 << i)) == 0) {
            i--;
        }
        int s = 1;
        for (; i >= 0; i--) {
            s = s * s % mod;
            if ((b & (1 << i)) != 0) {
                s = s * a % mod;
            }
        }
        return s;
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