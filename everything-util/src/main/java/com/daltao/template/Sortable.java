package com.daltao.template;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by dalt on 2018/5/20.
 */
public class Sortable {
    private static final int THRESHOLD = 4;

    public static <T> void randomizedQuickSort(T[] data, Comparator<T> cmp, int f, int t) {
        Random random = new Random();
        int len = t - f;
        for (int i = len - 1; i >= 0; i--) {
            int rand = random.nextInt(i + 1);
            Memory.swap(data, f + rand, f + i);
        }
        quickSort(data, cmp, f, t);
    }

    public static <T> void quickSort(T[] data, Comparator<T> cmp, int f, int t) {
        if (t - f < 2) {
            //insertSort(data, cmp, f, t);
            return;
        }
        T rule = data[f];
        int l = f;
        int r = t;
        for (int i = f + 1; i < r; ) {
            int cmpRes = cmp.compare(data[i], rule);
            if (cmpRes < 0) {
                Memory.swap(data, l++, i);
            } else if (cmpRes > 0) {
                Memory.swap(data, --r, i);
            } else {
                i++;
            }
        }
        quickSort(data, cmp, f, l);
        quickSort(data, cmp, r, t);
    }

    public static <T> void mergeSort(T[] data, Comparator<T> cmp, int f, int t, T[] buf) {
        if (t - f < THRESHOLD) {
            insertSort(data, cmp, f, t);
            return;
        }

        int m = (f + t) >> 1;
        mergeSort(data, cmp, f, m, buf);
        mergeSort(data, cmp, m, t, buf);

        merge(data, buf, f, m, t, cmp);
    }

    public static <T> void selectSort(T[] data, Comparator<T> cmp, int f, int t) {
        for (int i = f; i < t; i++) {
            int minIndex = i;
            for (int j = i + 1; j < t; j++) {
                if (cmp.compare(data[minIndex], data[j]) > 0) {
                    minIndex = j;
                }
            }
            Memory.swap(data, minIndex, i);
        }
    }

    public static <T> void insertSort(T[] data, Comparator<T> cmp, int f, int t) {
        for (int i = f + 1; i < t; i++) {
            T v = data[i];
            int j = i - 1;
            while (j >= f && cmp.compare(v, data[j]) < 0) {
                data[j + 1] = data[j];
                j--;
            }
            data[j + 1] = v;
        }
    }

    public static void radixSort(int[] data, int from, int to, int bit, int[] buf, int bufFrom, int[] cnt) {
        if (cnt == null || cnt.length < (1 << bit)) {
            cnt = new int[1 << bit];
        }

        int sortTime = (31 + bit - 1) / bit;
        for (int i = 0; i < sortTime; i++) {
            radixSort0(data, from, to, cnt, bit * i, bit * (i + 1), buf, bufFrom);
            System.arraycopy(buf, bufFrom, data, from, to - from);
        }
    }

    private static void radixSort0(int[] data, int from, int to, int[] cnt, int bitFrom, int bitTo, int[] output, int outputFrom) {
        Arrays.fill(cnt, 0);

        int mask = (1 << (bitTo - bitFrom)) - 1;
        for (int i = from; i < to; i++) {
            cnt[getInt(data[i], bitFrom, mask)]++;
        }

        for (int i = 1, until = cnt.length; i < until; i++) {
            cnt[i] += cnt[i - 1];
        }

        for (int i = to - 1; i >= from; i--) {
            int bitval = getInt(data[i], bitFrom, mask);
            output[outputFrom + (--cnt[bitval])] = data[i];
        }
    }

    private static int getInt(int val, int shiftRight, int mask) {
        return (val >> shiftRight) & mask;
    }

    public static <T> void happySort(T[] data, Comparator<T> cmp, int f, int t) {
        int l;
        int m;
        int r;

        Object[] buf = new Object[t - f];

        while (true) {
            l = r = f;
            while (r < t) {
                l = r;
                m = findTendency(data, cmp, l, t);
                r = findTendency(data, cmp, m, t);

                if (m == t) {
                    break;
                }

                merge(data, buf, l, m, r, cmp);
            }

            if (l == f) {
                break;
            }
        }
    }

    private static <T> int findTendency(T[] data, Comparator<T> cmp, int f, int t) {
        int r = f + 1;
        int cmpRes = 0;
        while (r < t && (cmpRes = cmp.compare(data[r - 1], data[r])) == 0) {
            r++;
        }

        if (r >= t) {
            return r;
        }

        r++;
        if (cmpRes < 0) {
            while (r < t && cmp.compare(data[r - 1], data[r]) <= 0) {
                r++;
            }
        } else {
            while (r < t && cmp.compare(data[r - 1], data[r]) >= 0) {
                r++;
            }
            Memory.reverse(data, f, r);
        }

        return r;
    }
    

    public static void merge(Object[] data, Object[] buf, int l, int m, int r, Comparator cmp) {
        int lIndex = l;
        int rIndex = m;
        int bIndex = 0;
        while (lIndex < m && rIndex < r) {
            if (cmp.compare(data[lIndex], data[rIndex]) <= 0) {
                buf[bIndex++] = data[lIndex++];
            } else {
                buf[bIndex++] = data[rIndex++];
            }
        }
        if (lIndex < m) {
            Memory.copy(data, data, lIndex, l + bIndex, m - lIndex);
        }
        Memory.copy(buf, data, 0, l, bIndex);
    }


}
