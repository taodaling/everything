package com.daltao.template;

import java.math.BigInteger;
import java.util.Random;

public class MathUtils {
    private static Random random = new Random(123456789);

    /**
     * 扩展欧几里得
     */
    public static class ExtGCD extends Gcd {
        private long x;
        private long y;

        public long getX() {
            return x;
        }

        public long getY() {
            return y;
        }

        public long extgcd(long a, long b) {
            if (a >= b) {
                return extgcd0(a, b);
            } else {
                long g = extgcd0(b, a);
                long tmp = x;
                x = y;
                y = tmp;
                return g;
            }
        }

        private long extgcd0(long a, long b) {
            if (b == 0) {
                x = 1;
                y = 0;
                return a;
            }
            long g = extgcd0(b, a % b);
            long n = x;
            long m = y;
            x = m;
            y = n - m * (a / b);
            return g;
        }
    }

    public static class Gcd {
        public long gcd(long a, long b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private long gcd0(long a, long b) {
            return b == 0 ? a : gcd0(b, a % b);
        }

        public int gcd(int a, int b) {
            return a >= b ? gcd0(a, b) : gcd0(b, a);
        }

        private int gcd0(int a, int b) {
            return b == 0 ? a : gcd0(b, a % b);
        }
    }

    /**
     * 欧拉筛
     */
    public static class EulerSieve {
        int[] primes;
        boolean[] isComp;
        int primeLength;

