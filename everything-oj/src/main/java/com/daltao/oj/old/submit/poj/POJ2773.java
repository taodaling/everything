package com.daltao.oj.old.submit.poj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/2/9.
 */
public class POJ2773 {
    static final boolean IS_OJ = System.getProperty("ONLINE_JUDGE") != null;
    public static BlockReader input;
    public static int N_LIMIT = 1000000;
    public static boolean[] isComposite = new boolean[N_LIMIT + 1];
    public static int[] primes = new int[N_LIMIT + 1];

    public static void init() {
        isComposite[1] = true;
        int primesLen = 0;
        for (int i = 2; i <= N_LIMIT; i++) {
            if (isComposite[i] == false) {
                primes[primesLen++] = i;
            }
            for (int j = 0, bound = N_LIMIT / i; j < primesLen && primes[j] <= bound; j++) {
                isComposite[primes[j] * i] = true;
                if (i % primes[j] == 0) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (!IS_OJ) {
            System.setIn(new FileInputStream("D:\\DataBase\\TESTCASE\\poj\\POJ2773.in"));
        }
        input = new BlockReader(System.in);

        init();

        while (input.hasMore()) {
            solve();
        }
    }

    public static void solve() {
        int m = input.nextInteger();
        int k = input.nextInteger();

        int primesLen = 0;
        for (int i = 1; i * i <= m; i++) {
            if (m % i == 0) {
                if (isComposite[i] == false) {
                    primes[primesLen++] = i;
                }
                int mi = m / i;
                if (isComposite[mi] == false) {
                    primes[primesLen++] = mi;
                }
            }
        }

        if (primesLen >= 2 && primes[primesLen - 2] == primes[primesLen - 1]) {
            primesLen--;
        }

        long up = (long) 1e12;
        long bottom = 1;
        //result t should satisfied count(t) >= k and count(t - 1) < k
        while (up != bottom) {
            long mid = (up + bottom) >> 1;
            long cnt = count(mid, primes, primesLen);
            if (cnt >= k) {
                up = mid;
            } else {
                bottom = mid + 1;
            }
        }

        System.out.println(up);
    }

    //Calculate how many x <= n that satisfied gcd(x, p)=1(p = primes[0] * primes[1] * ...)
    public static long count(long n, int[] primes, int primesCnt) {
        return n - search(primes, primesCnt, 0, n, 1, 0);
    }

    public static long search(int[] primes, int primesCnt, int i, long n, long product, int choose) {
        if (i == primesCnt) {
            if (product == 1) {
                return 0;
            }
            long cnt = n / product;
            if ((choose & 1) == 0) {
                cnt = -cnt;
            }
            return cnt;
        }
        long res = search(primes, primesCnt, i + 1, n, product, choose) + search(primes, primesCnt, i + 1, n, product * primes[i], choose + 1);
        return res;
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
