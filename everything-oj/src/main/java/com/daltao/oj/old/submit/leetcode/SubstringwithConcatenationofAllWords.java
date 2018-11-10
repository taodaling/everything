package com.daltao.oj.old.submit.leetcode;

import java.util.*;

/**
 * Created by dalt on 2017/6/22.
 */
public class SubstringwithConcatenationofAllWords {
    private static final class Substring {
        private char[] data;
        private int from;
        private int length;

        public Substring(char[] data, int from, int length) {
            this.data = data;
            this.from = from;
            this.length = length;
        }

        public Substring substring(int from, int length) {
            return new Substring(data, this.from + from, length);
        }

        Integer cachedHashCode;

        @Override
        public int hashCode() {
            if (cachedHashCode == cachedHashCode) {
                int value = 0;
                for (int i = from, bound = from + length; i < bound; i++) {
                    value = (value << 5) - value + data[i];
                }
                cachedHashCode = Integer.valueOf(value);
            }
            return cachedHashCode.intValue();
        }

        public char charAt(int i) {
            return data[i + from];
        }

        public int size() {
            return length;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj.getClass() != Substring.class)
                return false;
            Substring other = (Substring) obj;
            if (hashCode() != other.hashCode() || length != other.length)
                return false;
            for (int i = 0; i < length; i++) {
                if (charAt(i) != other.charAt(i))
                    return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return String.valueOf(data, from, length);
        }
    }

    private static final class IntHolder {
        private int value;
        private int storedValue;

        public IntHolder(int initValue) {
            value = initValue;
        }

        public void inc() {
            value++;
        }

        public void dec() {
            value--;
        }

        public void store() {
            storedValue = value;
        }

        public void restore() {
            value = storedValue;
        }

        public int getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public String toString() {
            return value + "(" + storedValue + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (obj.getClass() == IntHolder.class) {
                return ((IntHolder) obj).value == value;
            }
            return false;
        }
    }

    public List<Integer> findSubstring(String s, String[] words) {
        if (words.length == 0) {
            List<Integer> result = new ArrayList<>(s.length());
            for (int i = 0, bound = s.length(); i < bound; i++) {
                result.add(Integer.valueOf(i));
            }
            return result;
        }
        int m = s.length();
        int n = words[0].length();
        int k = words.length;

        Map<Substring, IntHolder> map = new HashMap<>(k);
        for (String word : words) {
            Substring pack = new Substring(word.toCharArray(), 0, word.length());
            IntHolder holder = map.get(pack);
            if (holder == null) {
                holder = new IntHolder(0);
                map.put(pack, holder);
            }
            holder.inc();
        }

        List<IntHolder> holders = new ArrayList<IntHolder>(map.values());
        for (IntHolder holder : holders) {
            holder.store();
        }
        List<Integer> result = new LinkedList<>();
        char[] sarray = s.toCharArray();
        for (int i = 0; i < n; i++) {
            for (IntHolder holder : holders) {
                holder.restore();
            }
            int remain = words.length;
            for (int j = i; j < m; j = j + n) {
                int start = j - n * k;
                int end = j;
                if (start >= 0) {
                    Substring sub = new Substring(sarray, start, n);
                    IntHolder times = map.get(sub);
                    if (times != null) {
                        times.inc();
                        if (times.getValue() > 0) {
                            remain++;
                        }
                    }
                }
                if (end + n <= m) {
                    Substring sub = new Substring(sarray, end, n);
                    IntHolder times = map.get(sub);
                    if (times != null) {
                        times.dec();
                        if (times.getValue() >= 0) {
                            remain--;
                        }
                    }
                }
                if (remain == 0) {
                    result.add(start + n);
                }
            }
        }
        return result;
    }
}