        public EulerSieve(int limit) {
            isComp = new boolean[limit + 1];
            primes = new int[limit + 1];
            primeLength = 0;
            for (int i = 2; i <= limit; i++) {
                if (!isComp[i]) {
                    primes[primeLength++] = i;
                }
                for (int j = 0, until = limit / i; j < primeLength && primes[j] <= until; j++) {
                    int pi = primes[j] * i;
                    isComp[pi] = true;
                    if (i % primes[j] == 0) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 模运算
     */
    public static class Modular {
        final int m;

        public Modular(int m) {
            this.m = m;
        }

        public int valueOf(int x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return x;
        }

        public int valueOf(long x) {
            x %= m;
            if (x < 0) {
                x += m;
            }
            return (int) x;
        }

        public int mul(int x, int y) {
            return valueOf((long) x * y);
        }

        public int plus(int x, int y) {
            return valueOf(x + y);
        }

        @Override
        public String toString() {
            return "mod " + m;
        }
    }

    /**
     * 位运算
     */
    public static class BitOperator {
        public int bitAt(int x, int i) {
            return (x >> i) & 1;
        }

        public int bitAt(long x, int i) {
            return (int) ((x >> i) & 1);
        }

        public int setBit(int x, int i, boolean v) {
            if (v) {
                x |= 1 << i;
            } else {
                x &= ~(1 << i);
            }
            return x;
        }

        public long setBit(long x, int i, boolean v) {
            if (v) {
                x |= 1L << i;
            } else {
                x &= ~(1L << i);
            }
            return x;
        }
    }

    /**
     * 幂运算
     */
    public static class Power {
        final Modular modular;

        public Power(Modular modular) {
            this.modular = modular;
        }

        public int pow(int x, int n) {
            if (n == 0) {
                return 1;
            }
            long r = pow(x, n >> 1);
            r = modular.valueOf(r * r);
            if ((n & 1) == 1) {
                r = modular.valueOf(r * x);
            }
            return (int) r;
        }

        public int inverse(int x) {
            return pow(x, modular.m - 2);
        }

        public int pow2(int x) {
            return x * x;
        }

        public long pow2(long x) {
            return x * x;
        }

        public double pow2(double x) {
            return x * x;
        }
    }

    /**
     * 对数
     */
    public static class Log2 {
        public int ceilLog(int x) {
            return 32 - Integer.numberOfLeadingZeros(x - 1);
        }

        public int floorLog(int x) {
            return 31 - Integer.numberOfLeadingZeros(x);
        }

        public int ceilLog(long x) {
            return 64 - Long.numberOfLeadingZeros(x - 1);
        }

        public int floorLog(long x) {
            return 63 - Long.numberOfLeadingZeros(x);
        }
    }

    /**
     * 乘法逆元
     */
    public static class InverseNumber {
        int[] inv;

        public InverseNumber(int[] inv, int limit, Modular modular) {
            this.inv = inv;
            inv[1] = 1;
            int p = modular.m;
            for (int i = 2; i <= limit; i++) {
                int k = p / i;
                int r = p % i;
                inv[i] = modular.mul(-k, inv[r]);
            }
        }

        public InverseNumber(int limit, Modular modular) {
            this(new int[limit + 1], limit, modular);
        }
    }

    /**
     * 排列
     */
    public static class Factorial {
        int[] fact;
        int[] inv;

        public Factorial(int[] fact, int[] inv, InverseNumber in, int limit, Modular modular) {
            this.fact = fact;
            this.inv = inv;
            fact[0] = inv[0] = 1;
            for (int i = 1; i <= limit; i++) {
                fact[i] = modular.mul(fact[i - 1], i);
                inv[i] = modular.mul(inv[i - 1], in.inv[i]);
            }
        }

        public Factorial(int limit, Modular modular) {
            this(new int[limit + 1], new int[limit + 1], new InverseNumber(limit, modular), limit, modular);
        }
    }

    /**
     * 组合
     */
    public static class Composite {
        final Factorial factorial;
        final Modular modular;

        public Composite(Factorial factorial, Modular modular) {
            this.factorial = factorial;
            this.modular = modular;
        }

        public Composite(int limit, Modular modular) {
            this(new Factorial(limit, modular), modular);
        }

        public int composite(int m, int n) {
            if (n > m) {
                return 0;
            }
            return modular.mul(modular.mul(factorial.fact[m], factorial.inv[n]), factorial.inv[m - n]);
        }
    }

    /**
     * 大素数测试
     */
    public static class MillerRabin {
        Modular modular;
        Power power;

        /**
         * 判断n是否是素数
         */
        public boolean mr(int n, int s) {
            if (n == 2) {
                return true;
            }
            if (n % 2 == 0) {
                return false;
            }
            modular = new Modular(n);
            power = new Power(modular);
            for (int i = 0; i < s; i++) {
                int x = random.nextInt(n - 2) + 2;
                if (!mr0(x, n)) {
                    return false;
                }
            }
            return true;
        }

        private boolean mr0(int x, int n) {
            int exp = n - 1;
            while (true) {
                int y = power.pow(x, exp);
                if (y != 1 && y != n - 1) {
                    return false;
                }
                if (y != 1 || exp % 2 == 1) {
                    break;
                }
                exp = exp / 2;
            }
            return true;
        }
    }


    public static class LongModular {
        final long m;

        public LongModular(long m) {
            this.m = m;
        }

        public long mul(long a, long b) {
            return b == 0 ? 0 : ((mul(a, b >> 1) << 1) % m + a * (b & 1)) % m;
        }

        public long plus(long a, long b) {
            return valueOf(a + b);
        }

        public long valueOf(long a) {
            a %= m;
            if (a < 0) {
                a += m;
            }
            return a;
        }
    }

    public static class LongPower {
        final LongModular modular;

        public LongPower(LongModular modular) {
            this.modular = modular;
        }

        long pow(long x, long n) {
            if (n == 0) {
                return 1;
            }
            long r = pow(x, n >> 1);
            r = modular.mul(r, r);
            if ((n & 1) == 1) {
                r = modular.mul(r, x);
            }
            return r;
        }

        long inverse(long x) {
            return pow(x, modular.m - 2);
        }
    }

    /**
     * 大素数测试
     */
    public static class LongMillerRabin {
        LongModular modular;
        LongPower power;

        /**
         * 判断n是否是素数
         */
        public boolean mr(long n, int s) {
            if (n == 2) {
                return true;
            }
            if (n % 2 == 0) {
                return false;
            }
            modular = new LongModular(n);
            power = new LongPower(modular);
            for (int i = 0; i < s; i++) {
                long x = (long) (random.nextDouble() * (n - 2) + 2);
                if (!mr0(x, n)) {
                    return false;
                }
            }
            return true;
        }

        private boolean mr0(long x, long n) {
            long exp = n - 1;
            while (true) {
                long y = power.pow(x, exp);
                if (y != 1 && y != n - 1) {
                    return false;
                }
                if (y != 1 || exp % 2 == 1) {
                    break;
                }
                exp = exp / 2;
            }
            return true;
        }
    }

    public static class LongPollardRho {
        LongMillerRabin mr = new LongMillerRabin();
        Gcd gcd = new Gcd();
        LongModular modular;


        public long findFactor(long n) {
            if (mr.mr(n, 3)) {
                return n;
            }
            modular = new LongModular(n);
            while (true) {
                long f = findFactor0((long) (random.nextDouble() * n), (long) (random.nextDouble() * n), n);
                if (f != -1) {
                    return f;
                }
            }
        }

        private long findFactor0(long x, long c, long n) {
            long xi = x;
            long xj = x;
            int j = 2;
            int i = 1;
            while (i < n) {
                i++;
                xi = modular.plus(modular.mul(xi, xi), c);
                long g = gcd.gcd(n, Math.abs(xi - xj));
                if (g != 1 && g != n) {
                    return g;
                }
                if (i == j) {
                    j = j << 1;
                    xj = xi;
                }
            }
            return -1;
        }
    }

    /**
     * 扩展中国余数定理
     */
    public static class ExtCRT {
        /**
         * remainder
         */
        long r;
        /**
         * modulus
         */
        long m;
        ExtGCD gcd = new ExtGCD();

        public ExtCRT() {
            r = 0;
            m = 1;
        }

        public boolean add(long r, long m) {
            long m1 = this.m;
            long x1 = this.r;
            long m2 = m;
            long x2 = ((r % m) + m) % m;
            long g = gcd.extgcd(m1, m2);
            long a = gcd.getX();
            if ((x2 - x1) % g != 0) {
                return false;
            }
            this.m = m1 / g * m2;
            this.r = BigInteger.valueOf(a).multiply(BigInteger.valueOf((x2 - x1) / g))
                    .multiply(BigInteger.valueOf(m1)).add(BigInteger.valueOf(x1))
                    .mod(BigInteger.valueOf(this.m)).longValue();
            return true;
        }
    }


    /**
     * 卢卡斯定理
     */
    public static class Lucas {
        private final Composite composite;
        private int modulus;

        public Lucas(Composite composite) {
            this.composite = composite;
            this.modulus = composite.modular.m;
        }

        public int composite(long m, long n) {
            if (n == 0) {
                return 1;
            }
            return composite.modular.mul(composite.composite((int) (m % modulus), (int) (n % modulus)),
                    composite(m / modulus, n / modulus));
        }
    }

    /**
     * 因式分解
     */
    public static class PollardRho {
        MillerRabin mr = new MillerRabin();
        Gcd gcd = new Gcd();
        Random random = new Random(123456789);

        public int findFactor(int n) {
            if (mr.mr(n, 10)) {
                return n;
            }
            while (true) {
                int f = findFactor0(random.nextInt(n), random.nextInt(n), n);
                if (f != -1) {
                    return f;
                }
            }
        }

        private int findFactor0(int x, int c, int n) {
            int xi = x;
            int xj = x;
            int j = 2;
            int i = 1;
            while (i < n) {
                i++;
                xi = (int) ((long) xi * xi + c) % n;
                int g = gcd.gcd(n, Math.abs(xi - xj));
                if (g != 1 && g != n) {
                    return g;
                }
                if (i == j) {
                    j = j << 1;
                    xj = xi;
                }
            }
            return -1;
        }
    }
}